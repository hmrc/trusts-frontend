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

import models.UserAnswers
import models.WhatKindOfAsset.Money
import pages.{AssetMoneyValuePage, WhatKindOfAssetPage}
import viewmodels.addAnother.{AssetViewModel, MoneyAssetViewModel}

import scala.util.Try

class AssetMapper extends Mapping[Assets] {

  override def build(userAnswers: UserAnswers): Option[Assets] = {
    val allMoney = buildMoney(userAnswers)

    allMoney.map {
      money =>
        Assets(
          monetary = Some(money),
          propertyOrLand = None,
          shares = None,
          business = None,
          partnerShip = None,
          other = None
        )
    }
  }

  private def buildMoney(userAnswers: UserAnswers, index : Int) : Option[List[AssetMonetaryAmount]] = {
    userAnswers.get(WhatKindOfAssetPage(index)) match {
      case Some(Money) =>
        for {
          money <- userAnswers.get(AssetMoneyValuePage(0))
          valueAsLong <- Try(money.toLong).toOption
        } yield {
          List(
            AssetMonetaryAmount(valueAsLong)
          )
        }
      case _ => None
    }
  }
}
