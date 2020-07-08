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

package connector

import config.FrontendAppConfig
import javax.inject.Inject
import models.RegistrationSubmission.{AllAnswerSections, AllStatus}
import models.{SubmissionDraftData, SubmissionDraftId, SubmissionDraftResponse}
import play.api.libs.json.{JsObject, JsValue, Json}
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}
import uk.gov.hmrc.play.bootstrap.http.HttpClient

import scala.concurrent.{ExecutionContext, Future}

class SubmissionDraftConnector @Inject()(http: HttpClient, config : FrontendAppConfig) {
  private val mainSection = "main"
  private val registrationSection = "registration"
  private val statusSection = "status"
  private val answerSectionsSection = "answerSections"

  private val submissionsBaseUrl = s"${config.trustsUrl}/trusts/register/submission-drafts"

  private def getDraftSection(draftId: String, section: String)(implicit hc: HeaderCarrier, ec : ExecutionContext): Future[SubmissionDraftResponse] = {
    http.GET[SubmissionDraftResponse](s"$submissionsBaseUrl/$draftId/$section")
  }

  def setDraftMain(draftId : String, draftData: JsValue, inProgress: Boolean, reference: Option[String])
                     (implicit hc: HeaderCarrier, ec : ExecutionContext): Future[HttpResponse] = {
    val submissionDraftData = SubmissionDraftData(draftData, reference, Some(inProgress))
    http.POST[JsValue, HttpResponse](s"$submissionsBaseUrl/$draftId/$mainSection", Json.toJson(submissionDraftData))
  }

  def getDraftMain(draftId: String)(implicit hc: HeaderCarrier, ec : ExecutionContext): Future[SubmissionDraftResponse] =
    getDraftSection(draftId, mainSection)

  def getCurrentDraftIds()(implicit hc: HeaderCarrier, ec : ExecutionContext): Future[List[SubmissionDraftId]] = {
    http.GET[List[SubmissionDraftId]](s"$submissionsBaseUrl")
  }

  def getStatus(draftId: String)(implicit hc: HeaderCarrier, ec : ExecutionContext): Future[AllStatus] =
    getDraftSection(draftId, statusSection).map {
      section => section.data.as[AllStatus]
    }

  def getRegistrationPieces(draftId: String)(implicit hc: HeaderCarrier, ec : ExecutionContext): Future[JsObject] =
    getDraftSection(draftId, registrationSection).map {
      section => section.data.as[JsObject]
    }

  def getAnswerSections(draftId: String)(implicit hc: HeaderCarrier, ec : ExecutionContext): Future[AllAnswerSections] =
    getDraftSection(draftId, answerSectionsSection).map {
      section => section.data.as[AllAnswerSections]
    }
}
