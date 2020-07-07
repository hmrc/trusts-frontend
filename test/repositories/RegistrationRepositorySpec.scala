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
import models._
import models.registration.pages.Status.InProgress
import org.mockito.Matchers.any
import org.mockito.Mockito.{verify, when}
import org.scalatest.MustMatchers
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.Json
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.http.HeaderCarrier
import utils.TrustsDateFormatter
import viewmodels.{AnswerRow, AnswerSection, RegistrationAnswerSections}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

class RegistrationRepositorySpec extends PlaySpec with MustMatchers with MockitoSugar {

  private implicit val executionContext: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global

  private def createRepository(mockConnector: SubmissionDraftConnector) = {
    val mockConfig = mock[FrontendAppConfig]
    when(mockConfig.ttlInSeconds).thenReturn(1200)

    new DefaultRegistrationsRepository(new TrustsDateFormatter(mockConfig), mockConnector)
  }

  "RegistrationRepository" when {

    "adding a registration section" must {

      "combine into empty sections" in {

        implicit lazy val hc: HeaderCarrier = HeaderCarrier()

        val draftId = "DraftId"

        val mockConnector = mock[SubmissionDraftConnector]

        val repository = createRepository(mockConnector)

        val registrationSectionsData = Json.parse(
          """
            |{
            | "field/subfield": {
            |   "dataField": "newData"
            | }
            |}
            |""".stripMargin)
        val existingSubmissionResponse = SubmissionDraftResponse(LocalDateTime.now(), registrationSectionsData, None)

        when(mockConnector.getDraftSection(any(), any())(any(), any())).thenReturn(Future.successful(existingSubmissionResponse))

        val currentRegistrationJson = Json.parse(
          """
            |{
            | "existingObject": {
            |   "existingField": "existingValue"
            | }
            |}
            |""".stripMargin)


        val result = Await.result(repository.addDraftRegistrationSections(draftId, currentRegistrationJson), Duration.Inf)

        val expectedRegistrationJson = Json.parse(
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

        result mustBe expectedRegistrationJson
        verify(mockConnector).getDraftSection(draftId, "registration")(hc, executionContext)
      }
    }
    "reading section status" must {

      "read existing status when there isn't any" in {

        implicit lazy val hc: HeaderCarrier = HeaderCarrier()

        val draftId = "DraftId"

        val mockConnector = mock[SubmissionDraftConnector]

        val repository = createRepository(mockConnector)

        val statusData = Json.parse(
          """
            |{
            |}
            |""".stripMargin)
        val existingStatusResponse = SubmissionDraftResponse(LocalDateTime.now(), statusData, None)

        when(mockConnector.getDraftSection(any(), any())(any(), any())).thenReturn(Future.successful(existingStatusResponse))

        val result = Await.result(repository.getSectionStatus(draftId, "assets"), Duration.Inf)

        result mustBe None
      }
      "read existing status when there is status" in {

        implicit lazy val hc: HeaderCarrier = HeaderCarrier()

        val draftId = "DraftId"

        val mockConnector = mock[SubmissionDraftConnector]

        val repository = createRepository(mockConnector)

        val statusData = Json.parse(
          """
            |{
            | "assets": "progress"
            |}
            |""".stripMargin)

        val existingStatusResponse = SubmissionDraftResponse(LocalDateTime.now(), statusData, None)

        when(mockConnector.getDraftSection(any(), any())(any(), any())).thenReturn(Future.successful(existingStatusResponse))

        val result = Await.result(repository.getSectionStatus(draftId, "assets"), Duration.Inf)

        result mustBe Some(InProgress)
        verify(mockConnector).getDraftSection(draftId, "status")(hc, executionContext)
      }
    }
    "reading answer sections" must {
      "return empty answer sections if there are none" in {
        implicit lazy val hc: HeaderCarrier = HeaderCarrier()

        val draftId = "DraftId"

        val mockConnector = mock[SubmissionDraftConnector]

        val repository = createRepository(mockConnector)

        val answerSectionsResponse = SubmissionDraftResponse(LocalDateTime.now(), Json.obj(), None)

        when(mockConnector.getDraftSection(any(), any())(any(), any())).thenReturn(Future.successful(answerSectionsResponse))

        val result = Await.result(repository.getAnswerSections(draftId), Duration.Inf)

        result mustBe RegistrationAnswerSections(beneficiaries = None)
        verify(mockConnector).getDraftSection(draftId, "answerSections")(hc, executionContext)
      }
    }
    "return deserialised answer sections if there are some" in {
      implicit lazy val hc: HeaderCarrier = HeaderCarrier()

      val draftId = "DraftId"

      val mockConnector = mock[SubmissionDraftConnector]

      val repository = createRepository(mockConnector)

      val answerSectionsJson = Json.parse(
        """
          |{
          | "beneficiaries": [
          |   {
          |     "headingKey": "headingKey1",
          |     "rows": [
          |       {
          |         "label": "label1",
          |         "answer": "answer1",
          |         "labelArg": "labelArg1"
          |       }
          |     ],
          |     "sectionKey": "sectionKey1"
          |   },
          |   {
          |     "headingKey": "headingKey2",
          |     "rows": [
          |       {
          |         "label": "label2",
          |         "answer": "answer2",
          |         "labelArg": "labelArg2"
          |       }
          |     ],
          |     "sectionKey": "sectionKey2"
          |   }
          | ]
          |}
          |""".stripMargin)

      val answerSectionsResponse = SubmissionDraftResponse(LocalDateTime.now(), answerSectionsJson, None)

      when(mockConnector.getDraftSection(any(), any())(any(), any())).thenReturn(Future.successful(answerSectionsResponse))

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
      verify(mockConnector).getDraftSection(draftId, "answerSections")(hc, executionContext)
    }
  }
}
