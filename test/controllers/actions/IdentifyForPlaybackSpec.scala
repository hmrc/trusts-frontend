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

package controllers.actions

import base.SpecBase
import config.FrontendAppConfig
import connector.EnrolmentStoreConnector
import controllers.playback.routes
import models.AgentTrustsResponse.NotClaimed
import models.requests.IdentifierRequest
import org.mockito.Matchers.{any, eq => mEq}
import org.mockito.Mockito._
import play.api.inject.bind
import play.api.mvc.{Action, AnyContent, Results}
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core.AffinityGroup.Agent
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.retrieve.{EmptyRetrieval, Retrieval, ~}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class IdentifyForPlaybackSpec extends SpecBase {

  type RetrievalType = Option[String] ~ Option[AffinityGroup] ~ Enrolments

  val mockAuthConnector: AuthConnector = mock[AuthConnector]
  val appConfig: FrontendAppConfig = injector.instanceOf[FrontendAppConfig]

  lazy override val trustsAuth = new TrustsAuth(mockAuthConnector, appConfig)

  private val noEnrollment = Enrolments(Set())

  private def authRetrievals(affinityGroup: AffinityGroup, enrolment: Enrolments) =
    Future.successful(new ~(new ~(Some("id"), Some(affinityGroup)), enrolment))

  private val agentEnrolment = Enrolments(Set(Enrolment("HMRC-AS-AGENT", List(EnrolmentIdentifier("AgentReferenceNumber", "SomeVal")), "Activated", None)))


  "invoking the IdentifyForPlaybacks action builder" when {
    "passing a non authenticated request" must {
      "redirect to the login page" in {

        val identify: IdentifierAction = injector.instanceOf[IdentifierAction]
        val application = applicationBuilder(userAnswers = None).build()

        def fakeAction: Action[AnyContent] = identify { _ => Results.Ok }

        when(mockAuthConnector.authorise(any(), any[Retrieval[RetrievalType]]())(any(), any()))
          .thenReturn(Future failed BearerTokenExpired())

        val result = fakeAction.apply(fakeRequest)

        status(result) mustBe SEE_OTHER

        application.stop()
      }
    }

    "passing an identifier request" must {
      "execute the body of the action" in {

        val identify: IdentifierAction = injector.instanceOf[IdentifierAction]
        val fakeAction: Action[AnyContent] = identify { _ => Results.Ok }

        val application = applicationBuilder(userAnswers = None).build()

        val idRequest = IdentifierRequest(fakeRequest, "id", AffinityGroup.Agent, Enrolments(Set.empty[Enrolment]))

        when(mockAuthConnector.authorise(any(), any[Retrieval[RetrievalType]]())(any(), any()))
          .thenReturn(authRetrievals(AffinityGroup.Agent, agentEnrolment))

        when(mockAuthConnector.authorise(any[Relationship], mEq(EmptyRetrieval))(any(), any()))
          .thenReturn(Future.successful(()))

        val result = fakeAction.apply(idRequest)

        status(result) mustBe OK

        application.stop()
      }
    }

    "fetching NotClaimed response from Enrolments" must {
      "redirect to NotClaimed" in {

        val enrolmentStoreConnector = mock[EnrolmentStoreConnector]

        val identify: IdentifierAction = injector.instanceOf[IdentifierAction]
        val fakeAction: Action[AnyContent] = identify { _ => Results.Ok }

        val application = applicationBuilder(userAnswers = None, affinityGroup = Agent)
          .overrides(bind[EnrolmentStoreConnector].toInstance(enrolmentStoreConnector))
          .build()

        val idRequest = IdentifierRequest(fakeRequest, "id", AffinityGroup.Agent, Enrolments(Set.empty[Enrolment]))

        when(enrolmentStoreConnector.getAgentTrusts(any[String])(any[HeaderCarrier], any[ExecutionContext]))
          .thenReturn(Future.successful(NotClaimed))

        val result = fakeAction.apply(idRequest)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.TrustNotClaimedController.onPageLoad().url

        application.stop()
      }
    }


    "an agent that does not have a trusts enrolment" must {
      "redirect to agent not authorised" in {

        val utr = "0987654321"

        val enrolmentStoreConnector = mock[EnrolmentStoreConnector]

        val identify: IdentifierAction = injector.instanceOf[IdentifierAction]
        val fakeAction: Action[AnyContent] = identify { _ => Results.Ok }

        val application = applicationBuilder(userAnswers = None, affinityGroup = Agent)
          .overrides(bind[EnrolmentStoreConnector].toInstance(enrolmentStoreConnector))
          .build()

        val idRequest = IdentifierRequest(fakeRequest, "id", AffinityGroup.Agent, Enrolments(Set.empty[Enrolment]))

        when(enrolmentStoreConnector.getAgentTrusts(any[String])(any[HeaderCarrier], any[ExecutionContext]))
          .thenReturn(Future.successful(NotClaimed))

        val result = fakeAction.apply(idRequest)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustBe routes.AgentNotAuthorisedController.onPageLoad().url

        application.stop()
      }
    }

    "an agent that has a trusts enrolment without matching submitted utr" must {
      "redirect to agent not authorised" in {

        val utr = "0987654321"

        val enrolmentStoreConnector = mock[EnrolmentStoreConnector]

        val identify: IdentifierAction = injector.instanceOf[IdentifierAction]
        val fakeAction: Action[AnyContent] = identify { _ => Results.Ok }

        val application = applicationBuilder(userAnswers = None, affinityGroup = Agent)
          .overrides(bind[EnrolmentStoreConnector].toInstance(enrolmentStoreConnector))
          .build()

        val idRequest = IdentifierRequest(fakeRequest, "id", AffinityGroup.Agent, Enrolments(Set.empty[Enrolment]))

        when(enrolmentStoreConnector.getAgentTrusts(any[String])(any[HeaderCarrier], any[ExecutionContext]))
          .thenReturn(Future.successful(NotClaimed))

        val result = fakeAction.apply(idRequest)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustBe controllers.playback.routes.AgentNotAuthorisedController.onPageLoad().url

        application.stop()
      }
    }

  }
}
