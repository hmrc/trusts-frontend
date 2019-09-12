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

package navigation.navigators

import base.SpecBase
import controllers.routes
import generators.Generators
import models.{NormalMode, UserAnswers}
import navigation.Navigator
import org.scalacheck.Arbitrary.arbitrary
import org.scalatest.prop.PropertyChecks
import pages._


trait DeceasedSettlorRoutes {

  self: PropertyChecks with Generators with SpecBase =>

  def deceasedSettlorRoutes()(implicit navigator: Navigator) = {

    "go to WhatKindOfTrustPage from SetupAfterSettlorDiedPage when user answers no" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          val answers = userAnswers.set(SetupAfterSettlorDiedPage, value = false).success.value

          navigator.nextPage(SetupAfterSettlorDiedPage, NormalMode, fakeDraftId)(answers)
            .mustBe(routes.SettlorKindOfTrustController.onPageLoad(NormalMode, fakeDraftId))
      }
    }

    "go to SettlorsNamePage from SetupAfterSettlorDiedPage when user answers yes" in {
      forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            val answers = userAnswers.set(SetupAfterSettlorDiedPage, value = true).success.value

            navigator.nextPage(SetupAfterSettlorDiedPage, NormalMode, fakeDraftId)(answers)
              .mustBe(routes.SettlorsNameController.onPageLoad(NormalMode, fakeDraftId))
        }
    }

    "go to SettlorDateOfDeathYesNoPage from SettlorsNamePage" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          navigator.nextPage(SettlorsNamePage, NormalMode, fakeDraftId)(userAnswers)
            .mustBe(routes.SettlorDateOfDeathYesNoController.onPageLoad(NormalMode, fakeDraftId))
      }
    }
    "go to SettlorDateOfBirthYesNoPage from SettlorDateOfDeathYesNoPage when user answers no" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          val answers = userAnswers.set(SettlorDateOfDeathYesNoPage, value = false).success.value
          navigator.nextPage(SettlorDateOfDeathYesNoPage, NormalMode, fakeDraftId)(answers)
            .mustBe(routes.SettlorDateOfBirthYesNoController.onPageLoad(NormalMode, fakeDraftId))
      }
    }

    "go to SettlorDateOfDeathPage from SettlorDateOfDeathYesNoPage when user answers yes" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          val answers = userAnswers.set(SettlorDateOfDeathYesNoPage, value = true).success.value
          navigator.nextPage(SettlorDateOfDeathYesNoPage, NormalMode, fakeDraftId)(answers)
            .mustBe(routes.SettlorDateOfDeathController.onPageLoad(NormalMode, fakeDraftId))
      }
    }

    "go to SettlorDateOfBirthYesNoPage from SettlorDateOfDeathPage" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          navigator.nextPage(SettlorDateOfDeathPage, NormalMode, fakeDraftId)(userAnswers)
            .mustBe(routes.SettlorDateOfBirthYesNoController.onPageLoad(NormalMode, fakeDraftId))
      }
    }

    "go to SettlorDateOfBirthPage from SettlorDateOfBirthYesNoPage when user answers yes" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          val answers = userAnswers.set(SettlorDateOfBirthYesNoPage, value = true).success.value

          navigator.nextPage(SettlorDateOfBirthYesNoPage, NormalMode, fakeDraftId)(answers)
            .mustBe(routes.SettlorsDateOfBirthController.onPageLoad(NormalMode, fakeDraftId))
      }
    }

    "go to SettlorNINoYesNoPage from SettlorsDateOfBirthPage" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          navigator.nextPage(SettlorsDateOfBirthPage, NormalMode, fakeDraftId)(userAnswers)
            .mustBe(routes.SettlorsNINoYesNoController.onPageLoad(NormalMode, fakeDraftId))
      }
    }

    "go to SettlorNationalInsuranceNumberPage from SettlorsNINOYesNoPage when user answers yes" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          val answers = userAnswers.set(SettlorsNINoYesNoPage, true).success.value

          navigator.nextPage(SettlorsNINoYesNoPage, NormalMode, fakeDraftId)(answers)
            .mustBe(routes.SettlorNationalInsuranceNumberController.onPageLoad(NormalMode, fakeDraftId))
      }
    }

    "go to DeceasedSettlorAnswerPage from SettlorNationalInsuranceNumberPage" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          navigator.nextPage(SettlorNationalInsuranceNumberPage, NormalMode, fakeDraftId)(userAnswers)
            .mustBe(routes.DeceasedSettlorAnswerController.onPageLoad(fakeDraftId))
      }
    }

    "go to SettlorsNINoYesNoPage from SettlorDateOfBirthYesNoPage when user answers no" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          val answers = userAnswers.set(SettlorDateOfBirthYesNoPage, value = false).success.value
          navigator.nextPage(SettlorDateOfBirthYesNoPage, NormalMode, fakeDraftId)(answers)
            .mustBe(routes.SettlorsNINoYesNoController.onPageLoad(NormalMode, fakeDraftId))
      }
    }
    "go to SettlorsLastKnownAddressYesNoPage from SettlorsNINoYesNoPage when user answers no" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          val answers = userAnswers.set(SettlorsNINoYesNoPage, value = false).success.value
          navigator.nextPage(SettlorsNINoYesNoPage, NormalMode, fakeDraftId)(answers)
            .mustBe(routes.SettlorsLastKnownAddressYesNoController.onPageLoad(NormalMode, fakeDraftId))
      }
    }

    "go to DeceasedSettlorAnswerPage from SettlorsLastKnownAddressYesNoPage when user answers no" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          val answers = userAnswers.set(SettlorsLastKnownAddressYesNoPage, value = false).success.value
          navigator.nextPage(SettlorsLastKnownAddressYesNoPage, NormalMode, fakeDraftId)(answers)
            .mustBe(routes.DeceasedSettlorAnswerController.onPageLoad(fakeDraftId))
      }
    }
    "go to WasSettlorsAddressUKYesNoPage from SettlorsLastKnownAddressYesNoPage when user answers yes" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          val answers = userAnswers.set(SettlorsLastKnownAddressYesNoPage, value = true).success.value
          navigator.nextPage(SettlorsLastKnownAddressYesNoPage, NormalMode, fakeDraftId)(answers)
            .mustBe(routes.WasSettlorsAddressUKYesNoController.onPageLoad(NormalMode, fakeDraftId))
      }
    }
    "go to SettlorsUKAddressPage from WasSettlorsAddressUKYesNoPage when user answers yes" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          val answers = userAnswers.set(WasSettlorsAddressUKYesNoPage, value = true).success.value
          navigator.nextPage(WasSettlorsAddressUKYesNoPage, NormalMode, fakeDraftId)(answers)
            .mustBe(routes.SettlorsUKAddressController.onPageLoad(NormalMode, fakeDraftId))
      }
    }
    "go to SettlorsInternationalAddressPage from WasSettlorsAddressUKYesNoPage when user answers no" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          val answers = userAnswers.set(WasSettlorsAddressUKYesNoPage, value = false).success.value
          navigator.nextPage(WasSettlorsAddressUKYesNoPage, NormalMode, fakeDraftId)(answers)
            .mustBe(routes.SettlorsInternationalAddressController.onPageLoad(NormalMode, fakeDraftId))
      }
    }
    "go to DeceasedSettlorAnswerPage from SettlorsInternationalAddressPage" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          navigator.nextPage(SettlorsInternationalAddressPage, NormalMode, fakeDraftId)(userAnswers)
            .mustBe(routes.DeceasedSettlorAnswerController.onPageLoad(fakeDraftId))
      }
    }
    "go to DeceasedSettlorAnswerPage from SettlorsUKAddressPage" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          navigator.nextPage(SettlorsUKAddressPage, NormalMode, fakeDraftId)(userAnswers)
            .mustBe(routes.DeceasedSettlorAnswerController.onPageLoad(fakeDraftId))
      }
    }
    "go to TaskList from DeceasedSettlorAnswerPage" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          navigator.nextPage(DeceasedSettlorAnswerPage, NormalMode, fakeDraftId)(userAnswers)
            .mustBe(routes.TaskListController.onPageLoad(fakeDraftId))
      }
    }
  }
}
