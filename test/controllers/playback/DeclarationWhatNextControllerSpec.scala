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

package controllers.playback

import base.SpecBase
import forms.DeclarationWhatNextFormProvider
import models.playback.pages.DeclarationWhatNext
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.playback.DeclarationWhatNextView
import controllers.register.routes._
import pages.playback.DeclarationWhatNextPage

class DeclarationWhatNextControllerSpec extends SpecBase {

  lazy val declarationWhatNextRoute = routes.DeclarationWhatNextController.onPageLoad().url

  val formProvider = new DeclarationWhatNextFormProvider()
  val form = formProvider()

  "DeclarationWhatNext Controller" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request = FakeRequest(GET, declarationWhatNextRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[DeclarationWhatNextView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form)(fakeRequest, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers.set(DeclarationWhatNextPage, DeclarationWhatNext.values.head).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, declarationWhatNextRoute)

      val view = application.injector.instanceOf[DeclarationWhatNextView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(DeclarationWhatNext.values.head))(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to the declaration page when DeclareTheTrustIsUpToDate is submitted" in {

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request =
        FakeRequest(POST, declarationWhatNextRoute)
          .withFormUrlEncodedBody(("value", DeclarationWhatNext.DeclareTheTrustIsUpToDate.toString))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.playback.routes.DeclarationController.onPageLoad().url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request =
        FakeRequest(POST, declarationWhatNextRoute)
          .withFormUrlEncodedBody(("value", "invalid value"))

      val boundForm = form.bind(Map("value" -> "invalid value"))

      val view = application.injector.instanceOf[DeclarationWhatNextView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm)(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, declarationWhatNextRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, declarationWhatNextRoute)
          .withFormUrlEncodedBody(("value", DeclarationWhatNext.values.head.toString))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual SessionExpiredController.onPageLoad().url

      application.stop()
    }
  }
}
