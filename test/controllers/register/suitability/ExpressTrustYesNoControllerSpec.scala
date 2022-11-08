/*
 * Copyright 2022 HM Revenue & Customs
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

package controllers.register.suitability

import base.RegistrationSpecBase
import forms.YesNoFormProvider
import models.core.MatchingAndSuitabilityUserAnswers
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.scalatest.BeforeAndAfterEach
import pages.register.TrustHaveAUTRPage
import pages.register.suitability.{ExpressTrustYesNoPage, TrustTaxableYesNoPage}
import play.api.data.Form
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.register.suitability.ExpressTrustYesNoView

import scala.concurrent.Future

class ExpressTrustYesNoControllerSpec extends RegistrationSpecBase with BeforeAndAfterEach {

  val formProvider = new YesNoFormProvider()
  val form: Form[Boolean] = formProvider.withPrefix("suitability.expressTrust")
  val index: Int = 0
  val businessName = "Test"

  lazy val expressTrustYesNo: String = routes.ExpressTrustYesNoController.onPageLoad().url

  override def beforeEach(): Unit = {
    reset(cacheRepository)
    when(cacheRepository.set(any())).thenReturn(Future.successful(true))
  }

  "ExpressTrustYesNoController" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyMatchingAndSuitabilityUserAnswers)).build()

      val request = FakeRequest(GET, expressTrustYesNo)

      val result = route(application, request).value

      val view = application.injector.instanceOf[ExpressTrustYesNoView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form)(request, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyMatchingAndSuitabilityUserAnswers.set(ExpressTrustYesNoPage, true).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, expressTrustYesNo)

      val view = application.injector.instanceOf[ExpressTrustYesNoView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(true))(request, messages).toString

      application.stop()
    }

      "redirect to the next page when valid data is submitted and the trust has a UTR" in {

        val answers = emptyMatchingAndSuitabilityUserAnswers.set(TrustHaveAUTRPage, true).success.value

        val application = applicationBuilder(userAnswers = Some(answers))
          .build()

        val request = FakeRequest(POST, expressTrustYesNo)
          .withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual fakeNavigator.desiredRoute.url

        val uaCaptor = ArgumentCaptor.forClass(classOf[MatchingAndSuitabilityUserAnswers])
        verify(cacheRepository).set(uaCaptor.capture)
        uaCaptor.getValue.get(TrustTaxableYesNoPage).get mustBe true

        application.stop()
      }

      "redirect to the next page when valid data is submitted and the trust doesn't have a UTR" in {

        val answers = emptyMatchingAndSuitabilityUserAnswers.set(TrustHaveAUTRPage, false).success.value

        val application = applicationBuilder(userAnswers = Some(answers))
          .build()

        val request = FakeRequest(POST, expressTrustYesNo)
          .withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual fakeNavigator.desiredRoute.url

        val uaCaptor = ArgumentCaptor.forClass(classOf[MatchingAndSuitabilityUserAnswers])
        verify(cacheRepository).set(uaCaptor.capture)
        uaCaptor.getValue.get(TrustTaxableYesNoPage) mustNot be(defined)

        application.stop()
      }

    "return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyMatchingAndSuitabilityUserAnswers)).build()

      val request = FakeRequest(POST, expressTrustYesNo)
        .withFormUrlEncodedBody(("value", ""))

      val boundForm = form.bind(Map("value" -> ""))

      val view = application.injector.instanceOf[ExpressTrustYesNoView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm)(request, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, expressTrustYesNo)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.register.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(POST, expressTrustYesNo)
        .withFormUrlEncodedBody(("value", "true"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.register.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }
  }
}
