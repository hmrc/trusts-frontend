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
import pages.deceased_settlor.{SettlorNationalInsuranceNumberPage, SettlorsLastKnownAddressYesNoPage, SettlorsNINoYesNoPage, SettlorsUKAddressPage}

class SettlorsNINoYesNoPageSpec extends PageBehaviours {

  "SettlorsNINoYesNoPage" must {

    beRetrievable[Boolean](SettlorsNINoYesNoPage)

    beSettable[Boolean](SettlorsNINoYesNoPage)

    beRemovable[Boolean](SettlorsNINoYesNoPage)
  }

  "remove SettlorNinoPage when SettlorsNINoYesNoPage is set to false" in {
    forAll(arbitrary[UserAnswers], arbitrary[String]) {
      (initial, str) =>
        val answers: UserAnswers = initial.set(SettlorNationalInsuranceNumberPage, str).success.value
        val result = answers.set(SettlorsNINoYesNoPage, false).success.value

        result.get(SettlorNationalInsuranceNumberPage) mustNot be(defined)
    }
  }

  "remove relevant Data when SettlorsNINoYesNoPage is set to true" in {
    forAll(arbitrary[UserAnswers], arbitrary[String]) {
      (initial, str) =>
        val answers: UserAnswers = initial.set(SettlorsLastKnownAddressYesNoPage, true).success.value
          .set(WasSettlorsAddressUKYesNoPage, true).success.value
        .set(SettlorsUKAddressPage, UKAddress(str, Some(str), Some(str), str, str)).success.value

        val result = answers.set(SettlorsNINoYesNoPage, true).success.value

        result.get(SettlorsLastKnownAddressYesNoPage) mustNot be(defined)
        result.get(WasSettlorsAddressUKYesNoPage) mustNot be(defined)
        result.get(SettlorsUKAddressPage) mustNot be(defined)
    }
  }
}
