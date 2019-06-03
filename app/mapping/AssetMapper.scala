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
import models.entities.MoneyAsset

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

  private def buildMoney(userAnswers: UserAnswers) : Option[List[AssetMonetaryAmount]] = {
    val assets : List[models.entities.Asset] = userAnswers.get(models.entities.Assets).getOrElse(List.empty[models.entities.Asset])

     assets match {
      case Nil => None
      case list => Some(list.map{
        case x: MoneyAsset=>
          AssetMonetaryAmount(x.value.toLong)

      })
    }
  }
}
