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

package navigation.navigators


import base.SpecBase
import controllers.routes
import generators.Generators
import models.WhatKindOfAsset.{Money, Shares}
import models.{AddAssets, NormalMode, UserAnswers}
import navigation.Navigator
import org.scalacheck.Arbitrary.arbitrary
import org.scalatest.prop.PropertyChecks
import pages._

trait AssetRoutes {

  self: PropertyChecks with Generators with SpecBase =>

  def assetRoutes()(implicit navigator: Navigator) = {

    "money assets" must {

      "go to AssetMoneyValuePage from WhatKindOfAsset page when the money option is selected" in {
        val index = 0

        forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            val answers = userAnswers.set(WhatKindOfAssetPage(index), Money).success.value

            navigator.nextPage(WhatKindOfAssetPage(index), NormalMode, fakeDraftId)(answers)
              .mustBe(routes.AssetMoneyValueController.onPageLoad(NormalMode, index, fakeDraftId))
        }
      }

      "go to AddAssetsPage from AssetMoneyValue page when the amount submitted" in {

        val index = 0

        forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            val answers = userAnswers.set(WhatKindOfAssetPage(index), Money).success.value

            navigator.nextPage(AssetMoneyValuePage(index), NormalMode, fakeDraftId)(answers)
              .mustBe(routes.AddAssetsController.onPageLoad(fakeDraftId))

        }
      }

    }

    "share assets" must {

      "go to SharesInAPortfolio from WhatKindOfAsset when share option is selected" in {
        val index = 0

        forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            val answers = userAnswers.set(WhatKindOfAssetPage(index), Shares).success.value

            navigator.nextPage(WhatKindOfAssetPage(index), NormalMode, fakeDraftId)(answers)
              .mustBe(routes.SharesInAPortfolioController.onPageLoad(NormalMode, index, fakeDraftId))
        }
      }

      "go to SharePortfolioName from SharesInAPortfolio when user answers yes" in {
        val index = 0

        forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            val answers = userAnswers.set(SharesInAPortfolioPage(index), true).success.value

            navigator.nextPage(SharesInAPortfolioPage(index), NormalMode, fakeDraftId)(answers)
              .mustBe(routes.SharePortfolioNameController.onPageLoad(NormalMode, index, fakeDraftId))
        }
      }

      "go to SharePortfolioOnStockExchange from SharePortfolioName" in {
        val index = 0

        forAll(arbitrary[UserAnswers]) {
          userAnswers =>
            navigator.nextPage(SharePortfolioNamePage(index), NormalMode, fakeDraftId)(userAnswers)
              .mustBe(routes.SharePortfolioOnStockExchangeController.onPageLoad(NormalMode, index, fakeDraftId))
        }
      }

      "go to SharePortfolioQuantityInTrust from SharePortfolioOnStockExchange" in {
        val index = 0

        forAll(arbitrary[UserAnswers]) {
          userAnswers =>
            navigator.nextPage(SharePortfolioOnStockExchangePage(index), NormalMode, fakeDraftId)(userAnswers)
              .mustBe(routes.SharePortfolioQuantityInTrustController.onPageLoad(NormalMode, index, fakeDraftId))
        }
      }

      "go to SharePortfolioValueInTrust from SharePortfolioQuantityInTrust" in {
        val index = 0

        forAll(arbitrary[UserAnswers]) {
          userAnswers =>
            navigator.nextPage(SharePortfolioQuantityInTrustPage(index), NormalMode, fakeDraftId)(userAnswers)
              .mustBe(routes.SharePortfolioValueInTrustController.onPageLoad(NormalMode, index, fakeDraftId))
        }
      }

      "go to ShareAnswers from SharePortfolioValueInTrust" in {
        val index = 0

        forAll(arbitrary[UserAnswers]) {
          userAnswers =>
            navigator.nextPage(SharePortfolioValueInTrustPage(index), NormalMode, fakeDraftId)(userAnswers)
              .mustBe(routes.ShareAnswerController.onPageLoad(index, fakeDraftId))
        }
      }

    }


   "add another asset" must {

      "go to the WhatKindOfAssetPage from AddAssetsPage when selected add them now" in {

        val answers = emptyUserAnswers
          .set(WhatKindOfAssetPage(0), Money).success.value
          .set(AddAssetsPage, AddAssets.YesNow).success.value

        navigator.nextPage(AddAssetsPage, NormalMode, fakeDraftId)(answers)
          .mustBe(routes.WhatKindOfAssetController.onPageLoad(NormalMode, 1, fakeDraftId))
      }
    }

    "go to RegistrationProgress from AddAssetsPage when selecting add them later" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          val answers = emptyUserAnswers
            .set(WhatKindOfAssetPage(0), Money).success.value
            .set(AddAssetsPage, AddAssets.YesLater).success.value

          navigator.nextPage(AddAssetsPage, NormalMode, fakeDraftId)(answers)
            .mustBe(routes.TaskListController.onPageLoad(fakeDraftId))
      }
    }

    "go to RegistrationProgress from AddAssetsPage when selecting no complete" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          val answers = emptyUserAnswers
            .set(WhatKindOfAssetPage(0), Money).success.value
            .set(AddAssetsPage, AddAssets.NoComplete).success.value

          navigator.nextPage(AddAssetsPage, NormalMode, fakeDraftId)(answers)
            .mustBe(routes.TaskListController.onPageLoad(fakeDraftId))
      }
    }
  }
}