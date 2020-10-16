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

import java.time.LocalDate

import base.SpecBaseHelpers
import connector.TrustConnector
import generators.Generators
import mapping.registration.{AddressType, LeadTrusteeType, RegistrationMapper}
import models.RegistrationSubmission.AllStatus
import models.core.UserAnswers
import models.core.http.RegistrationTRNResponse
import models.core.http.TrustResponse.UnableToRegister
import models.requests.RegistrationDataRequest
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatest.{FreeSpec, MustMatchers, OptionValues}
import play.api.libs.json.JsValue
import repositories.RegistrationsRepository
import uk.gov.hmrc.auth.core.{AffinityGroup, Enrolments}
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

    override def getTrustName(draftId: String)(implicit hc: HeaderCarrier): Future[String] = Future.successful("Name")

    override def getTrustSetupDate(draftId: String)(implicit hc: HeaderCarrier): Future[Option[LocalDate]] = Future.successful(Some(LocalDate.parse("2020-10-05")))
  }

  private val auditService : AuditService = injector.instanceOf[FakeAuditService]

  private val submissionService = new DefaultSubmissionService(
    registrationMapper,
    mockConnector,
    auditService,
    stubbedRegistrationsRepository
  )

  private implicit lazy val hc: HeaderCarrier = HeaderCarrier()
  private implicit lazy val request: RegistrationDataRequest[_] = RegistrationDataRequest(
    fakeRequest,
    "internalId",
    emptyUserAnswers,
    AffinityGroup.Organisation,
    Enrolments(Set())
  )

  "SubmissionService" -  {

    "for an empty user answers" - {

      "must not be able to submit data " in {

        val userAnswers = emptyUserAnswers

        val result  = Await.result(submissionService.submit(userAnswers),Duration.Inf)
        result mustBe UnableToRegister()
      }
    }

    "when user answers is not empty" - {

      "must be able to submit data when all data available for registration by organisation user" in {

        val userAnswers = newTrustUserAnswers

        when(mockConnector.register(any[JsValue], any())(any[HeaderCarrier], any())).
          thenReturn(Future.successful(RegistrationTRNResponse("XTRN1234567")))

        val result  = Await.result(submissionService.submit(userAnswers),Duration.Inf)
        result mustBe RegistrationTRNResponse("XTRN1234567")
      }

      "must be able to submit data when all data available for registration by agent" in {

        val userAnswers = TestUserAnswers.withAgent(newTrustUserAnswers)

        when(mockConnector.register(any[JsValue], any())(any[HeaderCarrier], any())).
          thenReturn(Future.successful(RegistrationTRNResponse("XTRN1234567")))

        val result  = Await.result(submissionService.submit(userAnswers),Duration.Inf)
        result mustBe RegistrationTRNResponse("XTRN1234567")
      }

      "must not be able to submit data when not all data available for registration" in {

        val emptyUserAnswers = TestUserAnswers.emptyUserAnswers
        val userAnswers = TestUserAnswers.withDeceasedSettlor(emptyUserAnswers)

        val result  = Await.result(submissionService.submit(userAnswers),Duration.Inf)
        result mustBe UnableToRegister()
      }
    }
  }

  private val newTrustUserAnswers = {
    val emptyUserAnswers = TestUserAnswers.emptyUserAnswers
    val uaWithDeceased = TestUserAnswers.withDeceasedSettlor(emptyUserAnswers)
    val asset = TestUserAnswers.withMoneyAsset(uaWithDeceased)
    val userAnswers = TestUserAnswers.withDeclaration(asset)

    userAnswers
  }

}
