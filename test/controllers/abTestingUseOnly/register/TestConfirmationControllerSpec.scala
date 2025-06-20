/*
 * Copyright 2024 HM Revenue & Customs
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

package controllers.abTestingUseOnly.register

import base.RegistrationSpecBase
import controllers.abTestingUseOnly.routes.TestSignOutController
import models.core.http.{IdentificationType, LeadTrusteeIndType, LeadTrusteeType}
import models.core.pages.FullName
import models.registration.pages.RegistrationStatus
import org.mockito.ArgumentMatchers.any
import pages.register.suitability.TrustTaxableYesNoPage
import pages.register.{RegistrationTRNPage, TrustHaveAUTRPage}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core.AffinityGroup
import views.html.abTestingUseOnly.register.confirmation._
import views.html.register.confirmation._
import org.mockito.Mockito.when

import java.time.LocalDate
import scala.concurrent.Future

class TestConfirmationControllerSpec extends RegistrationSpecBase {

  private def agentUrl: String = controllers.register.agents.routes.AgentOverviewController.onPageLoad().url

  private val leadTrusteeInd = LeadTrusteeType(
    Some(LeadTrusteeIndType(
      FullName("first name", Some("middle name"), "Last Name"),
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

  "TestConfirmationController" must {

    "return OK and the correct view for a GET when TRN is available" when {

      "registering new trust" when {

        "agent" when {

          "lead trustee individual" in {
            val userAnswers = emptyUserAnswers.copy(progress = RegistrationStatus.Complete)
              .set(RegistrationTRNPage, "xTRN1234678").success.value
              .set(TrustTaxableYesNoPage, true).success.value
              .set(TrustHaveAUTRPage, false).success.value

            when(registrationsRepository.getLeadTrustee(any())(any())).thenReturn(Future.successful(leadTrusteeInd))

            val application = applicationBuilder(userAnswers = Some(userAnswers), affinityGroup = AffinityGroup.Agent).build()

            val request = FakeRequest(GET, routes.TestConfirmationController.onPageLoad(fakeDraftId).url)

            val result = route(application, request).value

            val view = application.injector.instanceOf[newTrust.taxable.AgentView]

            val content = contentAsString(result)

            status(result) mustEqual OK

            content mustEqual
              view(draftId = fakeDraftId, "xTRN1234678", "first name Last Name")(request, messages).toString

            content must include(agentUrl)

            application.stop()
          }

          "lead trustee organisation" in {
            val userAnswers = emptyUserAnswers.copy(progress = RegistrationStatus.Complete)
              .set(RegistrationTRNPage, "xTRN1234678").success.value
              .set(TrustTaxableYesNoPage, true).success.value
              .set(TrustHaveAUTRPage, false).success.value

            when(registrationsRepository.getLeadTrustee(any())(any())).thenReturn(Future.successful(testLeadTrusteeOrg))

            val application = applicationBuilder(userAnswers = Some(userAnswers), affinityGroup = AffinityGroup.Agent).build()

            val request = FakeRequest(GET, routes.TestConfirmationController.onPageLoad(fakeDraftId).url)

            val result = route(application, request).value

            val view = application.injector.instanceOf[newTrust.taxable.AgentView]

            val content = contentAsString(result)

            status(result) mustEqual OK

            content mustEqual
              view(draftId = fakeDraftId, "xTRN1234678", "Lead Org")(request, messages).toString

            content must include(agentUrl)

            application.stop()
          }

        }

        "org" when {

          "lead trustee individual" in {
            val userAnswers = emptyUserAnswers.copy(progress = RegistrationStatus.Complete)
              .set(RegistrationTRNPage, "xTRN1234678").success.value
              .set(TrustTaxableYesNoPage, true).success.value
              .set(TrustHaveAUTRPage, false).success.value

            when(registrationsRepository.getLeadTrustee(any())(any())).thenReturn(Future.successful(leadTrusteeInd))

            val application = applicationBuilder(userAnswers = Some(userAnswers), affinityGroup = AffinityGroup.Organisation).build()

            val request = FakeRequest(GET, routes.TestConfirmationController.onPageLoad(fakeDraftId).url)

            val result = route(application, request).value

            val view = application.injector.instanceOf[newTrust.taxable.IndividualView]

            val content = contentAsString(result)

            status(result) mustEqual OK

            content mustEqual
              view(draftId = fakeDraftId, "xTRN1234678", "first name Last Name")(request, messages).toString

            content mustNot include(agentUrl)

            application.stop()
          }

          "lead trustee organisation" in {
            val userAnswers = emptyUserAnswers.copy(progress = RegistrationStatus.Complete)
              .set(RegistrationTRNPage, "xTRN1234678").success.value
              .set(TrustTaxableYesNoPage, true).success.value
              .set(TrustHaveAUTRPage, false).success.value

            when(registrationsRepository.getLeadTrustee(any())(any())).thenReturn(Future.successful(testLeadTrusteeOrg))

            val application = applicationBuilder(userAnswers = Some(userAnswers), affinityGroup = AffinityGroup.Organisation).build()

            val request = FakeRequest(GET, routes.TestConfirmationController.onPageLoad(fakeDraftId).url)

            val result = route(application, request).value

            val view = application.injector.instanceOf[newTrust.taxable.IndividualView]

            val content = contentAsString(result)

            status(result) mustEqual OK

            content mustEqual
              view(draftId = fakeDraftId, "xTRN1234678", "Lead Org")(request, messages).toString

            content mustNot include(agentUrl)

            application.stop()
          }

        }

        "render TestIndividualView (non-taxable organisation user type)" in {

          val userAnswers = emptyUserAnswers.copy(progress = RegistrationStatus.Complete)
            .set(RegistrationTRNPage, "xTRN1234678").success.value
            .set(TrustTaxableYesNoPage, false).success.value
            .set(TrustHaveAUTRPage, false).success.value

          when(registrationsRepository.getLeadTrustee(any())(any())).thenReturn(Future.successful(testLeadTrusteeOrg))

          val application = applicationBuilder(userAnswers = Some(userAnswers), affinityGroup = AffinityGroup.Organisation).build()

          val request = FakeRequest(GET, routes.TestConfirmationController.onPageLoad(fakeDraftId).url)

          val result = route(application, request).value

          val view = application.injector.instanceOf[nonTaxable.TestIndividualView]

          val content = contentAsString(result)

          status(result) mustEqual OK

          content mustEqual
            view(fakeDraftId, "xTRN1234678")(request, messages).toString

          content mustNot include(agentUrl)

          application.stop()
        }

        "render nonTaxableAgentView" in {

          val userAnswers = emptyUserAnswers.copy(progress = RegistrationStatus.Complete)
            .set(RegistrationTRNPage, "xTRN1234678").success.value
            .set(TrustTaxableYesNoPage, false).success.value
            .set(TrustHaveAUTRPage, false).success.value

          when(registrationsRepository.getLeadTrustee(any())(any())).thenReturn(Future.successful(testLeadTrusteeOrg))

          val application = applicationBuilder(userAnswers = Some(userAnswers), affinityGroup = AffinityGroup.Agent).build()

          val request = FakeRequest(GET, routes.TestConfirmationController.onPageLoad(fakeDraftId).url)

          val result = route(application, request).value

          val view = application.injector.instanceOf[newTrust.nonTaxable.AgentView]

          val content = contentAsString(result)

          status(result) mustEqual OK

          content mustEqual
            view(fakeDraftId, "xTRN1234678", "Lead Org")(request, messages).toString

          content mustNot include(agentUrl)

          application.stop()
        }
      }

      "registering existing trust" when {

        "agent" when {

          "lead trustee individual" in {
            val userAnswers = emptyUserAnswers.copy(progress = RegistrationStatus.Complete)
              .set(RegistrationTRNPage, "xTRN1234678").success.value
              .set(TrustTaxableYesNoPage, true).success.value
              .set(TrustHaveAUTRPage, true).success.value

            when(registrationsRepository.getLeadTrustee(any())(any())).thenReturn(Future.successful(leadTrusteeInd))

            val application = applicationBuilder(userAnswers = Some(userAnswers), affinityGroup = AffinityGroup.Agent).build()

            val request = FakeRequest(GET, routes.TestConfirmationController.onPageLoad(fakeDraftId).url)

            val result = route(application, request).value

            val view = application.injector.instanceOf[existingTrust.AgentView]

            status(result) mustEqual OK

            contentAsString(result) mustEqual
              view(draftId = fakeDraftId, "xTRN1234678", "first name Last Name")(request, messages).toString

            application.stop()
          }

          "lead trustee organisation" in {
            val userAnswers = emptyUserAnswers.copy(progress = RegistrationStatus.Complete)
              .set(RegistrationTRNPage, "xTRN1234678").success.value
              .set(TrustTaxableYesNoPage, true).success.value
              .set(TrustHaveAUTRPage, true).success.value

            when(registrationsRepository.getLeadTrustee(any())(any())).thenReturn(Future.successful(testLeadTrusteeOrg))

            val application = applicationBuilder(userAnswers = Some(userAnswers), affinityGroup = AffinityGroup.Agent).build()

            val request = FakeRequest(GET, routes.TestConfirmationController.onPageLoad(fakeDraftId).url)

            val result = route(application, request).value

            val view = application.injector.instanceOf[existingTrust.AgentView]

            status(result) mustEqual OK

            contentAsString(result) mustEqual
              view(draftId = fakeDraftId, "xTRN1234678", "Lead Org")(request, messages).toString

            application.stop()
          }
        }

        "org" when {

          "lead trustee individual" in {
            val userAnswers = emptyUserAnswers.copy(progress = RegistrationStatus.Complete)
              .set(RegistrationTRNPage, "xTRN1234678").success.value
              .set(TrustTaxableYesNoPage, true).success.value
              .set(TrustHaveAUTRPage, true).success.value

            when(registrationsRepository.getLeadTrustee(any())(any())).thenReturn(Future.successful(leadTrusteeInd))

            val application = applicationBuilder(userAnswers = Some(userAnswers), affinityGroup = AffinityGroup.Organisation).build()

            val request = FakeRequest(GET, routes.TestConfirmationController.onPageLoad(fakeDraftId).url)

            val result = route(application, request).value

            val view = application.injector.instanceOf[existingTrust.IndividualView]

            status(result) mustEqual OK

            contentAsString(result) mustEqual
              view(draftId = fakeDraftId, "xTRN1234678", "first name Last Name")(request, messages).toString

            application.stop()
          }

          "lead trustee organisation" in {
            val userAnswers = emptyUserAnswers.copy(progress = RegistrationStatus.Complete)
              .set(RegistrationTRNPage, "xTRN1234678").success.value
              .set(TrustTaxableYesNoPage, true).success.value
              .set(TrustHaveAUTRPage, true).success.value

            when(registrationsRepository.getLeadTrustee(any())(any())).thenReturn(Future.successful(testLeadTrusteeOrg))

            val application = applicationBuilder(userAnswers = Some(userAnswers), affinityGroup = AffinityGroup.Organisation).build()

            val request = FakeRequest(GET, routes.TestConfirmationController.onPageLoad(fakeDraftId).url)

            val result = route(application, request).value

            val view = application.injector.instanceOf[existingTrust.IndividualView]

            status(result) mustEqual OK

            contentAsString(result) mustEqual
              view(draftId = fakeDraftId, "xTRN1234678", "Lead Org")(request, messages).toString

            application.stop()
          }
        }

      }
    }

    "redirect to TestSignOutController for POST when TRN is available (non-taxable organisation user type)" in {

      val userAnswers = emptyUserAnswers.copy(progress = RegistrationStatus.Complete)
        .set(RegistrationTRNPage, "xTRN1234678").success.value
        .set(TrustTaxableYesNoPage, false).success.value
        .set(TrustHaveAUTRPage, false).success.value

      when(registrationsRepository.getLeadTrustee(any())(any())).thenReturn(Future.successful(testLeadTrusteeOrg))

      val application = applicationBuilder(userAnswers = Some(userAnswers), affinityGroup = AffinityGroup.Organisation).build()

      val request = FakeRequest(POST, routes.TestConfirmationController.onSubmit(fakeDraftId).url)

      val result = route(application, request).value

      val content = contentAsString(result)

      status(result) mustEqual SEE_OTHER

      redirectLocation(result) mustBe Some(TestSignOutController.onPageLoad().url)

      content mustNot include(agentUrl)

      application.stop()
    }

    Seq(GET, POST).foreach { requestMethod =>

      s"return to Task List view  when registration is in progress for $requestMethod method" in {

        val userAnswers = emptyUserAnswers.copy(progress = RegistrationStatus.InProgress)

        val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

        val request = FakeRequest(requestMethod, routes.TestConfirmationController.onPageLoad(fakeDraftId).url)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.register.routes.TaskListController.onPageLoad(fakeDraftId).url

        application.stop()
      }


      s"return to trust registered online page when registration is not started for $requestMethod method" in {

        val userAnswers = emptyUserAnswers.copy(progress = RegistrationStatus.NotStarted)

        val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

        val request = FakeRequest(requestMethod, routes.TestConfirmationController.onPageLoad(fakeDraftId).url)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.register.routes.TrustRegisteredOnlineController.onPageLoad().url

        application.stop()
      }

      s"return InternalServerError when TRN is not available for $requestMethod method" in {

        val userAnswers = emptyUserAnswers.copy(progress = RegistrationStatus.Complete)

        val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

        val request = FakeRequest(requestMethod, routes.TestConfirmationController.onPageLoad(fakeDraftId).url)

        val result = route(application, request).value

        status(result) mustEqual INTERNAL_SERVER_ERROR

        application.stop()
      }

    }
  }

}
