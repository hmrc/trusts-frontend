package models

import play.api.libs.json._
import viewmodels.RadioOption

sealed trait AddABeneficiary

object AddABeneficiary extends Enumerable.Implicits {

  case object 1 extends WithName("1") with AddABeneficiary
  case object 2 extends WithName("2") with AddABeneficiary

  val values: List[AddABeneficiary] = List(
    1, 2
  )

  val options: List[RadioOption] = values.map {
    value =>
      RadioOption("addABeneficiary", value.toString)
  }

  implicit val enumerable: Enumerable[AddABeneficiary] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
