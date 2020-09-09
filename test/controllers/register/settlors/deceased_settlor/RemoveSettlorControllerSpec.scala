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

package controllers.register.settlors.deceased_settlor

import base.RegistrationSpecBase
import controllers.register.routes._
import forms.RemoveIndexFormProvider
import models.core.pages.FullName
import pages.register.settlors.deceased_settlor.SettlorsNamePage
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.register.settlors.deceased_settlor.RemoveDeceasedSettlorView

class RemoveSettlorControllerSpec extends RegistrationSpecBase {

  val prefix : String = "removeSettlor"

  val formProvider = new RemoveIndexFormProvider()
  val form = formProvider(prefix)
  val fakeName = "Test User"

  lazy val removeSettlorRoute = routes.RemoveSettlorController.onPageLoad(fakeDraftId)
  lazy val removeSettlorRoutePOST = routes.RemoveSettlorController.onSubmit(fakeDraftId)

  "RemoveSettlor Controller" when {
    "no name provided" must {
      "return OK and the correct view for a GET" in {

        val userAnswers = emptyUserAnswers

        val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

        val request = FakeRequest(GET, removeSettlorRoute.url)

        val result = route(application, request).value

        val view = application.injector.instanceOf[RemoveDeceasedSettlorView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual view(prefix, form, fakeDraftId, "the settlor")(request, messages).toString

        application.stop()
      }
    }


    "return OK and the correct view for a GET" in {

      val answers = emptyUserAnswers
        .set(SettlorsNamePage, FullName("Test", None, "User"))
        .success
        .value

      val application = applicationBuilder(userAnswers = Some(answers)).build()

      val request = FakeRequest(GET, removeSettlorRoute.url)

      val result = route(application, request).value

      val view = application.injector.instanceOf[RemoveDeceasedSettlorView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(prefix, form, fakeDraftId, fakeName)(request, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request =
        FakeRequest(POST, removeSettlorRoutePOST.url)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.register.settlors.routes.AddASettlorController.onPageLoad(fakeDraftId).url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val answers = emptyUserAnswers
        .set(SettlorsNamePage, FullName("Test", None, "User"))
        .success
        .value


      val application = applicationBuilder(userAnswers = Some(answers)).build()

      val request =
        FakeRequest(POST, removeSettlorRoutePOST.url)
          .withFormUrlEncodedBody(("value", ""))

      val boundForm = form.bind(Map("value" -> ""))

      val view = application.injector.instanceOf[RemoveDeceasedSettlorView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(prefix, boundForm, fakeDraftId, fakeName)(request, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, removeSettlorRoute.url)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, removeSettlorRoutePOST.url)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual SessionExpiredController.onPageLoad().url

      application.stop()
    }
  }

}
