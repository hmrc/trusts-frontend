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

package utils

import base.RegistrationSpecBase
import controllers.register.settlors.routes
import controllers.register.settlors.living_settlor.{routes => individualRoutes}
import controllers.register.settlors.living_settlor.business.{routes => businessRoutes}
import controllers.register.settlors.deceased_settlor.{routes => deceasedRoutes}
import models.NormalMode
import models.core.pages.{FullName, IndividualOrBusiness}
import models.registration.pages.KindOfTrust.Deed
import models.registration.pages.Status.{Completed, InProgress}
import pages.entitystatus.{DeceasedSettlorStatus, LivingSettlorStatus}
import pages.register.settlors.SetUpAfterSettlorDiedYesNoPage
import pages.register.settlors.deceased_settlor._
import pages.register.settlors.living_settlor._
import pages.register.settlors.living_settlor.business.{SettlorBusinessNamePage, SettlorBusinessUtrYesNoPage}
import pages.register.settlors.living_settlor.trust_type.{KindOfTrustPage, SetUpInAdditionToWillTrustYesNoPage}
import viewmodels.AddRow

class AddASettlorViewHelperSpec extends RegistrationSpecBase   {

  val index = 0
  val indexOne = 1
  val settlorName: FullName = FullName("first name", Some("middle name"), "last name")
  val settlorBusinessName = "Business Name"
  val utr = "1234567890"

  def individualSettlorStartRoute(index: Int): String = individualRoutes.SettlorIndividualNameController.onPageLoad(NormalMode, index, fakeDraftId).url
  def businessSettlorStartRoute(index: Int): String = businessRoutes.SettlorBusinessNameController.onPageLoad(NormalMode, index, fakeDraftId).url
  def deceasedSettlorStartRoute: String = deceasedRoutes.SettlorsNameController.onPageLoad(NormalMode, fakeDraftId).url

  def individualSettlorCYARoute(index: Int): String = individualRoutes.SettlorIndividualAnswerController.onPageLoad(index, fakeDraftId).url
  def businessSettlorCYARoute(index: Int): String = businessRoutes.SettlorBusinessAnswerController.onPageLoad(index, fakeDraftId).url
  def deceasedSettlorCYARoute: String = deceasedRoutes.DeceasedSettlorAnswerController.onPageLoad(fakeDraftId).url

  def removeLivingSettlorRoute(index: Int): String = routes.RemoveSettlorYesNoController.onPageLoadLiving(index, fakeDraftId).url
  def removeDeceasedSettlorRoute(): String = routes.RemoveSettlorYesNoController.onPageLoadDeceased(fakeDraftId).url

