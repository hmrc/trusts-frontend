/*
 * Copyright 2019 HM Revenue & Customs
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

package models

import play.api.libs.json._
import viewmodels.RadioOption

sealed trait WhatKindOfAsset

object WhatKindOfAsset extends Enumerable.Implicits {

  case object One extends WithName("one") with WhatKindOfAsset
  case object Two extends WithName("two") with WhatKindOfAsset
  case object Three extends WithName("three") with WhatKindOfAsset
  case object Four extends WithName("four") with WhatKindOfAsset
  case object Five extends WithName("five") with WhatKindOfAsset
  case object Six extends WithName("six") with WhatKindOfAsset

  val values: Set[WhatKindOfAsset] = Set(
    One, Two, Three, Four, Five, Six
  )

  val options: Set[RadioOption] = values.map {
    value =>
      RadioOption("whatKindOfAsset", value.toString)
  }

  implicit val enumerable: Enumerable[WhatKindOfAsset] =
    Enumerable(values.toSeq.map(v => v.toString -> v): _*)
}
