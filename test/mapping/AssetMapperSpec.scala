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

package mapping

import base.SpecBaseHelpers
import generators.Generators
import models.Status.Completed
import models.WhatKindOfAsset
import org.scalatest.{FreeSpec, MustMatchers, OptionValues}
import pages.entitystatus.AssetStatus
import pages.shares._
import pages.{AssetMoneyValuePage, WhatKindOfAssetPage}

class AssetMapperSpec extends FreeSpec with MustMatchers
  with OptionValues with Generators with SpecBaseHelpers {

  val assetMapper: Mapping[Assets] = injector.instanceOf[AssetMapper]

  "AssetMapper" - {

    "when user answers is empty" - {

      "must not be able to create Assets" in {

        val userAnswers = emptyUserAnswers

        assetMapper.build(userAnswers) mustNot be(defined)
      }
    }

    "when user answers is not empty " - {

      "must be able to create Assets for money" in {

        val userAnswers = emptyUserAnswers
          .set(WhatKindOfAssetPage(0), WhatKindOfAsset.Money).success.value
          .set(AssetMoneyValuePage(0), "2000").success.value
          .set(AssetStatus(0), Completed).success.value


        val expected = Some(Assets(Some(List(AssetMonetaryAmount(2000))),None,None,None,None,None))

        assetMapper.build(userAnswers) mustBe expected
      }

      "must be able to create Assets for shares" in {

        val userAnswers = emptyUserAnswers
          .set(WhatKindOfAssetPage(0), WhatKindOfAsset.Shares).success.value
          .set(SharesInAPortfolioPage(0), true).success.value
          .set(SharePortfolioNamePage(0), "Portfolio").success.value
          .set(SharePortfolioQuantityInTrustPage(0), "30").success.value
          .set(SharePortfolioValueInTrustPage(0), "999999999999").success.value
          .set(SharePortfolioOnStockExchangePage(0), false).success.value
          .set(AssetStatus(0), Completed).success.value


        val expected = Some(Assets(None,None,Some(List(SharesType("30","Portfolio","Other","Unquoted",999999999999L))),None,None,None))

        assetMapper.build(userAnswers) mustBe expected
      }

      "must be able to create Assets for both shares and money" in {

        val userAnswers = emptyUserAnswers
          .set(WhatKindOfAssetPage(0), WhatKindOfAsset.Shares).success.value
          .set(SharesInAPortfolioPage(0), true).success.value
          .set(SharePortfolioNamePage(0), "Portfolio").success.value
          .set(SharePortfolioQuantityInTrustPage(0), "30").success.value
          .set(SharePortfolioValueInTrustPage(0), "999999999999").success.value
          .set(SharePortfolioOnStockExchangePage(0), false).success.value
          .set(AssetStatus(0), Completed).success.value
          .set(WhatKindOfAssetPage(1), WhatKindOfAsset.Money).success.value
          .set(AssetMoneyValuePage(1), "2000").success.value
          .set(AssetStatus(1), Completed).success.value


        val expected = Some(Assets(Some(List(AssetMonetaryAmount(2000))),None,Some(List(SharesType("30","Portfolio","Other","Unquoted",999999999999L))),None,None,None))

        assetMapper.build(userAnswers) mustBe expected
      }
    }
  }
}