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
import forms.DeclarationFormProvider
import javax.inject.Inject
import models.{AlreadyRegistered, Mode, RegistrationProgress, RegistrationTRNResponse, TrustResponse, UnableToRegister, UserAnswers}
import navigation.Navigator
import pages.{DeclarationPage, RegistrationTRNPage}
import play.api.Logger
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import repositories.SessionRepository
import services.SubmissionService
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.DeclarationView

import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal

class DeclarationController @Inject()(
                                       override val messagesApi: MessagesApi,
                                       sessionRepository: SessionRepository,
                                       navigator: Navigator,
                                       identify: IdentifierAction,
                                       getData: DataRetrievalAction,
                                       requireData: DataRequiredAction,
                                       formProvider: DeclarationFormProvider,
                                       val controllerComponents: MessagesControllerComponents,
                                       view: DeclarationView,
                                       submissionService: SubmissionService,
                                       registrationComplete : RegistrationCompleteActionRefiner
                                    )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  val form = formProvider()

  def actions = identify andThen getData andThen requireData andThen registrationComplete

  def onPageLoad(mode: Mode): Action[AnyContent] = actions {
    implicit request =>

      val preparedForm = request.userAnswers.get(DeclarationPage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, mode))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = actions.async {
    implicit request =>

      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(view(formWithErrors, mode))),

        value => {

           val r = for {
              updatedAnswers <- Future.fromTry(request.userAnswers.set(DeclarationPage, value))
              _ <- sessionRepository.set(updatedAnswers)
              response <- submissionService.submit(updatedAnswers)
              result <- handleResponse(updatedAnswers, response)
            } yield result

            r.recover {
              case _ : UnableToRegister =>
                Logger.error(s"[onSubmit] Not able to register , redirecting to registration in progress.")
                Redirect(routes.TaskListController.onPageLoad())
              case NonFatal(e) =>
                Logger.error(s"[onSubmit] Non fatal exception, throwing again. ${e.getMessage}")
                throw e
            }

        }
      )
  }

  private def handleResponse(updatedAnswers: UserAnswers, response: TrustResponse) : Future[Result] = {
    response match {
      case trn: RegistrationTRNResponse =>
        Logger.info("[DeclarationController][handleResponse] Saving trust registration trn.")
        saveTRNAndCompleteRegistration(updatedAnswers, trn)
      case AlreadyRegistered =>
        Logger.info(s"[DeclarationController][handleResponse] unable to submit as trust is already registered")
        Future.successful(Redirect(routes.UTRSentByPostController.onPageLoad()))
      case e =>
        Logger.warn(s"[DeclarationController][handleResponse] unable to submit due to error $e")
        Future.successful(Redirect(routes.TaskListController.onPageLoad()))
    }
  }

  private def saveTRNAndCompleteRegistration(updatedAnswers: UserAnswers, trn: RegistrationTRNResponse): Future[Result] = {
      Future.fromTry(updatedAnswers.set(RegistrationTRNPage, trn.trn)).flatMap {
        trnSaved =>
          sessionRepository.set(trnSaved.copy(progress = RegistrationProgress.Complete)).map {
            _ =>
              Redirect(routes.ConfirmationController.onPageLoad())
          }
      }
  }
}
