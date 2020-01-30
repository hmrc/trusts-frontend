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

import config.FrontendAppConfig
import controllers.actions.register.{DraftIdRetrievalActionProvider, RegistrationDataRequiredAction, RegistrationIdentifierAction}
import handlers.ErrorHandler
import javax.inject.Inject
import mapping.reads.{LeadTrusteeIndividual, Trustees}
import models.NormalMode
import models.core.UserAnswers
import models.registration.pages.RegistrationStatus
import models.requests.RegistrationDataRequest
import pages.register.{RegistrationTRNPage, TrustHaveAUTRPage}
import play.api.Logger
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import uk.gov.hmrc.auth.core.AffinityGroup.Agent
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.register.{ConfirmationIndividualView, ConfirmationAgentView, ConfirmationExistingView}

import scala.concurrent.{ExecutionContext, Future}

class ConfirmationController @Inject()(
                                        override val messagesApi: MessagesApi,
                                        identify: RegistrationIdentifierAction,
                                        getData: DraftIdRetrievalActionProvider,
                                        requireData: RegistrationDataRequiredAction,
                                        config: FrontendAppConfig,
                                        val controllerComponents: MessagesControllerComponents,
                                        viewIndividual: ConfirmationIndividualView,
                                        viewAgent: ConfirmationAgentView,
                                        viewExisting: ConfirmationExistingView,
                                        errorHandler: ErrorHandler
                                     )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private def renderView(trn : String, userAnswers: UserAnswers, draftId: String)(implicit request : RegistrationDataRequest[AnyContent]) : Future[Result] = {

    val trustees = userAnswers.get(Trustees).getOrElse(Nil)

    val isAgent = request.affinityGroup == Agent

    trustees.find(_.isLead) match {
      case Some(lt : LeadTrusteeIndividual) =>

        userAnswers.get(TrustHaveAUTRPage) match {
          case Some(true) =>
            Future.successful(Ok(viewExisting(draftId, isAgent, trn, lt.name)))
          case Some(false) if isAgent =>
            Future.successful(Ok(viewAgent(draftId, trn, lt.name)))
          case Some(false) =>
            Future.successful(Ok(viewIndividual(draftId, trn, lt.name)))
          case None =>
            errorHandler.onServerError(request, new Exception("Could not determine if trust was new or existing."))
        }
      case _ =>
        errorHandler.onServerError(request, new Exception("Could not retrieve lead trustee from user answers."))
    }

  }

  def onPageLoad(draftId: String): Action[AnyContent] = (identify andThen getData(draftId) andThen requireData).async {
    implicit request =>
      val userAnswers = request.userAnswers

       userAnswers.progress match {
        case RegistrationStatus.Complete =>
          userAnswers.get(RegistrationTRNPage) match {
            case None =>
              Logger.info("[ConfirmationController][onPageLoad] No TRN available for completed trusts. Throwing exception.")
              errorHandler.onServerError(request, new Exception("TRN is not available for completed trust."))
            case Some(trn) =>
              renderView(trn, userAnswers, draftId)
          }
        case RegistrationStatus.InProgress =>
          Logger.info("[ConfirmationController][onPageLoad] Registration inProgress status,redirecting to task list.")
          Future.successful(Redirect(routes.TaskListController.onPageLoad(draftId)))
        case RegistrationStatus.NotStarted =>
          Logger.info("[ConfirmationController][onPageLoad] Registration NotStarted status,redirecting to trust registered page online.")
          Future.successful(Redirect(routes.TrustRegisteredOnlineController.onPageLoad(NormalMode, draftId)))
      }
  }
}
