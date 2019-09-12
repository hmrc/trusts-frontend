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

import java.time.{LocalDate, ZoneOffset}

import base.SpecBase
import models.{FullName, IndividualOrBusiness, NormalMode, UKAddress}
import pages.trustees._
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.CheckYourAnswersHelper
import utils.countryOptions.CountryOptions
import viewmodels.AnswerSection
import views.html.trustees.TrusteesAnswerPageView

class TrusteesAnswerPageControllerSpec extends SpecBase {

  val index = 0

  "TrusteesAnswerPage Controller" must {

    "return OK and the correct view (for a lead trustee) for a GET" in {

      val answers =
        emptyUserAnswers
          .set(IsThisLeadTrusteePage(index), true).success.value
          .set(TrusteeIndividualOrBusinessPage(index), IndividualOrBusiness.Individual).success.value
          .set(TrusteesNamePage(index), FullName("First", None, "Trustee")).success.value
          .set(TrusteesDateOfBirthPage(index), LocalDate.now(ZoneOffset.UTC)).success.value
          .set(TrusteeAUKCitizenPage(index), true).success.value
          .set(TrusteesNinoPage(index), "AB123456C").success.value
          .set(TelephoneNumberPage(index), "0191 1111111").success.value
          .set(TrusteeLiveInTheUKPage(index), true).success.value
          .set(TrusteesUkAddressPage(index), UKAddress("line1", Some("line2"), Some("line3"), "town or city", "AB1 1AB")).success.value


      val countryOptions = injector.instanceOf[CountryOptions]

      val checkYourAnswersHelper = new CheckYourAnswersHelper(countryOptions)(answers, fakeDraftId)

      val leadTrusteeIndividualOrBusinessMessagePrefix = "leadTrusteeIndividualOrBusiness"
      val leadTrusteeFullNameMessagePrefix = "leadTrusteesName"

      val expectedSections = Seq(
        AnswerSection(
          None,
          Seq(
            checkYourAnswersHelper.isThisLeadTrustee(index).value,
            checkYourAnswersHelper.trusteeIndividualOrBusiness(index, leadTrusteeIndividualOrBusinessMessagePrefix).value,
            checkYourAnswersHelper.trusteeFullName(index, leadTrusteeFullNameMessagePrefix).value,
            checkYourAnswersHelper.trusteesDateOfBirth(index).value,
            checkYourAnswersHelper.trusteeAUKCitizen(index).value,
            checkYourAnswersHelper.trusteesNino(index).value,
            checkYourAnswersHelper.trusteeLiveInTheUK(index).value,
            checkYourAnswersHelper.trusteesUkAddress(index).value,
            checkYourAnswersHelper.telephoneNumber(index).value
          )
        )
      )

      val application = applicationBuilder(userAnswers = Some(answers)).build()

      val request = FakeRequest(GET, routes.TrusteesAnswerPageController.onPageLoad(index, fakeDraftId).url)

      val result = route(application, request).value

      val view = application.injector.instanceOf[TrusteesAnswerPageView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(index, fakeDraftId, expectedSections)(fakeRequest, messages).toString

      application.stop()
    }

    "return OK and the correct view (for a trustee) for a GET" in {

      val answers =
        emptyUserAnswers
          .set(IsThisLeadTrusteePage(index), false).success.value
          .set(TrusteeIndividualOrBusinessPage(index), IndividualOrBusiness.Individual).success.value
          .set(TrusteesNamePage(index), FullName("First", None, "Trustee")).success.value
          .set(TrusteesDateOfBirthPage(index), LocalDate.now(ZoneOffset.UTC)).success.value
          .set(TrusteeAUKCitizenPage(index), true).success.value
          .set(TrusteesNinoPage(index), "AB123456C").success.value
          .set(TelephoneNumberPage(index), "0191 1111111").success.value
          .set(TrusteeLiveInTheUKPage(index), true).success.value
          .set(TrusteesUkAddressPage(index), UKAddress("line1", Some("line2"), Some("line3"), "town or city", "AB1 1AB")).success.value


      val countryOptions = injector.instanceOf[CountryOptions]

      val checkYourAnswersHelper = new CheckYourAnswersHelper(countryOptions)(answers, fakeDraftId)

      val trusteeIndividualOrBusinessMessagePrefix = "trusteeIndividualOrBusiness"
      val trusteeFullNameMessagePrefix = "trusteesName"

      val expectedSections = Seq(
        AnswerSection(
          None,
          Seq(
            checkYourAnswersHelper.isThisLeadTrustee(index).value,
            checkYourAnswersHelper.trusteeIndividualOrBusiness(index, trusteeIndividualOrBusinessMessagePrefix).value,
            checkYourAnswersHelper.trusteeFullName(index, trusteeFullNameMessagePrefix).value,
            checkYourAnswersHelper.trusteesDateOfBirth(index).value,
            checkYourAnswersHelper.trusteeAUKCitizen(index).value,
            checkYourAnswersHelper.trusteesNino(index).value,
            checkYourAnswersHelper.trusteeLiveInTheUK(index).value,
            checkYourAnswersHelper.trusteesUkAddress(index).value,
            checkYourAnswersHelper.telephoneNumber(index).value
          )
        )
      )

      val application = applicationBuilder(userAnswers = Some(answers)).build()

      val request = FakeRequest(GET, routes.TrusteesAnswerPageController.onPageLoad(index, fakeDraftId).url)

      val result = route(application, request).value

      val view = application.injector.instanceOf[TrusteesAnswerPageView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(index, fakeDraftId, expectedSections)(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to TrusteeName on a GET if no name for trustee at index" in {
      val answers =
        emptyUserAnswers
          .set(IsThisLeadTrusteePage(index), false).success.value
          .set(TrusteeIndividualOrBusinessPage(index), IndividualOrBusiness.Individual).success.value
          .set(TrusteesDateOfBirthPage(index), LocalDate.now(ZoneOffset.UTC)).success.value
          .set(TrusteeAUKCitizenPage(index), true).success.value

      val application = applicationBuilder(userAnswers = Some(answers)).build()

      val request = FakeRequest(GET, routes.TrusteesAnswerPageController.onPageLoad(index, fakeDraftId).url)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.TrusteesNameController.onPageLoad(NormalMode, index, fakeDraftId).url

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val answers =
        emptyUserAnswers
          .set(TrusteesNamePage(index), FullName("First", None, "Trustee")).success.value
          .set(IsThisLeadTrusteePage(index), false).success.value

      val application = applicationBuilder(userAnswers = Some(answers)).build()

      val request =
        FakeRequest(POST, routes.TrusteesAnswerPageController.onSubmit(index, fakeDraftId).url)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual fakeNavigator.desiredRoute.url

      application.stop()
    }

    "redirect to TrusteeNamePage when valid data is submitted with no Trustee Name required answer" in {

      val answers =
        emptyUserAnswers
          .set(IsThisLeadTrusteePage(index), false).success.value

      val application =
        applicationBuilder(userAnswers = Some(answers))
          .build()

      val request =
        FakeRequest(POST, routes.TrusteesAnswerPageController.onSubmit(index, fakeDraftId).url)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.TrusteesNameController.onPageLoad(NormalMode, index, fakeDraftId).url

      application.stop()
    }

    "redirect to IsThisLeadTrusteePage when valid data is submitted with no Is This Lead Trustee required answer" in {

      val answers =
        emptyUserAnswers
          .set(TrusteesNamePage(index), FullName("First", None, "Trustee")).success.value

      val application =
        applicationBuilder(userAnswers = Some(answers))
          .build()

      val request =
        FakeRequest(POST, routes.TrusteesAnswerPageController.onSubmit(index, fakeDraftId).url)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.IsThisLeadTrusteeController.onPageLoad(NormalMode, index, fakeDraftId).url

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, routes.TrusteesAnswerPageController.onPageLoad(index, fakeDraftId).url)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(POST, routes.TrusteesAnswerPageController.onSubmit(index, fakeDraftId).url)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

  }
}
