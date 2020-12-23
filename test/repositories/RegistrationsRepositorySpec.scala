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

package repositories

import base.RegistrationSpecBase
import config.FrontendAppConfig
import connector.SubmissionDraftConnector
import mapping.registration.AddressMapper
import models.RegistrationSubmission.{AllAnswerSections, AllStatus}
import models._
import models.core.UserAnswers
import models.core.http.{AddressType, IdentificationOrgType, LeadTrusteeOrgType, LeadTrusteeType}
import models.core.pages.UKAddress
import models.registration.pages.Status.{Completed, InProgress}
import org.mockito.Matchers.any
import org.mockito.Mockito.{times, verify, when}
import org.scalatest.MustMatchers
import org.scalatestplus.mockito.MockitoSugar
import pages.register.agents.{AgentAddressYesNoPage, AgentInternalReferencePage, AgentUKAddressPage}
import play.api.Configuration
import play.api.libs.json.{JsArray, Json}
import play.api.test.Helpers.OK
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}
import utils.DateFormatter
import viewmodels.{AnswerRow, AnswerSection, DraftRegistration, RegistrationAnswerSections}

import java.time.{LocalDate, LocalDateTime}
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

class RegistrationsRepositorySpec extends RegistrationSpecBase with MustMatchers with MockitoSugar {

  private implicit lazy val hc: HeaderCarrier = HeaderCarrier()

  private val userAnswersDateTime = LocalDateTime.of(2020, 2, 24, 13, 34, 0)

  private def createRepository(mockConnector: SubmissionDraftConnector, config: FrontendAppConfig = fakeFrontendAppConfig) = {
    val mockDateFormatter: DateFormatter = mock[DateFormatter]
    when(mockDateFormatter.savedUntil(any())(any())).thenReturn("4 February 2012")
    val mapper: AddressMapper = injector.instanceOf[AddressMapper]

    new DefaultRegistrationsRepository(mockDateFormatter, mockConnector, mapper, config)
  }

