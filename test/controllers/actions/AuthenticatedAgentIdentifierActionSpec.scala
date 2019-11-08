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
import play.api.mvc.{BodyParsers, Results}
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core.retrieve.~
import uk.gov.hmrc.auth.core.{AffinityGroup, Enrolment, EnrolmentIdentifier, Enrolments}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class AuthenticatedAgentIdentifierActionSpec extends SpecBase {

  class Harness(authAction: IdentifierAction) {
    def onPageLoad() = authAction { _ => Results.Ok }
  }

  private def authRetrievals(enrolment: Enrolments = agentEnrolment) =
    Future.successful(new ~(new ~(Some("id"), Some(AffinityGroup.Agent)), enrolment))

  private val agentEnrolment = Enrolments(Set(Enrolment(
    "HMRC-AS-AGENT",
    List(EnrolmentIdentifier("AgentReferenceNumber","SomeVal")),
    "Activated",
    None
  )))

  "AuthenticatedAgentIdentifierAction" when {

    "enrolment exists for trusts" must {
      "allow user to continue" in {

        val enrolments = agentEnrolment.copy(
          agentEnrolment.enrolments ++ Set(Enrolment(
            "HMRC-TERS-ORG",
            List(EnrolmentIdentifier("","")),
            "Activated",
            None
          ))
        )

        val application = applicationBuilder(userAnswers = None).build()
        val bodyParsers = application.injector.instanceOf[BodyParsers.Default]
        val authAction = new AuthenticatedAgentIdentifierAction(new FakeAuthConnector(authRetrievals(enrolments)), frontendAppConfig, bodyParsers)

        val controller = new Harness(authAction)
        val result = controller.onPageLoad()(fakeRequest)

        status(result) mustBe OK

        application.stop()

      }
    }

    "enrolment does not exist for trusts" must {
      "redirect to AgentNotAuthorised" in {

        val application = applicationBuilder(userAnswers = None).build()
        val bodyParsers = application.injector.instanceOf[BodyParsers.Default]
        val authAction = new AuthenticatedAgentIdentifierAction(new FakeAuthConnector(authRetrievals()), frontendAppConfig, bodyParsers)

        val controller = new Harness(authAction)
        val result = controller.onPageLoad()(fakeRequest)

        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some(controllers.routes.AgentNotAuthorisedController.onPageLoad().url)

        application.stop()

      }
    }

  }


}
