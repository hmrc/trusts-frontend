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

import java.time.LocalDate

import base.SpecBase
import forms.AddATrusteeFormProvider
import models.{AddATrustee, FullName, IndividualOrBusiness, NormalMode, UserAnswers}
import navigation.{FakeNavigator, Navigator}
import pages._
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import viewmodels.AddRow
import views.html.AddATrusteeView

class AddATrusteeControllerSpec extends SpecBase {

  def onwardRoute = Call("GET", "/foo")

  lazy val addATrusteeRoute : String = routes.AddATrusteeController.onPageLoad().url

  val formProvider = new AddATrusteeFormProvider()
  val form = formProvider()

  val trustee = List(
    AddRow("First 0 Last 0", typeLabel = "Trustee Individual", "#", "#"),
    AddRow("First 1 Last 1", typeLabel = "Trustee Individual", "#", "#")
  )

  val userAnswersWithTrusteesComplete = UserAnswers(userAnswersId)
    .set(IsThisLeadTrusteePage(0), false).success.value
    .set(TrusteesNamePage(0), FullName("First 0", None, "Last 0")).success.value
    .set(TrusteeIndividualOrBusinessPage(0), IndividualOrBusiness.Individual).success.value
    .set(TrusteesDateOfBirthPage(0), LocalDate.of(2010, 10, 10)).success.value
    .set(IsThisLeadTrusteePage(1), true).success.value
    .set(TrusteesNamePage(1), FullName("First 1",None, "Last 1")).success.value
    .set(TrusteeIndividualOrBusinessPage(1), IndividualOrBusiness.Individual).success.value
    .set(TelephoneNumberPage(1), "+11112222").success.value
    .set(AddATrusteePage, AddATrustee.NoComplete).success.value

  "AddATrustee Controller" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(userAnswersWithTrusteesComplete)).build()

      val request = FakeRequest(GET, addATrusteeRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[AddATrusteeView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, NormalMode, Nil, trustee)(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val application =
        applicationBuilder(userAnswers = Some(userAnswersWithTrusteesComplete))
          .overrides(bind[Navigator].toInstance(new FakeNavigator(onwardRoute)))
          .build()

      val request =
        FakeRequest(POST, addATrusteeRoute)
          .withFormUrlEncodedBody(("value", AddATrustee.options.head.value))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(userAnswersWithTrusteesComplete)).build()

      val request =
        FakeRequest(POST, addATrusteeRoute)
          .withFormUrlEncodedBody(("value", "invalid value"))

      val boundForm = form.bind(Map("value" -> "invalid value"))

      val view = application.injector.instanceOf[AddATrusteeView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, NormalMode, Nil, trustee)(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, addATrusteeRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, addATrusteeRoute)
          .withFormUrlEncodedBody(("value", AddATrustee.values.head.toString))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }
  }
}
