/*
 * Copyright 2021 HM Revenue & Customs
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

package controllers.register

import controllers.actions._
import controllers.actions.register.RequireDraftRegistrationActionRefiner
import forms.DeclarationFormProvider
import models.Mode
import models.core.UserAnswers
import models.core.http.TrustResponse._
import models.core.http.{RegistrationTRNResponse, TrustResponse}
import models.core.pages.Declaration
import models.registration.pages.RegistrationStatus
import models.requests.RegistrationDataRequest
import pages.register.{DeclarationPage, RegistrationSubmissionDatePage, RegistrationTRNPage}
import play.api.Logging
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import repositories.RegistrationsRepository
import services.{FeatureFlagService, SubmissionService}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.HeaderCarrierConverter
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.register.DeclarationView

import java.time.temporal.ChronoUnit.DAYS
import java.time.{LocalDateTime, ZoneOffset}
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal

class DeclarationController @Inject()(
                                       override val messagesApi: MessagesApi,
                                       registrationsRepository: RegistrationsRepository,
                                       formProvider: DeclarationFormProvider,
                                       featureFlagService: FeatureFlagService,
                                       val controllerComponents: MessagesControllerComponents,
                                       view: DeclarationView,
                                       submissionService: SubmissionService,
                                       registrationComplete : TaskListCompleteActionRefiner,
                                       requireDraft : RequireDraftRegistrationActionRefiner,
                                       standardAction: StandardActionSets
                                    )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with Logging {

  private val form: Form[Declaration] = formProvider()

  def actions(draftId: String): ActionBuilder[RegistrationDataRequest, AnyContent] =
    standardAction.identifiedUserWithData(draftId) andThen registrationComplete andThen requireDraft

  def onPageLoad(mode: Mode, draftId: String): Action[AnyContent] = actions(draftId) {
    implicit request =>

      val preparedForm = request.userAnswers.get(DeclarationPage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, mode, draftId,request.affinityGroup))
  }

  def onSubmit(mode: Mode, draftId: String): Action[AnyContent] = actions(draftId).async {
    implicit request =>
      implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromHeadersAndSession(request.headers, Some(request.session))

      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(view(formWithErrors, mode, draftId, request.affinityGroup))),

        value => {

          val r = for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(DeclarationPage, value))
            _ <- registrationsRepository.set(updatedAnswers)
            is5mldEnabled <- featureFlagService.is5mldEnabled()
            response <- submissionService.submit(updatedAnswers, is5mldEnabled)
            result <- handleResponse(updatedAnswers, response, draftId)
          } yield result

          r.recover {
            case _ : UnableToRegister =>
              logger.error(s"[onSubmit][Session ID: ${request.sessionId}] Not able to register, redirecting to registration in progress.")
              Redirect(routes.TaskListController.onPageLoad(draftId))
            case NonFatal(e) =>
              logger.error(s"[onSubmit][Session ID: ${request.sessionId}] Non fatal exception, throwing again. ${e.getMessage}")
              throw e
          }
        }
      )
  }

  private def handleResponse(updatedAnswers: UserAnswers, response: TrustResponse, draftId: String)
                            (implicit hc: HeaderCarrier, request: RegistrationDataRequest[AnyContent]): Future[Result] = {
    response match {
      case trn: RegistrationTRNResponse =>
        logger.info(s"[handleResponse][Session ID: ${request.sessionId}] Saving trust registration trn.")
        saveTRNAndCompleteRegistration(updatedAnswers, trn)
      case AlreadyRegistered =>
        logger.info(s"[handleResponse][Session ID: ${request.sessionId}] unable to submit as trust is already registered")
        Future.successful(Redirect(routes.UTRSentByPostController.onPageLoad()))
      case e =>
        logger.warn(s"[handleResponse][Session ID: ${request.sessionId}] unable to submit due to error $e")
        Future.successful(Redirect(routes.TaskListController.onPageLoad(draftId)))
    }
  }

  private def saveTRNAndCompleteRegistration(updatedAnswers: UserAnswers, trn: RegistrationTRNResponse)
                                            (implicit hc: HeaderCarrier, request: RegistrationDataRequest[AnyContent]): Future[Result] = {
    Future.fromTry(updatedAnswers.set(RegistrationTRNPage, trn.trn)).flatMap {
      trnSaved =>
        val submissionDate = LocalDateTime.now(ZoneOffset.UTC)
        Future.fromTry(trnSaved.set(RegistrationSubmissionDatePage, submissionDate)).flatMap {
          dateSaved =>
            val days = DAYS.between(updatedAnswers.createdAt, submissionDate)
            logger.info(s"[saveTRNAndCompleteRegistration][Session ID: ${request.sessionId}] Days between creation and submission : $days")
            registrationsRepository.set(dateSaved.copy(progress = RegistrationStatus.Complete)).map {
              _ =>
                Redirect(routes.ConfirmationController.onPageLoad(updatedAnswers.draftId))
            }
        }
    }
  }
}
