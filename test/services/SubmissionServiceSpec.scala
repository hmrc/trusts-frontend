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

import base.SpecBaseHelpers
import connector.TrustConnector
import generators.Generators
import mapping.{Registration, RegistrationMapper}
import models.core.http.RegistrationTRNResponse
import models.core.http.TrustResponse.UnableToRegister
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatest.{FreeSpec, MustMatchers, OptionValues}
import uk.gov.hmrc.http.HeaderCarrier
import utils.TestUserAnswers

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

class SubmissionServiceSpec extends FreeSpec with MustMatchers
  with OptionValues with Generators with SpecBaseHelpers
{

  private lazy val registrationMapper: RegistrationMapper = injector.instanceOf[RegistrationMapper]

  val mockConnector : TrustConnector = mock[TrustConnector]

  val auditService : AuditService = injector.instanceOf[FakeAuditService]

  val submissionService = new DefaultSubmissionService(registrationMapper,mockConnector,auditService)

  implicit lazy val hc: HeaderCarrier = HeaderCarrier()

  "SubmissionService" -  {

    "for an empty user answers" - {

      "must not able to submit data " in {

        val userAnswers = emptyUserAnswers

        intercept[UnableToRegister] {
          Await.result(submissionService.submit(userAnswers),Duration.Inf)
        }
      }
    }

    "when user answers is not empty" - {

      "must able to submit data  when all data available for registration by organisation user" in {

        val userAnswers = newTrustUserAnswers

        when(mockConnector.register(any[Registration], any())(any[HeaderCarrier], any())).
          thenReturn(Future.successful(RegistrationTRNResponse("XTRN1234567")))

        val result  = Await.result(submissionService.submit(userAnswers),Duration.Inf)
        result mustBe RegistrationTRNResponse("XTRN1234567")
      }

      "must able to submit data  when all data available for registration by agent" in {

        val userAnswers = TestUserAnswers.withAgent(newTrustUserAnswers)

        when(mockConnector.register(any[Registration], any())(any[HeaderCarrier], any())).
          thenReturn(Future.successful(RegistrationTRNResponse("XTRN1234567")))

        val result  = Await.result(submissionService.submit(userAnswers),Duration.Inf)
        result mustBe RegistrationTRNResponse("XTRN1234567")
      }

      "must not able to submit data  when all data not available for registration" in {

        val emptyUserAnswers = TestUserAnswers.emptyUserAnswers
        val uaWithLead = TestUserAnswers.withLeadTrustee(emptyUserAnswers)
        val userAnswers = TestUserAnswers.withDeceasedSettlor(uaWithLead)


        intercept[UnableToRegister] {
          Await.result( submissionService.submit(userAnswers),Duration.Inf)
        }
      }

      "must not able to submit data  when lead details not available" in {

        val emptyUserAnswers = TestUserAnswers.emptyUserAnswers
        val uaWithDeceased = TestUserAnswers.withDeceasedSettlor(emptyUserAnswers)
        val uaWithIndBen = TestUserAnswers.withIndividualBeneficiary(uaWithDeceased)
        val uaWithTrustDetails = TestUserAnswers.withTrustDetails(uaWithIndBen)
        val asset = TestUserAnswers.withMoneyAsset(uaWithTrustDetails)
        val userAnswers = TestUserAnswers.withDeclaration(asset)


        intercept[UnableToRegister] {
          Await.result( submissionService.submit(userAnswers),Duration.Inf)
        }
      }
    }



  }

  private val newTrustUserAnswers = {
    val emptyUserAnswers = TestUserAnswers.emptyUserAnswers
    val uaWithLead = TestUserAnswers.withLeadTrustee(emptyUserAnswers)
    val uaWithDeceased = TestUserAnswers.withDeceasedSettlor(uaWithLead)
    val uaWithIndBen = TestUserAnswers.withIndividualBeneficiary(uaWithDeceased)
    val uaWithTrustDetails = TestUserAnswers.withTrustDetails(uaWithIndBen)
    val asset = TestUserAnswers.withMoneyAsset(uaWithTrustDetails)
    val userAnswers = TestUserAnswers.withDeclaration(asset)

    userAnswers
  }

}
