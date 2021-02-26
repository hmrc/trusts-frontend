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

import navigation.registration.TaskListNavigator
import repositories.RegistrationsRepository
import uk.gov.hmrc.http.HeaderCarrier
import viewmodels._

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class RegistrationProgress @Inject()(
                                      navigator: TaskListNavigator,
                                      registrationsRepository: RegistrationsRepository
                                    )(implicit ec: ExecutionContext) {

  def items(draftId: String, isTaxable: Boolean)(implicit hc: HeaderCarrier): Future[List[Task]] =
    for {
      allStatus <- registrationsRepository.getAllStatus(draftId)
    } yield {
      val entityTasks = List(
        Task(Link("trustDetails", navigator.trustDetailsJourney(draftId)), allStatus.trustDetails),
        Task(Link("settlors", navigator.settlorsJourney(draftId)), allStatus.settlors),
        Task(Link("trustees", navigator.trusteesJourneyUrl(draftId)), allStatus.trustees),
        Task(Link("beneficiaries", navigator.beneficiariesJourneyUrl(draftId)), allStatus.beneficiaries)
      )

      val taxableTasks = if (isTaxable) {
        List(
          Task(Link("assets", navigator.assetsJourneyUrl(draftId)), allStatus.assets),
          Task(Link("taxLiability", navigator.taxLiabilityJourney(draftId)), allStatus.taxLiability)
        )
      } else {
        Nil
      }

      entityTasks ::: taxableTasks
    }

  def additionalItems(draftId: String, isTaxable: Boolean)(implicit hc: HeaderCarrier): Future[List[Task]] = {
    for {
      allStatus <- registrationsRepository.getAllStatus(draftId)
    } yield {
      val nonTaxableTask = if (isTaxable) {
        Nil
      } else {
        List(Task(Link("companyOwnershipOrControllingInterest", navigator.assetsJourneyUrl(draftId)), allStatus.assets))
      }
      val entityTasks = List(
        Task(Link("protectors", navigator.protectorsJourneyUrl(draftId)), allStatus.protectors),
        Task(Link("otherIndividuals", navigator.otherIndividualsJourneyUrl(draftId)), allStatus.otherIndividuals)
      )

      nonTaxableTask ::: entityTasks
    }
  }

  def isTaskListComplete(draftId: String, isTaxable: Boolean)(implicit hc: HeaderCarrier): Future[Boolean] = {
    registrationsRepository.getAllStatus(draftId).flatMap {
      status =>
        registrationsRepository.getTrustSetupDate(draftId).map {
          trustSetUpDate =>
            status.allComplete(trustSetUpDate, isTaxable)
        }
    }
  }
}
