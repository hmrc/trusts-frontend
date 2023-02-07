/*
 * Copyright 2023 HM Revenue & Customs
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

package models

import base.RegistrationSpecBase
import pages.register.TrustHaveAUTRPage
import pages.register.suitability.TrustTaxableYesNoPage

class UserAnswersSpec extends RegistrationSpecBase {

  "UserAnswers" when {

    ".isTaxable" must {

      "return true" when {

        "TrustTaxableYesNoPage is true" in {
          val userAnswers = emptyUserAnswers
            .set(TrustTaxableYesNoPage, true).success.value

          userAnswers.isTaxable mustEqual true
        }

        "TrustTaxableYesNoPage is undefined" in {
          val userAnswers = emptyUserAnswers

          userAnswers.isTaxable mustEqual true
        }
      }

      "return false" when {
        "TrustTaxableYesNoPage is false" in {
          val userAnswers = emptyUserAnswers
            .set(TrustTaxableYesNoPage, false).success.value

          userAnswers.isTaxable mustEqual false
        }
      }
    }

    ".isExistingTrust" must {

      "return true" when {
        "TrustHaveAUTRPage is true" in {
          val userAnswers = emptyUserAnswers
            .set(TrustHaveAUTRPage, true).success.value

          userAnswers.isExistingTrust mustEqual true
        }
      }

      "return false" when {

        "TrustHaveAUTRPage is false" in {
          val userAnswers = emptyUserAnswers
            .set(TrustHaveAUTRPage, false).success.value

          userAnswers.isExistingTrust mustEqual false
        }

        "TrustHaveAUTRPage is undefined" in {
          val userAnswers = emptyUserAnswers

          userAnswers.isExistingTrust mustEqual false
        }
      }
    }
  }
}
