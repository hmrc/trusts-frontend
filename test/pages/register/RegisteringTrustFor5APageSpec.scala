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
import models.registration.pages.NonResidentType
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours
import pages.entitystatus.TrustDetailsStatus
import pages.register.agents.AgentOtherThanBarristerPage

class RegisteringTrustFor5APageSpec extends PageBehaviours {

  "RegisteringTrustFor5APage" must {

    beRetrievable[Boolean](RegisteringTrustFor5APage)

    beSettable[Boolean](RegisteringTrustFor5APage)

    beRemovable[Boolean](RegisteringTrustFor5APage)
  }


  "Trust Details must not be defined when RegisteringTrustFor5APage is set to false" in {

    forAll(arbitrary[UserAnswers]) {
      initial =>

        val result = initial.set(RegisteringTrustFor5APage, false).success.value

        result.get(TrustDetailsStatus) mustNot be(defined)
    }

  }

  "remove InheritanceTaxActPage and AgentOtherThanBarristerPage when RegisteringTrustFor5APage is set to true" in {

    forAll(arbitrary[UserAnswers], arbitrary[Boolean]) {
      (initial, bool) =>
         val answers = initial.set(InheritanceTaxActPage, true).success.value.set(AgentOtherThanBarristerPage, bool).success.value

        val result = answers.set(RegisteringTrustFor5APage, true).success.value

        result.get(InheritanceTaxActPage) mustNot be (defined)
        result.get(AgentOtherThanBarristerPage) mustNot be (defined)
        result.get(TrustDetailsStatus) mustNot be(defined)

    }
  }


}
