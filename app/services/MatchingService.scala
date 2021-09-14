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
import play.api.mvc.Results.Redirect
import controllers.register.routes._
import models.core.MatchingAndSuitabilityUserAnswers
import models.core.http.MatchedResponse.AlreadyRegistered
import models.core.http.{MatchData, MatchedResponse, SuccessOrFailureResponse}
import models.registration.Matched
import pages.register.{ExistingTrustMatched, MatchingNamePage, PostcodeForTheTrustPage, WhatIsTheUTRPage}
import play.api.mvc.{Call, Result}
import repositories.CacheRepository
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal

class MatchingService @Inject()(trustConnector: TrustConnector,
                                cacheRepository: CacheRepository) {

  private case class MatchingContext(isAgent: Boolean, userAnswers: MatchingAndSuitabilityUserAnswers)

  def matching(userAnswers: MatchingAndSuitabilityUserAnswers, isAgent: Boolean)
              (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Result] = {
    val matchResult = for {
      result <- {
        val context = MatchingContext(isAgent, userAnswers)
        val data = payload(userAnswers)
        attemptMatch(context, data)
      }
    } yield result

    matchResult recoverWith {
      case NonFatal(_) =>
        Future.successful(Redirect(MatchingDownController.onPageLoad()))
    }
  }

  private def payload(userAnswers: MatchingAndSuitabilityUserAnswers): Option[MatchData] = for {
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
          (
            successful(context) orElse
              unsuccessful(context) orElse
              alreadyRegistered(context) orElse
              recover()
            ).apply(r)
      }
    case None =>
      Future.successful(Redirect(MatchingDownController.onPageLoad()))
  }

  private def successful(context: MatchingContext)
                        (implicit ec: ExecutionContext): PartialFunction[MatchedResponse, Future[Result]] = {
    case SuccessOrFailureResponse(true) =>
        saveTrustMatchedStatusAndRedirect(
          context.userAnswers,
          Matched.Success,
          controllers.register.suitability.routes.ExpressTrustYesNoController.onPageLoad()
        )
  }

  private def unsuccessful(context: MatchingContext)
                          (implicit ec: ExecutionContext): PartialFunction[MatchedResponse, Future[Result]] = {
    case SuccessOrFailureResponse(false) =>
      saveTrustMatchedStatusAndRedirect(context.userAnswers, Matched.Failed, FailedMatchController.onPageLoad())
  }

  private def alreadyRegistered(context: MatchingContext)
                               (implicit ec: ExecutionContext): PartialFunction[MatchedResponse, Future[Result]] = {
    case AlreadyRegistered =>
      saveTrustMatchedStatusAndRedirect(context.userAnswers, Matched.AlreadyRegistered, TrustAlreadyRegisteredController.onPageLoad())
  }

  private def recover(): PartialFunction[MatchedResponse, Future[Result]] = {
    case _ =>
      Future.successful(Redirect(controllers.register.routes.MatchingDownController.onPageLoad()))
  }

  private def saveTrustMatchedStatusAndRedirect(userAnswers: MatchingAndSuitabilityUserAnswers, trustMatchedStatus: Matched, redirect: Call)
                                               (implicit ec: ExecutionContext): Future[Result] = {
    for {
      updatedAnswers <- Future.fromTry(userAnswers.set(ExistingTrustMatched, trustMatchedStatus))
      _ <- cacheRepository.set(updatedAnswers)
    } yield Redirect(redirect)
  }

}
