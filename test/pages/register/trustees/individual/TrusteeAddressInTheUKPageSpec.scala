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

package pages.register.trustees.individual

import models.core.UserAnswers
import models.core.pages.UKAddress
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours

class TrusteeAddressInTheUKPageSpec extends PageBehaviours {

  "TrusteeLiveInTheUKPage" must {

    beRetrievable[Boolean](TrusteeAddressInTheUKPage(0))

    beSettable[Boolean](TrusteeAddressInTheUKPage(0))

    beRemovable[Boolean](TrusteeAddressInTheUKPage(0))
  }


  "remove TrusteesUkAddressPage when TrusteeLiveInTheUKPage is set to false" in {
    val index = 0
    forAll(arbitrary[UserAnswers], arbitrary[String]) {
      (initial, str) =>
        val answers: UserAnswers = initial.set(TrusteesUkAddressPage(index),UKAddress(str, str, Some(str), Some(str), str) ).success.value
        val result: UserAnswers = answers.set(TrusteeAddressInTheUKPage(index), false).success.value

        result.get(TrusteesUkAddressPage(index)) mustNot be(defined)
    }
  }
}
