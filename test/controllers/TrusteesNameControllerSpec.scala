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
import forms.TrusteesNameFormProvider
import generators.FullNameGenerator
import models.{FullName, NormalMode, UserAnswers}
import navigation.{FakeNavigator, Navigator}
import org.scalacheck.Arbitrary.arbitrary
import pages.TrusteesNamePage
import play.api.inject.bind
import play.api.mvc.{AnyContentAsEmpty, AnyContentAsFormUrlEncoded, Call}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.TrusteesNameView

class TrusteesNameControllerSpec extends SpecBase with IndexValidation with FullNameGenerator {

  def onwardRoute = Call("GET", "/foo")

  val formProvider = new TrusteesNameFormProvider()
  val form = formProvider()

  val index = 0

  lazy val trusteesNameRoute: String = routes.TrusteesNameController.onPageLoad(NormalMode, index).url

  "TrusteesName Controller" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request = FakeRequest(GET, trusteesNameRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[TrusteesNameView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, NormalMode, index)(fakeRequest, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val name = FullName("first name", Some("middle name"), "last name")

      val userAnswers = UserAnswers(userAnswersId).set(TrusteesNamePage(index), name).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, trusteesNameRoute)

      val view = application.injector.instanceOf[TrusteesNameView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(name), NormalMode, index)(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(bind[Navigator].toInstance(new FakeNavigator(onwardRoute)))
          .build()

      val request =
        FakeRequest(POST, trusteesNameRoute)
          .withFormUrlEncodedBody(("firstName", "first"), ("middleName", "middle"), ("lastName", "last"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual onwardRoute.url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request =
        FakeRequest(POST, trusteesNameRoute)
          .withFormUrlEncodedBody(("value", ""))

      val boundForm = form.bind(Map("value" -> ""))

      val view = application.injector.instanceOf[TrusteesNameView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, NormalMode, index)(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, trusteesNameRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, trusteesNameRoute)
          .withFormUrlEncodedBody(("firstName", "first"), ("middleName", "middle"), ("lastName", "last"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "for a GET" must {

      def getForIndex(index: Int) : FakeRequest[AnyContentAsEmpty.type] = {
        val route = routes.TrusteeIndividualOrBusinessController.onPageLoad(NormalMode, index).url

        FakeRequest(GET, route)
      }

      validateIndex(
        arbitrary[FullName],
        TrusteesNamePage.apply,
        getForIndex
      )

    }

    "for a POST" must {
      def postForIndex(index: Int): FakeRequest[AnyContentAsFormUrlEncoded] = {

        val route =
          routes.TrusteeIndividualOrBusinessController.onPageLoad(NormalMode, index).url

        FakeRequest(POST, route)
          .withFormUrlEncodedBody(("firstName", "first"), ("middleName", "middle"), ("lastName", "last"))
      }

      validateIndex(
        arbitrary[FullName],
        TrusteesNamePage.apply,
        postForIndex
      )
    }
  }
}
