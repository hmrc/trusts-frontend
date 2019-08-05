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
import forms.RemoveIndexFormProvider
import models.{FullName, NormalMode}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatest.prop.PropertyChecks
import pages.IndividualBeneficiaryNamePage
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.RemoveIndividualBeneficiaryView

class RemoveIndividualBeneficiaryControllerSpec extends SpecBase with PropertyChecks {

  val formProvider = new RemoveIndexFormProvider()
  val form = formProvider("removeIndividualBeneficiary")

  val index = 0

  lazy val removeIndexRoute = routes.RemoveIndividualBeneficiaryController.onPageLoad(NormalMode, index, fakeDraftId).url

  "RemoveIndividualBeneficiary Controller" must {

    "return OK and the correct view for a GET" in {

      val userAnswers = emptyUserAnswers.set(IndividualBeneficiaryNamePage(0),
        FullName("First", None, "Last")).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, removeIndexRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[RemoveIndividualBeneficiaryView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, NormalMode, index, fakeDraftId, "First Last")(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val userAnswers = emptyUserAnswers.set(IndividualBeneficiaryNamePage(0),
        FullName("First", None, "Last")).success.value

      forAll(arbitrary[Boolean]) {
        value =>
        val application =
          applicationBuilder(userAnswers = Some(userAnswers))
            .build()

        val request =
          FakeRequest(POST, removeIndexRoute)
            .withFormUrlEncodedBody(("value", value.toString))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual routes.AddABeneficiaryController.onPageLoad(fakeDraftId).url

        application.stop()
      }

    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val userAnswers = emptyUserAnswers.set(IndividualBeneficiaryNamePage(0),
        FullName("First", None, "Last")).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request =
        FakeRequest(POST, removeIndexRoute)
          .withFormUrlEncodedBody(("value", ""))

      val boundForm = form.bind(Map("value" -> ""))

      val view = application.injector.instanceOf[RemoveIndividualBeneficiaryView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, NormalMode, index, fakeDraftId, "First Last")(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, removeIndexRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, removeIndexRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }
  }
}
