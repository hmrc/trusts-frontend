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

package pages.trustees

import models.core.UserAnswers
import models.core.pages.UKAddress
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours

class IsThisLeadTrusteePageSpec extends PageBehaviours {

  "IsThisLeadTrusteePage" must {

    beRetrievable[Boolean](IsThisLeadTrusteePage(0))

    beSettable[Boolean](IsThisLeadTrusteePage(0))

    beRemovable[Boolean](IsThisLeadTrusteePage(0))
  }

  "remove relevant data when IsThisLeadTrusteePage is set to false" in {
    val index = 0
    forAll(arbitrary[UserAnswers], arbitrary[String]) {
      (initial, str) =>
        val answers: UserAnswers = initial
          .set(TrusteeAUKCitizenPage(index), true).success.value
          .set(TrusteesNinoPage(index), str).success.value
          .set(TrusteeLiveInTheUKPage(index), true).success.value
          .set(TrusteesUkAddressPage(index), UKAddress(str, str, None, None, str)).success.value
          .set(TelephoneNumberPage(index), str).success.value

        val result = answers.set(IsThisLeadTrusteePage(index), false).success.value

        result.get(TrusteeAUKCitizenPage(index)) mustNot be(defined)
        result.get(TrusteesNinoPage(index)) mustNot be(defined)
        result.get(TrusteeLiveInTheUKPage(index)) mustNot be(defined)
        result.get(TrusteesUkAddressPage(index)) mustNot be(defined)
        result.get(TelephoneNumberPage(index)) mustNot be(defined)
    }
  }


}
