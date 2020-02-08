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

import models.core.pages.{Address, IndividualOrBusiness}
import play.api.libs.json.{JsError, JsSuccess, Reads, __}

final case class TrusteeOrganisation(override val isLead : Boolean,
                                    name: String,
                                     utr: Option[String],
                                     address: Option[Address]) extends Trustee


object TrusteeOrganisation {

  import play.api.libs.functional.syntax._

  implicit lazy val reads: Reads[TrusteeOrganisation] = {

    val trusteeReads: Reads[TrusteeOrganisation] = (
      (__ \ "name").read[String] and
        (__ \ "utr").readNullable[String] and
        (__ \ "address").readNullable[Address]
      )((name, utr, address) => TrusteeOrganisation(isLead = false, name, utr, address))

    ((__ \ "isThisLeadTrustee").read[Boolean] and
      (__ \ "individualOrBusiness").read[IndividualOrBusiness]) ((_, _)).flatMap[(Boolean, IndividualOrBusiness)] {
      case (isLead, individualOrBusiness) =>
        if (individualOrBusiness == IndividualOrBusiness.Business && !isLead) {
          Reads(_ => JsSuccess((isLead, individualOrBusiness)))
        } else {
          Reads(_ => JsError("trustee organisation must not be a `individual` or a `lead`"))
        }
    }.andKeep(trusteeReads)

  }
}