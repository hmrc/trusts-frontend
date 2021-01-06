/*
 * Copyright 2021 HM Revenue & Customs
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
import models.core.UserAnswers
import models.core.http.MatchedResponse._
import models.core.http.SuccessOrFailureResponse
import models.{Mode, NormalMode}
import navigation.registration.TaskListNavigator
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import pages.register.{MatchingNamePage, PostcodeForTheTrustPage, WhatIsTheUTRPage}
import play.api.test.Helpers._
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.Future

class MatchingServiceSpec extends RegistrationSpecBase {

  implicit lazy val hc: HeaderCarrier = HeaderCarrier()

  private val mockConnector: TrustConnector = mock[TrustConnector]

  private val navigator = injector.instanceOf[TaskListNavigator]

  private val mode: Mode = NormalMode

  "Matching Service" when {

    val service = new MatchingService(mockConnector, registrationsRepository, navigator)

    val userAnswers: UserAnswers = emptyUserAnswers
      .set(WhatIsTheUTRPage, "utr").success.value
      .set(MatchingNamePage, "name").success.value
      .set(PostcodeForTheTrustPage, "postcode").success.value

    "Success response" when {
      "agent" must {
        "redirect to client internal reference page" in {

          when(mockConnector.matching(any())(any(), any())).thenReturn(Future.successful(SuccessOrFailureResponse(true)))

          val result = service.matching(userAnswers, fakeDraftId, isAgent = true, mode)

          redirectLocation(result).value mustBe "http://localhost:8847/trusts-registration/agent-details/id/start"
        }
      }

      "non-agent" must {
        "redirect to task list" in {
          
          when(mockConnector.matching(any())(any(), any())).thenReturn(Future.successful(SuccessOrFailureResponse(true)))

          val result = service.matching(userAnswers, fakeDraftId, isAgent = false, mode)

          redirectLocation(result).value mustBe controllers.register.routes.TaskListController.onPageLoad(fakeDraftId).url
        }
      }
    }

    "Failed response" must {
      "redirect to FailedMatch" in {

        when(mockConnector.matching(any())(any(), any())).thenReturn(Future.successful(SuccessOrFailureResponse(false)))

        val result = service.matching(userAnswers, fakeDraftId, isAgent = false, mode)

        redirectLocation(result).value mustBe controllers.register.routes.FailedMatchController.onPageLoad(fakeDraftId).url
      }
    }

    "AlreadyRegistered response" must {
      "redirect to TrustAlreadyRegistered" in {

        when(mockConnector.matching(any())(any(), any())).thenReturn(Future.successful(AlreadyRegistered))

        val result = service.matching(userAnswers, fakeDraftId, isAgent = false, mode)

        redirectLocation(result).value mustBe controllers.register.routes.TrustAlreadyRegisteredController.onPageLoad(fakeDraftId).url
      }
    }

    "InternalServerError response" must {
      "redirect to MatchingDown" in {

        when(mockConnector.matching(any())(any(), any())).thenReturn(Future.successful(InternalServerError))

        val result = service.matching(userAnswers, fakeDraftId, isAgent = false, mode)

        redirectLocation(result).value mustBe controllers.register.routes.MatchingDownController.onPageLoad().url
      }
    }

    "WhatIsUtrPage and/or TrustNamePage not answered" must {
      "redirect to FailedMatch" in {

        val result = service.matching(emptyUserAnswers, fakeDraftId, isAgent = false, mode)

        redirectLocation(result).value mustBe controllers.register.routes.FailedMatchController.onPageLoad(fakeDraftId).url
      }
    }
  }
}
