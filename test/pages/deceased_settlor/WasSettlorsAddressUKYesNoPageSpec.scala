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

package pages.deceased_settlor

import models.{InternationalAddress, UKAddress, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours

class WasSettlorsAddressUKYesNoPageSpec extends PageBehaviours {

  "WasSettlorsAddressUKYesNoPage" must {

    beRetrievable[Boolean](WasSettlorsAddressUKYesNoPage)

    beSettable[Boolean](WasSettlorsAddressUKYesNoPage)

    beRemovable[Boolean](WasSettlorsAddressUKYesNoPage)
  }

  "remove DeceasedSettlorUkAddress when WasSettlorsAddressUKYesNoPage is set to false" in {
    forAll(arbitrary[UserAnswers], arbitrary[String]) {
      (initial, str) =>
        val answers : UserAnswers = initial.set(SettlorsUKAddressPage, UKAddress(str, str, Some(str), Some(str), str)).success.value
        val result = answers.set(WasSettlorsAddressUKYesNoPage, false).success.value

        result.get(SettlorsUKAddressPage) mustNot be (defined)
    }
  }

  "remove DeceasedSettlorInternationalAddress when WasSettlorsAddressUKYesNoPage is set to true" in {
    forAll(arbitrary[UserAnswers], arbitrary[String]) {
      (initial, str) =>
        val answers: UserAnswers = initial.set(SettlorsInternationalAddressPage, InternationalAddress(str, str,Some(str),Some(str),str)).success.value
        val result = answers.set(WasSettlorsAddressUKYesNoPage, true).success.value

        result.get(SettlorsInternationalAddressPage) mustNot be(defined)
    }
  }

}
