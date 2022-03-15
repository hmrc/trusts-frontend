/*
 * Copyright 2022 HM Revenue & Customs
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

import models.registration.pages.TagStatus
import models.registration.pages.TagStatus.{Completed, NotStarted}
import play.api.libs.json.{Format, Json}

case class TaskStatuses(beneficiaries: TagStatus = NotStarted,
                        trustees: TagStatus = NotStarted,
                        taxLiability: TagStatus = NotStarted,
                        protectors: TagStatus = NotStarted,
                        other: TagStatus = NotStarted,
                        trustDetails: TagStatus = NotStarted,
                        settlors: TagStatus = NotStarted,
                        assets: TagStatus = NotStarted) {

  /**
   *
   * @param taxLiabilityEnabled - used to determine if the tax liability task needs to be enabled on the task list
   * @return true if all of the relevant sections have a status of Completed
   */
  def allComplete(taxLiabilityEnabled: Boolean): Boolean =
    beneficiaries.equals(Completed) &&
      trustees.equals(Completed) &&
      protectors.equals(Completed) &&
      other.equals(Completed) &&
      trustDetails.equals(Completed) &&
      settlors.equals(Completed) &&
      assets.equals(Completed) &&
      (taxLiability.equals(Completed) || !taxLiabilityEnabled)
}

object TaskStatuses {

  implicit lazy val format: Format[TaskStatuses] = Json.format[TaskStatuses]

  val withAllComplete: TaskStatuses = TaskStatuses(
    beneficiaries = Completed,
    trustees = Completed,
    taxLiability = Completed,
    protectors = Completed,
    other = Completed,
    trustDetails = Completed,
    settlors = Completed,
    assets = Completed
  )
}
