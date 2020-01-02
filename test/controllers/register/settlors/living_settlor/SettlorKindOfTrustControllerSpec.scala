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

package controllers.register.settlors.living_settlor

import base.SpecBase
import controllers.IndexValidation
import forms.SettlorKindOfTrustFormProvider
import models.NormalMode
import models.registration.pages.SettlorKindOfTrust
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.register.settlors.living_settlor.SettlorKindOfTrustView
import controllers.register.routes._
import pages.register.settlors.deceased_settlor.SetupAfterSettlorDiedPage
import pages.register.settlors.living_settlor.SettlorKindOfTrustPage

class SettlorKindOfTrustControllerSpec extends SpecBase with IndexValidation {

  val index = 0

  lazy val settlorKindOfTrustRoute = routes.SettlorKindOfTrustController.onPageLoad(NormalMode, fakeDraftId).url

  val formProvider = new SettlorKindOfTrustFormProvider()
  val form = formProvider()

  "SettlorKindOfTrust Controller" must {

    "return OK and the correct view for a GET" in {

      val userAnswers = emptyUserAnswers.set(SetupAfterSettlorDiedPage, false).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, settlorKindOfTrustRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[SettlorKindOfTrustView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, NormalMode, fakeDraftId)(fakeRequest, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers.set(SetupAfterSettlorDiedPage, false).success.value
        .set(SettlorKindOfTrustPage, SettlorKindOfTrust.values.head).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, settlorKindOfTrustRoute)

      val view = application.injector.instanceOf[SettlorKindOfTrustView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(SettlorKindOfTrust.values.head), NormalMode, fakeDraftId)(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val userAnswers = emptyUserAnswers.set(SetupAfterSettlorDiedPage, false).success.value

      val application =
        applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request =
        FakeRequest(POST, settlorKindOfTrustRoute)
          .withFormUrlEncodedBody(("value", SettlorKindOfTrust.options.head.value))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual fakeNavigator.desiredRoute.url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val userAnswers = emptyUserAnswers.set(SetupAfterSettlorDiedPage, false).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request =
        FakeRequest(POST, settlorKindOfTrustRoute)
          .withFormUrlEncodedBody(("value", "invalid value"))

      val boundForm = form.bind(Map("value" -> "invalid value"))

      val view = application.injector.instanceOf[SettlorKindOfTrustView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, NormalMode, fakeDraftId)(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, settlorKindOfTrustRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, settlorKindOfTrustRoute)
          .withFormUrlEncodedBody(("value", SettlorKindOfTrust.values.head.toString))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual SessionExpiredController.onPageLoad().url

      application.stop()
    }
  }
}
