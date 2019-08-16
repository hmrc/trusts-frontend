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
import models.{FullName, NormalMode, UserAnswers}
import navigation.{FakeNavigator, Navigator}
import org.scalacheck.Arbitrary.arbitrary
import pages.{IsThisLeadTrusteePage, TrusteesNamePage}
import play.api.i18n.Messages
import play.api.inject.bind
import play.api.mvc.{AnyContentAsEmpty, AnyContentAsFormUrlEncoded, Call}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.TrusteesNameView
import views.html.components.heading

class TrusteesNameControllerSpec extends SpecBase with IndexValidation {

  val formProvider = new TrusteesNameFormProvider()

  val index = 0

  lazy val trusteesNameRoute: String = routes.TrusteesNameController.onPageLoad(NormalMode, index, fakeDraftId).url

  "TrusteesName Controller" must {

    "return Ok and the correct view for a GET" when {

      "is lead trustee" in {

        val messageKeyPrefix = "leadTrusteesName"

        val heading = Messages(s"$messageKeyPrefix.heading")

        val userAnswers = emptyUserAnswers
          .set(IsThisLeadTrusteePage(index), true).success.value

        val application = applicationBuilder(Some(userAnswers)).build()

        val request = FakeRequest(GET, trusteesNameRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[TrusteesNameView]

        val form = formProvider(messageKeyPrefix)

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(form, NormalMode, fakeDraftId, index, heading)(fakeRequest, messages).toString

        application.stop()
      }

      "is trustee" in {

        val messageKeyPrefix = "trusteesName"

        val heading = Messages(s"$messageKeyPrefix.heading")

        val userAnswers = emptyUserAnswers
          .set(IsThisLeadTrusteePage(index), false).success.value

        val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

        val request = FakeRequest(GET, trusteesNameRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[TrusteesNameView]

        val form = formProvider(messageKeyPrefix)

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(form, NormalMode, fakeDraftId, index, heading)(fakeRequest, messages).toString

        application.stop()
      }

    }

    "return a Bad Request and errors when invalid data is submitted" when {

      "is lead trustee" in {

        val messageKeyPrefix = "leadTrusteesName"

        val heading = Messages(s"$messageKeyPrefix.heading")

        val userAnswers = emptyUserAnswers
          .set(IsThisLeadTrusteePage(index), true).success.value

        val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

        val request =
          FakeRequest(POST, trusteesNameRoute)
            .withFormUrlEncodedBody(("value", ""))

        val form = formProvider(messageKeyPrefix)

        val boundForm = form.bind(Map("value" -> ""))

        val view = application.injector.instanceOf[TrusteesNameView]

        val result = route(application, request).value


        status(result) mustEqual BAD_REQUEST

        contentAsString(result) mustEqual
          view(boundForm, NormalMode, fakeDraftId, index, heading)(fakeRequest, messages).toString

        application.stop()
      }

      "is trustee" in {

        val messageKeyPrefix = "trusteesName"

        val heading = Messages(s"$messageKeyPrefix.heading")

        val userAnswers = emptyUserAnswers
          .set(IsThisLeadTrusteePage(index), false).success.value

        val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

        val request =
          FakeRequest(POST, trusteesNameRoute)
            .withFormUrlEncodedBody(("value", ""))

        val form = formProvider(messageKeyPrefix)

        val boundForm = form.bind(Map("value" -> ""))

        val view = application.injector.instanceOf[TrusteesNameView]

        val result = route(application, request).value


        status(result) mustEqual BAD_REQUEST

        contentAsString(result) mustEqual
          view(boundForm, NormalMode, fakeDraftId, index, heading)(fakeRequest, messages).toString

        application.stop()

      }


      "redirect to IsThisLeadTrustee a GET when no answer to IsThisLeadTrustee" in {

        val userAnswers = emptyUserAnswers

        val application = applicationBuilder(Some(userAnswers)).build()

        val request = FakeRequest(GET, trusteesNameRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual routes.IsThisLeadTrusteeController.onPageLoad(NormalMode, index, fakeDraftId).url

        application.stop()
      }

      "populate the view correctly on a GET when the question has previously been answered" in {

        val messageKeyPrefix = "trusteesName"

        val name = FullName("first name", Some("middle name"), "last name")

        val userAnswers = emptyUserAnswers
          .set(TrusteesNamePage(index), name).success.value
          .set(IsThisLeadTrusteePage(index), false).success.value

        val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

        val request = FakeRequest(GET, trusteesNameRoute)

        val view = application.injector.instanceOf[TrusteesNameView]

        val result = route(application, request).value

        val heading = Messages(s"$messageKeyPrefix.heading")

        val form = formProvider(messageKeyPrefix)

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(form.fill(name), NormalMode, fakeDraftId, index, heading)(fakeRequest, messages).toString

        application.stop()
      }


      "redirect to the next page on a POST" when {

        "valid data is submitted" in {

          val name = FullName("first name", Some("middle name"), "last name")

          val userAnswers = emptyUserAnswers
            .set(TrusteesNamePage(index), name).success.value
            .set(IsThisLeadTrusteePage(index), false).success.value

          val application =
            applicationBuilder(userAnswers = Some(userAnswers)).build()

          val request =
            FakeRequest(POST, trusteesNameRoute)
              .withFormUrlEncodedBody(("firstName", "first"), ("middleName", "middle"), ("lastName", "last"))

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual fakeNavigator.desiredRoute.url

          application.stop()
        }

        "no answer to IsThisLeadTrustee is given" in {

          val userAnswers = emptyUserAnswers

          val application =
            applicationBuilder(userAnswers = Some(userAnswers)).build()

          val request =
            FakeRequest(POST, trusteesNameRoute)
              .withFormUrlEncodedBody(("firstName", "first"), ("middleName", "middle"), ("lastName", "last"))

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual routes.IsThisLeadTrusteeController.onPageLoad(NormalMode, index, fakeDraftId).url

          application.stop()
        }
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

        def getForIndex(index: Int): FakeRequest[AnyContentAsEmpty.type] = {
          val route = routes.TrusteeIndividualOrBusinessController.onPageLoad(NormalMode, index, fakeDraftId).url

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
            routes.TrusteeIndividualOrBusinessController.onPageLoad(NormalMode, index, fakeDraftId).url

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
}
