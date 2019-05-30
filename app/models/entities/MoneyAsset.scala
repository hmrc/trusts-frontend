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

package models.entities

import models.WhatKindOfAsset
import models.WhatKindOfAsset.Money
import play.api.libs.json.Reads
import play.api.libs.json._

final case class MoneyAsset(value: String) extends Asset

object MoneyAsset {

  import play.api.libs.functional.syntax._

  implicit lazy val reads: Reads[MoneyAsset] = {

    val moneyReads: Reads[MoneyAsset] = (__ \ "assetMoneyValue").read[String].map(MoneyAsset(_))

    (__ \ "whatKindOfAsset").read[WhatKindOfAsset].flatMap[WhatKindOfAsset] {
      whatKindOfAsset: WhatKindOfAsset =>
        if (whatKindOfAsset == Money) {
          Reads(_ => JsSuccess(whatKindOfAsset))
        } else {
          Reads(_ => JsError("money asset must be of type `Money`"))
        }
    }.andKeep(moneyReads)

  }

}
