/*
 * Copyright 2021 HM Revenue & Customs
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

package pages.register

import models.FirstTaxYearAvailable
import models.registration.pages.TagStatus
import models.registration.pages.TagStatus.{CannotStartYet, NoActionNeeded}
import navigation.registration.TaskListNavigator
import pages.register.RegistrationProgress.taxLiabilityLinkDisplay
import services.TrustsStoreService
import uk.gov.hmrc.http.HeaderCarrier
import viewmodels._

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class RegistrationProgress @Inject()(
                                      navigator: TaskListNavigator,
                                      trustsStoreService: TrustsStoreService
                                    )(implicit ec: ExecutionContext) {

  def items(draftId: String, firstTaxYearAvailable: Option[FirstTaxYearAvailable], isTaxable: Boolean, isExistingTrust: Boolean)
           (implicit hc: HeaderCarrier): Future[List[Task]] = {
    trustsStoreService.getTaskStatuses(draftId) map { statuses =>
      val entityTasks: List[Task] = List(
        Task(Link("trustDetails", navigator.trustDetailsJourney(draftId)), statuses.trustDetails),
        Task(Link("settlors", navigator.settlorsJourney(draftId)), statuses.settlors),
        Task(Link("trustees", navigator.trusteesJourneyUrl(draftId)), statuses.trustees),
        Task(Link("beneficiaries", navigator.beneficiariesJourneyUrl(draftId)), statuses.beneficiaries)
      )

      val taxableTasks: List[Task] = if (isTaxable) {
        val assetsTask = Task(Link("assets", navigator.assetsJourneyUrl(draftId)), statuses.assets)

        val taxLiabilityStatus: Option[TagStatus] = taxLiabilityLinkDisplay(firstTaxYearAvailable, isTaxable, isExistingTrust) match {
          case HideTask => None
          case x if statuses.trustDetails.isCompleted => Some(if (x.isEnabled) statuses.taxLiability else NoActionNeeded)
          case _ => Some(CannotStartYet)
        }

        val taxLiabilityTask: Option[Task] = taxLiabilityStatus map { value =>
          Task(Link("taxLiability", navigator.taxLiabilityJourney(draftId)), value)
        }

        assetsTask +: taxLiabilityTask.toList
      } else {
        Nil
      }

      entityTasks ::: taxableTasks
    }
  }

  def additionalItems(draftId: String, isTaxable: Boolean)(implicit hc: HeaderCarrier): Future[List[Task]] = {
    trustsStoreService.getTaskStatuses(draftId) map { statuses =>
      val nonTaxableTask: Option[Task] = if (isTaxable) {
        None
      } else {
        Some(Task(Link("companyOwnershipOrControllingInterest", navigator.assetsJourneyUrl(draftId)), statuses.assets))
      }

      val entityTasks: List[Task] = List(
        Task(Link("protectors", navigator.protectorsJourneyUrl(draftId)), statuses.protectors),
        Task(Link("otherIndividuals", navigator.otherIndividualsJourneyUrl(draftId)), statuses.other)
      )

      nonTaxableTask.toList ::: entityTasks
    }
  }

  def isTaskListComplete(draftId: String, firstTaxYearAvailable: Option[FirstTaxYearAvailable], isTaxable: Boolean, isExistingTrust: Boolean)
                        (implicit hc: HeaderCarrier): Future[Boolean] = {
    trustsStoreService.getTaskStatuses(draftId).map { statuses =>
      statuses.allComplete(taxLiabilityLinkDisplay(firstTaxYearAvailable, isTaxable, isExistingTrust).isEnabled)
    }
  }

}

object RegistrationProgress {
  def taxLiabilityLinkDisplay(firstTaxYearAvailable: Option[FirstTaxYearAvailable], isTaxable: Boolean, isExistingTrust: Boolean): TaskDisplay = {
    if (isTaxable && !isExistingTrust) {
      if (firstTaxYearAvailable.fold(false)(_.yearsAgo > 0)) EnableTask else DisableTask
    } else {
      HideTask
    }
  }
}
