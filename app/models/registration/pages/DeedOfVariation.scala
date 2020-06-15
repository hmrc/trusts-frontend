/*
 * Copyright 2020 HM Revenue & Customs
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

package models.registration.pages

import models.{Enumerable, WithName}
import viewmodels.RadioOption

sealed trait DeedOfVariation

object DeedOfVariation extends Enumerable.Implicits {

  case object ReplacedWill extends WithName("ReplacedWill") with DeedOfVariation
  case object ReplaceAbsolute extends WithName("ReplaceAbsolute") with DeedOfVariation

  val values: List[DeedOfVariation] = List(
    ReplacedWill,
    ReplaceAbsolute
  )

  val options: List[RadioOption] = values.map {
    value =>
      RadioOption("howDeedOfVariationCreated", value.toString)
  }

  implicit val enumerable: Enumerable[DeedOfVariation] =
    Enumerable(values.map(v => v.toString -> v): _*)
}