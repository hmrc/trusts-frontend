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

package pages.shares

import models.UserAnswers
import pages.QuestionPage
import pages.entitystatus.AssetStatus
import play.api.libs.json.JsPath
import sections.Assets

import scala.util.Try

final case class SharesInAPortfolioPage(index : Int) extends QuestionPage[Boolean] {

  override def path: JsPath = Assets.path \ index \ toString

  override def toString: String = "sharesInAPortfolio"

  override def cleanup(value: Option[Boolean], userAnswers: UserAnswers): Try[UserAnswers] = {
    value match {
      case Some(true) =>

        userAnswers.remove(SharesOnStockExchangePage(index))
          .flatMap(_.remove(ShareCompanyNamePage(index)))
          .flatMap(_.remove(ShareClassPage(index)))
          .flatMap(_.remove(ShareQuantityInTrustPage(index)))
          .flatMap(_.remove(ShareValueInTrustPage(index)))
          .flatMap(_.remove(AssetStatus(index)))

      case Some(false) =>

        userAnswers.remove(SharePortfolioNamePage(index))
          .flatMap(_.remove(SharePortfolioOnStockExchangePage(index)))
          .flatMap(_.remove(SharePortfolioQuantityInTrustPage(index)))
          .flatMap(_.remove(SharePortfolioValueInTrustPage(index)))
          .flatMap(_.remove(AssetStatus(index)))

      case _ => super.cleanup(value, userAnswers)
    }
  }
}
