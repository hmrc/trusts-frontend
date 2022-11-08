/*
 * Copyright 2022 HM Revenue & Customs
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

package controllers.actions

import base.RegistrationSpecBase
import config.FrontendAppConfig
import org.mockito.ArgumentMatchers.any
import play.api.mvc.{Action, AnyContent, DefaultActionBuilder, Results}
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.retrieve.{Retrieval, ~}

import scala.concurrent.Future

class AffinityGroupIdentifierActionSpec extends RegistrationSpecBase {

  type RetrievalType = Option[String] ~ Option[AffinityGroup] ~ Enrolments
  val action: DefaultActionBuilder = app.injector.instanceOf[DefaultActionBuilder]
  val mockAuthConnector: AuthConnector = mock[AuthConnector]
  val appConfig: FrontendAppConfig = injector.instanceOf[FrontendAppConfig]
  val fakeAction: Action[AnyContent] = action { _ => Results.Ok }

  val utr = "0987654321"

  lazy override val trustsAuth = new TrustsAuthorisedFunctions(mockAuthConnector, appConfig)

  private val noEnrollment = Enrolments(Set())

  private def authRetrievals(affinityGroup: AffinityGroup, enrolment: Enrolments): Future[Some[String] ~ Some[AffinityGroup] ~ Enrolments] =
    Future.successful(new ~(new ~(Some("id"), Some(affinityGroup)), enrolment))

  private val agentEnrolment = Enrolments(Set(Enrolment("HMRC-AS-AGENT", List(EnrolmentIdentifier("AgentReferenceNumber", "SomeVal")), "Activated", None)))
  private val emptyAgentEnrolment = Enrolments(Set(Enrolment("HMRC-AS-AGENT", List(EnrolmentIdentifier("AgentReferenceNumber", "")), "Activated", None)))
  private val trustsTaxableEnrolment = Enrolments(Set(Enrolment("HMRC-TERS-ORG", List(EnrolmentIdentifier("SAUTR", utr)), "Activated", None)))
  private val emptyTaxableEnrolment = Enrolments(Set(Enrolment("HMRC-TERS-ORG", List(EnrolmentIdentifier("SAUTR", "")), "Activated", None)))
  private val trustsNonTaxableEnrolment = Enrolments(Set(Enrolment("HMRC-TERSNT-ORG", List(EnrolmentIdentifier("URN", utr)), "Activated", None)))
  private val emptyNonTaxableEnrolment = Enrolments(Set(Enrolment("HMRC-TERSNT-ORG", List(EnrolmentIdentifier("URN", "")), "Activated", None)))

  "invoking an AuthenticatedIdentifier" when {

    "an Agent user" must {
      "redirect the user to the create agent services page" when {
        "hasn't enrolled an Agent Services Account" in {

          val application = applicationBuilder(userAnswers = None).build()

          when(mockAuthConnector.authorise(any(), any[Retrieval[RetrievalType]]())(any(), any()))
            .thenReturn(authRetrievals(AffinityGroup.Agent, noEnrollment))

          val result = new AffinityGroupIdentifierAction(fakeAction, trustsAuth, appConfig, checkForTrustIdentifier = true).apply(fakeRequest)

          status(result) mustBe SEE_OTHER
          redirectLocation(result) mustBe Some(controllers.register.routes.CreateAgentServicesAccountController.onPageLoad().url)
          application.stop()
        }
        "has an empty agent enrolment" in {

          val application = applicationBuilder(userAnswers = None).build()

          when(mockAuthConnector.authorise(any(), any[Retrieval[RetrievalType]]())(any(), any()))
            .thenReturn(authRetrievals(AffinityGroup.Agent, emptyAgentEnrolment))

          val result = new AffinityGroupIdentifierAction(fakeAction, trustsAuth, appConfig, checkForTrustIdentifier = true).apply(fakeRequest)

          status(result) mustBe SEE_OTHER
          redirectLocation(result) mustBe Some(controllers.register.routes.CreateAgentServicesAccountController.onPageLoad().url)
          application.stop()
        }
      }

      "has correctly enrolled an Agent Services Account" must {
        "allow user to continue" in {

          val application = applicationBuilder(userAnswers = None).build()

          when(mockAuthConnector.authorise(any(), any[Retrieval[RetrievalType]]())(any(), any()))
            .thenReturn(authRetrievals(AffinityGroup.Agent, agentEnrolment))

          val result = new AffinityGroupIdentifierAction(fakeAction, trustsAuth, appConfig, checkForTrustIdentifier = true).apply(fakeRequest)

          status(result) mustBe OK
          application.stop()
        }
      }
    }

    "an Org user" when {

      "with no enrolments" must {
        "allow user to continue" in {

          val application = applicationBuilder(userAnswers = None).build()

          when(mockAuthConnector.authorise(any(), any[Retrieval[RetrievalType]]())(any(), any()))
            .thenReturn(authRetrievals(AffinityGroup.Organisation, agentEnrolment))

          val result = new AffinityGroupIdentifierAction(fakeAction, trustsAuth, appConfig, checkForTrustIdentifier = true).apply(fakeRequest)

          status(result) mustBe OK
          application.stop()
        }
      }

      "with taxable trusts enrolments" when {
        "enrolment has an identifier" must {
          "redirect to maintain-a-trust when checkForTrustIdentifier is true" in {

            val application = applicationBuilder(userAnswers = None).build()

            when(mockAuthConnector.authorise(any(), any[Retrieval[RetrievalType]]())(any(), any()))
              .thenReturn(authRetrievals(AffinityGroup.Organisation, trustsTaxableEnrolment))

            val result = new AffinityGroupIdentifierAction(fakeAction, trustsAuth, appConfig, checkForTrustIdentifier = true).apply(fakeRequest)

            status(result) mustBe SEE_OTHER
            redirectLocation(result).value mustBe s"${appConfig.maintainATrustFrontendUrl}"
            application.stop()
          }

          "allow a user to continue when checkForTrustIdentifier is false" in {

            val application = applicationBuilder(userAnswers = None).build()

            when(mockAuthConnector.authorise(any(), any[Retrieval[RetrievalType]]())(any(), any()))
              .thenReturn(authRetrievals(AffinityGroup.Organisation, trustsTaxableEnrolment))

            val result = new AffinityGroupIdentifierAction(fakeAction, trustsAuth, appConfig, checkForTrustIdentifier = false).apply(fakeRequest)

            status(result) mustBe OK
            application.stop()
          }
        }

        "enrolment has no identifier" must {
          "continue without enrolment" in {

            val application = applicationBuilder(userAnswers = None).build()

            when(mockAuthConnector.authorise(any(), any[Retrieval[RetrievalType]]())(any(), any()))
              .thenReturn(authRetrievals(AffinityGroup.Organisation, emptyTaxableEnrolment))

            val result = new AffinityGroupIdentifierAction(fakeAction, trustsAuth, appConfig, checkForTrustIdentifier = true).apply(fakeRequest)

            status(result) mustBe OK
            application.stop()
          }
        }
      }

      "with non-taxable trusts enrolments" when {
        "enrolment has an identifier" must {
          "redirect to maintain-a-trust when checkForTrustIdentifier is true" in {

            val application = applicationBuilder(userAnswers = None).build()

            when(mockAuthConnector.authorise(any(), any[Retrieval[RetrievalType]]())(any(), any()))
              .thenReturn(authRetrievals(AffinityGroup.Organisation, trustsNonTaxableEnrolment))

            val result = new AffinityGroupIdentifierAction(fakeAction, trustsAuth, appConfig, checkForTrustIdentifier = true).apply(fakeRequest)

            status(result) mustBe SEE_OTHER
            redirectLocation(result).value mustBe s"${appConfig.maintainATrustFrontendUrl}"
            application.stop()
          }
          "allow a user to continue when checkForTrustIdentifier is false" in {

            val application = applicationBuilder(userAnswers = None).build()

            when(mockAuthConnector.authorise(any(), any[Retrieval[RetrievalType]]())(any(), any()))
              .thenReturn(authRetrievals(AffinityGroup.Organisation, trustsNonTaxableEnrolment))

            val result = new AffinityGroupIdentifierAction(fakeAction, trustsAuth, appConfig, checkForTrustIdentifier = false).apply(fakeRequest)

            status(result) mustBe OK
            application.stop()
          }
        }
        "enrolment has no identifier" must {
          "continue without enrolment" in {

            val application = applicationBuilder(userAnswers = None).build()

            when(mockAuthConnector.authorise(any(), any[Retrieval[RetrievalType]]())(any(), any()))
              .thenReturn(authRetrievals(AffinityGroup.Organisation, emptyNonTaxableEnrolment))

            val result = new AffinityGroupIdentifierAction(fakeAction, trustsAuth, appConfig, checkForTrustIdentifier = true).apply(fakeRequest)

            status(result) mustBe OK
            application.stop()
          }
        }
      }
    }

    "an Individual user" must {
      "redirect the user to the unauthorised page" in {

        val application = applicationBuilder(userAnswers = None).build()

        when(mockAuthConnector.authorise(any(), any[Retrieval[RetrievalType]]())(any(), any()))
          .thenReturn(authRetrievals(AffinityGroup.Individual, noEnrollment))

        val result = new AffinityGroupIdentifierAction(fakeAction, trustsAuth, appConfig, checkForTrustIdentifier = true).apply(fakeRequest)

        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some(controllers.register.routes.UnauthorisedController.onPageLoad().url)

        application.stop()
      }
    }

    "the user hasn't logged in" must {
      "redirect the user to log in " in {

        val application = applicationBuilder(userAnswers = None).build()

        when(mockAuthConnector.authorise(any(), any[Retrieval[RetrievalType]]())(any(), any())) thenReturn (Future failed MissingBearerToken())

        val result = new AffinityGroupIdentifierAction(fakeAction, trustsAuth, appConfig, checkForTrustIdentifier = true).apply(fakeRequest)

        status(result) mustBe SEE_OTHER

        redirectLocation(result).get must startWith(fakeFrontendAppConfig.loginUrl)
        application.stop()
      }
    }

    "the user's session has expired" must {
      "redirect the user to log in " in {

        val application = applicationBuilder(userAnswers = None).build()

        when(mockAuthConnector.authorise(any(), any[Retrieval[RetrievalType]]())(any(), any())) thenReturn (Future failed BearerTokenExpired())

        val result = new AffinityGroupIdentifierAction(fakeAction, trustsAuth, appConfig, checkForTrustIdentifier = true).apply(fakeRequest)

        status(result) mustBe SEE_OTHER

        redirectLocation(result).get must startWith(fakeFrontendAppConfig.loginUrl)
        application.stop()
      }
    }

    "the user doesn't have sufficient enrolments" must {
      "redirect the user to the unauthorised page" in {

        val application = applicationBuilder(userAnswers = None).build()

        when(mockAuthConnector.authorise(any(), any[Retrieval[RetrievalType]]())(any(), any())) thenReturn (Future failed InsufficientEnrolments())

        val result = new AffinityGroupIdentifierAction(fakeAction, trustsAuth, appConfig, checkForTrustIdentifier = true).apply(fakeRequest)

        status(result) mustBe SEE_OTHER

        redirectLocation(result) mustBe Some(controllers.register.routes.UnauthorisedController.onPageLoad().url)
        application.stop()
      }
    }

    "the user doesn't have sufficient confidence level" must {
      "redirect the user to the unauthorised page" in {

        val application = applicationBuilder(userAnswers = None).build()

        when(mockAuthConnector.authorise(any(), any[Retrieval[RetrievalType]]())(any(), any())) thenReturn (Future failed InsufficientConfidenceLevel())

        val result = new AffinityGroupIdentifierAction(fakeAction, trustsAuth, appConfig, checkForTrustIdentifier = true).apply(fakeRequest)

        status(result) mustBe SEE_OTHER

        redirectLocation(result) mustBe Some(controllers.register.routes.UnauthorisedController.onPageLoad().url)
        application.stop()
      }
    }

    "the user used an unaccepted auth provider" must {
      "redirect the user to the unauthorised page" in {

        val application = applicationBuilder(userAnswers = None).build()

        when(mockAuthConnector.authorise(any(), any[Retrieval[RetrievalType]]())(any(), any())) thenReturn (Future failed UnsupportedAuthProvider())

        val result = new AffinityGroupIdentifierAction(fakeAction, trustsAuth, appConfig, checkForTrustIdentifier = true).apply(fakeRequest)

        status(result) mustBe SEE_OTHER

        redirectLocation(result) mustBe Some(controllers.register.routes.UnauthorisedController.onPageLoad().url)
        application.stop()
      }
    }

    "the user has an unsupported affinity group" must {
      "redirect the user to the unauthorised page" in {

        val application = applicationBuilder(userAnswers = None).build()

        when(mockAuthConnector.authorise(any(), any[Retrieval[RetrievalType]]())(any(), any())) thenReturn (Future failed UnsupportedAffinityGroup())

        val result = new AffinityGroupIdentifierAction(fakeAction, trustsAuth, appConfig, checkForTrustIdentifier = true).apply(fakeRequest)

        status(result) mustBe SEE_OTHER

        redirectLocation(result) mustBe Some(controllers.register.routes.UnauthorisedController.onPageLoad().url)
        application.stop()
      }
    }

    "the user has an unsupported credential role" must {
      "redirect the user to the unauthorised page" in {

        val application = applicationBuilder(userAnswers = None).build()

        when(mockAuthConnector.authorise(any(), any[Retrieval[RetrievalType]]())(any(), any())) thenReturn (Future failed UnsupportedCredentialRole())

        val result = new AffinityGroupIdentifierAction(fakeAction, trustsAuth, appConfig, checkForTrustIdentifier = true).apply(fakeRequest)

        status(result) mustBe SEE_OTHER

        redirectLocation(result) mustBe Some(controllers.register.routes.UnauthorisedController.onPageLoad().url)
        application.stop()
      }
    }
  }
}

