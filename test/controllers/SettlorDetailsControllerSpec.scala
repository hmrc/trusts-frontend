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
import forms.SettlorDetailsFormProvider
import models.{NormalMode, SettlorDetails, UserAnswers}
import navigation.{FakeNavigator, Navigator}
import pages.{SettlorBusinessDetailsPage, SettlorDetailsPage}
import play.api.inject.bind
import org.scalacheck.Arbitrary.arbitrary
import play.api.mvc.{AnyContentAsEmpty, AnyContentAsFormUrlEncoded, Call}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.SettlorDetailsView

class SettlorDetailsControllerSpec extends SpecBase with IndexValidation {

  val index = 0

  val fakeName = "Test User"

  lazy val settlorDetailsRoute = routes.SettlorDetailsController.onPageLoad(NormalMode,index, fakeDraftId).url

  val formProvider = new SettlorDetailsFormProvider()
  val form = formProvider()

  "SettlorDetails Controller" must {

    "return OK and the correct view for a GET" in {

      val answers = emptyUserAnswers
        .set(SettlorBusinessDetailsPage(index), fakeName)
        .success
        .value

      val application = applicationBuilder(userAnswers = Some(answers)).build()

      val request = FakeRequest(GET, settlorDetailsRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[SettlorDetailsView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, NormalMode, index, fakeDraftId, fakeName)(fakeRequest, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers
        .set(SettlorDetailsPage(index), SettlorDetails.values.head)
        .success
        .value
        .set(SettlorBusinessDetailsPage(index), fakeName)
        .success
        .value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, settlorDetailsRoute)

      val view = application.injector.instanceOf[SettlorDetailsView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(SettlorDetails.values.head), NormalMode,index, fakeDraftId, fakeName)(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request =
        FakeRequest(POST, settlorDetailsRoute)
          .withFormUrlEncodedBody(("value", SettlorDetails.options.head.value))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual fakeNavigator.desiredRoute.url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {


      val answers = emptyUserAnswers
        .set(SettlorBusinessDetailsPage(index), fakeName)
        .success
        .value

      val application = applicationBuilder(userAnswers = Some(answers)).build()

      val request =
        FakeRequest(POST, settlorDetailsRoute)
          .withFormUrlEncodedBody(("value", "invalid value"))

      val boundForm = form.bind(Map("value" -> "invalid value"))

      val view = application.injector.instanceOf[SettlorDetailsView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, NormalMode,index, fakeDraftId, fakeName)(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, settlorDetailsRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, settlorDetailsRoute)
          .withFormUrlEncodedBody(("value", SettlorDetails.values.head.toString))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }


    "for a GET" must {

      def getForIndex(index: Int): FakeRequest[AnyContentAsEmpty.type] = {
        val route = routes.SettlorDetailsController.onPageLoad(NormalMode, index, fakeDraftId).url

        FakeRequest(GET, route)
      }

      validateIndex(
        arbitrary[SettlorDetails],
        SettlorDetailsPage.apply,
        getForIndex
      )

    }

    "for a POST" must {
      def postForIndex(index: Int): FakeRequest[AnyContentAsFormUrlEncoded] = {

        val route =
          routes.SettlorDetailsController.onPageLoad(NormalMode, index, fakeDraftId).url

        FakeRequest(POST, route)
          .withFormUrlEncodedBody("Value" -> "true")
      }

      validateIndex(
        arbitrary[SettlorDetails],
        SettlorDetailsPage.apply,
        postForIndex
      )
    }
  }

}
