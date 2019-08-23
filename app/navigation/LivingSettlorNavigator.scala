/*
 * Copyright 2019 HM Revenue & Customs
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

package navigation

import javax.inject.Singleton
import models.{NormalMode, UserAnswers}
import pages.Page
import pages.living_settlor._
import play.api.mvc.Call
import uk.gov.hmrc.auth.core.AffinityGroup
import controllers.living_settlor.routes

@Singleton
class LivingSettlorNavigator extends Navigator {

  override protected def normalRoutes(draftId: String): Page => AffinityGroup => UserAnswers => Call = {
    case SettlorIndividualNamePage(index) => _ => _ => routes.SettlorIndividualDateOfBirthYesNoController.onPageLoad(NormalMode, index, draftId)
    case SettlorIndividualDateOfBirthYesNoPage(index) => _ => settlorIndividualDateOfBirthYesNoPage(draftId, index)
    case SettlorIndividualDateOfBirthPage(index) => _ => _ => routes.SettlorIndividualNINOYesNoController.onPageLoad(NormalMode, index, draftId)
    case SettlorIndividualNINOYesNoPage(index) => _ => settlorIndividualNINOYesNoPage(draftId, index)
    case SettlorIndividualNINOPage(index) => _ => _ => ???
    case SettlorIndividualAddressYesNoPage(index) => _ => _ => routes.SettlorIndividualAddressUKYesNoController.onPageLoad(NormalMode, index, draftId)
    case SettlorIndividualAddressUKYesNoPage(index) => _ => settlorIndividualAddressUKYesNoPage(draftId, index)
    case SettlorIndividualAddressUKPage(index) => _ => _ => routes.SettlorIndividualPassportYesNoController.onPageLoad(NormalMode, index, draftId)
    case SettlorIndividualAddressInternationalPage(index) => _ => _ => routes.SettlorIndividualPassportYesNoController.onPageLoad(NormalMode, index, draftId)
    case SettlorIndividualPassportYesNoPage(index) => _ => settlorIndividualPassportYesNoPage(draftId, index)
    case SettlorIndividualIDCardYesNoPage(index) => _ =>  settlorIndividualIDCardYesNoPage(draftId, index)
  }

  private def settlorIndividualDateOfBirthYesNoPage(draftId: String, index: Int)(answers: UserAnswers) =
    answers.get(SettlorIndividualDateOfBirthYesNoPage(index)) match {
      case Some(true) => routes.SettlorIndividualDateOfBirthController.onPageLoad(NormalMode, index, draftId)
      case Some(false) => routes.SettlorIndividualNINOYesNoController.onPageLoad(NormalMode, index, draftId)
      case None => controllers.routes.SessionExpiredController.onPageLoad()
    }

  private def settlorIndividualNINOYesNoPage(draftId: String, index: Int)(answers: UserAnswers) =
    answers.get(SettlorIndividualNINOYesNoPage(index)) match {
      case Some(true) => routes.SettlorIndividualNINOController.onPageLoad(NormalMode, index, draftId)
      case Some(false) => routes.SettlorIndividualAddressYesNoController.onPageLoad(NormalMode, index, draftId)
      case None => controllers.routes.SessionExpiredController.onPageLoad()
    }

  private def settlorIndividualAddressUKYesNoPage(draftId: String, index: Int)(answers: UserAnswers) =
    answers.get(SettlorIndividualAddressUKYesNoPage(index)) match {
      case Some(true) => routes.SettlorIndividualAddressUKController.onPageLoad(NormalMode, index, draftId)
      case Some(false) => routes.SettlorIndividualAddressInternationalController.onPageLoad(NormalMode, index, draftId)
      case None => controllers.routes.SessionExpiredController.onPageLoad()
    }

  private def settlorIndividualPassportYesNoPage(draftId: String, index: Int)(answers: UserAnswers) =
    answers.get(SettlorIndividualPassportYesNoPage(index)) match {
      case Some(true) => routes.SettlorIndividualPassportController.onPageLoad(NormalMode, index, draftId)
      case Some(false) => routes.SettlorIndividualIDCardYesNoController.onPageLoad(NormalMode, index, draftId)
      case None => controllers.routes.SessionExpiredController.onPageLoad()
    }

  private def settlorIndividualIDCardYesNoPage(draftId: String, index: Int)(answers: UserAnswers) =
    answers.get(SettlorIndividualIDCardYesNoPage(index)) match {
      case None => controllers.routes.SessionExpiredController.onPageLoad()
      case _ => routes.SettlorIndividualNINOYesNoController.onPageLoad(NormalMode, index, draftId)
    }

}
