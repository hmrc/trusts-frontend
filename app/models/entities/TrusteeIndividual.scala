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

package models.entities

import java.time.LocalDate

import models.FullName
import play.api.libs.json._

final case class TrusteeIndividual(name: FullName, dateOfBirth: LocalDate) extends Trustee

object TrusteeIndividual {

  import play.api.libs.functional.syntax._

  implicit lazy val reads: Reads[TrusteeIndividual] = {

    val trusteeReads: Reads[TrusteeIndividual] = (
      (__ \ "name").read[FullName] and
        (__ \ "dateOfBirth").read[LocalDate]
      )(TrusteeIndividual.apply _)

    ((__ \ "isThisLeadTrustee").read[Boolean] and
      (__ \ "individualOrBusiness").read[String]) ((_, _)).flatMap[(Boolean, String)] {
      case (isLead, individualOrBusiness) =>
        if (individualOrBusiness == "individual" && !isLead) {
          Reads(_ => JsSuccess((isLead, individualOrBusiness)))
        } else {
          Reads(_ => JsError("trustee individual must not be a `business` or a `lead`"))
        }
    }.andKeep(trusteeReads)

  }
}
