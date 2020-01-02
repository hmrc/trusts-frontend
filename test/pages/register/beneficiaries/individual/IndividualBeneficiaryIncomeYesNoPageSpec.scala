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

package pages.register.beneficiaries.individual

import models.core.UserAnswers
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours

class IndividualBeneficiaryIncomeYesNoPageSpec extends PageBehaviours {

  "IndividualBeneficiaryIncomeYesNoPage" must {

    beRetrievable[Boolean](IndividualBeneficiaryIncomeYesNoPage(0))

    beSettable[Boolean](IndividualBeneficiaryIncomeYesNoPage(0))

    beRemovable[Boolean](IndividualBeneficiaryIncomeYesNoPage(0))
  }


  "remove IndividualBeneficiaryIncome when IndividualBeneficiaryIncomeYesNoPage is set to true" in {
    forAll(arbitrary[UserAnswers]) {
      initial =>
        val answers: UserAnswers = initial.set(IndividualBeneficiaryIncomePage(0), "100").success.value
        val result = answers.set(IndividualBeneficiaryIncomeYesNoPage(0), true).success.value

        result.get(IndividualBeneficiaryIncomePage(0)) mustNot be(defined)
    }
  }
}
