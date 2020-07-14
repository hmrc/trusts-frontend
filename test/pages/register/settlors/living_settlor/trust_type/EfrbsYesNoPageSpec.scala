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

package pages.register.settlors.living_settlor.trust_type

import java.time.LocalDate

import models.core.UserAnswers
import models.core.pages.{InternationalAddress, UKAddress}
import models.registration.pages.PassportOrIdCardDetails
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours

class EfrbsYesNoPageSpec extends PageBehaviours {

  "EfrbsYesNoPage" must {

    beRetrievable[Boolean](EfrbsYesNoPage)

    beSettable[Boolean](EfrbsYesNoPage)

    beRemovable[Boolean](EfrbsYesNoPage)

    "remove relevant data" when {

      val page = EfrbsYesNoPage

      "set to false" in {
        forAll(arbitrary[UserAnswers]) {
          initial =>
            val answers: UserAnswers = initial.set(page, true).success.value
              .set(EfrbsYesNoPage, true).success.value
              .set(EfrbsStartDatePage, LocalDate.of(2000, 1, 1)).success.value

            val result = answers.set(page, false).success.value

            result.get(EfrbsStartDatePage) must not be defined
        }
      }

    }
  }
}
