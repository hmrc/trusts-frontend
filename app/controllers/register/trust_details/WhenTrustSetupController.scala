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

import java.time.LocalDate

import config.FrontendAppConfig
import connector.SubmissionDraftConnector
import controllers.actions.register.{DraftIdRetrievalActionProvider, RegistrationDataRequiredAction, RegistrationIdentifierAction}
import forms.WhenTrustSetupFormProvider
import javax.inject.Inject
import models.Mode
import models.requests.RegistrationDataRequest
import navigation.Navigator
import pages.register.settlors.deceased_settlor.SettlorDateOfDeathPage
import pages.register.trust_details.WhenTrustSetupPage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.RegistrationsRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.register.trust_details.WhenTrustSetupView

import scala.concurrent.{ExecutionContext, Future}

class WhenTrustSetupController @Inject()(
                                          override val messagesApi: MessagesApi,
                                          registrationsRepository: RegistrationsRepository,
                                          submissionDraftConnector: SubmissionDraftConnector,
                                          navigator: Navigator,
                                          identify: RegistrationIdentifierAction,
                                          getData: DraftIdRetrievalActionProvider,
                                          requireData: RegistrationDataRequiredAction,
                                          formProvider: WhenTrustSetupFormProvider,
                                          val controllerComponents: MessagesControllerComponents,
                                          view: WhenTrustSetupView,
                                          appConfig: FrontendAppConfig
                                        )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {


 private def actions(draftId: String) = identify andThen getData(draftId) andThen requireData

  private def form(config: (LocalDate, String)): Form[LocalDate] =
    formProvider.withConfig(config)

  def onPageLoad(mode: Mode, draftId: String): Action[AnyContent] = actions(draftId) {
    implicit request =>

      val preparedForm = request.userAnswers.get(WhenTrustSetupPage) match {
        case None => form(minDate)
        case Some(value) => form(minDate).fill(value)
      }

      Ok(view(preparedForm, mode, draftId))
  }

  def onSubmit(mode: Mode, draftId: String): Action[AnyContent] = actions(draftId).async {
    implicit request =>

      form(minDate).bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(view(formWithErrors, mode, draftId))),

        value =>
          for {
           _ <- {
             val previousAnswer = request.userAnswers.get(WhenTrustSetupPage)
             if (previousAnswer.isDefined && !previousAnswer.contains(value)) {
               submissionDraftConnector.resetTaxLiability(draftId).map(_ => ())
             } else {
               Future.successful(())
             }
           }
            updatedAnswers <- Future.fromTry(request.userAnswers.set(WhenTrustSetupPage, value))
            _              <- registrationsRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(WhenTrustSetupPage, mode, draftId)(updatedAnswers))
      )
  }

  private def minDate(implicit request: RegistrationDataRequest[AnyContent]): (LocalDate, String) = {
    request.userAnswers.get(SettlorDateOfDeathPage) match {
      case Some(dateOfDeath) =>
        (dateOfDeath, "beforeDateOfDeath")
      case None =>
        (appConfig.minDate, "past")
    }
  }
}

