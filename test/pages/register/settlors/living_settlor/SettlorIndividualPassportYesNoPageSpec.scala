/*
 * Copyright 2020 HM Revenue & Customs
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

package pages.register.settlors.living_settlor

import java.time.LocalDate

import models.core.UserAnswers
import models.registration.pages.PassportOrIdCardDetails
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours

class SettlorIndividualPassportYesNoPageSpec extends PageBehaviours {

  "SettlorIndividualPassportYesNoPage" must {

    beRetrievable[Boolean](SettlorIndividualPassportYesNoPage(0))

    beSettable[Boolean](SettlorIndividualPassportYesNoPage(0))

    beRemovable[Boolean](SettlorIndividualPassportYesNoPage(0))

    "remove relevant data" when {

      val page = SettlorIndividualPassportYesNoPage(0)

      "set to true" in {
        forAll(arbitrary[UserAnswers]) {
          initial =>
            val answers: UserAnswers = initial.set(page, false).success.value
              .set(SettlorIndividualIDCardYesNoPage(0), true).success.value
              .set(SettlorIndividualIDCardPage(0), PassportOrIdCardDetails("France", "98765546", LocalDate.now())).success.value

            val result = answers.set(page, true).success.value

            result.get(SettlorIndividualIDCardYesNoPage(0)) must not be defined
            result.get(SettlorIndividualIDCardPage(0)) must not be defined
        }
      }

      "set to false" in {
        forAll(arbitrary[UserAnswers]) {
          initial =>
            val answers: UserAnswers = initial.set(page, true).success.value
              .set(SettlorIndividualPassportPage(0), PassportOrIdCardDetails("France", "234323", LocalDate.now())).success.value

            val result = answers.set(page, false).success.value

            result.get(SettlorIndividualPassportPage(0)) must not be defined
        }
      }

    }
  }
}
