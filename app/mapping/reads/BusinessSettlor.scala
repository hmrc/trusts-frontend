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

import models.core.pages.Address
import play.api.libs.json.{JsResult, JsSuccess, Reads, __}

final case class BusinessSettlor(name: String,
                                 utr: Option[String],
                                 address : Option[Address],
                                 companyType: Option[String],
                                 companyTime: Option[Boolean]) extends Settlor

object BusinessSettlor {
  import play.api.libs.functional.syntax._

  implicit lazy val reads: Reads[BusinessSettlor] = {
    ((__ \ "businessName").read[String] and
      (__ \ "utr").readNullable[String] and
      readAddress() and
      (__ \ "companyType").readNullable[String] and
      (__ \ "companyTime").readNullable[Boolean])(BusinessSettlor.apply _)
  }

  private def readAddress(): Reads[Option[Address]] = {
    (__ \ "ukAddress").read[Address].map(Some(_): Option[Address]) orElse
      (__ \ "internationalAddress").read[Address].map(Some(_): Option[Address]) orElse
    Reads(_ => JsSuccess(None: Option[Address]))
  }
}

