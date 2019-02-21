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
import forms.TrusteeAUKCitizenFormProvider
import models.{FullName, NormalMode, UserAnswers}
import navigation.{FakeNavigator, Navigator}
import pages.{TrusteeAUKCitizenPage, TrusteesNamePage}
import play.api.i18n.Messages
import play.api.inject.bind
import play.api.libs.json.{JsBoolean, Json}
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.TrusteeAUKCitizenView


class TrusteeAUKCitizenControllerSpec extends SpecBase {

  def onwardRoute = Call("GET", "/foo")

  val formProvider = new TrusteeAUKCitizenFormProvider()
  val form = formProvider()

  val index = 0

  lazy val trusteeAUKCitizenRoute = routes.TrusteeAUKCitizenController.onPageLoad(NormalMode, index).url

  "trusteeAUKCitizen Controller" must {


    "return OK and the correct view for a GET" when {


      "trustees name has been provided" in {

        val name = FullName("First", Some(""), "Last")

        val heading = Messages("trusteeAUKCitizen.heading", name)

        val userAnswers = UserAnswers(userAnswersId)
          .set(TrusteesNamePage(index), name).success.value

        val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

        val request = FakeRequest(GET, trusteeAUKCitizenRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[TrusteeAUKCitizenView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(form, NormalMode, index, heading)(fakeRequest, messages).toString

        application.stop()
      }

      "trustees name has not been provided" in {

        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

        val request = FakeRequest(GET, trusteeAUKCitizenRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual routes.TrusteesNameController.onPageLoad(NormalMode, index).url

        application.stop()

      }
    }


    "redirect to the correct page for a POST" when {


      "trustees name has been provided" in {

        val name = FullName("first name", Some("middle name"), "last name")

        val userAnswers = UserAnswers(userAnswersId)
          .set(TrusteesNamePage(index), name).success.value

        val application =
          applicationBuilder(userAnswers = Some(userAnswers))
            .overrides(bind[Navigator].toInstance(new FakeNavigator(onwardRoute)))
            .build()

        val request =
          FakeRequest(POST, trusteeAUKCitizenRoute)
            .withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual onwardRoute.url

        application.stop()
      }

      "trustees name has not been provided" in {

        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

        val request = FakeRequest(POST, trusteeAUKCitizenRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual routes.TrusteesNameController.onPageLoad(NormalMode, index).url

        application.stop()

      }
    }


    "return a Bad Request and errors when invalid data is submitted" when {


      "on a GET if no existing data is found" in {

        val application = applicationBuilder(userAnswers = None).build()

        val request = FakeRequest(GET, trusteeAUKCitizenRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url

        application.stop()
      }

      "on a POST if no existing data is found" in {

        val application = applicationBuilder(userAnswers = None).build()

        val request =
          FakeRequest(POST, trusteeAUKCitizenRoute)
            .withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url

        application.stop()
      }
    }
  }
}
