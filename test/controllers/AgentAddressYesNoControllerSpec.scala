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
import forms.AgentAddressYesNoFormProvider
import models.{NormalMode, UserAnswers}
import navigation.{FakeNavigator, Navigator}
import pages.{AgentAddressYesNoPage, AgentNamePage}
import play.api.inject.bind
import play.api.libs.json.{JsBoolean, Json}
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core.AffinityGroup
import views.html.AgentAddressYesNoView

class AgentAddressYesNoControllerSpec extends SpecBase {

  def onwardRoute = Call("GET", "/foo")

  val formProvider = new AgentAddressYesNoFormProvider()
  val form = formProvider()
  val name = "name"

  lazy val agentAddressYesNoRoute = routes.AgentAddressYesNoController.onPageLoad(NormalMode).url

  "AgentAddressYesNo Controller" must {

    "return OK and the correct view for a GET" in {

      val userAnswers = UserAnswers(userAnswersId)
        .set(AgentNamePage, name).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers), AffinityGroup.Agent).build()

      val request = FakeRequest(GET, agentAddressYesNoRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[AgentAddressYesNoView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, NormalMode, name)(fakeRequest, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = UserAnswers(userAnswersId)
        .set(AgentAddressYesNoPage, true).success.value
        .set(AgentNamePage, name).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers), AffinityGroup.Agent).build()

      val request = FakeRequest(GET, agentAddressYesNoRoute)

      val view = application.injector.instanceOf[AgentAddressYesNoView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(true), NormalMode, name)(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val userAnswers = UserAnswers(userAnswersId)
        .set(AgentNamePage, name).success.value

      val application =
        applicationBuilder(userAnswers = Some(userAnswers), AffinityGroup.Agent)
          .overrides(bind[Navigator].toInstance(new FakeNavigator(onwardRoute)))
          .build()

      val request =
        FakeRequest(POST, agentAddressYesNoRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val userAnswers = UserAnswers(userAnswersId)
        .set(AgentNamePage, name).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers), AffinityGroup.Agent).build()

      val request =
        FakeRequest(POST, agentAddressYesNoRoute)
          .withFormUrlEncodedBody(("value", ""))

      val boundForm = form.bind(Map("value" -> ""))

      val view = application.injector.instanceOf[AgentAddressYesNoView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, NormalMode, name)(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None, AffinityGroup.Agent).build()

      val request = FakeRequest(GET, agentAddressYesNoRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None, AffinityGroup.Agent).build()

      val request =
        FakeRequest(POST, agentAddressYesNoRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to unauthorised page when accessing Agent resources with AffinityGroup.Organisation" in {

      val application = applicationBuilder(userAnswers = None, AffinityGroup.Organisation).build()

      val request = FakeRequest(GET, agentAddressYesNoRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.UnauthorisedController.onPageLoad().url

      application.stop()
    }
  }
}
