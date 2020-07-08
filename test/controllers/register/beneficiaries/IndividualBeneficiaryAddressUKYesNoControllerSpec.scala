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

package controllers.register.beneficiaries

import base.RegistrationSpecBase
import controllers.register.routes._
import forms.YesNoFormProvider
import models.NormalMode
import models.core.pages.FullName
import pages.register.beneficiaries.individual.{IndividualBeneficiaryAddressUKYesNoPage, IndividualBeneficiaryNamePage}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.register.beneficiaries.IndividualBeneficiaryAddressUKYesNoView

class IndividualBeneficiaryAddressUKYesNoControllerSpec extends RegistrationSpecBase {

  val formProvider = new YesNoFormProvider()
  val form = formProvider.withPrefix("individualBeneficiaryAddressUKYesNo")
  val index: Int = 0

  val name = FullName("first name", None, "Last name")

  lazy val individualBeneficiaryAddressUKYesNoRoute = routes.IndividualBeneficiaryAddressUKYesNoController.onPageLoad(NormalMode, index, fakeDraftId).url

  "IndividualBeneficiaryAddressUKYesNo Controller" must {

    "return OK and the correct view for a GET" in {

      val userAnswers = emptyUserAnswers.set(IndividualBeneficiaryNamePage(index),
        name).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, individualBeneficiaryAddressUKYesNoRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[IndividualBeneficiaryAddressUKYesNoView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, NormalMode, fakeDraftId, name, index)(fakeRequest, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers.set(IndividualBeneficiaryAddressUKYesNoPage(index), true).success.value
        .set(IndividualBeneficiaryNamePage(index),name).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, individualBeneficiaryAddressUKYesNoRoute)

      val view = application.injector.instanceOf[IndividualBeneficiaryAddressUKYesNoView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(true), NormalMode, fakeDraftId, name, index)(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val userAnswers = emptyUserAnswers.set(IndividualBeneficiaryNamePage(index),
        name).success.value

      val application =
        applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request =
        FakeRequest(POST, individualBeneficiaryAddressUKYesNoRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual fakeNavigator.desiredRoute.url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val userAnswers = emptyUserAnswers.set(IndividualBeneficiaryNamePage(index),
        name).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request =
        FakeRequest(POST, individualBeneficiaryAddressUKYesNoRoute)
          .withFormUrlEncodedBody(("value", ""))

      val boundForm = form.bind(Map("value" -> ""))

      val view = application.injector.instanceOf[IndividualBeneficiaryAddressUKYesNoView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, NormalMode, fakeDraftId, name, index)(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, individualBeneficiaryAddressUKYesNoRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, individualBeneficiaryAddressUKYesNoRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to IndividualBeneficiaryNamePage when individual beneficiary name is not answered" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request = FakeRequest(GET, individualBeneficiaryAddressUKYesNoRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.IndividualBeneficiaryNameController.onPageLoad(NormalMode, index, fakeDraftId).url

      application.stop()
    }

  }
}