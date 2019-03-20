package models

import play.api.libs.json._
import viewmodels.RadioOption

sealed trait WhatKindOfAsset

object WhatKindOfAsset extends Enumerable.Implicits {

  case object One extends WithName("one") with WhatKindOfAsset
  case object Two extends WithName("two") with WhatKindOfAsset

  val values: Set[WhatKindOfAsset] = Set(
    One, Two
  )

  val options: Set[RadioOption] = values.map {
    value =>
      RadioOption("whatKindOfAsset", value.toString)
  }

  implicit val enumerable: Enumerable[WhatKindOfAsset] =
    Enumerable(values.toSeq.map(v => v.toString -> v): _*)
}
