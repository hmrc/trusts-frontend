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

package pages.register.settlors.living_settlor

import models.core.UserAnswers
import models.core.pages.{FullName, IndividualOrBusiness}
import models.registration.pages.KindOfTrust
import models.registration.pages.Status.InProgress
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours
import pages.entitystatus.LivingSettlorStatus
import pages.register.settlors.SetUpAfterSettlorDiedYesNoPage
import pages.register.settlors.living_settlor.trust_type.{HoldoverReliefYesNoPage, KindOfTrustPage}

class SettlorIndividualOrBusinessPageSpec extends PageBehaviours {

  "SettlorIndividualOrBusinessPage" must {

    beRetrievable[IndividualOrBusiness](SettlorIndividualOrBusinessPage(0))

    beSettable[IndividualOrBusiness](SettlorIndividualOrBusinessPage(0))

    beRemovable[IndividualOrBusiness](SettlorIndividualOrBusinessPage(0))
  }

  "remove business related data when changing to individual" in {
    forAll(arbitrary[UserAnswers], arbitrary[String]) {
      (initial, str) =>
        val answers: UserAnswers = initial
          .set(SetUpAfterSettlorDiedYesNoPage, false).success.value
          .set(KindOfTrustPage, KindOfTrust.Intervivos).success.value
          .set(HoldoverReliefYesNoPage, true).success.value
          .set(SettlorIndividualOrBusinessPage(0), IndividualOrBusiness.Business).success.value
          .set(SettlorBusinessNamePage(0), "AWS").success.value
          .set(LivingSettlorStatus(0), InProgress).success.value

        val result = answers.set(SettlorIndividualOrBusinessPage(0), IndividualOrBusiness.Individual).success.value

        result.get(LivingSettlorStatus(0)) mustNot be(defined)
    }
  }

  "remove individual related data when changing to business" in {
    forAll(arbitrary[UserAnswers], arbitrary[String]) {
      (initial, str) =>
        val answers: UserAnswers = initial
          .set(SetUpAfterSettlorDiedYesNoPage, false).success.value
          .set(KindOfTrustPage, KindOfTrust.Intervivos).success.value
          .set(HoldoverReliefYesNoPage, true).success.value
          .set(SettlorIndividualOrBusinessPage(0), IndividualOrBusiness.Individual).success.value
          .set(SettlorIndividualNamePage(0), FullName("First", None, "Last")).success.value
          .set(SettlorIndividualDateOfBirthYesNoPage(0), true).success.value
          .set(LivingSettlorStatus(0), InProgress).success.value

        val result = answers.set(SettlorIndividualOrBusinessPage(0), IndividualOrBusiness.Business).success.value

        result.get(SettlorIndividualDateOfBirthYesNoPage(0)) mustNot be(defined)
        result.get(SettlorIndividualDateOfBirthPage(0)) mustNot be(defined)
        result.get(SettlorIndividualNINOYesNoPage(0)) mustNot be(defined)
        result.get(SettlorIndividualNINOPage(0)) mustNot be(defined)
        result.get(SettlorAddressYesNoPage(0)) mustNot be(defined)
        result.get(SettlorAddressUKYesNoPage(0)) mustNot be(defined)
        result.get(SettlorAddressUKPage(0)) mustNot be(defined)
        result.get(SettlorAddressInternationalPage(0)) mustNot be(defined)
        result.get(SettlorIndividualPassportYesNoPage(0)) mustNot be(defined)
        result.get(SettlorIndividualPassportPage(0)) mustNot be(defined)
        result.get(SettlorIndividualIDCardYesNoPage(0)) mustNot be(defined)
        result.get(SettlorIndividualIDCardPage(0)) mustNot be(defined)
        result.get(LivingSettlorStatus(0)) mustNot be(defined)
    }
  }

}
