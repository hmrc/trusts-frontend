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

import base.SpecBaseHelpers
import connector.TrustConnector
import generators.Generators
import mapping.registration.RegistrationMapper
import models.core.UserAnswers
import models.core.http.TrustResponse.UnableToRegister
import models.core.http._
import models.requests.RegistrationDataRequest
import models.{FirstTaxYearAvailable, RegistrationSubmission}
import org.mockito.ArgumentMatchers.{eq => eqTo, _}
import org.mockito.Mockito.{verify, when}
import org.scalatest.OptionValues
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import play.api.i18n.Messages
import play.api.libs.json.{JsObject, JsValue, Json}
import play.api.test.Helpers.OK
import repositories.RegistrationsRepository
import uk.gov.hmrc.auth.core.AffinityGroup.Organisation
import uk.gov.hmrc.auth.core.{AffinityGroup, Enrolments}
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}
import utils.TestUserAnswers
import viewmodels.{DraftRegistration, RegistrationAnswerSections}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

class SubmissionServiceSpec extends AnyFreeSpec with Matchers
  with OptionValues with Generators with SpecBaseHelpers {

  private lazy val registrationMapper: RegistrationMapper = injector.instanceOf[RegistrationMapper]

  private val mockConnector: TrustConnector = mock[TrustConnector]

  private val stubbedRegistrationsRepository: RegistrationsRepository = new RegistrationsRepository {
    private val correspondenceAddress = AddressType("line1", "line2", None, None, Some("AA1 1AA"), "GB")

    override def get(draftId: String)
                    (implicit hc: HeaderCarrier): Future[Option[UserAnswers]] =
      Future.successful(None)

    override def set(userAnswers: UserAnswers, affinityGroup: AffinityGroup)
                    (implicit hc: HeaderCarrier): Future[Boolean] =
      Future.successful(true)

    override def getMostRecentDraftId()(implicit hc: HeaderCarrier): Future[Option[String]] =
      Future.successful(None)

    override def listDrafts()
                           (implicit hc: HeaderCarrier, messages: Messages): Future[List[DraftRegistration]] =
      Future.successful(List.empty)

    override def addDraftRegistrationSections(draftId: String, registrationJson: JsValue)
                                             (implicit hc: HeaderCarrier): Future[JsValue] =
      Future.successful(registrationJson)

    override def getAnswerSections(draftId: String)
                                  (implicit hc: HeaderCarrier, messages: Messages): Future[RegistrationAnswerSections] =
      Future.successful(RegistrationAnswerSections())

    override def getLeadTrustee(draftId: String)
                               (implicit hc: HeaderCarrier): Future[LeadTrusteeType] =
      Future.successful(testLeadTrusteeOrg)

    override def getCorrespondenceAddress(draftId: String)
                                         (implicit hc: HeaderCarrier): Future[AddressType] =
      Future.successful(correspondenceAddress)

    override def getTrustName(draftId: String)
                             (implicit hc: HeaderCarrier): Future[String] =
      Future.successful("Name")

    override def removeDraft(draftId: String)(implicit hc: HeaderCarrier): Future[HttpResponse] =
      Future.successful(HttpResponse(OK, ""))

    override def getAgentAddress(draftId: String)(implicit hc: HeaderCarrier): Future[Option[AddressType]] =
      Future.successful(None)

    override def getClientReference(draftId: String, affinityGroup: AffinityGroup)(implicit hc: HeaderCarrier): Future[Option[String]] =
      Future.successful(None)

    override def getFirstTaxYearAvailable(draftId: String)(implicit hc: HeaderCarrier): Future[Option[FirstTaxYearAvailable]] =
      Future.successful(Some(FirstTaxYearAvailable(2, earlierYearsToDeclare = false)))

    override def getRegistrationPieces(draftId: String)(implicit hc: HeaderCarrier): Future[JsObject] =
      Future.successful(Json.parse("{}").as[JsObject])

    override def getDraftSettlors(draftId: String)(implicit hc: HeaderCarrier): Future[JsValue] =
      Future.successful(Json.parse("{}"))

    override def setDraftSettlors(draftId: String, data: JsValue)(implicit hc: HeaderCarrier): Future[HttpResponse] =
      Future.successful(HttpResponse(OK, ""))

    override def getSettlorsAnswerSections(draftId: String)(implicit hc: HeaderCarrier): Future[Seq[RegistrationSubmission.AnswerSection]] =
      Future.successful(
        RegistrationSubmission.AllAnswerSections().settlors.getOrElse(Seq.empty[RegistrationSubmission.AnswerSection])
      )
  }

  private val auditService: AuditService = injector.instanceOf[FakeAuditService]

  private val submissionService = new DefaultSubmissionService(
    registrationMapper,
    mockConnector,
    auditService,
    stubbedRegistrationsRepository
  )

  private implicit lazy val hc: HeaderCarrier = HeaderCarrier()

  private implicit lazy val request: RegistrationDataRequest[_] =
    RegistrationDataRequest(fakeRequest, "internalId", "sessionId", emptyUserAnswers, AffinityGroup.Organisation, Enrolments(Set()))

  private val newTrustUserAnswers = {
    val emptyUserAnswers = TestUserAnswers.emptyUserAnswers
    val userAnswers = TestUserAnswers.withDeclaration(emptyUserAnswers)

    userAnswers
  }

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

        when(mockConnector.register(any[JsValue], any())(any(), any[HeaderCarrier], any())).
          thenReturn(Future.successful(RegistrationTRNResponse("XTRN1234567")))

        val result  = Await.result(submissionService.submit(userAnswers),Duration.Inf)
        result mustBe RegistrationTRNResponse("XTRN1234567")
      }

      "must be able to submit data when all data available for registration by agent" in {

        val userAnswers = newTrustUserAnswers

        when(mockConnector.register(any[JsValue], any())(any(), any[HeaderCarrier], any())).
          thenReturn(Future.successful(RegistrationTRNResponse("XTRN1234567")))

        val result  = Await.result(submissionService.submit(userAnswers),Duration.Inf)
        result mustBe RegistrationTRNResponse("XTRN1234567")
      }

      "must not be able to submit data when not all data available for registration" in {

        val emptyUserAnswers = TestUserAnswers.emptyUserAnswers

        val result  = Await.result(submissionService.submit(emptyUserAnswers),Duration.Inf)
        result mustBe UnableToRegister()
      }
    }

    "must audit events" - {

      val mockRegistrationMapper: RegistrationMapper = mock[RegistrationMapper]
      val mockAuditService: AuditService = mock[AuditService]
      val mockRegistrationsRepository: RegistrationsRepository = mock[RegistrationsRepository]

      val submissionService = new DefaultSubmissionService(
        mockRegistrationMapper,
        mockConnector,
        mockAuditService,
        mockRegistrationsRepository
      )

      val userAnswers: UserAnswers = newTrustUserAnswers
      val correspondenceAddress: AddressType = AddressType("Line 1", "Line 2", None, None, None, "GB")
      val trustName: String = "Name"
      val registration: Future[Option[Registration]] = registrationMapper.build(userAnswers, correspondenceAddress, trustName, Organisation)

      "when error retrieving correspondence address transformation" in {

        val errorReason: String = "Error retrieving correspondence address transformation."

        when(mockRegistrationsRepository.getCorrespondenceAddress(any())(any())).thenReturn(Future.failed(new Throwable("")))

        Await.result(submissionService.submit(userAnswers), Duration.Inf)
        verify(mockAuditService).auditRegistrationPreparationFailed(any(), eqTo(errorReason))(any(), any())
      }

      "when error retrieving trust name transformation" in {

        val errorReason: String = "Error retrieving trust name transformation."

        when(mockRegistrationsRepository.getCorrespondenceAddress(any())(any())).thenReturn(Future.successful(correspondenceAddress))
        when(mockRegistrationsRepository.getTrustName(any())(any())).thenReturn(Future.failed(new Throwable("")))

        Await.result(submissionService.submit(userAnswers), Duration.Inf)
        verify(mockAuditService).auditRegistrationPreparationFailed(any(), eqTo(errorReason))(any(), any())
      }

      "when error mapping user answers to Registration" in {

        val errorReason: String = "Error mapping UserAnswers to Registration."

        when(mockRegistrationsRepository.getCorrespondenceAddress(any())(any())).thenReturn(Future.successful(correspondenceAddress))
        when(mockRegistrationsRepository.getTrustName(any())(any())).thenReturn(Future.successful(trustName))
        when(mockRegistrationMapper.build(any(), any(), any(), any())(any(), any())).thenReturn(Future.successful(None))

        Await.result(submissionService.submit(userAnswers), Duration.Inf)
        verify(mockAuditService).auditRegistrationPreparationFailed(any(), eqTo(errorReason))(any(), any())
      }

      "when error adding draft registration sections" in {

        val errorReason: String = "Error adding draft registration sections."

        when(mockRegistrationsRepository.getCorrespondenceAddress(any())(any())).thenReturn(Future.successful(correspondenceAddress))
        when(mockRegistrationsRepository.getTrustName(any())(any())).thenReturn(Future.successful(trustName))
        when(mockRegistrationMapper.build(any(), any(), any(), any())(any(), any())).thenReturn(registration)
        when(mockRegistrationsRepository.addDraftRegistrationSections(any(), any())(any())).thenReturn(Future.failed(new Throwable("")))

        Await.result(submissionService.submit(userAnswers), Duration.Inf)
        verify(mockAuditService).auditRegistrationPreparationFailed(any(), eqTo(errorReason))(any(), any())
      }

      "when registration submission fails" in {

        when(mockRegistrationsRepository.getCorrespondenceAddress(any())(any())).thenReturn(Future.successful(correspondenceAddress))
        when(mockRegistrationsRepository.getTrustName(any())(any())).thenReturn(Future.successful(trustName))
        when(mockRegistrationMapper.build(any(), any(), any(), any())(any(), any())).thenReturn(registration)
        when(mockRegistrationsRepository.addDraftRegistrationSections(any(), any())(any())).thenReturn(Future.successful(Json.obj()))
        when(mockConnector.register(any(), any())(any(), any(), any())).thenReturn(Future.successful(TrustResponse.InternalServerError))

        Await.result(submissionService.submit(userAnswers), Duration.Inf)
        verify(mockAuditService).auditRegistrationSubmissionFailed(any(), any())(any(), any())
      }

      "when registration already submitted" in {

        when(mockRegistrationsRepository.getCorrespondenceAddress(any())(any())).thenReturn(Future.successful(correspondenceAddress))
        when(mockRegistrationsRepository.getTrustName(any())(any())).thenReturn(Future.successful(trustName))
        when(mockRegistrationMapper.build(any(), any(), any(), any())(any(), any())).thenReturn(registration)
        when(mockRegistrationsRepository.addDraftRegistrationSections(any(), any())(any())).thenReturn(Future.successful(Json.obj()))
        when(mockConnector.register(any(), any())(any(), any(), any())).thenReturn(Future.successful(TrustResponse.AlreadyRegistered))

        Await.result(submissionService.submit(userAnswers), Duration.Inf)
        verify(mockAuditService).auditRegistrationAlreadySubmitted(any(), any())(any(), any())
      }

      "when registration successfully submitted" in {

        val response: RegistrationTRNResponse = RegistrationTRNResponse("trn")

        when(mockRegistrationsRepository.getCorrespondenceAddress(any())(any())).thenReturn(Future.successful(correspondenceAddress))
        when(mockRegistrationsRepository.getTrustName(any())(any())).thenReturn(Future.successful(trustName))
        when(mockRegistrationMapper.build(any(), any(), any(), any())(any(), any())).thenReturn(registration)
        when(mockRegistrationsRepository.addDraftRegistrationSections(any(), any())(any())).thenReturn(Future.successful(Json.obj()))
        when(mockConnector.register(any(), any())(any(), any(), any())).thenReturn(Future.successful(response))

        Await.result(submissionService.submit(userAnswers), Duration.Inf)
        verify(mockAuditService).auditRegistrationSubmitted(any(), any(), eqTo(response))(any(), any())
      }
    }
  }
}
