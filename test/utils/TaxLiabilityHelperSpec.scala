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

package utils

import base.RegistrationSpecBase

import java.time.LocalDate

class TaxLiabilityHelperSpec extends RegistrationSpecBase {

  "TaxLiabilityHelper" when {

    ".showTaxLiability" must {

      val dateBeforeStartDateOfCurrentTaxYear: LocalDate = LocalDate.parse("2000-01-01")
      val dateAfterStartDateOfCurrentTaxYear: LocalDate = LocalDate.now()

      "return false" when {

        "non-taxable trust" in {

          val result = TaxLiabilityHelper.showTaxLiability(Some(dateBeforeStartDateOfCurrentTaxYear), isTaxable = false, isExistingTrust = false)
          result mustBe false
        }

        "no trust setup date" in {

          val result = TaxLiabilityHelper.showTaxLiability(None, isTaxable = true, isExistingTrust = false)
          result mustBe false
        }

        "an existing trust" in {

          val result = TaxLiabilityHelper.showTaxLiability(Some(dateBeforeStartDateOfCurrentTaxYear), isTaxable = true, isExistingTrust = true)
          result mustBe false
        }

        "start date after current tax year start date" in {

          val result = TaxLiabilityHelper.showTaxLiability(Some(dateAfterStartDateOfCurrentTaxYear), isTaxable = true, isExistingTrust = false)
          result mustBe false
        }
      }

      "return true" when {
        "new, taxable trust with start date before current tax year start date" in {

          val result = TaxLiabilityHelper.showTaxLiability(Some(dateBeforeStartDateOfCurrentTaxYear), isTaxable = true, isExistingTrust = false)
          result mustBe true
        }
      }
    }
  }
}
