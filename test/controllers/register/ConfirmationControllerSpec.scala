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

package controllers.register

import base.RegistrationSpecBase
import models.NormalMode
import models.core.pages.FullName
import models.registration.pages.RegistrationStatus
import pages.register.{RegistrationTRNPage, TrustHaveAUTRPage}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core.AffinityGroup
import utils.TestUserAnswers
import views.html.register.{ConfirmationAgentView, ConfirmationExistingView, ConfirmationIndividualView}

class ConfirmationControllerSpec extends RegistrationSpecBase {

  val postHMRC = "https://www.gov.uk/government/organisations/hm-revenue-customs/contact/trusts"

  def agentUrl = controllers.register.agents.routes.AgentOverviewController.onPageLoad().url

  "Confirmation Controller" must {

    "return OK and the correct view for a GET when TRN is available" when {
      "registering trust" when {
        "agent" in {

          val userAnswers = TestUserAnswers.withLeadTrustee(
            emptyUserAnswers.copy(progress = RegistrationStatus.Complete)
              .set(RegistrationTRNPage, "xTRN1234678").success.value
              .set(TrustHaveAUTRPage, false).success.value
          )

          val application = applicationBuilder(userAnswers = Some(userAnswers), affinityGroup = AffinityGroup.Agent).build()

          val request = FakeRequest(GET, routes.ConfirmationController.onPageLoad(fakeDraftId).url)

          val result = route(application, request).value

          val view = application.injector.instanceOf[ConfirmationAgentView]

          val content = contentAsString(result)

          status(result) mustEqual OK

          content mustEqual
            view(draftId = fakeDraftId, "xTRN1234678")(fakeRequest, messages).toString

          content must include(agentUrl)

          application.stop()
        }
        "org" in {

          val userAnswers = TestUserAnswers.withLeadTrustee(
            emptyUserAnswers.copy(progress = RegistrationStatus.Complete)
              .set(RegistrationTRNPage, "xTRN1234678").success.value
              .set(TrustHaveAUTRPage, false).success.value
          )

          val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

          val request = FakeRequest(GET, routes.ConfirmationController.onPageLoad(fakeDraftId).url)

          val result = route(application, request).value

          val view = application.injector.instanceOf[ConfirmationIndividualView]

          val content = contentAsString(result)

          status(result) mustEqual OK

          content mustEqual
            view(draftId = fakeDraftId, "xTRN1234678")(fakeRequest, messages).toString

          content mustNot include(agentUrl)

          application.stop()
        }
      }
      "maintaining trust" in {

        val userAnswers = TestUserAnswers.withLeadTrustee(
          emptyUserAnswers.copy(progress = RegistrationStatus.Complete)
            .set(RegistrationTRNPage, "xTRN1234678").success.value
            .set(TrustHaveAUTRPage, false).success.value
        )

        val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

        val request = FakeRequest(GET, routes.ConfirmationController.onPageLoad(fakeDraftId).url)

        val result = route(application, request).value

        val view = application.injector.instanceOf[ConfirmationExistingView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(draftId = fakeDraftId, false, "", FullName("first name", None, "Last Name"))(fakeRequest, messages).toString

        application.stop()
      }
    }

    "return to Task List view  when registration is in progress " in {

      val userAnswers = emptyUserAnswers.copy(progress = RegistrationStatus.InProgress)

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, routes.ConfirmationController.onPageLoad(fakeDraftId).url)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.TaskListController.onPageLoad(fakeDraftId).url

      application.stop()
    }

    "return to trust registered online page when registration is not started. " in {

      val userAnswers = emptyUserAnswers.copy(progress = RegistrationStatus.NotStarted)

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, routes.ConfirmationController.onPageLoad(fakeDraftId).url)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.TrustRegisteredOnlineController.onPageLoad(NormalMode,fakeDraftId).url

      application.stop()
    }

    "return InternalServerError when TRN is not available" in {

      val userAnswers = emptyUserAnswers.copy(progress = RegistrationStatus.Complete)

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, routes.ConfirmationController.onPageLoad(fakeDraftId).url)

      val result = route(application, request).value

      status(result) mustEqual INTERNAL_SERVER_ERROR

      application.stop()
    }

  }
}
