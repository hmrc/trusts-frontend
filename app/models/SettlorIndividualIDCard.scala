package models

import play.api.libs.json._

case class SettlorIndividualIDCard (field1: String, field2: String)

object SettlorIndividualIDCard {
  implicit val format = Json.format[SettlorIndividualIDCard]
}
