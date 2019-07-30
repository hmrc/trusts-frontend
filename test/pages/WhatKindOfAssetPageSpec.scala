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

import models.{ShareClass, Status, UserAnswers, WhatKindOfAsset}
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours
import pages.entitystatus.AssetStatus
import pages.shares._

class WhatKindOfAssetPageSpec extends PageBehaviours {

  "WhatKindOfAssetPage" must {

    beRetrievable[WhatKindOfAsset](WhatKindOfAssetPage(0))

    beSettable[WhatKindOfAsset](WhatKindOfAssetPage(0))

    beRemovable[WhatKindOfAsset](WhatKindOfAssetPage(0))
  }

  "remove money when changing type of asset" in {
    forAll(arbitrary[UserAnswers]) {
      initial =>
        val answers: UserAnswers = initial
          .set(WhatKindOfAssetPage(0), WhatKindOfAsset.Money).success.value
          .set(AssetMoneyValuePage(0), "200").success.value
          .set(AssetStatus(0), Status.Completed).success.value

        val result = answers.set(WhatKindOfAssetPage(0), WhatKindOfAsset.Shares).success.value

        result.get(WhatKindOfAssetPage(0)).value mustEqual WhatKindOfAsset.Shares
        result.get(AssetMoneyValuePage(0)) mustNot be(defined)
        result.get(AssetStatus(0)) mustNot be(defined)
    }
  }

  "remove share portfolio data when changing type of asset" in {
    forAll(arbitrary[UserAnswers]) {
      initial =>
        val answers: UserAnswers = initial
          .set(WhatKindOfAssetPage(0), WhatKindOfAsset.Shares).success.value
          .set(SharesInAPortfolioPage(0), true).success.value
          .set(SharePortfolioNamePage(0), "Shares").success.value
          .set(SharePortfolioOnStockExchangePage(0), true).success.value
          .set(SharePortfolioQuantityInTrustPage(0), "20").success.value
          .set(SharePortfolioValueInTrustPage(0), "2000").success.value
          .set(AssetStatus(0), Status.Completed).success.value

        val result = answers.set(WhatKindOfAssetPage(0), WhatKindOfAsset.Money).success.value

        result.get(WhatKindOfAssetPage(0)).value mustEqual WhatKindOfAsset.Money

        result.get(SharesInAPortfolioPage(0)) mustNot be(defined)
        result.get(SharePortfolioNamePage(0)) mustNot be(defined)
        result.get(SharePortfolioOnStockExchangePage(0)) mustNot be(defined)
        result.get(SharePortfolioQuantityInTrustPage(0)) mustNot be(defined)
        result.get(SharePortfolioValueInTrustPage(0)) mustNot be(defined)
        result.get(AssetStatus(0)) mustNot be(defined)
    }
  }

  "remove share data when changing type of asset" in {
    forAll(arbitrary[UserAnswers]) {
      initial =>

        val answers: UserAnswers = initial
          .set(SharesInAPortfolioPage(0), false).success.value
          .set(ShareCompanyNamePage(0), "Company").success.value
          .set(SharesOnStockExchangePage(0), false).success.value
          .set(ShareClassPage(0), ShareClass.Ordinary).success.value
          .set(ShareQuantityInTrustPage(0), "20").success.value
          .set(ShareValueInTrustPage(0), "2000").success.value
          .set(AssetStatus(0), Status.Completed).success.value

        val result = answers.set(WhatKindOfAssetPage(0), WhatKindOfAsset.Money).success.value

        result.get(WhatKindOfAssetPage(0)).value mustEqual WhatKindOfAsset.Money

        result.get(SharesInAPortfolioPage(0)) mustNot be(defined)
        result.get(ShareCompanyNamePage(0)) mustNot be(defined)
        result.get(SharesOnStockExchangePage(0)) mustNot be(defined)
        result.get(ShareClassPage(0)) mustNot be(defined)
        result.get(ShareQuantityInTrustPage(0)) mustNot be(defined)
        result.get(ShareValueInTrustPage(0)) mustNot be(defined)
        result.get(AssetStatus(0)) mustNot be(defined)
    }
  }

}
