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

import base.RegistrationSpecBase
import navigation.registration.TaskListNavigator

class TaskListNavigatorSpec extends RegistrationSpecBase {

  val navigator : TaskListNavigator = new TaskListNavigator(fakeFrontendAppConfig)

  "TaskList Navigator" must {

    "for trust details task" when {

        "go to Check Trust Answers Page" in {

          navigator.trustDetailsJourney(fakeDraftId) mustBe fakeFrontendAppConfig.trustDetailsFrontendUrl(fakeDraftId)
        }
      }

    "for beneficiaries task" must {
      "go to BeneficiaryInfoPage" in {
        navigator.beneficiariesJourneyUrl(fakeDraftId) mustBe fakeFrontendAppConfig.beneficiariesFrontendUrl(fakeDraftId)
      }
    }

    "for settlors task" must {
      "go to SettlorsInfoPage" in {
        navigator.settlorsJourney(fakeDraftId) mustBe fakeFrontendAppConfig.settlorsFrontendUrl(fakeDraftId)
      }
    }

    "for assets task" must {
      "go to Asset service start" in {
        navigator.assetsJourneyUrl(fakeDraftId) mustBe fakeFrontendAppConfig.assetsFrontendUrl(fakeDraftId)
      }
    }

    "for trustee task" must {
      "go to Trustee service start" in {
        navigator.trusteesJourneyUrl(fakeDraftId) mustBe fakeFrontendAppConfig.trusteesFrontendUrl(fakeDraftId)
      }
    }

    "for task liability task" must {
      "go to TaxLiabilityPage" in {
        navigator.taxLiabilityJourney(fakeDraftId) mustBe fakeFrontendAppConfig.taxLiabilityFrontendUrl(fakeDraftId)
      }
    }

    "for protectors task" must {
      "go to Protector service start" in {
        navigator.protectorsJourneyUrl(fakeDraftId) mustBe fakeFrontendAppConfig.protectorsFrontendUrl(fakeDraftId)
      }
    }

    "for other individuals task" must {
      "go to Other Individual service start" in {
        navigator.otherIndividualsJourneyUrl(fakeDraftId) mustBe fakeFrontendAppConfig.otherIndividualsFrontendUrl(fakeDraftId)
      }
    }
  }
}
