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

package controllers.register.agents

import base.RegistrationSpecBase
import controllers.register.routes._
import forms.AgentTelephoneNumber
import models.NormalMode
import models.core.UserAnswers
import pages.register.agents.{AgentNamePage, AgentTelephoneNumberPage}
import play.api.Application
import play.api.data.Form
import play.api.mvc.{AnyContentAsFormUrlEncoded, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core.AffinityGroup
import views.html.register.agents.AgentTelephoneNumberView

import scala.concurrent.Future

class AgentTelephoneNumberControllerSpec extends RegistrationSpecBase {

  val formProvider = new AgentTelephoneNumber()
  val form: Form[String] = formProvider()
  val agencyName = "FirstName LastName"

  lazy val agentTelephoneNumberRoute: String = routes.AgentTelephoneNumberController.onPageLoad(NormalMode, fakeDraftId).url

  "AgentTelephoneNumber Controller" must {

    "return OK and the correct view for a GET" in {

      val userAnswers: UserAnswers = emptyUserAnswers
        .set(AgentNamePage, "FirstName LastName").success.value

      val application: Application = applicationBuilder(userAnswers = Some(userAnswers), AffinityGroup.Agent).build()

      val request = FakeRequest(GET, agentTelephoneNumberRoute)

      val result: Future[Result] = route(application, request).value

      val view: AgentTelephoneNumberView = application.injector.instanceOf[AgentTelephoneNumberView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, NormalMode, fakeDraftId, agencyName)(request, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers: UserAnswers = emptyUserAnswers
        .set(AgentNamePage, "FirstName LastName").success.value
        .set(AgentTelephoneNumberPage, "answer").success.value

      val application: Application = applicationBuilder(userAnswers = Some(userAnswers), AffinityGroup.Agent).build()

      val request = FakeRequest(GET, agentTelephoneNumberRoute)

      val view: AgentTelephoneNumberView = application.injector.instanceOf[AgentTelephoneNumberView]

      val result: Future[Result] = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill("answer"), NormalMode, fakeDraftId, agencyName)(request, messages).toString

      application.stop()
    }

    "redirect to AgentName page when AgentName is not answered" in {

      val userAnswers: UserAnswers = emptyUserAnswers
        .set(AgentTelephoneNumberPage, "answer").success.value

      val application: Application =
        applicationBuilder(userAnswers = Some(userAnswers), AffinityGroup.Agent)
          .build()

      val request: FakeRequest[AnyContentAsFormUrlEncoded] =
        FakeRequest(POST, agentTelephoneNumberRoute)
          .withFormUrlEncodedBody(("value", "0191 1111111"))

      val result: Future[Result] = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.AgentNameController.onPageLoad(NormalMode, fakeDraftId).url

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val userAnswers: UserAnswers = emptyUserAnswers
        .set(AgentNamePage, "FirstName LastName").success.value
        .set(AgentTelephoneNumberPage, "answer").success.value

      val application: Application =
        applicationBuilder(userAnswers = Some(userAnswers), AffinityGroup.Agent)
          .build()

      val request: FakeRequest[AnyContentAsFormUrlEncoded] =
        FakeRequest(POST, agentTelephoneNumberRoute)
          .withFormUrlEncodedBody(("value", "0191 1111111"))

      val result: Future[Result] = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual fakeNavigator.desiredRoute.url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val userAnswers: UserAnswers = emptyUserAnswers
        .set(AgentNamePage, "FirstName LastName").success.value
        .set(AgentTelephoneNumberPage, "answer").success.value

      val application: Application = applicationBuilder(userAnswers = Some(userAnswers), AffinityGroup.Agent).build()

      val request: FakeRequest[AnyContentAsFormUrlEncoded] =
        FakeRequest(POST, agentTelephoneNumberRoute)
          .withFormUrlEncodedBody(("value", ""))

      val boundForm: Form[String] = form.bind(Map("value" -> ""))

      val view: AgentTelephoneNumberView = application.injector.instanceOf[AgentTelephoneNumberView]

      val result: Future[Result] = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, NormalMode, fakeDraftId, agencyName)(request, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application: Application = applicationBuilder(userAnswers = None, AffinityGroup.Agent).build()

      val request = FakeRequest(GET, agentTelephoneNumberRoute)

      val result: Future[Result] = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application: Application = applicationBuilder(userAnswers = None, AffinityGroup.Agent).build()

      val request: FakeRequest[AnyContentAsFormUrlEncoded] =
        FakeRequest(POST, agentTelephoneNumberRoute)
          .withFormUrlEncodedBody(("value", "answer"))

      val result: Future[Result] = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual SessionExpiredController.onPageLoad().url

      application.stop()
    }
  }
}
