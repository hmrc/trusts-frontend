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
import models.requests.MatchingAndSuitabilityDataRequest
import pages.register.TrustHaveAUTRPage
import pages.register.suitability.{ExpressTrustYesNoPage, TrustTaxableYesNoPage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.FeatureFlagService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.register.suitability._

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class BeforeYouContinueController @Inject()(
                                             override val messagesApi: MessagesApi,
                                             actions: StandardActionSets,
                                             val controllerComponents: MessagesControllerComponents,
                                             taxableView: BeforeYouContinueTaxableView,
                                             existingTaxableView: BeforeYouContinueExistingTaxableView,
                                             nonTaxableView: BeforeYouContinueNonTaxableView,
                                             nonTaxableAgentView: BeforeYouContinueNonTaxAgentView,
                                             featureFlagService: FeatureFlagService,
                                             appConfig: FrontendAppConfig
                                           )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  def onPageLoad(): Action[AnyContent] = actions.identifiedUserMatchingAndSuitabilityData() {
    implicit request =>

      (doesTrustHaveUtr, isTrustTaxable) match {
        case (Some(false), Some(true)) =>
          Ok(taxableView())
        case (Some(true), Some(true)) =>
          Ok(existingTaxableView())
        case (_, Some(false)) =>
          if (appConfig.disableNonTaxableRegistrations) {
            Redirect(controllers.register.suitability.routes.NoNeedToRegisterController.onPageLoad())
          } else {
            if (isAgentUser) {
              Ok(nonTaxableAgentView())
            } else {
              Ok(nonTaxableView())
            }
          }
        case _ =>
          Redirect(controllers.register.routes.SessionExpiredController.onPageLoad())
      }
  }

  def onSubmit(): Action[AnyContent] = actions.identifiedUserMatchingAndSuitabilityData().async {
    implicit request =>

      featureFlagService.isNonTaxableAccessCodeEnabled() map { isNonTaxableAccessEnabled =>
        Redirect {
          (isExpressTrust, isTrustTaxable, isNonTaxableAccessEnabled) match {
            case (Some(true), Some(false), true) =>
              routes.NonTaxableTrustRegistrationAccessCodeController.onPageLoad().url
            case _ =>
              controllers.register.routes.CreateDraftRegistrationController.create().url
          }
        }
      }
  }

  private def isTrustTaxable(implicit request: MatchingAndSuitabilityDataRequest[_]): Option[Boolean] =
    request.getPage(TrustTaxableYesNoPage)

  private def isExpressTrust(implicit request: MatchingAndSuitabilityDataRequest[_]): Option[Boolean] =
    request.getPage(ExpressTrustYesNoPage)

  private def doesTrustHaveUtr(implicit request: MatchingAndSuitabilityDataRequest[_]): Option[Boolean] =
    request.getPage(TrustHaveAUTRPage)

  private def isAgentUser(implicit request: MatchingAndSuitabilityDataRequest[_]): Boolean =
    request.isAgent
}
