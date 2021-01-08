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

package controllers.register

import controllers.actions._
import controllers.actions.register._
import javax.inject.Inject
import models.NormalMode
import models.registration.Matched.{AlreadyRegistered, Failed, Success}
import models.registration.pages.RegistrationStatus.InProgress
import models.requests.RegistrationDataRequest
import navigation.registration.TaskListNavigator
import pages.register.{ExistingTrustMatched, RegistrationProgress, TrustHaveAUTRPage, TrustRegisteredOnlinePage}
import play.api.Logging
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, ActionBuilder, AnyContent, MessagesControllerComponents, Result}
import repositories.RegistrationsRepository
import uk.gov.hmrc.auth.core.AffinityGroup
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import utils.{DateFormatter, TaxLiabilityHelper}
import viewmodels.Task
import views.html.register.TaskListView

import scala.concurrent.{ExecutionContext, Future}

class TaskListController @Inject()(
                                    override val messagesApi: MessagesApi,
                                    requiredAnswer: RequiredAnswerActionProvider,
                                    val controllerComponents: MessagesControllerComponents,
                                    view: TaskListView,
                                    registrationProgress: RegistrationProgress,
                                    registrationsRepository: RegistrationsRepository,
                                    taskListNavigator : TaskListNavigator,
                                    requireDraft : RequireDraftRegistrationActionRefiner,
                                    dateFormatter: DateFormatter,
                                    standardAction: StandardActionSets
                                  )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with Logging {

  private def actions(draftId: String): ActionBuilder[RegistrationDataRequest, AnyContent] =
    standardAction.identifiedUserWithRequiredAnswer(draftId,
      RequiredAnswer(TrustRegisteredOnlinePage,controllers.register.routes.TrustRegisteredOnlineController.onPageLoad(NormalMode, draftId))) andThen
      requiredAnswer(
        RequiredAnswer(
          TrustHaveAUTRPage, controllers.register.routes.TrustHaveAUTRController.onPageLoad(NormalMode, draftId))
      ) andThen requireDraft

  def onPageLoad(draftId: String): Action[AnyContent] = actions(draftId).async {
    implicit request =>

      def renderView(affinityGroup : AffinityGroup): Future[Result] = {
        val savedUntil : String = dateFormatter.savedUntil(request.userAnswers.createdAt)

        val updatedAnswers = request.userAnswers.copy(progress = InProgress)

        for {
          _  <- registrationsRepository.set(updatedAnswers)
          sections <- registrationProgress.items(draftId)
          additionalSections <- registrationProgress.additionalItems(draftId)
          isTaskListComplete <- registrationProgress.isTaskListComplete(draftId, request.affinityGroup)
          trustSetUpDate <- registrationsRepository.getTrustSetupDate(draftId)
        } yield {

          val filteredSections = if (TaxLiabilityHelper.showTaxLiability(trustSetUpDate)) {
            sections
          } else {
            val removeTaxLiabilityFromTaskList = (t : Task) => t.link.url == taskListNavigator.taxLiabilityJourney(draftId)
            sections.filterNot(removeTaxLiabilityFromTaskList)
          }

          logger.debug(s"[sections][Session ID: ${request.sessionId}] $sections")

          Ok(view(draftId ,savedUntil, filteredSections, additionalSections, isTaskListComplete, affinityGroup))        }
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
