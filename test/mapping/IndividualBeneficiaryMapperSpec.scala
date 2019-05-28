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

package mapping

import java.time.LocalDate

import base.SpecBaseHelpers
import generators.Generators
import models.FullName
import org.scalatest.{FreeSpec, MustMatchers, OptionValues}
import pages.{IndividualBeneficiaryDateOfBirthPage, IndividualBeneficiaryDateOfBirthYesNoPage, IndividualBeneficiaryIncomePage, IndividualBeneficiaryIncomeYesNoPage, IndividualBeneficiaryNamePage, IndividualBeneficiaryNationalInsuranceNumberPage, IndividualBeneficiaryNationalInsuranceYesNoPage, IndividualBeneficiaryVulnerableYesNoPage}

class IndividualBeneficiaryMapperSpec extends FreeSpec with MustMatchers
  with OptionValues with Generators with SpecBaseHelpers {

  val individualBeneficiariesMapper : Mapping[IndividualDetailsType] = injector.instanceOf[IndividualBeneficiaryMapper]

  "IndividualBeneficiariesMapper" - {

    "when user answers is empty" - {

      "must not be able to create IndividualDetailsType" in {

        val userAnswers = emptyUserAnswers

        individualBeneficiariesMapper.build(userAnswers) mustNot be(defined)
      }
    }

    "when user answers is not empty" - {

      "must be able to create IndividualDetailsType" in {
        val index = 0
        val date = LocalDate.of(2010, 10, 10)
        val userAnswers =
          emptyUserAnswers
            .set(IndividualBeneficiaryNamePage(index), FullName("first name", None, "last name")).success.value
            .set(IndividualBeneficiaryDateOfBirthYesNoPage(index),true).success.value
            .set(IndividualBeneficiaryDateOfBirthPage(index),date).success.value
            .set(IndividualBeneficiaryIncomeYesNoPage(index),false).success.value
            .set(IndividualBeneficiaryIncomePage(index),"100").success.value
            .set(IndividualBeneficiaryNationalInsuranceYesNoPage(index),true).success.value
            .set(IndividualBeneficiaryNationalInsuranceNumberPage(index),"AB123456C").success.value
            .set(IndividualBeneficiaryVulnerableYesNoPage(index),true).success.value

        individualBeneficiariesMapper.build(userAnswers).value mustBe IndividualDetailsType(
          name = NameType("first name",None,"last name"),
          dateOfBirth = Some(date),
          vulnerableBeneficiary = true,
          beneficiaryType = None,
          beneficiaryDiscretion = Some(false),
          beneficiaryShareOfIncome = Some("100"),
          identification = Some(IdentificationType(nino = Some("AB123456C"), None, None))
        )
      }
    }
  }


}
