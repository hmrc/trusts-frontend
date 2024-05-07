/*
 * Copyright 2024 HM Revenue & Customs
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

package controllers.register.agents

import controllers.actions._
import controllers.actions.register.RegistrationIdentifierAction
import models.core.MatchingAndSuitabilityUserAnswers
import models.requests.IdentifierRequest
import navigation.registration.TaskListNavigator
import play.api.Logging
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, ActionBuilder, AnyContent, MessagesControllerComponents}
import repositories.{CacheRepository, RegistrationsRepository}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.register.agents.AgentOverviewView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class AgentOverviewController @Inject()(
                                         override val messagesApi: MessagesApi,
                                         standardActionSets: StandardActionSets,
                                         identify: RegistrationIdentifierAction,
                                         hasAgentAffinityGroup: RequireStateActionProviderImpl,
                                         registrationsRepository: RegistrationsRepository,
                                         cacheRepository: CacheRepository,
                                         val controllerComponents: MessagesControllerComponents,
                                         view: AgentOverviewView,
                                         taskListNavigator: TaskListNavigator
                                       )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with Logging {

  private def actions: ActionBuilder[IdentifierRequest, AnyContent] = identify andThen hasAgentAffinityGroup()

  def onPageLoad(): Action[AnyContent] = actions.async {
    implicit request =>
      registrationsRepository.listDrafts().map {
        drafts =>
          Ok(view(drafts))
      }
  }

  def onSubmit(): Action[AnyContent] = actions.async {
    implicit request =>
      cacheRepository.set(MatchingAndSuitabilityUserAnswers(request.internalId)) flatMap { _ =>
        Future.successful(Redirect(controllers.register.routes.TrustRegisteredOnlineController.onPageLoad()))
      }
  }

  def continue(draftId: String): Action[AnyContent] = standardActionSets.identifiedUserWithRegistrationData(draftId).async {
    implicit request =>

      (for {
        address <- registrationsRepository.getAgentAddress(draftId)
      } yield {
        if (address.isEmpty) {
          Redirect(taskListNavigator.agentDetailsJourneyUrl(draftId))
        } else {
          Redirect(controllers.register.routes.TaskListController.onPageLoad(draftId))
        }
      }) recover {
        case e =>
          logger.error(s"[Draft ID: $draftId][Session ID: ${request.sessionId}] failed to continue with draft: ${e.getMessage}")
          Redirect(routes.AgentOverviewController.onPageLoad())
      }
  }

  def remove(draftId: String): Action[AnyContent] = standardActionSets.identifiedUserWithRegistrationData(draftId) {
    Redirect(routes.RemoveDraftYesNoController.onPageLoad(draftId))
  }

}
