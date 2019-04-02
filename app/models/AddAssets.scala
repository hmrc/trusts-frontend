package models

import play.api.libs.json._
import viewmodels.RadioOption

sealed trait AddAssets

object AddAssets extends Enumerable.Implicits {

  case object Option1 extends WithName("option1") with AddAssets
  case object Option2 extends WithName("option2") with AddAssets

  val values: List[AddAssets] = List(
    Option1, Option2
  )

  val options: List[RadioOption] = values.map {
    value =>
      RadioOption("addAssets", value.toString)
  }

  implicit val enumerable: Enumerable[AddAssets] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
