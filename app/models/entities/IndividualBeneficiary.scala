package models.entities

import java.time.LocalDate

import models.FullName
import play.api.libs.json.{Format, Json}


final case class IndividualBeneficiary(name: FullName, dateOfBirth: LocalDate,
                                       nationalInsuranceNumber: String, income: String
                                      ) {
}

object IndividualBeneficiary {
  implicit val classFormat: Format[IndividualBeneficiary] = Json.format[IndividualBeneficiary]
}



