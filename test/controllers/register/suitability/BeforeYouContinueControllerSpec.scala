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

package controllers.register.suitability

import base.RegistrationSpecBase
import models.NormalMode
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core.AffinityGroup._
import views.html.register.suitability.BeforeYouContinueView

class BeforeYouContinueControllerSpec extends RegistrationSpecBase {

  private lazy val beforeYouContinueRoute: String = routes.BeforeYouContinueController.onPageLoad(fakeDraftId).url

  "BeforeYouContinue Controller" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request = FakeRequest(GET, beforeYouContinueRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[BeforeYouContinueView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(fakeDraftId)(request, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" when {

      "agent user" in {

        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers), affinityGroup = Agent).build()

        val request = FakeRequest(POST, beforeYouContinueRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual
          controllers.register.agents.routes.AgentInternalReferenceController.onPageLoad(NormalMode, fakeDraftId).url

        application.stop()
      }

      "non-agent user" in {

        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers), affinityGroup = Organisation).build()

        val request = FakeRequest(POST, beforeYouContinueRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual
          controllers.register.routes.TaskListController.onPageLoad(fakeDraftId).url

        application.stop()
      }
    }
  }
}
