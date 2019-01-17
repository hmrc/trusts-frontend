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
import forms.TrustHaveAUTRFormProvider
import javax.inject.Inject
import models.{Mode, UserAnswers}
import navigation.Navigator
import pages.TrustHaveAUTRPage
import play.api.data.Form
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import viewmodels.Link
import views.html.TrustHaveAUTRView

import scala.concurrent.{ExecutionContext, Future}

class TrustHaveAUTRController @Inject()( override val messagesApi: MessagesApi,
                                         sessionRepository: SessionRepository,
                                         navigator: Navigator,
                                         identify: IdentifierAction,
                                         getData: DataRetrievalAction,
                                         requireData: DataRequiredAction,
                                         formProvider: TrustHaveAUTRFormProvider,
                                         val controllerComponents: MessagesControllerComponents,
                                         view: TrustHaveAUTRView
                                 )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  val form: Form[Boolean] = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) {
    implicit request =>

      val preparedForm = request.userAnswers.get(TrustHaveAUTRPage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      val link = Link(Messages("trustHaveAUTR.link"), "https://www.gov.uk/find-lost-utr-number")

      Ok(view(preparedForm, mode, Some(link)))
  }

  def onSubmit(mode: Mode) = (identify andThen getData andThen requireData).async {
    implicit request =>

      val link = Link(Messages("trustHaveAUTR.link"), "https://www.gov.uk/find-lost-utr-number")

      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(view(formWithErrors, mode, Some(link)))),

        value => {
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(TrustHaveAUTRPage, value))
            _              <- sessionRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(TrustHaveAUTRPage, mode)(updatedAnswers))
        }
      )
  }
}
