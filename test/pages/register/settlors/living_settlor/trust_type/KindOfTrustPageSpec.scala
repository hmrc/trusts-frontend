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

package pages.register.settlors.living_settlor.trust_type

import models.core.UserAnswers
import models.registration.pages.KindOfTrust
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours
import pages.register.settlors.SetUpAfterSettlorDiedYesNoPage

class KindOfTrustPageSpec extends PageBehaviours {

  "KindOfTrustPage" must {

    beRetrievable[KindOfTrust](KindOfTrustPage)

    beSettable[KindOfTrust](KindOfTrustPage)

    beRemovable[KindOfTrust](KindOfTrustPage)
  }

  "for a Lifetime trust remove holdover relief when changing type of trust" in {
    forAll(arbitrary[UserAnswers], arbitrary[String]) {
      (initial, str) =>
        val answers: UserAnswers = initial
          .set(SetUpAfterSettlorDiedYesNoPage, false).success.value
          .set(KindOfTrustPage, KindOfTrust.Intervivos).success.value
          .set(HoldoverReliefYesNoPage, true).success.value

        val result = answers.set(KindOfTrustPage, KindOfTrust.FlatManagement).success.value

        result.get(HoldoverReliefYesNoPage) mustNot be(defined)
    }
  }
}
