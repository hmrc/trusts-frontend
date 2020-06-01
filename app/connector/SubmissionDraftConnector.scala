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
import models.{SubmissionDraftId, SubmissionDraftResponse}
import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}
import uk.gov.hmrc.play.bootstrap.http.HttpClient

import scala.concurrent.{ExecutionContext, Future}

class SubmissionDraftConnector @Inject()(http: HttpClient, config : FrontendAppConfig) {

  val submissionsBaseUrl = s"${config.trustsUrl}/trusts/register/submission-drafts"

  def setDraft(draftId : String, section: String, draftData: JsValue)(implicit hc: HeaderCarrier, ec : ExecutionContext): Future[HttpResponse] = {
    val url = s"$submissionsBaseUrl/$draftId/$section"
    http.POST[JsValue, HttpResponse](url, Json.toJson(draftData))
  }

  def getDraft(draftId: String, section: String)(implicit hc: HeaderCarrier, ec : ExecutionContext): Future[SubmissionDraftResponse] = {
    http.GET[SubmissionDraftResponse](s"$submissionsBaseUrl/$draftId/$section")
  }

  def getCurrentDraftIds()(implicit hc: HeaderCarrier, ec : ExecutionContext): Future[List[SubmissionDraftId]] = {
    http.GET[List[SubmissionDraftId]](s"$submissionsBaseUrl")
  }

  def deleteDraft(draftId: String)(implicit hc: HeaderCarrier, ec : ExecutionContext): Future[HttpResponse] = {
    http.DELETE[HttpResponse](s"$submissionsBaseUrl/$draftId")
  }
}
