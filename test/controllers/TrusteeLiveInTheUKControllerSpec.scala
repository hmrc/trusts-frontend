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
import forms.TrusteeLiveInTheUKFormProvider
import models.{FullName, NormalMode, UserAnswers}
import navigation.{FakeNavigator, Navigator}
import pages.{TrusteeLiveInTheUKPage, TrusteesNamePage}
import play.api.inject.bind
import play.api.libs.json.{JsBoolean, Json}
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.TrusteeLiveInTheUKView

class TrusteeLiveInTheUKControllerSpec extends SpecBase {

  def onwardRoute = Call("GET", "/foo")

  val formProvider = new TrusteeLiveInTheUKFormProvider()
  val form = formProvider()

  val index = 0
  val emptyTrusteeName = ""
  val trusteeName = "FirstName LastName"

  lazy val trusteeLiveInTheUKRoute = routes.TrusteeLiveInTheUKController.onPageLoad(NormalMode, index).url

  "TrusteeLiveInTheUK Controller" must {

    "return OK and the correct view for a GET" in {

      val userAnswers = UserAnswers(userAnswersId)
        .set(TrusteesNamePage(index), FullName("FirstName", None, "LastName")).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, trusteeLiveInTheUKRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[TrusteeLiveInTheUKView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, NormalMode, index, trusteeName)(fakeRequest, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = UserAnswers(userAnswersId)
        .set(TrusteesNamePage(index), FullName("FirstName", None, "LastName")).success.value
        .set(TrusteeLiveInTheUKPage(index), true).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, trusteeLiveInTheUKRoute)

      val view = application.injector.instanceOf[TrusteeLiveInTheUKView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(true), NormalMode, index, trusteeName)(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val userAnswers = UserAnswers(userAnswersId)
        .set(TrusteesNamePage(index), FullName("FirstName", None, "LastName")).success.value
        .set(TrusteeLiveInTheUKPage(index), true).success.value


      val application =
        applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(bind[Navigator].toInstance(new FakeNavigator(onwardRoute)))
          .build()

      val request =
        FakeRequest(POST, trusteeLiveInTheUKRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val userAnswers = UserAnswers(userAnswersId)
        .set(TrusteesNamePage(index), FullName("FirstName", None, "LastName")).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request =
        FakeRequest(POST, trusteeLiveInTheUKRoute)
          .withFormUrlEncodedBody(("value", ""))

      val boundForm = form.bind(Map("value" -> ""))

      val view = application.injector.instanceOf[TrusteeLiveInTheUKView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, NormalMode, index, trusteeName)(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, trusteeLiveInTheUKRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, trusteeLiveInTheUKRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }
  }
}
