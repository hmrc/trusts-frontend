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

sealed trait NonResidentType

object NonResidentType extends Enumerable.Implicits {

  case object Domiciled extends WithName("non-resident-domiciled") with NonResidentType
  case object NonDomiciled extends WithName("non-resident-non-domiciled") with NonResidentType
  case object CeaseResident extends WithName("non-resident-cease-resident") with NonResidentType

  val values: List[NonResidentType] = List(
    Domiciled, NonDomiciled, CeaseResident
  )

  val options: List[RadioOption] = values.map {
    value =>
      RadioOption("nonresidentType", value.toString)
  }

  implicit val enumerable: Enumerable[NonResidentType] =
    Enumerable(values.map(v => v.toString -> v): _*)


  def toDES(value : NonResidentType) = value match {
    case Domiciled => "Non Resident Domiciled"
    case NonDomiciled => "Non Resident Non Domiciled"
    case CeaseResident => "Non Resident Cease Resident"
  }
}
