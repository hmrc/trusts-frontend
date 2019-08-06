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

import mapping.reads.Assets
import models.WhatKindOfAsset.{Money, Shares}
import models.{UserAnswers, WhatKindOfAsset}
import pages.entitystatus.AssetStatus
import pages.shares._
import play.api.libs.json.JsPath

import scala.util.Try


final case class WhatKindOfAssetPage(index: Int) extends QuestionPage[WhatKindOfAsset] {

  override def path: JsPath = JsPath \ Assets \ index \ toString

  override def toString: String = "whatKindOfAsset"

  override def cleanup(value: Option[WhatKindOfAsset], userAnswers: UserAnswers): Try[UserAnswers] = {
    value match {
      case Some(Money) =>
        removeShare(userAnswers)
      case Some(Shares) =>
        removeMoney(userAnswers)
      case _ => super.cleanup(value, userAnswers)
    }
  }

  private def removeMoney(userAnswers: UserAnswers) : Try[UserAnswers] = {
    userAnswers.remove(AssetMoneyValuePage(index))
      .flatMap(_.remove(AssetStatus(index)))
  }

  private def removeShare(userAnswers: UserAnswers): Try[UserAnswers] = {
    userAnswers.remove(SharesInAPortfolioPage(index))
      .flatMap(_.remove(ShareCompanyNamePage(index)))
      .flatMap(_.remove(SharesOnStockExchangePage(index)))
      .flatMap(_.remove(ShareClassPage(index)))
      .flatMap(_.remove(ShareQuantityInTrustPage(index)))
      .flatMap(_.remove(ShareValueInTrustPage(index)))
      .flatMap(_.remove(SharePortfolioNamePage(index)))
      .flatMap(_.remove(SharePortfolioOnStockExchangePage(index)))
      .flatMap(_.remove(SharePortfolioQuantityInTrustPage(index)))
      .flatMap(_.remove(SharePortfolioValueInTrustPage(index)))
      .flatMap(_.remove(AssetStatus(index)))
      .flatMap(_.remove(ShareCompanyNamePage(index)))
  }
}
