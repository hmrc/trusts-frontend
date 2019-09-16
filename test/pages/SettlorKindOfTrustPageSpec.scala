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

import models.{SettlorKindOfTrust, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours

class SettlorKindOfTrustPageSpec extends PageBehaviours {

  "SettlorKindOfTrustPage" must {

    beRetrievable[SettlorKindOfTrust](SettlorKindOfTrustPage)

    beSettable[SettlorKindOfTrust](SettlorKindOfTrustPage)

    beRemovable[SettlorKindOfTrust](SettlorKindOfTrustPage)
  }

  "for a Lifetime trust remove holdover relief when changing type of trust" in {
    forAll(arbitrary[UserAnswers], arbitrary[String]) {
      (initial, str) =>
        val answers: UserAnswers = initial
          .set(SetupAfterSettlorDiedPage, false).success.value
          .set(SettlorKindOfTrustPage, SettlorKindOfTrust.Lifetime).success.value
          .set(SettlorHandoverReliefYesNoPage, true).success.value

        val result = answers.set(SettlorKindOfTrustPage, SettlorKindOfTrust.Building).success.value

        result.get(SettlorHandoverReliefYesNoPage) mustNot be(defined)
    }
  }
}
