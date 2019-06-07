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

import models.{NormalMode, RegistrationProgress}
import models.RegistrationProgress.Complete
import pages.RegistrationTRNPage
import play.api.Logger
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.ConfirmationView

import scala.concurrent.ExecutionContext

class ConfirmationController @Inject()(
                                       override val messagesApi: MessagesApi,
                                       identify: IdentifierAction,
                                       getData: DataRetrievalAction,
                                       requireData: DataRequiredAction,
                                       config: FrontendAppConfig,
                                       val controllerComponents: MessagesControllerComponents,
                                       view: ConfirmationView
                                     )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  def onPageLoad: Action[AnyContent] = (identify andThen getData andThen requireData) {
    implicit request =>
      val userAnswers = request.userAnswers
       userAnswers.progress match {
        case  Complete => userAnswers.get(RegistrationTRNPage) match {
          case None =>
            Logger.error("[ConfirmationController][onPageLoad] No TRN available for completed trusts. Throwing exception.")
            throw new Exception("TRN is not available for completed trust.")

          case Some(trn) =>
            val postHMRC = config.posthmrc
            Ok(view(trn, postHMRC))
        }
        case RegistrationProgress.InProgress =>
          Logger.error("[ConfirmationController][onPageLoad] Registration inProgress status,redirecting to task list.")
          Redirect(routes.TaskListController.onPageLoad())
        case RegistrationProgress.NotStarted =>
          Logger.error("[ConfirmationController][onPageLoad] Registration NotStarted status,redirecting to trust registered page online.")
          Redirect(routes.TrustRegisteredOnlineController.onPageLoad(NormalMode))
      }
  }
}
