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
import play.api.mvc.Results.Redirect
import forms.PostcodeForTheTrustFormProvider
import org.mockito.ArgumentMatchers.any
import pages.register.PostcodeForTheTrustPage
import play.api.data.Form
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.MatchingService
import views.html.register.PostcodeForTheTrustView

import scala.concurrent.Future

class PostcodeForTheTrustControllerSpec extends RegistrationSpecBase {

  val formProvider = new PostcodeForTheTrustFormProvider()
  val form: Form[String] = formProvider()

  lazy val postcodeForTheTrustRoute: String = routes.PostcodeForTheTrustController.onPageLoad().url

  val validAnswer: String = "AA9A 9AA"

  "PostcodeForTheTrust Controller" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyMatchingAndSuitabilityUserAnswers)).build()

      val request = FakeRequest(GET, postcodeForTheTrustRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[PostcodeForTheTrustView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form)(request, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyMatchingAndSuitabilityUserAnswers.set(PostcodeForTheTrustPage, validAnswer).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, postcodeForTheTrustRoute)

      val view = application.injector.instanceOf[PostcodeForTheTrustView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(validAnswer))(request, messages).toString

      application.stop()
    }

    "redirect when valid data is submitted" in {

      val redirectUrl = "redirect-url"

      val mockMatchingService: MatchingService = mock[MatchingService]
      when(mockMatchingService.matching(any(), any())(any(), any())).thenReturn(Future.successful(Redirect(redirectUrl)))

      val application = applicationBuilder(userAnswers = Some(emptyMatchingAndSuitabilityUserAnswers))
        .overrides(bind[MatchingService].toInstance(mockMatchingService))
        .build()

      val request = FakeRequest(POST, postcodeForTheTrustRoute)
        .withFormUrlEncodedBody(("value", validAnswer))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual redirectUrl

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyMatchingAndSuitabilityUserAnswers)).build()

      val request = FakeRequest(POST, postcodeForTheTrustRoute)
        .withFormUrlEncodedBody(("value", ""))

      val boundForm = form.bind(Map("value" -> ""))

      val view = application.injector.instanceOf[PostcodeForTheTrustView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm)(request, messages).toString

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
          .withFormUrlEncodedBody(("value", validAnswer))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }
  }
}
