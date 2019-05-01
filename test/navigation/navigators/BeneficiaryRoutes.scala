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


trait BeneficiaryRoutes {
  self: PropertyChecks with Generators with SpecBase =>


  def beneficiaryRoutes()(implicit navigator: Navigator) = {
    val indexForBeneficiary = 0

    "go to IndividualBeneficiaryDateOfBirthYesNoPage from IndividualBeneficiaryNamePage" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          navigator.nextPage(IndividualBeneficiaryNamePage(indexForBeneficiary), NormalMode)(userAnswers)
            .mustBe(routes.IndividualBeneficiaryDateOfBirthYesNoController.onPageLoad(NormalMode, indexForBeneficiary))
      }
    }

    "go to IndividualBeneficiaryDateOfBirthPage from IndividualBeneficiaryDateOfBirthYesNoPage when user answers yes" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          val answers = userAnswers.set(IndividualBeneficiaryDateOfBirthYesNoPage(indexForBeneficiary), value = true).success.value
          navigator.nextPage(IndividualBeneficiaryDateOfBirthYesNoPage(indexForBeneficiary), NormalMode)(answers)
            .mustBe(routes.IndividualBeneficiaryDateOfBirthController.onPageLoad(NormalMode, indexForBeneficiary))
      }
    }

    "go to IndividualBeneficiaryIncomeYesNoPage from IndividualBeneficiaryDateOfBirthYesNoPage when user answers no" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          val answers = userAnswers.set(IndividualBeneficiaryDateOfBirthYesNoPage(indexForBeneficiary), value = false).success.value
          navigator.nextPage(IndividualBeneficiaryDateOfBirthYesNoPage(indexForBeneficiary), NormalMode)(answers)
            .mustBe(routes.IndividualBeneficiaryIncomeYesNoController.onPageLoad(NormalMode, indexForBeneficiary))
      }
    }

    "go to IndividualBeneficiaryIncomeYesNoPage from IndividualBeneficiaryDateOfBirthPage" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          navigator.nextPage(IndividualBeneficiaryDateOfBirthPage(indexForBeneficiary), NormalMode)(userAnswers)
            .mustBe(routes.IndividualBeneficiaryIncomeYesNoController.onPageLoad(NormalMode, indexForBeneficiary))
      }
    }

    "go to IndividualBeneficiaryIncomePage from IndividualBeneficiaryIncomeYesNoPage when user answers no" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          val answers = userAnswers.set(IndividualBeneficiaryIncomeYesNoPage(indexForBeneficiary), value = false).success.value
          navigator.nextPage(IndividualBeneficiaryIncomeYesNoPage(indexForBeneficiary), NormalMode)(answers)
            .mustBe(routes.IndividualBeneficiaryIncomeController.onPageLoad(NormalMode, indexForBeneficiary))
      }
    }

    "go to IndividualBeneficiaryNationalInsuranceYesNoPage from IndividualBeneficiaryIncomeYesNoPage when user answers yes" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          val answers = userAnswers.set(IndividualBeneficiaryIncomeYesNoPage(indexForBeneficiary), value = true).success.value
          navigator.nextPage(IndividualBeneficiaryIncomeYesNoPage(indexForBeneficiary), NormalMode)(answers)
            .mustBe(routes.IndividualBeneficiaryNationalInsuranceYesNoController.onPageLoad(NormalMode, indexForBeneficiary))
      }
    }

    "go to IndividualBeneficiaryNationalInsuranceYesNoPage from IndividualBeneficiaryIncomePage" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          navigator.nextPage(IndividualBeneficiaryIncomePage(indexForBeneficiary), NormalMode)(userAnswers)
            .mustBe(routes.IndividualBeneficiaryNationalInsuranceYesNoController.onPageLoad(NormalMode, indexForBeneficiary))
      }
    }

    "go to IndividualBeneficiaryAddressYesNoPage from IndividualBeneficiaryNationalInsuranceYesNoPage when user answers no" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          val answers = userAnswers.set(IndividualBeneficiaryNationalInsuranceYesNoPage(indexForBeneficiary), value = false).success.value
          navigator.nextPage(IndividualBeneficiaryNationalInsuranceYesNoPage(indexForBeneficiary), NormalMode)(answers)
            .mustBe(routes.IndividualBeneficiaryAddressYesNoController.onPageLoad(NormalMode, indexForBeneficiary))
      }
    }

    "go to IndividualBeneficiaryNationalInsuranceNumberPage from IndividualBeneficiaryNationalInsuranceYesNoPage when user answers yes" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          val answers = userAnswers.set(IndividualBeneficiaryNationalInsuranceYesNoPage(indexForBeneficiary), value = true).success.value
          navigator.nextPage(IndividualBeneficiaryNationalInsuranceYesNoPage(indexForBeneficiary), NormalMode)(answers)
            .mustBe(routes.IndividualBeneficiaryNationalInsuranceNumberController.onPageLoad(NormalMode, indexForBeneficiary))
      }
    }

    "go to IndividualBeneficiaryVulnerableYesNoPage from IndividualBeneficiaryNationalInsuranceNumberPage" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          navigator.nextPage(IndividualBeneficiaryNationalInsuranceNumberPage(indexForBeneficiary), NormalMode)(userAnswers)
            .mustBe(routes.IndividualBeneficiaryVulnerableYesNoController.onPageLoad(NormalMode, indexForBeneficiary))
      }
    }

    "go to IndividualBeneficiaryAddressUKPage from IndividualBeneficiaryAddressYesNoPage when user answers yes" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          val answers = userAnswers.set(IndividualBeneficiaryAddressYesNoPage(indexForBeneficiary), value = true).success.value
          navigator.nextPage(IndividualBeneficiaryAddressYesNoPage(indexForBeneficiary), NormalMode)(answers)
            .mustBe(routes.IndividualBeneficiaryAddressUKController.onPageLoad(NormalMode))
      }
    }

    "go to IndividualBeneficiaryVulnerableYesNoPage from IndividualBeneficiaryAddressYesNoPage when user answers no" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          val answers = userAnswers.set(IndividualBeneficiaryAddressYesNoPage(indexForBeneficiary), value = false).success.value
          navigator.nextPage(IndividualBeneficiaryAddressYesNoPage(indexForBeneficiary), NormalMode)(answers)
            .mustBe(routes.IndividualBeneficiaryVulnerableYesNoController.onPageLoad(NormalMode, indexForBeneficiary))
      }
    }

    "go to IndividualBeneficiaryVulnerableYesNoPage from IndividualBeneficiaryAddressUKPage" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          navigator.nextPage(IndividualBeneficiaryAddressUKPage, NormalMode)(userAnswers)
            .mustBe(routes.IndividualBeneficiaryVulnerableYesNoController.onPageLoad(NormalMode, indexForBeneficiary))
      }
    }

    "go to IndividualBeneficiaryAnswersPage from IndividualBeneficiaryVulnerableYesNoPage" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          navigator.nextPage(IndividualBeneficiaryVulnerableYesNoPage(indexForBeneficiary), NormalMode)(userAnswers)
            .mustBe(routes.IndividualBeneficiaryAnswersController.onPageLoad(indexForBeneficiary))
      }
    }

    "go to AddABeneficiaryPage from IndividualBeneficiaryAnswersPage" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          navigator.nextPage(IndividualBeneficiaryAnswersPage, NormalMode)(userAnswers)
            .mustBe(routes.AddABeneficiaryController.onPageLoad(NormalMode))
      }
    }

  }

}
