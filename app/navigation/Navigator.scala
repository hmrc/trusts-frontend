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

package navigation

import config.FrontendAppConfig
import models._
import models.core.TrustsFrontendUserAnswers
import navigation.routes._
import pages._
import play.api.mvc.Call
import uk.gov.hmrc.auth.core.AffinityGroup

import javax.inject.{Inject, Singleton}

@Singleton
class Navigator @Inject()(config: FrontendAppConfig) {

  private def defaultRoute: PartialFunction[Page, AffinityGroup => TrustsFrontendUserAnswers[_] => Call] = {
    case _ => _ => _ => controllers.register.routes.IndexController.onPageLoad()
  }

  protected def route(draftId: String, is5mldEnabled: Boolean): PartialFunction[Page, AffinityGroup => TrustsFrontendUserAnswers[_] => Call] =
    MatchingRoutes.route(config, is5mldEnabled) orElse
      SuitabilityRoutes.route(draftId, is5mldEnabled) orElse
      defaultRoute

  def nextPage(page: Page,
               mode: Mode = NormalMode,
               draftId: String = "",
               af: AffinityGroup = AffinityGroup.Organisation,
               is5mldEnabled: Boolean = false): TrustsFrontendUserAnswers[_] => Call =
    route(draftId, is5mldEnabled)(page)(af)

}
