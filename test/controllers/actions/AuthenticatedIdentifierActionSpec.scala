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
import com.google.inject.Inject
import controllers.routes
import org.scalatest.FreeSpec
import play.api.mvc.{Action, BodyParsers, EssentialAction, Results}
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core._
import play.api.inject.{Injector, bind}
import uk.gov.hmrc.auth.core.authorise.Predicate
import uk.gov.hmrc.auth.core.retrieve.{Retrieval, ~}
import org.mockito.Mockito._
import org.mockito.Matchers.any
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}

class AuthenticatedIdentifierActionSpec extends SpecBase {

  class Harness[A](authAction: AuthenticatedIdentifierAction[A]) {
    def apply(authAction: AuthenticatedIdentifierAction[A]) = Results.Ok
  }



  private def authRetrievals(affinityGroup: AffinityGroup = AffinityGroup.Individual, enrolment: Enrolments = noEnrollment) =
    Future.successful(new ~(new ~(Some("id"), Some(affinityGroup)), enrolment))

  private val noEnrollment = Enrolments(Set())
  private val agentEnrolment = Enrolments(Set(Enrolment("HMRC-AS-AGENT", List(EnrolmentIdentifier("AgentReferenceNumber", "SomeVal")), "Activated", None)))


  "invoking an AuthenticatedIdentifierAction" when {
    "an Agent user hasn't enrolled an Agent Services Account" must {
      "redirect the user to the create agent services page" in {

        val fakeAuthConnector = new FakeAuthConnector(authRetrievals(AffinityGroup.Agent, noEnrollment))
        val application = applicationBuilder(userAnswers = None).overrides(bind[FakeAuthConnector].toInstance(fakeAuthConnector)).build()
        val action = Action { _ => Results.Ok }
        val trustAuth = application.injector.instanceOf[TrustsAuth]
        val result = new AuthenticatedIdentifierAction(action, trustAuth).apply(fakeRequest)

        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some(controllers.routes.CreateAgentServicesAccountController.onPageLoad().url)
        application.stop()
      }
    }


//    "Agent user has correct enrolled in Agent Services Account" - {
//
//      "allow user to continue" - {
//
//        val application = applicationBuilder(userAnswers = None).build()
//        val bodyParsers = application.injector.instanceOf[BodyParsers.Default]
//        val authAction = new AuthenticatedIdentifierAction(new FakeAuthConnector(authRetrievals(agentAffinityGroup, agentEnrolment)), frontendAppConfig, bodyParsers)
//        val controller = new Harness(authAction)
//        val result = controller.onPageLoad()(fakeRequest)
//        status(result) mustBe OK
//        application.stop()
//      }
//
//    }
//
//    "Org user with no enrolments" - {
//
//      "allow user to continue" - {
//
//        val application = applicationBuilder(userAnswers = None).build()
//        val bodyParsers = application.injector.instanceOf[BodyParsers.Default]
//        val authAction = new AuthenticatedIdentifierAction(new FakeAuthConnector(authRetrievals(orgAffinityGroup, noEnrollment)), frontendAppConfig, bodyParsers)
//        val controller = new Harness(authAction)
//        val result = controller.onPageLoad()(fakeRequest)
//        status(result) mustBe OK
//        application.stop()
//      }
//
//    }
//
//    "Individual user" must {
//
//      "redirect the user to the unauthorised page" in {
//
//        val application = applicationBuilder(userAnswers = None).build()
//        val bodyParsers = application.injector.instanceOf[BodyParsers.Default]
//        val authAction = new AuthenticatedIdentifierAction(new FakeAuthConnector(authRetrievals(enrolment = noEnrollment)), frontendAppConfig, bodyParsers)
//        val controller = new Harness(authAction)
//        val result = controller.onPageLoad()(fakeRequest)
//        status(result) mustBe SEE_OTHER
//        redirectLocation(result) mustBe Some(routes.UnauthorisedController.onPageLoad().url)
//        application.stop()
//      }
//
//    }
//
//    "the user hasn't logged in" must {
//
//      "redirect the user to log in " in {
//
//        val application = applicationBuilder(userAnswers = None).build()
//
//        val bodyParsers = application.injector.instanceOf[BodyParsers.Default]
//
//        val authAction = new AuthenticatedIdentifierAction(new FakeFailingAuthConnector(new MissingBearerToken), frontendAppConfig, bodyParsers)
//        val controller = new Harness(authAction)
//        val result = controller.onPageLoad()(fakeRequest)
//
//        status(result) mustBe SEE_OTHER
//
//        redirectLocation(result).get must startWith(frontendAppConfig.loginUrl)
//        application.stop()
//      }
//    }
//
//    "the user's session has expired" must {
//
//      "redirect the user to log in " in {
//
//        val application = applicationBuilder(userAnswers = None).build()
//
//        val bodyParsers = application.injector.instanceOf[BodyParsers.Default]
//
//        val authAction = new AuthenticatedIdentifierAction(new FakeFailingAuthConnector(new BearerTokenExpired), frontendAppConfig, bodyParsers)
//        val controller = new Harness(authAction)
//        val result = controller.onPageLoad()(fakeRequest)
//
//        status(result) mustBe SEE_OTHER
//
//        redirectLocation(result).get must startWith(frontendAppConfig.loginUrl)
//        application.stop()
//      }
//    }
//
//    "the user doesn't have sufficient enrolments" must {
//
//      "redirect the user to the unauthorised page" in {
//
//        val application = applicationBuilder(userAnswers = None).build()
//
//        val bodyParsers = application.injector.instanceOf[BodyParsers.Default]
//
//        val authAction = new AuthenticatedIdentifierAction(new FakeFailingAuthConnector(new InsufficientEnrolments), frontendAppConfig, bodyParsers)
//        val controller = new Harness(authAction)
//        val result = controller.onPageLoad()(fakeRequest)
//
//        status(result) mustBe SEE_OTHER
//
//        redirectLocation(result) mustBe Some(routes.UnauthorisedController.onPageLoad().url)
//        application.stop()
//      }
//    }
//
//    "the user doesn't have sufficient confidence level" must {
//
//      "redirect the user to the unauthorised page" in {
//
//        val application = applicationBuilder(userAnswers = None).build()
//
//        val bodyParsers = application.injector.instanceOf[BodyParsers.Default]
//
//        val authAction = new AuthenticatedIdentifierAction(new FakeFailingAuthConnector(new InsufficientConfidenceLevel), frontendAppConfig, bodyParsers)
//        val controller = new Harness(authAction)
//        val result = controller.onPageLoad()(fakeRequest)
//
//        status(result) mustBe SEE_OTHER
//
//        redirectLocation(result) mustBe Some(routes.UnauthorisedController.onPageLoad().url)
//        application.stop()
//      }
//    }
//
//    "the user used an unaccepted auth provider" must {
//
//      "redirect the user to the unauthorised page" in {
//
//        val application = applicationBuilder(userAnswers = None).build()
//
//        val bodyParsers = application.injector.instanceOf[BodyParsers.Default]
//
//        val authAction = new AuthenticatedIdentifierAction(new FakeFailingAuthConnector(new UnsupportedAuthProvider), frontendAppConfig, bodyParsers)
//        val controller = new Harness(authAction)
//        val result = controller.onPageLoad()(fakeRequest)
//
//        status(result) mustBe SEE_OTHER
//
//        redirectLocation(result) mustBe Some(routes.UnauthorisedController.onPageLoad().url)
//        application.stop()
//      }
//    }
//
//    "the user has an unsupported affinity group" must {
//
//      "redirect the user to the unauthorised page" in {
//
//        val application = applicationBuilder(userAnswers = None).build()
//
//        val bodyParsers = application.injector.instanceOf[BodyParsers.Default]
//
//        val authAction = new AuthenticatedIdentifierAction(new FakeFailingAuthConnector(new UnsupportedAffinityGroup), frontendAppConfig, bodyParsers)
//        val controller = new Harness(authAction)
//        val result = controller.onPageLoad()(fakeRequest)
//
//        status(result) mustBe SEE_OTHER
//
//        redirectLocation(result) mustBe Some(routes.UnauthorisedController.onPageLoad().url)
//        application.stop()
//      }
//    }
//
//    "the user has an unsupported credential role" must {
//
//      "redirect the user to the unauthorised page" in {
//
//        val application = applicationBuilder(userAnswers = None).build()
//
//        val bodyParsers = application.injector.instanceOf[BodyParsers.Default]
//
//        val authAction = new AuthenticatedIdentifierAction(new FakeFailingAuthConnector(new UnsupportedCredentialRole), frontendAppConfig, bodyParsers)
//        val controller = new Harness(authAction)
//        val result = controller.onPageLoad()(fakeRequest)
//
//        status(result) mustBe SEE_OTHER
//
//        redirectLocation(result) mustBe Some(routes.UnauthorisedController.onPageLoad().url)
//        application.stop()
//      }
//    }
  }
}

class FakeFailingAuthConnector @Inject()(exceptionToReturn: Throwable) extends AuthConnector {
  val serviceUrl: String = ""

  override def authorise[A](predicate: Predicate, retrieval: Retrieval[A])(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[A] =
    Future.failed(exceptionToReturn)
}



class FakeAuthConnector(stubbedRetrievalResult: Future[_]) extends AuthConnector {

  override def authorise[A](predicate: Predicate, retrieval: Retrieval[A])(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[A] = {
    stubbedRetrievalResult.map(_.asInstanceOf[A])
  }

}

