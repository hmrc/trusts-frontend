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

package utils

import base.SpecBase
import models.ShareClass
import models.Status.Completed
import models.WhatKindOfAsset.{Money, PropertyOrLand, Shares}
import models.{ShareClass, UKAddress}
import pages.entitystatus.AssetStatus
import pages.property_or_land._
import pages.shares._
import pages.{AssetMoneyValuePage, WhatKindOfAssetPage}
import viewmodels.AddRow

class AddAssetViewHelperSpec extends SpecBase {

  def removeMoneyRoute(index : Int) =
    controllers.money.routes.RemoveMoneyAssetController.onPageLoad(index, fakeDraftId).url

  def removeSharePortfolioRoute(index : Int) =
    controllers.shares.routes.RemoveSharePortfolioAssetController.onPageLoad(index, fakeDraftId).url

  def removeShareCompanyRoute(index : Int) =
    controllers.shares.routes.RemoveShareCompanyNameAssetController.onPageLoad(index, fakeDraftId).url

  def removePropertyOrLandRoute(index : Int) =
    controllers.property_or_land.routes.RemovePropertyOrLandWithAddressUKController.onPageLoad(index, fakeDraftId).url

  "AddAssetViewHelper" when {

    ".row" must {

      "generate Nil for no user answers" in {
        val rows = new AddAssetViewHelper(emptyUserAnswers, fakeDraftId).rows
        rows.inProgress mustBe Nil
        rows.complete mustBe Nil
      }

      "generate rows from user answers for assets in progress" in {

        val userAnswers = emptyUserAnswers
          .set(WhatKindOfAssetPage(0), Shares).success.value
          .set(SharesInAPortfolioPage(0), true).success.value
          .set(WhatKindOfAssetPage(1), Money).success.value
          .set(WhatKindOfAssetPage(2), PropertyOrLand).success.value
          .set(PropertyOrLandAddressYesNoPage(2), true).success.value

        val rows = new AddAssetViewHelper(userAnswers, fakeDraftId).rows
        rows.inProgress mustBe List(
          AddRow("No name added", typeLabel = "Shares", "#", removeSharePortfolioRoute(0)),
          AddRow("No value added", typeLabel = "Money", "#", removeMoneyRoute(1)),
          AddRow("No address added", typeLabel = "PropertyOrLand", "#", removePropertyOrLandRoute(2))
        )
        rows.complete mustBe Nil
      }

      "generate rows from user answers for complete assets" in {

        val userAnswers = emptyUserAnswers
          .set(WhatKindOfAssetPage(0), Shares).success.value
          .set(SharesInAPortfolioPage(0), false).success.value
          .set(ShareCompanyNamePage(0), "Share Company Name").success.value
          .set(SharesOnStockExchangePage(0), true).success.value
          .set(ShareClassPage(0), ShareClass.Ordinary).success.value
          .set(ShareQuantityInTrustPage(0), "1000").success.value
          .set(ShareValueInTrustPage(0), "10").success.value
          .set(AssetStatus(0), Completed).success.value
          .set(WhatKindOfAssetPage(1), Money).success.value
          .set(AssetMoneyValuePage(1), "200").success.value
          .set(AssetStatus(1), Completed).success.value
          .set(WhatKindOfAssetPage(2), PropertyOrLand).success.value
          .set(PropertyOrLandAddressYesNoPage(2), true).success.value
          .set(PropertyOrLandAddressUkYesNoPage(2), true).success.value
          .set(PropertyOrLandUKAddressPage(2), UKAddress("line 1", None, None, "Newcastle upon Tyne", "NE1 1NE")).success.value
          .set(PropertyOrLandTotalValuePage(2), "100").success.value
          .set(TrustOwnAllThePropertyOrLandPage(2), true).success.value
          .set(AssetStatus(2), Completed).success.value

        val rows = new AddAssetViewHelper(userAnswers, fakeDraftId).rows
        rows.complete mustBe List(
          AddRow("Share Company Name", typeLabel = "Shares", "#", removeShareCompanyRoute(0)),
          AddRow("£200", typeLabel = "Money", "#", removeMoneyRoute(1)),
          AddRow("line 1", typeLabel = "PropertyOrLand", "#", removePropertyOrLandRoute(2))
        )
        rows.inProgress mustBe Nil
      }

    }
  }
}
