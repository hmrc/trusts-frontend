/*
 * Copyright 2024 HM Revenue & Customs
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

package controllers.register

import base.RegistrationSpecBase
import navigation.registration.TaskListNavigator
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core.AffinityGroup.Agent

class CreateDraftRegistrationControllerSpec extends RegistrationSpecBase {

  "CreateDraftRegistrationController" must {
    "create a draft registration and redirect" when {

      "agent user" in {

        val navigator: TaskListNavigator = mock[TaskListNavigator]
        val redirectUrl                  = "redirect-url"
        when(navigator.agentDetailsJourneyUrl(any())).thenReturn(redirectUrl)

        val application =
          applicationBuilder(userAnswers = Some(emptyMatchingAndSuitabilityUserAnswers), affinityGroup = Agent)
            .overrides(bind[TaskListNavigator].toInstance(navigator))
            .build()

        val request = FakeRequest(GET, routes.CreateDraftRegistrationController.create().url)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual redirectUrl

        application.stop()
      }

      "non-agent user" in {

        val application = applicationBuilder(userAnswers = Some(emptyMatchingAndSuitabilityUserAnswers)).build()

        val request = FakeRequest(GET, routes.CreateDraftRegistrationController.create().url)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustBe routes.TaskListController.onPageLoad(fakeDraftId).url

        application.stop()
      }
    }
  }

}
