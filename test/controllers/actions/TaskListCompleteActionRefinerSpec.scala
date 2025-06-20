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

package controllers.actions

import base.RegistrationSpecBase
import generators.ModelGenerators
import models.FirstTaxYearAvailable
import models.requests.RegistrationDataRequest
import org.mockito.ArgumentMatchers.any
import org.scalacheck.Arbitrary.arbitrary
import org.scalatest.concurrent.ScalaFutures.whenReady
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.register.suitability.TrustTaxableYesNoPage
import pages.register.{RegistrationProgress, TrustHaveAUTRPage}
import play.api.mvc.Result
import play.api.test.Helpers._
import repositories.RegistrationsRepository
import uk.gov.hmrc.auth.core.AffinityGroup.Organisation
import uk.gov.hmrc.auth.core.Enrolments
import org.mockito.Mockito.{when, mock}

import scala.concurrent.Future

class TaskListCompleteActionRefinerSpec extends RegistrationSpecBase with ScalaCheckPropertyChecks with ModelGenerators {

  class Harness(mockRegistrationProgress: RegistrationProgress,
                mockRegistrationsRepository: RegistrationsRepository) extends TaskListCompleteActionRefinerImpl(
    mockRegistrationProgress,
    mockRegistrationsRepository,
    executionContext
  ) {
    def callRefine[A](request: RegistrationDataRequest[A]): Future[Either[Result, RegistrationDataRequest[A]]] = refine(request)
  }

  "TaskListCompleteActionRefiner" when {

    "task list complete" must {
      "return request" in {

        forAll(arbitrary[Option[FirstTaxYearAvailable]], arbitrary[Boolean], arbitrary[Boolean]) {
          (firstTaxYearAvailable, isTaxable, isExistingTrust) =>

            val mockRegistrationProgress: RegistrationProgress = mock[RegistrationProgress]
            val mockRegistrationsRepository: RegistrationsRepository = mock[RegistrationsRepository]

            when(mockRegistrationsRepository.getFirstTaxYearAvailable(any())(any()))
              .thenReturn(Future.successful(firstTaxYearAvailable))

            when(mockRegistrationProgress.isTaskListComplete(any(), any(), any(), any())(any()))
              .thenReturn(Future.successful(true))

            val userAnswers = emptyUserAnswers
              .set(TrustTaxableYesNoPage, isTaxable).success.value
              .set(TrustHaveAUTRPage, isExistingTrust).success.value

            val request = RegistrationDataRequest(
              request = fakeRequest,
              internalId = "internalId",
              sessionId = "sessionId",
              userAnswers = userAnswers,
              affinityGroup = Organisation,
              enrolments = Enrolments(Set())
            )

            val action = new Harness(mockRegistrationProgress, mockRegistrationsRepository)

            whenReady(action.callRefine(request)) { r =>
              r.value mustEqual request
            }
        }
      }
    }

    "task list incomplete" must {
      "redirect to task list" in {

        forAll(arbitrary[Option[FirstTaxYearAvailable]], arbitrary[Boolean], arbitrary[Boolean]) {
          (firstTaxYearAvailable, isTaxable, isExistingTrust) =>

            val mockRegistrationProgress: RegistrationProgress = mock[RegistrationProgress]
            val mockRegistrationsRepository: RegistrationsRepository = mock[RegistrationsRepository]

            when(mockRegistrationsRepository.getFirstTaxYearAvailable(any())(any()))
              .thenReturn(Future.successful(firstTaxYearAvailable))

            when(mockRegistrationProgress.isTaskListComplete(any(), any(), any(), any())(any()))
              .thenReturn(Future.successful(false))

            val userAnswers = emptyUserAnswers
              .set(TrustTaxableYesNoPage, isTaxable).success.value
              .set(TrustHaveAUTRPage, isExistingTrust).success.value

            val request = RegistrationDataRequest(
              request = fakeRequest,
              internalId = "internalId",
              sessionId = "sessionId",
              userAnswers = userAnswers,
              affinityGroup = Organisation,
              enrolments = Enrolments(Set())
            )

            val action = new Harness(mockRegistrationProgress, mockRegistrationsRepository)

            whenReady(action.callRefine(request)) { r =>
              val result = Future.successful(r.left.value)
              status(result) mustEqual SEE_OTHER
              redirectLocation(result).value mustBe controllers.register.routes.TaskListController.onPageLoad(fakeDraftId).url
            }
        }
      }
    }
  }
}
