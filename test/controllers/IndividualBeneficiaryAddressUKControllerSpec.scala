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
import forms.{ UKAddressFormProvider}
import models.{FullName,  NormalMode, UKAddress, UserAnswers}
import navigation.{FakeNavigator, Navigator}
import pages.{IndividualBeneficiaryAddressUKPage, IndividualBeneficiaryNamePage}
import play.api.inject.bind
import play.api.libs.json.Json
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.IndividualBeneficiaryAddressUKView

class IndividualBeneficiaryAddressUKControllerSpec extends SpecBase {

  def onwardRoute = Call("GET", "/foo")

  val formProvider = new UKAddressFormProvider()
  val form = formProvider()
  val index: Int = 0
  val name = FullName("first name", None, "Last name")

  lazy val individualBeneficiaryAddressUKRoute = routes.IndividualBeneficiaryAddressUKController.onPageLoad(NormalMode, index).url

  "IndividualBeneficiaryAddressUK Controller" must {

    "return OK and the correct view for a GET" in {

      val userAnswers = UserAnswers(userAnswersId).set(IndividualBeneficiaryNamePage(index),
        name).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, individualBeneficiaryAddressUKRoute)

      val view = application.injector.instanceOf[IndividualBeneficiaryAddressUKView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, NormalMode, name, index)(request, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = UserAnswers(userAnswersId)
        .set(IndividualBeneficiaryAddressUKPage(index),  UKAddress("line 1", Some("line 2"), Some("line 3"), "line 4","line 5")).success.value.set(IndividualBeneficiaryNamePage(index),
        name).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, individualBeneficiaryAddressUKRoute)

      val view = application.injector.instanceOf[IndividualBeneficiaryAddressUKView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(UKAddress("line 1", Some("line 2"), Some("line 3"), "line 4","line 5")), NormalMode, name, index)(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val userAnswers = UserAnswers(userAnswersId).set(IndividualBeneficiaryNamePage(index),
        name).success.value

      val application =
        applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(bind[Navigator].toInstance(new FakeNavigator(onwardRoute)))
          .build()

      val request =
        FakeRequest(POST, individualBeneficiaryAddressUKRoute)
          .withFormUrlEncodedBody(("line1", "value 1"), ("townOrCity", "value 2"),("postcode", "NE1 1ZZ"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val userAnswers = UserAnswers(userAnswersId).set(IndividualBeneficiaryNamePage(index),
        name).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request =
        FakeRequest(POST, individualBeneficiaryAddressUKRoute)
          .withFormUrlEncodedBody(("value", "invalid value"))

      val boundForm = form.bind(Map("value" -> "invalid value"))

      val view = application.injector.instanceOf[IndividualBeneficiaryAddressUKView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, NormalMode, name, index)(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, individualBeneficiaryAddressUKRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, individualBeneficiaryAddressUKRoute)
          .withFormUrlEncodedBody(("line1", "value 1"), ("townOrCity", "value 2"),("postcode", "NE1 1ZZ"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }
  }
}
