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

import models.registration.pages.Status.InProgress
import models.registration.pages.WhatKindOfAsset.Other
import models.registration.pages.{Status, WhatKindOfAsset}
import play.api.libs.functional.syntax._
import play.api.libs.json._

final case class OtherAssetViewModel(`type`: WhatKindOfAsset,
                                     description: String,
                                     override val status: Status) extends AssetViewModel

object OtherAssetViewModel {

  implicit lazy val reads: Reads[OtherAssetViewModel] = {

    val otherReads: Reads[OtherAssetViewModel] =
      ((__ \ "otherAssetDescription").read[String] and
        (__ \ "status").readWithDefault[Status](InProgress)
        )((description, status) => OtherAssetViewModel(Other, description, status))

    (__ \ "whatKindOfAsset").read[WhatKindOfAsset].flatMap[WhatKindOfAsset] {
      whatKindOfAsset: WhatKindOfAsset =>
        if (whatKindOfAsset == Other) {
          Reads(_ => JsSuccess(whatKindOfAsset))
        } else {
          Reads(_ => JsError("other asset must be of type `Other`"))
        }
    }.andKeep(otherReads)

  }

}