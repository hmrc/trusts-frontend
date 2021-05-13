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

import config.FrontendAppConfig
import controllers.actions.register.{RegistrationDataRetrievalAction, RegistrationIdentifierAction}
import pages.register.TrustRegisteredOnlinePage
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import uk.gov.hmrc.auth.core.AffinityGroup
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController

import javax.inject.Inject
import scala.concurrent.Future

class IndexController @Inject()(
                                 identify: RegistrationIdentifierAction,
                                 getRegistrationData: RegistrationDataRetrievalAction,
                                 config: FrontendAppConfig,
                                 val controllerComponents: MessagesControllerComponents
                               ) extends FrontendBaseController with I18nSupport with Logging {

  def onPageLoad(): Action[AnyContent] = (identify andThen getRegistrationData).async {
    implicit request =>

      request.affinityGroup match {
        case AffinityGroup.Agent =>
          logger.info(s"[Session ID: ${request.sessionId}] user is an agent, redirect to overview")
          Future.successful(Redirect(controllers.register.agents.routes.AgentOverviewController.onPageLoad()))
        case _ =>

          def startRegistrationJourney(): Future[Result] = {
            logger.info(s"[Session ID: ${request.sessionId}] user is new, starting registration journey")
            Future.successful(Redirect(controllers.register.routes.TrustRegisteredOnlineController.onPageLoad()))
          }

          request.userAnswers match {
            case Some(userAnswers) =>
              userAnswers.get(TrustRegisteredOnlinePage) match {
                case Some(false) =>
                  logger.info(s"[Session ID: ${request.sessionId}] user previously indicated trust is not registered online, redirecting to register task list")
                  Future.successful(Redirect(routes.TaskListController.onPageLoad(userAnswers.draftId)))
                case Some(true) =>
                  logger.info(s"[Session ID: ${request.sessionId}] user previously indicated trust is registered online, redirecting to maintain")
                  Future.successful(Redirect(config.maintainATrustFrontendUrl))
                case None =>
                  startRegistrationJourney()
              }
            case None =>
              startRegistrationJourney()
          }
      }
  }
}
