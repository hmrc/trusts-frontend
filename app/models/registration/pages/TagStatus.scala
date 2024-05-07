/*
 * Copyright 2024 HM Revenue & Customs
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

import models.registration.pages.TagStatus.Completed
import models.{Enumerable, WithName}

sealed trait TagStatus {
  def isCompleted: Boolean = this == Completed
}

object TagStatus extends Enumerable.Implicits {

  case object Completed extends WithName("completed") with TagStatus
  case object InProgress extends WithName("in-progress") with TagStatus
  case object NotStarted extends WithName("not-started") with TagStatus
  case object CannotStartYet extends WithName("cannot-start-yet") with TagStatus
  case object NoActionNeeded extends WithName("no-action-needed") with TagStatus

  val values: Set[TagStatus] = Set(
    Completed, InProgress, NotStarted, CannotStartYet, NoActionNeeded
  )

  implicit val enumerable: Enumerable[TagStatus] =
    Enumerable(values.toSeq.map(v => v.toString -> v): _*)
}
