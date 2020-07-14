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

import java.time.LocalDateTime

import config.FrontendAppConfig
import connector.SubmissionDraftConnector
import models.RegistrationSubmission.{AllAnswerSections, AllStatus}
import models._
import models.registration.pages.Status.{Completed, InProgress}
import org.mockito.Matchers.any
import org.mockito.Mockito.{verify, when}
import org.scalatest.MustMatchers
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.http
import play.api.libs.json.Json
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}
import utils.TrustsDateFormatter
import viewmodels.{AnswerRow, AnswerSection, DraftRegistration, RegistrationAnswerSections}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

class RegistrationRepositorySpec extends PlaySpec with MustMatchers with MockitoSugar {

  private implicit val executionContext: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global

  private def createRepository(mockConnector: SubmissionDraftConnector) = {
    val mockConfig = mock[FrontendAppConfig]
    when(mockConfig.ttlInSeconds).thenReturn(60*60*24*3)   // 3 days

    new DefaultRegistrationsRepository(new TrustsDateFormatter(mockConfig), mockConnector)
  }

  "RegistrationRepository" when {
    "getting user answers" must {
      "read answers from main section" in {
        implicit lazy val hc: HeaderCarrier = HeaderCarrier()

        val draftId = "DraftId"

        val userAnswers = models.core.UserAnswers(draftId = draftId, internalAuthId = "internalAuthId")

        val mockConnector = mock[SubmissionDraftConnector]

        val repository = createRepository(mockConnector)

        val response = SubmissionDraftResponse(LocalDateTime.now, Json.toJson(userAnswers), None)

        when(mockConnector.getDraftMain(any())(any(), any())).thenReturn(Future.successful(response))

        val result = Await.result(repository.get(draftId), Duration.Inf)

        result mustBe Some(userAnswers)
        verify(mockConnector).getDraftMain(draftId)(hc, executionContext)
      }
    }

    "listing drafts" must {
      "transforms received from connector" in {
        implicit lazy val hc: HeaderCarrier = HeaderCarrier()

        val draftId = "DraftId"

        val userAnswers = models.core.UserAnswers(draftId = draftId, internalAuthId = "internalAuthId")

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

        val response = SubmissionDraftResponse(LocalDateTime.now, Json.toJson(userAnswers), None)

        when(mockConnector.getCurrentDraftIds()(any(), any())).thenReturn(Future.successful(drafts))

        val result = Await.result(repository.listDrafts(), Duration.Inf)

        result mustBe List(
          DraftRegistration("draft1", "reference1", "4 February 2012"),
          DraftRegistration("draft2", "reference2", "5 January 2011")
        )
        verify(mockConnector).getCurrentDraftIds()(hc, executionContext)
      }
    }

    "setting user answers" must {
      "write answers to main section" in {
        implicit lazy val hc: HeaderCarrier = HeaderCarrier()

        val draftId = "DraftId"

        val userAnswers = models.core.UserAnswers(draftId = draftId, internalAuthId = "internalAuthId")

        val mockConnector = mock[SubmissionDraftConnector]

        val repository = createRepository(mockConnector)

        when(mockConnector.setDraftMain(any(), any(), any(), any())(any(), any())).thenReturn(Future.successful(HttpResponse(http.Status.OK)))

        val result = Await.result(repository.set(userAnswers), Duration.Inf)

        result mustBe true
        verify(mockConnector).setDraftMain(draftId, Json.toJson(userAnswers), inProgress = true, None)(hc, executionContext)
      }
    }


    "adding a registration section" must {

      "combine into empty sections" in {

        implicit lazy val hc: HeaderCarrier = HeaderCarrier()

        val draftId = "DraftId"

        val mockConnector = mock[SubmissionDraftConnector]

        val repository = createRepository(mockConnector)

        val registrationSectionsData = Json.obj(
          "field/subfield" -> Json.parse(
          """
            |{
            |   "dataField": "newData"
            |}
            |""".stripMargin))

        when(mockConnector.getRegistrationPieces(any())(any(), any())).thenReturn(Future.successful(registrationSectionsData))

        val currentRegistrationJson = Json.parse(
          """
            |{
            | "existingObject": {
            |   "existingField": "existingValue"
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
            |     "dataField": "newData"
            |   }
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

        when(mockConnector.setStatus(any(), any())(any(), any())).thenReturn(Future.successful(HttpResponse(http.Status.OK)))

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
          )
        )

        when(mockConnector.getAnswerSections(any())(any(), any())).thenReturn(Future.successful(answerSections))

        val result = Await.result(repository.getAnswerSections(draftId), Duration.Inf)

        val expected = Some(List(
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

        result mustBe RegistrationAnswerSections(beneficiaries = expected)
        verify(mockConnector).getAnswerSections(draftId)(hc, executionContext)
      }
    }
  }
}
