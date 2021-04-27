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

package controllers.register.suitability

import config.FrontendAppConfig
import controllers.actions.StandardActionSets
import models.requests.RegistrationDataRequest
import navigation.registration.TaskListNavigator
import pages.register.TrustHaveAUTRPage
import pages.register.suitability.{ExpressTrustYesNoPage, TrustTaxableYesNoPage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, ActionBuilder, AnyContent, MessagesControllerComponents}
import services.FeatureFlagService
import uk.gov.hmrc.auth.core.AffinityGroup
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.register.suitability._

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class BeforeYouContinueController @Inject()(
                                             override val messagesApi: MessagesApi,
                                             standardActionSets: StandardActionSets,
                                             val controllerComponents: MessagesControllerComponents,
                                             taxableView: BeforeYouContinueTaxableView,
                                             existingTaxableView: BeforeYouContinueExistingTaxableView,
                                             nonTaxableView: BeforeYouContinueNonTaxableView,
                                             nonTaxableAgentView: BeforeYouContinueNonTaxAgentView,
                                             navigator: TaskListNavigator,
                                             featureFlagService: FeatureFlagService,
                                             appConfig: FrontendAppConfig
                                           )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private def actions(draftId: String): ActionBuilder[RegistrationDataRequest, AnyContent] =
    standardActionSets.identifiedUserWithData(draftId)

  def onPageLoad(draftId: String): Action[AnyContent] = actions(draftId) {
    implicit request =>

      val isAgent: Boolean = request.affinityGroup == AffinityGroup.Agent
      val trustHasUtr = request.userAnswers.get(TrustHaveAUTRPage)
      val trustTaxable = request.userAnswers.get(TrustTaxableYesNoPage)

      (trustHasUtr, trustTaxable, isAgent) match {
        case (Some(false), Some(true), _) => Ok(taxableView(draftId))
        case (Some(true), Some(true), _) => Ok(existingTaxableView(draftId))
        case (_, Some(false), false) =>
          if(appConfig.disableNonTaxableRegistrations) {
            Redirect(controllers.register.suitability.routes.NoNeedToRegisterController.onPageLoad(draftId))
          } else {
            Ok(nonTaxableView(draftId))
          }
        case (_, Some(false), true)  =>
          if(appConfig.disableNonTaxableRegistrations) {
            Redirect(controllers.register.suitability.routes.NoNeedToRegisterController.onPageLoad(draftId))
          } else {
            Ok(nonTaxableAgentView(draftId))
          }
        case _ =>
          Redirect(controllers.register.routes.SessionExpiredController.onPageLoad())
      }
  }

  def onSubmit(draftId: String): Action[AnyContent] = actions(draftId).async {
    implicit request =>

      featureFlagService.isNonTaxableAccessCodeEnabled() map { isNonTaxableAccessEnabled =>
        Redirect {
          (request.userAnswers.get(ExpressTrustYesNoPage), request.userAnswers.get(TrustTaxableYesNoPage), isNonTaxableAccessEnabled) match {
            case (Some(true), Some(false), true) =>
              routes.NonTaxableTrustRegistrationAccessCodeController.onPageLoad(draftId).url
            case _ =>
              request.affinityGroup match {
                case AffinityGroup.Agent =>
                  navigator.agentDetailsJourneyUrl(draftId)
                case _ =>
                  controllers.register.routes.TaskListController.onPageLoad(draftId).url
              }
          }
        }
      }
  }
}
