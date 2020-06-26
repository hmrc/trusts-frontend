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

import java.time.{LocalDate, ZoneOffset}

import base.RegistrationSpecBase
import controllers.register.asset._
import models.NormalMode
import models.core.pages.UKAddress
import models.registration.pages.ShareClass
import models.registration.pages.Status.Completed
import models.registration.pages.WhatKindOfAsset._
import pages.entitystatus.AssetStatus
import pages.register.asset.WhatKindOfAssetPage
import pages.register.asset.money.AssetMoneyValuePage
import pages.register.asset.other.{OtherAssetDescriptionPage, OtherAssetValuePage}
import pages.register.asset.partnership.{PartnershipDescriptionPage, PartnershipStartDatePage}
import pages.register.asset.property_or_land._
import pages.register.asset.shares._
import viewmodels.AddRow

class AddAssetViewHelperSpec extends RegistrationSpecBase {

  def changeMoneyAssetRoute(index: Int): String =
    money.routes.AssetMoneyValueController.onPageLoad(NormalMode, index, fakeDraftId).url

  def changePropertyOrLandAssetRoute(index: Int): String =
    property_or_land.routes.PropertyOrLandAddressYesNoController.onPageLoad(NormalMode, index, fakeDraftId).url

  def changeSharesAssetRoute(index: Int): String =
    shares.routes.SharesInAPortfolioController.onPageLoad(NormalMode, index, fakeDraftId).url

  def changeBusinessAssetRoute(index: Int): String =
    controllers.routes.FeatureNotAvailableController.onPageLoad().url

  def changePartnershipAssetRoute(index: Int): String =
    partnership.routes.PartnershipDescriptionController.onPageLoad(NormalMode, index, fakeDraftId).url

  def changeOtherAssetRoute(index: Int): String =
    other.routes.OtherAssetDescriptionController.onPageLoad(NormalMode, index, fakeDraftId).url

  def removeAssetYesNoRoute(index: Int): String =
    routes.RemoveAssetYesNoController.onPageLoad(index, fakeDraftId).url

  "AddAssetViewHelper" when {

    ".row" must {

      "generate Nil for no user answers" in {
        val rows = new AddAssetViewHelper(emptyUserAnswers, NormalMode, fakeDraftId).rows
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
          .set(WhatKindOfAssetPage(5), Other).success.value
          .set(OtherAssetDescriptionPage(5), "Description").success.value
          .set(WhatKindOfAssetPage(6), Partnership).success.value
          .set(PartnershipDescriptionPage(6), "Partnership Description").success.value

        val rows = new AddAssetViewHelper(userAnswers, NormalMode, fakeDraftId).rows
        rows.inProgress mustBe List(
          AddRow("No name added", typeLabel = "Shares", changeSharesAssetRoute(0), removeAssetYesNoRoute(0)),
          AddRow("No address added", typeLabel = "Property or Land", changePropertyOrLandAssetRoute(2), removeAssetYesNoRoute(2)),
          AddRow("No description added", typeLabel = "Property or Land", changePropertyOrLandAssetRoute(3), removeAssetYesNoRoute(3)),
          AddRow("No address or description added", typeLabel = "Property or Land", changePropertyOrLandAssetRoute(4), removeAssetYesNoRoute(4)),
          AddRow("Description", typeLabel = "Other", changeOtherAssetRoute(5), removeAssetYesNoRoute(5)),
          AddRow("Partnership Description", typeLabel = "Partnership", changePartnershipAssetRoute(6), removeAssetYesNoRoute(6))
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
          .set(WhatKindOfAssetPage(4), Other).success.value
          .set(OtherAssetDescriptionPage(4), "Description").success.value
          .set(OtherAssetValuePage(4), "4000").success.value
          .set(AssetStatus(4), Completed).success.value
          .set(WhatKindOfAssetPage(5), Partnership).success.value
          .set(PartnershipDescriptionPage(5), "Partnership Description").success.value
          .set(PartnershipStartDatePage(5), LocalDate.now(ZoneOffset.UTC)).success.value
          .set(AssetStatus(5), Completed).success.value

        val rows = new AddAssetViewHelper(userAnswers, NormalMode, fakeDraftId).rows
        rows.complete mustBe List(
          AddRow("Share Company Name", typeLabel = "Shares", changeSharesAssetRoute(0), removeAssetYesNoRoute(0)),
          AddRow("£200", typeLabel = "Money", changeMoneyAssetRoute(1), removeAssetYesNoRoute(1)),
          AddRow("line 1", typeLabel = "Property or Land", changePropertyOrLandAssetRoute(2), removeAssetYesNoRoute(2)),
          AddRow("1 hectare of land", typeLabel = "Property or Land", changePropertyOrLandAssetRoute(3), removeAssetYesNoRoute(3)),
          AddRow("Description", typeLabel = "Other", changeOtherAssetRoute(4), removeAssetYesNoRoute(4)),
          AddRow("Partnership Description", typeLabel = "Partnership", changePartnershipAssetRoute(5), removeAssetYesNoRoute(5))
        )
        rows.inProgress mustBe Nil
      }

    }
  }
}
