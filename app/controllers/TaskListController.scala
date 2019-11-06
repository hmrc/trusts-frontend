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

package controllers

import config.FrontendAppConfig
import controllers.actions._
import javax.inject.Inject
import models.Matched.{AlreadyRegistered, Failed, Success}
import models.NormalMode
import models.RegistrationStatus.InProgress
import navigation.registration.TaskListNavigator
import pages._
import play.api.Logger
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import sections.TaxLiability
import uk.gov.hmrc.auth.core.AffinityGroup
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import utils.DateFormatter
import viewmodels.Task
import views.html.TaskListView

import scala.concurrent.{ExecutionContext, Future}

class TaskListController @Inject()(
                                       override val messagesApi: MessagesApi,
                                       identify: IdentifierAction,
                                       getData: DraftIdRetrievalActionProvider,
                                       requireData: DataRequiredAction,
                                       requiredAnswer: RequiredAnswerActionProvider,
                                       val controllerComponents: MessagesControllerComponents,
                                       view: TaskListView,
                                       config: FrontendAppConfig,
                                       registrationProgress: RegistrationProgress,
                                       sessionRepository: SessionRepository,
                                       taskListNavigator : TaskListNavigator,
                                       requireDraft : RequireDraftRegistrationActionRefiner,
                                       dateFormatter: DateFormatter
                                     )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private def actions(draftId: String) =
    identify andThen getData(draftId) andThen requireData andThen
      requiredAnswer(
        RequiredAnswer(
          TrustRegisteredOnlinePage, routes.TrustRegisteredOnlineController.onPageLoad(NormalMode, draftId))
      ) andThen
      requiredAnswer(
        RequiredAnswer(
          TrustHaveAUTRPage, routes.TrustHaveAUTRController.onPageLoad(NormalMode, draftId))
      ) andThen requireDraft

  def onPageLoad(draftId: String): Action[AnyContent] = actions(draftId).async {
    implicit request =>

      def renderView(affinityGroup : AffinityGroup) = {
        val savedUntil : String = dateFormatter.savedUntil(request.userAnswers.createdAt)

        val updatedAnswers = request.userAnswers.copy(progress = InProgress)

        for {
          _  <- sessionRepository.set(updatedAnswers)
        } yield {

          val sections = if (config.removeTaxLiabilityOnTaskList) {
            val removeTaxLiabilityFromTaskList = (t : Task) => t.link.url == taskListNavigator.nextPage(TaxLiability, updatedAnswers, draftId).url
            registrationProgress.items(updatedAnswers, draftId).filterNot(removeTaxLiabilityFromTaskList)
          } else {
            registrationProgress.items(updatedAnswers, draftId)
          }

          val isTaskListComplete = registrationProgress.isTaskListComplete(updatedAnswers)

          Logger.debug(s"[TaskList][sections] $sections")

          Ok(view(draftId ,savedUntil, sections, isTaskListComplete, affinityGroup))        }
      }

      val isExistingTrust = request.userAnswers.get(TrustHaveAUTRPage).get

      if (isExistingTrust) {
        request.userAnswers.get(ExistingTrustMatched) match {
          case Some(Success) =>
            renderView(request.affinityGroup)

          case Some(AlreadyRegistered) | Some(Failed) =>
            Future.successful(Redirect(routes.FailedMatchController.onPageLoad(draftId).url))

          case None =>
            Future.successful(Redirect(routes.WhatIsTheUTRController.onPageLoad(NormalMode, draftId).url))
        }
      } else {
        renderView(request.affinityGroup)
      }
  }
}
