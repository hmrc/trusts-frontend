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

package navigation

import config.FrontendAppConfig
import javax.inject.{Inject, Singleton}
import models._
import models.core.UserAnswers
import navigation.routes._
import navigation.routes.non_taxable._
import pages._
import play.api.mvc.Call
import uk.gov.hmrc.auth.core.AffinityGroup

@Singleton
class Navigator @Inject()(
                         config: FrontendAppConfig
                         ) {

  private def defaultRoute: PartialFunction[Page, AffinityGroup => UserAnswers => Call] = {
    case _ => _ => _ => controllers.register.routes.IndexController.onPageLoad()
  }

  //TODO - Example 1: Alter the routing here providing an alternate route for non taxable
  protected def route(draftId: String, isNonTaxable: Option[Boolean]): PartialFunction[Page, AffinityGroup => UserAnswers => Call] = {
    isNonTaxable match {
      case Some(true) => routeNonTaxable(draftId)
      case _          => routeTaxable(draftId)
    }
  }

  protected def routeTaxable(draftId: String): PartialFunction[Page, AffinityGroup => UserAnswers => Call] = {
    AgentRoutes.route(draftId) orElse
      AssetsRoutes.route(draftId) orElse
      DeceasedSettlorRoutes.route(draftId) orElse
      MatchingRoutes.route(draftId, config) orElse
      TrusteeRoutes.route(draftId) orElse
      TrustDetailRoutes.route(draftId) orElse
      defaultRoute
  }

  protected def routeNonTaxable(draftId: String): PartialFunction[Page, AffinityGroup => UserAnswers => Call] = {
    NonTaxableAgentRoutes.route(draftId) orElse
      NonTaxableAssetsRoutes.route(draftId) orElse
      NonTaxableDeceasedSettlorRoutes.route(draftId) orElse
      NonTaxableMatchingRoutes.route(draftId, config) orElse
      NonTaxableTrusteeRoutes.route(draftId) orElse
      NonTaxableTrustDetailRoutes.route(draftId) orElse
      defaultRoute
  }

  //TODO - Example 1: - Pass the taxable trust answer into the navigator here as an option
  // Where the routing needs to change for non taxable pass in else leave call to nextpage as is and
  // it will default to taxable
  def nextPage(
                page: Page,
                mode: Mode,
                draftId: String,
                isNonTaxable: Option[Boolean] = Some(false),
                af :AffinityGroup = AffinityGroup.Organisation
              ): UserAnswers => Call = mode match {

    case NormalMode => route(draftId, isNonTaxable)(page)(af)
    case CheckMode  => route(draftId, isNonTaxable)(page)(af)
  }

}
