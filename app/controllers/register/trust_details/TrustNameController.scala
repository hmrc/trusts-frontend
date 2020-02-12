/*
 * Copyright 2020 HM Revenue & Customs
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

package controllers.register.trust_details

import controllers.actions.register.{DraftIdRetrievalActionProvider, RegistrationDataRequiredAction, RegistrationIdentifierAction}
import forms.TrustNameFormProvider
import javax.inject.Inject
import models.Mode
import navigation.Navigator
import pages.register.TrustHaveAUTRPage
import pages.register.trust_details.TrustNamePage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.RegistrationsRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.register.trust_details.TrustNameView

import scala.concurrent.{ExecutionContext, Future}

class TrustNameController @Inject()(
                                     override val messagesApi: MessagesApi,
                                     registrationsRepository: RegistrationsRepository,
                                     navigator: Navigator,
                                     identify: RegistrationIdentifierAction,
                                     getData: DraftIdRetrievalActionProvider,
                                     requireData: RegistrationDataRequiredAction,
                                     formProvider: TrustNameFormProvider,
                                     val controllerComponents: MessagesControllerComponents,
                                     view: TrustNameView
                                    )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private def actions(draftId: String) = identify andThen getData(draftId) andThen requireData

  val form = formProvider()

  def onPageLoad(mode: Mode, draftId: String): Action[AnyContent] = actions(draftId) {
    implicit request =>

      val preparedForm = request.userAnswers.get(TrustNamePage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      val hintTextShown = request.userAnswers.get(TrustHaveAUTRPage).contains(true)

      Ok(view(preparedForm, mode, draftId, hintTextShown))
  }

  def onSubmit(mode: Mode, draftId: String): Action[AnyContent] = actions(draftId: String).async {
    implicit request =>

      val hintTextShown = request.userAnswers.get(TrustHaveAUTRPage).contains(true)

      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(view(formWithErrors, mode, draftId, hintTextShown))),

        value => {
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(TrustNamePage, value))
            _              <- registrationsRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(TrustNamePage, mode, draftId)(updatedAnswers))
        }
      )
  }
}
