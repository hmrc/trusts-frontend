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

sealed trait WhatTypeOfBeneficiary

object WhatTypeOfBeneficiary extends Enumerable.Implicits {

  case object Option1 extends WithName("option1") with WhatTypeOfBeneficiary
  case object Option2 extends WithName("option2") with WhatTypeOfBeneficiary

  val values: List[WhatTypeOfBeneficiary] = List(
    Option1, Option2
  )

  val options: List[RadioOption] = values.map {
    value =>
      RadioOption("whatTypeOfBeneficiary", value.toString)
  }

  implicit val enumerable: Enumerable[WhatTypeOfBeneficiary] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
