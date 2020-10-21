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
import forms.YesNoFormProvider
import org.mockito.Matchers
import org.mockito.Matchers.any
import org.mockito.Mockito.{reset, times, verify, when}
import play.api.data.Form
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}
import viewmodels.DraftRegistration
import views.html.register.agents.RemoveDraftYesNoView

import scala.concurrent.Future

class RemoveDraftYesNoControllerSpec extends RegistrationSpecBase {

  private val form: Form[Boolean] = new YesNoFormProvider().withPrefix("removeDraftYesNo")

  private lazy val removeDraftYesNoRoute: String = routes.RemoveDraftYesNoController.onPageLoad(fakeDraftId).url
  private lazy val agentOverviewRoute: String = routes.AgentOverviewController.onPageLoad().url

  private val clientReferenceNumber: String = "crn"
  private val savedUntil: String = "3 February 1996"

  private val draftRegistration: DraftRegistration = DraftRegistration(fakeDraftId, clientReferenceNumber, savedUntil)

  "RemoveDraftYesNo Controller" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      when(registrationsRepository.getDraft(any())(any())).thenReturn(Future.successful(draftRegistration))

      val request = FakeRequest(GET, removeDraftYesNoRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[RemoveDraftYesNoView]

      status(result) mustEqual OK

      val content: String = contentAsString(result)

      content mustEqual
        view(form, fakeDraftId, clientReferenceNumber)(request, messages).toString

      content must include(s"Are you sure you want to remove $clientReferenceNumber?")

      application.stop()
    }

    "remove draft and redirect to agent overview when YES submitted" in {

      reset(registrationsRepository)

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      when(registrationsRepository.removeDraft(any())(any())).thenReturn(Future.successful(HttpResponse(OK)))

      val request = FakeRequest(POST, removeDraftYesNoRoute)
        .withFormUrlEncodedBody(("value", "true"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual agentOverviewRoute

      verify(registrationsRepository, times(1)).removeDraft(Matchers.eq(fakeDraftId))(any[HeaderCarrier])

      application.stop()
    }

    "redirect to agent overview when NO submitted" in {

      reset(registrationsRepository)

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request = FakeRequest(POST, removeDraftYesNoRoute)
        .withFormUrlEncodedBody(("value", "false"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual agentOverviewRoute

      verify(registrationsRepository, times(0)).removeDraft(Matchers.eq(fakeDraftId))(any[HeaderCarrier])

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      when(registrationsRepository.getDraft(any())(any())).thenReturn(Future.successful(draftRegistration))

      val request = FakeRequest(POST, removeDraftYesNoRoute)
        .withFormUrlEncodedBody(("value", ""))

      val boundForm = form.bind(Map("value" -> ""))

      val view = application.injector.instanceOf[RemoveDraftYesNoView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, fakeDraftId, clientReferenceNumber)(request, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, removeDraftYesNoRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, removeDraftYesNoRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual SessionExpiredController.onPageLoad().url

      application.stop()
    }
  }
}
