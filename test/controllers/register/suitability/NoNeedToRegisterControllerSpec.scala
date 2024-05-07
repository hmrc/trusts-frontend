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

package controllers.register.suitability

import base.RegistrationSpecBase
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core.AffinityGroup._
import views.html.register.suitability.NoNeedToRegisterView

class NoNeedToRegisterControllerSpec extends RegistrationSpecBase {

  private lazy val noNeedToRegisterRoute: String = routes.NoNeedToRegisterController.onPageLoad().url

  "NoNeedToRegister Controller" must {

    "return OK and the correct view for a GET" when {

      "agent user" in {

        val application = applicationBuilder(userAnswers = Some(emptyMatchingAndSuitabilityUserAnswers), affinityGroup = Agent).build()

        val request = FakeRequest(GET, noNeedToRegisterRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[NoNeedToRegisterView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(isAgent = true)(request, messages).toString

        application.stop()
      }

      "non-agent user" in {

        val application = applicationBuilder(userAnswers = Some(emptyMatchingAndSuitabilityUserAnswers), affinityGroup = Organisation).build()

        val request = FakeRequest(GET, noNeedToRegisterRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[NoNeedToRegisterView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(isAgent = false)(request, messages).toString

        application.stop()
      }
    }
  }
}
