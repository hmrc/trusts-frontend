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

package sections

import models.UserAnswers
import pages.entitystatus.AssetStatus
import pages._
import play.api.libs.json.JsPath

import scala.util.Try

final case class RemoveShareAsset(index : Int) extends QuestionPage[Nothing]{

    override def path: JsPath = JsPath

    override def toString: String = "moneyAsset"

    override def cleanup(value: Option[Nothing], userAnswers: UserAnswers): Try[UserAnswers] = {
        userAnswers.remove(SharesInAPortfolioPage(index))
          .flatMap(_.remove(SharesOnStockExchangePage(index)))
          .flatMap(_.remove(ShareClassPage(index)))
          .flatMap(_.remove(ShareQuantityInTrustPage(index)))
          .flatMap(_.remove(ShareValueInTrustPage(index)))
          .flatMap(_.remove(SharePortfolioNamePage(index)))
          .flatMap(_.remove(SharePortfolioOnStockExchangePage(index)))
          .flatMap(_.remove(SharePortfolioQuantityInTrustPage(index)))
          .flatMap(_.remove(SharePortfolioValueInTrustPage(index)))
          .flatMap(_.remove(AssetStatus(index)))
    }

    def apply(userAnswers: UserAnswers) = cleanup(None, userAnswers)
}
