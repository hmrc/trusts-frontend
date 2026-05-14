/*
 * Copyright 2026 HM Revenue & Customs
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

import controllers.actions.register.RequireDraftRegistrationActionRefiner
import controllers.actions.{StandardActionSets, TaskListCompleteActionRefiner}
import models.requests.RegistrationDataRequest
import pages.register.RegistrationTRNPage
import play.api.Logging
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, ActionBuilder, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.register.MissingSettlorView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class MissingSettlorController @Inject() (
  override val messagesApi: MessagesApi,
  val controllerComponents: MessagesControllerComponents,
  missingSettlorView: MissingSettlorView,
  registrationComplete: TaskListCompleteActionRefiner,
  requireDraft: RequireDraftRegistrationActionRefiner,
  standardAction: StandardActionSets
) extends FrontendBaseController with I18nSupport with Logging {

  def actions(draftId: String): ActionBuilder[RegistrationDataRequest, AnyContent] =
    standardAction.identifiedUserWithRegistrationData(draftId) andThen registrationComplete andThen requireDraft

  def onPageLoad(draftId: String): Action[AnyContent] = actions(draftId).async { implicit request =>
    request.userAnswers.get(RegistrationTRNPage) match {
      case Some(trn) => Future.successful(Ok(missingSettlorView(trn)))
      case None      => Future.successful(Redirect(routes.SessionExpiredController.onPageLoad()))
    }
  }

}
