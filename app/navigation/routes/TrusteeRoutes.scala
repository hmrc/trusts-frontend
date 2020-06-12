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

package navigation.routes

import controllers.register.routes
import models.NormalMode
import models.core.UserAnswers
import models.core.pages.IndividualOrBusiness
import models.registration.pages.AddATrustee
import pages.Page
import pages.register.trustees.individual._
import pages.register.trustees.organisation._
import pages.register.trustees._
import play.api.mvc.Call
import sections.Trustees
import uk.gov.hmrc.auth.core.AffinityGroup

object TrusteeRoutes {
  def route(draftId: String): PartialFunction[Page, AffinityGroup => UserAnswers => Call] = {
    case IsThisLeadTrusteePage(index) => _ =>_ => controllers.register.trustees.routes.TrusteeIndividualOrBusinessController.onPageLoad(NormalMode, index, draftId)
    case TrusteeIndividualOrBusinessPage(index)  => _ => ua => trusteeIndividualOrBusinessRoute(ua, index, draftId)
    case TrusteeUtrYesNoPage(index) => _ => _ => controllers.register.trustees.organisation.routes.TrusteeBusinessNameController.onPageLoad(NormalMode, index, draftId)
    case TrusteeOrgNamePage(index)  => _ => ua => trusteeBusinessNameRoute(ua, index, draftId)
    case TrusteesUtrPage(index) => _ => _ => controllers.register.trustees.organisation.routes.TrusteeOrgAddressUkYesNoController.onPageLoad(NormalMode, index, draftId)
    case TrusteeOrgAddressUkYesNoPage(index) => _ => ua => trusteeOrgAddressUkYesNoRoute(ua, index, draftId)
    case TrusteeOrgAddressUkPage(index) => _ => _ => controllers.register.trustees.organisation.routes.TrusteeOrgTelephoneNumberController.onPageLoad(NormalMode, index, draftId)
    case TrusteeOrgAddressInternationalPage(index) => _ => _ => controllers.register.trustees.organisation.routes.TrusteeOrgTelephoneNumberController.onPageLoad(NormalMode, index, draftId)

    case TrusteesNamePage(index) => _ => _ => controllers.register.trustees.individual.routes.TrusteesDateOfBirthController.onPageLoad(NormalMode, index, draftId)
    case TrusteesDateOfBirthPage(index) => _ => ua => trusteeDateOfBirthRoute(ua, index, draftId)
    case TrusteeAUKCitizenPage(index) => _ => ua => trusteeAUKCitizenRoute(ua, index, draftId)
    case TrusteesNinoPage(index) => _ => _ => controllers.register.trustees.individual.routes.TrusteeLiveInTheUKController.onPageLoad(NormalMode, index, draftId)
    case TrusteeAddressInTheUKPage(index)  => _ => ua => trusteeLiveInTheUKRoute(ua, index, draftId)
    case TrusteesUkAddressPage(index) => _ => _ => controllers.register.trustees.individual.routes.TelephoneNumberController.onPageLoad(NormalMode, index, draftId)
    case TelephoneNumberPage(index) => _ => _ => controllers.register.trustees.routes.TrusteesAnswerPageController.onPageLoad(index, draftId)
    case TrusteesAnswerPage => _ => _ => controllers.register.trustees.routes.AddATrusteeController.onPageLoad(draftId)
    case AddATrusteePage => _ => addATrusteeRoute(draftId)
    case AddATrusteeYesNoPage => _ => addATrusteeYesNoRoute(draftId)
  }
  private def addATrusteeYesNoRoute(draftId: String)(answers: UserAnswers) : Call = {
    answers.get(AddATrusteeYesNoPage) match {
      case Some(true) =>
        controllers.register.trustees.routes.IsThisLeadTrusteeController.onPageLoad(NormalMode, 0, draftId)
      case Some(false) =>
        routes.TaskListController.onPageLoad(draftId)
      case _ => routes.SessionExpiredController.onPageLoad()
    }
  }

