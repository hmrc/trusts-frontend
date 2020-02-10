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

package pages.register.trustees

import java.time.LocalDate

import models.core.UserAnswers
import models.core.pages.IndividualOrBusiness._
import models.core.pages.{FullName, IndividualOrBusiness, UKAddress}
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours
import pages.register.trustees.organisation.{TrusteeOrgAddressUkPage, TrusteeOrgAddressUkYesNoPage, TrusteeOrgNamePage, TrusteeUtrYesNoPage, TrusteesUtrPage}


class TrusteeIndividualOrBusinessPageSpec extends PageBehaviours {

  "IndividualOrBusinessPage" must {

    beRetrievable[IndividualOrBusiness](TrusteeIndividualOrBusinessPage(0))

    beSettable[IndividualOrBusiness](TrusteeIndividualOrBusinessPage(0))

    beRemovable[IndividualOrBusiness](TrusteeIndividualOrBusinessPage(0))
  }

  "remove relevant data when TrusteeIndividualOrBusinessPage is set to Individual" in {
    val index = 0
    forAll(arbitrary[UserAnswers], arbitrary[String]) {
      (initial, str) =>
        val answers: UserAnswers = initial
          .set(TrusteeUtrYesNoPage(index), true).success.value
          .set(TrusteeOrgNamePage(index), str).success.value
          .set(TrusteesUtrPage(index), str).success.value
          .set(TrusteeOrgAddressUkYesNoPage(index), true).success.value
          .set(TrusteeOrgAddressUkPage(index), UKAddress(str, str, None, None, str)).success.value
          .set(TelephoneNumberPage(index), str).success.value

        val result = answers.set(TrusteeIndividualOrBusinessPage(index), Individual).success.value

        result.get(TrusteeUtrYesNoPage(index)) mustNot be(defined)
        result.get(TrusteeOrgNamePage(index)) mustNot be(defined)
        result.get(TrusteesUtrPage(index)) mustNot be(defined)
        result.get(TrusteeOrgAddressUkYesNoPage(index)) mustNot be(defined)
        result.get(TrusteeOrgAddressUkPage(index)) mustNot be(defined)
        result.get(TelephoneNumberPage(index)) mustNot be(defined)
    }
  }


  "remove relevant data when TrusteeIndividualOrBusinessPage is set to Business" in {
    val index = 0
    forAll(arbitrary[UserAnswers], arbitrary[String]) {
      (initial, str) =>
        val answers: UserAnswers = initial
          .set(TrusteesDateOfBirthPage(index), LocalDate.now()).success.value
          .set(TrusteeAUKCitizenPage(index), true).success.value
          .set(TrusteesNinoPage(index), str).success.value
          .set(TrusteeAddressInTheUKPage(index), true).success.value
          .set(TrusteesUkAddressPage(index), UKAddress(str, str, None, None, str)).success.value
          .set(TelephoneNumberPage(index), str).success.value

        val result = answers.set(TrusteeIndividualOrBusinessPage(index), Business).success.value

        result.get(TrusteesDateOfBirthPage(index)) mustNot be(defined)
        result.get(TrusteeAUKCitizenPage(index)) mustNot be(defined)
        result.get(TrusteesNinoPage(index)) mustNot be(defined)
        result.get(TrusteeAddressInTheUKPage(index)) mustNot be(defined)
        result.get(TrusteesUkAddressPage(index)) mustNot be(defined)
        result.get(TelephoneNumberPage(index)) mustNot be(defined)
    }
  }


}
