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
import models.core.pages.{FullName, IndividualOrBusiness, UKAddress}
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours

class IsThisLeadTrusteePageSpec extends PageBehaviours {

  "IsThisLeadTrusteePage" must {

    beRetrievable[Boolean](IsThisLeadTrusteePage(0))

    beSettable[Boolean](IsThisLeadTrusteePage(0))

    beRemovable[Boolean](IsThisLeadTrusteePage(0))
  }

  "remove relevant data when IsThisLeadTrusteePage is set to false for an individual" in {
    val index = 0
    forAll(arbitrary[UserAnswers], arbitrary[String], arbitrary[FullName], arbitrary[LocalDate]) {
      (initial, str, name, date) =>
        val answers: UserAnswers = initial
          .set(TrusteeIndividualOrBusinessPage(index), IndividualOrBusiness.Individual).success.value
          .set(TrusteesNamePage(index), name).success.value
          .set(TrusteesDateOfBirthPage(index), date).success.value
          .set(TrusteeAUKCitizenPage(index), true).success.value
          .set(TrusteesNinoPage(index), str).success.value
          .set(TrusteeAddressInTheUKPage(index), true).success.value
          .set(TrusteesUkAddressPage(index), UKAddress(str, str, None, None, str)).success.value
          .set(TelephoneNumberPage(index), str).success.value

        val result = answers.set(IsThisLeadTrusteePage(index), false).success.value

        result.get(TrusteeIndividualOrBusinessPage(index)) mustNot be(defined)
        result.get(TrusteesNamePage(index)) mustNot be(defined)
        result.get(TrusteesDateOfBirthPage(index)) mustNot be(defined)
        result.get(TrusteeAUKCitizenPage(index)) mustNot be(defined)
        result.get(TrusteesNinoPage(index)) mustNot be(defined)
        result.get(TrusteeAddressInTheUKPage(index)) mustNot be(defined)
        result.get(TrusteesUkAddressPage(index)) mustNot be(defined)
        result.get(TelephoneNumberPage(index)) mustNot be(defined)
    }
  }

  "remove relevant data when IsThisLeadTrusteePage is set to false for an organisation" in {
    val index = 0
    forAll(arbitrary[UserAnswers], arbitrary[String], arbitrary[UKAddress]) {
      (initial, str, address) =>
        val answers: UserAnswers = initial
          .set(TrusteeIndividualOrBusinessPage(index), IndividualOrBusiness.Business).success.value
          .set(TrusteeUtrYesNoPage(index), true).success.value
          .set(TrusteeOrgNamePage(index), str).success.value
          .set(TrusteesUtrPage(index), str).success.value
          .set(TrusteeOrgAddressUkYesNoPage(index), true).success.value
          .set(TrusteeOrgAddressUkPage(index), address).success.value
          .set(TelephoneNumberPage(index), str).success.value

        val result = answers.set(IsThisLeadTrusteePage(index), false).success.value

        result.get(TrusteeIndividualOrBusinessPage(index)) mustNot be(defined)
        result.get(TrusteeUtrYesNoPage(index)) mustNot be(defined)
        result.get(TrusteeOrgNamePage(index)) mustNot be(defined)
        result.get(TrusteesUtrPage(index)) mustNot be(defined)
        result.get(TrusteeOrgAddressUkYesNoPage(index)) mustNot be(defined)
        result.get(TrusteeOrgAddressUkPage(index)) mustNot be(defined)
        result.get(TelephoneNumberPage(index)) mustNot be(defined)
    }
  }


}
