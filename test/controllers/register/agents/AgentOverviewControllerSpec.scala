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

import java.time.LocalDateTime

import base.RegistrationSpecBase
import controllers.register.routes._
import models.NormalMode
import models.core.UserAnswers
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import pages.register.agents.AgentTelephoneNumberPage
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core.AffinityGroup
import viewmodels.DraftRegistration
import views.html.register.agents.AgentOverviewView

import scala.concurrent.Future

class AgentOverviewControllerSpec extends RegistrationSpecBase {

  lazy val agentOverviewRoute: String = routes.AgentOverviewController.onSubmit().url

  "AgentOverview Controller" when {

    "there are no drafts" must {

      "return OK and the correct view for a GET" in {

        when(registrationsRepository.listDrafts()(any())).thenReturn(Future.successful(Nil))

        val application = applicationBuilder(userAnswers = None, AffinityGroup.Agent).build()

        val request = FakeRequest(GET, routes.AgentOverviewController.onPageLoad().url)

        val result = route(application, request).value

        val view = application.injector.instanceOf[AgentOverviewView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(Nil)(request, messages).toString

        application.stop()
      }

      "redirect for a POST" in {

        val application =
          applicationBuilder(userAnswers = None, AffinityGroup.Agent)
            .build()

        val request =
          FakeRequest(POST, agentOverviewRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual CreateDraftRegistrationController.create().url

        application.stop()

      }

    }

    "there are drafts" must {

      "return OK and the correct view for a GET" in {

        val draft = List(DraftRegistration(fakeDraftId, "InternalRef", LocalDateTime.now().toString))

        when(registrationsRepository.listDrafts()(any()))
          .thenReturn(Future.successful(draft))

        val application = applicationBuilder(userAnswers = None, AffinityGroup.Agent).build()

        val request = FakeRequest(GET, routes.AgentOverviewController.onPageLoad().url)

        val result = route(application, request).value

        val view = application.injector.instanceOf[AgentOverviewView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(draft)(request, messages).toString

        application.stop()
      }

      "redirect to registration progress page for a draft with completed agent details" in {

        val telephoneNumber: String = "+441234567890"

        def userAnswers: UserAnswers = emptyUserAnswers.set(AgentTelephoneNumberPage, telephoneNumber).success.value

        val application = applicationBuilder(userAnswers = Some(userAnswers), AffinityGroup.Agent).build()

        val request = FakeRequest(GET, routes.AgentOverviewController.continue(fakeDraftId).url)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual TaskListController.onPageLoad(fakeDraftId).url

        application.stop()

      }

      "redirect to agent internal client reference page for a draft with incomplete agent details" in {

        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers), AffinityGroup.Agent).build()

        val request = FakeRequest(GET, routes.AgentOverviewController.continue(fakeDraftId).url)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual routes.AgentInternalReferenceController.onPageLoad(NormalMode, fakeDraftId).url

        application.stop()

      }

      "redirect to remove draft yes no page when remove selected" in {

        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers), AffinityGroup.Agent).build()

        val request = FakeRequest(GET, routes.AgentOverviewController.remove(fakeDraftId).url)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual routes.RemoveDraftYesNoController.onPageLoad(fakeDraftId).url

        application.stop()
      }
    }
  }
}
