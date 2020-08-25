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

package controllers.register.asset.business

import base.RegistrationSpecBase
import controllers.IndexValidation
import controllers.register.routes._
import forms.DescriptionFormProvider
import models.NormalMode
import org.scalacheck.Arbitrary.arbitrary
import pages.register.asset.business.BusinessNamePage
import play.api.mvc.{AnyContentAsEmpty, AnyContentAsFormUrlEncoded}
import play.api.test.FakeRequest
import play.api.test.Helpers.{route, _}
import views.html.register.asset.buisness.BusinessDescriptionView

class BusinessDescriptionControllerSpec extends RegistrationSpecBase  with IndexValidation {

  val formProvider = new DescriptionFormProvider()
  val form = formProvider.withConfig(56, "assetDescription")
  val index = 0
  val businessName = "Test"

  lazy val assetDescriptionRoute = routes.BusinessDescriptionController.onPageLoad(NormalMode, index, fakeDraftId).url

  "assetDescription Controller" must {

    "return OK and the correct view for a GET" in {

      val userAnswers = emptyUserAnswers
        .set(BusinessNamePage(index), "Test").success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, assetDescriptionRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[BusinessDescriptionView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, NormalMode,fakeDraftId, index, businessName)(fakeRequest, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers
        .set(BusinessNamePage(index), "Test").success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, assetDescriptionRoute)

      val view = application.injector.instanceOf[BusinessDescriptionView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, NormalMode,fakeDraftId, index, businessName)(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to AssetNamePage when AssetName is not answered" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request = FakeRequest(GET, assetDescriptionRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.BusinessNameController.onPageLoad(NormalMode, index, fakeDraftId).url

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val userAnswers = emptyUserAnswers
        .set(BusinessNamePage(index), "Test").success.value

      val application =
        applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request =
        FakeRequest(POST, assetDescriptionRoute)
          .withFormUrlEncodedBody(("value", "answer"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual fakeNavigator.desiredRoute.url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val userAnswers = emptyUserAnswers
        .set(BusinessNamePage(index), "Test").success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request =
        FakeRequest(POST, assetDescriptionRoute)
          .withFormUrlEncodedBody(("value", ""))

      val boundForm = form.bind(Map("value" -> ""))

      val view = application.injector.instanceOf[BusinessDescriptionView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, NormalMode,fakeDraftId, index, businessName)(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, assetDescriptionRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, assetDescriptionRoute)
          .withFormUrlEncodedBody(("value", "answer"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "for a GET" must {

      def getForIndex(index: Int): FakeRequest[AnyContentAsEmpty.type] = {
        val route = routes.BusinessDescriptionController.onPageLoad(NormalMode, index, fakeDraftId).url

        FakeRequest(GET, route)
      }

      validateIndex(
        arbitrary[String],
        BusinessNamePage.apply,
        getForIndex
      )

    }

    "for a POST" must {

      def postForIndex(index: Int): FakeRequest[AnyContentAsFormUrlEncoded] = {

        val route =
          routes.BusinessDescriptionController.onPageLoad(NormalMode, index, fakeDraftId).url

        FakeRequest(POST, route)
          .withFormUrlEncodedBody(("value", "true"))
      }

      validateIndex(
        arbitrary[String],
        BusinessNamePage.apply,
        postForIndex
      )
    }


  }
}
