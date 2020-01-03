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

package controllers.actions

import base.RegistrationSpecBase
import controllers.actions.register.RequireDraftRegistrationActionRefinerImpl
import controllers.register.routes._
import models.registration.pages.RegistrationStatus.{Complete, InProgress}
import models.requests.RegistrationDataRequest
import org.scalatest.EitherValues
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.mockito.MockitoSugar
import play.api.http.HeaderNames
import play.api.mvc.Result
import uk.gov.hmrc.auth.core.{AffinityGroup, Enrolment, Enrolments}
import utils.TestUserAnswers

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class RequireDraftRegistrationActionRefinerSpec extends RegistrationSpecBase with MockitoSugar with ScalaFutures with EitherValues {

  class Harness()
    extends RequireDraftRegistrationActionRefinerImpl {

    def callRefine[A](request: RegistrationDataRequest[A]): Future[Either[Result, RegistrationDataRequest[A]]] = refine(request)
  }

  "require draft registration action" when {

      "there is a complete registration" must {

        "redirect to Confirmation by default" in {
          val answers = TestUserAnswers.emptyUserAnswers.copy(progress = Complete)

          val action = new Harness()
          val futureResult = action.callRefine(new RegistrationDataRequest(fakeRequest, "id", answers, AffinityGroup.Organisation, Enrolments(Set.empty[Enrolment])))

          whenReady(futureResult) { result =>
            result.left.value.header.headers(HeaderNames.LOCATION) mustBe ConfirmationController.onPageLoad(answers.draftId).url
          }
        }

      }

    "there is a non-complete registration" must {

      "continue with refining the request" in {
        val answers = TestUserAnswers.emptyUserAnswers.copy(progress = InProgress)

        val dataRequest = new RegistrationDataRequest(fakeRequest, "id", answers, AffinityGroup.Organisation, Enrolments(Set.empty[Enrolment]))

        val action = new Harness()
        val futureResult = action.callRefine(dataRequest)

        whenReady(futureResult) { result =>
          result.right.value mustEqual dataRequest
        }
      }

    }

  }

}
