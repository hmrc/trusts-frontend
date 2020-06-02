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

package controllers.register.settlors.deceased_settlor

import java.time.LocalDate

import controllers.actions._
import controllers.actions.register.{DraftIdRetrievalActionProvider, RegistrationDataRequiredAction, RegistrationIdentifierAction}
import forms.deceased_settlor.SettlorDateOfDeathFormProvider
import javax.inject.Inject
import models.requests.RegistrationDataRequest
import models.{Mode, NormalMode}
import navigation.Navigator
import pages.register.settlors.deceased_settlor.{SettlorDateOfDeathPage, SettlorsDateOfBirthPage, SettlorsNamePage}
import pages.register.trust_details.WhenTrustSetupPage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.RegistrationsRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.register.settlors.deceased_settlor.SettlorDateOfDeathView

import scala.concurrent.{ExecutionContext, Future}

class SettlorDateOfDeathController @Inject()(
                                              override val messagesApi: MessagesApi,
                                              registrationsRepository: RegistrationsRepository,
                                              navigator: Navigator,
                                              identify: RegistrationIdentifierAction,
                                              getData: DraftIdRetrievalActionProvider,
                                              requireData: RegistrationDataRequiredAction,
                                              formProvider: SettlorDateOfDeathFormProvider,
                                              requiredAnswer: RequiredAnswerActionProvider,
                                              val controllerComponents: MessagesControllerComponents,
                                              view: SettlorDateOfDeathView
                                            )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private def actions(draftId: String) =
    identify andThen
      getData(draftId) andThen
      requireData andThen
      requiredAnswer(RequiredAnswer(SettlorsNamePage, routes.SettlorsNameController.onPageLoad(NormalMode, draftId)))

  private def form(maxDate: (LocalDate, String), minDate: (LocalDate, String)): Form[LocalDate] =
    formProvider.withConfig(maxDate, minDate)

  def onPageLoad(mode: Mode, draftId: String): Action[AnyContent] = actions(draftId) {
    implicit request =>

      val name = request.userAnswers.get(SettlorsNamePage).get

      val preparedForm = request.userAnswers.get(SettlorDateOfDeathPage) match {
        case None => form(maxDate, minDate)
        case Some(value) => form(maxDate, minDate).fill(value)
      }

      Ok(view(preparedForm, mode, draftId, name))
  }

  def onSubmit(mode: Mode, draftId: String): Action[AnyContent] = actions(draftId).async {
    implicit request =>

      val name = request.userAnswers.get(SettlorsNamePage).get

      form(maxDate, minDate).bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(view(formWithErrors, mode, draftId, name))),

        value => {
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(SettlorDateOfDeathPage, value))
            _              <- registrationsRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(SettlorDateOfDeathPage, mode, draftId)(updatedAnswers))
        }
      )
  }

  private def minDate(implicit request: RegistrationDataRequest[AnyContent]): (LocalDate, String) = {
    request.userAnswers.get(SettlorsDateOfBirthPage) match {
      case Some(startDate) =>
        (startDate, "beforeDateOfBirth")
      case None =>
        (LocalDate.of(1500,1,1), "past")
    }
  }

  private def maxDate(implicit request: RegistrationDataRequest[AnyContent]): (LocalDate, String) = {
    request.userAnswers.get(WhenTrustSetupPage) match {
      case Some(startDate) =>
        (startDate, "afterTrustStartDate")
      case None =>
        (LocalDate.now, "future")
    }
  }
}
