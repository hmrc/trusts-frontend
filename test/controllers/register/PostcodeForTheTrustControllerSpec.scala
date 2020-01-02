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

package controllers.register

import base.SpecBase
import forms.PostcodeForTheTrustFormProvider
import models.NormalMode
import navigation.Navigator
import pages.register.PostcodeForTheTrustPage
import play.api.data.Form
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.register.PostcodeForTheTrustView

class PostcodeForTheTrustControllerSpec extends SpecBase {

  val formProvider = new PostcodeForTheTrustFormProvider()
  val form : Form[Option[String]] = formProvider()

  lazy val postcodeForTheTrustRoute : String = routes.PostcodeForTheTrustController.onPageLoad(NormalMode,fakeDraftId).url
  lazy val matchingFailedRoute : String = routes.FailedMatchController.onPageLoad(fakeDraftId).url

  val navigator = injector.instanceOf[Navigator]

  "PostcodeForTheTrust Controller" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request = FakeRequest(GET, postcodeForTheTrustRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[PostcodeForTheTrustView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, NormalMode,fakeDraftId)(fakeRequest, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers.set(PostcodeForTheTrustPage, "AA9A 9AA").success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, postcodeForTheTrustRoute)

      val view = application.injector.instanceOf[PostcodeForTheTrustView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(Some("AA9A 9AA")), NormalMode,fakeDraftId)(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers), navigator = navigator).build()

      val request =
        FakeRequest(POST, postcodeForTheTrustRoute)
          .withFormUrlEncodedBody(("value", "AA9A 9AA"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual matchingFailedRoute

      application.stop()
    }

    "redirect to the next page when no data is submitted" in {

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers), navigator = navigator).build()

      val request =
        FakeRequest(POST, postcodeForTheTrustRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual matchingFailedRoute

      application.stop()
    }

    "redirect to the next page when an empty string is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers), navigator = navigator).build()

      val request =
        FakeRequest(POST, postcodeForTheTrustRoute)
          .withFormUrlEncodedBody(("value", ""))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual matchingFailedRoute

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request =
        FakeRequest(POST, postcodeForTheTrustRoute)
          .withFormUrlEncodedBody(("value", "AA1 1A"))

      val boundForm = form.bind(Map("value" -> "AA1 1A"))

      val view = application.injector.instanceOf[PostcodeForTheTrustView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, NormalMode,fakeDraftId)(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, postcodeForTheTrustRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, postcodeForTheTrustRoute)
          .withFormUrlEncodedBody(("value", "answer"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }
  }
}
