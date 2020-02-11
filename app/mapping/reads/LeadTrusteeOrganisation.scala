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
import models.core.pages.IndividualOrBusiness.Business
import play.api.libs.json.{JsError, JsSuccess, Reads, __}

final case class LeadTrusteeOrganisation(override val isLead : Boolean = true,
                                       name: String,
                                       isUKBusiness : Boolean,
                                       utr : Option[String],
                                       liveInUK: Boolean,
                                       address : Address,
                                       telephoneNumber : String
                                      ) extends Trustee

object LeadTrusteeOrganisation {

  import play.api.libs.functional.syntax._

  implicit lazy val reads: Reads[LeadTrusteeOrganisation] = {

    val leadTrusteeReads: Reads[LeadTrusteeOrganisation] = (
      (__ \ "isThisLeadTrustee").read[Boolean] and
        (__ \ "name").read[String] and
        (__ \ "isUKBusiness").read[Boolean] and
        (__ \ "utr").readNullable[String] and
        (__ \ "addressUKYesNo").read[Boolean] and
        (__ \ "address").read[Address] and
        (__ \ "telephoneNumber").read[String]
      )(LeadTrusteeOrganisation.apply _)

    ((__ \ "isThisLeadTrustee").read[Boolean] and
      (__ \ "individualOrBusiness").read[String]) ((_, _)).flatMap[(Boolean, String)] {
      case (isLead, individualOrBusiness) =>
        if (individualOrBusiness == Business.toString && isLead) {
          Reads(_ => JsSuccess((isLead, individualOrBusiness)))
        } else {
          Reads(_ => JsError("lead trustee organisation must not be an `individual` or a normal trustee"))
        }
    }.andKeep(leadTrusteeReads)

  }

}