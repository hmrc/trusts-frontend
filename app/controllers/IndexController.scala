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

import controllers.actions.{DataRetrievalAction, IdentifierAction}
import javax.inject.Inject
import models.NormalMode
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import services.CreateDraftRegistrationService
import uk.gov.hmrc.auth.core.AffinityGroup
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.IndexView

import scala.concurrent.{ExecutionContext, Future}

class IndexController @Inject()(
                                 identify: IdentifierAction,
                                 getData: DataRetrievalAction,
                                 sessionRepository: SessionRepository,
                                 val controllerComponents: MessagesControllerComponents,
                                 view: IndexView,
                                 draftService : CreateDraftRegistrationService
                               )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  def onPageLoad: Action[AnyContent] = (identify andThen getData).async {
    implicit request =>

      def routeAffinityGroupNotStarted = {
        request.affinityGroup match {
          case AffinityGroup.Agent =>
            Redirect(routes.AgentOverviewController.onPageLoad())
          case _ =>
            Redirect(routes.TrustRegisteredOnlineController.onPageLoad(NormalMode))
        }
      }

      def routeAffinityGroupInProgress = {
        request.affinityGroup match {
          case AffinityGroup.Agent =>
            Redirect(routes.AgentOverviewController.onPageLoad())
          case _ =>
            Redirect(routes.TaskListController.onPageLoad())
        }
      }

      request.userAnswers match {
        case Some(_) =>
          Future.successful(routeAffinityGroupInProgress)
        case None =>
          draftService.create(request, routeAffinityGroupNotStarted)
      }
  }
}
