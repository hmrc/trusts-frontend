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
import models.RegistrationSubmission.{AllAnswerSections, AllStatus}
import models.core.http.{AddressType, LeadTrusteeType}
import models.{SubmissionDraftData, SubmissionDraftId, SubmissionDraftResponse}
import play.api.libs.json.{JsObject, JsValue, Json}
import uk.gov.hmrc.http.HttpReads.Implicits.{readFromJson, readRaw}
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpResponse}

import java.time.LocalDate
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class SubmissionDraftConnector @Inject()(http: HttpClient, config: FrontendAppConfig) {

  private val mainSection = "main"
  private val beneficiariesSection = "beneficiaries"
  private val registrationSection = "registration"
  private val statusSection = "status"
  private val answerSectionsSection = "answerSections"

  private val submissionsBaseUrl = s"${config.trustsUrl}/trusts/register/submission-drafts"

  private def getDraftSection(draftId: String, section: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[SubmissionDraftResponse] = {
    http.GET[SubmissionDraftResponse](s"$submissionsBaseUrl/$draftId/$section")
  }

  def setDraftMain(draftId: String, draftData: JsValue, inProgress: Boolean, reference: Option[String])
                  (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {
    val submissionDraftData = SubmissionDraftData(draftData, reference, Some(inProgress))
    http.POST[JsValue, HttpResponse](s"$submissionsBaseUrl/$draftId/$mainSection", Json.toJson(submissionDraftData))
  }

  def getDraftMain(draftId: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[SubmissionDraftResponse] =
    getDraftSection(draftId, mainSection)

  def getDraftBeneficiaries(draftId: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[SubmissionDraftResponse] =
    getDraftSection(draftId, beneficiariesSection)

  def getCurrentDraftIds()(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[List[SubmissionDraftId]] = {
    http.GET[List[SubmissionDraftId]](s"$submissionsBaseUrl")
  }

  def getStatus(draftId: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[AllStatus] =
    getDraftSection(draftId, statusSection).map {
      section => section.data.as[AllStatus]
    }

  def setStatus(draftId: String, status: AllStatus)
               (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {
    val submissionDraftData = SubmissionDraftData(Json.toJson(status), None, None)
    http.POST[JsValue, HttpResponse](s"$submissionsBaseUrl/$draftId/status", Json.toJson(submissionDraftData))
  }

  def getRegistrationPieces(draftId: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[JsObject] =
    getDraftSection(draftId, registrationSection).map {
      section => section.data.as[JsObject]
    }

  def getAnswerSections(draftId: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[AllAnswerSections] =
    getDraftSection(draftId, answerSectionsSection).map {
      section => section.data.as[AllAnswerSections]
    }

  def getLeadTrustee(draftId: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[LeadTrusteeType] =
    http.GET[LeadTrusteeType](s"$submissionsBaseUrl/$draftId/lead-trustee")

  def getCorrespondenceAddress(draftId: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[AddressType] =
    http.GET[AddressType](s"$submissionsBaseUrl/$draftId/correspondence-address")

  def getAgentAddress(draftId: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[AddressType] =
    http.GET[AddressType](s"$submissionsBaseUrl/$draftId/agent-address")

  def getClientReference(draftId: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[String] =
    http.GET[String](s"$submissionsBaseUrl/$draftId/client-reference")

  def resetTaxLiability(draftId: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {
    http.POSTEmpty[HttpResponse](s"$submissionsBaseUrl/$draftId/reset/taxLiability")
  }

  def getTrustSetupDate(draftId: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Option[LocalDate]] = {
    http.GET[HttpResponse](s"$submissionsBaseUrl/$draftId/when-trust-setup").map {
      response =>
        (response.json \ "startDate").asOpt[LocalDate]
    }.recover {
      case _ => None
    }
  }

  def getTrustName(draftId: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[String] =
    http.GET[HttpResponse](s"$submissionsBaseUrl/$draftId/trust-name").map {
      response =>
        (response.json \ "trustName").as[String]
    }

  def removeDraft(draftId: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {
    http.DELETE[HttpResponse](s"$submissionsBaseUrl/$draftId")
  }
}
