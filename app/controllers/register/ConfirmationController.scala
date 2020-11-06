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

package controllers.register

import controllers.actions.register.{DraftIdRetrievalActionProvider, RegistrationDataRequiredAction, RegistrationIdentifierAction}
import handlers.ErrorHandler
import javax.inject.Inject
import mapping.registration.LeadTrusteeType
import models.NormalMode
import models.core.UserAnswers
import models.registration.pages.RegistrationStatus
import models.requests.RegistrationDataRequest
import pages.register.{RegistrationTRNPage, TrustHaveAUTRPage}
import play.api.Logger
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import repositories.RegistrationsRepository
import uk.gov.hmrc.auth.core.AffinityGroup.Agent
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.register.confirmation._

import scala.concurrent.{ExecutionContext, Future}

class ConfirmationController @Inject()(
                                        override val messagesApi: MessagesApi,
                                        identify: RegistrationIdentifierAction,
                                        getData: DraftIdRetrievalActionProvider,
                                        requireData: RegistrationDataRequiredAction,
                                        val controllerComponents: MessagesControllerComponents,
                                        newIndividualView: newTrust.IndividualView,
                                        newAgentView: newTrust.AgentView,
                                        existingIndividualView: existingTrust.IndividualView,
                                        existingAgentView: existingTrust.AgentView,
                                        errorHandler: ErrorHandler,
                                        registrationsRepository: RegistrationsRepository
                                     )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private val logger: Logger = Logger(getClass)

  private def renderView(trn : String, userAnswers: UserAnswers, draftId: String)(implicit request : RegistrationDataRequest[AnyContent]) : Future[Result] = {
    val isAgent = request.affinityGroup == Agent
    registrationsRepository.getLeadTrustee(draftId) flatMap {
      case LeadTrusteeType(Some(ltInd), None) => render(userAnswers, draftId, isAgent, trn, ltInd.name.toString)
      case LeadTrusteeType(None, Some(ltOrg)) => render(userAnswers, draftId, isAgent, trn, ltOrg.name)
      case _ => errorHandler.onServerError(request, new Exception("Could not retrieve lead trustee from user answers."))
    }
  }

  private def render(userAnswers: UserAnswers,
                      draftId: String,
                      isAgent: Boolean,
                      trn: String,
                      name: String)(implicit request : RegistrationDataRequest[AnyContent]) = {

    userAnswers.get(TrustHaveAUTRPage) match {
      case Some(true) if isAgent =>
        Future.successful(Ok(existingAgentView(draftId, trn, name)))
      case Some(true) =>
        Future.successful(Ok(existingIndividualView(draftId, trn, name)))
      case Some(false) if isAgent =>
        Future.successful(Ok(newAgentView(draftId, trn, name)))
      case Some(false) =>
        Future.successful(Ok(newIndividualView(draftId, trn, name)))
      case None =>
        errorHandler.onServerError(request, new Exception("Could not determine if trust was new or existing."))
    }

  }

  def onPageLoad(draftId: String): Action[AnyContent] = (identify andThen getData(draftId) andThen requireData).async {
    implicit request =>
      val userAnswers = request.userAnswers

       userAnswers.progress match {
        case RegistrationStatus.Complete =>
          userAnswers.get(RegistrationTRNPage) match {
            case None =>
              logger.info(s"[onPageLoad][Session ID: ${request.sessionId}] No TRN available for completed trusts. Throwing exception.")
              errorHandler.onServerError(request, new Exception("TRN is not available for completed trust."))
            case Some(trn) =>
              renderView(trn, userAnswers, draftId)
          }
        case RegistrationStatus.InProgress =>
          logger.info(s"[onPageLoad][Session ID: ${request.sessionId}] Registration inProgress status,redirecting to task list.")
          Future.successful(Redirect(routes.TaskListController.onPageLoad(draftId)))
        case RegistrationStatus.NotStarted =>
          logger.info(s"[onPageLoad][Session ID: ${request.sessionId}] Registration NotStarted status,redirecting to trust registered page online.")
          Future.successful(Redirect(routes.TrustRegisteredOnlineController.onPageLoad(NormalMode, draftId)))
      }
  }
}
