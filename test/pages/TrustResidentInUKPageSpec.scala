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

import models.UserAnswers
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours

class TrustResidentInUKPageSpec extends PageBehaviours {

  "TrustResidentInUKPage" must {

    beRetrievable[Boolean](TrustResidentInUKPage)

    beSettable[Boolean](TrustResidentInUKPage)

    beRemovable[Boolean](TrustResidentInUKPage)
  }

  "remove EstablishedUnderScotsLaw, TrustResidentOffshore and TrustPreviouslyResident when TrustResidentInUK is set to false" in {

    forAll(arbitrary[UserAnswers], arbitrary[Boolean], arbitrary[String]) {
      (initial, bool, str) =>

        val answers = initial.set(EstablishedUnderScotsLawPage, bool).success.value
          .set(TrustResidentOffshorePage, true).success.value
          .set(TrustPreviouslyResidentPage, str).success.value

        val result = answers.set(TrustResidentInUKPage, false).success.value

        result.get(EstablishedUnderScotsLawPage) mustNot be (defined)
        result.get(TrustResidentOffshorePage) mustNot be (defined)
        result.get(TrustPreviouslyResidentPage) mustNot be (defined)
    }

  }

  "remove EstablishedUnderScotsLaw and TrustResidentOffshore when TrustResidentInUK is set to false" in {

    forAll(arbitrary[UserAnswers], arbitrary[Boolean]) {
      (initial, bool) =>

        val answers = initial.set(EstablishedUnderScotsLawPage, bool).success.value
          .set(TrustResidentOffshorePage, false).success.value

        val result = answers.set(TrustResidentInUKPage, false).success.value

        result.get(EstablishedUnderScotsLawPage) mustNot be (defined)
        result.get(TrustResidentOffshorePage) mustNot be (defined)
        result.get(TrustPreviouslyResidentPage) mustNot be (defined)
    }

  }

}
