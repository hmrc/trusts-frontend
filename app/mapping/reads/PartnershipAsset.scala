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

package mapping.reads

import java.time.LocalDate

import models.registration.pages.WhatKindOfAsset
import models.registration.pages.WhatKindOfAsset.Partnership
import play.api.libs.json.{JsError, JsSuccess, Reads, __}
import play.api.libs.functional.syntax._

final case class PartnershipAsset(
                             override val whatKindOfAsset: WhatKindOfAsset,
                             description: String,
                             startDate: LocalDate
                           ) extends Asset

object PartnershipAsset {

  implicit lazy val reads: Reads[PartnershipAsset] = {

    val partnershipReads: Reads[PartnershipAsset] = (
      (__ \ "partnershipDescription").read[String] and
        (__ \ "partnershipStartDate").read[LocalDate] and
        (__ \ "whatKindOfAsset").read[WhatKindOfAsset]
      )((description, startDate, kind) => PartnershipAsset(kind, description, startDate))

    (__ \ "whatKindOfAsset").read[WhatKindOfAsset].flatMap[WhatKindOfAsset] {
      whatKindOfAsset: WhatKindOfAsset =>
        if (whatKindOfAsset == Partnership) {
          Reads(_ => JsSuccess(whatKindOfAsset))
        } else {
          Reads(_ => JsError("partnership asset must be of type `Partnership`"))
        }
    }.andKeep(partnershipReads)

  }

}

