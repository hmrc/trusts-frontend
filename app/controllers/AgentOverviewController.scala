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
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.AgentOverviewView

import scala.concurrent.{ExecutionContext, Future}

class AgentOverviewController @Inject()(
                                         override val messagesApi: MessagesApi,
                                         identify: IdentifierAction,
                                         hasAgentAffinityGroup: RequireStateActionProviderImpl,
                                         sessionRepository: SessionRepository,
                                         config: FrontendAppConfig,
                                         val controllerComponents: MessagesControllerComponents,
                                         view: AgentOverviewView
                                     )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private def actions = identify andThen hasAgentAffinityGroup()

  def onPageLoad: Action[AnyContent] = actions.async {
    implicit request =>
      sessionRepository.listDrafts(request.identifier).map {
        drafts =>
          Ok(view(drafts))
      }
  }

  def onSubmit() = actions.async {
    implicit request =>
      Future.successful(Redirect(routes.CreateDraftRegistrationController.create()))
  }
}
