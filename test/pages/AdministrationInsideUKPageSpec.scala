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
import pages.status.TrustDetailsStatus

class AdministrationInsideUKPageSpec extends PageBehaviours {

  "AdministrationInsideUKPage" must {

    beRetrievable[Boolean](AdministrationInsideUKPage)

    beSettable[Boolean](AdministrationInsideUKPage)

    beRemovable[Boolean](AdministrationInsideUKPage)
  }

  "remove CountryAdministeringTrust when AdministrationInsideUK is set to true" in {

    forAll(arbitrary[UserAnswers], arbitrary[String]) {
      (initial, answer) =>

        val answers = initial.set(CountryAdministeringTrustPage, answer).success.value

        val result = answers.set(AdministrationInsideUKPage, true).success.value

        result.get(CountryAdministeringTrustPage) mustNot be (defined)
        result.get(TrustDetailsStatus) mustNot be(defined)
    }

  }

}
