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

package services

import base.RegistrationSpecBase
import connector.TrustConnector
import models.core.MatchingAndSuitabilityUserAnswers
import models.core.http.MatchedResponse._
import models.core.http.SuccessOrFailureResponse
import models.registration.Matched
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, verify, when}
import org.scalatest.BeforeAndAfterEach
import pages.register.{ExistingTrustMatched, MatchingNamePage, PostcodeForTheTrustPage, WhatIsTheUTRPage}
import play.api.test.Helpers._
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.Future

class MatchingServiceSpec extends RegistrationSpecBase with BeforeAndAfterEach {

  implicit lazy val hc: HeaderCarrier = HeaderCarrier()

  private val mockConnector: TrustConnector = mock[TrustConnector]

  override def beforeEach(): Unit = {
    reset(mockConnector)

    reset(cacheRepository)
    when(cacheRepository.set(any())).thenReturn(Future.successful(true))
  }

  "Matching Service" when {

    val service = new MatchingService(mockConnector, cacheRepository)

    val userAnswers: MatchingAndSuitabilityUserAnswers = emptyMatchingAndSuitabilityUserAnswers
      .set(WhatIsTheUTRPage, "utr")
      .success
      .value
      .set(MatchingNamePage, "name")
      .success
      .value
      .set(PostcodeForTheTrustPage, "postcode")
      .success
      .value

    "Success response" when {

      "agent" must {

        "redirect to ExpressTrust page" in {

          when(mockConnector.matching(any())(any(), any()))
            .thenReturn(Future.successful(SuccessOrFailureResponse(true)))

          val result = service.matching(userAnswers, isAgent = true)

          redirectLocation(result).value mustBe controllers.register.suitability.routes.ExpressTrustYesNoController
            .onPageLoad()
            .url

          verify(mockConnector).matching(any())(any(), any())

          val expectedAnswers = userAnswers.set(ExistingTrustMatched, Matched.Success).success.value

          verify(cacheRepository).set(ArgumentMatchers.eq(expectedAnswers))
        }
      }

      "non-agent" must {

        "redirect to Express trust" in {

          when(mockConnector.matching(any())(any(), any()))
            .thenReturn(Future.successful(SuccessOrFailureResponse(true)))

          val result = service.matching(userAnswers, isAgent = false)

          redirectLocation(result).value mustBe controllers.register.suitability.routes.ExpressTrustYesNoController
            .onPageLoad()
            .url

          verify(mockConnector).matching(any())(any(), any())

          val expectedAnswers = userAnswers.set(ExistingTrustMatched, Matched.Success).success.value

          verify(cacheRepository).set(ArgumentMatchers.eq(expectedAnswers))
        }
      }
    }

    "unsuccessful match" must {
      "redirect to FailedMatch" in {

        when(mockConnector.matching(any())(any(), any())).thenReturn(Future.successful(SuccessOrFailureResponse(false)))

        val result = service.matching(userAnswers, isAgent = false)

        redirectLocation(result).value mustBe controllers.register.routes.FailedMatchController.onPageLoad().url
      }
    }

    "AlreadyRegistered response" must {
      "redirect to TrustAlreadyRegistered" in {

        when(mockConnector.matching(any())(any(), any())).thenReturn(Future.successful(AlreadyRegistered))

        val result = service.matching(userAnswers, isAgent = false)

        redirectLocation(result).value mustBe controllers.register.routes.TrustAlreadyRegisteredController
          .onPageLoad()
          .url
      }
    }

    "InternalServerError response" must {
      "redirect to MatchingDown" in {

        when(mockConnector.matching(any())(any(), any())).thenReturn(Future.successful(InternalServerError))

        val result = service.matching(userAnswers, isAgent = false)

        redirectLocation(result).value mustBe controllers.register.routes.MatchingDownController.onPageLoad().url
      }
    }

    "no user answers" must {

      "redirect to matching down" in {

        val result = service.matching(emptyMatchingAndSuitabilityUserAnswers, isAgent = false)

        redirectLocation(result).value mustBe controllers.register.routes.MatchingDownController.onPageLoad().url
      }
    }
  }

}
