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
import models.{NonResidentType, UserAnswers}
import navigation.{FakeNavigator, Navigator}
import pages._
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.CheckYourAnswersHelper
import utils.countryOptions.CountryOptions
import viewmodels.AnswerSection
import views.html.TrustDetailsAnswerPageView

class TrustDetailsAnswerPageControllerSpec extends SpecBase {

  def onwardRoute = Call("GET", "/foo")

  "TrustDetailsAnswerPageController" when {

    "trust based in the UK" must {

      "return OK and the correct view for a GET" in {

        val answers =
          emptyUserAnswers
            .set(TrustNamePage, "New Trust").success.value
            .set(WhenTrustSetupPage, LocalDate.of(2010, 10, 10)).success.value
            .set(GovernedInsideTheUKPage, true).success.value
            .set(AdministrationInsideUKPage, true).success.value
            .set(TrustResidentInUKPage, true).success.value
            .set(EstablishedUnderScotsLawPage, true).success.value
            .set(TrustResidentOffshorePage, false).success.value

        val application = applicationBuilder(userAnswers = Some(answers)).build()

        val countryOptions = application.injector.instanceOf[CountryOptions]

        val checkYourAnswersHelper = new CheckYourAnswersHelper(countryOptions)(answers, fakeDraftId)

        val expectedSections = Seq(
          AnswerSection(
            None,
            Seq(
              checkYourAnswersHelper.trustName.value,
              checkYourAnswersHelper.whenTrustSetup.value,
              checkYourAnswersHelper.governedInsideTheUK.value,
              checkYourAnswersHelper.administrationInsideUK.value,
              checkYourAnswersHelper.trustResidentInUK.value,
              checkYourAnswersHelper.establishedUnderScotsLaw.value,
              checkYourAnswersHelper.trustResidentOffshore.value
            )
          )
        )

        val request = FakeRequest(GET, routes.TrustDetailsAnswerPageController.onPageLoad.url)

        val result = route(application, request).value

        val view = application.injector.instanceOf[TrustDetailsAnswerPageView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(expectedSections)(fakeRequest, messages).toString

        application.stop()
      }
    }

    "trust not administered in the UK and registering for Schedule 5A" must {

      "return OK and the correct view for a GET" in {

        val answers =
          emptyUserAnswers
            .set(TrustNamePage, "New Trust").success.value
            .set(WhenTrustSetupPage, LocalDate.of(2010, 10, 10)).success.value
            .set(GovernedInsideTheUKPage, true).success.value
            .set(AdministrationInsideUKPage, true).success.value
            .set(TrustResidentInUKPage, false).success.value
            .set(RegisteringTrustFor5APage, true).success.value
            .set(NonResidentTypePage, NonResidentType.NonDomiciled).success.value

        val application = applicationBuilder(userAnswers = Some(answers)).build()

        val countryOptions = application.injector.instanceOf[CountryOptions]

        val checkYourAnswersHelper = new CheckYourAnswersHelper(countryOptions)(answers, fakeDraftId)

        val expectedSections = Seq(
          AnswerSection(
            None,
            Seq(
              checkYourAnswersHelper.trustName.value,
              checkYourAnswersHelper.whenTrustSetup.value,
              checkYourAnswersHelper.governedInsideTheUK.value,
              checkYourAnswersHelper.administrationInsideUK.value,
              checkYourAnswersHelper.trustResidentInUK.value,
              checkYourAnswersHelper.registeringTrustFor5A.value,
              checkYourAnswersHelper.nonresidentType.value
            )
          )
        )

        val request = FakeRequest(GET, routes.TrustDetailsAnswerPageController.onPageLoad.url)

        val result = route(application, request).value

        val view = application.injector.instanceOf[TrustDetailsAnswerPageView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(expectedSections)(fakeRequest, messages).toString

        application.stop()
      }

    }

    "trust not administered in the UK and registering for inheritance tax" must {

      "return OK and the correct view for a GET" in {

        val answers =
          emptyUserAnswers
            .set(TrustNamePage, "New Trust").success.value
            .set(WhenTrustSetupPage, LocalDate.of(2010, 10, 10)).success.value
            .set(GovernedInsideTheUKPage, true).success.value
            .set(AdministrationInsideUKPage, true).success.value
            .set(TrustResidentInUKPage, false).success.value
            .set(RegisteringTrustFor5APage, false).success.value
            .set(InheritanceTaxActPage, true).success.value
            .set(AgentOtherThanBarristerPage, true).success.value

        val application = applicationBuilder(userAnswers = Some(answers)).build()

        val countryOptions = application.injector.instanceOf[CountryOptions]

        val checkYourAnswersHelper = new CheckYourAnswersHelper(countryOptions)(answers, fakeDraftId)

        val expectedSections = Seq(
          AnswerSection(
            None,
            Seq(
              checkYourAnswersHelper.trustName.value,
              checkYourAnswersHelper.whenTrustSetup.value,
              checkYourAnswersHelper.governedInsideTheUK.value,
              checkYourAnswersHelper.administrationInsideUK.value,
              checkYourAnswersHelper.trustResidentInUK.value,
              checkYourAnswersHelper.registeringTrustFor5A.value,
              checkYourAnswersHelper.inheritanceTaxAct.value,
              checkYourAnswersHelper.agentOtherThanBarrister.value
            )
          )
        )

        val request = FakeRequest(GET, routes.TrustDetailsAnswerPageController.onPageLoad.url)

        val result = route(application, request).value

        val view = application.injector.instanceOf[TrustDetailsAnswerPageView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(expectedSections)(fakeRequest, messages).toString

        application.stop()
      }

    }

    "trust not administered or governed in the UK but trustees resident in UK" must {

      "return OK and the correct view for a GET" in {

        val answers =
          emptyUserAnswers
            .set(TrustNamePage, "New Trust").success.value
            .set(WhenTrustSetupPage, LocalDate.of(2010, 10, 10)).success.value
            .set(GovernedInsideTheUKPage, false).success.value
            .set(CountryGoverningTrustPage, "France").success.value
            .set(AdministrationInsideUKPage, false).success.value
            .set(CountryAdministeringTrustPage, "Spain").success.value
            .set(TrustResidentInUKPage, true).success.value
            .set(EstablishedUnderScotsLawPage, false).success.value
            .set(TrustResidentOffshorePage, true).success.value
            .set(TrustPreviouslyResidentPage, "France").success.value

        val application = applicationBuilder(userAnswers = Some(answers)).build()

        val countryOptions = application.injector.instanceOf[CountryOptions]

        val checkYourAnswersHelper = new CheckYourAnswersHelper(countryOptions)(answers, fakeDraftId)

        val expectedSections = Seq(
          AnswerSection(
            None,
            Seq(
              checkYourAnswersHelper.trustName.value,
              checkYourAnswersHelper.whenTrustSetup.value,
              checkYourAnswersHelper.governedInsideTheUK.value,
              checkYourAnswersHelper.countryGoverningTrust.value,
              checkYourAnswersHelper.administrationInsideUK.value,
              checkYourAnswersHelper.countryAdministeringTrust.value,
              checkYourAnswersHelper.trustResidentInUK.value,
              checkYourAnswersHelper.establishedUnderScotsLaw.value,
              checkYourAnswersHelper.trustResidentOffshore.value,
              checkYourAnswersHelper.trustPreviouslyResident.value
            )
          )
        )

        val request = FakeRequest(GET, routes.TrustDetailsAnswerPageController.onPageLoad.url)

        val result = route(application, request).value

        val view = application.injector.instanceOf[TrustDetailsAnswerPageView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(expectedSections)(fakeRequest, messages).toString

        application.stop()
      }

    }

    "redirect to the next page when valid data is submitted" in {

      val answers =
        emptyUserAnswers

      val application =
        applicationBuilder(userAnswers = Some(answers))
          .overrides(bind[Navigator].toInstance(new FakeNavigator(onwardRoute)))
          .build()

      val request =
        FakeRequest(POST, routes.TrustDetailsAnswerPageController.onSubmit.url)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, routes.TrustDetailsAnswerPageController.onPageLoad.url)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(POST, routes.TrustDetailsAnswerPageController.onSubmit.url)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

  }
}
