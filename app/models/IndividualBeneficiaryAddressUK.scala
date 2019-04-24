package models

import play.api.libs.json._

case class IndividualBeneficiaryAddressUK (field1: String, field2: String)

object IndividualBeneficiaryAddressUK {
  implicit val format = Json.format[IndividualBeneficiaryAddressUK]
}
