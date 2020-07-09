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
import forms.UtrFormProvider
import models.NormalMode
import org.scalacheck.Arbitrary.arbitrary
import pages.register.settlors.living_settlor.business.{SettlorBusinessNamePage, SettlorBusinessUtrPage}
import play.api.mvc.{AnyContentAsEmpty, AnyContentAsFormUrlEncoded}
import play.api.test.FakeRequest
import play.api.test.Helpers.{route, _}
import views.html.register.settlors.living_settlor.business.SettlorBusinessUtrView

class SettlorBusinessUtrControllerSpec extends RegistrationSpecBase with IndexValidation {

  val formProvider = new UtrFormProvider()
  val form = formProvider("settlorBusinessUtr")

  val index = 0
  val fakeBusinessName = "Business name"
  val fakeUtr = "1234567890"

  lazy val settlorBusinessUtrRoute = routes.SettlorBusinessUtrController.onPageLoad(NormalMode, index, fakeDraftId).url

  "SettlorBusinessUtr Controller" must {

    "return OK and the correct view for a GET" in {

      val userAnswers = emptyUserAnswers
        .set(SettlorBusinessNamePage(index), fakeBusinessName).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, settlorBusinessUtrRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[SettlorBusinessUtrView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, NormalMode, fakeDraftId, index, fakeBusinessName)(fakeRequest, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers
        .set(SettlorBusinessNamePage(index), fakeBusinessName).success.value
        .set(SettlorBusinessUtrPage(index), fakeUtr).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, settlorBusinessUtrRoute)

      val view = application.injector.instanceOf[SettlorBusinessUtrView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(fakeUtr), NormalMode, fakeDraftId, index, fakeBusinessName)(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to SettlorBusinessBusinessName page when SettlorBusinessBusinessName is not answered" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request = FakeRequest(GET, settlorBusinessUtrRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SettlorBusinessNameController.onPageLoad(NormalMode, index, fakeDraftId).url

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val userAnswers = emptyUserAnswers
        .set(SettlorBusinessNamePage(index), fakeBusinessName).success.value

      val application =
        applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request =
        FakeRequest(POST, settlorBusinessUtrRoute)
          .withFormUrlEncodedBody(("value", fakeUtr))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual fakeNavigator.desiredRoute.url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val userAnswers = emptyUserAnswers
        .set(SettlorBusinessNamePage(index), fakeBusinessName).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request =
        FakeRequest(POST, settlorBusinessUtrRoute)
          .withFormUrlEncodedBody(("value", ""))

      val boundForm = form.bind(Map("value" -> ""))

      val view = application.injector.instanceOf[SettlorBusinessUtrView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, NormalMode, fakeDraftId, index, fakeBusinessName)(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, settlorBusinessUtrRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, settlorBusinessUtrRoute)
          .withFormUrlEncodedBody(("value", fakeUtr))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "for a GET" must {

      def getForIndex(index: Int) : FakeRequest[AnyContentAsEmpty.type] = {
        val route = routes.SettlorBusinessUtrController.onPageLoad(NormalMode, index, fakeDraftId).url

        FakeRequest(GET, route)
      }

      validateIndex(
        arbitrary[String],
        SettlorBusinessUtrPage.apply,
        getForIndex
      )

    }

    "for a POST" must {
      def postForIndex(index: Int): FakeRequest[AnyContentAsFormUrlEncoded] = {

        val route =
          routes.SettlorBusinessUtrController.onPageLoad(NormalMode, index, fakeDraftId).url

        FakeRequest(POST, route)
          .withFormUrlEncodedBody(("value", fakeUtr))
      }

      validateIndex(
        arbitrary[String],
        SettlorBusinessUtrPage.apply,
        postForIndex
      )
    }

  }
}