  "AddSettlorViewHelper" when {

    ".row" must {

      "generate Nil for no user answers" in {
        val rows = new AddASettlorViewHelper(emptyUserAnswers, fakeDraftId).rows
        rows.inProgress mustBe Nil
        rows.complete mustBe Nil
      }

      "generate rows from user answers for settlors in progress" when {
        "individual" in {

          val userAnswers = emptyUserAnswers
            .set(SetUpAfterSettlorDiedYesNoPage, false).success.value
            .set(SettlorIndividualOrBusinessPage(0), IndividualOrBusiness.Individual).success.value
            .set(SettlorIndividualOrBusinessPage(1), IndividualOrBusiness.Individual).success.value
            .set(SettlorIndividualNamePage(1), settlorName).success.value
            .set(SettlorIndividualDateOfBirthYesNoPage(1), false).success.value
            .set(SettlorIndividualNINOYesNoPage(1), false).success.value
            .set(LivingSettlorStatus(0), InProgress).success.value
            .set(LivingSettlorStatus(1), InProgress).success.value

          val rows = new AddASettlorViewHelper(userAnswers, fakeDraftId).rows
          rows.inProgress mustBe List(
            AddRow("No name added", typeLabel = "Individual Settlor", individualSettlorStartRoute(index), removeLivingSettlorRoute(index)),
            AddRow("first name last name", typeLabel = "Individual Settlor", individualSettlorStartRoute(indexOne), removeLivingSettlorRoute(indexOne))
          )
          rows.complete mustBe Nil
        }

        "business" in {

          val userAnswers = emptyUserAnswers
            .set(SetUpAfterSettlorDiedYesNoPage, false).success.value
            .set(SettlorIndividualOrBusinessPage(0), IndividualOrBusiness.Business).success.value
            .set(SettlorBusinessNamePage(0), settlorBusinessName).success.value
            .set(SettlorBusinessUtrYesNoPage(0), false).success.value
            .set(LivingSettlorStatus(0), InProgress).success.value

          val rows = new AddASettlorViewHelper(userAnswers, fakeDraftId).rows
          rows.inProgress mustBe List(
            AddRow("Business Name", typeLabel = "Business Settlor", businessSettlorStartRoute(index), removeLivingSettlorRoute(index))
          )
          rows.complete mustBe Nil
        }
        "deceased" in {

          val userAnswers = emptyUserAnswers
            .set(SetUpAfterSettlorDiedYesNoPage, true).success.value
            .set(SettlorsNamePage, FullName("first name", None, "last name")).success.value
            .set(SettlorDateOfDeathYesNoPage, false).success.value
            .set(DeceasedSettlorStatus, InProgress).success.value

          val rows = new AddASettlorViewHelper(userAnswers, fakeDraftId).rows
          rows.inProgress mustBe List(
            AddRow("first name last name", typeLabel = "Will Trust", deceasedSettlorStartRoute, removeDeceasedSettlorRoute())
          )
          rows.complete mustBe Nil
        }
      }

      "generate rows from user answers for complete settlors" when {
        "individual" in {

          val userAnswers = emptyUserAnswers
            .set(SetUpAfterSettlorDiedYesNoPage, false).success.value
            .set(SettlorIndividualOrBusinessPage(0), IndividualOrBusiness.Individual).success.value
            .set(SettlorIndividualNamePage(0), settlorName).success.value
            .set(SettlorIndividualDateOfBirthYesNoPage(0), false).success.value
            .set(SettlorIndividualNINOYesNoPage(0), false).success.value
            .set(SettlorAddressYesNoPage(0), false).success.value
            .set(LivingSettlorStatus(0), Completed).success.value

          val rows = new AddASettlorViewHelper(userAnswers, fakeDraftId).rows
          rows.complete mustBe List(
            AddRow("first name last name", typeLabel = "Individual Settlor", individualSettlorCYARoute(index), removeLivingSettlorRoute(index))
          )
          rows.inProgress mustBe Nil
        }
        "business" in {

          val userAnswers = emptyUserAnswers
            .set(SetUpAfterSettlorDiedYesNoPage, false).success.value
            .set(SettlorIndividualOrBusinessPage(0), IndividualOrBusiness.Business).success.value
            .set(SettlorBusinessNamePage(0), settlorBusinessName).success.value
            .set(SettlorBusinessUtrYesNoPage(0), false).success.value
            .set(SettlorAddressYesNoPage(0), false).success.value
            .set(LivingSettlorStatus(0), Completed).success.value

          val rows = new AddASettlorViewHelper(userAnswers, fakeDraftId).rows
          rows.complete mustBe List(
            AddRow("Business Name", typeLabel = "Business Settlor", businessSettlorCYARoute(index), removeLivingSettlorRoute(index))
          )
          rows.inProgress mustBe Nil
        }
        "deceased" in {

          val userAnswers = emptyUserAnswers
            .set(SetUpAfterSettlorDiedYesNoPage, false).success.value
            .set(KindOfTrustPage, Deed).success.value
            .set(SetUpInAdditionToWillTrustYesNoPage, true).success.value
            .set(SettlorsNamePage, FullName("first name", None, "last name")).success.value
            .set(SettlorDateOfDeathYesNoPage, false).success.value
            .set(SettlorDateOfBirthYesNoPage, false).success.value
            .set(SettlorsNationalInsuranceYesNoPage, false).success.value
            .set(SettlorsLastKnownAddressYesNoPage, false).success.value
            .set(DeceasedSettlorStatus, Completed).success.value

          val rows = new AddASettlorViewHelper(userAnswers, fakeDraftId).rows
          rows.complete mustBe List(
            AddRow("first name last name", typeLabel = "Will Trust", deceasedSettlorCYARoute, removeDeceasedSettlorRoute())
          )
          rows.inProgress mustBe Nil
        }
      }

    }
  }
}
