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

import base.SpecBaseHelpers
import connector.TrustConnector
import generators.Generators
import mapping.registration.RegistrationMapper
import models.core.UserAnswers
import models.core.http.RegistrationTRNResponse
import models.core.http.TrustResponse.UnableToRegister
import models.registration.pages.Status
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatest.{FreeSpec, MustMatchers, OptionValues}
import play.api.libs.json.JsValue
import repositories.RegistrationsRepository
import uk.gov.hmrc.http.HeaderCarrier
import utils.TestUserAnswers
import viewmodels.DraftRegistration

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

class SubmissionServiceSpec extends FreeSpec with MustMatchers
  with OptionValues with Generators with SpecBaseHelpers
{

  private lazy val registrationMapper: RegistrationMapper = injector.instanceOf[RegistrationMapper]

  private val mockConnector : TrustConnector = mock[TrustConnector]

  private val stubbedRegistrationsRepository = new RegistrationsRepository {
    override def get(draftId: String)
                    (implicit hc: HeaderCarrier): Future[Option[UserAnswers]] = Future.successful(None)

    override def set(userAnswers: UserAnswers)
                    (implicit hc: HeaderCarrier): Future[Boolean] = Future.successful(true)

    override def listDrafts()
                           (implicit hc: HeaderCarrier): Future[List[DraftRegistration]] = Future.successful(List.empty)

    override def addDraftRegistrationSections(draftId: String, registrationJson: JsValue)
                                             (implicit hc: HeaderCarrier): Future[JsValue] = Future.successful(registrationJson)

    override def getSectionStatus(draftId: String, section: String)(implicit hc: HeaderCarrier) : Future[Option[Status]] = Future.successful(None)
  }

  private val auditService : AuditService = injector.instanceOf[FakeAuditService]

  private val submissionService = new DefaultSubmissionService(
    registrationMapper,
    mockConnector,
    auditService,
    stubbedRegistrationsRepository)

  private implicit lazy val hc: HeaderCarrier = HeaderCarrier()

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

        when(mockConnector.register(any[JsValue], any())(any[HeaderCarrier], any())).
          thenReturn(Future.successful(RegistrationTRNResponse("XTRN1234567")))

        val result  = Await.result(submissionService.submit(userAnswers),Duration.Inf)
        result mustBe RegistrationTRNResponse("XTRN1234567")
      }

      "must able to submit data  when all data available for registration by agent" in {

        val userAnswers = TestUserAnswers.withAgent(newTrustUserAnswers)

        when(mockConnector.register(any[JsValue], any())(any[HeaderCarrier], any())).
          thenReturn(Future.successful(RegistrationTRNResponse("XTRN1234567")))

        val result  = Await.result(submissionService.submit(userAnswers),Duration.Inf)
        result mustBe RegistrationTRNResponse("XTRN1234567")
      }

      "must not able to submit data  when all data not available for registration" in {

        val emptyUserAnswers = TestUserAnswers.emptyUserAnswers
        val uaWithLead = TestUserAnswers.withLeadTrusteeIndividual(emptyUserAnswers)
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
    val uaWithLead = TestUserAnswers.withLeadTrusteeIndividual(emptyUserAnswers)
    val uaWithDeceased = TestUserAnswers.withDeceasedSettlor(uaWithLead)
    val uaWithIndBen = TestUserAnswers.withIndividualBeneficiary(uaWithDeceased)
    val uaWithTrustDetails = TestUserAnswers.withTrustDetails(uaWithIndBen)
    val asset = TestUserAnswers.withMoneyAsset(uaWithTrustDetails)
    val userAnswers = TestUserAnswers.withDeclaration(asset)

    userAnswers
  }

}
