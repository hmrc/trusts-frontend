/*
 * Copyright 2022 HM Revenue & Customs
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
import connector.SubmissionDraftConnector
import models.RegistrationSubmission.AllAnswerSections
import models._
import models.core.UserAnswers
import models.core.http.{AddressType, IdentificationOrgType, LeadTrusteeOrgType, LeadTrusteeType}
import org.mockito.Matchers.any
import org.mockito.Mockito.{never, times, verify, when}
import org.scalatest.MustMatchers
import org.scalatestplus.mockito.MockitoSugar
import play.api.libs.json.{JsArray, Json}
import play.api.test.Helpers.OK
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.auth.core.AffinityGroup
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}
import utils.{AnswerRowUtils, DateFormatter}
import viewmodels.{AnswerRow, AnswerSection, DraftRegistration, RegistrationAnswerSections}

import java.time.LocalDateTime
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

class RegistrationsRepositorySpec extends RegistrationSpecBase with MustMatchers with MockitoSugar {

  private implicit lazy val hc: HeaderCarrier = HeaderCarrier()

  private val userAnswersDateTime = LocalDateTime.of(2020, 2, 24, 13, 34, 0)

  private def createRepository(mockConnector: SubmissionDraftConnector) = {
    val mockDateFormatter: DateFormatter = mock[DateFormatter]
    when(mockDateFormatter.savedUntil(any())(any())).thenReturn("4 February 2012")

    implicit val answerRowUtils: AnswerRowUtils = injector.instanceOf[AnswerRowUtils]
    new DefaultRegistrationsRepository(mockDateFormatter, mockConnector)
  }

  "RegistrationRepository" when {
    "getting user answers" must {
      "read answers from main section" in {
        implicit lazy val hc: HeaderCarrier = HeaderCarrier()

        val userAnswers = models.core.UserAnswers(draftId = fakeDraftId, internalAuthId = "internalAuthId", createdAt = userAnswersDateTime)

        val mockConnector = mock[SubmissionDraftConnector]

        val repository = createRepository(mockConnector)

        val response = SubmissionDraftResponse(LocalDateTime.now, Json.toJson(userAnswers), None)

        when(mockConnector.getDraftMain(any())(any(), any())).thenReturn(Future.successful(response))

        val result = Await.result(repository.get(fakeDraftId), Duration.Inf)

        result mustBe Some(userAnswers)
        verify(mockConnector).getDraftMain(fakeDraftId)(hc, executionContext)
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

    "setting user answers" when {

      val baseAnswers = UserAnswers(draftId = fakeDraftId, internalAuthId = "internalAuthId", createdAt = userAnswersDateTime)
      val clientRef = "client-ref"

      "there is a client reference" must {
        "write answers to main section" in {

          val mockConnector = mock[SubmissionDraftConnector]

          val repository = createRepository(mockConnector)

          when(mockConnector.getClientReference(any())(any(), any())).thenReturn(Future.successful(clientRef))
          when(mockConnector.setDraftMain(any(), any(), any(), any())(any(), any())).thenReturn(Future.successful(HttpResponse(OK, "")))

          val result = Await.result(repository.set(baseAnswers, AffinityGroup.Agent), Duration.Inf)

          result mustBe true
          verify(mockConnector).setDraftMain(fakeDraftId, Json.toJson(baseAnswers), inProgress = false, Some(clientRef))(hc, executionContext)
        }
      }

      "there is not a client reference" must {
        "write answers to main section" in {

          val mockConnector = mock[SubmissionDraftConnector]

          val repository = createRepository(mockConnector)

          when(mockConnector.getClientReference(any())(any(), any())).thenReturn(Future.failed(new Throwable("no client ref found")))
          when(mockConnector.setDraftMain(any(), any(), any(), any())(any(), any())).thenReturn(Future.successful(HttpResponse(OK, "")))

          val result = Await.result(repository.set(baseAnswers, AffinityGroup.Agent), Duration.Inf)

          result mustBe true
          verify(mockConnector).setDraftMain(fakeDraftId, Json.toJson(baseAnswers), inProgress = false, None)(hc, executionContext)
        }
      }
    }

    "adding a registration section" must {

      "combine into empty sections" in {

        implicit lazy val hc: HeaderCarrier = HeaderCarrier()

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


        val result = Await.result(repository.addDraftRegistrationSections(fakeDraftId, currentRegistrationJson), Duration.Inf)

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
        verify(mockConnector).getRegistrationPieces(fakeDraftId)(hc, executionContext)
      }
    }

    "reading answer sections" must {
      "return deserialised answer sections" in {
        implicit lazy val hc: HeaderCarrier = HeaderCarrier()

        val mockConnector = mock[SubmissionDraftConnector]

        val repository = createRepository(mockConnector)

        val answerSections = AllAnswerSections(
          beneficiaries = Some(
            List(
              RegistrationSubmission.AnswerSection(
                headingKey = Some("headingKey1"),
                headingArgs = Nil,
                rows = List(
                  RegistrationSubmission.AnswerRow("label1", "answer1", Seq("labelArg1"))
                ),
                sectionKey = Some("sectionKey1")),
              RegistrationSubmission.AnswerSection(
                headingKey = Some("headingKey2"),
                headingArgs = Nil,
                rows = List(
                  RegistrationSubmission.AnswerRow("label2", "answer2", Seq("labelArg2"))
                ),
                sectionKey = Some("sectionKey2"))
            )
          ),
          trustees = Some(
            List(
              RegistrationSubmission.AnswerSection(
                headingKey = Some("trusteeHeadingKey1"),
                headingArgs = Nil,
                rows = List(
                  RegistrationSubmission.AnswerRow("label1", "answer1", Seq("labelArg1"))
                ),
                sectionKey = Some("trusteeSectionKey1")),
              RegistrationSubmission.AnswerSection(
                headingKey = Some("trusteeHeadingKey2"),
                headingArgs = Nil,
                rows = List(
                  RegistrationSubmission.AnswerRow("label2", "answer2", Seq("labelArg2"))
                ),
                sectionKey = Some("trusteeSectionKey2"))
            )
          ),
          protectors = None,
          otherIndividuals = None,
          trustDetails = None,
          settlors = None,
          assets = None
        )

        when(mockConnector.getAnswerSections(any())(any(), any())).thenReturn(Future.successful(answerSections))

        val result = Await.result(repository.getAnswerSections(fakeDraftId), Duration.Inf)

        val expectedBeneficiaries = Some(List(
          AnswerSection(
            Some("headingKey1"),
            List(
              AnswerRow("label1", HtmlFormat.raw("answer1"), None, Seq("labelArg1"), canEdit = false)
            ),
            Some("sectionKey1")
          ),
          AnswerSection(
            Some("headingKey2"),
            List(
              AnswerRow("label2", HtmlFormat.raw("answer2"), None, Seq("labelArg2"), canEdit = false)
            ),
            Some("sectionKey2")
          )
        ))

        val expectedTrustees = Some(List(
          AnswerSection(
            Some("trusteeHeadingKey1"),
            List(
              AnswerRow("label1", HtmlFormat.raw("answer1"), None, Seq("labelArg1"), canEdit = false)
            ),
            Some("trusteeSectionKey1")
          ),
          AnswerSection(
            Some("trusteeHeadingKey2"),
            List(
              AnswerRow("label2", HtmlFormat.raw("answer2"), None, Seq("labelArg2"), canEdit = false)
            ),
            Some("trusteeSectionKey2")
          )
        ))

        result mustBe RegistrationAnswerSections(
          beneficiaries = expectedBeneficiaries,
          trustees = expectedTrustees
        )
        verify(mockConnector).getAnswerSections(fakeDraftId)(hc, executionContext)
      }
    }

    "reading lead trustee" must {

      "read existing lead trustee from connector" in {

        implicit lazy val hc: HeaderCarrier = HeaderCarrier()

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

        val result = Await.result(repository.getLeadTrustee(fakeDraftId), Duration.Inf)

        result mustBe leadTrusteeOrg

        verify(mockConnector).getLeadTrustee(fakeDraftId)(hc, executionContext)
      }
    }

    "reading correspondence address" must {

      "read existing correspondence address from connector" in {

        implicit lazy val hc: HeaderCarrier = HeaderCarrier()

        val mockConnector = mock[SubmissionDraftConnector]

        val repository = createRepository(mockConnector)

        val correspondenceAddress = AddressType("line1", "line2", None, None, Some("AA1 1AA"), "GB")

        when(mockConnector.getCorrespondenceAddress(any())(any(), any())).thenReturn(Future.successful(correspondenceAddress))

        val result = Await.result(repository.getCorrespondenceAddress(fakeDraftId), Duration.Inf)

        result mustBe correspondenceAddress

        verify(mockConnector).getCorrespondenceAddress(fakeDraftId)(hc, executionContext)
      }
    }

    "reading agent address" when {

      "successful call" must {
        "return Some(agent address)" in {

          val mockConnector = mock[SubmissionDraftConnector]

          val repository = createRepository(mockConnector)

          val agentAddress = AddressType("line1", "line2", None, None, Some("AA1 1AA"), "GB")

          when(mockConnector.getAgentAddress(any())(any(), any())).thenReturn(Future.successful(agentAddress))

          val result = Await.result(repository.getAgentAddress(fakeDraftId), Duration.Inf)

          result mustBe Some(agentAddress)

          verify(mockConnector, times(1)).getAgentAddress(any())(any(), any())
        }
      }

      "unsuccessful call" must {
        "return None" in {

          val mockConnector = mock[SubmissionDraftConnector]

          val repository = createRepository(mockConnector)

          when(mockConnector.getAgentAddress(any())(any(), any())).thenReturn(Future.failed(new Throwable("agent address not found")))

          val result = Await.result(repository.getAgentAddress(fakeDraftId), Duration.Inf)

          result mustBe None

          verify(mockConnector, times(1)).getAgentAddress(any())(any(), any())
        }
      }
    }

    "reading client reference" when {

      val clientRef = "client-ref"

      "not an agent" must {

        "not attempt the call" in {
          val mockConnector = mock[SubmissionDraftConnector]

          val repository = createRepository(mockConnector)

          val result = Await.result(repository.getClientReference(fakeDraftId, AffinityGroup.Organisation), Duration.Inf)

          result mustBe None

          verify(mockConnector, never()).getClientReference(any())(any(), any())
        }

      }

      "an agent" must {

        "successful call" must {
          "return Some(client reference)" in {

            val mockConnector = mock[SubmissionDraftConnector]

            val repository = createRepository(mockConnector)

            when(mockConnector.getClientReference(any())(any(), any())).thenReturn(Future.successful(clientRef))

            val result = Await.result(repository.getClientReference(fakeDraftId, AffinityGroup.Agent), Duration.Inf)

            result mustBe Some(clientRef)

            verify(mockConnector, times(1)).getClientReference(any())(any(), any())
          }
        }

        "unsuccessful call" must {
          "return None" in {

            val mockConnector = mock[SubmissionDraftConnector]

            val repository = createRepository(mockConnector)

            when(mockConnector.getClientReference(any())(any(), any())).thenReturn(Future.failed(new Throwable("client ref not found")))

            val result = Await.result(repository.getClientReference(fakeDraftId, AffinityGroup.Agent), Duration.Inf)

            result mustBe None

            verify(mockConnector, times(1)).getClientReference(any())(any(), any())
          }
        }
      }


    }

    "removing draft" must {
      "get response from connector" in {
        implicit lazy val hc: HeaderCarrier = HeaderCarrier()

        val mockConnector = mock[SubmissionDraftConnector]

        val repository = createRepository(mockConnector)

        val status: Int = 200

        when(mockConnector.removeDraft(any())(any(), any())).thenReturn(Future.successful(HttpResponse(status, "")))

        val result = Await.result(repository.removeDraft(fakeDraftId), Duration.Inf)

        result.status mustBe status

        verify(mockConnector).removeDraft(fakeDraftId)(hc, executionContext)
      }
    }
  }
}
