/*
 * Copyright 2021 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package services

import com.google.inject.Inject
import connector.TrustConnector
import controllers.Assets.Redirect
import controllers.register.routes._
import models.Mode
import models.core.UserAnswers
import models.core.http.MatchedResponse.AlreadyRegistered
import models.core.http.{MatchData, MatchedResponse, SuccessOrFailureResponse}
import models.registration.Matched
import navigation.registration.TaskListNavigator
import pages.register.{ExistingTrustMatched, MatchingNamePage, PostcodeForTheTrustPage, WhatIsTheUTRPage}
import play.api.mvc.{Call, Result}
import repositories.RegistrationsRepository
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal

class MatchingService @Inject()(trustConnector: TrustConnector,
                                registrationsRepository: RegistrationsRepository,
                                featureFlagService: FeatureFlagService,
                                navigator: TaskListNavigator) {

  private case class MatchingContext(is5mld: Boolean, isAgent: Boolean, userAnswers: UserAnswers, draftId: String, mode: Mode)

  def matching(userAnswers: UserAnswers,
               draftId: String,
               isAgent: Boolean,
               mode: Mode
              )(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Result] = {
    val matchResult = for {
      is5mld <- featureFlagService.is5mldEnabled()
      result <- {
        val context = MatchingContext(is5mld, isAgent, userAnswers, draftId, mode)
        val data = payload(userAnswers)
        attemptMatch(context, data)
      }
    } yield result

    matchResult recoverWith {
      case NonFatal(_) =>
        Future.successful(Redirect(MatchingDownController.onPageLoad()))
    }
  }

  private def payload(userAnswers: UserAnswers): Option[MatchData] = for {
    utr: String <- userAnswers.get(WhatIsTheUTRPage)
    name: String <- userAnswers.get(MatchingNamePage)
    postcode: Option[String] = userAnswers.get(PostcodeForTheTrustPage)
  } yield {
    MatchData(utr, name, postcode)
  }

  private def attemptMatch(context: MatchingContext, matchData: Option[MatchData])
                          (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Result] = matchData match {
    case Some(data) =>
      trustConnector.matching(data) flatMap {
        r =>
          (successAsAgent(context) orElse
            successAsOrganisation(context) orElse
            unsuccessful(context) orElse
            alreadyRegistered(context) orElse
            recover()
            ).apply(r)
      }
    case None =>
      Future.successful(Redirect(MatchingDownController.onPageLoad()))
  }

  private def successAsAgent(context: MatchingContext)
                            (implicit hc: HeaderCarrier, ec: ExecutionContext): PartialFunction[MatchedResponse, Future[Result]] = {
    case SuccessOrFailureResponse(true) if context.isAgent =>
      if (context.is5mld) {
        saveTrustMatchedStatusAndRedirect(
          context.userAnswers,
          Matched.Success,
          controllers.register.suitability.routes.ExpressTrustYesNoController.onPageLoad(context.mode, context.draftId)
        )
      } else {
        saveTrustMatchedStatusAndRedirect(context.userAnswers, Matched.Success, Call("GET", navigator.agentDetailsJourneyUrl(context.draftId)))
      }
  }

  private def successAsOrganisation(context: MatchingContext)
                                   (implicit hc: HeaderCarrier, ec: ExecutionContext): PartialFunction[MatchedResponse, Future[Result]] = {
    case SuccessOrFailureResponse(true) =>
      if (context.is5mld) {
        saveTrustMatchedStatusAndRedirect(
          context.userAnswers,
          Matched.Success,
          controllers.register.suitability.routes.ExpressTrustYesNoController.onPageLoad(context.mode, context.draftId)
        )
      } else {
        saveTrustMatchedStatusAndRedirect(context.userAnswers, Matched.Success, TaskListController.onPageLoad(context.draftId))
      }
  }

  private def unsuccessful(context: MatchingContext)
                          (implicit hc: HeaderCarrier, ec: ExecutionContext): PartialFunction[MatchedResponse, Future[Result]] = {
    case SuccessOrFailureResponse(false) =>
      saveTrustMatchedStatusAndRedirect(context.userAnswers, Matched.Failed, FailedMatchController.onPageLoad(context.draftId))
  }

  private def alreadyRegistered(context: MatchingContext)
                               (implicit hc: HeaderCarrier, ec: ExecutionContext): PartialFunction[MatchedResponse, Future[Result]] = {
    case AlreadyRegistered =>
      saveTrustMatchedStatusAndRedirect(context.userAnswers, Matched.AlreadyRegistered, TrustAlreadyRegisteredController.onPageLoad(context.draftId))
  }

  private def recover()(implicit ec: ExecutionContext): PartialFunction[MatchedResponse, Future[Result]] = {
    case _ =>
      Future.successful(Redirect(controllers.register.routes.MatchingDownController.onPageLoad()))
  }

  private def saveTrustMatchedStatusAndRedirect(userAnswers: UserAnswers,
                                                trustMatchedStatus: Matched,
                                                redirect: Call)(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[Result] = {
    for {
      updatedAnswers <- Future.fromTry(userAnswers.set(ExistingTrustMatched, trustMatchedStatus))
      _ <- registrationsRepository.set(updatedAnswers)
    } yield Redirect(redirect)
  }
}
