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

package controllers.register.trustees

import java.time.{LocalDate, ZoneOffset}

import base.RegistrationSpecBase
import models.NormalMode
import models.core.pages.{FullName, IndividualOrBusiness, InternationalAddress, UKAddress}
import pages.register.trustees._
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.CheckYourAnswersHelper
import utils.countryOptions.CountryOptions
import viewmodels.AnswerSection
import views.html.register.trustees.TrusteesAnswerPageView
import controllers.register.routes._

class TrusteesAnswerPageControllerSpec extends RegistrationSpecBase {

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
          .set(TrusteeAddressInTheUKPage(index), true).success.value
          .set(TrusteesUkAddressPage(index), UKAddress("line1", "line2", Some("line3"), Some("line4"), "AB1 1AB")).success.value
          .set(TelephoneNumberPage(index), "0191 1111111").success.value

      val countryOptions = injector.instanceOf[CountryOptions]

      val checkYourAnswersHelper = new CheckYourAnswersHelper(countryOptions)(answers, fakeDraftId, canEdit = true)

      val leadTrusteeIndividualOrBusinessMessagePrefix = "leadTrusteeIndividualOrBusiness"
      val leadTrusteeFullNameMessagePrefix = "leadTrusteesName"
      val titlePrefix = "leadTrusteesAnswerPage"

