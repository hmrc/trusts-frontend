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
import uk.gov.hmrc.http.HeaderCarrier
import utils.TrustsDateFormatter

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

class RegistrationRepositorySpec extends PlaySpec with MustMatchers with MockitoSugar {

  private implicit val executionContext: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global

  "RegistrationRepository" when {

    "adding a registration section" must {

      "combine into empty sections" in {

        implicit lazy val hc: HeaderCarrier = HeaderCarrier()

        val draftId = "DraftId"

        val mockConnector = mock[SubmissionDraftConnector]

        val mockConfig = mock[FrontendAppConfig]
        when(mockConfig.ttlInSeconds).thenReturn(1200)

        val repository = new DefaultRegistrationsRepository(new TrustsDateFormatter(mockConfig), mockConnector)

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

        val mockConfig = mock[FrontendAppConfig]
        when(mockConfig.ttlInSeconds).thenReturn(1200)

        val repository = new DefaultRegistrationsRepository(new TrustsDateFormatter(mockConfig), mockConnector)

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

        val mockConfig = mock[FrontendAppConfig]
        when(mockConfig.ttlInSeconds).thenReturn(1200)

        val repository = new DefaultRegistrationsRepository(new TrustsDateFormatter(mockConfig), mockConnector)

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

  }
}
