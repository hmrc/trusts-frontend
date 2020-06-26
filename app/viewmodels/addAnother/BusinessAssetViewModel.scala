/*
 * Copyright 2020 HM Revenue & Customs
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

import models.core.pages.Address
import models.registration.pages.Status.InProgress
import models.registration.pages.WhatKindOfAsset.Business
import models.registration.pages.{Status, WhatKindOfAsset}

final case class BusinessAssetViewModel(`type`: WhatKindOfAsset,
                                        name: String,
                                        description: Option[String],
                                        addressUkYesNo: Option[Boolean],
                                        address: Option[Address],
                                        currentValue: Option[String],
                                        override val status: Status) extends AssetViewModel

object BusinessAssetViewModel {

  import play.api.libs.functional.syntax._
  import play.api.libs.json._

  implicit lazy val reads: Reads[BusinessAssetViewModel] = {

    val businessReads: Reads[BusinessAssetViewModel] =
      ((__ \ "assetName").read[String] and
        (__ \ "assetDescription").readNullable[String] and
        (__ \ "addressUkYesNo").readNullable[Boolean] and
        (__ \ "address").readNullable[Address] and
        (__ \ "currentValue").readNullable[String] and
        (__ \ "status").readWithDefault[Status](InProgress)
        )((name, description, addressUkYesNo, address, value, status) =>
        BusinessAssetViewModel(Business, name, description, addressUkYesNo, address, value, status))

    (__ \ "whatKindOfAsset").read[WhatKindOfAsset].flatMap[WhatKindOfAsset] {
      whatKindOfAsset: WhatKindOfAsset =>
        if (whatKindOfAsset == Business) {
          Reads(_ => JsSuccess(whatKindOfAsset))
        } else {
          Reads(_ => JsError("business asset must be of type `Business`"))
        }
    }.andKeep(businessReads)

  }

}