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
            .mustBe(routes.IndividualBeneficiaryDateOfBirthYesNoController.onPageLoad(NormalMode))
      }
    }

    "go to IndividualBeneficiaryDateOfBirthPage from IndividualBeneficiaryDateOfBirthYesNoPage when user answers yes" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          val answers = userAnswers.set(IndividualBeneficiaryDateOfBirthYesNoPage, value = true).success.value
          navigator.nextPage(IndividualBeneficiaryDateOfBirthYesNoPage, NormalMode)(answers)
            .mustBe(routes.IndividualBeneficiaryDateOfBirthController.onPageLoad(NormalMode))
      }
    }

    "go to IndividualBeneficiaryIncomeYesNoPage from IndividualBeneficiaryDateOfBirthYesNoPage when user answers no" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          val answers = userAnswers.set(IndividualBeneficiaryDateOfBirthYesNoPage, value = false).success.value
          navigator.nextPage(IndividualBeneficiaryDateOfBirthYesNoPage, NormalMode)(answers)
            .mustBe(routes.IndividualBeneficiaryIncomeYesNoController.onPageLoad(NormalMode))
      }
    }

    "go to IndividualBeneficiaryIncomePage from IndividualBeneficiaryIncomeYesNoPage when user answers no" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          val answers = userAnswers.set(IndividualBeneficiaryIncomeYesNoPage, value = false).success.value
          navigator.nextPage(IndividualBeneficiaryIncomeYesNoPage, NormalMode)(answers)
            .mustBe(routes.IndividualBeneficiaryIncomeController.onPageLoad(NormalMode))
      }
    }

    "go to IndividualBeneficiaryNationalInsuranceYesNoPage from IndividualBeneficiaryIncomeYesNoPage when user answers yes" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          val answers = userAnswers.set(IndividualBeneficiaryIncomeYesNoPage, value = true).success.value
          navigator.nextPage(IndividualBeneficiaryIncomeYesNoPage, NormalMode)(answers)
            .mustBe(routes.IndividualBeneficiaryNationalInsuranceYesNoController.onPageLoad(NormalMode))
      }
    }

    "go to IndividualBeneficiaryNationalInsuranceYesNoPage from IndividualBeneficiaryIncomePage" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          navigator.nextPage(IndividualBeneficiaryIncomePage, NormalMode)(userAnswers)
            .mustBe(routes.IndividualBeneficiaryNationalInsuranceYesNoController.onPageLoad(NormalMode))
      }
    }

    "go to IndividualBeneficiaryAdressYesNoPage from IndividualBeneficiaryNationalInsuranceYesNoPage when user answers no" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          val answers = userAnswers.set(IndividualBeneficiaryIncomeYesNoPage, value = false).success.value
          navigator.nextPage(IndividualBeneficiaryNationalInsuranceYesNoPage, NormalMode)(answers)
            .mustBe(routes.IndividualBeneficiaryAdressYesNoController.onPageLoad(NormalMode))
      }
    }

  }

}
