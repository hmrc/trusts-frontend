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

package navigation.navigators.registration

import base.RegistrationSpecBase
import controllers.register.settlors.deceased_settlor.routes
import generators.Generators
import models.NormalMode
import models.core.UserAnswers
import models.registration.pages.DeedOfVariation.{ReplaceAbsolute, ReplacedWill}
import navigation.Navigator
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.register.settlors.SetUpAfterSettlorDiedYesNoPage
import pages.register.settlors.deceased_settlor._
import pages.register.settlors.living_settlor.trust_type.{HowDeedOfVariationCreatedPage, SetUpInAdditionToWillTrustYesNoPage}

trait DeceasedSettlorRoutes {
  self: ScalaCheckPropertyChecks with Generators with RegistrationSpecBase =>

  def deceasedSettlorRoutes()(implicit navigator: Navigator) = {

    "go to SettlorIndividualOrBusinessPage from from HowDeedOfVariationCreatedPage" when {
      "selected ReplacedWill" in {
        val index = 0

        forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            val answers = userAnswers.set(HowDeedOfVariationCreatedPage, ReplacedWill).success.value

            navigator.nextPage(HowDeedOfVariationCreatedPage, NormalMode, fakeDraftId)(answers)
              .mustBe(controllers.register.settlors.living_settlor.routes.SettlorIndividualOrBusinessController.onPageLoad(NormalMode,
                index, fakeDraftId))
        }
      }
      "selected ReplaceAbsolute" in {
        val index = 0

        forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            val answers = userAnswers.set(HowDeedOfVariationCreatedPage, ReplaceAbsolute).success.value

            navigator.nextPage(HowDeedOfVariationCreatedPage, NormalMode, fakeDraftId)(answers)
              .mustBe(controllers.register.settlors.living_settlor.routes.SettlorIndividualOrBusinessController.onPageLoad(NormalMode,
                index, fakeDraftId))
        }
      }
    }

    "go to WhatKindOfTrustPage from SetUpAfterSettlorDiedPage when user answers no" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          val answers = userAnswers.set(SetUpAfterSettlorDiedYesNoPage, value = false).success.value

          navigator.nextPage(SetUpAfterSettlorDiedYesNoPage, NormalMode, fakeDraftId)(answers)
            .mustBe(controllers.register.settlors.living_settlor.routes.KindOfTrustController.onPageLoad(NormalMode, fakeDraftId))
      }
    }

    "go to SettlorsNamePage from SetUpAfterSettlorDiedPage when user answers yes" in {
      forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            val answers = userAnswers.set(SetUpAfterSettlorDiedYesNoPage, value = true).success.value

            navigator.nextPage(SetUpAfterSettlorDiedYesNoPage, NormalMode, fakeDraftId)(answers)
              .mustBe(routes.SettlorsNameController.onPageLoad(NormalMode, fakeDraftId))
        }
    }

    "go to SettlorsNamePage from SetUpInAdditionToWillTrustYesNoPage when user answers yes" in {
      forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            val answers = userAnswers.set(SetUpInAdditionToWillTrustYesNoPage, value = true).success.value

            navigator.nextPage(SetUpInAdditionToWillTrustYesNoPage, NormalMode, fakeDraftId)(answers)
              .mustBe(routes.SettlorsNameController.onPageLoad(NormalMode, fakeDraftId))
        }
    }

    "go to HowDeedOfVariationCreatedPage from SetUpInAdditionToWillTrustYesNoPage when user answers yes" in {
      forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            val answers = userAnswers.set(SetUpInAdditionToWillTrustYesNoPage, value = false).success.value

            navigator.nextPage(SetUpInAdditionToWillTrustYesNoPage, NormalMode, fakeDraftId)(answers)
              .mustBe(controllers.register.settlors.routes.HowDeedOfVariationCreatedController.onPageLoad(NormalMode, fakeDraftId))
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

          val answers = userAnswers.set(SettlorsNationalInsuranceYesNoPage, true).success.value

          navigator.nextPage(SettlorsNationalInsuranceYesNoPage, NormalMode, fakeDraftId)(answers)
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
          val answers = userAnswers.set(SettlorsNationalInsuranceYesNoPage, value = false).success.value
          navigator.nextPage(SettlorsNationalInsuranceYesNoPage, NormalMode, fakeDraftId)(answers)
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
            .mustBe(controllers.register.routes.TaskListController.onPageLoad(fakeDraftId))
      }
    }
  }
}
