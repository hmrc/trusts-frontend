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
import controllers.register.settlors.living_settlor.routes
import models.core.pages.{FullName, IndividualOrBusiness}
import models.registration.pages.Status.{Completed, InProgress}
import pages.entitystatus.{DeceasedSettlorStatus, LivingSettlorStatus}
import pages.register.settlors.SetUpAfterSettlorDiedYesNoPage
import pages.register.settlors.deceased_settlor.{SettlorDateOfDeathYesNoPage, SettlorsNamePage}
import pages.register.settlors.living_settlor._
import viewmodels.AddRow

class AddASettlorViewHelperSpec extends RegistrationSpecBase   {

  val settlorName = FullName("first name", Some("middle name"), "last name")
  val featureUnavalible = "/trusts-registration/feature-not-available"

  def removeSettlorRoute(index : Int) =
    routes.RemoveSettlorController.onPageLoad(index, fakeDraftId).url

  "AddSettlorViewHelper" when {

    ".row" must {

      "generate Nil for no user answers" in {
        val rows = new AddASettlorViewHelper(emptyUserAnswers, fakeDraftId).rows
        rows.inProgress mustBe Nil
        rows.complete mustBe Nil
      }

      "generate rows from user answers for settlors in progress" when {
        "living" in {

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
            AddRow("No name added", typeLabel = "Individual Settlor", featureUnavalible, removeSettlorRoute(0)),
            AddRow("first name last name", typeLabel = "Individual Settlor", featureUnavalible, removeSettlorRoute(1))
          )
          rows.complete mustBe Nil
        }
        "deceased" in {

          val userAnswers = emptyUserAnswers
            .set(SetUpAfterSettlorDiedYesNoPage, false).success.value
            .set(SettlorsNamePage, FullName("first name", None, "last name")).success.value
            .set(SettlorDateOfDeathYesNoPage, false).success.value
            .set(DeceasedSettlorStatus, InProgress).success.value

          val rows = new AddASettlorViewHelper(userAnswers, fakeDraftId).rows
          rows.inProgress mustBe List(
            AddRow("first name last name", typeLabel = "Will Trust", featureUnavalible, removeSettlorRoute(0))
          )
          rows.complete mustBe Nil
        }
      }

      "generate rows from user answers for complete settlors" when {
        "living" in {

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
            AddRow("first name last name", typeLabel = "Individual Settlor", featureUnavalible, removeSettlorRoute(0))
          )
          rows.inProgress mustBe Nil
        }
        "deceased" in {

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
            AddRow("first name last name", typeLabel = "Will Trust", featureUnavalible, removeSettlorRoute(0))
          )
          rows.inProgress mustBe Nil
        }
      }

    }
  }
}