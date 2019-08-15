package models

import play.api.libs.json._

case class SettlorIndividualAddressInternational (field1: String, field2: String)

object SettlorIndividualAddressInternational {
  implicit val format = Json.format[SettlorIndividualAddressInternational]
}
