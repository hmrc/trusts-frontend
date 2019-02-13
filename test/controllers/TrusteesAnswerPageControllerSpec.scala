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
import models.{FullName, IndividualOrBusiness, UserAnswers}
import pages.{IsThisLeadTrusteePage, IndividualOrBusinessPage, TrusteesNamePage}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.api.inject.bind
import utils.CheckYourAnswersHelper
import viewmodels.AnswerSection
import views.html.TrusteesAnswerPageView
import navigation.{FakeNavigator, Navigator}
import play.api.mvc.Call

class TrusteesAnswerPageControllerSpec extends SpecBase {

  def onwardRoute = Call("GET", "/foo")

  val index = 0

  "TrusteesAnswerPage Controller" must {

    "return OK and the correct view for a GET" in {

      val answers =
        UserAnswers(userAnswersId)
          .set(IsThisLeadTrusteePage(index), false).success.value
          .set(IndividualOrBusinessPage(index), IndividualOrBusiness.Individual).success.value
          .set(TrusteesNamePage(index), FullName("First", None, "Trustee")).success.value

      val checkYourAnswersHelper = new CheckYourAnswersHelper(answers)

      val expectedSections = Seq(
        AnswerSection(
          None,
          Seq(
            checkYourAnswersHelper.isThisLeadTrustee(index).value,
            checkYourAnswersHelper.trusteeOrIndividual(index).value,
            checkYourAnswersHelper.trusteeFullName(index).value
          )
        )
      )

      val application = applicationBuilder(userAnswers = Some(answers)).build()

      val request = FakeRequest(GET, routes.TrusteesAnswerPageController.onPageLoad(index).url)

      val result = route(application, request).value

      val view = application.injector.instanceOf[TrusteesAnswerPageView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(index, expectedSections)(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(bind[Navigator].toInstance(new FakeNavigator(onwardRoute)))
          .build()

      val request =
        FakeRequest(POST, routes.TrusteesAnswerPageController.onSubmit(index).url)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, routes.TrusteesAnswerPageController.onPageLoad(index).url)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to AddATrustee for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request = FakeRequest(GET, routes.TrusteesAnswerPageController.onPageLoad(index).url)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.AddATrusteeController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(POST, routes.TrusteesAnswerPageController.onSubmit(index).url)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

  }
}
