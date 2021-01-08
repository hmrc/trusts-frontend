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
import controllers.register.agents.{routes => agentRoutes}
import controllers.register.routes._
import models.Mode
import models.core.UserAnswers
import models.core.http.MatchedResponse.AlreadyRegistered
import models.core.http.{MatchData, SuccessOrFailureResponse}
import models.registration.Matched
import pages.register.{ExistingTrustMatched, MatchingNamePage, PostcodeForTheTrustPage, WhatIsTheUTRPage}
import play.api.mvc.{Call, Result}
import repositories.RegistrationsRepository
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class MatchingService @Inject()(trustConnector: TrustConnector,
                                registrationsRepository: RegistrationsRepository) {

  def matching(userAnswers: UserAnswers, draftId: String, isAgent: Boolean, mode: Mode)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Result] = {

    def saveTrustMatchedStatusAndRedirect(trustMatchedStatus: Matched, redirect: Call): Future[Result] = {
      for {
        updatedAnswers <- Future.fromTry(userAnswers.set(ExistingTrustMatched, trustMatchedStatus))
        _ <- registrationsRepository.set(updatedAnswers)
      } yield Redirect(redirect)
    }

    (for {
      utr: String <- userAnswers.get(WhatIsTheUTRPage)
      name: String <- userAnswers.get(MatchingNamePage)
      postcode: Option[String] = userAnswers.get(PostcodeForTheTrustPage)
    } yield {
      trustConnector.matching(MatchData(utr, name, postcode)) flatMap {
        case SuccessOrFailureResponse(true) if isAgent =>
          saveTrustMatchedStatusAndRedirect(Matched.Success, agentRoutes.AgentInternalReferenceController.onPageLoad(mode, draftId))
        case SuccessOrFailureResponse(true) =>
          saveTrustMatchedStatusAndRedirect(Matched.Success, TaskListController.onPageLoad(draftId))
        case SuccessOrFailureResponse(false) =>
          saveTrustMatchedStatusAndRedirect(Matched.Failed, FailedMatchController.onPageLoad(draftId))
        case AlreadyRegistered =>
          saveTrustMatchedStatusAndRedirect(Matched.AlreadyRegistered, TrustAlreadyRegisteredController.onPageLoad(draftId))
        case _ =>
          Future.successful(Redirect(MatchingDownController.onPageLoad()))
      }
    }).getOrElse(Future.successful(Redirect(controllers.register.routes.FailedMatchController.onPageLoad(draftId))))
  }

}
