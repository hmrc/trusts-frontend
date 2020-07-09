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

package controllers.register.settlors.living_settlor.business

import base.RegistrationSpecBase
import controllers.IndexValidation
import controllers.register.routes._
import forms.living_settlor.SettlorBusinessTypeFormProvider
import models.NormalMode
import models.registration.pages.KindOfBusiness
import models.registration.pages.KindOfTrust.Employees
import pages.register.settlors.living_settlor.business.{SettlorBusinessNamePage, SettlorBusinessTypePage}
import pages.register.settlors.living_settlor.trust_type.KindOfTrustPage
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.register.settlors.living_settlor.business.SettlorBusinessTypeView

class SettlorBusinessTypeControllerSpec extends RegistrationSpecBase with IndexValidation {

  val index = 0

  lazy val settlorBusinessTypeRoute = routes.SettlorBusinessTypeController.onPageLoad(NormalMode, index, fakeDraftId).url
  lazy val settlorBusinessTimeYesNoRoute = routes.SettlorBusinessTimeYesNoController.onPageLoad(NormalMode, index, fakeDraftId).url

  val formProvider = new SettlorBusinessTypeFormProvider()
  val form = formProvider()
  val name = "Business name"
  
  "SettlorBusinessType Controller" must {

    "return OK and the correct view for a GET" in {

      val userAnswers = emptyUserAnswers.set(SettlorBusinessNamePage(index), name).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, settlorBusinessTypeRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[SettlorBusinessTypeView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, NormalMode, fakeDraftId, index, name)(fakeRequest, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers.set(SettlorBusinessNamePage(index), name).success.value
        .set(SettlorBusinessTypePage(index), KindOfBusiness.values.head).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, settlorBusinessTypeRoute)

      val view = application.injector.instanceOf[SettlorBusinessTypeView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(KindOfBusiness.values.head), NormalMode, fakeDraftId, index, name)(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val userAnswers = emptyUserAnswers.set(SettlorBusinessNamePage(index), name).success.value
        .set(KindOfTrustPage, Employees).success.value

      val application =
        applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request =
        FakeRequest(POST, settlorBusinessTypeRoute)
          .withFormUrlEncodedBody(("value", KindOfBusiness.options.head.value))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual fakeNavigator.desiredRoute.url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val userAnswers = emptyUserAnswers.set(SettlorBusinessNamePage(index), name).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request =
        FakeRequest(POST, settlorBusinessTypeRoute)
          .withFormUrlEncodedBody(("value", "invalid value"))

      val boundForm = form.bind(Map("value" -> "invalid value"))

      val view = application.injector.instanceOf[SettlorBusinessTypeView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, NormalMode, fakeDraftId, index, name)(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, settlorBusinessTypeRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, settlorBusinessTypeRoute)
          .withFormUrlEncodedBody(("value", KindOfBusiness.values.head.toString))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual SessionExpiredController.onPageLoad().url

      application.stop()
    }
  }
}
