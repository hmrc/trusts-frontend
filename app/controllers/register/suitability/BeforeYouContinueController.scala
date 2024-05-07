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

package controllers.register.suitability

import controllers.actions.StandardActionSets
import models.requests.MatchingAndSuitabilityDataRequest
import pages.register.TrustHaveAUTRPage
import pages.register.suitability.TrustTaxableYesNoPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.register.suitability._

import javax.inject.Inject
import scala.concurrent.Future

class BeforeYouContinueController @Inject()(
                                             override val messagesApi: MessagesApi,
                                             actions: StandardActionSets,
                                             val controllerComponents: MessagesControllerComponents,
                                             taxableView: BeforeYouContinueTaxableView,
                                             taxableAgentView: BeforeYouContinueTaxableAgentView,
                                             existingTaxableView: BeforeYouContinueExistingTaxableView,
                                             nonTaxableView: BeforeYouContinueNonTaxableView,
                                             nonTaxableAgentView: BeforeYouContinueNonTaxAgentView
                                           ) extends FrontendBaseController with I18nSupport {

  private def routeNonTaxable()(implicit request: MatchingAndSuitabilityDataRequest[_]) = if (isAgentUser) {
    Ok(nonTaxableAgentView())
  } else {
    Ok(nonTaxableView())
  }

  private def routeTaxable()(implicit request: MatchingAndSuitabilityDataRequest[_]) = if (isAgentUser) {
    Ok(taxableAgentView())
  } else {
    Ok(taxableView())
  }

  def onPageLoad(): Action[AnyContent] = actions.identifiedUserMatchingAndSuitabilityData() {
    implicit request =>
      if (isExistingTrust) {
        Ok(existingTaxableView())
      } else {
        if (isTrustTaxable) {
          routeTaxable()
        } else {
          routeNonTaxable()
        }
      }
  }

  def onSubmit(): Action[AnyContent] = actions.identifiedUserMatchingAndSuitabilityData().async { _ =>
      Future.successful(Redirect(controllers.register.routes.CreateDraftRegistrationController.create().url))
  }

  private def isTrustTaxable(implicit request: MatchingAndSuitabilityDataRequest[_]): Boolean =
    request.getPage(TrustTaxableYesNoPage).contains(true)

  private def isExistingTrust(implicit request: MatchingAndSuitabilityDataRequest[_]): Boolean =
    request.getPage(TrustHaveAUTRPage).contains(true)

  private def isAgentUser(implicit request: MatchingAndSuitabilityDataRequest[_]): Boolean =
    request.isAgent
}
