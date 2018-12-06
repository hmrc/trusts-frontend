/*
 * Copyright 2018 HM Revenue & Customs
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

sealed trait NonResidentType

object NonResidentType extends Enumerable.Implicits {

  case object 1 extends WithName("1") with NonResidentType
  case object 2 extends WithName("2") with NonResidentType
  case object 3 extends WithName("3") with NonResidentType

  val values: Set[NonResidentType] = Set(
    1, 2, 3
  )

  val options: Set[RadioOption] = values.map {
    value =>
      RadioOption("nonresidentType", value.toString)
  }

  implicit val enumerable: Enumerable[NonResidentType] =
    Enumerable(values.toSeq.map(v => v.toString -> v): _*)
}
