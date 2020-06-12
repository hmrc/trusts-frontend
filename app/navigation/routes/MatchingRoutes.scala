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

package navigation.routes

import config.FrontendAppConfig
import controllers.register.routes
import models.NormalMode
import models.core.UserAnswers
import pages.Page
import pages.register.{PostcodeForTheTrustPage, TrustHaveAUTRPage, TrustRegisteredOnlinePage, WhatIsTheUTRPage}
import play.api.mvc.Call
import uk.gov.hmrc.auth.core.AffinityGroup

object MatchingRoutes {
  def route(draftId: String, config: FrontendAppConfig): PartialFunction[Page, AffinityGroup => UserAnswers => Call] = {
    case TrustRegisteredOnlinePage => _ => _ => routes.TrustHaveAUTRController.onPageLoad(NormalMode, draftId)
    case TrustHaveAUTRPage => af => userAnswers => trustHaveAUTRRoute(userAnswers, af, draftId, config)
    case WhatIsTheUTRPage => _ => _ => controllers.register.trust_details.routes.TrustNameController.onPageLoad(NormalMode, draftId)
    case PostcodeForTheTrustPage => _ => _ => routes.FailedMatchController.onPageLoad(draftId)
  }

  private def trustHaveAUTRRoute(answers: UserAnswers, af: AffinityGroup, draftId: String, config: FrontendAppConfig) = {
    val condition = (answers.get(TrustRegisteredOnlinePage), answers.get(TrustHaveAUTRPage))

    condition match {
      case (Some(false), Some(true)) => routes.WhatIsTheUTRController.onPageLoad(NormalMode, draftId)
      case (Some(false), Some(false)) => routeToRegistration(af, draftId)
      case (Some(true), Some(false)) => routes.UTRSentByPostController.onPageLoad()
      case (Some(true), Some(true)) => routeToMaintain(config)
      case _ => routes.SessionExpiredController.onPageLoad()
    }
  }

  private def routeToMaintain(config: FrontendAppConfig) : Call = {
    Call("GET", config.maintainATrustFrontendUrl)
  }

  private def routeToRegistration(affinityGroup: AffinityGroup, draftId: String) = {
    if(affinityGroup == AffinityGroup.Organisation){
      routes.TaskListController.onPageLoad(draftId)
    } else {
      controllers.register.agents.routes.AgentInternalReferenceController.onPageLoad(NormalMode, draftId)
    }
  }
}
