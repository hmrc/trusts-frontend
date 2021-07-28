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
import navigation.registration.TaskListNavigator
import pages.register.RegistrationProgress.enableTaxLiabilityLink
import repositories.RegistrationsRepository
import uk.gov.hmrc.http.HeaderCarrier
import viewmodels._

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class RegistrationProgress @Inject()(
                                      navigator: TaskListNavigator,
                                      registrationsRepository: RegistrationsRepository
                                    )(implicit ec: ExecutionContext) {

  def items(draftId: String, firstTaxYearAvailable: Option[FirstTaxYearAvailable], isTaxable: Boolean, isExistingTrust: Boolean)
           (implicit hc: HeaderCarrier): Future[List[Task]] = {
    registrationsRepository.getAllStatus(draftId) map {
      allStatus =>
        val entityTasks: List[Task] = List(
          Task(Link("trustDetails", Some(navigator.trustDetailsJourney(draftId))), allStatus.trustDetails),
          Task(Link("settlors", Some(navigator.settlorsJourney(draftId))), allStatus.settlors),
          Task(Link("trustees", Some(navigator.trusteesJourneyUrl(draftId))), allStatus.trustees),
          Task(Link("beneficiaries", Some(navigator.beneficiariesJourneyUrl(draftId))), allStatus.beneficiaries)
        )

        val taxableTasks: List[Task] = if (isTaxable) {
          val assetsTask = Task(Link("assets", Some(navigator.assetsJourneyUrl(draftId))), allStatus.assets)
          val taxLiabilityTask = if (enableTaxLiabilityLink(firstTaxYearAvailable, isTaxable, isExistingTrust)) {
            List(Task(Link("taxLiability", Some(navigator.taxLiabilityJourney(draftId))), allStatus.taxLiability))
          } else {
            Nil
          }
          assetsTask +: taxLiabilityTask
        } else {
          Nil
        }

        entityTasks ::: taxableTasks
    }
  }

  def additionalItems(draftId: String, isTaxable: Boolean)(implicit hc: HeaderCarrier): Future[List[Task]] = {
    registrationsRepository.getAllStatus(draftId) map {
      allStatus =>
        val nonTaxableTask = if (isTaxable) {
          Nil
        } else {
          List(Task(Link("companyOwnershipOrControllingInterest", Some(navigator.assetsJourneyUrl(draftId))), allStatus.assets))
        }
        val entityTasks = List(
          Task(Link("protectors", Some(navigator.protectorsJourneyUrl(draftId))), allStatus.protectors),
          Task(Link("otherIndividuals", Some(navigator.otherIndividualsJourneyUrl(draftId))), allStatus.otherIndividuals)
        )

        nonTaxableTask ::: entityTasks
    }
  }

  def isTaskListComplete(draftId: String, firstTaxYearAvailable: Option[FirstTaxYearAvailable], isTaxable: Boolean, isExistingTrust: Boolean)
                        (implicit hc: HeaderCarrier): Future[Boolean] = {
    registrationsRepository.getAllStatus(draftId).map { status =>
      status.allComplete(enableTaxLiabilityLink(firstTaxYearAvailable, isTaxable, isExistingTrust))
    }
  }

}

object RegistrationProgress {
  def enableTaxLiabilityLink(firstTaxYearAvailable: Option[FirstTaxYearAvailable], isTaxable: Boolean, isExistingTrust: Boolean): Boolean = {
    if (isTaxable && !isExistingTrust) {
      firstTaxYearAvailable.fold(false)(_.yearsAgo > 0)
    } else {
      false
    }
  }
}
