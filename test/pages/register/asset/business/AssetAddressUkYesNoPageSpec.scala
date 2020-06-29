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

package pages.register.asset.business

import models.core.UserAnswers
import models.core.pages.{InternationalAddress, UKAddress}
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours
import pages.register.asset.property_or_land.{PropertyOrLandAddressUkYesNoPage, PropertyOrLandInternationalAddressPage, PropertyOrLandUKAddressPage}
import pages.register.settlors.deceased_settlor.{SettlorsInternationalAddressPage, SettlorsUKAddressPage, WasSettlorsAddressUKYesNoPage}


class AssetAddressUkYesNoPageSpec extends PageBehaviours {

  "AssetMoneyValuePage" must {

    beRetrievable[Boolean](AssetAddressUkYesNoPage(0))

    beSettable[Boolean](AssetAddressUkYesNoPage(0))

    beRemovable[Boolean](AssetAddressUkYesNoPage(0))
  }

  "remove relevant data" when {

    val page = AssetAddressUkYesNoPage(0)

    "set to true" in {
      forAll(arbitrary[UserAnswers]) {
        initial =>
          val answers: UserAnswers = initial.set(page, false).success.value
            .set(AssetInternationalAddressPage(0), InternationalAddress("line 1", "line 2", None, "France")).success.value

          val result = answers.set(page, true).success.value

          result.get(AssetInternationalAddressPage(0)) must not be defined
      }
    }

    "set to false" in {
      forAll(arbitrary[UserAnswers]) {
        initial =>
          val answers: UserAnswers = initial.set(page, true).success.value
            .set(AssetUkAddressPage(0), UKAddress("line 1", "line 2", None, None, "NE1 1NE")).success.value

          val result = answers.set(page, false).success.value

          result.get(AssetUkAddressPage(0)) must not be defined
      }
    }
  }
}
