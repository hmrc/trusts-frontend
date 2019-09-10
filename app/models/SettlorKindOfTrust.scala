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

import viewmodels.RadioOption

sealed trait SettlorKindOfTrust

object SettlorKindOfTrust extends Enumerable.Implicits {

  case object Lifetime extends WithName("lifetime") with SettlorKindOfTrust
  case object Deed extends WithName("deed") with SettlorKindOfTrust
  case object Employees extends WithName("employees") with SettlorKindOfTrust
  case object Building extends WithName("building") with SettlorKindOfTrust
  case object Repair extends WithName("repair") with SettlorKindOfTrust

  val values: List[SettlorKindOfTrust] = List(
    Lifetime, Deed, Employees, Building, Repair
  )

  val options: List[RadioOption] = values.map {
    value =>
      RadioOption("settlorKindOfTrust", value.toString)
  }

  implicit val enumerable: Enumerable[SettlorKindOfTrust] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
