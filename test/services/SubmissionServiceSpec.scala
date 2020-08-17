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
import mapping.registration.{AddressType, IdentificationOrgType, LeadTrusteeOrgType, LeadTrusteeType, RegistrationMapper}
import models.RegistrationSubmission.AllStatus
import models.core.UserAnswers
import models.core.http.RegistrationTRNResponse
import models.core.http.TrustResponse.UnableToRegister
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatest.{FreeSpec, MustMatchers, OptionValues}
import play.api.libs.json.JsValue
import repositories.RegistrationsRepository
import uk.gov.hmrc.http.HeaderCarrier
import utils.TestUserAnswers
import viewmodels.{DraftRegistration, RegistrationAnswerSections}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

class SubmissionServiceSpec extends FreeSpec with MustMatchers
  with OptionValues with Generators with SpecBaseHelpers
{

  private lazy val registrationMapper: RegistrationMapper = injector.instanceOf[RegistrationMapper]

  private val mockConnector : TrustConnector = mock[TrustConnector]

  private val stubbedRegistrationsRepository: RegistrationsRepository = new RegistrationsRepository {
    private val correspondenceAddress = AddressType("line1", "line2", None, None, Some("AA1 1AA"), "GB")
    override def get(draftId: String)
                    (implicit hc: HeaderCarrier): Future[Option[UserAnswers]] = Future.successful(None)

    override def set(userAnswers: UserAnswers)
                    (implicit hc: HeaderCarrier): Future[Boolean] = Future.successful(true)

    override def listDrafts()
                           (implicit hc: HeaderCarrier): Future[List[DraftRegistration]] = Future.successful(List.empty)

    override def addDraftRegistrationSections(draftId: String, registrationJson: JsValue)
                                             (implicit hc: HeaderCarrier): Future[JsValue] = Future.successful(registrationJson)

    override def getAllStatus(draftId: String)
                             (implicit hc: HeaderCarrier) : Future[AllStatus] = Future.successful(AllStatus())

    override def setAllStatus(draftId: String, status: AllStatus)(implicit hc: HeaderCarrier): Future[Boolean] = Future.successful(true)

    override def getAnswerSections(draftId: String)
                                  (implicit hc:HeaderCarrier) : Future[RegistrationAnswerSections] = Future.successful(RegistrationAnswerSections())

    override def getLeadTrustee(draftId: String)(implicit hc: HeaderCarrier): Future[LeadTrusteeType] = Future.successful(testLeadTrusteeOrg)

    override def getCorrespondenceAddress(draftId: String)(implicit hc: HeaderCarrier): Future[AddressType] = Future.successful(correspondenceAddress)
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
        val userAnswers = TestUserAnswers.withDeceasedSettlor(emptyUserAnswers)

        intercept[UnableToRegister] {
          Await.result( submissionService.submit(userAnswers),Duration.Inf)
        }
      }
    }
  }

  private val newTrustUserAnswers = {
    val emptyUserAnswers = TestUserAnswers.emptyUserAnswers
    val uaWithDeceased = TestUserAnswers.withDeceasedSettlor(emptyUserAnswers)
    val uaWithTrustDetails = TestUserAnswers.withTrustDetails(uaWithDeceased)
    val asset = TestUserAnswers.withMoneyAsset(uaWithTrustDetails)
    val userAnswers = TestUserAnswers.withDeclaration(asset)

    userAnswers
  }

}
