/*
 * Copyright 2019 HM Revenue & Customs
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

package navigation.navigators

import base.SpecBase
import controllers.routes
import generators.Generators
import models.{NormalMode, UserAnswers}
import navigation.Navigator
import org.scalacheck.Arbitrary.arbitrary
import org.scalatest.prop.PropertyChecks
import pages._
import uk.gov.hmrc.auth.core.AffinityGroup

trait AgentRoutes {

  self: PropertyChecks with Generators with SpecBase =>

  def agentRoutes()(implicit navigator: Navigator) = {

    "go to AgentName from AgentInternalReference Page" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          navigator.nextPage(AgentInternalReferencePage, NormalMode, AffinityGroup.Agent)(userAnswers)
            .mustBe(routes.AgentNameController.onPageLoad(NormalMode))
      }
    }

    "go to AgentAddressYesNo from AgentName Page" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          navigator.nextPage(AgentNamePage, NormalMode, AffinityGroup.Agent)(userAnswers)
            .mustBe(routes.AgentAddressYesNoController.onPageLoad(NormalMode))
      }
    }

    "go to AgentUKAddress from AgentAddressYesNo Page when user answers yes" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          val answers = userAnswers.set(AgentAddressYesNoPage, value = true).success.value
          navigator.nextPage(AgentAddressYesNoPage, NormalMode, AffinityGroup.Agent)(answers)
            .mustBe(routes.AgentUKAddressController.onPageLoad(NormalMode))
      }
    }

    "go to AgentTelephoneNumber from AgentUKAddress Page" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          navigator.nextPage(AgentUKAddressPage, NormalMode, AffinityGroup.Agent)(userAnswers)
            .mustBe(routes.AgentTelephoneNumberController.onPageLoad(NormalMode))
      }
    }

    "go to AgentTelephoneNumber from AgentAddressYesNo Page when user answers no" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          val answers = userAnswers.set(AgentAddressYesNoPage, value = false).success.value
          navigator.nextPage(AgentAddressYesNoPage, NormalMode, AffinityGroup.Agent)(answers)
            .mustBe(routes.AgentTelephoneNumberController.onPageLoad(NormalMode))
      }
    }

    "go to CheckAgentAnswer Page from AgentTelephoneNumber page" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          navigator.nextPage(AgentTelephoneNumberPage, NormalMode, AffinityGroup.Agent)(userAnswers)
            .mustBe(routes.AgentAnswerController.onPageLoad())
      }
    }

    "go to RegistrationProgress from CheckAgentAnswer Page" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          navigator.nextPage(AgentAnswerPage, NormalMode, AffinityGroup.Agent)(userAnswers)
            .mustBe(routes.TaskListController.onPageLoad())
      }
    }
  }
}