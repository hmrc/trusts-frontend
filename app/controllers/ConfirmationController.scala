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

import config.FrontendAppConfig
import controllers.actions._
import handlers.ErrorHandler
import javax.inject.Inject
import models.entities.{LeadTrusteeIndividual, Trustees}
import models.requests.DataRequest
import models.{NormalMode, RegistrationProgress, UserAnswers}
import pages.{RegistrationTRNPage, TrustHaveAUTRPage}
import play.api.Logger
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import uk.gov.hmrc.auth.core.AffinityGroup.Agent
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.ConfirmationView

import scala.concurrent.{ExecutionContext, Future}

class ConfirmationController @Inject()(
                                       override val messagesApi: MessagesApi,
                                       identify: IdentifierAction,
                                       getData: DraftIdRetrievalActionProvider,
                                       requireData: DataRequiredAction,
                                       config: FrontendAppConfig,
                                       val controllerComponents: MessagesControllerComponents,
                                       view: ConfirmationView,
                                       errorHandler: ErrorHandler
                                     )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private def renderView(trn : String, userAnswers: UserAnswers)(implicit request : DataRequest[AnyContent]) : Future[Result] = {
    val trustees = userAnswers.get(Trustees).getOrElse(Nil)
    val isAgent = request.affinityGroup == Agent
    val agentOverviewUrl = routes.AgentOverviewController.onPageLoad().url

    trustees.find(_.isLead) match {
      case Some(lt : LeadTrusteeIndividual) =>

        userAnswers.get(TrustHaveAUTRPage) match {
          case Some(isExistingTrust) =>
            val postHMRC = config.posthmrc
            Future.successful(Ok(view(isExistingTrust, isAgent, trn, postHMRC, agentOverviewUrl, lt.name)))
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
        case RegistrationProgress.Complete =>
          userAnswers.get(RegistrationTRNPage) match {
            case None =>
              Logger.info("[ConfirmationController][onPageLoad] No TRN available for completed trusts. Throwing exception.")
              errorHandler.onServerError(request, new Exception("TRN is not available for completed trust."))
            case Some(trn) =>
              renderView(trn, userAnswers)
          }
        case RegistrationProgress.InProgress =>
          Logger.info("[ConfirmationController][onPageLoad] Registration inProgress status,redirecting to task list.")
          Future.successful(Redirect(routes.TaskListController.onPageLoad()))
        case RegistrationProgress.NotStarted =>
          Logger.info("[ConfirmationController][onPageLoad] Registration NotStarted status,redirecting to trust registered page online.")
          Future.successful(Redirect(routes.TrustRegisteredOnlineController.onPageLoad(NormalMode)))
      }
  }
}
