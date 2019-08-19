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
import models.{InternationalAddress, Status, UKAddress, WhatKindOfAsset}

sealed trait PropertyOrLandAssetViewModel extends AssetViewModel

final case class PropertyOrLandAssetUKAddressViewModel(`type` : WhatKindOfAsset,
                                                       address : Option[String],
                                                       override val status : Status) extends PropertyOrLandAssetViewModel

final case class PropertyOrLandAssetInternationalAddressViewModel(`type` : WhatKindOfAsset,
                                                       address : Option[String],
                                                       override val status : Status) extends PropertyOrLandAssetViewModel

final case class PropertyOrLandAssetDescriptionViewModel(`type` : WhatKindOfAsset,
                                                       description : Option[String],
                                                       override val status : Status) extends PropertyOrLandAssetViewModel

object PropertyOrLandAssetViewModel {

  import play.api.libs.functional.syntax._
  import play.api.libs.json._

  implicit lazy val reads: Reads[PropertyOrLandAssetViewModel] = {

    val ukReads: Reads[PropertyOrLandAssetViewModel] =
    {
      (__ \ "address").read[UKAddress].flatMap{ _ =>
        ((__ \ "address" \ "line1").readNullable[String] and
          (__ \ "status").readWithDefault[Status](InProgress)
          )((address, status) => {
          PropertyOrLandAssetUKAddressViewModel(
            PropertyOrLand,
            address,
            status)
        })
      }
    }

    val internationalReads: Reads[PropertyOrLandAssetViewModel] =
    {
      (__ \ "address").read[InternationalAddress].flatMap{ _ =>
        ((__ \ "address" \ "line1").readNullable[String] and
          (__ \ "status").readWithDefault[Status](InProgress)
          )((address, status) => {
          PropertyOrLandAssetInternationalAddressViewModel(
            PropertyOrLand,
            address,
            status)
        })
      }
    }

    val descriptionReads: Reads[PropertyOrLandAssetViewModel] =
    {
        ((__ \ "propertyOrLandDescription").readNullable[String] and
          (__ \ "status").readWithDefault[Status](InProgress)
          )((description, status) => {
          PropertyOrLandAssetDescriptionViewModel(
            PropertyOrLand,
            description,
            status)
        })
    }

    (__ \ "whatKindOfAsset").read[WhatKindOfAsset].flatMap[WhatKindOfAsset] {
      whatKindOfAsset: WhatKindOfAsset =>
        if (whatKindOfAsset == PropertyOrLand) {
          Reads(_ => JsSuccess(whatKindOfAsset))
        } else {
          Reads(_ => JsError("Property or Land asset must be of type `PropertyOrLand`"))
        }
    } andKeep ukReads orElse internationalReads orElse descriptionReads


  }

}
