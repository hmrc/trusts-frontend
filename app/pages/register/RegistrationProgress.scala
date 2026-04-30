/*
 * Copyright 2026 HM Revenue & Customs
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
import models.registration.pages.TagStatus.{CannotStartYet, Completed, NoActionNeeded}
import navigation.registration.TaskListNavigator
import org.apache.pekko.event.Logging
import models.requests.RegistrationDataRequest
import pages.register.RegistrationProgress.taxLiabilityLinkDisplay
import play.api.Logging
import play.api.libs.json.JsObject
import play.api.mvc.AnyContent
import repositories.RegistrationsRepository
import services.{AuditService, SettlorValidationService, TrustsStoreService}
import uk.gov.hmrc.http.HeaderCarrier
import viewmodels._

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class RegistrationProgress @Inject() (
  navigator: TaskListNavigator,
  trustsStoreService: TrustsStoreService,
  registrationsRepository: RegistrationsRepository,
  settlorValidationService: SettlorValidationService,
  auditService: AuditService
)(implicit ec: ExecutionContext)
    extends Logging {

  private val className = getClass.getSimpleName

  def items(draftId: String)(implicit hc: HeaderCarrier): Future[List[Task]] =
    trustsStoreService.getTaskStatuses(draftId) map { statuses =>
      val entityTasks: List[Task] = List(
        Task(Link("trustees", navigator.trusteesJourneyUrl(draftId)), statuses.trustees),
        Task(Link("settlors", navigator.settlorsJourney(draftId)), statuses.settlors),
        Task(Link("beneficiaries", navigator.beneficiariesJourneyUrl(draftId)), statuses.beneficiaries),
        Task(Link("protectors", navigator.protectorsJourneyUrl(draftId)), statuses.protectors),
        Task(Link("otherIndividuals", navigator.otherIndividualsJourneyUrl(draftId)), statuses.other)
      )
      entityTasks
    }

  def additionalItems(
    draftId: String,
    firstTaxYearAvailable: Option[FirstTaxYearAvailable],
    isTaxable: Boolean,
    isExistingTrust: Boolean
  )(implicit hc: HeaderCarrier): Future[List[Task]] =
    trustsStoreService.getTaskStatuses(draftId) map { statuses =>
      val taxableTasks: List[Task] = if (isTaxable) {
        val assetsTask = Task(Link("assets", navigator.assetsJourneyUrl(draftId)), statuses.assets)

        val taxLiabilityStatus: Option[TagStatus] =
          taxLiabilityLinkDisplay(firstTaxYearAvailable, isTaxable, isExistingTrust) match {
            case HideTask                               => None
            case x if statuses.trustDetails.isCompleted =>
              Some(if (x.isEnabled) statuses.taxLiability else NoActionNeeded)
            case _                                      => Some(CannotStartYet)
          }

        val taxLiabilityTask: Option[Task] = taxLiabilityStatus map { value =>
          Task(Link("taxLiability", navigator.taxLiabilityJourney(draftId)), value)
        }

        assetsTask +: taxLiabilityTask.toList
      } else {
        List(
          Task(
            link = Link("companyOwnershipOrControllingInterest", navigator.assetsJourneyUrl(draftId)),
            tag = statuses.assets,
            appTaskStyles = Some(Width("70%").toString),
            taskTagTextStyles = Some(Width("70%").toString)
          )
        )
      }

      val entityTasks: List[Task] = List(
        Task(Link("trustDetails", navigator.trustDetailsJourney(draftId)), statuses.trustDetails)
      )

      entityTasks ::: taxableTasks
    }

  def isTaskListComplete[A](
    draftId: String,
    firstTaxYearAvailable: Option[FirstTaxYearAvailable],
    isTaxable: Boolean,
    isExistingTrust: Boolean
  )(implicit hc: HeaderCarrier, request: RegistrationDataRequest[A]): Future[Boolean] = {

    val taxLiabilityEnabled = taxLiabilityLinkDisplay(firstTaxYearAvailable, isTaxable, isExistingTrust).isEnabled

    for {
      statuses         <- trustsStoreService.getTaskStatuses(draftId)
      tasksComplete     = statuses.allComplete(taxLiabilityEnabled)
      settlorDataValid <-
        if (tasksComplete) {
          isSettlorDataComplete(draftId, calledFrom = "RegistrationProgress.isTaskListComplete")
        } else {
          Future.successful(false)
        }
    } yield tasksComplete && settlorDataValid
  }

  // due to issues with trusts being registered without settlor information we are not solely relying on the task list statuses
  // in addition we directly check the required settlor data is present
  def isSettlorDataComplete[A](
    draftId: String,
    calledFrom: String
  )(implicit hc: HeaderCarrier, request: RegistrationDataRequest[A]): Future[Boolean] =
    for {
      registrationPieces     <- registrationsRepository.getRegistrationPieces(draftId)
      draftSettlors          <- registrationsRepository.getDraftSettlors(draftId)
      registrationValidation  =
        settlorValidationService.validateRegistrationSettlorComponent(Some(registrationPieces))
      answerSectionValidation =
        settlorValidationService.validateAnswerSectionSettlorComponent(Some(draftSettlors.as[JsObject]))

      allMissingComponents = registrationValidation ::: answerSectionValidation

      _ = if (allMissingComponents.nonEmpty) {
            val missingInfo = allMissingComponents.mkString(", ")
            val logMessage  =
              s"[$className][isSettlorDataComplete][Session ID: ${hc.sessionId}][calledFrom: $calledFrom] Trust registration stopping due to missing settlor information: $missingInfo"

            logger.error(logMessage)
            auditService.auditRegistrationWithMissingSettlorInfo(request.userAnswers, missingInfo)
          }

    } yield allMissingComponents.isEmpty

  def taskCount(
    draftId: String,
    firstTaxYearAvailable: Option[FirstTaxYearAvailable],
    isTaxable: Boolean,
    isExistingTrust: Boolean
  )(implicit hc: HeaderCarrier): Future[(Int, Int)] =
    for {
      statuses           <- trustsStoreService.getTaskStatuses(draftId)
      mainSections       <- items(draftId)
      additionalSections <- additionalItems(draftId, firstTaxYearAvailable, isTaxable, isExistingTrust)
    } yield {
      val allTasks     = mainSections ++ additionalSections
      val totalTasks   = allTasks.size
      val taskStatuses = List(
        statuses.beneficiaries,
        statuses.other,
        statuses.assets,
        statuses.protectors,
        statuses.settlors,
        statuses.trustDetails,
        statuses.trustees
      )

      val taxLiabilityStatus =
        if (taxLiabilityLinkDisplay(firstTaxYearAvailable, isTaxable, isExistingTrust).isEnabled) {
          List(statuses.taxLiability)
        } else {
          List.empty
        }

      val completedTasks = (taskStatuses ++ taxLiabilityStatus).count(_ == TagStatus.Completed)

      (completedTasks, totalTasks)
    }

}

object RegistrationProgress {

  def taxLiabilityLinkDisplay(
    firstTaxYearAvailable: Option[FirstTaxYearAvailable],
    isTaxable: Boolean,
    isExistingTrust: Boolean
  ): TaskDisplay =
    if (isTaxable && !isExistingTrust) {
      if (firstTaxYearAvailable.fold(false)(_.yearsAgo > 0)) EnableTask else DisableTask
    } else {
      HideTask
    }

}
