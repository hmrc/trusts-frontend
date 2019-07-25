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

package mapping.reads

import models.WhatKindOfAsset.Shares
import models.{ShareClass, Status, WhatKindOfAsset}
import play.api.libs.json._

final case class ShareAsset(
                           listedOnTheStockExchange: Boolean,
                           shareCompanyName: String,
                           sharesInAPortfolio: Boolean,
                           quantityInTheTrust: String,
                           value: String,
                           whatKindOfAsset: WhatKindOfAsset,
                           `class`: ShareClass,
                           status: Status
                           ) extends Asset

object ShareAsset {

  import play.api.libs.functional.syntax._

  implicit lazy val reads: Reads[ShareAsset] = {

    val shareReads : Reads[ShareAsset] = Json.reads[ShareAsset]

    (__ \ "whatKindOfAsset").read[WhatKindOfAsset].flatMap[WhatKindOfAsset] {
      whatKindOfAsset: WhatKindOfAsset =>
        if (whatKindOfAsset == Shares) {
          Reads(_ => JsSuccess(whatKindOfAsset))
        } else {
          Reads(_ => JsError("share asset must be of type `Shares`"))
        }
    }.andKeep(shareReads)

  }

}
