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

package pages.living_settlor

import models.{InternationalAddress, UKAddress, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours

class SettlorIndividualAddressUKYesNoPageSpec extends PageBehaviours {

  "SettlorIndividualAddressUKYesNoPage" must {

    beRetrievable[Boolean](SettlorIndividualAddressUKYesNoPage(0))

    beSettable[Boolean](SettlorIndividualAddressUKYesNoPage(0))

    beRemovable[Boolean](SettlorIndividualAddressUKYesNoPage(0))

    "remove relevant data" when {

      val page = SettlorIndividualAddressUKYesNoPage(0)

      "set to true" in {
        forAll(arbitrary[UserAnswers]) {
          initial =>
            val answers: UserAnswers = initial.set(page, false).success.value
              .set(SettlorIndividualAddressInternationalPage(0), InternationalAddress("line1", "line2", None, None, "France")).success.value

            val result = answers.set(page, true).success.value

            result.get(SettlorIndividualAddressInternationalPage(0)) must not be defined
        }
      }

      "set to false" in {
        forAll(arbitrary[UserAnswers]) {
          initial =>
            val answers: UserAnswers = initial.set(page, true).success.value
              .set(SettlorIndividualAddressUKPage(0), UKAddress("line1", None, None, "Town", "NE11NE")).success.value

            val result = answers.set(page, false).success.value

            result.get(SettlorIndividualAddressUKPage(0)) must not be defined
        }
      }

    }
  }
}
