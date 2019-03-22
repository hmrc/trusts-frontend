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

import viewmodels.{RadioOption, WeightedRadioOption}

sealed trait WhatKindOfAsset

trait WithWeight {
  val weight : Int
}

object WhatKindOfAsset extends Enumerable.Implicits {

  case object Money extends WithName("Money") with WhatKindOfAsset with WithWeight {
    override val weight: Int = 0
  }
  case object PropertyOrLand extends WithName("PropertyOrLand") with WhatKindOfAsset with WithWeight {
    override val weight: Int = 1
  }
  case object Shares extends WithName("Shares") with WhatKindOfAsset with WithWeight {
    override val weight: Int = 2
  }
  case object Business extends WithName("Business") with WhatKindOfAsset with WithWeight {
    override val weight: Int = 3
  }
  case object Partnership extends WithName("Partnership") with WhatKindOfAsset with WithWeight {
    override val weight: Int = 4
  }
  case object Other extends WithName("Other") with WhatKindOfAsset with WithWeight {
    override val weight: Int = 5
  }

  val values: Set[WhatKindOfAsset] = Set(
    Money, PropertyOrLand, Shares, Business, Partnership, Other
  )

  val options: Set[WeightedRadioOption] = values.map {
    value =>
      WeightedRadioOption(value.weight,"whatKindOfAsset", value.toString)
  }

  implicit val enumerable: Enumerable[WhatKindOfAsset] =
    Enumerable(values.toSeq.map(v => v.toString -> v): _*)
}
