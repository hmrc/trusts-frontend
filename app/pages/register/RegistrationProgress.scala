/*
 * Copyright 2020 HM Revenue & Customs
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

import javax.inject.Inject
import models.core.UserAnswers
import models.registration.pages.Status._
import models.registration.pages._
import navigation.registration.TaskListNavigator
import pages.entitystatus.{DeceasedSettlorStatus, TrustDetailsStatus}
import pages.register.asset.AddAssetsPage
import pages.register.settlors.living_settlor.trust_type.SetUpInAdditionToWillTrustYesNoPage
import pages.register.settlors.{AddASettlorPage, SetUpAfterSettlorDiedYesNoPage}
import pages.register.trust_details.WhenTrustSetupPage
import repositories.RegistrationsRepository
import sections._
import sections.beneficiaries.Beneficiaries
import uk.gov.hmrc.http.HeaderCarrier
import viewmodels._

import scala.concurrent.{ExecutionContext, Future}

class RegistrationProgress @Inject()(navigator: TaskListNavigator, registrationsRepository: RegistrationsRepository)
                                    (implicit ec: ExecutionContext) {

  def items(userAnswers: UserAnswers, draftId: String)(implicit hc: HeaderCarrier): Future[List[Task]] =
    for {
      allStatus <- registrationsRepository.getAllStatus(draftId)
    } yield {
      List(
        Task(Link(TrustDetails, navigator.trustDetailsJourney(userAnswers, draftId).url), trustDetailsStatus(userAnswers)),
        Task(Link(Settlors, navigator.settlorsJourney(userAnswers, draftId).url), settlorsStatus(userAnswers)),
        Task(Link(Trustees, navigator.trusteesJourneyUrl(draftId)), allStatus.trustees),
        Task(Link(Beneficiaries, navigator.beneficiariesJourneyUrl(draftId)), allStatus.beneficiaries),
        Task(Link(Assets, navigator.assetsJourney(userAnswers, draftId).url), assetsStatus(userAnswers)),
        Task(Link(TaxLiability, navigator.taxLiabilityJourney(draftId).url), None)
      )
    }

  private def determineStatus(complete: Boolean): Option[Status] = {
    if (complete) {
      Some(Completed)
    } else {
      Some(InProgress)
    }
  }

  def trustDetailsStatus(userAnswers: UserAnswers): Option[Status] = {
    userAnswers.get(WhenTrustSetupPage) match {
      case None => None
      case Some(_) =>
        val completed = userAnswers.get(TrustDetailsStatus).contains(Completed)
        determineStatus(completed)
    }
  }

  def settlorsStatus(userAnswers: UserAnswers): Option[Status] = {
    val setUpAfterSettlorDied = userAnswers.get(SetUpAfterSettlorDiedYesNoPage)
    val inAdditionToWillTrust = userAnswers.get(SetUpInAdditionToWillTrustYesNoPage).getOrElse(false)

    def isDeceasedSettlorComplete: Option[Status] = {
      val deceasedCompleted = userAnswers.get(DeceasedSettlorStatus)
      val isComplete = deceasedCompleted.contains(Completed)
      determineStatus(isComplete)
    }

    setUpAfterSettlorDied match {
      case None => None
      case Some(setupAfterDeceased) =>
        if (setupAfterDeceased) {isDeceasedSettlorComplete}
        else {
          userAnswers.get(LivingSettlors).getOrElse(Nil) match {
            case Nil =>
              if (!setupAfterDeceased && !inAdditionToWillTrust) {Some(Status.InProgress)}
              else { determineStatus(true) }
            case living =>
              val noMoreToAdd = userAnswers.get(AddASettlorPage).contains(AddASettlor.NoComplete)
              val isComplete = !living.exists(_.status == InProgress)
              determineStatus(isComplete && noMoreToAdd)
          }
        }
    }
  }

  def assetsStatus(userAnswers: UserAnswers): Option[Status] = {
    val noMoreToAdd = userAnswers.get(AddAssetsPage).contains(AddAssets.NoComplete)
    val assets = userAnswers.get(sections.Assets).getOrElse(List.empty)

    assets match {
      case Nil => None
      case list =>

        val status = !list.exists(_.status == InProgress) && noMoreToAdd
        determineStatus(status)
    }
  }

  def isTaskListComplete(userAnswers: UserAnswers)(implicit hc: HeaderCarrier): Future[Boolean] = {
    if (trustDetailsStatus(userAnswers).contains(Completed) &&
      settlorsStatus(userAnswers).contains(Completed) &&
      assetsStatus(userAnswers).contains(Completed)) {
      registrationsRepository.getAllStatus(userAnswers.draftId).map {
        status => status.allComplete
      }
    } else {
      Future.successful(false)
    }
  }
}
