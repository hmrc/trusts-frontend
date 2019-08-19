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

package viewmodels.addAnother

import models.Status.InProgress
import models.WhatKindOfAsset.PropertyOrLand
import models.{Address, Status, WhatKindOfAsset}
import play.api.libs.json.{JsError, JsSuccess, Reads, __}

final case class PropertyOrLandAddressViewModel(`type` : WhatKindOfAsset,
                                                addressIsUK: Option[Boolean],
                                                address: Option[Address],
                                                override val status : Status) extends AssetViewModel


object PropertyOrLandAddressViewModel {

  import play.api.libs.functional.syntax._

  implicit lazy val reads: Reads[PropertyOrLandAddressViewModel] = {

    val reads: Reads[PropertyOrLandAddressViewModel] =
      (
          (__ \ "propertyOrLandUKAddressYesNo").readNullable[Boolean] and
          (__ \ "address").readNullable[Address] and
          (__ \ "status").readWithDefault[Status](InProgress)
        )(
        (addressIsUK, address, status) => {
          PropertyOrLandAddressViewModel(PropertyOrLand, addressIsUK, address, status)
        })

    (
      (__ \ "whatKindOfAsset").read[WhatKindOfAsset] and
        (__ \ "propertyOrLandAddressYesNo").read[Boolean]
      ){
      (whatKindOfAsset, hasAddress) =>
        if (whatKindOfAsset == PropertyOrLand && hasAddress) {
          Reads(_ => JsSuccess((whatKindOfAsset, hasAddress)))
        } else {
          Reads(_ =>
            JsError("Property or Land description asset must be of type `PropertyOrLand` and have an address"))
        }
    } andKeep reads
  }
}
