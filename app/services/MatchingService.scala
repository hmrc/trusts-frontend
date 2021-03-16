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
import models.{Mode, NormalMode}
import models.core.UserAnswers
import models.core.http.MatchedResponse.AlreadyRegistered
import models.core.http.{MatchData, SuccessOrFailureResponse}
import models.registration.Matched
import navigation.registration.TaskListNavigator
import pages.register.{ExistingTrustMatched, MatchingNamePage, PostcodeForTheTrustPage, WhatIsTheUTRPage}
import play.api.mvc.{Call, Result, Results}
import repositories.RegistrationsRepository
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class MatchingService @Inject()(trustConnector: TrustConnector,
                                registrationsRepository: RegistrationsRepository,
                                featureFlagService: FeatureFlagService,
                                navigator: TaskListNavigator) {

  def matching(userAnswers: UserAnswers, draftId: String, isAgent: Boolean, mode: Mode)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Result] = {

    def saveTrustMatchedStatusAndRedirect(trustMatchedStatus: Matched, redirect: Call): Future[Result] = {
      for {
        updatedAnswers <- Future.fromTry(userAnswers.set(ExistingTrustMatched, trustMatchedStatus))
        _ <- registrationsRepository.set(updatedAnswers)
      } yield Redirect(redirect)
    }

    featureFlagService.is5mldEnabled().flatMap {
      is5mld =>
        (for {
          utr: String <- userAnswers.get(WhatIsTheUTRPage)
          name: String <- userAnswers.get(MatchingNamePage)
          postcode: Option[String] = userAnswers.get(PostcodeForTheTrustPage)
        } yield {
          /*
          * 1. Get is5MLD flag from featureFlagService
          * 2. IF is Agent THEN
          *   IF is5MLD goto Express trust page
          *   ELSE goto agentDetailsJourneyUrl
          * 3. IF not and Agent THEN
          *   IF is5MLD goto Express trust page
          *   ELSE got TaskListController
          * */
          trustConnector.matching(MatchData(utr, name, postcode)) flatMap {
            case SuccessOrFailureResponse(true) if isAgent =>
              if (is5mld){
                saveTrustMatchedStatusAndRedirect(Matched.Success, controllers.register.suitability.routes.ExpressTrustYesNoController.onPageLoad(mode, draftId))
              } else {
                saveTrustMatchedStatusAndRedirect(Matched.Success, Call("GET", navigator.agentDetailsJourneyUrl(draftId)))
              }
            case SuccessOrFailureResponse(true) =>
              if (is5mld) {
                saveTrustMatchedStatusAndRedirect(Matched.Success, controllers.register.suitability.routes.ExpressTrustYesNoController.onPageLoad(mode, draftId))
              } else {
                saveTrustMatchedStatusAndRedirect(Matched.Success, TaskListController.onPageLoad(draftId))
              }
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
}
