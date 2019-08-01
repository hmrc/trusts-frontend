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
import models.WhatKindOfAsset.Shares
import pages.WhatKindOfAssetPage
import pages.entitystatus.AssetStatus
import pages.shares._
import viewmodels.AddRow

class AddAssetViewHelperSpec extends SpecBase {

  "AddAssetViewHelper" when {

    ".row" must {

      "generate Nil for no user answers" in {
        val rows = new AddAssetViewHelper(emptyUserAnswers).rows
        rows.inProgress mustBe Nil
        rows.complete mustBe Nil
      }

      "generate rows from user answers for assets in progress" in {

        val userAnswers = emptyUserAnswers
          .set(WhatKindOfAssetPage(0), Shares).success.value
          .set(SharesInAPortfolioPage(0), true).success.value

        val rows = new AddAssetViewHelper(userAnswers).rows
        rows.inProgress mustBe List(
          AddRow("No name added", typeLabel = "Shares", "#", "#")
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

        val rows = new AddAssetViewHelper(userAnswers).rows
        rows.complete mustBe List(
          AddRow("Share Company Name", typeLabel = "Shares", "#", "#")
        )
        rows.inProgress mustBe Nil
      }

    }
  }
}
