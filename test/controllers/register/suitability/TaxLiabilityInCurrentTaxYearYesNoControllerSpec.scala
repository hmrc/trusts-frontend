/*
 * Copyright 2024 HM Revenue & Customs
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
import controllers.register.routes._
import forms.YesNoFormProvider
import org.mockito.Mockito.when
import java.time.LocalDate
import pages.register.suitability.TaxLiabilityInCurrentTaxYearYesNoPage
import play.api.data.Form
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.LocalDateService
import views.html.register.suitability.TaxLiabilityInCurrentTaxYearYesNoView

class TaxLiabilityInCurrentTaxYearYesNoControllerSpec extends RegistrationSpecBase {

  private val form: Form[Boolean] = new YesNoFormProvider().withPrefix("suitability.taxLiabilityInCurrentTaxYear")

  private lazy val taxLiabilityInCurrentTaxYearYesNoRoute: String =
    routes.TaxLiabilityInCurrentTaxYearYesNoController.onPageLoad().url

  private val mockLocalDateService = mock[LocalDateService]
  when(mockLocalDateService.now()).thenReturn(LocalDate.parse("2018-08-18"))

  private val currentTaxYearStartAndEnd: (String, String) = ("6 April 2018", "5 April 2019")

  "TaxLiabilityInCurrentTaxYearYesNo Controller" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyMatchingAndSuitabilityUserAnswers))
        .overrides(bind[LocalDateService].toInstance(mockLocalDateService))
        .build()

      val request = FakeRequest(GET, taxLiabilityInCurrentTaxYearYesNoRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[TaxLiabilityInCurrentTaxYearYesNoView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, currentTaxYearStartAndEnd)(request, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyMatchingAndSuitabilityUserAnswers
        .set(TaxLiabilityInCurrentTaxYearYesNoPage, true)
        .success
        .value

      val application = applicationBuilder(userAnswers = Some(userAnswers))
        .overrides(bind[LocalDateService].toInstance(mockLocalDateService))
        .build()

      val request = FakeRequest(GET, taxLiabilityInCurrentTaxYearYesNoRoute)

      val view = application.injector.instanceOf[TaxLiabilityInCurrentTaxYearYesNoView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(true), currentTaxYearStartAndEnd)(request, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyMatchingAndSuitabilityUserAnswers))
        .overrides(bind[LocalDateService].toInstance(mockLocalDateService))
        .build()

      val request = FakeRequest(POST, taxLiabilityInCurrentTaxYearYesNoRoute)
        .withFormUrlEncodedBody(("value", "true"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual fakeNavigator.desiredRoute.url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyMatchingAndSuitabilityUserAnswers))
        .overrides(bind[LocalDateService].toInstance(mockLocalDateService))
        .build()

      val request = FakeRequest(POST, taxLiabilityInCurrentTaxYearYesNoRoute)
        .withFormUrlEncodedBody(("value", ""))

      val boundForm = form.bind(Map("value" -> ""))

      val view = application.injector.instanceOf[TaxLiabilityInCurrentTaxYearYesNoView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, currentTaxYearStartAndEnd)(request, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, taxLiabilityInCurrentTaxYearYesNoRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, taxLiabilityInCurrentTaxYearYesNoRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual SessionExpiredController.onPageLoad().url

      application.stop()
    }
  }

}
