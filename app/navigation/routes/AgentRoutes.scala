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

import controllers.register.routes
import models.NormalMode
import models.core.UserAnswers
import pages.Page
import pages.register.agents._
import play.api.mvc.Call
import uk.gov.hmrc.auth.core.AffinityGroup

object AgentRoutes {
  def route(draftId: String): PartialFunction[Page, AffinityGroup => UserAnswers => Call] = {
    case AgentInternalReferencePage => _ => _ => controllers.register.agents.routes.AgentNameController.onPageLoad(NormalMode, draftId)
    case AgentNamePage => _ => _ => controllers.register.agents.routes.AgentAddressYesNoController.onPageLoad(NormalMode, draftId)
    case AgentAddressYesNoPage => _ => ua => agentAddressYesNoRoute(ua, draftId)
    case AgentUKAddressPage => _ => _ => controllers.register.agents.routes.AgentTelephoneNumberController.onPageLoad(NormalMode, draftId)
    case AgentInternationalAddressPage => _ => _ => controllers.register.agents.routes.AgentTelephoneNumberController.onPageLoad(NormalMode, draftId)
    case AgentTelephoneNumberPage => _ => _ => controllers.register.agents.routes.AgentAnswerController.onPageLoad(draftId)
    case AgentAnswerPage => _ => _ => routes.TaskListController.onPageLoad(draftId)
  }

  private def agentAddressYesNoRoute(userAnswers: UserAnswers, draftId: String) : Call =
    userAnswers.get(AgentAddressYesNoPage) match {
      case Some(false) => controllers.register.agents.routes.AgentInternationalAddressController.onPageLoad(NormalMode, draftId)
      case Some(true) => controllers.register.agents.routes.AgentUKAddressController.onPageLoad(NormalMode, draftId)
      case _ => routes.SessionExpiredController.onPageLoad()
    }
}
