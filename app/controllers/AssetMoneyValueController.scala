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
import forms.AssetMoneyValueFormProvider
import javax.inject.Inject
import models.{Mode, UserAnswers}
import navigation.Navigator
import pages.AssetMoneyValuePage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.AssetMoneyValueView

import scala.concurrent.{ExecutionContext, Future}

class AssetMoneyValueController @Inject()(
                                        override val messagesApi: MessagesApi,
                                        sessionRepository: SessionRepository,
                                        navigator: Navigator,
                                        identify: IdentifierAction,
                                        getData: DataRetrievalAction,
                                        requireData: DataRequiredAction,
                                        formProvider: AssetMoneyValueFormProvider,
                                        val controllerComponents: MessagesControllerComponents,
                                        view: AssetMoneyValueView
                                    )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  val form = formProvider()

  def onPageLoad(mode: Mode,  index: Int): Action[AnyContent] = (identify andThen getData andThen requireData) {
    implicit request =>

      val preparedForm = request.userAnswers.get(AssetMoneyValuePage(index)) match {
        case None => form
        case v @ Some(_) => form.fill(v)
      }

      Ok(view(preparedForm, mode, index))
  }

  def onSubmit(mode: Mode, index: Int): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      def redirect(userAnswers: UserAnswers) = Redirect(navigator.nextPage(AssetMoneyValuePage(index), mode)(userAnswers))

      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(view(formWithErrors, mode, index))),

        value => {
          value match {
            case Some(v) =>
              for {
                updatedAnswers <- Future.fromTry(request.userAnswers.set(AssetMoneyValuePage(index), v))
                _              <- sessionRepository.set(updatedAnswers)
              } yield redirect(updatedAnswers)
            case None =>
              Future.successful(redirect(request.userAnswers))
          }

        }
      )
  }
}
