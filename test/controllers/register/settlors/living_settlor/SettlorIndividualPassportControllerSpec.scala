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

package controllers.register.settlors.living_settlor

import java.time.{LocalDate, ZoneOffset}

import base.RegistrationSpecBase
import controllers.IndexValidation
import controllers.register.routes._
import forms.PassportOrIdCardFormProvider
import models.NormalMode
import models.core.pages.{FullName, IndividualOrBusiness}
import models.registration.pages.PassportOrIdCardDetails
import org.scalacheck.Arbitrary.arbitrary
import pages.register.settlors.living_settlor.{SettlorIndividualDateOfBirthYesNoPage, SettlorIndividualNamePage, SettlorIndividualPassportPage}
import play.api.mvc.{AnyContentAsEmpty, AnyContentAsFormUrlEncoded, Call}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils._
import utils.countryOptions.CountryOptions
import views.html.register.settlors.living_settlor.SettlorIndividualPassportView

class SettlorIndividualPassportControllerSpec extends RegistrationSpecBase with IndexValidation {

  def onwardRoute = Call("GET", "/foo")

  val formProvider = new PassportOrIdCardFormProvider(frontendAppConfig)
  val form = formProvider("settlorIndividualPassport")
  val index = 0
  val name = FullName("First", Some("Middle"), "Last")
  val validAnswer = LocalDate.now(ZoneOffset.UTC)

  lazy val settlorIndividualPassportRoute = routes.SettlorIndividualPassportController.onPageLoad(NormalMode, index, fakeDraftId).url

  "SettlorIndividualPassport Controller" must {

    "return OK and the correct view for a GET" in {

      val userAnswers = emptyUserAnswers
        .set(SettlorIndividualNamePage(index),name).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, settlorIndividualPassportRoute)

      val view = application.injector.instanceOf[SettlorIndividualPassportView]

      val result = route(application, request).value

      val countryOptions: Seq[InputOption] = app.injector.instanceOf[CountryOptions].options

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, countryOptions, NormalMode, fakeDraftId, index, name)(request, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers
        .set(SettlorIndividualNamePage(index),name).success.value
        .set(SettlorIndividualPassportPage(index),
          PassportOrIdCardDetails("Field 1", "Field 2", validAnswer )).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, settlorIndividualPassportRoute)

      val view = application.injector.instanceOf[SettlorIndividualPassportView]

      val result = route(application, request).value

      val countryOptions: Seq[InputOption] = app.injector.instanceOf[CountryOptions].options

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(PassportOrIdCardDetails("Field 1", "Field 2", validAnswer)),
          countryOptions, NormalMode, fakeDraftId, index, name)(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val userAnswers = emptyUserAnswers
        .set(SettlorIndividualNamePage(index),name).success.value

      val application =
        applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request =
        FakeRequest(POST, settlorIndividualPassportRoute)
          .withFormUrlEncodedBody(
            "country" -> "country",
            "number" -> "123456",
            "expiryDate.day"   -> validAnswer.getDayOfMonth.toString,
            "expiryDate.month" -> validAnswer.getMonthValue.toString,
            "expiryDate.year"  -> validAnswer.getYear.toString)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url

      application.stop()
    }

    "redirect to Settlors Name page when Settlors name is not answered" in {

      val userAnswers = emptyUserAnswers.set(SettlorIndividualDateOfBirthYesNoPage(index), true).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, settlorIndividualPassportRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SettlorIndividualNameController.onPageLoad(NormalMode, index, fakeDraftId).url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val userAnswers = emptyUserAnswers
        .set(SettlorIndividualNamePage(index),name).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request =
        FakeRequest(POST, settlorIndividualPassportRoute)
          .withFormUrlEncodedBody(("value", "invalid value"))

      val boundForm = form.bind(Map("value" -> "invalid value"))

      val view = application.injector.instanceOf[SettlorIndividualPassportView]

      val result = route(application, request).value

      val countryOptions: Seq[InputOption] = app.injector.instanceOf[CountryOptions].options

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, countryOptions, NormalMode, fakeDraftId, index, name)(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, settlorIndividualPassportRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, settlorIndividualPassportRoute)
          .withFormUrlEncodedBody(("field1", "value 1"), ("field2", "value 2"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual SessionExpiredController.onPageLoad().url

      application.stop()
    }


    "for a GET" must {

      def getForIndex(index: Int) : FakeRequest[AnyContentAsEmpty.type] = {
        val route = routes.SettlorIndividualPassportController.onPageLoad(NormalMode, index, fakeDraftId).url

        FakeRequest(GET, route)
      }

      validateIndex(
        arbitrary[PassportOrIdCardDetails],
        SettlorIndividualPassportPage.apply,
        getForIndex
      )

    }

    "for a POST" must {
      def postForIndex(index: Int): FakeRequest[AnyContentAsFormUrlEncoded] = {

        val route =
          routes.SettlorIndividualPassportController.onPageLoad(NormalMode, index, fakeDraftId).url

        FakeRequest(POST, route)
          .withFormUrlEncodedBody(("value", IndividualOrBusiness.values.head.toString))
      }

      validateIndex(
        arbitrary[PassportOrIdCardDetails],
        SettlorIndividualPassportPage.apply,
        postForIndex
      )
    }
  }
}
