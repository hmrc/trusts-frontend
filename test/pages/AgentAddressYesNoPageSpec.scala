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

import models.{InternationalAddress, UKAddress, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours

class AgentAddressYesNoPageSpec extends PageBehaviours {

  "AgentAddressYesNoPage" must {

    beRetrievable[Boolean](AgentAddressYesNoPage)

    beSettable[Boolean](AgentAddressYesNoPage)

    beRemovable[Boolean](AgentAddressYesNoPage)
  }

  "remove AgentUKAddressPage when AgentAddressYesNoPage is set to false" in {
    val index = 0
    forAll(arbitrary[UserAnswers], arbitrary[String]) {
      (initial, str) =>
        val answers: UserAnswers = initial.set(AgentUKAddressPage,UKAddress(str, str, Some(str), Some(str), str) ).success.value
        val result = answers.set(AgentAddressYesNoPage, false).success.value

        result.get(AgentUKAddressPage) mustNot be(defined)
    }
  }

  "remove AgentInternationalAddressPage when AgentAddressYesNoPage is set to true" in {
    val index = 0
    forAll(arbitrary[UserAnswers], arbitrary[String]) {
      (initial, str) =>
        val answers: UserAnswers = initial.set(AgentInternationalAddressPage,InternationalAddress(str, str, Some(str), Some(str), str) ).success.value
        val result = answers.set(AgentAddressYesNoPage, true).success.value

        result.get(AgentInternationalAddressPage) mustNot be(defined)
    }
  }
}
