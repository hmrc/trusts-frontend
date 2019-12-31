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

package controllers.register.trustees

import base.RegistrationSpecBase
import forms.YesNoFormProvider
import forms.trustees.AddATrusteeFormProvider
import models.NormalMode
import models.core.pages.{FullName, IndividualOrBusiness}
import models.registration.pages.AddATrustee
import models.registration.pages.Status.Completed
import pages.entitystatus.TrusteeStatus
import pages.register.trustees.{TrusteeIndividualOrBusinessPage, TrusteesNamePage}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import viewmodels.AddRow
import controllers.register.routes._
import views.html.register.trustees.{AddATrusteeView, AddATrusteeYesNoView}

class AddATrusteeControllerSpec extends RegistrationSpecBase {

  lazy val getRoute : String = routes.AddATrusteeController.onPageLoad(fakeDraftId).url
  lazy val submitAnotherRoute : String = routes.AddATrusteeController.submitAnother(fakeDraftId).url
  lazy val submitYesNoRoute : String = routes.AddATrusteeController.submitOne(fakeDraftId).url

  val addTrusteeForm = new AddATrusteeFormProvider()()
  val yesNoForm = new YesNoFormProvider().withPrefix("addATrusteeYesNo")

  val trustee = List(
    AddRow("First 0 Last 0", typeLabel = "Trustee Individual", "#", "/trusts-registration/id/trustee/0/remove"),
    AddRow("First 1 Last 1", typeLabel = "Trustee Individual", "#", "/trusts-registration/id/trustee/1/remove")
  )

  val userAnswersWithTrusteesComplete = emptyUserAnswers
    .set(TrusteesNamePage(0), FullName("First 0", None, "Last 0")).success.value
    .set(TrusteeIndividualOrBusinessPage(0), IndividualOrBusiness.Individual).success.value
    .set(TrusteeStatus(0), Completed).success.value
    .set(TrusteesNamePage(1), FullName("First 1", None, "Last 1")).success.value
    .set(TrusteeIndividualOrBusinessPage(1), IndividualOrBusiness.Individual).success.value
    .set(TrusteeStatus(1), Completed).success.value

  "AddATrustee Controller" when {

    "no data" must {

      "redirect to Session Expired for a GET if no existing data is found" in {

        val application = applicationBuilder(userAnswers = None).build()

        val request = FakeRequest(GET, getRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual SessionExpiredController.onPageLoad().url

        application.stop()
      }

      "redirect to Session Expired for a POST if no existing data is found" in {

        val application = applicationBuilder(userAnswers = None).build()

        val request =
          FakeRequest(POST, submitAnotherRoute)
            .withFormUrlEncodedBody(("value", AddATrustee.values.head.toString))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual SessionExpiredController.onPageLoad().url

        application.stop()
      }
    }

    "no trustees" must {

      "return OK and the correct view for a GET" in {

        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

        val request = FakeRequest(GET, getRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[AddATrusteeYesNoView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(yesNoForm, NormalMode, fakeDraftId)(fakeRequest, messages).toString

        application.stop()
      }

      "redirect to the next page when valid data is submitted" in {

        val application =
          applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

        val request =
          FakeRequest(POST, submitYesNoRoute)
            .withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual fakeNavigator.desiredRoute.url

        application.stop()
      }

      "return a Bad Request and errors when invalid data is submitted" in {

        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

        val request =
          FakeRequest(POST, submitYesNoRoute)
            .withFormUrlEncodedBody(("value", "invalid value"))

        val boundForm = yesNoForm.bind(Map("value" -> "invalid value"))

        val view = application.injector.instanceOf[AddATrusteeYesNoView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST

        contentAsString(result) mustEqual
          view(boundForm, NormalMode, fakeDraftId)(fakeRequest, messages).toString

        application.stop()
      }
    }

    "there are trustees" must {

      "return OK and the correct view for a GET" in {

        val application = applicationBuilder(userAnswers = Some(userAnswersWithTrusteesComplete)).build()

        val request = FakeRequest(GET, getRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[AddATrusteeView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(addTrusteeForm, NormalMode, fakeDraftId,Nil, trustee, isLeadTrusteeDefined = false, heading = "You have added 2 trustees")(fakeRequest, messages).toString

        application.stop()
      }

      "redirect to the next page when valid data is submitted" in {

        val application =
          applicationBuilder(userAnswers = Some(userAnswersWithTrusteesComplete)).build()

        val request =
          FakeRequest(POST, submitAnotherRoute)
            .withFormUrlEncodedBody(("value", AddATrustee.options.head.value))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual fakeNavigator.desiredRoute.url

        application.stop()
      }

      "return a Bad Request and errors when invalid data is submitted" in {

        val application = applicationBuilder(userAnswers = Some(userAnswersWithTrusteesComplete)).build()

        val request =
          FakeRequest(POST, submitAnotherRoute)
            .withFormUrlEncodedBody(("value", "invalid value"))

        val boundForm = addTrusteeForm.bind(Map("value" -> "invalid value"))

        val view = application.injector.instanceOf[AddATrusteeView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST

        contentAsString(result) mustEqual
          view(
            boundForm,
            NormalMode,
            fakeDraftId,
            Nil,
            trustee,
            isLeadTrusteeDefined = false,
            heading = "You have added 2 trustees"
          )(fakeRequest, messages).toString

        application.stop()
      }

    }

  }
}
