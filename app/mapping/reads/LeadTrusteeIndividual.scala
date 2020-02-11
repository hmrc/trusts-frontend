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

import models.core.pages.IndividualOrBusiness.Individual
import models.core.pages.{Address, FullName}
import play.api.libs.json.{JsError, JsSuccess, Reads, __}

final case class LeadTrusteeIndividual(override val isLead : Boolean = true,
                                       name: FullName,
                                       dateOfBirth: LocalDate,
                                       isUKCitizen : Boolean,
                                       nino : Option[String],
                                       passport: Option[String],
                                       liveInUK: Boolean,
                                       address : Address,
                                       telephoneNumber : String
                                      ) extends Trustee

object LeadTrusteeIndividual {

  import play.api.libs.functional.syntax._

  implicit lazy val reads: Reads[LeadTrusteeIndividual] = {

    val leadTrusteeReads: Reads[LeadTrusteeIndividual] = (
      (__ \ "isThisLeadTrustee").read[Boolean] and
        (__ \ "name").read[FullName] and
        (__ \ "dateOfBirth").read[LocalDate] and
        (__ \ "isUKCitizen").read[Boolean] and
        (__ \ "nino").readNullable[String] and
        (__ \ "passport").readNullable[String] and
        (__ \ "addressUKYesNo").read[Boolean] and
        (__ \ "address").read[Address] and
        (__ \ "telephoneNumber").read[String]
      )(LeadTrusteeIndividual.apply _)

    ((__ \ "isThisLeadTrustee").read[Boolean] and
      (__ \ "individualOrBusiness").read[String]) ((_, _)).flatMap[(Boolean, String)] {
      case (isLead, individualOrBusiness) =>
        if (individualOrBusiness == Individual.toString && isLead) {
          Reads(_ => JsSuccess((isLead, individualOrBusiness)))
        } else {
          Reads(_ => JsError("lead trustee individual must not be a `business` or a normal trustee"))
        }
    }.andKeep(leadTrusteeReads)

  }

}