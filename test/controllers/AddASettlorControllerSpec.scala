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
import forms.YesNoFormProvider
import forms.AddASettlorFormProvider
import models.SettlorKindOfTrust.Lifetime
import models.{AddASettlor, NormalMode}
import pages.SettlorKindOfTrustPage
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.{AddASettlorView, AddASettlorYesNoView}

class AddASettlorControllerSpec extends SpecBase {

  lazy val getRoute : String = routes.AddASettlorController.onPageLoad(fakeDraftId).url
  lazy val submitAnotherRoute : String = routes.AddASettlorController.submitAnother(fakeDraftId).url
  lazy val submitYesNoRoute : String = routes.AddASettlorController.submitOne(fakeDraftId).url

  val yesNoForm = new YesNoFormProvider().withPrefix("addASettlorYesNo")
  val addSettlorForm = new AddASettlorFormProvider()()

  val settlors = List()

  val hint = "addASettlor.lifetime"

  val userAnswersWithSettlorsComplete = emptyUserAnswers
    .set(SettlorKindOfTrustPage, Lifetime)
    .success
    .value


  "AddASettlor Controller" when {

    "no data" must {

      "redirect to Session Expired for a GET if no existing data is found" in {

        val application = applicationBuilder(userAnswers = None).build()

        val request = FakeRequest(GET, getRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url

        application.stop()
      }

      "redirect to Session Expired for a POST if no existing data is found" in {

        val application = applicationBuilder(userAnswers = None).build()

        val request =
          FakeRequest(POST, submitAnotherRoute)
            .withFormUrlEncodedBody(("value", AddASettlor.values.head.toString))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url

        application.stop()
      }
    }

    "no settlors" must {

      "return OK and the correct view for a GET" in {

        val answers = emptyUserAnswers
          .set(SettlorKindOfTrustPage, Lifetime)
          .success
          .value

        val application = applicationBuilder(userAnswers = Some(answers)).build()

        val request = FakeRequest(GET, getRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[AddASettlorYesNoView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(yesNoForm, NormalMode, fakeDraftId, Some(hint))(fakeRequest, messages).toString

        application.stop()
      }

      "redirect to the next page when valid data is submitted" in {

        val answers = emptyUserAnswers
          .set(SettlorKindOfTrustPage, Lifetime)
          .success
          .value

        val application =
          applicationBuilder(userAnswers = Some(answers)).build()

        val request =
          FakeRequest(POST, submitYesNoRoute)
            .withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual fakeNavigator.desiredRoute.url

        application.stop()
      }

      "return a Bad Request and errors when invalid data is submitted" in {

        val answers = emptyUserAnswers
          .set(SettlorKindOfTrustPage, Lifetime)
          .success
          .value

        val application = applicationBuilder(userAnswers = Some(answers)).build()

        val request =
          FakeRequest(POST, submitYesNoRoute)
            .withFormUrlEncodedBody(("value", "invalid value"))

        val boundForm = yesNoForm.bind(Map("value" -> "invalid value"))

        val view = application.injector.instanceOf[AddASettlorYesNoView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST

        contentAsString(result) mustEqual
          view(boundForm, NormalMode, fakeDraftId, Some(hint))(fakeRequest, messages).toString

        application.stop()
      }
    }

    "there are settlors" ignore {

      "return OK and the correct view for a GET" in {

        val application = applicationBuilder(userAnswers = Some(userAnswersWithSettlorsComplete)).build()

        val request = FakeRequest(GET, getRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[AddASettlorView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(addSettlorForm, NormalMode, fakeDraftId,Nil, settlors, heading = "Do you want to add a settlor?", Some(hint))(fakeRequest, messages).toString

        application.stop()
      }

      "redirect to the next page when valid data is submitted" in {

        val application =
          applicationBuilder(userAnswers = Some(userAnswersWithSettlorsComplete)).build()

        val request =
          FakeRequest(POST, submitAnotherRoute)
            .withFormUrlEncodedBody(("value", AddASettlor.options.head.value))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual fakeNavigator.desiredRoute.url

        application.stop()
      }

      "return a Bad Request and errors when invalid data is submitted" in {

        val application = applicationBuilder(userAnswers = Some(userAnswersWithSettlorsComplete)).build()

        val request =
          FakeRequest(POST, submitAnotherRoute)
            .withFormUrlEncodedBody(("value", "invalid value"))

        val boundForm = addSettlorForm.bind(Map("value" -> "invalid value"))

        val view = application.injector.instanceOf[AddASettlorView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST

        contentAsString(result) mustEqual
          view(
            boundForm,
            NormalMode,
            fakeDraftId,
            Nil,
            settlors,
            heading = "Add a settlor",
            Some(hint)
          )(fakeRequest, messages).toString

        application.stop()
      }

    }

  }
}
