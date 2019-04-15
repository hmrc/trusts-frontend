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

import java.time.LocalDate

import models.{FullName, UKAddress, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours

class SetupAfterSettlorDiedPageSpec extends PageBehaviours {

  "SetupAfterSettlorDiedPage" must {

    beRetrievable[Boolean](SetupAfterSettlorDiedPage)

    beSettable[Boolean](SetupAfterSettlorDiedPage)

    beRemovable[Boolean](SetupAfterSettlorDiedPage)
  }

  "Relevant Data and NINO are removed when SetupAfterSettlorDiedPage set to false" in {
    forAll(arbitrary[UserAnswers], arbitrary[String]) {
      (initial, str) =>
        val answers: UserAnswers = initial.set(SettlorsNamePage, FullName(str,None, str)).success.value
          .set(SettlorDateOfDeathYesNoPage, true).success.value
          .set(SettlorDateOfDeathPage, LocalDate.now).success.value
          .set(SettlorDateOfBirthYesNoPage, true).success.value
          .set(SettlorsDateOfBirthPage, LocalDate.now.minusDays(10)).success.value
          .set(SettlorsNINoYesNoPage, true).success.value
          .set(SettlorNationalInsuranceNumberPage, str).success.value
        val result = answers.set(SetupAfterSettlorDiedPage, false).success.value

        result.get(SettlorsNamePage) mustNot be(defined)
        result.get(SettlorDateOfDeathYesNoPage) mustNot be(defined)
        result.get(SettlorDateOfDeathPage) mustNot be(defined)
        result.get(SettlorDateOfBirthYesNoPage) mustNot be(defined)
        result.get(SettlorsDateOfBirthPage) mustNot be(defined)
        result.get(SettlorsNINoYesNoPage) mustNot be(defined)
        result.get(SettlorNationalInsuranceNumberPage) mustNot be(defined)

    }
  }

  "Relevant Data and Addresses are removed when SetupAfterSettlorDiedPage set to false" in {
    forAll(arbitrary[UserAnswers], arbitrary[String]) {
      (initial, str) =>
        val answers: UserAnswers = initial.set(SettlorsNamePage, FullName(str,None, str)).success.value
          .set(SettlorDateOfDeathYesNoPage, true).success.value
          .set(SettlorDateOfDeathPage, LocalDate.now).success.value
          .set(SettlorDateOfBirthYesNoPage, true).success.value
          .set(SettlorsDateOfBirthPage, LocalDate.now.minusDays(10)).success.value
          .set(SettlorsNINoYesNoPage, false).success.value
          .set(SettlorsLastKnownAddressYesNoPage, true).success.value
          .set(WasSettlorsAddressUKYesNoPage, true).success.value
          .set(SettlorsUKAddressPage, UKAddress(str, Some(str), Some(str), str, str)).success.value
        val result = answers.set(SetupAfterSettlorDiedPage, false).success.value

        result.get(SettlorsNamePage) mustNot be(defined)
        result.get(SettlorDateOfDeathYesNoPage) mustNot be(defined)
        result.get(SettlorDateOfDeathPage) mustNot be(defined)
        result.get(SettlorDateOfBirthYesNoPage) mustNot be(defined)
        result.get(SettlorsDateOfBirthPage) mustNot be(defined)
        result.get(SettlorsNINoYesNoPage) mustNot be(defined)
        result.get(SettlorsLastKnownAddressYesNoPage) mustNot be(defined)
        result.get(WasSettlorsAddressUKYesNoPage) mustNot be(defined)
        result.get(SettlorsUKAddressPage) mustNot be(defined)

    }
  }
}