  private def addATrusteeRoute(draftId: String)(answers: UserAnswers) = {
    val addAnother = answers.get(AddATrusteePage)

    def routeToTrusteeIndex = {
      val trustees = answers.get(Trustees).getOrElse(List.empty)
      trustees match {
        case Nil =>
          controllers.register.trustees.routes.IsThisLeadTrusteeController.onPageLoad(NormalMode, 0, draftId)
        case t if t.nonEmpty =>
          controllers.register.trustees.routes.IsThisLeadTrusteeController.onPageLoad(NormalMode, t.size, draftId)
      }
    }

    addAnother match {
      case Some(AddATrustee.YesNow) =>
        routeToTrusteeIndex
      case Some(AddATrustee.YesLater) =>
        routes.TaskListController.onPageLoad(draftId)
      case Some(AddATrustee.NoComplete) =>
        routes.TaskListController.onPageLoad(draftId)
      case _ => routes.SessionExpiredController.onPageLoad()
    }
  }
  private def trusteeAUKCitizenRoute(answers: UserAnswers, index: Int, draftId: String) = answers.get(TrusteeAUKCitizenPage(index)) match {
    case Some(true)   => controllers.register.trustees.individual.routes.TrusteesNinoController.onPageLoad(NormalMode,index, draftId)
    case Some(false)  => controllers.register.trustees.individual.routes.TrusteeAUKCitizenController.onPageLoad(NormalMode,index, draftId)
    case None         => routes.SessionExpiredController.onPageLoad()
  }

  private def trusteeLiveInTheUKRoute(answers: UserAnswers, index: Int, draftId: String) = answers.get(TrusteeAddressInTheUKPage(index)) match {
    case Some(true)   => controllers.register.trustees.individual.routes.TrusteesUkAddressController.onPageLoad(NormalMode,index, draftId)
    case Some(false)  => controllers.register.trustees.individual.routes.TrusteeLiveInTheUKController.onPageLoad(NormalMode,index, draftId)
    case None         => routes.SessionExpiredController.onPageLoad()
  }

  private def trusteeOrgAddressUkYesNoRoute(answers: UserAnswers, index: Int, draftId: String) = answers.get(TrusteeOrgAddressUkYesNoPage(index)) match {
    case Some(true)   => controllers.register.trustees.organisation.routes.TrusteesOrgUkAddressController.onPageLoad(NormalMode,index, draftId)
    case Some(false)  => controllers.register.trustees.organisation.routes.TrusteeOrgAddressInternationalController.onPageLoad(NormalMode,index, draftId)
    case None         => routes.SessionExpiredController.onPageLoad()
  }

  private def trusteeDateOfBirthRoute(answers: UserAnswers, index : Int, draftId: String) = answers.get(IsThisLeadTrusteePage(index)) match {
    case Some(true) => controllers.register.trustees.individual.routes.TrusteeAUKCitizenController.onPageLoad(NormalMode, index, draftId)
    case Some(false) => controllers.register.trustees.routes.TrusteesAnswerPageController.onPageLoad(index, draftId)
    case None => routes.SessionExpiredController.onPageLoad()
  }

  private def trusteeIndividualOrBusinessRoute(answers: UserAnswers, index : Int, draftId: String) = {
    (answers.get(IsThisLeadTrusteePage(index)), answers.get(TrusteeIndividualOrBusinessPage(index))) match {
      case (Some(_), Some(IndividualOrBusiness.Individual)) => controllers.register.trustees.individual.routes.TrusteesNameController.onPageLoad(NormalMode, index, draftId)
      case (Some(true), Some(IndividualOrBusiness.Business)) => controllers.register.trustees.organisation.routes.TrusteeUtrYesNoController.onPageLoad(NormalMode,index, draftId)
      case (Some(false), Some(IndividualOrBusiness.Business)) => controllers.register.trustees.routes.TrusteeIndividualOrBusinessController.onPageLoad(NormalMode,index, draftId)
      case _ => routes.SessionExpiredController.onPageLoad()
    }
  }

  private def trusteeBusinessNameRoute(answers: UserAnswers, index : Int, draftId: String) = answers.get(TrusteeUtrYesNoPage(index)) match {
    case Some(true) => controllers.register.trustees.organisation.routes.TrusteeUtrController.onPageLoad(NormalMode, index, draftId)
    case Some(false) => controllers.register.trustees.organisation.routes.TrusteeOrgAddressUkYesNoController.onPageLoad(NormalMode,index, draftId)
    case None => routes.SessionExpiredController.onPageLoad()
  }

}
