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

package controllers.living_settlor

import base.SpecBase
import controllers.IndexValidation
import forms.living_settlor.SettlorBusinessDetailsFormProvider
import models.registration.pages.SettlorBusinessDetails
import models.{NormalMode, SettlorBusinessDetails}
import org.scalacheck.Arbitrary.arbitrary
import pages.living_settlor.{SettlorBusinessDetailsPage, SettlorBusinessNamePage, SettlorIndividualNINOPage}
import play.api.mvc.{AnyContentAsEmpty, AnyContentAsFormUrlEncoded}
import play.api.test.FakeRequest
import play.api.test.Helpers.{route, _}
import views.html.living_settlor.SettlorBusinessDetailsView

class SettlorBusinessDetailsControllerSpec extends SpecBase with IndexValidation {

  val index = 0

  val fakeName = "Test User"

  val UTR = SettlorBusinessDetails.UTR

  lazy val settlorBusinessDetailsRoute =  controllers.living_settlor.routes.SettlorBusinessDetailsController.onPageLoad(NormalMode,index, fakeDraftId).url

  val formProvider = new SettlorBusinessDetailsFormProvider()
  val form = formProvider()

  "SettlorBusinessDetailsController" must {

    "return OK and the correct view for a GET" in {

      val answers = emptyUserAnswers
        .set(SettlorBusinessNamePage(index), fakeName)
        .success
        .value


      val application = applicationBuilder(userAnswers = Some(answers)).build()

      val request = FakeRequest(GET, settlorBusinessDetailsRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[SettlorBusinessDetailsView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, NormalMode, index, fakeDraftId, fakeName)(fakeRequest, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers
        .set(SettlorBusinessDetailsPage(index), SettlorBusinessDetails.values.head)
        .success
        .value
        .set(SettlorBusinessNamePage(index), fakeName)
        .success
        .value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, settlorBusinessDetailsRoute)

      val view = application.injector.instanceOf[SettlorBusinessDetailsView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(SettlorBusinessDetails.values.head), NormalMode,index, fakeDraftId, fakeName)(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val ua = emptyUserAnswers
        .set(SettlorBusinessNamePage(index), fakeName)
        .success
        .value

      val application =
        applicationBuilder(userAnswers = Some(ua)).build()

      val request =
        FakeRequest(POST, settlorBusinessDetailsRoute)
          .withFormUrlEncodedBody(("value", SettlorBusinessDetails.options.head.value))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual fakeNavigator.desiredRoute.url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {


      val answers = emptyUserAnswers
        .set(SettlorBusinessNamePage(index), fakeName)
        .success
        .value

      val application = applicationBuilder(userAnswers = Some(answers)).build()

      val request =
        FakeRequest(POST, settlorBusinessDetailsRoute)
          .withFormUrlEncodedBody(("value", "invalid value"))

      val boundForm = form.bind(Map("value" -> "invalid value"))

      val view = application.injector.instanceOf[SettlorBusinessDetailsView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, NormalMode,index, fakeDraftId, fakeName)(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, settlorBusinessDetailsRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, settlorBusinessDetailsRoute)
          .withFormUrlEncodedBody(("value", SettlorBusinessDetails.values.head.toString))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to SettlorBusinessName page when SettlorsBusinessName is not answered" in {

      val userAnswers = emptyUserAnswers

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, settlorBusinessDetailsRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SettlorBusinessNameController.onPageLoad(NormalMode, index, fakeDraftId).url

      application.stop()
    }


    "for a GET" must {

      def getForIndex(index: Int): FakeRequest[AnyContentAsEmpty.type] = {
        val route = controllers.living_settlor.routes.SettlorBusinessDetailsController.onPageLoad(NormalMode, index, fakeDraftId).url

        FakeRequest(GET, route)
      }

      validateIndex(
        arbitrary[SettlorBusinessDetails],
        SettlorBusinessDetailsPage.apply,
        getForIndex
      )

    }

    "for a POST" must {
      def postForIndex(index: Int): FakeRequest[AnyContentAsFormUrlEncoded] = {

        val route =
          controllers.living_settlor.routes.SettlorBusinessDetailsController.onPageLoad(NormalMode, index, fakeDraftId).url

        FakeRequest(POST, route).withFormUrlEncodedBody(("value", SettlorBusinessDetails.values.head.toString)).withFormUrlEncodedBody("Value" -> "UTR")
      }

      validateIndex(
        arbitrary[SettlorBusinessDetails],
        SettlorBusinessDetailsPage.apply,
        postForIndex
      )
    }
  }

}
