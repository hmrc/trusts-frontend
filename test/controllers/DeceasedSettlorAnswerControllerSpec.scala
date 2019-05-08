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

import java.time.LocalDate

import base.SpecBase
import models.{FullName, InternationalAddress, NormalMode, UKAddress, UserAnswers}
import pages.{SettlorDateOfBirthYesNoPage, SettlorDateOfDeathPage, SettlorDateOfDeathYesNoPage, SettlorNationalInsuranceNumberPage, SettlorsDateOfBirthPage, SettlorsInternationalAddressPage, SettlorsLastKnownAddressYesNoPage, SettlorsNINoYesNoPage, SettlorsNamePage, SettlorsUKAddressPage, SetupAfterSettlorDiedPage, WasSettlorsAddressUKYesNoPage}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.CheckYourAnswersHelper
import utils.countryOptions.CountryOptions
import viewmodels.AnswerSection
import views.html.DeceasedSettlorAnswerView

class DeceasedSettlorAnswerControllerSpec extends SpecBase {

  "DeceasedSettlorAnswer Controller" must {

    lazy val deceasedSettlorsAnswerRoute = routes.DeceasedSettlorAnswerController.onPageLoad().url

    "return OK and the correct view for a GET" in {

      val answers =
        UserAnswers(userAnswersId)
        .set(SetupAfterSettlorDiedPage, true).success.value
        .set(SettlorsNamePage, FullName("First", None, "Last")).success.value
        .set(SettlorDateOfDeathYesNoPage, true).success.value
        .set(SettlorDateOfDeathPage, LocalDate.now).success.value
        .set(SettlorDateOfBirthYesNoPage, true).success.value
        .set(SettlorsDateOfBirthPage, LocalDate.now).success.value
        .set(SettlorsNINoYesNoPage, true).success.value
        .set(SettlorNationalInsuranceNumberPage, "AB123456C").success.value
        .set(SettlorsLastKnownAddressYesNoPage, true).success.value
        .set(WasSettlorsAddressUKYesNoPage, true).success.value
        .set(SettlorsUKAddressPage, UKAddress("Line1", None, None, "Town", "NE1 1ZZ")).success.value
        .set(SettlorsInternationalAddressPage, InternationalAddress("Line1", "Line2", None, None, "Country")).success.value


      val countryOptions = injector.instanceOf[CountryOptions]

      val checkYourAnswersHelper = new CheckYourAnswersHelper(countryOptions)(answers)

      val expectedSections = Seq(
        AnswerSection(
          None,
          Seq(checkYourAnswersHelper.setupAfterSettlorDied.value,
            checkYourAnswersHelper.settlorsName.value,
            checkYourAnswersHelper.settlorDateOfDeathYesNo.value,
            checkYourAnswersHelper.settlorDateOfDeath.value,
            checkYourAnswersHelper.settlorDateOfBirthYesNo.value,
            checkYourAnswersHelper.settlorsDateOfBirth.value,
            checkYourAnswersHelper.settlorsNINoYesNo.value,
            checkYourAnswersHelper.settlorNationalInsuranceNumber.value,
            checkYourAnswersHelper.settlorsLastKnownAddressYesNo.value,
            checkYourAnswersHelper.wasSettlorsAddressUKYesNo.value,
            checkYourAnswersHelper.settlorsUKAddress.value,
            checkYourAnswersHelper.settlorsInternationalAddress.value
          )
        )
      )

      val application = applicationBuilder(userAnswers = Some(answers)).build()

      val request = FakeRequest(GET, routes.DeceasedSettlorAnswerController.onPageLoad().url)

      val result = route(application, request).value

      val view = application.injector.instanceOf[DeceasedSettlorAnswerView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(expectedSections)(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, deceasedSettlorsAnswerRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, deceasedSettlorsAnswerRoute)


      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to SettlorNamePage when settlor name is not answered" in {


      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request = FakeRequest(GET, deceasedSettlorsAnswerRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SettlorsNameController.onPageLoad(NormalMode).url

      application.stop()
    }
  }
}
