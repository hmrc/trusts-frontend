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
import config.FrontendAppConfig
import forms.WhichIdentifierFormProvider
import models.WhichIdentifier
import org.mockito.MockitoSugar
import play.api.data.Form
import play.api.mvc.{AnyContentAsFormUrlEncoded, Call}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.register.WhichIdentifierView

class WhichIdentifierControllerSpec extends RegistrationSpecBase with MockitoSugar {

  val formProvider = new WhichIdentifierFormProvider()
  val form: Form[WhichIdentifier] = formProvider()

  lazy val onPageLoad: String = routes.WhichIdentifierController.onPageLoad().url

  lazy val onSubmit: Call = routes.WhichIdentifierController.onSubmit()

  val appConfig: FrontendAppConfig = injector.instanceOf[FrontendAppConfig]

  "WhichIdentifier Controller" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyMatchingAndSuitabilityUserAnswers)).build()

      val request = FakeRequest(GET, onPageLoad)

      val result = route(application, request).value

      val view = application.injector.instanceOf[WhichIdentifierView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form)(request, messages).toString

      application.stop()
    }

    "redirect to UTR page when user selects 'UTR'" in {

      val application = applicationBuilder(userAnswers = Some(emptyMatchingAndSuitabilityUserAnswers)).build()

      implicit val request: FakeRequest[AnyContentAsFormUrlEncoded] = FakeRequest(POST, onSubmit.url)
        .withFormUrlEncodedBody(("value", "utr"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustBe
        appConfig.maintainATrustWithUTR

      application.stop()
    }

    "redirect to URN page when user selects 'URN'" in {

      val application = applicationBuilder(userAnswers = Some(emptyMatchingAndSuitabilityUserAnswers)).build()

      implicit val request: FakeRequest[AnyContentAsFormUrlEncoded] = FakeRequest(POST, onSubmit.url)
        .withFormUrlEncodedBody(("value", "urn"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustBe
        appConfig.maintainATrustWithURN

      application.stop()
    }

    "redirect to Ref Sent By Post page when user selects 'none of the above'" in {

      val application = applicationBuilder(userAnswers = Some(emptyMatchingAndSuitabilityUserAnswers)).build()

      implicit val request: FakeRequest[AnyContentAsFormUrlEncoded] = FakeRequest(POST, onSubmit.url)
        .withFormUrlEncodedBody(("value", "none"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustBe
        controllers.register.routes.RefSentByPostController.onPageLoad().url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyMatchingAndSuitabilityUserAnswers)).build()

      val request = FakeRequest(POST, onSubmit.url)
        .withFormUrlEncodedBody(("value", ""))

      val boundForm = form.bind(Map("value" -> ""))

      val view = application.injector.instanceOf[WhichIdentifierView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm)(request, messages).toString

      application.stop()
    }
  }
}
