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

package utils

import base.SpecBase
import models.Status.{Completed, InProgress}
import models.{FullName, IndividualOrBusiness}
import pages.entitystatus.LivingSettlorStatus
import pages.living_settlor._
import pages.SetupAfterSettlorDiedPage
import viewmodels.AddRow

class AddASettlorViewHelperSpec extends SpecBase   {

  val settlorName = FullName("first name", Some("middle name"), "last name")

  "AddSettlorViewHelper" when {

    ".row" must {

      "generate Nil for no user answers" in {
        val rows = new AddASettlorViewHelper(emptyUserAnswers, fakeDraftId).rows
        rows.inProgress mustBe Nil
        rows.complete mustBe Nil
      }

      "generate rows from user answers for settlors in progress" in {

        val userAnswers = emptyUserAnswers
          .set(SetupAfterSettlorDiedPage, false).success.value
          .set(SettlorIndividualOrBusinessPage(0), IndividualOrBusiness.Individual).success.value
          .set(SettlorIndividualOrBusinessPage(1), IndividualOrBusiness.Individual).success.value
          .set(SettlorIndividualNamePage(1), settlorName).success.value
          .set(SettlorIndividualDateOfBirthYesNoPage(1), false).success.value
          .set(SettlorIndividualNINOYesNoPage(1), false).success.value
          .set(LivingSettlorStatus(0), InProgress).success.value
          .set(LivingSettlorStatus(1), InProgress).success.value

        val rows = new AddASettlorViewHelper(userAnswers, fakeDraftId).rows
        rows.inProgress mustBe List(
          AddRow("No name added", typeLabel = "Individual Settlor", "#", "#"),
          AddRow("first name last name", typeLabel = "Individual Settlor", "#", "#")
        )
        rows.complete mustBe Nil
      }

      "generate rows from user answers for complete settlors" in {

        val userAnswers = emptyUserAnswers
          .set(SetupAfterSettlorDiedPage, false).success.value
          .set(SettlorIndividualOrBusinessPage(0), IndividualOrBusiness.Individual).success.value
          .set(SettlorIndividualNamePage(0), settlorName).success.value
          .set(SettlorIndividualDateOfBirthYesNoPage(0), false).success.value
          .set(SettlorIndividualNINOYesNoPage(0), false).success.value
          .set(SettlorIndividualAddressYesNoPage(0), false).success.value
          .set(LivingSettlorStatus(0), Completed).success.value

        val rows = new AddASettlorViewHelper(userAnswers, fakeDraftId).rows
        rows.complete mustBe List(
          AddRow("first name last name", typeLabel = "Individual Settlor", "#", "#")
        )
        rows.inProgress mustBe Nil
      }

    }
  }
}