/*
 * Copyright 2023 HM Revenue & Customs
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
import forms.WhatIsTheUTRFormProvider
import pages.register.WhatIsTheUTRPage
import play.api.data.Form
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.register.WhatIsTheUTRView

class WhatIsTheUTRControllerSpec extends RegistrationSpecBase {

  val formProvider = new WhatIsTheUTRFormProvider()
  val form: Form[String] = formProvider()

  lazy val whatIsTheUTRRoute: String = routes.WhatIsTheUTRController.onPageLoad().url

  val validAnswer: String = "1111111111"

  "WhatIsTheUTR Controller" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyMatchingAndSuitabilityUserAnswers)).build()

      val request = FakeRequest(GET, whatIsTheUTRRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[WhatIsTheUTRView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form)(request, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyMatchingAndSuitabilityUserAnswers.set(WhatIsTheUTRPage, validAnswer).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, whatIsTheUTRRoute)

      val view = application.injector.instanceOf[WhatIsTheUTRView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(validAnswer))(request, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyMatchingAndSuitabilityUserAnswers)).build()

      val request = FakeRequest(POST, whatIsTheUTRRoute)
        .withFormUrlEncodedBody(("value", validAnswer))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual fakeNavigator.desiredRoute.url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyMatchingAndSuitabilityUserAnswers)).build()

      val request = FakeRequest(POST, whatIsTheUTRRoute)
        .withFormUrlEncodedBody(("value", ""))

      val boundForm = form.bind(Map("value" -> ""))

      val view = application.injector.instanceOf[WhatIsTheUTRView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm)(request, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, whatIsTheUTRRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(POST, whatIsTheUTRRoute)
        .withFormUrlEncodedBody(("value", validAnswer))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }
  }
}
