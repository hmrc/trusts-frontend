/*
 * Copyright 2018 HM Revenue & Customs
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

import play.api.mvc.Controller
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.authorise.Predicate
import uk.gov.hmrc.auth.core.retrieve.{Retrieval, ~}
import base.SpecBase
import controllers.routes
import org.scalatest.mockito.MockitoSugar
import uk.gov.hmrc.http.HeaderCarrier
import org.mockito.{Matchers, Mockito}
import org.mockito.Matchers.eq
import org.mockito.Matchers.any
import uk.gov.hmrc.auth.core.AffinityGroup.{Agent, Organisation}

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import uk.gov.hmrc.auth.core.authorise.Predicate

class AuthActionSpec extends SpecBase with MockitoSugar {

  class Harness(authAction: IdentifierAction) extends Controller {
    def onPageLoad() = authAction { request => Ok }
  }


  "Auth Action" when {

    "the user has an Organisation affinity group" must {
       "allow user to enter service" in {
         val mockAuthConnector: AuthConnector = mockAuthSuccessRetrieval
         val authAction = new AuthenticatedIdentifierAction(mockAuthConnector, frontendAppConfig)
         val controller = new Harness(authAction)
         val result = controller.onPageLoad()(fakeRequest)
         status(result) mustBe OK
       }
    }

    "the user has an Agent affinity group" must {
      "allow user to enter service" in {
        val mockAuthConnector: AuthConnector = mockAuthSuccessRetrieval

        val authAction = new AuthenticatedIdentifierAction(mockAuthConnector, frontendAppConfig)
        val controller = new Harness(authAction)
        val result = controller.onPageLoad()(fakeRequest)
        status(result) mustBe OK
      }
    }


    "the user hasn't logged in" must {
      "redirect the user to log in " in {
        val authAction = new AuthenticatedIdentifierAction(new FakeFailingAuthConnector(new MissingBearerToken), frontendAppConfig)
        val controller = new Harness(authAction)
        val result = controller.onPageLoad()(fakeRequest)
        status(result) mustBe SEE_OTHER
        redirectLocation(result).get must startWith(frontendAppConfig.loginUrl)
      }
    }

    "the user's session has expired" must {
      "redirect the user to log in " in {
        val authAction = new AuthenticatedIdentifierAction(new FakeFailingAuthConnector(new BearerTokenExpired), frontendAppConfig)
        val controller = new Harness(authAction)
        val result = controller.onPageLoad()(fakeRequest)
        status(result) mustBe SEE_OTHER
        redirectLocation(result).get must startWith(frontendAppConfig.loginUrl)
      }
    }

    "the user doesn't have sufficient enrolments" must {
      "redirect the user to the unauthorised page" in {
        val authAction = new AuthenticatedIdentifierAction(new FakeFailingAuthConnector(new InsufficientEnrolments), frontendAppConfig)
        val controller = new Harness(authAction)
        val result = controller.onPageLoad()(fakeRequest)
        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some(routes.UnauthorisedController.onPageLoad().url)
      }
    }

    "the user doesn't have sufficient confidence level" must {
      "redirect the user to the unauthorised page" in {
        val authAction = new AuthenticatedIdentifierAction(new FakeFailingAuthConnector(new InsufficientConfidenceLevel), frontendAppConfig)
        val controller = new Harness(authAction)
        val result = controller.onPageLoad()(fakeRequest)
        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some(routes.UnauthorisedController.onPageLoad().url)
      }
    }

    "the user used an unaccepted auth provider" must {
      "redirect the user to the unauthorised page" in {
        val authAction = new AuthenticatedIdentifierAction(new FakeFailingAuthConnector(new UnsupportedAuthProvider), frontendAppConfig)
        val controller = new Harness(authAction)
        val result = controller.onPageLoad()(fakeRequest)
        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some(routes.UnauthorisedController.onPageLoad().url)
      }
    }

    "the user has an unsupported affinity group" must {
      "redirect the user to the unauthorised page" in {
        val authAction = new AuthenticatedIdentifierAction(new FakeFailingAuthConnector(new UnsupportedAffinityGroup), frontendAppConfig)
        val controller = new Harness(authAction)
        val result = controller.onPageLoad()(fakeRequest)
        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some(routes.UnauthorisedController.onPageLoad().url)
      }
    }

    "the user has an unsupported credential role" must {
      "redirect the user to the unauthorised page" in {
        val authAction = new AuthenticatedIdentifierAction(new FakeFailingAuthConnector(new UnsupportedCredentialRole), frontendAppConfig)
        val controller = new Harness(authAction)
        val result = controller.onPageLoad()(fakeRequest)
        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some(routes.UnauthorisedController.onPageLoad().url)
      }
    }
  }

  private def mockAuthSuccessRetrieval = {
    implicit val hc = mock[HeaderCarrier]
    val mockAuthConnector: AuthConnector = mock[AuthConnector]
    val retrievalResult: Future[Option[String]] = Future.successful(Some("123"))
    val organisationPredicate: Predicate = AffinityGroup.Organisation
    val agentPredicate: Predicate = Agent and Enrolment("HMRC-AS-AGENT")

    Mockito.when(mockAuthConnector.authorise[Option[String]](Matchers.eq(agentPredicate or organisationPredicate), Matchers.any())
      (any(), any())).thenReturn(retrievalResult)
    mockAuthConnector
  }
}

class FakeFailingAuthConnector(exceptionToReturn: Throwable) extends AuthConnector {
  val serviceUrl: String = ""

  override def authorise[A](predicate: Predicate, retrieval: Retrieval[A])(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[A] =
    Future.failed(exceptionToReturn)
}
