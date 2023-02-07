/*
 * Copyright 2023 HM Revenue & Customs
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

package models.core.http

import java.time.LocalDate

import models.core.pages.FullName
import play.api.libs.json.{Format, Json}

case class LeadTrusteeType(leadTrusteeInd : Option[LeadTrusteeIndType] = None,
                           leadTrusteeOrg : Option[LeadTrusteeOrgType] = None)

object LeadTrusteeType {
  implicit val leadTrusteeTypeReads:Format[LeadTrusteeType] = Json.format[LeadTrusteeType]
}

case class LeadTrusteeIndType (name: FullName,
                               dateOfBirth: LocalDate ,
                               phoneNumber: String,
                               email: Option[String] = None,
                               identification: IdentificationType)

object LeadTrusteeIndType {
  implicit val leadTrusteeIndTypeFormat: Format[LeadTrusteeIndType] = Json.format[LeadTrusteeIndType]
}

case class LeadTrusteeOrgType(name: String,
                              phoneNumber: String,
                              email: Option[String] = None,
                              identification: IdentificationOrgType)

object LeadTrusteeOrgType {
  implicit val leadTrusteeOrgTypeFormat: Format[LeadTrusteeOrgType] = Json.format[LeadTrusteeOrgType]
}
