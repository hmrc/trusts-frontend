package models

import play.api.libs.json._

case class SettlorIndividualAddressUK (field1: String, field2: String)

object SettlorIndividualAddressUK {
  implicit val format = Json.format[SettlorIndividualAddressUK]
}
