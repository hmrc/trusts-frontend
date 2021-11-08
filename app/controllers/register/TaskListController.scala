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
import models.registration.Matched.{AlreadyRegistered, Failed, Success}
import models.registration.pages.RegistrationStatus.InProgress
import models.requests.RegistrationDataRequest
import pages.register.{ExistingTrustMatched, RegistrationProgress, TrustHaveAUTRPage, TrustRegisteredOnlinePage}
import play.api.Logging
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import repositories.RegistrationsRepository
import uk.gov.hmrc.auth.core.AffinityGroup
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import utils.DateFormatter
import views.html.register.TaskListView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class TaskListController @Inject()(
                                    override val messagesApi: MessagesApi,
                                    requiredAnswer: RequiredAnswerActionProvider,
                                    val controllerComponents: MessagesControllerComponents,
                                    view: TaskListView,
                                    registrationProgress: RegistrationProgress,
                                    registrationsRepository: RegistrationsRepository,
                                    requireDraft: RequireDraftRegistrationActionRefiner,
                                    dateFormatter: DateFormatter,
                                    standardAction: StandardActionSets
                                  )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with Logging {

  private def actions(draftId: String): ActionBuilder[RegistrationDataRequest, AnyContent] =
    standardAction.identifiedUserWithRequiredAnswer(draftId,
      RequiredAnswer(TrustRegisteredOnlinePage, controllers.register.routes.TrustRegisteredOnlineController.onPageLoad())) andThen
      requiredAnswer(
        RequiredAnswer(
          TrustHaveAUTRPage, controllers.register.routes.TrustHaveAUTRController.onPageLoad())
      ) andThen requireDraft

  def onPageLoad(draftId: String): Action[AnyContent] = actions(draftId).async {
    implicit request =>

      val isExistingTrust = request.userAnswers.isExistingTrust

      def renderView(affinityGroup: AffinityGroup): Future[Result] = {
        val savedUntil: String = dateFormatter.savedUntil(request.userAnswers.createdAt)

        val updatedAnswers = request.userAnswers.copy(progress = InProgress)

        // TODO CALL trusts-store to update tax-liability status if required, this is done in trusts but no longer read anywhere due to now reading from trusts-store
        for {
          _ <- registrationsRepository.set(updatedAnswers, request.affinityGroup)
          _ <- registrationsRepository.updateTaxLiability(draftId)
          firstTaxYearAvailable <- registrationsRepository.getFirstTaxYearAvailable(draftId)
          isTaxable = updatedAnswers.isTaxable
          sections <- registrationProgress.items(draftId, firstTaxYearAvailable, isTaxable, isExistingTrust)
          additionalSections <- registrationProgress.additionalItems(draftId, isTaxable)
          isTaskListComplete <- registrationProgress.isTaskListComplete(draftId, firstTaxYearAvailable, isTaxable, isExistingTrust)
        } yield {
          logger.debug(s"[sections][Session ID: ${request.sessionId}] $sections")
          Ok(view(isTaxable, draftId, savedUntil, sections, additionalSections, isTaskListComplete, affinityGroup))
        }
      }

      (isExistingTrust, request.userAnswers.get(ExistingTrustMatched)) match {
        case (true, Some(Success)) | (false, _) =>
          renderView(request.affinityGroup)
        case (_, Some(AlreadyRegistered)) | (_, Some(Failed)) =>
          Future.successful(Redirect(controllers.register.routes.FailedMatchController.onPageLoad().url))
        case _ =>
          Future.successful(Redirect(controllers.register.routes.WhatIsTheUTRController.onPageLoad().url))
      }
  }

  def onSubmit(draftId: String): Action[AnyContent] = actions(draftId) { _ =>
    Redirect(controllers.register.routes.DeclarationController.onPageLoad(draftId))
  }
}