      val expectedSections = Seq(
        AnswerSection(
          None,
          Seq(
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
        view(index, fakeDraftId, expectedSections, titlePrefix)(fakeRequest, messages).toString

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
          .set(TrusteeAddressInTheUKPage(index), true).success.value
          .set(TrusteesUkAddressPage(index), UKAddress("line1", "line2", Some("line3"), Some("line4"), "AB1 1AB")).success.value
          .set(TelephoneNumberPage(index), "0191 1111111").success.value

      val countryOptions = injector.instanceOf[CountryOptions]

      val checkYourAnswersHelper = new CheckYourAnswersHelper(countryOptions)(answers, fakeDraftId, canEdit = true)

      val trusteeIndividualOrBusinessMessagePrefix = "trusteeIndividualOrBusiness"
      val trusteeFullNameMessagePrefix = "trusteesName"
      val titlePrefix = "trusteesAnswerPage"

      val expectedSections = Seq(
        AnswerSection(
          None,
          Seq(
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
        view(index, fakeDraftId, expectedSections, titlePrefix)(fakeRequest, messages).toString

      application.stop()
    }



    "return OK and the correct view (for a lead trustee uk org with utr) for a GET" in {

      val answers =
        emptyUserAnswers
          .set(IsThisLeadTrusteePage(index), true).success.value
          .set(TrusteeIndividualOrBusinessPage(index), IndividualOrBusiness.Business).success.value
          .set(TrusteeUtrYesNoPage(index), true).success.value
          .set(TrusteeOrgNamePage(index), "Amazon").success.value
          .set(TrusteesUtrPage(index), "1234567890").success.value
          .set(TrusteeOrgAddressUkYesNoPage(index), true).success.value
          .set(TrusteeOrgAddressUkPage(index), UKAddress("line1", "line2", Some("line3"), Some("line4"), "AB1 1AB")).success.value
          .set(TelephoneNumberPage(index), "1256723389").success.value

      val countryOptions = injector.instanceOf[CountryOptions]

      val checkYourAnswersHelper = new CheckYourAnswersHelper(countryOptions)(answers, fakeDraftId, canEdit = true)

      val trusteeIndividualOrBusinessMessagePrefix = "leadTrusteeIndividualOrBusiness"
      val titlePrefix = "leadTrusteesAnswerPage"

      val expectedSections = Seq(
        AnswerSection(
          None,
          Seq(
            checkYourAnswersHelper.trusteeIndividualOrBusiness(index, trusteeIndividualOrBusinessMessagePrefix).value,
            checkYourAnswersHelper.trusteeUtrYesNo(index).value,
            checkYourAnswersHelper.trusteeOrgName(index).value,
            checkYourAnswersHelper.trusteeUtr(index).value,
            checkYourAnswersHelper.orgAddressInTheUkYesNo(index).value,
            checkYourAnswersHelper.trusteesOrgUkAddress(index).value,
            checkYourAnswersHelper.orgTelephoneNumber(index).value
          )
        )
      )

      val application = applicationBuilder(userAnswers = Some(answers)).build()

      val request = FakeRequest(GET, routes.TrusteesAnswerPageController.onPageLoad(index, fakeDraftId).url)

      val result = route(application, request).value

      val view = application.injector.instanceOf[TrusteesAnswerPageView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(index, fakeDraftId, expectedSections, titlePrefix)(fakeRequest, messages).toString

      application.stop()
    }

    "return OK and the correct view (for a lead trustee international org with no utr) for a GET" in {

      val answers =
        emptyUserAnswers
          .set(IsThisLeadTrusteePage(index), true).success.value
          .set(TrusteeIndividualOrBusinessPage(index), IndividualOrBusiness.Business).success.value
          .set(TrusteeUtrYesNoPage(index), false).success.value
          .set(TrusteeOrgNamePage(index), "Amazon").success.value
          .set(TrusteeOrgAddressUkYesNoPage(index), false).success.value
          .set(TrusteeOrgAddressInternationalPage(index), InternationalAddress("line1", "line2", Some("line3"), "Ukraine")).success.value
          .set(TelephoneNumberPage(index), "1256723389").success.value

      val countryOptions = injector.instanceOf[CountryOptions]

      val checkYourAnswersHelper = new CheckYourAnswersHelper(countryOptions)(answers, fakeDraftId, canEdit = true)

      val trusteeIndividualOrBusinessMessagePrefix = "leadTrusteeIndividualOrBusiness"
      val titlePrefix = "leadTrusteesAnswerPage"

      val expectedSections = Seq(
        AnswerSection(
          None,
          Seq(
            checkYourAnswersHelper.trusteeIndividualOrBusiness(index, trusteeIndividualOrBusinessMessagePrefix).value,
            checkYourAnswersHelper.trusteeUtrYesNo(index).value,
            checkYourAnswersHelper.trusteeOrgName(index).value,
            checkYourAnswersHelper.orgAddressInTheUkYesNo(index).value,
            checkYourAnswersHelper.trusteeOrgInternationalAddress(index).value,
            checkYourAnswersHelper.orgTelephoneNumber(index).value
          )
        )
      )

      val application = applicationBuilder(userAnswers = Some(answers)).build()

      val request = FakeRequest(GET, routes.TrusteesAnswerPageController.onPageLoad(index, fakeDraftId).url)

      val result = route(application, request).value

      val view = application.injector.instanceOf[TrusteesAnswerPageView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(index, fakeDraftId, expectedSections, titlePrefix)(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val answers =
        emptyUserAnswers
          .set(IsThisLeadTrusteePage(index), false).success.value
          .set(TrusteeIndividualOrBusinessPage(index),IndividualOrBusiness.Individual).success.value

      val application = applicationBuilder(userAnswers = Some(answers)).build()

      val request =
        FakeRequest(POST, routes.TrusteesAnswerPageController.onSubmit(index, fakeDraftId).url)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual fakeNavigator.desiredRoute.url

      application.stop()
    }


    "redirect to TrusteeIndividualOrBusinessPage when valid data is submitted with no business or individual answer" in {

      val answers =
        emptyUserAnswers
          .set(IsThisLeadTrusteePage(index), true).success.value

      val application =
        applicationBuilder(userAnswers = Some(answers))
          .build()

      val request =
        FakeRequest(POST, routes.TrusteesAnswerPageController.onSubmit(index, fakeDraftId).url)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.TrusteeIndividualOrBusinessController.onPageLoad(NormalMode, index, fakeDraftId).url

      application.stop()
    }

    "redirect to IsThisLeadTrusteePage when valid data is submitted with no Is This Lead Trustee required answer" in {

      val answers =
        emptyUserAnswers
          .set(TrusteeIndividualOrBusinessPage(index),IndividualOrBusiness.Individual).success.value

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
      redirectLocation(result).value mustEqual SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(POST, routes.TrusteesAnswerPageController.onSubmit(index, fakeDraftId).url)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual SessionExpiredController.onPageLoad().url

      application.stop()
    }

  }
}
