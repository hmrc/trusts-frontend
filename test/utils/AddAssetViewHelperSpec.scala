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

package utils

import base.RegistrationSpecBase
import models.core.pages.UKAddress
import models.registration.pages.ShareClass
import models.registration.pages.Status.Completed
import models.registration.pages.WhatKindOfAsset.{Money, PropertyOrLand, Shares}
import pages.entitystatus.AssetStatus
import pages.register.asset.property_or_land._
import pages.register.asset.shares._
import viewmodels.AddRow
import controllers.register.asset._
import pages.register.asset.WhatKindOfAssetPage
import pages.register.asset.money.AssetMoneyValuePage

class AddAssetViewHelperSpec extends RegistrationSpecBase {

  def removeMoneyRoute(index: Int) =
    money.routes.RemoveMoneyAssetController.onPageLoad(index, fakeDraftId).url

  def removeSharePortfolioRoute(index: Int) =
    shares.routes.RemoveSharePortfolioAssetController.onPageLoad(index, fakeDraftId).url

  def removeShareCompanyRoute(index: Int) =
    shares.routes.RemoveShareCompanyNameAssetController.onPageLoad(index, fakeDraftId).url

  def removePropertyOrLandRoute(index: Int) =
    property_or_land.routes.RemovePropertyOrLandWithAddressUKController.onPageLoad(index, fakeDraftId).url

  def removePropertyOrLandDescriptionRoute(index: Int) =
    property_or_land.routes.RemovePropertyOrLandWithDescriptionController.onPageLoad(index, fakeDraftId).url

  def removeAssetRoute(index: Int) =
    routes.DefaultRemoveAssetController.onPageLoad(index, fakeDraftId).url

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
          .set(PropertyOrLandAddressUkYesNoPage(2), true).success.value
          .set(WhatKindOfAssetPage(3), PropertyOrLand).success.value
          .set(PropertyOrLandAddressYesNoPage(3), false).success.value
          .set(WhatKindOfAssetPage(4), PropertyOrLand).success.value

        val rows = new AddAssetViewHelper(userAnswers, fakeDraftId).rows
        rows.inProgress mustBe List(
          AddRow("No name added", typeLabel = "Shares", "#", removeSharePortfolioRoute(0)),
          AddRow("No value added", typeLabel = "Money", "#", removeMoneyRoute(1)),
          AddRow("No address added", typeLabel = "Property or Land", "#", removePropertyOrLandRoute(2)),
          AddRow("No description added", typeLabel = "Property or Land", "#", removePropertyOrLandDescriptionRoute(3)),
          AddRow("No address or description added", typeLabel = "Property or Land", "#", removeAssetRoute(4))
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
          .set(PropertyOrLandUKAddressPage(2), UKAddress("line 1", "line 2", None, None, "NE1 1NE")).success.value
          .set(PropertyOrLandTotalValuePage(2), "100").success.value
          .set(TrustOwnAllThePropertyOrLandPage(2), true).success.value
          .set(AssetStatus(2), Completed).success.value
          .set(WhatKindOfAssetPage(3), PropertyOrLand).success.value
          .set(PropertyOrLandAddressYesNoPage(3), false).success.value
          .set(PropertyOrLandDescriptionPage(3), "1 hectare of land").success.value
          .set(PropertyOrLandTotalValuePage(3), "100").success.value
          .set(TrustOwnAllThePropertyOrLandPage(3), true).success.value
          .set(AssetStatus(3), Completed).success.value

        val rows = new AddAssetViewHelper(userAnswers, fakeDraftId).rows
        rows.complete mustBe List(
          AddRow("Share Company Name", typeLabel = "Shares", "#", removeShareCompanyRoute(0)),
          AddRow("£200", typeLabel = "Money", "#", removeMoneyRoute(1)),
          AddRow("line 1", typeLabel = "Property or Land", "#", removePropertyOrLandRoute(2)),
          AddRow("1 hectare of land", typeLabel = "Property or Land", "#", removePropertyOrLandDescriptionRoute(3))
        )
        rows.inProgress mustBe Nil
      }

    }
  }
}
