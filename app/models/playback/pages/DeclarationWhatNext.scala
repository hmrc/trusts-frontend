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

package models.playback.pages

import models.{Enumerable, WithName}
import viewmodels.RadioOption

sealed trait DeclarationWhatNext

object DeclarationWhatNext extends Enumerable.Implicits {

  case object DeclareTheTrustIsUpToDate extends WithName("declare") with DeclarationWhatNext
  case object MakeChanges extends WithName("makeChanges") with DeclarationWhatNext
  case object CloseTrust extends WithName("closeTrust") with DeclarationWhatNext

  val values: List[DeclarationWhatNext] = List(
    DeclareTheTrustIsUpToDate, MakeChanges, CloseTrust
  )

  val options: List[RadioOption] = values.map {
    value =>
      RadioOption("declarationWhatNext", value.toString)
  }

  implicit val enumerable: Enumerable[DeclarationWhatNext] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