  "RegistrationRepository" when {
    "getting user answers" must {
      "read answers from main section" in {
        implicit lazy val hc: HeaderCarrier = HeaderCarrier()

        val draftId = "DraftId"

        val userAnswers = models.core.UserAnswers(draftId = draftId, internalAuthId = "internalAuthId", createdAt = userAnswersDateTime)

        val mockConnector = mock[SubmissionDraftConnector]

        val repository = createRepository(mockConnector)

        val response = SubmissionDraftResponse(LocalDateTime.now, Json.toJson(userAnswers), None)

        when(mockConnector.getDraftMain(any())(any(), any())).thenReturn(Future.successful(response))

        val result = Await.result(repository.get(draftId), Duration.Inf)

        result mustBe Some(userAnswers)
        verify(mockConnector).getDraftMain(draftId)(hc, executionContext)
      }
    }

    "getting most recent draft id" must {
      "return the first draft's id received from connector" in {
        implicit lazy val hc: HeaderCarrier = HeaderCarrier()

        val mockConnector = mock[SubmissionDraftConnector]

        val repository = createRepository(mockConnector)

        val drafts = List(
          SubmissionDraftId(
            "draft1",
            LocalDateTime.of(2012, 2, 1, 12, 30, 0),
            Some("reference1")
          ),
          SubmissionDraftId(
            "draft2",
            LocalDateTime.of(2011, 1, 2, 9, 42, 0),
            Some("reference2")
          )
        )

        when(mockConnector.getCurrentDraftIds()(any(), any())).thenReturn(Future.successful(drafts))

        val result = Await.result(repository.getMostRecentDraftId(), Duration.Inf)

        result mustBe Some("draft1")
        verify(mockConnector).getCurrentDraftIds()(hc, executionContext)
      }
    }

    "listing drafts" must {
      "return the drafts received from connector" in {

        val mockConnector = mock[SubmissionDraftConnector]

        val repository = createRepository(mockConnector)

        val drafts = List(
          SubmissionDraftId(
            "draft1",
            LocalDateTime.of(2012, 2, 1, 12, 30, 0),
            Some("reference1")
          ),
          SubmissionDraftId(
            "draft2",
            LocalDateTime.of(2012, 2, 1, 12, 30, 0),
            Some("reference2")
          )
        )

        when(mockConnector.getCurrentDraftIds()(any(), any())).thenReturn(Future.successful(drafts))

        val result = Await.result(repository.listDrafts()(any(), any()), Duration.Inf)

        result mustBe List(
          DraftRegistration("draft1", "reference1", "4 February 2012"),
          DraftRegistration("draft2", "reference2", "4 February 2012")
        )
      }
    }

    "setting user answers" must {
      "write answers to main section" in {
        implicit lazy val hc: HeaderCarrier = HeaderCarrier()

        val draftId = "DraftId"

        val userAnswers = models.core.UserAnswers(draftId = draftId, internalAuthId = "internalAuthId", createdAt = userAnswersDateTime)

        val mockConnector = mock[SubmissionDraftConnector]

        val repository = createRepository(mockConnector)

        when(mockConnector.setDraftMain(any(), any(), any(), any())(any(), any())).thenReturn(Future.successful(HttpResponse(OK, "")))

        val result = Await.result(repository.set(userAnswers), Duration.Inf)

        result mustBe true
        verify(mockConnector).setDraftMain(draftId, Json.toJson(userAnswers), inProgress = false, None)(hc, executionContext)
      }
    }

    "adding a registration section" must {

      "combine into empty sections" in {

        implicit lazy val hc: HeaderCarrier = HeaderCarrier()

        val draftId = "DraftId"

        val mockConnector = mock[SubmissionDraftConnector]

        val repository = createRepository(mockConnector)

        val registrationSectionsData = Json.obj(
          "field/subfield" -> Json.obj(
            "dataField"-> "newData"
            ),
          "field/subfield2" -> JsArray(
            Seq(
              Json.obj("subSubField2"-> "newData")
            )
          )
        )

        when(mockConnector.getRegistrationPieces(any())(any(), any())).thenReturn(Future.successful(registrationSectionsData))

        val currentRegistrationJson = Json.parse(
          """
            |{
            | "existingObject": {
            |   "existingField": "existingValue"
            | },
            | "field" : {
            |   "subfield": {
            |     "otherDataField": "otherData"
            |   },
            |   "subfield2": []
            | }
            |}
            |""".stripMargin)


        val result = Await.result(repository.addDraftRegistrationSections(draftId, currentRegistrationJson), Duration.Inf)

        val expectedCombinedRegistrationJson = Json.parse(
          """
            |{
            | "existingObject": {
            |   "existingField": "existingValue"
            | },
            | "field" : {
            |   "subfield": {
            |     "dataField": "newData",
            |     "otherDataField": "otherData"
            |   },
            |   "subfield2": [
            |     {
            |       "subSubField2": "newData"
            |     }
            |   ]
            | }
            |}
            |""".stripMargin)

        result mustBe expectedCombinedRegistrationJson
        verify(mockConnector).getRegistrationPieces(draftId)(hc, executionContext)
      }
    }
    "reading status" must {

      "read existing status from connector" in {

        implicit lazy val hc: HeaderCarrier = HeaderCarrier()

        val draftId = "DraftId"

        val mockConnector = mock[SubmissionDraftConnector]

        val repository = createRepository(mockConnector)

        val allStatus = AllStatus(beneficiaries = Some(Completed))

        when(mockConnector.getStatus(any())(any(), any())).thenReturn(Future.successful(allStatus))

        val result = Await.result(repository.getAllStatus(draftId), Duration.Inf)

        result mustBe allStatus

        verify(mockConnector).getStatus(draftId)(hc, executionContext)
      }
    }

    "setting status" must {

      "write status to draft" in {

        implicit lazy val hc: HeaderCarrier = HeaderCarrier()

        val draftId = "DraftId"

        val status = AllStatus(beneficiaries = Some(InProgress))

        val mockConnector = mock[SubmissionDraftConnector]

        val repository = createRepository(mockConnector)

        when(mockConnector.setStatus(any(), any())(any(), any())).thenReturn(Future.successful(HttpResponse(OK, "")))

        val result = Await.result(repository.setAllStatus(draftId, status), Duration.Inf)

        result mustBe true
        verify(mockConnector).setStatus(draftId, status)(hc, executionContext)
      }
    }

    "reading answer sections" must {
      "return deserialised answer sections" in {
        implicit lazy val hc: HeaderCarrier = HeaderCarrier()

        val draftId = "DraftId"

        val mockConnector = mock[SubmissionDraftConnector]

        val repository = createRepository(mockConnector)

        val answerSections = AllAnswerSections(
          beneficiaries = Some(
            List(
              RegistrationSubmission.AnswerSection(
                Some("headingKey1"),
                List(
                  RegistrationSubmission.AnswerRow("label1", "answer1", "labelArg1")
                ),
                Some("sectionKey1")),
              RegistrationSubmission.AnswerSection(
                Some("headingKey2"),
                List(
                  RegistrationSubmission.AnswerRow("label2", "answer2", "labelArg2")
                ),
                Some("sectionKey2"))
            )
          ),
          trustees = Some(
            List(
              RegistrationSubmission.AnswerSection(
                Some("trusteeHeadingKey1"),
                List(
                  RegistrationSubmission.AnswerRow("label1", "answer1", "labelArg1")
                ),
                Some("trusteeSectionKey1")),
              RegistrationSubmission.AnswerSection(
                Some("trusteeHeadingKey2"),
                List(
                  RegistrationSubmission.AnswerRow("label2", "answer2", "labelArg2")
                ),
                Some("trusteeSectionKey2"))
            )
          ),
          protectors = None,
          otherIndividuals = None,
          trustDetails = None,
          settlors = None,
          assets = None
        )

        when(mockConnector.getAnswerSections(any())(any(), any())).thenReturn(Future.successful(answerSections))

        val result = Await.result(repository.getAnswerSections(draftId), Duration.Inf)

        val expectedBeneficiaries = Some(List(
          AnswerSection(
            Some("headingKey1"),
            List(
              AnswerRow("label1", HtmlFormat.raw("answer1"), None, "labelArg1", canEdit = false)
            ),
            Some("sectionKey1")
          ),
          AnswerSection(
            Some("headingKey2"),
            List(
              AnswerRow("label2", HtmlFormat.raw("answer2"), None, "labelArg2", canEdit = false)
            ),
            Some("sectionKey2")
          )
        ))

        val expectedTrustees = Some(List(
          AnswerSection(
            Some("trusteeHeadingKey1"),
            List(
              AnswerRow("label1", HtmlFormat.raw("answer1"), None, "labelArg1", canEdit = false)
            ),
            Some("trusteeSectionKey1")
          ),
          AnswerSection(
            Some("trusteeHeadingKey2"),
            List(
              AnswerRow("label2", HtmlFormat.raw("answer2"), None, "labelArg2", canEdit = false)
            ),
            Some("trusteeSectionKey2")
          )
        ))

        result mustBe RegistrationAnswerSections(
          beneficiaries = expectedBeneficiaries,
          trustees = expectedTrustees
        )
        verify(mockConnector).getAnswerSections(draftId)(hc, executionContext)
      }
    }

    "reading lead trustee" must {

      "read existing lead trustee from connector" in {

        implicit lazy val hc: HeaderCarrier = HeaderCarrier()

        val draftId = "DraftId"

        val mockConnector = mock[SubmissionDraftConnector]

        val repository = createRepository(mockConnector)

        val leadTrusteeOrg = LeadTrusteeType(
          None,
          Some(LeadTrusteeOrgType(
            "Lead Org",
            "07911234567",
            None,
            IdentificationOrgType(None, Some(AddressType("line1", "line2", None, None, Some("AA1 1AA"), "GB")))))
        )

        when(mockConnector.getLeadTrustee(any())(any(), any())).thenReturn(Future.successful(leadTrusteeOrg))

        val result = Await.result(repository.getLeadTrustee(draftId), Duration.Inf)

        result mustBe leadTrusteeOrg

        verify(mockConnector).getLeadTrustee(draftId)(hc, executionContext)
      }
    }

    "reading correspondence address" must {

      "read existing correspondence address from connector" in {

        implicit lazy val hc: HeaderCarrier = HeaderCarrier()

        val draftId = "DraftId"

        val mockConnector = mock[SubmissionDraftConnector]

        val repository = createRepository(mockConnector)

        val correspondenceAddress = AddressType("line1", "line2", None, None, Some("AA1 1AA"), "GB")

        when(mockConnector.getCorrespondenceAddress(any())(any(), any())).thenReturn(Future.successful(correspondenceAddress))

        val result = Await.result(repository.getCorrespondenceAddress(draftId), Duration.Inf)

        result mustBe correspondenceAddress

        verify(mockConnector).getCorrespondenceAddress(draftId)(hc, executionContext)
      }
    }

    "reading agent address" when {

      "agent details microservice enabled" must {
        "read agent address from connector" in {

          val mockConnector = mock[SubmissionDraftConnector]

          val fakeFrontendAppConfig: FrontendAppConfig = {
            lazy val config: Configuration = injector.instanceOf[FrontendAppConfig].configuration
            new FrontendAppConfig(config) {
              override lazy val agentDetailsMicroserviceEnabled: Boolean = true
            }
          }

          val repository = createRepository(mockConnector, fakeFrontendAppConfig)

          val agentAddress = AddressType("line1", "line2", None, None, Some("AA1 1AA"), "GB")

          when(mockConnector.getAgentAddress(any())(any(), any())).thenReturn(Future.successful(agentAddress))

          val result = Await.result(repository.getAgentAddress(emptyUserAnswers), Duration.Inf).value

          result mustBe agentAddress

          verify(mockConnector, times(1)).getAgentAddress(any())(any(), any())
        }
      }

      "agent details microservice not enabled" must {
        "read agent address from UserAnswers" in {

          val mockConnector = mock[SubmissionDraftConnector]

          val fakeFrontendAppConfig: FrontendAppConfig = {
            lazy val config: Configuration = injector.instanceOf[FrontendAppConfig].configuration
            new FrontendAppConfig(config) {
              override lazy val agentDetailsMicroserviceEnabled: Boolean = false
            }
          }

          val repository = createRepository(mockConnector, fakeFrontendAppConfig)

          val userAnswers: UserAnswers = emptyUserAnswers
            .set(AgentAddressYesNoPage, true).success.value
            .set(AgentUKAddressPage, UKAddress("Line 1", "Line 2", None, None, "AB1 1AB")).success.value

          val result = Await.result(repository.getAgentAddress(userAnswers), Duration.Inf).value

          result mustBe AddressType("Line 1", "Line 2", None, None, Some("AB1 1AB"), "GB")

          verify(mockConnector, times(0)).getAgentAddress(any())(any(), any())
        }
      }
    }

    "reading client reference" when {

      val clientRef = "client-ref"

      "agent details microservice enabled" must {
        "read client referebce from connector" in {

          val mockConnector = mock[SubmissionDraftConnector]

          val fakeFrontendAppConfig: FrontendAppConfig = {
            lazy val config: Configuration = injector.instanceOf[FrontendAppConfig].configuration
            new FrontendAppConfig(config) {
              override lazy val agentDetailsMicroserviceEnabled: Boolean = true
            }
          }

          val repository = createRepository(mockConnector, fakeFrontendAppConfig)

          when(mockConnector.getClientReference(any())(any(), any())).thenReturn(Future.successful(clientRef))

          val result = Await.result(repository.getClientReference(emptyUserAnswers), Duration.Inf).value

          result mustBe clientRef

          verify(mockConnector, times(1)).getClientReference(any())(any(), any())
        }
      }

      "agent details microservice not enabled" must {
        "read client reference from UserAnswers" in {

          val mockConnector = mock[SubmissionDraftConnector]

          val fakeFrontendAppConfig: FrontendAppConfig = {
            lazy val config: Configuration = injector.instanceOf[FrontendAppConfig].configuration
            new FrontendAppConfig(config) {
              override lazy val agentDetailsMicroserviceEnabled: Boolean = false
            }
          }

          val repository = createRepository(mockConnector, fakeFrontendAppConfig)

          val userAnswers: UserAnswers = emptyUserAnswers
            .set(AgentInternalReferencePage, clientRef).success.value

          val result = Await.result(repository.getClientReference(userAnswers), Duration.Inf).value

          result mustBe clientRef

          verify(mockConnector, times(0)).getClientReference(any())(any(), any())
        }
      }
    }

    "reading when trust setup date" must {

      "read existing date from connector" in {

        implicit lazy val hc: HeaderCarrier = HeaderCarrier()

        val draftId = "DraftId"

        val mockConnector = mock[SubmissionDraftConnector]

        val repository = createRepository(mockConnector)

        val expected = LocalDate.parse("2020-10-10")

        when(mockConnector.getTrustSetupDate(any())(any(), any())).thenReturn(Future.successful(Some(LocalDate.parse("2020-10-10"))))

        val result = Await.result(repository.getTrustSetupDate(draftId), Duration.Inf)

        result.get mustBe expected

        verify(mockConnector).getTrustSetupDate(draftId)(hc, executionContext)
      }
    }

    "getting draft" must {

      val drafts = List(
        SubmissionDraftId(
          "draft1",
          LocalDateTime.of(2012, 2, 1, 12, 30, 0),
          Some("reference1")
        ),
        SubmissionDraftId(
          "draft2",
          LocalDateTime.of(2012, 2, 1, 12, 30, 0),
          Some("reference2")
        )
      )

      val mockConnector = mock[SubmissionDraftConnector]
      val repository = createRepository(mockConnector)
      when(mockConnector.getCurrentDraftIds()(any(), any())).thenReturn(Future.successful(drafts))

      "return draft from list of current drafts if it exists" in {

        val result1 = Await.result(repository.getDraft("draft1")(any(), any()), Duration.Inf)
        result1 mustBe Some(DraftRegistration("draft1", "reference1", "4 February 2012"))

        val result2 = Await.result(repository.getDraft("draft2")(any(), any()), Duration.Inf)
        result2 mustBe Some(DraftRegistration("draft2", "reference2", "4 February 2012"))
      }

      "return None if draft is not found" in {

        val result1 = Await.result(repository.getDraft("draft3")(any(), any()), Duration.Inf)
        result1 mustBe None
      }
    }

    "removing draft" must {
      "get response from connector" in {
        implicit lazy val hc: HeaderCarrier = HeaderCarrier()

        val mockConnector = mock[SubmissionDraftConnector]

        val repository = createRepository(mockConnector)

        val draftId: String = "draftId"

        val status: Int = 200

        when(mockConnector.removeDraft(any())(any(), any())).thenReturn(Future.successful(HttpResponse(status, "")))

        val result = Await.result(repository.removeDraft(draftId), Duration.Inf)

        result.status mustBe status

        verify(mockConnector).removeDraft(draftId)(hc, executionContext)
      }
    }
  }
}
