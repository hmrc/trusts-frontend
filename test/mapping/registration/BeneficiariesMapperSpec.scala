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

package mapping.registration

import java.time.LocalDate

import base.SpecBaseHelpers
import generators.Generators
import mapping.Mapping
import models.core.pages.FullName
import org.scalatest.{FreeSpec, MustMatchers, OptionValues}
import pages._
import pages.register.beneficiaries.ClassBeneficiaryDescriptionPage
import pages.register.beneficiaries.individual.{IndividualBeneficiaryDateOfBirthPage, IndividualBeneficiaryDateOfBirthYesNoPage, IndividualBeneficiaryIncomePage, IndividualBeneficiaryIncomeYesNoPage, IndividualBeneficiaryNamePage, IndividualBeneficiaryNationalInsuranceNumberPage, IndividualBeneficiaryNationalInsuranceYesNoPage, IndividualBeneficiaryVulnerableYesNoPage}

class BeneficiariesMapperSpec extends FreeSpec with MustMatchers
  with OptionValues with Generators with SpecBaseHelpers {

  val beneficiariesMapper : Mapping[BeneficiaryType] = injector.instanceOf[BeneficiariesMapper]

  "BeneficiariesMapper" - {

    "when user answers is empty" - {

      "must not be able to create BeneficiaryType" in {

        val userAnswers = emptyUserAnswers

        beneficiariesMapper.build(userAnswers) mustNot be(defined)
      }
    }

    "when user answers is not empty" - {

      "must not be able to create BeneficiaryType when there is incomplete data" in {
        val index = 0
        val dateOfBirth = LocalDate.of(2010, 10, 10)

        val userAnswers = emptyUserAnswers
          .set(IndividualBeneficiaryNamePage(index), FullName("first name", None, "last name")).success.value
          .set(IndividualBeneficiaryDateOfBirthYesNoPage(index), true).success.value
          .set(IndividualBeneficiaryDateOfBirthPage(index), dateOfBirth).success.value
          .set(IndividualBeneficiaryIncomeYesNoPage(index), false).success.value
          .set(IndividualBeneficiaryIncomePage(index), "100").success.value

        beneficiariesMapper.build(userAnswers) mustNot be(defined)
      }

      "must be able to create BeneficiaryType when there is an individual beneficiary" in {

        val index = 0
        val dateOfBirth = LocalDate.of(2010, 10, 10)

        val userAnswers = emptyUserAnswers
          .set(IndividualBeneficiaryNamePage(index), FullName("first name", None, "last name")).success.value
          .set(IndividualBeneficiaryDateOfBirthYesNoPage(index), true).success.value
          .set(IndividualBeneficiaryDateOfBirthPage(index), dateOfBirth).success.value
          .set(IndividualBeneficiaryIncomeYesNoPage(index), false).success.value
          .set(IndividualBeneficiaryIncomePage(index), "100").success.value
          .set(IndividualBeneficiaryNationalInsuranceYesNoPage(index), true).success.value
          .set(IndividualBeneficiaryNationalInsuranceNumberPage(index), "AB123456C").success.value
          .set(IndividualBeneficiaryVulnerableYesNoPage(index), true).success.value

        val result = beneficiariesMapper.build(userAnswers).value

        result.individualDetails mustBe defined
        result.unidentified mustNot be(defined)
        result.charity mustNot be(defined)
        result.company mustNot be(defined)
        result.trust mustNot be(defined)
        result.large mustNot be(defined)
        result.other mustNot be(defined)
      }

      "must be able to create BeneficiaryType when there is an individual beneficiary and class of beneficiary" in {

        val index = 0
        val classOfBeneficiaryIndex = 0
        val dateOfBirth = LocalDate.of(2010, 10, 10)

        val userAnswers = emptyUserAnswers
          .set(IndividualBeneficiaryNamePage(index), FullName("first name", None, "last name")).success.value
          .set(IndividualBeneficiaryDateOfBirthYesNoPage(index), true).success.value
          .set(IndividualBeneficiaryDateOfBirthPage(index), dateOfBirth).success.value
          .set(IndividualBeneficiaryIncomeYesNoPage(index), false).success.value
          .set(IndividualBeneficiaryIncomePage(index), "100").success.value
          .set(IndividualBeneficiaryNationalInsuranceYesNoPage(index), true).success.value
          .set(IndividualBeneficiaryNationalInsuranceNumberPage(index), "AB123456C").success.value
          .set(IndividualBeneficiaryVulnerableYesNoPage(index), true).success.value
          .set(ClassBeneficiaryDescriptionPage(classOfBeneficiaryIndex), "class of ben 1").success.value

        val result = beneficiariesMapper.build(userAnswers).value

        result.individualDetails mustBe defined
        result.unidentified mustBe defined
        result.charity mustNot be(defined)
        result.company mustNot be(defined)
        result.trust mustNot be(defined)
        result.large mustNot be(defined)
        result.other mustNot be(defined)
      }

    }

  }

}
