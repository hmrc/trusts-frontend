/*
 * Copyright 2021 HM Revenue & Customs
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

package controllers.register

import base.RegistrationSpecBase
import controllers.Assets.Redirect
import controllers.register.routes._
import controllers.register.agents.routes.AgentInternalReferenceController
import forms.YesNoFormProvider
import models.{Mode, NormalMode}
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import pages.register.TrustRegisteredWithUkAddressYesNoPage
import play.api.data.Form
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.MatchingService
import uk.gov.hmrc.auth.core.AffinityGroup
import views.html.register.TrustRegisteredWithUkAddressYesNoView

import scala.concurrent.Future

class TrustRegisteredWithUkAddressYesNoControllerSpec extends RegistrationSpecBase {

  private val form: Form[Boolean] = new YesNoFormProvider().withPrefix("trustRegisteredWithUkAddress")

  private val mode: Mode = NormalMode

  private lazy val trustRegisteredWithUkAddressYesNoRoute: String = routes.TrustRegisteredWithUkAddressYesNoController.onPageLoad(NormalMode, fakeDraftId).url
  private lazy val postcodeRoute: String = routes.PostcodeForTheTrustController.onPageLoad(NormalMode, fakeDraftId).url
  private lazy val taskListRoute: String = routes.TaskListController.onPageLoad(fakeDraftId).url
  private lazy val agentDetailsRoute: String = AgentInternalReferenceController.onPageLoad(mode, fakeDraftId).url

  "TrustRegisteredWithUkAddressYesNo Controller" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request = FakeRequest(GET, trustRegisteredWithUkAddressYesNoRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[TrustRegisteredWithUkAddressYesNoView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, NormalMode, fakeDraftId)(request, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers
        .set(TrustRegisteredWithUkAddressYesNoPage, true).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, trustRegisteredWithUkAddressYesNoRoute)

      val view = application.injector.instanceOf[TrustRegisteredWithUkAddressYesNoView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(true), NormalMode, fakeDraftId)(request, messages).toString

      application.stop()
    }

    "redirect to PostcodeForTheTrust page when YES submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request = FakeRequest(POST, trustRegisteredWithUkAddressYesNoRoute)
        .withFormUrlEncodedBody(("value", "true"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual postcodeRoute

      application.stop()
    }

    "redirect to Task List when non-agent and valid data is submitted" in {

      val mockMatchingService: MatchingService = mock[MatchingService]
      when(mockMatchingService.matching(any(), any(), any(), any())(any(), any())).thenReturn(Future.successful(Redirect(taskListRoute)))

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(bind[MatchingService].toInstance(mockMatchingService))
          .build()

      val request =
        FakeRequest(POST, trustRegisteredWithUkAddressYesNoRoute)
          .withFormUrlEncodedBody(("value", "false"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual taskListRoute

      application.stop()
    }

    "redirect to Agent Details when agent and valid data is submitted" in {

      val mockMatchingService: MatchingService = mock[MatchingService]
      when(mockMatchingService.matching(any(), any(), any(), any())(any(), any())).thenReturn(Future.successful(Redirect(agentDetailsRoute)))

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers), AffinityGroup.Agent)
          .overrides(bind[MatchingService].toInstance(mockMatchingService))
          .build()

      val request =
        FakeRequest(POST, trustRegisteredWithUkAddressYesNoRoute)
          .withFormUrlEncodedBody(("value", "false"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual agentDetailsRoute

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request = FakeRequest(POST, trustRegisteredWithUkAddressYesNoRoute)
        .withFormUrlEncodedBody(("value", ""))

      val boundForm = form.bind(Map("value" -> ""))

      val view = application.injector.instanceOf[TrustRegisteredWithUkAddressYesNoView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, NormalMode, fakeDraftId)(request, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, trustRegisteredWithUkAddressYesNoRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, trustRegisteredWithUkAddressYesNoRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual SessionExpiredController.onPageLoad().url

      application.stop()
    }
  }
}
