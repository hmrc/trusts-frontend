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

package controllers

import base.RegistrationSpecBase
import config.FrontendAppConfig
import play.api.Configuration
import play.api.mvc.{AnyContent, MessagesControllerComponents, Request}
import play.api.test.FakeRequest
import play.api.test.Helpers._

class SessionTimeoutControllerSpec extends RegistrationSpecBase {

  object TestSessionTimeoutController extends SessionTimeoutController(
    app.injector.instanceOf[FrontendAppConfig],
    app.injector.instanceOf[Configuration],
    app.injector.instanceOf[MessagesControllerComponents])

  "timeout" should {

    "stay on current page with current session" when {
      "the keep alive method is used" in {
        val fakeRequest: Request[AnyContent] = FakeRequest().withSession()
        val res = TestSessionTimeoutController.keepAlive(fakeRequest)
        status(res) mustEqual OK
      }
    }

    "redirect to session expired page new session " when {
      "the timeout method is" in {
        val fakeRequest: Request[AnyContent] = FakeRequest().withSession()
        val res = TestSessionTimeoutController.timeout(fakeRequest)
        status(res) mustEqual SEE_OTHER
        redirectLocation(res).value mustEqual controllers.register.routes.SessionExpiredController.onPageLoad().url
      }
    }
    "redirect to next page for a POST" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request = FakeRequest(POST, controllers.register.routes.SessionExpiredController.onPageLoad().url)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustBe "http://localhost:9949/auth-login-stub/gg-sign-in?continue=" +
        "http%3A%2F%2Flocalhost%3A9781%2Ftrusts-registration&origin=trusts-frontend"
      application.stop()

    }
  }

}
