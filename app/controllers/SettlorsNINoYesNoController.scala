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

import controllers.actions._
import forms.SettlorsNINoYesNoFormProvider
import javax.inject.Inject
import models.{Mode, NormalMode, UserAnswers}
import navigation.Navigator
import pages.{SettlorsNINoYesNoPage, SettlorsNamePage}
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.SettlorsNINoYesNoView

import scala.concurrent.{ExecutionContext, Future}

class SettlorsNINoYesNoController @Inject()(
                                         override val messagesApi: MessagesApi,
                                         sessionRepository: SessionRepository,
                                         navigator: Navigator,
                                         identify: IdentifierAction,
                                         getData: DataRetrievalAction,
                                         requireData: DataRequiredAction,
                                         formProvider: SettlorsNINoYesNoFormProvider,
                                         requiredAnswer: RequiredAnswerActionProvider,
                                         val controllerComponents: MessagesControllerComponents,
                                         view: SettlorsNINoYesNoView
                                 )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  val form: Form[Boolean] = formProvider()

  private def actions() =
    identify andThen
      getData andThen
      requireData andThen
      requiredAnswer(RequiredAnswer(SettlorsNamePage, routes.SettlorsNameController.onPageLoad(NormalMode)))

  def onPageLoad(mode: Mode): Action[AnyContent] = actions() {
    implicit request =>

      val preparedForm = request.userAnswers.get(SettlorsNINoYesNoPage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, mode))
  }

  def onSubmit(mode: Mode) = actions().async {
    implicit request =>

      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(view(formWithErrors, mode))),

        value => {
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(SettlorsNINoYesNoPage, value))
            _              <- sessionRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(SettlorsNINoYesNoPage, mode)(updatedAnswers))
        }
      )
  }
}
