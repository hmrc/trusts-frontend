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
import models.UserAnswers
import pages.AgentTelephoneNumberPage
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core.AffinityGroup
import utils.CheckYourAnswersHelper
import utils.countryOptions.CountryOptions
import viewmodels.AnswerSection
import views.html.AgentAnswerView


class AgentAnswerControllerSpec extends SpecBase {

  val agentID = AffinityGroup.Agent

  def onwardRoute = Call("GET", "/foo")

  "AgentAnswer Controller" must {

    "return OK and the correct view for a GET" in {

      val answers =
        UserAnswers(userAnswersId)
          .set(AgentTelephoneNumberPage, "123456789").success.value

      val countryOptions = injector.instanceOf[CountryOptions]

      val checkYourAnswersHelper = new CheckYourAnswersHelper(countryOptions)(answers)

      val expectedSections = Seq(
        AnswerSection(
          None,
          Seq(
            checkYourAnswersHelper..value,
            checkYourAnswersHelper.agenciesTelephoneNumber.value
          )
        )
      )

      val application = applicationBuilder(userAnswers = Some(answers), agentID).build()

      val request = FakeRequest(GET, routes.AgentAnswerController.onPageLoad().url)

      val result = route(application, request).value

      val view = application.injector.instanceOf[AgentAnswerView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(expectedSections)(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to the next page on a post" in {

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers), agentID)
          .build()

      val request =
        FakeRequest(POST, routes.AgentAnswerController.onSubmit().url)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.AgentAnswerController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None, agentID).build()

      val request = FakeRequest(GET, routes.AgentAnswerController.onPageLoad().url)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None, agentID).build()

      val request = FakeRequest(POST, routes.AgentAnswerController.onSubmit().url)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }
  }
}