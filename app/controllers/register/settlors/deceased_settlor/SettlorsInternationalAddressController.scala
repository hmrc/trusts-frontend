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

package controllers.register.settlors.deceased_settlor

import controllers.actions._
import controllers.actions.register.RegistrationIdentifierAction
import forms.InternationalAddressFormProvider
import javax.inject.Inject
import models.{Mode, NormalMode}
import navigation.Navigator
import pages.register.settlors.deceased_settlor.{SettlorsInternationalAddressPage, SettlorsNamePage}
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.RegistrationsRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import utils.countryOptions.CountryOptionsNonUK
import views.html.register.settlors.deceased_settlor.SettlorsInternationalAddressView

import scala.concurrent.{ExecutionContext, Future}

class SettlorsInternationalAddressController @Inject()(
                                                        override val messagesApi: MessagesApi,
                                                        registrationsRepository: RegistrationsRepository,
                                                        navigator: Navigator,
                                                        identify: RegistrationIdentifierAction,
                                                        getData: DraftIdRetrievalActionProvider,
                                                        requireData: DataRequiredAction,
                                                        formProvider: InternationalAddressFormProvider,
                                                        requiredAnswer: RequiredAnswerActionProvider,
                                                        val controllerComponents: MessagesControllerComponents,
                                                        view: SettlorsInternationalAddressView,
                                                        val countryOptions: CountryOptionsNonUK
                                                      )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  val form = formProvider()

  private def actions(draftId: String) =
    identify andThen
      getData(draftId) andThen
      requireData andThen
      requiredAnswer(RequiredAnswer(SettlorsNamePage, routes.SettlorsNameController.onPageLoad(NormalMode, draftId)))

  def onPageLoad(mode: Mode, draftId: String): Action[AnyContent] = actions(draftId) {
    implicit request =>

      val name = request.userAnswers.get(SettlorsNamePage).get

      val preparedForm = request.userAnswers.get(SettlorsInternationalAddressPage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, countryOptions.options, mode, draftId, name))
  }

  def onSubmit(mode: Mode, draftId: String): Action[AnyContent] = actions(draftId).async {
    implicit request =>

      val name = request.userAnswers.get(SettlorsNamePage).get

      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(view(formWithErrors, countryOptions.options, mode, draftId, name))),

        value => {
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(SettlorsInternationalAddressPage, value))
            _              <- registrationsRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(SettlorsInternationalAddressPage, mode, draftId)(updatedAnswers))
        }
      )
  }
}
