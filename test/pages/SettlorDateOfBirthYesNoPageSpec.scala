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

import models.UserAnswers
import pages.behaviours.PageBehaviours
import org.scalacheck.Arbitrary.arbitrary

class SettlorDateOfBirthYesNoPageSpec extends PageBehaviours {

  "SettlorDateOfBirthYesNoPage" must {

    beRetrievable[Boolean](SettlorDateOfBirthYesNoPage)

    beSettable[Boolean](SettlorDateOfBirthYesNoPage)

    beRemovable[Boolean](SettlorDateOfBirthYesNoPage)
  }

  "remove SettlorDateOfBirth when SettlorDateOfBirthYesNoPage is set to false" in {
    forAll(arbitrary[UserAnswers], arbitrary[String]) {
      (initial, str) =>
        val answers: UserAnswers = initial.set(SettlorsDateOfBirthPage, LocalDate.now()).success.value
        val result: UserAnswers = answers.set(SettlorDateOfBirthYesNoPage, false).success.value

        result.get(SettlorsDateOfBirthPage) mustNot be (defined)
    }
  }
}
