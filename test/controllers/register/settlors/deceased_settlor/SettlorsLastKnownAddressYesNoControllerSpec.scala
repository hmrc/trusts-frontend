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

package controllers.register.settlors.deceased_settlor

import base.RegistrationSpecBase
import forms.YesNoFormProvider
import models.NormalMode
import models.core.pages.FullName
import pages.register.settlors.deceased_settlor.{SettlorsLastKnownAddressYesNoPage, SettlorsNamePage}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.register.settlors.deceased_settlor.SettlorsLastKnownAddressYesNoView
import controllers.register.routes._

class SettlorsLastKnownAddressYesNoControllerSpec extends RegistrationSpecBase {

  val form = new YesNoFormProvider().withPrefix("settlorsLastKnownAddressYesNo")

  lazy val settlorsLastKnownAddressYesNoRoute = routes.SettlorsLastKnownAddressYesNoController.onPageLoad(NormalMode,fakeDraftId).url

  val name = FullName("first name", None, "Last name")

  "SettlorsLastKnownAddressYesNo Controller" must {

    "return OK and the correct view for a GET" in {

      val userAnswers = emptyUserAnswers.set(SettlorsNamePage,
        name).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, settlorsLastKnownAddressYesNoRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[SettlorsLastKnownAddressYesNoView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, NormalMode, fakeDraftId, name)(fakeRequest, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers.set(SettlorsLastKnownAddressYesNoPage, true).success.value.set(SettlorsNamePage,
        name).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, settlorsLastKnownAddressYesNoRoute)

      val view = application.injector.instanceOf[SettlorsLastKnownAddressYesNoView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(true), NormalMode, fakeDraftId, name)(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val userAnswers = emptyUserAnswers.set(SettlorsNamePage,
        name).success.value

      val application =
        applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request =
        FakeRequest(POST, settlorsLastKnownAddressYesNoRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual fakeNavigator.desiredRoute.url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val userAnswers = emptyUserAnswers.set(SettlorsNamePage,
        name).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request =
        FakeRequest(POST, settlorsLastKnownAddressYesNoRoute)
          .withFormUrlEncodedBody(("value", ""))

      val boundForm = form.bind(Map("value" -> ""))

      val view = application.injector.instanceOf[SettlorsLastKnownAddressYesNoView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, NormalMode, fakeDraftId, name)(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, settlorsLastKnownAddressYesNoRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, settlorsLastKnownAddressYesNoRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to SettlorNamePage when settlor name is not answered" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request = FakeRequest(GET, settlorsLastKnownAddressYesNoRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SettlorsNameController.onPageLoad(NormalMode,fakeDraftId).url

      application.stop()
    }
  }
}
