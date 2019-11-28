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

package queries

import models.core.UserAnswers
import models.core.pages.{FullName, UKAddress}
import org.scalacheck.Arbitrary.arbitrary
import pages._
import pages.behaviours.PageBehaviours

class RemoveIndividualBeneficiaryQuerySpec extends PageBehaviours {

  val index : Int = 0

  "RemoveIndividualBeneficiaryQuery" must {

    "remove individual beneficiary at index" in {
      forAll(arbitrary[UserAnswers]) {
        initial =>

          val answers: UserAnswers = initial
            .set(IndividualBeneficiaryNamePage(0), FullName("First", None, "Last")).success.value
            .set(IndividualBeneficiaryAddressYesNoPage(0), true).success.value
            .set(IndividualBeneficiaryAddressUKYesNoPage(0), true).success.value
            .set(IndividualBeneficiaryAddressUKPage(0), UKAddress("1", "2", Some("3"), Some("4"), "5")).success.value
            .set(IndividualBeneficiaryNamePage(1), FullName("Second", None, "Last")).success.value

          val result = answers.remove(RemoveIndividualBeneficiaryQuery(index)).success.value

          result.get(IndividualBeneficiaryNamePage(0)).value mustBe FullName("Second", None, "Last")
          result.get(IndividualBeneficiaryAddressYesNoPage(0)) mustNot be(defined)
          result.get(IndividualBeneficiaryAddressUKYesNoPage(0)) mustNot be(defined)
          result.get(IndividualBeneficiaryAddressUKPage(0)) mustNot be(defined)

          result.get(IndividualBeneficiaryNamePage(1)) mustNot be(defined)
      }
    }

  }

}
