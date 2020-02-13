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

package pages.register.trust_details

import models.core.UserAnswers
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours
import pages.entitystatus.TrustDetailsStatus

class InheritanceTaxActPageSpec extends PageBehaviours {

  "InheritanceTaxActPage" must {

    beRetrievable[Boolean](InheritanceTaxActPage)

    beSettable[Boolean](InheritanceTaxActPage)

    beRemovable[Boolean](InheritanceTaxActPage)
  }

  "remove AgentOtherThanBarrister when InheritanceTaxAct is set to false" in {

    forAll(arbitrary[UserAnswers], arbitrary[Boolean]) {
      (initial, bool) =>

        val answers = initial.set(AgentOtherThanBarristerPage, bool).success.value

        val result = answers.set(InheritanceTaxActPage, false).success.value

        result.get(AgentOtherThanBarristerPage) mustNot be (defined)
        result.get(TrustDetailsStatus) mustNot be(defined)
    }

  }
}
