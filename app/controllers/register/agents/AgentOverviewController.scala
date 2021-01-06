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

package controllers.register.agents

import config.FrontendAppConfig
import controllers.actions._
import controllers.actions.register.RegistrationIdentifierAction
import models.requests.IdentifierRequest
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, ActionBuilder, AnyContent, MessagesControllerComponents}
import repositories.RegistrationsRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.register.agents.AgentOverviewView

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class AgentOverviewController @Inject()(override val messagesApi: MessagesApi,
                                        standardActionSets: StandardActionSets,
                                        identify: RegistrationIdentifierAction,
                                        hasAgentAffinityGroup: RequireStateActionProviderImpl,
                                        registrationsRepository: RegistrationsRepository,
                                        val controllerComponents: MessagesControllerComponents,
                                        view: AgentOverviewView,
                                        config: FrontendAppConfig)
                                       (implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private def actions: ActionBuilder[IdentifierRequest, AnyContent] = identify andThen hasAgentAffinityGroup()

  def onPageLoad(): Action[AnyContent] = actions.async {
    implicit request =>
      registrationsRepository.listDrafts().map {
        drafts =>
          Ok(view(drafts))
      }
  }

  def onSubmit(): Action[AnyContent] = actions {
      Redirect(controllers.register.routes.CreateDraftRegistrationController.create())
  }

  def continue(draftId: String): Action[AnyContent] = standardActionSets.identifiedUserWithData(draftId) {
    implicit request =>

      // TODO - call to backend to check for presence of agent details data
      // if the data is there, go to task list
      // otherwise go to agent details frontend

      if (true) {
        Redirect(config.agentDetailsFrontendUrl(draftId))
      } else {
        Redirect(controllers.register.routes.TaskListController.onPageLoad(draftId))
      }
  }

  def remove(draftId: String): Action[AnyContent] = standardActionSets.identifiedUserWithData(draftId) {
    Redirect(routes.RemoveDraftYesNoController.onPageLoad(draftId))
  }

}
