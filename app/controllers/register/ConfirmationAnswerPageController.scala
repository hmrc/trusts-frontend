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

package controllers.register

import java.time.LocalDateTime

import controllers.actions._
import controllers.actions.register.RegistrationIdentifierAction
import javax.inject.Inject
import pages.register.{RegistrationSubmissionDatePage, RegistrationTRNPage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.MessagesControllerComponents
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import utils.countryOptions.CountryOptions
import utils.{DateFormatter, PrintUserAnswersHelper}
import views.html.register.ConfirmationAnswerPageView


class ConfirmationAnswerPageController @Inject()(
                                                  override val messagesApi: MessagesApi,
                                                  identify: RegistrationIdentifierAction,
                                                  getData: DraftIdRetrievalActionProvider,
                                                  requireData: DataRequiredAction,
                                                  val controllerComponents: MessagesControllerComponents,
                                                  view: ConfirmationAnswerPageView,
                                                  countryOptions : CountryOptions,
                                                  registrationComplete : TaskListCompleteActionRefiner,
                                                  printUserAnswersHelper: PrintUserAnswersHelper,
                                                  dateFormatter: DateFormatter
                                            ) extends FrontendBaseController with I18nSupport {

  private def actions(draftId : String) =
    identify andThen getData(draftId) andThen requireData andThen registrationComplete

  def onPageLoad(draftId: String) = actions(draftId) {
    implicit request =>

      val sections = printUserAnswersHelper.summary(draftId, request.userAnswers)

      val trn = request.userAnswers.get(RegistrationTRNPage).getOrElse("")

      val trnDateTime = request.userAnswers.get(RegistrationSubmissionDatePage).getOrElse(LocalDateTime.now)

      val declarationSent : String = dateFormatter.formatDate(trnDateTime)

      Ok(view(sections, trn, declarationSent))

  }

}