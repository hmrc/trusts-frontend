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

package controllers.living_settlor

import java.time.{LocalDate, ZoneOffset}

import base.SpecBase
import models.{FullName, IndividualOrBusiness, InternationalAddress, NormalMode, PassportOrIdCardDetails, UKAddress}
import pages.living_settlor.{SettlorIndividualOrBusinessPage, _}
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.CheckYourAnswersHelper
import utils.countryOptions.CountryOptions
import viewmodels.AnswerSection
import views.html.living_settlor.SettlorIndividualAnswersView

class SettlorIndividualAnswerControllerSpec extends SpecBase {

  def onwardRoute = Call("GET", "/foo")

  val settlorName = FullName("first name", Some("middle name"), "last name")
  val validDate = LocalDate.now(ZoneOffset.UTC)
  val nino = "CC123456A"
  val AddressUK = UKAddress("line 1", Some("line 2"), Some("line 3"), "line 4", "line 5")
  val AddressInternational = InternationalAddress("line 1", "line 2", Some("line 3"), Some("line 4"), "ES")
  val passportOrIDCardDetails = PassportOrIdCardDetails("Field 1", "Field 2", LocalDate.now(ZoneOffset.UTC))
  val index: Int = 0

  lazy val settlorIndividualAnswerRoute = controllers.living_settlor.routes.SettlorIndividualAnswerController.onPageLoad(index, fakeDraftId).url

