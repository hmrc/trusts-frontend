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

package services

import base.RegistrationSpecBase
import connector.TrustConnector
import controllers.Assets.Redirect
import models.core.UserAnswers
import models.core.http.MatchedResponse._
import models.core.http.SuccessOrFailureResponse
import models.{Mode, NormalMode}
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import pages.register.{MatchingNamePage, PostcodeForTheTrustPage, WhatIsTheUTRPage}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

class MatchingServiceSpec extends RegistrationSpecBase {

  implicit lazy val hc: HeaderCarrier = HeaderCarrier()

  private val mockConnector: TrustConnector = mock[TrustConnector]

  private val mode: Mode = NormalMode

  "Matching Service" when {

    val service = new MatchingService(mockConnector, registrationsRepository)

    val userAnswers: UserAnswers = emptyUserAnswers
      .set(WhatIsTheUTRPage, "utr").success.value
      .set(MatchingNamePage, "name").success.value
      .set(PostcodeForTheTrustPage, "postcode").success.value

    "Success response" when {
      "agent" must {
        "redirect to client internal reference page" in {

          when(mockConnector.matching(any())(any(), any())).thenReturn(Future.successful(SuccessOrFailureResponse(true)))

          val result = Await.result(service.matching(userAnswers, fakeDraftId, isAgent = true, mode), Duration.Inf)

          result mustBe Redirect(controllers.register.agents.routes.AgentInternalReferenceController.onPageLoad(mode, fakeDraftId))
        }
      }

      "non-agent" must {
        "redirect to task list" in {
          
          when(mockConnector.matching(any())(any(), any())).thenReturn(Future.successful(SuccessOrFailureResponse(true)))

          val result = Await.result(service.matching(userAnswers, fakeDraftId, isAgent = false, mode), Duration.Inf)

          result mustBe Redirect(controllers.register.routes.TaskListController.onPageLoad(fakeDraftId))
        }
      }
    }

    "Failed response" must {
      "redirect to FailedMatch" in {

        when(mockConnector.matching(any())(any(), any())).thenReturn(Future.successful(SuccessOrFailureResponse(false)))

        val result = Await.result(service.matching(userAnswers, fakeDraftId, isAgent = false, mode), Duration.Inf)

        result mustBe Redirect(controllers.register.routes.FailedMatchController.onPageLoad(fakeDraftId))
      }
    }

    "AlreadyRegistered response" must {
      "redirect to TrustAlreadyRegistered" in {

        when(mockConnector.matching(any())(any(), any())).thenReturn(Future.successful(AlreadyRegistered))

        val result = Await.result(service.matching(userAnswers, fakeDraftId, isAgent = false, mode), Duration.Inf)

        result mustBe Redirect(controllers.register.routes.TrustAlreadyRegisteredController.onPageLoad(fakeDraftId))
      }
    }

    "InternalServerError response" must {
      "redirect to MatchingDown" in {

        when(mockConnector.matching(any())(any(), any())).thenReturn(Future.successful(InternalServerError))

        val result = Await.result(service.matching(userAnswers, fakeDraftId, isAgent = false, mode), Duration.Inf)

        result mustBe Redirect(controllers.register.routes.MatchingDownController.onPageLoad())
      }
    }

    "WhatIsUtrPage and/or TrustNamePage not answered" must {
      "redirect to FailedMatch" in {

        val result = Await.result(service.matching(emptyUserAnswers, fakeDraftId, isAgent = false, mode), Duration.Inf)

        result mustBe Redirect(controllers.register.routes.FailedMatchController.onPageLoad(fakeDraftId))
      }
    }
  }
}
