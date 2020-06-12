/*
 * Copyright 2020 HM Revenue & Customs
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

package navigation.registration

import config.FrontendAppConfig
import controllers.register.settlors.living_settlor.routes
import javax.inject.{Inject, Singleton}
import models.NormalMode
import models.core.UserAnswers
import models.core.pages.IndividualOrBusiness._
import models.registration.pages.AddASettlor
import models.registration.pages.KindOfTrust._
import navigation.Navigator
import pages._
import pages.register.settlors.living_settlor._
import pages.register.settlors.living_settlor.trust_type.{HoldoverReliefYesNoPage, KindOfTrustPage}
import pages.register.settlors.{AddASettlorPage, AddASettlorYesNoPage}
import play.api.mvc.Call
import uk.gov.hmrc.auth.core.AffinityGroup

@Singleton
class LivingSettlorNavigator @Inject()(config: FrontendAppConfig) extends Navigator(config) {

  override protected def route(draftId: String): PartialFunction[Page, AffinityGroup => UserAnswers => Call] = {
    case KindOfTrustPage => _ => kindOfTrustPage(draftId)
    case HoldoverReliefYesNoPage => _ => _ => routes.SettlorIndividualOrBusinessController.onPageLoad(NormalMode, 0, draftId)
    case SettlorIndividualNamePage(index) => _ => _ => routes.SettlorIndividualDateOfBirthYesNoController.onPageLoad(NormalMode, index, draftId)
    case SettlorIndividualDateOfBirthYesNoPage(index) => _ => settlorIndividualDateOfBirthYesNoPage(draftId, index)
    case SettlorIndividualDateOfBirthPage(index) => _ => _ => routes.SettlorIndividualNINOYesNoController.onPageLoad(NormalMode, index, draftId)
    case SettlorIndividualNINOYesNoPage(index) => _ => settlorIndividualNINOYesNoPage(draftId, index)
    case SettlorIndividualNINOPage(index) => _ => _ => routes.SettlorIndividualAnswerController.onPageLoad(index, draftId)
    case SettlorAddressYesNoPage(index) => _ => settlorIndividualAddressYesNoPage(draftId, index)
    case SettlorAddressUKYesNoPage(index) => _ => settlorIndividualAddressUKYesNoPage(draftId, index)
    case SettlorAddressUKPage(index) => _ => _ => routes.SettlorIndividualPassportYesNoController.onPageLoad(NormalMode, index, draftId)
    case SettlorAddressInternationalPage(index) => _ => _ => routes.SettlorIndividualPassportYesNoController.onPageLoad(NormalMode, index, draftId)
    case SettlorIndividualPassportYesNoPage(index) => _ => settlorIndividualPassportYesNoPage(draftId, index)
    case SettlorIndividualPassportPage(index) => _ => _ => routes.SettlorIndividualAnswerController.onPageLoad(index, draftId)
    case SettlorIndividualIDCardYesNoPage(index) => _ =>  settlorIndividualIDCardYesNoPage(draftId, index)
    case SettlorIndividualIDCardPage(index) => _ => _ => routes.SettlorIndividualAnswerController.onPageLoad(index, draftId)
    case SettlorIndividualOrBusinessPage(index) => _ => settlorIndividualOrBusinessPage(index, draftId)
    case SettlorIndividualAnswerPage => _ => _ => controllers.register.settlors.routes.AddASettlorController.onPageLoad(draftId)
    case SettlorBusinessNamePage(index)  =>_ => _ => routes.SettlorBusinessNameController.onPageLoad(NormalMode, index, draftId)
    case AddASettlorPage => _ => addSettlorRoute(draftId)
    case AddASettlorYesNoPage => _ => addASettlorYesNoRoute(draftId)
  }

  private def addASettlorYesNoRoute(draftId: String)(answers: UserAnswers) : Call = {
    answers.get(AddASettlorYesNoPage) match {
      case Some(true) =>
        routes.SettlorIndividualOrBusinessController.onPageLoad(NormalMode, 0, draftId)
      case Some(false) =>
        controllers.register.routes.TaskListController.onPageLoad(draftId)
      case _ => controllers.register.routes.SessionExpiredController.onPageLoad()
    }
  }

  private def addSettlorRoute(draftId: String)(answers: UserAnswers) = {
    val addAnother = answers.get(AddASettlorPage)

    def routeToSettlorIndex = {
      val settlors = answers.get(sections.LivingSettlors).getOrElse(List.empty)
      settlors match {
        case Nil =>
          routes.SettlorIndividualOrBusinessController.onPageLoad(NormalMode, 0, draftId)
        case t if t.nonEmpty =>
          routes.SettlorIndividualOrBusinessController.onPageLoad(NormalMode, t.size, draftId)
      }
    }

    addAnother match {
      case Some(AddASettlor.YesNow) =>
        routeToSettlorIndex
      case Some(AddASettlor.YesLater) =>
        controllers.register.routes.TaskListController.onPageLoad(draftId)
      case Some(AddASettlor.NoComplete) =>
        controllers.register.routes.TaskListController.onPageLoad(draftId)
      case _ => controllers.register.routes.SessionExpiredController.onPageLoad()
    }
  }

  private def kindOfTrustPage(draftId: String)(answers: UserAnswers) = {
    answers.get(KindOfTrustPage) match {
      case Some(Deed) =>
        controllers.register.settlors.routes.AdditionToWillTrustYesNoController.onPageLoad(NormalMode, draftId)
      case Some(Intervivos) =>
        routes.HoldoverReliefYesNoController.onPageLoad(NormalMode, draftId)
      case Some(FlatManagement) =>
        routes.SettlorIndividualOrBusinessController.onPageLoad(NormalMode, 0, draftId)
      case Some(HeritageMaintenanceFund) =>
        routes.SettlorIndividualOrBusinessController.onPageLoad(NormalMode, 0, draftId)
      case Some(Employees) =>
        routes.KindOfTrustController.onPageLoad(NormalMode, draftId)
      case _ => controllers.register.routes.SessionExpiredController.onPageLoad()
    }
  }

  private def settlorIndividualDateOfBirthYesNoPage(draftId: String, index: Int)(answers: UserAnswers) =
    answers.get(SettlorIndividualDateOfBirthYesNoPage(index)) match {
      case Some(true) => routes.SettlorIndividualDateOfBirthController.onPageLoad(NormalMode, index, draftId)
      case Some(false) => routes.SettlorIndividualNINOYesNoController.onPageLoad(NormalMode, index, draftId)
      case None => controllers.register.routes.SessionExpiredController.onPageLoad()
    }

  private def settlorIndividualNINOYesNoPage(draftId: String, index: Int)(answers: UserAnswers) =
    answers.get(SettlorIndividualNINOYesNoPage(index)) match {
      case Some(true) => routes.SettlorIndividualNINOController.onPageLoad(NormalMode, index, draftId)
      case Some(false) => routes.SettlorIndividualAddressYesNoController.onPageLoad(NormalMode, index, draftId)
      case None => controllers.register.routes.SessionExpiredController.onPageLoad()
    }

  private def settlorIndividualAddressYesNoPage(draftId: String, index: Int)(answers: UserAnswers) =
    answers.get(SettlorAddressYesNoPage(index)) match {
      case Some(true) => routes.SettlorIndividualAddressUKYesNoController.onPageLoad(NormalMode, index, draftId)
      case Some(false) => routes.SettlorIndividualAnswerController.onPageLoad(index, draftId)
      case None => controllers.register.routes.SessionExpiredController.onPageLoad()
    }

  private def settlorIndividualAddressUKYesNoPage(draftId: String, index: Int)(answers: UserAnswers) =
    answers.get(SettlorAddressUKYesNoPage(index)) match {
      case Some(true) => routes.SettlorIndividualAddressUKController.onPageLoad(NormalMode, index, draftId)
      case Some(false) => routes.SettlorIndividualAddressInternationalController.onPageLoad(NormalMode, index, draftId)
      case None => controllers.register.routes.SessionExpiredController.onPageLoad()
    }

  private def settlorIndividualPassportYesNoPage(draftId: String, index: Int)(answers: UserAnswers) =
    answers.get(SettlorIndividualPassportYesNoPage(index)) match {
      case Some(true) => routes.SettlorIndividualPassportController.onPageLoad(NormalMode, index, draftId)
      case Some(false) => routes.SettlorIndividualIDCardYesNoController.onPageLoad(NormalMode, index, draftId)
      case None => controllers.register.routes.SessionExpiredController.onPageLoad()
    }

  private def settlorIndividualIDCardYesNoPage(draftId: String, index: Int)(answers: UserAnswers) =
    answers.get(SettlorIndividualIDCardYesNoPage(index)) match {
      case Some(true) => routes.SettlorIndividualIDCardController.onPageLoad(NormalMode, index, draftId)
      case Some(false) => routes.SettlorIndividualAnswerController.onPageLoad(index, draftId)
      case None => controllers.register.routes.SessionExpiredController.onPageLoad()
    }

  private def settlorIndividualOrBusinessPage(index: Int, draftId: String)(answers: UserAnswers) =
    answers.get(SettlorIndividualOrBusinessPage(index)) match {
      case Some(Individual) => routes.SettlorIndividualNameController.onPageLoad(NormalMode, index, draftId)
      case Some(Business) =>
        if(config.livingSettlorBusinessEnabled) {
          routes.SettlorBusinessNameController.onPageLoad(NormalMode, index, draftId)
        } else {
          routes.SettlorIndividualOrBusinessController.onPageLoad(NormalMode, index, draftId)
        }
      case None => controllers.register.routes.SessionExpiredController.onPageLoad()
    }

}
