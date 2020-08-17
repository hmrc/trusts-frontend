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

package controllers.register

import java.time.LocalDate

import config.FrontendAppConfig
import controllers.actions._
import controllers.actions.register._
import javax.inject.Inject
import models.NormalMode
import models.registration.Matched.{AlreadyRegistered, Failed, Success}
import models.registration.pages.RegistrationStatus.InProgress
import models.registration.pages.Status.Completed
import navigation.registration.TaskListNavigator
import pages.register.trust_details.WhenTrustSetupPage
import pages.register.{ExistingTrustMatched, RegistrationProgress, TrustHaveAUTRPage, TrustRegisteredOnlinePage}
import play.api.Logger
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.RegistrationsRepository
import uk.gov.hmrc.auth.core.AffinityGroup
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import uk.gov.hmrc.time.TaxYear
import utils.DateFormatter
import viewmodels.Task
import views.html.register.TaskListView

import scala.concurrent.{ExecutionContext, Future}

class TaskListController @Inject()(
                                    override val messagesApi: MessagesApi,
                                    identify: RegistrationIdentifierAction,
                                    getData: DraftIdRetrievalActionProvider,
                                    requireData: RegistrationDataRequiredAction,
                                    requiredAnswer: RequiredAnswerActionProvider,
                                    val controllerComponents: MessagesControllerComponents,
                                    view: TaskListView,
                                    config: FrontendAppConfig,
                                    registrationProgress: RegistrationProgress,
                                    registrationsRepository: RegistrationsRepository,
                                    taskListNavigator : TaskListNavigator,
                                    requireDraft : RequireDraftRegistrationActionRefiner,
                                    dateFormatter: DateFormatter
                                     )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private def actions(draftId: String) =
    identify andThen getData(draftId) andThen requireData andThen
      requiredAnswer(
        RequiredAnswer(
          TrustRegisteredOnlinePage, controllers.register.routes.TrustRegisteredOnlineController.onPageLoad(NormalMode, draftId))
      ) andThen
      requiredAnswer(
        RequiredAnswer(
          TrustHaveAUTRPage, controllers.register.routes.TrustHaveAUTRController.onPageLoad(NormalMode, draftId))
      ) andThen requireDraft

  def onPageLoad(draftId: String): Action[AnyContent] = actions(draftId).async {
    implicit request =>

      def renderView(affinityGroup : AffinityGroup) = {
        val savedUntil : String = dateFormatter.savedUntil(request.userAnswers.createdAt)

        val updatedAnswers = request.userAnswers.copy(progress = InProgress)

        for {
          _  <- registrationsRepository.set(updatedAnswers)
          taskList <- registrationProgress.items(updatedAnswers, draftId)
          isTaskListComplete <- registrationProgress.isTaskListComplete(updatedAnswers)
        } yield {

          def showTaxLiability: Boolean = {
            val taxYearStart = TaxYear.current.starts
              (request.userAnswers.get(WhenTrustSetupPage).getOrElse(LocalDate.now()) isBefore
                LocalDate.of(taxYearStart.getYear, taxYearStart.getMonthOfYear, taxYearStart.getDayOfMonth))
          }

          val sections = if (showTaxLiability) {
            taskList
          } else {
            val removeTaxLiabilityFromTaskList = (t : Task) => t.link.url == taskListNavigator.taxLiabilityJourney(draftId)
            taskList.filterNot(removeTaxLiabilityFromTaskList)
          }

          Logger.debug(s"[TaskList][sections] $sections")

          Ok(view(draftId ,savedUntil, sections, isTaskListComplete, affinityGroup))        }
      }

      val isExistingTrust = request.userAnswers.get(TrustHaveAUTRPage).get

      if (isExistingTrust) {
        request.userAnswers.get(ExistingTrustMatched) match {
          case Some(Success) =>
            renderView(request.affinityGroup)

          case Some(AlreadyRegistered) | Some(Failed) =>
            Future.successful(Redirect(controllers.register.routes.FailedMatchController.onPageLoad(draftId).url))

          case None =>
            Future.successful(Redirect(controllers.register.routes.WhatIsTheUTRController.onPageLoad(NormalMode, draftId).url))
        }
      } else {
        renderView(request.affinityGroup)
      }
  }
}
