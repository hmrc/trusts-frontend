/*
 * Copyright 2021 HM Revenue & Customs
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

package pages.register.suitability

import models.core.UserAnswers
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours

class TaxLiabilityInCurrentTaxYearYesNoPageSpec extends PageBehaviours {

  "TaxLiabilityInCurrentTaxYearYesNo Page" must {

    beRetrievable[Boolean](TaxLiabilityInCurrentTaxYearYesNoPage)

    beSettable[Boolean](TaxLiabilityInCurrentTaxYearYesNoPage)

    beRemovable[Boolean](TaxLiabilityInCurrentTaxYearYesNoPage)

    "implement cleanup logic" when {

      "YES selected" in {
        forAll(arbitrary[UserAnswers], arbitrary[Boolean]) {
          (initial, bool) =>
            val answers: UserAnswers = initial.set(UndeclaredTaxLiabilityYesNoPage, bool).success.value
            val result = answers.set(TaxLiabilityInCurrentTaxYearYesNoPage, true).success.value

            result.get(UndeclaredTaxLiabilityYesNoPage) mustNot be(defined)
        }
      }
    }
  }
}
