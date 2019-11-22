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

import java.time.LocalDate

import models.core.UserAnswers
import models.core.pages.{InternationalAddress, UKAddress}
import models.registration.pages.PassportOrIdCardDetails
import models.{InternationalAddress, PassportOrIdCardDetails}
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours

class SettlorIndividualAddressYesNoPageSpec extends PageBehaviours {

  "SettlorIndividualAddressYesNoPage" must {

    beRetrievable[Boolean](SettlorIndividualAddressYesNoPage(0))

    beSettable[Boolean](SettlorIndividualAddressYesNoPage(0))

    beRemovable[Boolean](SettlorIndividualAddressYesNoPage(0))

    "remove relevant data" when {

      val page = SettlorIndividualAddressYesNoPage(0)

      "set to false" in {
        forAll(arbitrary[UserAnswers]) {
          initial =>
            val answers: UserAnswers = initial.set(page, true).success.value
              .set(SettlorIndividualAddressUKYesNoPage(0), true).success.value
              .set(SettlorIndividualAddressInternationalPage(0), InternationalAddress("line1", "line2", None, "France")).success.value
              .set(SettlorIndividualAddressUKPage(0), UKAddress("line1", "line2", None, None, "NE11NE")).success.value
              .set(SettlorIndividualPassportYesNoPage(0), true).success.value
              .set(SettlorIndividualPassportPage(0), PassportOrIdCardDetails("UK", "234567887", LocalDate.now())).success.value
              .set(SettlorIndividualIDCardYesNoPage(0), true).success.value
              .set(SettlorIndividualIDCardPage(0), PassportOrIdCardDetails("UK", "8765567", LocalDate.now())).success.value

            val result = answers.set(page, false).success.value

            result.get(SettlorIndividualAddressUKYesNoPage(0)) must not be defined
            result.get(SettlorIndividualAddressInternationalPage(0)) must not be defined
            result.get(SettlorIndividualAddressUKPage(0)) must not be defined
            result.get(SettlorIndividualPassportYesNoPage(0)) must not be defined
            result.get(SettlorIndividualPassportPage(0)) must not be defined
            result.get(SettlorIndividualIDCardYesNoPage(0)) must not be defined
            result.get(SettlorIndividualIDCardPage(0)) must not be defined
        }
      }

    }
  }
}
