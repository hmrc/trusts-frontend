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

package controllers

import base.SpecBase
import forms.UKAddressFormProvider
import models.{FullName, NormalMode, UKAddress, UserAnswers}
import navigation.{FakeNavigator, Navigator}
import org.scalacheck.Arbitrary.arbitrary
import pages.{TrusteesNamePage, TrusteesNinoPage, TrusteesUkAddressPage}
import play.api.inject.bind
import play.api.libs.json.Json
import play.api.mvc.{AnyContentAsEmpty, AnyContentAsFormUrlEncoded, Call}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.TrusteesUkAddressView

class UKAddressControllerSpec extends SpecBase with IndexValidation {

  val formProvider = new UKAddressFormProvider()
  val form = formProvider()
  val index = 0
  val trusteeName = "FirstName LastName"
  val validAnswer = UKAddress("value 1", Some("value 2"), Some("value 3"), "value 4", "AB1 1AB")

  lazy val trusteesUkAddressRoute = routes.TrusteesUkAddressController.onPageLoad(NormalMode, index, fakeDraftId).url

  "TrusteesUkAddress Controller" must {

    "return OK and the correct view for a GET" in {

      val userAnswers = emptyUserAnswers
        .set(TrusteesNamePage(index), FullName("FirstName", None, "LastName")).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, trusteesUkAddressRoute)

      val view = application.injector.instanceOf[TrusteesUkAddressView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, NormalMode, fakeDraftId, index, trusteeName)(request, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers
        .set(TrusteesUkAddressPage(index), validAnswer).success.value
        .set(TrusteesNamePage(index), FullName("FirstName", None, "LastName")).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, trusteesUkAddressRoute)

      val view = application.injector.instanceOf[TrusteesUkAddressView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(validAnswer), NormalMode, fakeDraftId, index, trusteeName)(fakeRequest, messages).toString

      application.stop()
    }


    "redirect to Trustee Name page when TrusteesName is not answered" in {
      val userAnswers = emptyUserAnswers
        .set(TrusteesUkAddressPage(index), validAnswer).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, trusteesUkAddressRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.TrusteesNameController.onPageLoad(NormalMode, index, fakeDraftId).url

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val userAnswers = emptyUserAnswers
        .set(TrusteesNamePage(index), FullName("FirstName", None, "LastName")).success.value

      val application =
        applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request =
        FakeRequest(POST, trusteesUkAddressRoute)
          .withFormUrlEncodedBody(("line1", "value 1"), ("line2", "value 2"), ("line3", "value 3"), ("townOrCity", "town"), ("postcode", "AB1 1AB") )

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual fakeNavigator.desiredRoute.url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val userAnswers = emptyUserAnswers
        .set(TrusteesNamePage(index), FullName("FirstName", None, "LastName")).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request =
        FakeRequest(POST, trusteesUkAddressRoute)
          .withFormUrlEncodedBody(("line1", "invalid value"))

      val boundForm = form.bind(Map("line1" -> "invalid value"))

      val view = application.injector.instanceOf[TrusteesUkAddressView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, NormalMode, fakeDraftId, index, trusteeName)(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, trusteesUkAddressRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, trusteesUkAddressRoute)
          .withFormUrlEncodedBody(("line1", "value 1"), ("line2", "value 2"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "for a GET" must {

      def getForIndex(index: Int) : FakeRequest[AnyContentAsEmpty.type] = {
        val route = routes.TrusteesUkAddressController.onPageLoad(NormalMode, index, fakeDraftId).url

        FakeRequest(GET, route)
      }

      validateIndex(
        arbitrary[UKAddress],
        TrusteesUkAddressPage.apply,
        getForIndex
      )

    }

    "for a POST" must {
      def postForIndex(index: Int): FakeRequest[AnyContentAsFormUrlEncoded] = {

        val route =
          routes.TrusteesUkAddressController.onPageLoad(NormalMode, index, fakeDraftId).url

        FakeRequest(POST, route)
          .withFormUrlEncodedBody(("line1", "line1"), ("line2", "line2"), ("line3", "line3"), ("townOrCity", "town or city"), ("postcode", "AB1 1AB"))
      }

      validateIndex(
        arbitrary[UKAddress],
        TrusteesUkAddressPage.apply,
        postForIndex
      )
    }


  }
}
