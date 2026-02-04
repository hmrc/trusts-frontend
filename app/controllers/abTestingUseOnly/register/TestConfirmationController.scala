/*
 * Copyright 2024 HM Revenue & Customs
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

package controllers.abTestingUseOnly.register

import controllers.abTestingUseOnly.routes.TestSignOutController
import controllers.actions.register.{
  ConfirmationIdentifierAction, DraftIdRetrievalActionProvider, RegistrationDataRequiredAction
}
import controllers.register.routes
import handlers.ErrorHandler
import models.core.UserAnswers
import models.core.http.LeadTrusteeType
import models.registration.pages.RegistrationStatus
import models.requests.RegistrationDataRequest
import pages.register.{RegistrationTRNPage, TrustHaveAUTRPage}
import play.api.Logging
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import repositories.RegistrationsRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.abTestingUseOnly.register.confirmation._
import views.html.register.confirmation.{existingTrust, newTrust}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class TestConfirmationController @Inject() (
  override val messagesApi: MessagesApi,
  identify: ConfirmationIdentifierAction,
  getData: DraftIdRetrievalActionProvider,
  requireData: RegistrationDataRequiredAction,
  val controllerComponents: MessagesControllerComponents,
  newTaxableIndividualView: newTrust.taxable.IndividualView,
  newTaxableAgentView: newTrust.taxable.AgentView,
  testNonTaxableIndividualView: nonTaxable.TestIndividualView,
  nonTaxableAgentView: newTrust.nonTaxable.AgentView,
  existingTaxableIndividualView: existingTrust.IndividualView,
  existingTaxableAgentView: existingTrust.AgentView,
  errorHandler: ErrorHandler,
  registrationsRepository: RegistrationsRepository
)(implicit ec: ExecutionContext)
    extends FrontendBaseController with I18nSupport with Logging {

  def onPageLoad(draftId: String): Action[AnyContent] = (identify andThen getData(draftId) andThen requireData).async {
    implicit request =>
      val userAnswers = request.userAnswers
      handleOutcome(draftId = draftId, userAnswers = userAnswers, methodName = "onPageLoad")
  }

  def onSubmit(draftId: String): Action[AnyContent] = (identify andThen getData(draftId) andThen requireData).async {
    implicit request =>
      val userAnswers = request.userAnswers
      handleOutcome(draftId = draftId, userAnswers = userAnswers, methodName = "onSubmit", isGetRequest = false)
  }

  private def handleOutcome(
    draftId: String,
    userAnswers: UserAnswers,
    methodName: String,
    isGetRequest: Boolean = true
  )(implicit request: RegistrationDataRequest[AnyContent]): Future[Result] =
    userAnswers.progress match {
      case RegistrationStatus.Complete   =>
        userAnswers.get(RegistrationTRNPage) match {
          case None      =>
            infoLogger(methodName, message = "No TRN available for completed trusts. Throwing exception.")
            errorHandler.onServerError(request, new Exception("TRN is not available for completed trust."))
          case Some(trn) =>
            renderViewOrRedirect(trn, userAnswers, draftId, isGetRequest)
        }
      case RegistrationStatus.InProgress =>
        infoLogger(methodName, message = "Registration inProgress status, redirecting to task list.")
        Future.successful(Redirect(routes.TaskListController.onPageLoad(draftId)))
      case RegistrationStatus.NotStarted =>
        infoLogger(methodName, message = "Registration NotStarted status, redirecting to trust registered page online.")
        Future.successful(Redirect(routes.TrustRegisteredOnlineController.onPageLoad()))
    }

  private def renderViewOrRedirect(trn: String, userAnswers: UserAnswers, draftId: String, isGetRequest: Boolean)(
    implicit request: RegistrationDataRequest[AnyContent]
  ): Future[Result] = {
    val isAgent = request.isAgent
    registrationsRepository.getLeadTrustee(draftId) flatMap {
      case LeadTrusteeType(Some(ltInd), None) =>
        renderOrRedirect(userAnswers, draftId, isAgent, trn, ltInd.name.toString, isGetRequest)
      case LeadTrusteeType(None, Some(ltOrg)) =>
        renderOrRedirect(userAnswers, draftId, isAgent, trn, ltOrg.name, isGetRequest)
      case _                                  => errorHandler.onServerError(request, new Exception("Could not retrieve lead trustee from user answers."))
    }
  }

  private def renderOrRedirect(
    userAnswers: UserAnswers,
    draftId: String,
    isAgent: Boolean,
    trn: String,
    name: String,
    isGetRequest: Boolean
  )(implicit request: RegistrationDataRequest[AnyContent]): Future[Result] = {

    val utr     = userAnswers.get(TrustHaveAUTRPage)
    val taxable = userAnswers.isTaxable

    (utr, taxable) match {
      case (Some(true), true) if isAgent   =>
        Future.successful(Ok(existingTaxableAgentView(draftId, trn, name)))
      case (Some(true), true)              =>
        Future.successful(Ok(existingTaxableIndividualView(draftId, trn, name)))
      case (Some(false), true) if isAgent  =>
        Future.successful(Ok(newTaxableAgentView(draftId, trn, name)))
      case (Some(false), true)             =>
        Future.successful(Ok(newTaxableIndividualView(draftId, trn, name)))
      case (Some(false), false) if isAgent =>
        Future.successful(Ok(nonTaxableAgentView(draftId, trn, name)))
      case (Some(false), false)            =>
        nonTaxableIndividualResult(draftId, trn, isGetRequest)
      case _                               =>
        errorHandler.onServerError(request, new Exception("Could not determine if trust was new or existing."))
    }
  }

  private def nonTaxableIndividualResult(draftId: String, trn: String, isGetRequest: Boolean)(implicit
    request: RegistrationDataRequest[AnyContent]
  ): Future[Result] =
    if (isGetRequest) {
      infoLogger(
        methodName = "nonTaxableIndividualResult",
        message = "TRN available, displaying confirmation page for A/B testing"
      )
      Future.successful(Ok(testNonTaxableIndividualView(draftId, trn)))
    } else {
      infoLogger(
        methodName = "nonTaxableIndividualResult",
        message = "Redirecting to TestSignOutController for A/B testing"
      )
      Future.successful(Redirect(TestSignOutController.onPageLoad()))
    }

  private def infoLogger(methodName: String, message: String)(implicit
    request: RegistrationDataRequest[AnyContent]
  ): Unit = {
    val className = "TestConfirmationController"
    logger.info(s"[$className][$methodName][Session ID: ${request.sessionId}] $message")
  }

}
