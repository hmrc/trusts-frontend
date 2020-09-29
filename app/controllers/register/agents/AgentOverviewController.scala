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

package controllers.register.agents

import controllers.actions._
import controllers.actions.register.{DraftIdRetrievalActionProvider, RegistrationDataRequiredAction, RegistrationIdentifierAction}
import javax.inject.Inject
import models.NormalMode
import pages.register.agents.AgentTelephoneNumberPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.RegistrationsRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.register.agents.AgentOverviewView

import scala.concurrent.{ExecutionContext, Future}

class AgentOverviewController @Inject()(override val messagesApi: MessagesApi,
                                        identify: RegistrationIdentifierAction,
                                        hasAgentAffinityGroup: RequireStateActionProviderImpl,
                                        registrationsRepository: RegistrationsRepository,
                                        getData: DraftIdRetrievalActionProvider,
                                        requireData: RegistrationDataRequiredAction,
                                        val controllerComponents: MessagesControllerComponents,
                                        view: AgentOverviewView)
                                       (implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private def actions = identify andThen hasAgentAffinityGroup()

  def onPageLoad: Action[AnyContent] = actions.async {
    implicit request =>
      registrationsRepository.listDrafts().map {
        drafts =>
          Ok(view(drafts))
      }
  }

  def onSubmit() = actions.async {
      Future.successful(Redirect(controllers.register.routes.CreateDraftRegistrationController.create()))
  }

  def continue(draftId: String): Action[AnyContent] = (actions andThen getData(draftId) andThen requireData).async {
    implicit request =>

      if (request.userAnswers.get(AgentTelephoneNumberPage).isEmpty) {
        Future.successful(Redirect(routes.AgentInternalReferenceController.onPageLoad(NormalMode, draftId)))
      } else {
        Future.successful(Redirect(controllers.register.routes.TaskListController.onPageLoad(draftId)))
      }

  }

}
