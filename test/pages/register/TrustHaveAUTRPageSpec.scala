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

package pages.register

import models.core.UserAnswers
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours

class TrustHaveAUTRPageSpec extends PageBehaviours {

  "TrustHaveAUTRPage" must {

    beRetrievable[Boolean](TrustHaveAUTRPage)

    beSettable[Boolean](TrustHaveAUTRPage)

    beRemovable[Boolean](TrustHaveAUTRPage)
  }

  "remove relevant data when TrustHaveAUTRPage is set to false" in {

    forAll(arbitrary[UserAnswers], arbitrary[String]) {
      (initial, str) =>

        val answers = initial.set(WhatIsTheUTRPage, str).success.value
          .set(TrustNamePage, str).success.value
          .set(PostcodeForTheTrustPage, str).success.value

        val result = answers.set(TrustHaveAUTRPage, false).success.value

        result.get(WhatIsTheUTRPage) mustNot be (defined)
        result.get(TrustNamePage) mustNot be (defined)
        result.get(PostcodeForTheTrustPage) mustNot be (defined)
    }

  }

}
