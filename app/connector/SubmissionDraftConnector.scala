/*
 * Copyright 2026 HM Revenue & Customs
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
import models.RegistrationSubmission.AllAnswerSections
import models.core.http.{AddressType, LeadTrusteeType}
import models.{FirstTaxYearAvailable, SubmissionDraftData, SubmissionDraftId, SubmissionDraftResponse}
import play.api.http.Status.NOT_FOUND
import play.api.libs.json.{JsObject, JsValue, Json}
import uk.gov.hmrc.http.HttpReads.Implicits.{readFromJson, readRaw}
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse, StringContextOps, UpstreamErrorResponse}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class SubmissionDraftConnector @Inject() (http: HttpClientV2, config: FrontendAppConfig) {

  private val mainSection           = "main"
  private val beneficiariesSection  = "beneficiaries"
  private val settlorsSection       = "settlors"
  private val registrationSection   = "registration"
  private val answerSectionsSection = "answerSections"

  private val submissionsBaseUrl = s"${config.trustsUrl}/trusts/register/submission-drafts"

  private def getDraftSection(draftId: String, section: String)(implicit
    hc: HeaderCarrier,
    ec: ExecutionContext
  ): Future[SubmissionDraftResponse] =
    http.get(url"$submissionsBaseUrl/$draftId/$section").execute[SubmissionDraftResponse]

  def setDraftMain(draftId: String, draftData: JsValue, inProgress: Boolean, reference: Option[String])(implicit
    hc: HeaderCarrier,
    ec: ExecutionContext
  ): Future[HttpResponse] = {
    val submissionDraftData = SubmissionDraftData(draftData, reference, Some(inProgress))
    http
      .post(url"$submissionsBaseUrl/$draftId/$mainSection")
      .withBody(Json.toJson(submissionDraftData))
      .execute[HttpResponse]
  }

  def getDraftMain(
    draftId: String
  )(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Option[SubmissionDraftResponse]] =
    getDraftSection(draftId, mainSection)
      .map(Some(_))
      .recover {
        case e: UpstreamErrorResponse if e.statusCode == NOT_FOUND => None
      }

  def getDraftBeneficiaries(
    draftId: String
  )(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[SubmissionDraftResponse] =
    getDraftSection(draftId, beneficiariesSection)

  def getDraftSettlors(
    draftId: String
  )(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[SubmissionDraftResponse] =
    getDraftSection(draftId, settlorsSection)

  def setDraftSettlors(draftId: String, data: JsValue)(implicit
    hc: HeaderCarrier,
    ec: ExecutionContext
  ): Future[HttpResponse] =
    http
      .post(url"$submissionsBaseUrl/$draftId/set/$settlorsSection")
      .withBody(data)
      .execute[HttpResponse]

  def getCurrentDraftIds()(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[List[SubmissionDraftId]] =
    http
      .get(url"$submissionsBaseUrl")
      .execute[List[SubmissionDraftId]]

  def getRegistrationPieces(draftId: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[JsObject] =
    getDraftSection(draftId, registrationSection).map { section =>
      section.data.as[JsObject]
    }

  def getAnswerSections(draftId: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[AllAnswerSections] =
    getDraftSection(draftId, answerSectionsSection).map { section =>
      section.data.as[AllAnswerSections]
    }

  def getLeadTrustee(draftId: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[LeadTrusteeType] =
    http
      .get(url"$submissionsBaseUrl/$draftId/lead-trustee")
      .execute[LeadTrusteeType]

  def getCorrespondenceAddress(draftId: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[AddressType] =
    http
      .get(url"$submissionsBaseUrl/$draftId/correspondence-address")
      .execute[AddressType]

  def getAgentAddress(draftId: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[AddressType] =
    http
      .get(url"$submissionsBaseUrl/$draftId/agent-address")
      .execute[AddressType]

  def getClientReference(draftId: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[String] =
    http
      .get(url"$submissionsBaseUrl/$draftId/client-reference")
      .execute[String]

  def getTrustName(draftId: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[String] =
    http
      .get(url"$submissionsBaseUrl/$draftId/trust-name")
      .execute[HttpResponse]
      .map { response =>
        (response.json \ "trustName").as[String]
      }

  def updateTaxLiability(draftId: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] =
    http
      .post(url"$submissionsBaseUrl/$draftId/update/tax-liability")
      .execute[HttpResponse]

  def removeDraft(draftId: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] =
    http
      .delete(url"$submissionsBaseUrl/$draftId")
      .execute[HttpResponse]

  def getFirstTaxYearAvailable(
    draftId: String
  )(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Option[FirstTaxYearAvailable]] =
    http
      .get(url"$submissionsBaseUrl/$draftId/first-tax-year-available")
      .execute[FirstTaxYearAvailable]
      .map(Some(_))
      .recover {
        case e: UpstreamErrorResponse if e.statusCode == NOT_FOUND => None
      }

}
