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

import models.WhatKindOfAsset.Money
import models.{UserAnswers, WhatKindOfAsset}
import models.entities.Asset
import pages.{Assets, Trustees}
import play.api.i18n.Messages
import viewmodels.{AssetRow, AssetRows, TrusteeRow, TrusteeRows}

class AddAssetViewHelper(userAnswers: UserAnswers)(implicit  messages: Messages) {

  private def parseAssetValue(value :  Option[String],isMoney :Boolean) : String = {
    value match {
      case Some(x) if isMoney =>s"£$x"
      case Some(x) =>s"$x"

      case None => ""
    }
  }

  private def parseAssetType(whatKindOfAsset: Option[WhatKindOfAsset]): String = {
    whatKindOfAsset match {
      case Some(x) =>
       x.toString
      case None =>
        ""
    }
  }

  private def parseAsset(asset: Asset) : TrusteeRow = {
    TrusteeRow(
      parseAssetValue(asset.assetMoneyValue,asset.isMoney),
      parseAssetType(asset.whatKindOfAsset),
      "#",
      "#"
    )
  }

  def rows : TrusteeRows = {
    val assets = userAnswers.get(Assets).toList.flatten

    val complete = assets.filter(_.isComplete).map(parseAsset)

    val inProgress = assets.filterNot(_.isComplete).map(parseAsset)

    TrusteeRows(inProgress, complete)
  }

}
