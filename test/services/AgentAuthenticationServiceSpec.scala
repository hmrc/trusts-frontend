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

package services

import base.SpecBase
import config.FrontendAppConfig
import connector.EnrolmentStoreConnector
import controllers.actions.TrustsAuth
import models.EnrolmentStoreResponse.{AlreadyClaimed, NotClaimed}
import models.requests.DataRequest
import org.mockito.Matchers.{any, eq => mEq}
import org.mockito.Mockito.when
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{EitherValues, RecoverMethods}
import play.api.http.HeaderNames
import play.api.inject.bind
import play.api.mvc.AnyContent
import uk.gov.hmrc.auth.core.AffinityGroup.Agent
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.retrieve.{EmptyRetrieval, Retrieval, ~}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class AgentAuthenticationServiceSpec extends SpecBase with ScalaFutures with EitherValues with RecoverMethods {

  val utr = "0987654321"

  val appConfig: FrontendAppConfig = injector.instanceOf[FrontendAppConfig]

  private val agentEnrolment = Enrolment("HMRC-AS-AGENT", List(EnrolmentIdentifier("AgentReferenceNumber", "SomeVal")), "Activated", None)
  private val trustsEnrolment = Enrolment(appConfig.serviceName, List(EnrolmentIdentifier("SAUTR", utr)), "Activated", None)

  val enrolments = Enrolments(Set(
    agentEnrolment,
    trustsEnrolment
  ))

  implicit val hc: HeaderCarrier = HeaderCarrier()
  implicit val dataRequest = DataRequest[AnyContent](fakeRequest, "internalId", emptyUserAnswers, Agent, enrolments)

  val mockAuthConnector: AuthConnector = mock[AuthConnector]
  val mockEnrolmentStoreConnector: EnrolmentStoreConnector = mock[EnrolmentStoreConnector]

  type RetrievalType = Option[String] ~ Option[AffinityGroup] ~ Enrolments

  private def authRetrievals(affinityGroup: AffinityGroup, enrolment: Enrolments) =
    Future.successful(new ~(new ~(Some("id"), Some(affinityGroup)), enrolment))

  lazy override val trustsAuth = new TrustsAuth(mockAuthConnector, appConfig)

  "invoking the IdentifyForPlaybacks action builder" when {
    "passing a non authenticated request" must {
      "redirect to the login page" in {

        val app = applicationBuilder()
          .overrides(bind[TrustsAuth].toInstance(trustsAuth))
          .build()

        when(mockAuthConnector.authorise(any(), any[Retrieval[RetrievalType]]())(any(), any()))
          .thenReturn(Future failed BearerTokenExpired())

        val service = app.injector.instanceOf[AgentAuthenticationService]

        recoverToSucceededIf[BearerTokenExpired](service.authenticate[AnyContent](utr))

      }
    }

    "passing an identifier request" must {
      "execute the body of the action" in {

        when(mockAuthConnector.authorise(any(), any[Retrieval[RetrievalType]]())(any(), any()))
          .thenReturn(authRetrievals(AffinityGroup.Agent, enrolments))

        when(mockAuthConnector.authorise(any[Relationship], mEq(EmptyRetrieval))(any(), any()))
          .thenReturn(Future.successful(()))

        when(mockEnrolmentStoreConnector.checkIfClaimed(mEq(utr))(any(), any()))
          .thenReturn(Future.successful(AlreadyClaimed))

        val app = applicationBuilder()
          .overrides(bind[TrustsAuth].toInstance(trustsAuth))
          .overrides(bind[EnrolmentStoreConnector].toInstance(mockEnrolmentStoreConnector))
          .build()

        val service = app.injector.instanceOf[AgentAuthenticationService]

        whenReady(service.authenticate[AnyContent](utr)) {
          result =>
            result.right.value mustBe dataRequest
        }

      }
    }

    "fetching NotClaimed response from Enrolments" must {
      "redirect to NotClaimed" in {

        when(mockAuthConnector.authorise(any(), any[Retrieval[RetrievalType]]())(any(), any()))
          .thenReturn(authRetrievals(AffinityGroup.Agent, enrolments))

        when(mockAuthConnector.authorise(any[Relationship], mEq(EmptyRetrieval))(any(), any()))
          .thenReturn(Future.successful(()))

        when(mockEnrolmentStoreConnector.checkIfClaimed(mEq(utr))(any[HeaderCarrier], any[ExecutionContext]))
          .thenReturn(Future.successful(NotClaimed))

        val app = applicationBuilder()
          .overrides(bind[TrustsAuth].toInstance(trustsAuth))
          .overrides(bind[EnrolmentStoreConnector].toInstance(mockEnrolmentStoreConnector))
          .build()

        val service = app.injector.instanceOf[AgentAuthenticationService]

        whenReady(service.authenticate[AnyContent](utr)) {
          result =>
            result.left.value.header.headers(HeaderNames.LOCATION) mustBe controllers.playback.routes.TrustNotClaimedController.onPageLoad().url
        }

      }
    }

    "an agent that does not have a trusts enrolment" must {
      "redirect to agent not authorised" in {

        val enrolments = Enrolments(Set(agentEnrolment))

        when(mockAuthConnector.authorise(any(), any[Retrieval[RetrievalType]]())(any(), any()))
          .thenReturn(authRetrievals(AffinityGroup.Agent, enrolments))

        when(mockAuthConnector.authorise(any[Relationship], mEq(EmptyRetrieval))(any(), any()))
          .thenReturn(Future.successful(()))

        when(mockEnrolmentStoreConnector.checkIfClaimed(mEq(utr))(any(), any()))
          .thenReturn(Future.successful(AlreadyClaimed))

        val app = applicationBuilder()
          .overrides(bind[TrustsAuth].toInstance(trustsAuth))
          .overrides(bind[EnrolmentStoreConnector].toInstance(mockEnrolmentStoreConnector))
          .build()

        val service = app.injector.instanceOf[AgentAuthenticationService]

        implicit val dataRequest = DataRequest[AnyContent](fakeRequest, "internalId", emptyUserAnswers, Agent, enrolments)

        whenReady(service.authenticate[AnyContent](utr)) {
          result =>
            result.left.value.header.headers(HeaderNames.LOCATION) mustBe controllers.playback.routes.AgentNotAuthorisedController.onPageLoad().url
        }

      }
    }

    "an agent that has a trusts enrolment without matching submitted utr" must {
      "redirect to agent not authorised" in {

        val enrolments = Enrolments(Set(
          agentEnrolment,
          Enrolment(appConfig.serviceName, List(EnrolmentIdentifier("SAUTR", "1234567890")), "Activated", None)
        ))

        when(mockAuthConnector.authorise(any(), any[Retrieval[RetrievalType]]())(any(), any()))
          .thenReturn(authRetrievals(AffinityGroup.Agent, enrolments))

        when(mockAuthConnector.authorise(any[Relationship], mEq(EmptyRetrieval))(any(), any()))
          .thenReturn(Future.successful(()))

        when(mockEnrolmentStoreConnector.checkIfClaimed(mEq(utr))(any(), any()))
          .thenReturn(Future.successful(AlreadyClaimed))

        val app = applicationBuilder()
          .overrides(bind[TrustsAuth].toInstance(trustsAuth))
          .overrides(bind[EnrolmentStoreConnector].toInstance(mockEnrolmentStoreConnector))
          .build()

        val service = app.injector.instanceOf[AgentAuthenticationService]

        implicit val dataRequest = DataRequest[AnyContent](fakeRequest, "internalId", emptyUserAnswers, Agent, enrolments)

        whenReady(service.authenticate[AnyContent](utr)) {
          result =>
            result.left.value.header.headers(HeaderNames.LOCATION) mustBe controllers.playback.routes.AgentNotAuthorisedController.onPageLoad().url
        }

      }
    }

  }

}
