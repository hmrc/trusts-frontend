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

package queries

import models.core.UserAnswers
import models.registration.pages.Status.Completed
import models.registration.pages.{ShareClass, WhatKindOfAsset}
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours
import pages.entitystatus.AssetStatus
import pages.register.asset.WhatKindOfAssetPage
import pages.register.asset.money.AssetMoneyValuePage
import pages.register.asset.property_or_land.{PropertyOrLandDescriptionPage, PropertyOrLandTotalValuePage, TrustOwnAllThePropertyOrLandPage}
import pages.register.asset.shares._

class RemoveAssetQuerySpec extends PageBehaviours {

  "RemoveAssetQuery" when {

    "money" must {

      "remove asset at index" in {
        forAll(arbitrary[UserAnswers]) {
          initial =>

            val answers: UserAnswers = initial
              .set(AssetMoneyValuePage(0), "200").success.value

            val result = answers.remove(RemoveAssetQuery(0)).success.value

            result.get(AssetMoneyValuePage(0)) mustNot be(defined)
        }
      }
    }

    "shares" must {

      "remove asset at index" in {
        forAll(arbitrary[UserAnswers]) {
          initial =>

            val answers: UserAnswers = initial
              .set(WhatKindOfAssetPage(0), WhatKindOfAsset.Shares).success.value
              .set(ShareClassPage(0), ShareClass.Ordinary).success.value
              .set(ShareCompanyNamePage(0), "AWS").success.value
              .set(SharesInAPortfolioPage(0), false).success.value
              .set(ShareValueInTrustPage(0), "2000").success.value
              .set(SharesOnStockExchangePage(0), false).success.value
              .set(ShareQuantityInTrustPage(0), "20").success.value
              .set(AssetStatus(0), Completed).success.value

            val result = answers.remove(RemoveAssetQuery(0)).success.value

            result.get(WhatKindOfAssetPage(0)) mustNot be(defined)
            result.get(ShareClassPage(0)) mustNot be(defined)
            result.get(ShareCompanyNamePage(0)) mustNot be(defined)
            result.get(SharesInAPortfolioPage(0)) mustNot be(defined)
            result.get(ShareValueInTrustPage(0)) mustNot be(defined)
            result.get(SharesOnStockExchangePage(0)) mustNot be(defined)
            result.get(ShareQuantityInTrustPage(0)) mustNot be(defined)
            result.get(AssetStatus(0)) mustNot be(defined)
        }
      }

    }

    "property or land" must {

      "remove asset at index" in {
        forAll(arbitrary[UserAnswers]) {
          initial =>

            val answers: UserAnswers = initial
              .set(WhatKindOfAssetPage(0), WhatKindOfAsset.PropertyOrLand).success.value
              .set(PropertyOrLandDescriptionPage(0), "Land").success.value
              .set(PropertyOrLandTotalValuePage(0), "80,000").success.value
              .set(TrustOwnAllThePropertyOrLandPage(0), true).success.value

            val result = answers.remove(RemoveAssetQuery(0)).success.value

            result.get(WhatKindOfAssetPage(0)) mustNot be(defined)
            result.get(PropertyOrLandDescriptionPage(0)) mustNot be(defined)
            result.get(PropertyOrLandTotalValuePage(0)) mustNot be(defined)
            result.get(TrustOwnAllThePropertyOrLandPage(0)) mustNot be(defined)
        }
      }

    }

  }

}
