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

import java.time.LocalDateTime

import controllers.actions._
import javax.inject.Inject
import models.requests.RegistrationDataRequest
import pages.register.{RegistrationSubmissionDatePage, RegistrationTRNPage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, ActionBuilder, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import utils.DateFormatter
import utils.print.register.PrintUserAnswersHelper
import views.html.register.ConfirmationAnswerPageView

import scala.concurrent.ExecutionContext


class ConfirmationAnswerPageController @Inject()(
                                                  override val messagesApi: MessagesApi,
                                                  val controllerComponents: MessagesControllerComponents,
                                                  view: ConfirmationAnswerPageView,
                                                  registrationComplete : TaskListCompleteActionRefiner,
                                                  printUserAnswersHelper: PrintUserAnswersHelper,
                                                  dateFormatter: DateFormatter,
                                                  actionSet: StandardActionSets
                                                )(implicit ec: ExecutionContext)
  extends FrontendBaseController with I18nSupport {

  private def actions(draftId : String): ActionBuilder[RegistrationDataRequest, AnyContent] =
    actionSet.identifiedUserWithRegistrationData(draftId) andThen registrationComplete

  def onPageLoad(draftId: String): Action[AnyContent] = actions(draftId).async {
    implicit request =>

      printUserAnswersHelper.summary(draftId).map {
        sections =>
          val trn = request.userAnswers.get(RegistrationTRNPage).getOrElse("")

          val trnDateTime = request.userAnswers.get(RegistrationSubmissionDatePage).getOrElse(LocalDateTime.now)

          val declarationSent : String = dateFormatter.formatDateTime(trnDateTime)

          Ok(view(sections, trn, declarationSent, request.userAnswers.isTaxable))
      }
  }
}