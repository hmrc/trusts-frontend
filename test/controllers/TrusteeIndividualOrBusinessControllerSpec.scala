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
import forms.trustees.TrusteeIndividualOrBusinessFormProvider
import models.{IndividualOrBusiness, NormalMode}
import org.scalacheck.Arbitrary.arbitrary
import pages.trustees.{IsThisLeadTrusteePage, TrusteeIndividualOrBusinessPage}
import play.api.i18n.Messages
import play.api.mvc.{AnyContentAsEmpty, AnyContentAsFormUrlEncoded}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.TrusteeIndividualOrBusinessView

class TrusteeIndividualOrBusinessControllerSpec extends SpecBase with IndexValidation {

  val index = 0

  lazy val trusteeIndividualOrBusinessRoute = routes.TrusteeIndividualOrBusinessController.onPageLoad(NormalMode, index, fakeDraftId).url

  val formProvider = new TrusteeIndividualOrBusinessFormProvider()

  "TrusteeIndividualOrBusiness Controller" must {

    "return OK and the correct view for a GET" when {

      "is lead trustee" in {

        val messageKeyPrefix = "leadTrusteeIndividualOrBusiness"

        val leadHeading = Messages(s"$messageKeyPrefix.heading")

        val userAnswers = emptyUserAnswers
          .set(IsThisLeadTrusteePage(index), true).success.value

        val application = applicationBuilder(Some(userAnswers)).build()

        val request = FakeRequest(GET, trusteeIndividualOrBusinessRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[TrusteeIndividualOrBusinessView]

        val form = formProvider(messageKeyPrefix)

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(form, NormalMode, fakeDraftId, index, leadHeading)(fakeRequest, messages).toString

        application.stop()
      }


      "is trustee" in {

        val messageKeyPrefix = "trusteeIndividualOrBusiness"

        val heading = Messages(s"$messageKeyPrefix.heading")

        val userAnswers = emptyUserAnswers
          .set(IsThisLeadTrusteePage(index), false).success.value

        val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

        val request = FakeRequest(GET, trusteeIndividualOrBusinessRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[TrusteeIndividualOrBusinessView]

        val form = formProvider(messageKeyPrefix)

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(form, NormalMode, fakeDraftId, index, heading)(fakeRequest, messages).toString

        application.stop()
      }
    }

    "populate the view correctly on a GET when the question has previously been answered" when {


      "is lead trustee" in {

        val messageKeyPrefix = "leadTrusteeIndividualOrBusiness"

        val userAnswers = emptyUserAnswers
          .set(IsThisLeadTrusteePage(index), true).success.value
          .set(TrusteeIndividualOrBusinessPage(index), IndividualOrBusiness.values.head).success.value

        val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

        val request = FakeRequest(GET, trusteeIndividualOrBusinessRoute)

        val view = application.injector.instanceOf[TrusteeIndividualOrBusinessView]

        val result = route(application, request).value

        val leadHeading = Messages(s"$messageKeyPrefix.heading")

        val form = formProvider(messageKeyPrefix)

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(form.fill(IndividualOrBusiness.values.head), NormalMode, fakeDraftId, index, leadHeading)(fakeRequest, messages).toString

        application.stop()
      }


      "is not lead trustee" in {

        val messageKeyPrefix = "trusteeIndividualOrBusiness"

        val userAnswers = emptyUserAnswers
          .set(IsThisLeadTrusteePage(index), false).success.value
          .set(TrusteeIndividualOrBusinessPage(index), IndividualOrBusiness.values.head).success.value

        val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

        val request = FakeRequest(GET, trusteeIndividualOrBusinessRoute)

        val view = application.injector.instanceOf[TrusteeIndividualOrBusinessView]

        val result = route(application, request).value

        val heading = Messages(s"$messageKeyPrefix.heading", "")

        val form = formProvider(messageKeyPrefix)

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(form.fill(IndividualOrBusiness.values.head), NormalMode, fakeDraftId, index, heading)(fakeRequest, messages).toString

        application.stop()
      }
    }

    "redirect to IsThisLeadTrustee when IsThisLeadTrustee is not answered" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request = FakeRequest(GET, trusteeIndividualOrBusinessRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.IsThisLeadTrusteeController.onPageLoad(NormalMode, index, fakeDraftId).url

      application.stop()
    }

    "redirect to the next page when valid data is submitted (lead trustee)" in {

      val userAnswers = emptyUserAnswers
        .set(IsThisLeadTrusteePage(index), true).success.value

      val application =
        applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request =
        FakeRequest(POST, trusteeIndividualOrBusinessRoute)
          .withFormUrlEncodedBody(("value", IndividualOrBusiness.options.head.value))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual fakeNavigator.desiredRoute.url

      application.stop()
    }

    "redirect to the next page when valid data is submitted (trustee)" in {

      val userAnswers = emptyUserAnswers
        .set(IsThisLeadTrusteePage(index), false).success.value

      val application =
        applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request =
        FakeRequest(POST, trusteeIndividualOrBusinessRoute)
          .withFormUrlEncodedBody(("value", IndividualOrBusiness.options.head.value))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual fakeNavigator.desiredRoute.url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val messageKeyPrefix = "trusteeIndividualOrBusiness"

      val heading = Messages(s"$messageKeyPrefix.heading")

      val userAnswers = emptyUserAnswers
        .set(IsThisLeadTrusteePage(index), false).success.value


      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request =
        FakeRequest(POST, trusteeIndividualOrBusinessRoute)
          .withFormUrlEncodedBody(("value", "invalid value"))

      val form = formProvider(messageKeyPrefix)

      val boundForm = form.bind(Map("value" -> "invalid value"))

      val view = application.injector.instanceOf[TrusteeIndividualOrBusinessView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, NormalMode, fakeDraftId, index, heading)(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, trusteeIndividualOrBusinessRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, trusteeIndividualOrBusinessRoute)
          .withFormUrlEncodedBody(("value", IndividualOrBusiness.values.head.toString))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "for a GET" must {

      def getForIndex(index: Int) : FakeRequest[AnyContentAsEmpty.type] = {
        val route = routes.TrusteeIndividualOrBusinessController.onPageLoad(NormalMode, index, fakeDraftId).url

        FakeRequest(GET, route)
      }

      validateIndex(
        arbitrary[IndividualOrBusiness],
        TrusteeIndividualOrBusinessPage.apply,
        getForIndex
      )

    }

    "for a POST" must {
      def postForIndex(index: Int): FakeRequest[AnyContentAsFormUrlEncoded] = {

        val route =
          routes.TrusteeIndividualOrBusinessController.onPageLoad(NormalMode, index, fakeDraftId).url

        FakeRequest(POST, route)
          .withFormUrlEncodedBody(("value", IndividualOrBusiness.values.head.toString))
      }

      validateIndex(
        arbitrary[IndividualOrBusiness],
        TrusteeIndividualOrBusinessPage.apply,
        postForIndex
      )
    }
  }
}
