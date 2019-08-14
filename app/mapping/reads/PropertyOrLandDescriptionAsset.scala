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

import models.WhatKindOfAsset.PropertyOrLand
import models.{Status, WhatKindOfAsset}
import play.api.libs.json._

final case class PropertyOrLandDescriptionAsset(override val whatKindOfAsset: WhatKindOfAsset,
                                                propertyOrLandDescription: String,
                                                propertyOrLandTotalValue: String,
                                                trustOwnAllThePropertyOrLand: Boolean,
                                                propertyLandValueTrust: String,
                                                status: Status
                                    ) extends Asset with PropertyOrLandAsset

object PropertyOrLandDescriptionAsset {

  import play.api.libs.functional.syntax._

  implicit lazy val reads: Reads[PropertyOrLandDescriptionAsset] = {

    val propertyOrLandReads : Reads[PropertyOrLandDescriptionAsset] = Json.reads[PropertyOrLandDescriptionAsset]

    (
      (__ \ "whatKindOfAsset").read[WhatKindOfAsset] and
        (__ \ "propertyOrLandAddressYesNo").read[Boolean]
      )((_, _)).flatMap[(WhatKindOfAsset, Boolean)] {
      case (whatKindOfAsset, hasAddress) =>
        if (whatKindOfAsset == PropertyOrLand && !hasAddress) {
          Reads(_ => JsSuccess((whatKindOfAsset, hasAddress)))
        } else {
          Reads(_ => JsError("property or land asset must be of type `PropertyOrLand`"))
        }
    }.andKeep(propertyOrLandReads)

  }

}






