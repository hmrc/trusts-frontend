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
import forms.YesNoFormProvider
import pages.register.TrustRegisteredOnlinePage
import play.api.data.Form
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.register.TrustRegisteredOnlineView

class TrustRegisteredOnlineControllerSpec extends RegistrationSpecBase {

  val formProvider = new YesNoFormProvider()
  val form: Form[Boolean] = formProvider.withPrefix("trustRegisteredOnline")

  lazy val trustRegisteredOnlineRoute: String = routes.TrustRegisteredOnlineController.onPageLoad().url

  "TrustRegisteredOnline Controller" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyMatchingAndSuitabilityUserAnswers)).build()

      val request = FakeRequest(GET, trustRegisteredOnlineRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[TrustRegisteredOnlineView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form)(request, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyMatchingAndSuitabilityUserAnswers.set(TrustRegisteredOnlinePage, true).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, trustRegisteredOnlineRoute)

      val view = application.injector.instanceOf[TrustRegisteredOnlineView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(true))(request, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyMatchingAndSuitabilityUserAnswers)).build()

      val request = FakeRequest(POST, trustRegisteredOnlineRoute)
        .withFormUrlEncodedBody(("value", "true"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual fakeNavigator.desiredRoute.url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyMatchingAndSuitabilityUserAnswers)).build()

      val request = FakeRequest(POST, trustRegisteredOnlineRoute)
        .withFormUrlEncodedBody(("value", ""))

      val boundForm = form.bind(Map("value" -> ""))

      val view = application.injector.instanceOf[TrustRegisteredOnlineView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm)(request, messages).toString

      application.stop()
    }

    "redirect SessionExpired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, trustRegisteredOnlineRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustBe routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to the SessionExpired when valid data is submitted if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(POST, trustRegisteredOnlineRoute)
        .withFormUrlEncodedBody(("value", "true"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

  }
}
