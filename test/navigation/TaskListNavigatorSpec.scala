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

import base.RegistrationSpecBase
import navigation.registration.TaskListNavigator

class TaskListNavigatorSpec extends RegistrationSpecBase {

  private val navigator: TaskListNavigator = new TaskListNavigator(fakeFrontendAppConfig)

  "TaskList Navigator" when {

    "trust details task" must {
      "go to trust details service" in {
        navigator.trustDetailsJourney(fakeDraftId) mustBe
          fakeFrontendAppConfig.trustDetailsFrontendUrl(fakeDraftId)
      }
    }

    "beneficiaries task" must {
      "go to beneficiaries service" in {
        navigator.beneficiariesJourneyUrl(fakeDraftId) mustBe
          fakeFrontendAppConfig.beneficiariesFrontendUrl(fakeDraftId)
      }
    }

    "settlors task" must {
      "go to settlors service" in {
        navigator.settlorsJourney(fakeDraftId) mustBe
          fakeFrontendAppConfig.settlorsFrontendUrl(fakeDraftId)
      }
    }

    "assets task" must {
      "go to assets service" in {
        navigator.assetsJourneyUrl(fakeDraftId) mustBe
          fakeFrontendAppConfig.assetsFrontendUrl(fakeDraftId)
      }
    }

    "trustees task" must {
      "go to trustees service" in {
        navigator.trusteesJourneyUrl(fakeDraftId) mustBe
          fakeFrontendAppConfig.trusteesFrontendUrl(fakeDraftId)
      }
    }

    "task liability task" must {
      "go to tax liability service" in {
        navigator.taxLiabilityJourney(fakeDraftId) mustBe
          fakeFrontendAppConfig.taxLiabilityFrontendUrl(fakeDraftId)
      }
    }

    "protectors task" must {
      "go to protectors service" in {
        navigator.protectorsJourneyUrl(fakeDraftId) mustBe
          fakeFrontendAppConfig.protectorsFrontendUrl(fakeDraftId)
      }
    }

    "other individuals task" must {
      "go to other individuals service" in {
        navigator.otherIndividualsJourneyUrl(fakeDraftId) mustBe
          fakeFrontendAppConfig.otherIndividualsFrontendUrl(fakeDraftId)
      }
    }

    "for agent details task" when {
      "go to agent details service" in {
        navigator.agentDetailsJourneyUrl(fakeDraftId) mustBe
          fakeFrontendAppConfig.agentDetailsFrontendUrl(fakeDraftId)
      }
    }
  }
}
