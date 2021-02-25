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
import sections.{Beneficiaries, Settlors, _}
import uk.gov.hmrc.http.HeaderCarrier
import viewmodels._

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class RegistrationProgress @Inject()(navigator: TaskListNavigator, registrationsRepository: RegistrationsRepository)
                                    (implicit ec: ExecutionContext) {

  def items(draftId: String)(implicit hc: HeaderCarrier): Future[List[Task]] =
    for {
      allStatus <- registrationsRepository.getAllStatus(draftId)
    } yield {
      List(
        Task(Link(TrustDetails, navigator.trustDetailsJourney(draftId)), allStatus.trustDetails),
        Task(Link(Settlors, navigator.settlorsJourney(draftId)), allStatus.settlors),
        Task(Link(Trustees, navigator.trusteesJourneyUrl(draftId)), allStatus.trustees),
        Task(Link(Beneficiaries, navigator.beneficiariesJourneyUrl(draftId)), allStatus.beneficiaries),
        Task(Link(Assets, navigator.assetsJourneyUrl(draftId)), allStatus.assets),
        Task(Link(TaxLiability, navigator.taxLiabilityJourney(draftId)), allStatus.taxLiability)
      )
    }

  def additionalItems(draftId: String)(implicit hc: HeaderCarrier): Future[List[Task]] = {
    for {
      allStatus <- registrationsRepository.getAllStatus(draftId)
    } yield {
      List(
        Task(Link(Protectors, navigator.protectorsJourneyUrl(draftId)), allStatus.protectors),
        Task(Link(OtherIndividuals, navigator.otherIndividualsJourneyUrl(draftId)), allStatus.otherIndividuals)
      )
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
