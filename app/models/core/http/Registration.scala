/*
 * Copyright 2024 HM Revenue & Customs
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

import models.core.pages.FullName
import play.api.libs.functional.syntax._
import play.api.libs.json._

/**
  * Trust Registration API Schema - definitions models below
  */

case class Registration(matchData: Option[MatchData],
                        correspondence: Correspondence,
                        declaration: Declaration,
                        trust: JsValue = Json.obj(),
                        agentDetails: Option[JsValue])

object Registration {
 implicit val registrationReads : Format[Registration] = Json.format[Registration]
}

case class MatchData(utr: String,
                     name: String,
                     postCode: Option[String])

object MatchData {
  implicit val matchDataFormat: Format[MatchData] = Json.format[MatchData]

  val writes: Writes[MatchData] =
    ((__ \ "utr").write[String] and
      (__ \ "name").write[String] and
      (__ \ "postcode").writeNullable[String]
      ).apply(unlift(MatchData.unapply))
}

case class Correspondence(name: String)

object Correspondence {
  implicit val correspondenceFormat : Format[Correspondence] = Json.format[Correspondence]
}

case class Declaration(name: FullName,
                       address: AddressType)

object Declaration {
  implicit val declarationFormat: Format[Declaration] = Json.format[Declaration]
}
