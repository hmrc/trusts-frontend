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

package controllers.register.agents

import base.RegistrationSpecBase
import models.core.UserAnswers
import models.core.pages.{InternationalAddress, UKAddress}
import navigation.Navigator
import pages._
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core.AffinityGroup
import utils.CheckYourAnswersHelper
import utils.countryOptions.CountryOptions
import viewmodels.AnswerSection
import views.html.register.agents.AgentAnswerView
import controllers.register.routes._
import pages.register.agents.{AgentAddressYesNoPage, AgentInternalReferencePage, AgentInternationalAddressPage, AgentNamePage, AgentTelephoneNumberPage, AgentUKAddressPage}


class AgentAnswerControllerSpec extends RegistrationSpecBase {

  val agentID: AffinityGroup.Agent.type = AffinityGroup.Agent

  "AgentAnswer Controller" must {

    "return OK and the correct view for a UK address GET" in {

      val answers: UserAnswers =
        emptyUserAnswers
          .set(AgentTelephoneNumberPage, "123456789").success.value
          .set(AgentUKAddressPage, UKAddress("Line1", "Line2", None, Some("TownOrCity"), "NE62RT")).success.value
          .set(AgentAddressYesNoPage, true).success.value
          .set(AgentNamePage, "Sam Curran Trust").success.value
          .set(AgentInternalReferencePage, "123456789").success.value


      val countryOptions: CountryOptions = injector.instanceOf[CountryOptions]

      val checkYourAnswersHelper = new CheckYourAnswersHelper(countryOptions)(answers, fakeDraftId)

      val expectedSections = Seq(
        AnswerSection(
          None,
          Seq(
            checkYourAnswersHelper.agentInternalReference.value,
            checkYourAnswersHelper.agentName.value,
            checkYourAnswersHelper.agentAddressYesNo.value,
            checkYourAnswersHelper.agentUKAddress.value,
            checkYourAnswersHelper.agenciesTelephoneNumber.value
          )
        )
      )

      val application = applicationBuilder(userAnswers = Some(answers), agentID).build()

      val request = FakeRequest(GET, routes.AgentAnswerController.onPageLoad(fakeDraftId).url)

      val result = route(application, request).value

      val view = application.injector.instanceOf[AgentAnswerView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(fakeDraftId, expectedSections)(fakeRequest, messages).toString

      application.stop()
    }

    "return OK and the correct view for a International address GET" in {

      val answers: UserAnswers =
        emptyUserAnswers
          .set(AgentTelephoneNumberPage, "123456789").success.value
          .set(AgentInternationalAddressPage, InternationalAddress("Line1", "Line2", None, "Country")).success.value
          .set(AgentAddressYesNoPage, false).success.value
          .set(AgentNamePage, "Sam Curran Trust").success.value
          .set(AgentInternalReferencePage, "123456789").success.value


      val countryOptions = injector.instanceOf[CountryOptions]

      val checkYourAnswersHelper = new CheckYourAnswersHelper(countryOptions)(answers, fakeDraftId)

      val expectedSections = Seq(
        AnswerSection(
          None,
          Seq(
            checkYourAnswersHelper.agentInternalReference.value,
            checkYourAnswersHelper.agentName.value,
            checkYourAnswersHelper.agentAddressYesNo.value,
            checkYourAnswersHelper.agentInternationalAddress.value,
            checkYourAnswersHelper.agenciesTelephoneNumber.value
          )
        )
      )

      val application = applicationBuilder(userAnswers = Some(answers), agentID).build()

      val request = FakeRequest(GET, routes.AgentAnswerController.onPageLoad(fakeDraftId).url)

      val result = route(application, request).value

      val view = application.injector.instanceOf[AgentAnswerView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(fakeDraftId, expectedSections)(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to the next page on a post" in {

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers), agentID, navigator = injector.instanceOf[Navigator]).build()

      val request =
        FakeRequest(POST, routes.AgentAnswerController.onSubmit(fakeDraftId).url)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual TaskListController.onPageLoad(fakeDraftId).url

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None, agentID).build()

      val request = FakeRequest(GET, routes.AgentAnswerController.onPageLoad(fakeDraftId).url)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None, agentID).build()

      val request = FakeRequest(POST, routes.AgentAnswerController.onSubmit(fakeDraftId).url)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual SessionExpiredController.onPageLoad().url

      application.stop()
    }
  }
}