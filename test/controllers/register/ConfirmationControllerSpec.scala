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

import java.time.LocalDate

import base.RegistrationSpecBase
import mapping.registration.{IdentificationType, LeadTrusteeIndType, LeadTrusteeType, NameType}
import models.NormalMode
import models.registration.pages.RegistrationStatus
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import pages.register.{RegistrationTRNPage, TrustHaveAUTRPage}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core.AffinityGroup
import views.html.register.{ConfirmationAgentView, ConfirmationExistingView, ConfirmationIndividualView}

import scala.concurrent.Future

class ConfirmationControllerSpec extends RegistrationSpecBase {

  private val postHMRC = "https://www.gov.uk/government/organisations/hm-revenue-customs/contact/trusts"

  private def agentUrl = controllers.register.agents.routes.AgentOverviewController.onPageLoad().url

  private val leadTrusteeInd = LeadTrusteeType(
    Some(LeadTrusteeIndType(
      NameType("first name", Some("middle name"), "Last Name"),
      LocalDate.of(1500, 10, 10),
      "0191 1111111",
      None,
      IdentificationType(
        Some("AB123456C"),
        None,
        None
      )
    )),
    None
  )

  "Confirmation Controller" must {

    "return OK and the correct view for a GET when TRN is available" when {
      "registering trust" when {
        "agent" when {

          "lead trustee individual" in {
            val userAnswers = emptyUserAnswers.copy(progress = RegistrationStatus.Complete)
                .set(RegistrationTRNPage, "xTRN1234678").success.value
                .set(TrustHaveAUTRPage, false).success.value

            when(registrationsRepository.getLeadTrustee(any())(any())).thenReturn(Future.successful(leadTrusteeInd))

            val application = applicationBuilder(userAnswers = Some(userAnswers), affinityGroup = AffinityGroup.Agent).build()

            val request = FakeRequest(GET, routes.ConfirmationController.onPageLoad(fakeDraftId).url)

            val result = route(application, request).value

            val view = application.injector.instanceOf[ConfirmationAgentView]

            val content = contentAsString(result)

            status(result) mustEqual OK

            content mustEqual
              view(draftId = fakeDraftId, "xTRN1234678", "first name Last Name")(fakeRequest, messages).toString

            content must include(agentUrl)

            application.stop()
          }

          "lead trustee organisation" in {
            val userAnswers = emptyUserAnswers.copy(progress = RegistrationStatus.Complete)
                .set(RegistrationTRNPage, "xTRN1234678").success.value
                .set(TrustHaveAUTRPage, false).success.value

            when(registrationsRepository.getLeadTrustee(any())(any())).thenReturn(Future.successful(testLeadTrusteeOrg))

            val application = applicationBuilder(userAnswers = Some(userAnswers), affinityGroup = AffinityGroup.Agent).build()

            val request = FakeRequest(GET, routes.ConfirmationController.onPageLoad(fakeDraftId).url)

            val result = route(application, request).value

            val view = application.injector.instanceOf[ConfirmationAgentView]

            val content = contentAsString(result)

            status(result) mustEqual OK

            content mustEqual
              view(draftId = fakeDraftId, "xTRN1234678", "Lead Org")(fakeRequest, messages).toString

            content must include(agentUrl)

            application.stop()
          }

        }

        "org" when {

          "lead trustee individual" in {
            val userAnswers = emptyUserAnswers.copy(progress = RegistrationStatus.Complete)
                .set(RegistrationTRNPage, "xTRN1234678").success.value
                .set(TrustHaveAUTRPage, false).success.value

            when(registrationsRepository.getLeadTrustee(any())(any())).thenReturn(Future.successful(leadTrusteeInd))

            val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

            val request = FakeRequest(GET, routes.ConfirmationController.onPageLoad(fakeDraftId).url)

            val result = route(application, request).value

            val view = application.injector.instanceOf[ConfirmationIndividualView]

            val content = contentAsString(result)

            status(result) mustEqual OK

            content mustEqual
              view(draftId = fakeDraftId, "xTRN1234678", "first name Last Name")(fakeRequest, messages).toString

            content mustNot include(agentUrl)

            application.stop()
          }

          "lead trustee organisation" in {
            val userAnswers = emptyUserAnswers.copy(progress = RegistrationStatus.Complete)
                .set(RegistrationTRNPage, "xTRN1234678").success.value
                .set(TrustHaveAUTRPage, false).success.value

            when(registrationsRepository.getLeadTrustee(any())(any())).thenReturn(Future.successful(testLeadTrusteeOrg))

            val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

            val request = FakeRequest(GET, routes.ConfirmationController.onPageLoad(fakeDraftId).url)

            val result = route(application, request).value

            val view = application.injector.instanceOf[ConfirmationIndividualView]

            val content = contentAsString(result)

            status(result) mustEqual OK

            content mustEqual
              view(draftId = fakeDraftId, "xTRN1234678", "Lead Org")(fakeRequest, messages).toString

            content mustNot include(agentUrl)

            application.stop()
          }

        }
      }

      "maintaining trust" when {

        "lead trustee individual" in {
          val userAnswers = emptyUserAnswers.copy(progress = RegistrationStatus.Complete)
              .set(RegistrationTRNPage, "xTRN1234678").success.value
              .set(TrustHaveAUTRPage, true).success.value

          when(registrationsRepository.getLeadTrustee(any())(any())).thenReturn(Future.successful(leadTrusteeInd))

          val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

          val request = FakeRequest(GET, routes.ConfirmationController.onPageLoad(fakeDraftId).url)

          val result = route(application, request).value

          val view = application.injector.instanceOf[ConfirmationExistingView]

          status(result) mustEqual OK

          contentAsString(result) mustEqual
            view(draftId = fakeDraftId, false, "xTRN1234678", "first name Last Name")(fakeRequest, messages).toString

          application.stop()
        }

        "lead trustee organisation" in {
          val userAnswers = emptyUserAnswers.copy(progress = RegistrationStatus.Complete)
              .set(RegistrationTRNPage, "xTRN1234678").success.value
              .set(TrustHaveAUTRPage, true).success.value

          when(registrationsRepository.getLeadTrustee(any())(any())).thenReturn(Future.successful(testLeadTrusteeOrg))

          val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

          val request = FakeRequest(GET, routes.ConfirmationController.onPageLoad(fakeDraftId).url)

          val result = route(application, request).value

          val view = application.injector.instanceOf[ConfirmationExistingView]

          status(result) mustEqual OK

          contentAsString(result) mustEqual
            view(draftId = fakeDraftId, false, "xTRN1234678", "Lead Org")(fakeRequest, messages).toString

          application.stop()
        }

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
