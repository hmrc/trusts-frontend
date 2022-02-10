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

package services

import auditing.TrustAuditing._
import auditing.{AuditEvent, RegistrationErrorAuditEvent, TrustRegistrationSubmissionAuditEvent}
import config.FrontendAppConfig
import models.core.UserAnswers
import models.core.http.{RegistrationTRNResponse, TrustResponse}
import models.requests.RegistrationDataRequest
import play.api.http.Status._
import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.audit.http.connector.AuditConnector

import javax.inject.Inject
import scala.concurrent.ExecutionContext.Implicits._

class AuditService @Inject()(auditConnector: AuditConnector, config: FrontendAppConfig) {

  def auditRegistrationSubmitted(payload: JsValue,
                                 draftId: String,
                                 response: RegistrationTRNResponse)(implicit request: RegistrationDataRequest[_], hc: HeaderCarrier): Unit = {

    val event = if (request.isAgent) {
      REGISTRATION_SUBMITTED_BY_AGENT
    } else {
      REGISTRATION_SUBMITTED_BY_ORGANISATION
    }

    audit(
      event = event,
      payload = Json.toJson(payload),
      draftId = draftId,
      internalId = request.internalId,
      response = response
    )
  }

  def auditRegistrationAlreadySubmitted(payload: JsValue,
                                        draftId: String)(implicit request: RegistrationDataRequest[_], hc: HeaderCarrier): Unit = {

    audit(
      event = REGISTRATION_SUBMISSION_FAILED,
      payload = Json.toJson(payload),
      draftId = draftId,
      internalId = request.internalId,
      response = RegistrationErrorAuditEvent(FORBIDDEN, "ALREADY_REGISTERED", "Trust is already registered.")
    )
  }

  def auditRegistrationSubmissionFailed(payload: JsValue,
                                        draftId: String)(implicit request: RegistrationDataRequest[_], hc: HeaderCarrier): Unit = {

    audit(
      event = REGISTRATION_SUBMISSION_FAILED,
      payload = Json.toJson(payload),
      draftId = draftId,
      internalId = request.internalId,
      response = RegistrationErrorAuditEvent(INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", "Internal Server Error.")
    )
  }

  def auditRegistrationPreparationFailed(userAnswers: UserAnswers,
                                         errorReason: String)(implicit request: RegistrationDataRequest[_], hc: HeaderCarrier): Unit = {

    audit(
      event = REGISTRATION_PREPARATION_FAILED,
      payload = userAnswers.data,
      draftId = userAnswers.draftId,
      internalId = request.internalId,
      RegistrationErrorAuditEvent(INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", errorReason)
    )
  }

  def auditUserAnswers(userAnswers: UserAnswers)(implicit hc: HeaderCarrier): Unit = {
    val auditPayload = AuditEvent(
      registration = userAnswers.data,
      draftId = userAnswers.draftId,
      internalAuthId = userAnswers.internalAuthId
    )

    auditConnector.sendExplicitAudit(
      USER_ANSWERS,
      auditPayload
    )
  }

  private def audit(event: String,
                    payload: JsValue,
                    draftId: String,
                    internalId: String,
                    response: TrustResponse)(implicit hc: HeaderCarrier): Unit = {

    if (config.auditSubmissions) {

      val auditPayload = TrustRegistrationSubmissionAuditEvent(
        registration = payload,
        draftId = draftId,
        internalAuthId = internalId,
        response = response
      )

      auditConnector.sendExplicitAudit(
        event,
        auditPayload
      )
    } else {
      ()
    }
  }

}
