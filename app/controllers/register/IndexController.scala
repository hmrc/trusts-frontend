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

import config.FrontendAppConfig
import controllers.actions.register.{RegistrationDataRetrievalAction, RegistrationIdentifierAction}
import javax.inject.Inject
import models.NormalMode
import pages.register.TrustRegisteredOnlinePage
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.auth.core.AffinityGroup
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController

import scala.concurrent.{ExecutionContext, Future}

class IndexController @Inject()(
                                 identify: RegistrationIdentifierAction,
                                 getData: RegistrationDataRetrievalAction,
                                 config: FrontendAppConfig,
                                 val controllerComponents: MessagesControllerComponents
                               )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  def onPageLoad(): Action[AnyContent] = (identify andThen getData).async {
    implicit request =>

      request.affinityGroup match {
        case AffinityGroup.Agent =>
          Future.successful(Redirect(controllers.register.agents.routes.AgentOverviewController.onPageLoad()))
        case _ =>
          request.userAnswers match {
            case Some(userAnswers) =>
              userAnswers.get(TrustRegisteredOnlinePage) match {
                case Some(false) =>
                  Future.successful(Redirect(routes.TaskListController.onPageLoad(userAnswers.draftId)))
                case Some(true) =>
                  Future.successful(Redirect(config.maintainATrustFrontendUrl))
                case None =>
                  Future.successful(Redirect(controllers.register.routes.TrustRegisteredOnlineController.onPageLoad(NormalMode, userAnswers.draftId)))
              }
            case None =>
              Future.successful(Redirect(routes.CreateDraftRegistrationController.create()))
          }
      }
  }
}
