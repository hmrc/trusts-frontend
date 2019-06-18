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
import forms.TrustNameFormProvider
import generators.Generators
import models.{NormalMode, UserAnswers}
import navigation.{FakeNavigator, Navigator}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatest.mockito.MockitoSugar
import org.scalatest.prop.PropertyChecks
import pages.{TrustHaveAUTRPage, TrustNamePage}
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.TrustNameView

class TrustNameControllerSpec extends SpecBase with MockitoSugar with Generators with PropertyChecks {

  def onwardRoute = Call("GET", "/foo")

  val formProvider = new TrustNameFormProvider()
  val form = formProvider()

  lazy val trustNameRoute = routes.TrustNameController.onPageLoad(NormalMode,fakeDraftId).url

  "TrustName Controller" when {

    "an existing trust" must {

      "return OK and the correct view for a GET" in {

        forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            val answers = userAnswers.set(TrustHaveAUTRPage, true).success.value
              .remove(TrustNamePage).success.value

            val application = applicationBuilder(userAnswers = Some(answers)).build()

            val request = FakeRequest(GET, trustNameRoute)

            val result = route(application, request).value

            val view = application.injector.instanceOf[TrustNameView]

            status(result) mustEqual OK

            contentAsString(result) mustEqual
              view(form, NormalMode, fakeDraftId, true)(fakeRequest, messages).toString

            application.stop()

        }
      }

      "populate the view correctly on a GET when the question has previously been answered" in {

        forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            val answers = userAnswers.set(TrustHaveAUTRPage, true).success.value
              .set(TrustNamePage, "answer").success.value

            val application = applicationBuilder(userAnswers = Some(answers)).build()

            val request = FakeRequest(GET, trustNameRoute)

            val view = application.injector.instanceOf[TrustNameView]

            val result = route(application, request).value

            status(result) mustEqual OK

            contentAsString(result) mustEqual
              view(form.fill("answer"), NormalMode, fakeDraftId, true)(fakeRequest, messages).toString

            application.stop()
        }
      }

      "return a Bad Request and errors when invalid data is submitted" in {

        forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            val answers = userAnswers.set(TrustHaveAUTRPage, true).success.value

            val application = applicationBuilder(userAnswers = Some(answers)).build()

            val request =
              FakeRequest(POST, trustNameRoute)
                .withFormUrlEncodedBody(("value", ""))

            val boundForm = form.bind(Map("value" -> ""))

            val view = application.injector.instanceOf[TrustNameView]

            val result = route(application, request).value

            status(result) mustEqual BAD_REQUEST

            contentAsString(result) mustEqual
              view(boundForm, NormalMode, fakeDraftId, true)(fakeRequest, messages).toString

            application.stop()
        }
      }

    }

    "a new trust" must {

      "return OK and the correct view for a GET" in {

        forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            val answers = userAnswers.set(TrustHaveAUTRPage, false).success.value
              .remove(TrustNamePage).success.value

            val application = applicationBuilder(userAnswers = Some(answers)).build()

            val request = FakeRequest(GET, trustNameRoute)

            val result = route(application, request).value

            val view = application.injector.instanceOf[TrustNameView]

            status(result) mustEqual OK

            contentAsString(result) mustEqual
              view(form, NormalMode, fakeDraftId, false)(fakeRequest, messages).toString

            application.stop()
        }
      }

      "populate the view correctly on a GET when the question has previously been answered" in {

        forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            val answers = userAnswers.set(TrustHaveAUTRPage, false).success.value
              .set(TrustNamePage, "answer").success.value

            val application = applicationBuilder(userAnswers = Some(answers)).build()

            val request = FakeRequest(GET, trustNameRoute)

            val view = application.injector.instanceOf[TrustNameView]

            val result = route(application, request).value

            status(result) mustEqual OK

            contentAsString(result) mustEqual
              view(form.fill("answer"), NormalMode, fakeDraftId, false)(fakeRequest, messages).toString

            application.stop()
        }
      }

      "return a Bad Request and errors when invalid data is submitted" in {

        forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            val answers = userAnswers.set(TrustHaveAUTRPage, false).success.value

            val application = applicationBuilder(userAnswers = Some(answers)).build()

            val request =
              FakeRequest(POST, trustNameRoute)
                .withFormUrlEncodedBody(("value", ""))

            val boundForm = form.bind(Map("value" -> ""))

            val view = application.injector.instanceOf[TrustNameView]

            val result = route(application, request).value

            status(result) mustEqual BAD_REQUEST

            contentAsString(result) mustEqual
              view(boundForm, NormalMode, fakeDraftId, false)(fakeRequest, messages).toString

            application.stop()
        }
      }

    }


    "redirect to the next page when valid data is submitted" in {

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(bind[Navigator].toInstance(new FakeNavigator(onwardRoute)))
          .build()

      val request =
        FakeRequest(POST, trustNameRoute)
          .withFormUrlEncodedBody(("value", "answer"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual onwardRoute.url

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, trustNameRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, trustNameRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

  }
}
