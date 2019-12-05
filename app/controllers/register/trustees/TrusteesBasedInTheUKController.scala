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

package controllers.register.trustees

import controllers.actions._
import forms.TrusteesBasedInTheUKFormProvider
import javax.inject.Inject
import models.{Enumerable, Mode}
import navigation.Navigator
import pages.register.trustees.TrusteesBasedInTheUKPage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.RegistrationsRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.register.trustees.TrusteesBasedInTheUKView

import scala.concurrent.{ExecutionContext, Future}

class TrusteesBasedInTheUKController @Inject()(
                                                override val messagesApi: MessagesApi,
                                                registrationsRepository: RegistrationsRepository,
                                                navigator: Navigator,
                                                identify: IdentifierAction,
                                                getData: DraftIdRetrievalActionProvider,
                                                requireData: DataRequiredAction,
                                                formProvider: TrusteesBasedInTheUKFormProvider,
                                                val controllerComponents: MessagesControllerComponents,
                                                view: TrusteesBasedInTheUKView
                                     )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with Enumerable.Implicits {

  val form = formProvider()

  def onPageLoad(mode: Mode, draftId: String): Action[AnyContent] = (identify andThen getData(draftId) andThen requireData) {
    implicit request =>

      val preparedForm = request.userAnswers.get(TrusteesBasedInTheUKPage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, mode, draftId))
  }

  def onSubmit(mode: Mode, draftId: String): Action[AnyContent] = (identify andThen getData(draftId) andThen requireData).async {
    implicit request =>

      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(view(formWithErrors, mode, draftId))),

        value => {
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(TrusteesBasedInTheUKPage, value))
            _              <- registrationsRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(TrusteesBasedInTheUKPage, mode, draftId)(updatedAnswers))
        }
      )
  }
}
