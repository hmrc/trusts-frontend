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

package pages.register.asset.property_or_land

import models.core.UserAnswers
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours

class TrustOwnAllThePropertyOrLandPageSpec extends PageBehaviours {

  "TrustOwnAllThePropertyOrLandPage" must {

    beRetrievable[Boolean](TrustOwnAllThePropertyOrLandPage(0))

    beSettable[Boolean](TrustOwnAllThePropertyOrLandPage(0))

    beRemovable[Boolean](TrustOwnAllThePropertyOrLandPage(0))

    "remove relevant data" when {

      val page = TrustOwnAllThePropertyOrLandPage(0)

      "set to true" in {
        forAll(arbitrary[UserAnswers]) {
          initial =>
            val answers: UserAnswers = initial.set(page, false).success.value
              .set(PropertyLandValueTrustPage(0), "100").success.value

            val result = answers.set(page, true).success.value

            result.get(PropertyLandValueTrustPage(0)) must not be defined
        }
      }

    }
  }
}
