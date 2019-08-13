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
import forms.AddABeneficiaryFormProvider
import models.Status.Completed
import models.{AddABeneficiary, FullName, NormalMode}
import navigation.{FakeNavigator, Navigator}
import pages.{AddABeneficiaryPage, ClassBeneficiaryDescriptionPage, IndividualBeneficiaryNamePage}
import pages.entitystatus.{ClassBeneficiaryStatus, IndividualBeneficiaryStatus}
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import viewmodels.AddRow
import views.html.AddABeneficiaryView

class AddABeneficiaryControllerSpec extends SpecBase {

  def removeIndividualRoute(index : Int) =
    routes.RemoveIndividualBeneficiaryController.onPageLoad(index, fakeDraftId).url

  def removeClassRoute(index : Int) =
    routes.RemoveClassOfBeneficiaryController.onPageLoad(index, fakeDraftId).url

  lazy val addABeneficiaryRoute = routes.AddABeneficiaryController.onPageLoad(fakeDraftId).url

  val formProvider = new AddABeneficiaryFormProvider()

  val form = formProvider()

  lazy val beneficiariesComplete = List(
    AddRow("First Last", typeLabel = "Individual Beneficiary", "#", removeIndividualRoute(0)),
    AddRow("description", typeLabel = "Class of beneficiaries", "#", removeClassRoute(0))
  )

  val userAnswersWithBeneficiariesComplete = emptyUserAnswers
    .set(IndividualBeneficiaryNamePage(0), FullName("First", None, "Last")).success.value
    .set(IndividualBeneficiaryStatus(0), Completed).success.value
    .set(ClassBeneficiaryDescriptionPage(0), "description").success.value
    .set(ClassBeneficiaryStatus(0), Completed).success.value

  "AddABeneficiary Controller" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(userAnswersWithBeneficiariesComplete)).build()

      val request = FakeRequest(GET, addABeneficiaryRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[AddABeneficiaryView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, NormalMode, fakeDraftId, Nil, beneficiariesComplete, "You have added 2 beneficiaries")(fakeRequest, messages).toString

      application.stop()
    }

    "populate the view without value on a GET when the question has previously been answered" in {
      val userAnswers = userAnswersWithBeneficiariesComplete.
        set(AddABeneficiaryPage,AddABeneficiary.YesNow).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, addABeneficiaryRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[AddABeneficiaryView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, NormalMode, fakeDraftId, Nil, beneficiariesComplete, "You have added 2 beneficiaries")(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(userAnswersWithBeneficiariesComplete)).build()

      val request =
        FakeRequest(POST, addABeneficiaryRoute)
          .withFormUrlEncodedBody(("value", AddABeneficiary.options.head.value))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual fakeNavigator.desiredRoute.url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request =
        FakeRequest(POST, addABeneficiaryRoute)
          .withFormUrlEncodedBody(("value", "invalid value"))

      val boundForm = form.bind(Map("value" -> "invalid value"))

      val view = application.injector.instanceOf[AddABeneficiaryView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, NormalMode, fakeDraftId, Nil, Nil, "Add a beneficiary")(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, addABeneficiaryRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, addABeneficiaryRoute)
          .withFormUrlEncodedBody(("value", AddABeneficiary.values.head.toString))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }
  }
}
