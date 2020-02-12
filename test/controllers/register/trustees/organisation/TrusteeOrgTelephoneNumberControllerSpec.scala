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

package controllers.register.trustees.organisation

import base.RegistrationSpecBase
import controllers.IndexValidation
import controllers.register.routes._
import forms.trustees.TelephoneNumberFormProvider
import models.NormalMode
import org.scalacheck.Arbitrary.arbitrary
import pages.register.trustees.organisation.TrusteeOrgNamePage
import pages.register.trustees.{IsThisLeadTrusteePage, TelephoneNumberPage}
import play.api.mvc.{AnyContentAsEmpty, AnyContentAsFormUrlEncoded}
import play.api.test.FakeRequest
import play.api.test.Helpers.{route, _}
import views.html.register.trustees.organisation.TrusteeOrgTelephoneNumberView

class TrusteeOrgTelephoneNumberControllerSpec extends RegistrationSpecBase with IndexValidation {

  val leadTrusteeMessagePrefix = "leadTrusteesTelephoneNumber"
  val trusteeMessagePrefix = "telephoneNumber"
  val formProvider = new TelephoneNumberFormProvider()
  val form = formProvider(trusteeMessagePrefix)

  val index = 0
  val emptyTrusteeName = ""
  val trusteeName = "FirstName LastName"

  lazy val telephoneNumberRoute = routes.TrusteeOrgTelephoneNumberController.onPageLoad(NormalMode, index, fakeDraftId).url

  "TelephoneNumber Controller" must {

    "return OK and the correct view (lead trustee) for a GET" in {

      val userAnswers = emptyUserAnswers
        .set(IsThisLeadTrusteePage(index), true).success.value
        .set(TrusteeOrgNamePage(index), "FirstName LastName").success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, telephoneNumberRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[TrusteeOrgTelephoneNumberView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, NormalMode, fakeDraftId, index, leadTrusteeMessagePrefix, trusteeName)(fakeRequest, messages).toString

      application.stop()
    }

    "return OK and the correct view (trustee) for a GET" in {

      val userAnswers = emptyUserAnswers
        .set(IsThisLeadTrusteePage(index), false).success.value
        .set(TrusteeOrgNamePage(index), "FirstName LastName").success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, telephoneNumberRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[TrusteeOrgTelephoneNumberView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, NormalMode, fakeDraftId, index, trusteeMessagePrefix, trusteeName)(fakeRequest, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers
        .set(IsThisLeadTrusteePage(index), true).success.value
        .set(TrusteeOrgNamePage(index), "FirstName LastName").success.value
        .set(TelephoneNumberPage(index), "0191 1111111").success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, telephoneNumberRoute)

      val view = application.injector.instanceOf[TrusteeOrgTelephoneNumberView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill("0191 1111111"), NormalMode, fakeDraftId, index, leadTrusteeMessagePrefix, trusteeName)(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to TrusteeName when TrusteesName is not answered" in {
      val userAnswers = emptyUserAnswers
        .set(IsThisLeadTrusteePage(index), false).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, telephoneNumberRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.TrusteeBusinessNameController.onPageLoad(NormalMode, index, fakeDraftId).url

      application.stop()
    }

    "redirect to IsThisLeadTrustee page when IsThisLeadTrustee is not answered" in {

      val userAnswers = emptyUserAnswers
        .set(TrusteeOrgNamePage(index), "FirstName LastName").success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, telephoneNumberRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.register.trustees.routes.IsThisLeadTrusteeController.onPageLoad(NormalMode, index, fakeDraftId).url

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val userAnswers = emptyUserAnswers
        .set(IsThisLeadTrusteePage(index), false).success.value
        .set(TrusteeOrgNamePage(index), "FirstName LastName").success.value

      val application =
        applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request =
        FakeRequest(POST, telephoneNumberRoute)
          .withFormUrlEncodedBody(("value", "0191 1111111"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual fakeNavigator.desiredRoute.url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val userAnswers = emptyUserAnswers
        .set(IsThisLeadTrusteePage(index), false).success.value
        .set(TrusteeOrgNamePage(index), "FirstName LastName").success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request =
        FakeRequest(POST, telephoneNumberRoute)
          .withFormUrlEncodedBody(("value", ""))

      val boundForm = form.bind(Map("value" -> ""))

      val view = application.injector.instanceOf[TrusteeOrgTelephoneNumberView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, NormalMode, fakeDraftId, index, trusteeMessagePrefix, trusteeName)(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, telephoneNumberRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, telephoneNumberRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual SessionExpiredController.onPageLoad().url

      application.stop()
    }

  "for a GET" must {

    def getForIndex(index: Int): FakeRequest[AnyContentAsEmpty.type] = {
      val route = routes.TrusteeOrgTelephoneNumberController.onPageLoad(NormalMode, index, fakeDraftId).url

      FakeRequest(GET, route)
    }

    validateIndex(
      arbitrary[String],
      TelephoneNumberPage.apply,
      getForIndex
    )

  }

  "for a POST" must {
    def postForIndex(index: Int): FakeRequest[AnyContentAsFormUrlEncoded] = {

      val route =
        routes.TrusteeOrgTelephoneNumberController.onPageLoad(NormalMode, index, fakeDraftId).url

      FakeRequest(POST, route)
        .withFormUrlEncodedBody(("value", "0191 1111111"))
    }

    validateIndex(
      arbitrary[String],
      TelephoneNumberPage.apply,
      postForIndex
    )
    }
  }
}
