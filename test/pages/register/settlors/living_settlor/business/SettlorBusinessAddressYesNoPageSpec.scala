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

package pages.register.settlors.living_settlor.business

import models.core.UserAnswers
import models.core.pages.{InternationalAddress, UKAddress}
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours

class SettlorBusinessAddressYesNoPageSpec extends PageBehaviours {

  "SettlorBusinessAddressYesNoPage" must {

    beRetrievable[Boolean](SettlorBusinessAddressYesNoPage(0))

    beSettable[Boolean](SettlorBusinessAddressYesNoPage(0))

    beRemovable[Boolean](SettlorBusinessAddressYesNoPage(0))

    "remove relevant data" when {

      val page = SettlorBusinessAddressYesNoPage(0)

      "set to false" in {
        forAll(arbitrary[UserAnswers]) {
          initial =>
            val answers: UserAnswers = initial.set(page, true).success.value
              .set(SettlorBusinessAddressUKYesNoPage(0), true).success.value
              .set(SettlorBusinessAddressInternationalPage(0), InternationalAddress("line1", "line2", None, "France")).success.value
              .set(SettlorBusinessAddressUKPage(0), UKAddress("line1", "line2", None, None, "NE11NE")).success.value

            val result = answers.set(page, false).success.value

            result.get(SettlorBusinessAddressUKYesNoPage(0)) must not be defined
            result.get(SettlorBusinessAddressInternationalPage(0)) must not be defined
            result.get(SettlorBusinessAddressUKPage(0)) must not be defined
        }
      }

    }
  }
}