  "SettlorIndividualAnswer Controller" must {

    "settlor individual -  no date of birth, no nino, no address" must {

      "return OK and the correct view for a GET" in {

        val userAnswers =
          emptyUserAnswers
            .set(SettlorIndividualOrBusinessPage(index), IndividualOrBusiness.Individual).success.value
            .set(SettlorIndividualNamePage(index), settlorName).success.value
            .set(SettlorIndividualDateOfBirthYesNoPage(index), false).success.value
            .set(SettlorIndividualNINOYesNoPage(index), false).success.value
            .set(SettlorIndividualAddressYesNoPage(index), false).success.value

        val countryOptions = injector.instanceOf[CountryOptions]
        val checkYourAnswersHelper = new CheckYourAnswersHelper(countryOptions)(userAnswers, fakeDraftId)

        val expectedSections = Seq(
          AnswerSection(
            None,
            Seq(
              checkYourAnswersHelper.settlorIndividualOrBusiness(index).value,
              checkYourAnswersHelper.settlorIndividualName(index).value,
              checkYourAnswersHelper.settlorIndividualDateOfBirthYesNo(index).value,
              checkYourAnswersHelper.settlorIndividualNINOYesNo(index).value,
              checkYourAnswersHelper.settlorIndividualAddressYesNo(index).value
            )
          )
        )

        val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

        val request = FakeRequest(GET, settlorIndividualAnswerRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[SettlorIndividualAnswersView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(index, fakeDraftId, expectedSections)(fakeRequest, messages).toString

        application.stop()
      }

    }

    "settlor individual -  with date of birth, with nino, and no address" must {

      "return OK and the correct view for a GET" in {

        val userAnswers =
          emptyUserAnswers
            .set(SettlorIndividualOrBusinessPage(index), IndividualOrBusiness.Individual).success.value
            .set(SettlorIndividualNamePage(index), settlorName).success.value
            .set(SettlorIndividualDateOfBirthYesNoPage(index), true).success.value
            .set(SettlorIndividualDateOfBirthPage(index), validDate).success.value
            .set(SettlorIndividualNINOYesNoPage(index), true).success.value
            .set(SettlorIndividualNINOPage(index), nino).success.value
            .set(SettlorIndividualAddressYesNoPage(index), false).success.value

        val countryOptions = injector.instanceOf[CountryOptions]
        val checkYourAnswersHelper = new CheckYourAnswersHelper(countryOptions)(userAnswers, fakeDraftId)

        val expectedSections = Seq(
          AnswerSection(
            None,
            Seq(
              checkYourAnswersHelper.settlorIndividualOrBusiness(index).value,
              checkYourAnswersHelper.settlorIndividualName(index).value,
              checkYourAnswersHelper.settlorIndividualDateOfBirthYesNo(index).value,
              checkYourAnswersHelper.settlorIndividualDateOfBirth(index).value,
              checkYourAnswersHelper.settlorIndividualNINOYesNo(index).value,
              checkYourAnswersHelper.settlorIndividualNINO(index).value,
              checkYourAnswersHelper.settlorIndividualAddressYesNo(index).value
            )
          )
        )

        val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

        val request = FakeRequest(GET, settlorIndividualAnswerRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[SettlorIndividualAnswersView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(index, fakeDraftId, expectedSections)(fakeRequest, messages).toString

        application.stop()
      }

    }

    "settlor individual -  no date of birth, no nino, UK address, no passport, no ID card" must {

      "return OK and the correct view for a GET" in {

        val userAnswers =
          emptyUserAnswers
            .set(SettlorIndividualOrBusinessPage(index), IndividualOrBusiness.Individual).success.value
            .set(SettlorIndividualNamePage(index), settlorName).success.value
            .set(SettlorIndividualDateOfBirthYesNoPage(index), false).success.value
            .set(SettlorIndividualNINOYesNoPage(index), false).success.value
            .set(SettlorIndividualAddressYesNoPage(index), true).success.value
            .set(SettlorIndividualAddressUKYesNoPage(index), true).success.value
            .set(SettlorIndividualAddressUKPage(index), AddressUK).success.value
            .set(SettlorIndividualPassportYesNoPage(index), false).success.value
            .set(SettlorIndividualIDCardYesNoPage(index), false).success.value

        val countryOptions = injector.instanceOf[CountryOptions]
        val checkYourAnswersHelper = new CheckYourAnswersHelper(countryOptions)(userAnswers, fakeDraftId)

        val expectedSections = Seq(
          AnswerSection(
            None,
            Seq(
              checkYourAnswersHelper.settlorIndividualOrBusiness(index).value,
              checkYourAnswersHelper.settlorIndividualName(index).value,
              checkYourAnswersHelper.settlorIndividualDateOfBirthYesNo(index).value,
              checkYourAnswersHelper.settlorIndividualNINOYesNo(index).value,
              checkYourAnswersHelper.settlorIndividualAddressYesNo(index).value,
              checkYourAnswersHelper.settlorIndividualAddressUKYesNo(index).value,
              checkYourAnswersHelper.settlorIndividualAddressUK(index).value,
              checkYourAnswersHelper.settlorIndividualPassportYesNo(index).value,
              checkYourAnswersHelper.settlorIndividualIDCardYesNo(index).value
            )
          )
        )

        val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

        val request = FakeRequest(GET, settlorIndividualAnswerRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[SettlorIndividualAnswersView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(index, fakeDraftId, expectedSections)(fakeRequest, messages).toString

        application.stop()
      }

    }

    "settlor individual -  no date of birth, no nino, International address, no passport, no ID card" must {

      "return OK and the correct view for a GET" in {

        val userAnswers =
          emptyUserAnswers
            .set(SettlorIndividualOrBusinessPage(index), IndividualOrBusiness.Individual).success.value
            .set(SettlorIndividualNamePage(index), settlorName).success.value
            .set(SettlorIndividualDateOfBirthYesNoPage(index), false).success.value
            .set(SettlorIndividualNINOYesNoPage(index), false).success.value
            .set(SettlorIndividualAddressYesNoPage(index), true).success.value
            .set(SettlorIndividualAddressUKYesNoPage(index), false).success.value
            .set(SettlorIndividualAddressInternationalPage(index), AddressInternational).success.value
            .set(SettlorIndividualPassportYesNoPage(index), false).success.value
            .set(SettlorIndividualIDCardYesNoPage(index), false).success.value

        val countryOptions = injector.instanceOf[CountryOptions]
        val checkYourAnswersHelper = new CheckYourAnswersHelper(countryOptions)(userAnswers, fakeDraftId)

        val expectedSections = Seq(
          AnswerSection(
            None,
            Seq(
              checkYourAnswersHelper.settlorIndividualOrBusiness(index).value,
              checkYourAnswersHelper.settlorIndividualName(index).value,
              checkYourAnswersHelper.settlorIndividualDateOfBirthYesNo(index).value,
              checkYourAnswersHelper.settlorIndividualNINOYesNo(index).value,
              checkYourAnswersHelper.settlorIndividualAddressYesNo(index).value,
              checkYourAnswersHelper.settlorIndividualAddressUKYesNo(index).value,
              checkYourAnswersHelper.settlorIndividualAddressInternational(index).value,
              checkYourAnswersHelper.settlorIndividualPassportYesNo(index).value,
              checkYourAnswersHelper.settlorIndividualIDCardYesNo(index).value
            )
          )
        )

        val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

        val request = FakeRequest(GET, settlorIndividualAnswerRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[SettlorIndividualAnswersView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(index, fakeDraftId, expectedSections)(fakeRequest, messages).toString

        application.stop()
      }

    }

    "settlor individual -  no date of birth, no nino, UK address, passport, ID card" must {

      "return OK and the correct view for a GET" in {

        val userAnswers =
          emptyUserAnswers
            .set(SettlorIndividualOrBusinessPage(index), IndividualOrBusiness.Individual).success.value
            .set(SettlorIndividualNamePage(index), settlorName).success.value
            .set(SettlorIndividualDateOfBirthYesNoPage(index), false).success.value
            .set(SettlorIndividualNINOYesNoPage(index), false).success.value
            .set(SettlorIndividualAddressYesNoPage(index), true).success.value
            .set(SettlorIndividualAddressUKYesNoPage(index), true).success.value
            .set(SettlorIndividualAddressUKPage(index), AddressUK).success.value
            .set(SettlorIndividualPassportYesNoPage(index), true).success.value
            .set(SettlorIndividualPassportPage(index), passportOrIDCardDetails).success.value
            .set(SettlorIndividualIDCardYesNoPage(index), true).success.value
            .set(SettlorIndividualIDCardPage(index), passportOrIDCardDetails).success.value

        val countryOptions = injector.instanceOf[CountryOptions]
        val checkYourAnswersHelper = new CheckYourAnswersHelper(countryOptions)(userAnswers, fakeDraftId)

        val expectedSections = Seq(
          AnswerSection(
            None,
            Seq(
              checkYourAnswersHelper.settlorIndividualOrBusiness(index).value,
              checkYourAnswersHelper.settlorIndividualName(index).value,
              checkYourAnswersHelper.settlorIndividualDateOfBirthYesNo(index).value,
              checkYourAnswersHelper.settlorIndividualNINOYesNo(index).value,
              checkYourAnswersHelper.settlorIndividualAddressYesNo(index).value,
              checkYourAnswersHelper.settlorIndividualAddressUKYesNo(index).value,
              checkYourAnswersHelper.settlorIndividualAddressUK(index).value,
              checkYourAnswersHelper.settlorIndividualPassportYesNo(index).value,
              checkYourAnswersHelper.settlorIndividualPassport(index).value,
              checkYourAnswersHelper.settlorIndividualIDCardYesNo(index).value,
              checkYourAnswersHelper.settlorIndividualIDCard(index).value
            )
          )
        )

        val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

        val request = FakeRequest(GET, settlorIndividualAnswerRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[SettlorIndividualAnswersView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(index, fakeDraftId, expectedSections)(fakeRequest, messages).toString

        application.stop()
      }

    }


    "redirect to SettlorIndividualOrBusinessPage on a GET if no answer for 'Is the settlor an individual or business?' at index" in {
      val answers =
        emptyUserAnswers
          .set(SettlorIndividualNamePage(index), settlorName).success.value
          .set(SettlorIndividualDateOfBirthYesNoPage(index), false).success.value
          .set(SettlorIndividualNINOYesNoPage(index), false).success.value
          .set(SettlorIndividualAddressYesNoPage(index), false).success.value

      val application = applicationBuilder(userAnswers = Some(answers)).build()

      val request = FakeRequest(GET, settlorIndividualAnswerRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SettlorIndividualOrBusinessController.onPageLoad(NormalMode, index, fakeDraftId).url

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, settlorIndividualAnswerRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

  }
}
