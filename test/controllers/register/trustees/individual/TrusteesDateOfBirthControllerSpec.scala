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

package controllers.register.trustees.individual

import java.time.{LocalDate, ZoneOffset}

import base.RegistrationSpecBase
import controllers.IndexValidation
import controllers.register.routes._
import forms.trustees.TrusteesDateOfBirthFormProvider
import models.NormalMode
import models.core.pages.FullName
import org.scalacheck.Gen
import org.scalatestplus.mockito.MockitoSugar
import pages.register.trustees.IsThisLeadTrusteePage
import pages.register.trustees.individual.{TrusteesDateOfBirthPage, TrusteesNamePage}
import play.api.mvc.{AnyContentAsEmpty, AnyContentAsFormUrlEncoded}
import play.api.test.FakeRequest
import play.api.test.Helpers.{route, _}
import views.html.register.trustees.individual.TrusteesDateOfBirthView

class TrusteesDateOfBirthControllerSpec extends RegistrationSpecBase with MockitoSugar with IndexValidation {

  val leadTrusteeMessagePrefix = "leadTrusteesDateOfBirth"
  val trusteeMessagePrefix = "trusteesDateOfBirth"
  val formProvider = new TrusteesDateOfBirthFormProvider(frontendAppConfig)
  val form = formProvider()

  val validAnswer = LocalDate.now(ZoneOffset.UTC)

  val index = 0
  val emptyTrusteeName = ""
  val trusteeName = "FirstName LastName"

  lazy val trusteesDateOfBirthRoute = routes.TrusteesDateOfBirthController.onPageLoad(NormalMode, index, fakeDraftId).url

  "TrusteesDateOfBirth Controller" must {

    "return OK and the correct view for a GET" in {

      val userAnswers = emptyUserAnswers
        .set(IsThisLeadTrusteePage(index), false).success.value
        .set(TrusteesNamePage(index), FullName("FirstName", None, "LastName")).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, trusteesDateOfBirthRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[TrusteesDateOfBirthView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, NormalMode, fakeDraftId, index, trusteeMessagePrefix, trusteeName)(fakeRequest, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers
        .set(IsThisLeadTrusteePage(index), false).success.value
        .set(TrusteesNamePage(index), FullName("FirstName", None, "LastName")).success.value
        .set(TrusteesDateOfBirthPage(index), validAnswer).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, trusteesDateOfBirthRoute)

      val view = application.injector.instanceOf[TrusteesDateOfBirthView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(validAnswer), NormalMode, fakeDraftId, index, trusteeMessagePrefix, trusteeName)(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to Trustee Name page when TrusteesName is not answered" in {
      val userAnswers = emptyUserAnswers
        .set(IsThisLeadTrusteePage(index), false).success.value
        .set(TrusteesDateOfBirthPage(index), validAnswer).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, trusteesDateOfBirthRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.TrusteesNameController.onPageLoad(NormalMode, index, fakeDraftId).url

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val userAnswers = emptyUserAnswers
        .set(IsThisLeadTrusteePage(index), false).success.value
        .set(TrusteesNamePage(index), FullName("FirstName", None, "LastName")).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request =
        FakeRequest(POST, trusteesDateOfBirthRoute)
          .withFormUrlEncodedBody(
            "value.day"   -> validAnswer.getDayOfMonth.toString,
            "value.month" -> validAnswer.getMonthValue.toString,
            "value.year"  -> validAnswer.getYear.toString
          )

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual fakeNavigator.desiredRoute.url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val userAnswers = emptyUserAnswers
        .set(IsThisLeadTrusteePage(index), false).success.value
        .set(TrusteesNamePage(index), FullName("FirstName", None, "LastName")).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request =
        FakeRequest(POST, trusteesDateOfBirthRoute)
          .withFormUrlEncodedBody(("value", "invalid value"))

      val boundForm = form.bind(Map("value" -> "invalid value"))

      val view = application.injector.instanceOf[TrusteesDateOfBirthView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, NormalMode, fakeDraftId, index, trusteeMessagePrefix, trusteeName)(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, trusteesDateOfBirthRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, trusteesDateOfBirthRoute)
          .withFormUrlEncodedBody(
            "value.day"   -> validAnswer.getDayOfMonth.toString,
            "value.month" -> validAnswer.getMonthValue.toString,
            "value.year"  -> validAnswer.getYear.toString
          )

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual SessionExpiredController.onPageLoad().url

      application.stop()
    }


    "for a GET" must {

      def getForIndex(index: Int) : FakeRequest[AnyContentAsEmpty.type] = {
        val route = routes.TrusteesDateOfBirthController.onPageLoad(NormalMode, index, fakeDraftId).url

        FakeRequest(GET, route)
      }

      validateIndex(
        Gen.const(LocalDate.of(2010,10,10)),
        TrusteesDateOfBirthPage.apply,
        getForIndex
      )

    }

    "for a POST" must {
      def postForIndex(index: Int): FakeRequest[AnyContentAsFormUrlEncoded] = {

        val route =
          routes.TrusteesDateOfBirthController.onPageLoad(NormalMode, index, fakeDraftId).url

        FakeRequest(POST, route)
          .withFormUrlEncodedBody(
            "value.day"   -> validAnswer.getDayOfMonth.toString,
            "value.month" -> validAnswer.getMonthValue.toString,
            "value.year"  -> validAnswer.getYear.toString
          )
      }

      validateIndex(
        Gen.const(LocalDate.of(2010,10,10)),
        TrusteesDateOfBirthPage.apply,
        postForIndex
      )
    }

  }
}
