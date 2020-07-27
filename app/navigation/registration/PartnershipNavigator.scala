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

package navigation.registration

import config.FrontendAppConfig
import controllers.register.asset.partnership.routes
import javax.inject.{Inject, Singleton}
import models.NormalMode
import models.core.UserAnswers
import navigation.Navigator
import pages.Page
import pages.register.asset.partnership.{PartnershipAnswerPage, PartnershipDescriptionPage, PartnershipStartDatePage}
import play.api.mvc.Call
import uk.gov.hmrc.auth.core.AffinityGroup

@Singleton
class PartnershipNavigator @Inject()(
                                      config: FrontendAppConfig
                                    ) extends Navigator(config) {

  override protected def route(draftId: String, isNonTaxable: Option[Boolean]): PartialFunction[Page, AffinityGroup => UserAnswers => Call] = {
    isNonTaxable match {
      case Some(true) => routeNonTaxable(draftId)
      case _          => routeTaxable(draftId)
    }
  }

  override protected def routeTaxable(draftId: String): PartialFunction[Page, AffinityGroup => UserAnswers => Call] = {
    case PartnershipDescriptionPage(index) => _ => _ => routes.PartnershipStartDateController.onPageLoad(NormalMode, index, draftId)
    case PartnershipStartDatePage(index) => _ => _ => routes.PartnershipAnswerController.onPageLoad(index, draftId)
    case PartnershipAnswerPage => _ => _ => controllers.register.asset.routes.AddAssetsController.onPageLoad(draftId)
  }

  override protected def routeNonTaxable(draftId: String): PartialFunction[Page, AffinityGroup => UserAnswers => Call] = {
    //TODO - add routing here for NTT
    ???
  }

}
