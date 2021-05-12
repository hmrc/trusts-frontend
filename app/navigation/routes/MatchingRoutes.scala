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
import models.NormalMode
import models.core.UserAnswers
import pages.Page
import pages.register._
import play.api.mvc.Call
import uk.gov.hmrc.auth.core.AffinityGroup

object MatchingRoutes extends Routes {

  def route(draftId: String, config: FrontendAppConfig, is5mldEnabled: Boolean): PartialFunction[Page, AffinityGroup => UserAnswers => Call] = {
    case TrustRegisteredOnlinePage => _ => ua => redirectToIdentifierQuestion(ua, draftId, is5mldEnabled)
    case TrustHaveAUTRPage => _ => userAnswers => trustHaveAUTRRoute(userAnswers, draftId, config, is5mldEnabled)
    case WhatIsTheUTRPage => _ => _ => controllers.register.routes.MatchingNameController.onPageLoad(draftId)
    case MatchingNamePage => _ => _ => controllers.register.routes.TrustRegisteredWithUkAddressYesNoController.onPageLoad(NormalMode, draftId)
  }

  private def redirectToIdentifierQuestion(answers: UserAnswers, draftId: String, is5mldEnabled: Boolean): Call = {
    answers.get(TrustRegisteredOnlinePage) match {
      case Some(true) if is5mldEnabled => routes.WhichIdentifierController.onPageLoad(draftId)
      case _ => routes.TrustHaveAUTRController.onPageLoad(NormalMode, draftId)
    }
  }

  private def trustHaveAUTRRoute(answers: UserAnswers, draftId: String, config: FrontendAppConfig, is5mldEnabled: Boolean): Call = {
    val condition = (answers.get(TrustRegisteredOnlinePage), answers.get(TrustHaveAUTRPage))

    condition match {
      case (Some(false), Some(true)) => routes.WhatIsTheUTRController.onPageLoad(NormalMode, draftId)
      case (Some(false), Some(false)) => askExpressIf5mld(draftId, is5mldEnabled)
      case (Some(true), Some(false)) => routes.UTRSentByPostController.onPageLoad()
      case (Some(true), Some(true)) => routeToMaintain(config)
      case _ => routes.SessionExpiredController.onPageLoad()
    }
  }

  private def askExpressIf5mld(draftId: String, is5mldEnabled: Boolean): Call = {
    if (is5mldEnabled) {
      controllers.register.suitability.routes.ExpressTrustYesNoController.onPageLoad(NormalMode, draftId)
    } else {
      controllers.register.suitability.routes.TaxLiabilityInCurrentTaxYearYesNoController.onPageLoad(NormalMode, draftId)
    }
  }

  private def routeToMaintain(config: FrontendAppConfig) : Call = {
    Call("GET", config.maintainATrustFrontendUrl)
  }
}

