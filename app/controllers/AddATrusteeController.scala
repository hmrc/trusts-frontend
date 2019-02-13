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
import forms.AddATrusteeFormProvider
import javax.inject.Inject
import models.{Enumerable, Mode, TrusteeOrIndividual}
import navigation.Navigator
import pages.{AddATrusteePage, Trustees}
import play.api.Logger
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import utils.AddATrusteeViewHelper
import viewmodels.TrusteeRow
import views.html.AddATrusteeView

import scala.concurrent.{ExecutionContext, Future}

class AddATrusteeController @Inject()(
                                       override val messagesApi: MessagesApi,
                                       sessionRepository: SessionRepository,
                                       navigator: Navigator,
                                       identify: IdentifierAction,
                                       getData: DataRetrievalAction,
                                       requireData: DataRequiredAction,
                                       formProvider: AddATrusteeFormProvider,
                                       val controllerComponents: MessagesControllerComponents,
                                       view: AddATrusteeView
                                     )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with Enumerable.Implicits {

  val form = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) {
    implicit request =>

      val preparedForm = request.userAnswers.get(AddATrusteePage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      val trustees = new AddATrusteeViewHelper(request.userAnswers).rows

      Ok(view(preparedForm, mode, trustees, Nil))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(view(formWithErrors, mode, Nil, Nil))),

        value => {

//          val currentTrustees = request.userAnswers.get(Trustees).getOrElse(List.empty)
//          val pushObject = currentTrustees ::: List(JsObject.empty)

          for {
//            updatedTrustees <- Future.fromTry(request.userAnswers.set(Trustees, pushObject))
            updatedAnswers <- Future.fromTry(request.userAnswers.set(AddATrusteePage, value))
            _              <- sessionRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(AddATrusteePage, mode)(updatedAnswers))
        }
      )
  }
}
