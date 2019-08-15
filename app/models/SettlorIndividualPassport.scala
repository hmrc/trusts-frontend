package models

import play.api.libs.json._

case class SettlorIndividualPassport (field1: String, field2: String)

object SettlorIndividualPassport {
  implicit val format = Json.format[SettlorIndividualPassport]
}
