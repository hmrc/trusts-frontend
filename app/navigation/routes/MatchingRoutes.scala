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

package navigation.routes

import config.FrontendAppConfig
import controllers.register.routes
import models.core.TrustsFrontendUserAnswers
import pages.Page
import pages.register._
import play.api.mvc.Call
import uk.gov.hmrc.auth.core.AffinityGroup

object MatchingRoutes extends Routes {

  def route(config: FrontendAppConfig, is5mldEnabled: Boolean): PartialFunction[Page, AffinityGroup => TrustsFrontendUserAnswers[_] => Call] = {
    case TrustRegisteredOnlinePage => _ => ua => redirectToIdentifierQuestion(ua, is5mldEnabled)
    case TrustHaveAUTRPage => _ => userAnswers => trustHaveAUTRRoute(userAnswers, config, is5mldEnabled)
    case WhatIsTheUTRPage => _ => _ => controllers.register.routes.MatchingNameController.onPageLoad()
    case MatchingNamePage => _ => _ => controllers.register.routes.TrustRegisteredWithUkAddressYesNoController.onPageLoad()
  }

  private def redirectToIdentifierQuestion(answers: TrustsFrontendUserAnswers[_], is5mldEnabled: Boolean): Call = {
    answers.get(TrustRegisteredOnlinePage) match {
      case Some(true) if is5mldEnabled => routes.WhichIdentifierController.onPageLoad()
      case _ => routes.TrustHaveAUTRController.onPageLoad()
    }
  }

  private def trustHaveAUTRRoute(answers: TrustsFrontendUserAnswers[_], config: FrontendAppConfig, is5mldEnabled: Boolean): Call = {
    val condition = (answers.get(TrustRegisteredOnlinePage), answers.get(TrustHaveAUTRPage))

    condition match {
      case (Some(false), Some(true)) => routes.WhatIsTheUTRController.onPageLoad()
      case (Some(false), Some(false)) => askExpressIf5mld(is5mldEnabled)
      case (Some(true), Some(false)) => routes.UTRSentByPostController.onPageLoad()
      case (Some(true), Some(true)) => routeToMaintain(config)
      case _ => routes.SessionExpiredController.onPageLoad()
    }
  }

  private def askExpressIf5mld(is5mldEnabled: Boolean): Call = {
    if (is5mldEnabled) {
      controllers.register.suitability.routes.ExpressTrustYesNoController.onPageLoad()
    } else {
      controllers.register.suitability.routes.TaxLiabilityInCurrentTaxYearYesNoController.onPageLoad()
    }
  }

  private def routeToMaintain(config: FrontendAppConfig) : Call = {
    Call("GET", config.maintainATrustFrontendUrl)
  }

}
