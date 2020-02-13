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
import forms.InternationalAddressFormProvider
import models.NormalMode
import models.core.pages.InternationalAddress
import org.scalacheck.Arbitrary.arbitrary
import pages.register.trustees.organisation.{TrusteeOrgAddressInternationalPage, TrusteeOrgAddressUkYesNoPage, TrusteeOrgNamePage}
import play.api.data.Form
import play.api.mvc.{AnyContentAsEmpty, AnyContentAsFormUrlEncoded, Call}
import play.api.test.FakeRequest
import play.api.test.Helpers.{route, _}
import utils.InputOption
import utils.countryOptions.CountryOptionsNonUK
import views.html.register.trustees.organisation.TrusteeOrgAddressInternationalView

class TrusteeOrgAddressInternationalControllerSpec extends RegistrationSpecBase with IndexValidation {

  def onwardRoute = Call("GET", "/foo")

  val formProvider = new InternationalAddressFormProvider()
  val form: Form[InternationalAddress] = formProvider()

  val index = 0
  val orgName = "Test"

  lazy val trusteeOrgAddressInternationalRoute = routes.TrusteeOrgAddressInternationalController.onPageLoad(NormalMode, index, fakeDraftId).url
  lazy val trusteeOrgAddressInternationalPOST = routes.TrusteeOrgAddressInternationalController.onSubmit(NormalMode, index, fakeDraftId).url

  "TrusteeOrgAddressInternational Controller" must {

    "return OK and the correct view for a GET" in {

      val userAnswers = emptyUserAnswers
        .set(TrusteeOrgNamePage(index), "Test").success.value
        .set(TrusteeOrgAddressUkYesNoPage(index), false).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, trusteeOrgAddressInternationalRoute)

      val result = route(application, request).value

      val countryOptions: Seq[InputOption] = app.injector.instanceOf[CountryOptionsNonUK].options

      val view = application.injector.instanceOf[TrusteeOrgAddressInternationalView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, countryOptions, NormalMode, index, fakeDraftId, orgName)(fakeRequest, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers
        .set(TrusteeOrgNamePage(index), "Test").success.value
        .set(TrusteeOrgAddressUkYesNoPage(index), false).success.value
        .set(TrusteeOrgAddressInternationalPage(index), InternationalAddress("line 1", "line 2", Some("line 3"), "country")).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, trusteeOrgAddressInternationalRoute)

      val view = application.injector.instanceOf[TrusteeOrgAddressInternationalView]

      val result = route(application, request).value

      val countryOptions: Seq[InputOption] = app.injector.instanceOf[CountryOptionsNonUK].options

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(InternationalAddress("line 1", "line 2", Some("line 3"), "country")), countryOptions, NormalMode, index, fakeDraftId, orgName)(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to TrusteeOrgNamePage when TrusteeOrgName is not answered" in {

      val userAnswers = emptyUserAnswers

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, trusteeOrgAddressInternationalRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.TrusteeBusinessNameController.onPageLoad(NormalMode, index, fakeDraftId).url

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val userAnswers = emptyUserAnswers
        .set(TrusteeOrgNamePage(index), "Test").success.value
        .set(TrusteeOrgAddressUkYesNoPage(index), false).success.value

      val application =
        applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request =
        FakeRequest(POST, trusteeOrgAddressInternationalPOST)
          .withFormUrlEncodedBody(("line1", "value 1"), ("line2", "value 2"), ("country", "IN"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual fakeNavigator.desiredRoute.url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val userAnswers = emptyUserAnswers
        .set(TrusteeOrgNamePage(index), "Test").success.value
        .set(TrusteeOrgAddressUkYesNoPage(index), false).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request =
        FakeRequest(POST, trusteeOrgAddressInternationalPOST)
          .withFormUrlEncodedBody(("value", "invalid value"))

      val boundForm = form.bind(Map("value" -> "invalid value"))

      val view = application.injector.instanceOf[TrusteeOrgAddressInternationalView]

      val result = route(application, request).value

      val countryOptions: Seq[InputOption] = app.injector.instanceOf[CountryOptionsNonUK].options

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, countryOptions, NormalMode, index, fakeDraftId, orgName)(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, trusteeOrgAddressInternationalRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, trusteeOrgAddressInternationalPOST)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "for a GET" must {

      def getForIndex(index: Int): FakeRequest[AnyContentAsEmpty.type] = {
        val route = routes.TrusteeOrgAddressInternationalController.onPageLoad(NormalMode, index, fakeDraftId).url

        FakeRequest(GET, route)
      }

      validateIndex(
        arbitrary[InternationalAddress],
        TrusteeOrgAddressInternationalPage.apply,
        getForIndex
      )

    }

    "for a POST" must {

      def postForIndex(index: Int): FakeRequest[AnyContentAsFormUrlEncoded] = {

        val route =
          routes.TrusteeOrgAddressInternationalController.onPageLoad(NormalMode, index, fakeDraftId).url

        FakeRequest(POST, route)
          .withFormUrlEncodedBody(("value", "true"))
      }

      validateIndex(
        arbitrary[InternationalAddress],
        TrusteeOrgAddressInternationalPage.apply,
        postForIndex
      )
    }
  }
}