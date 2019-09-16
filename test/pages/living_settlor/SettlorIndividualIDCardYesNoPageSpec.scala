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

import models.{PassportOrIdCardDetails, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours

class SettlorIndividualIDCardYesNoPageSpec extends PageBehaviours {

  "SettlorIndividualIDCardYesNoPage" must {

    beRetrievable[Boolean](SettlorIndividualIDCardYesNoPage(0))

    beSettable[Boolean](SettlorIndividualIDCardYesNoPage(0))

    beRemovable[Boolean](SettlorIndividualIDCardYesNoPage(0))

    "remove relevant data" when {

      val page = SettlorIndividualIDCardYesNoPage(0)

      "set to false" in {
        forAll(arbitrary[UserAnswers]) {
          initial =>
            val answers: UserAnswers = initial.set(page, true).success.value
              .set(SettlorIndividualIDCardPage(0), PassportOrIdCardDetails("France", "234122", LocalDate.now())).success.value

            val result = answers.set(page, false).success.value

            result.get(SettlorIndividualIDCardPage(0)) must not be defined
        }
      }

    }
  }
}
