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

package pages

import models.{UKAddress, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours

class SettlorsLastKnownAddressYesNoPageSpec extends PageBehaviours {

  "SettlorsLastKnownAddressYesNoPage" must {

    beRetrievable[Boolean](SettlorsLastKnownAddressYesNoPage)

    beSettable[Boolean](SettlorsLastKnownAddressYesNoPage)

    beRemovable[Boolean](SettlorsLastKnownAddressYesNoPage)
  }

  "remove WasSettlorsAddressUKYesNoPage, SettlorsAddressUK, SettlorsInternationalAddressPage when SettlorsLastKnownAddressYesNoPage is set to false" in {
    forAll(arbitrary[UserAnswers], arbitrary[String]) {
      (initial, str) =>
        val answers: UserAnswers = initial.set(SettlorsUKAddressPage, UKAddress(str, Some(str), Some(str), str, str)).success.value
          .set(WasSettlorsAddressUKYesNoPage, true).success.value
        val result = answers.set(SettlorsLastKnownAddressYesNoPage, false).success.value

        result.get(SettlorsUKAddressPage) mustNot be(defined)
        result.get(SettlorsInternationalAddressPage) mustNot be(defined)
        result.get(WasSettlorsAddressUKYesNoPage) mustNot be(defined)
    }
  }
}