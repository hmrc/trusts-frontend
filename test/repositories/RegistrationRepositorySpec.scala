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

import config.FrontendAppConfig
import connector.SubmissionDraftConnector
import models.RegistrationSubmission.{AllAnswerSections, AllStatus}
import models._
import models.registration.pages.Status.Completed
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
    "reading status got sections" must {

      "read existing status when there isn't any" in {

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
