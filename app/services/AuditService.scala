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

package services

import auditing.TrustAuditing.{REGISTRATION_ALREADY_SUBMITTED, REGISTRATION_SUBMISSION_FAILED}
import auditing.{RegistrationErrorAuditEvent, TrustAuditing, TrustRegistrationSubmissionAuditEvent}
import config.FrontendAppConfig
import javax.inject.Inject
import models.core.UserAnswers
import models.core.http.{RegistrationTRNResponse, TrustResponse}
import models.requests.RegistrationDataRequest
import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.auth.core.AffinityGroup.Agent
import uk.gov.hmrc.http.HeaderCarrier
import play.api.http.Status._
import uk.gov.hmrc.play.audit.http.connector.AuditConnector

import scala.concurrent.ExecutionContext.Implicits._

class AuditService @Inject()(auditConnector: AuditConnector, config: FrontendAppConfig) {

  def auditRegistrationSubmitted(payload: JsValue,
                                 draftId: String,
                                 response: RegistrationTRNResponse)(implicit request: RegistrationDataRequest[_], hc: HeaderCarrier): Unit = {

    val event = if (request.affinityGroup == Agent) {
      TrustAuditing.REGISTRATION_SUBMITTED_BY_AGENT
    } else {
      TrustAuditing.REGISTRATION_SUBMITTED_BY_ORGANISATION
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
      event = REGISTRATION_ALREADY_SUBMITTED,
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

  def auditErrorBuildingRegistration(userAnswers: UserAnswers,
                                     errorReason: String)(implicit request: RegistrationDataRequest[_], hc: HeaderCarrier): Unit = {

    audit(
      event = TrustAuditing.ERROR_BUILDING_REGISTRATION,
      payload = userAnswers.data,
      draftId = userAnswers.draftId,
      internalId = request.internalId,
      RegistrationErrorAuditEvent(INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", errorReason)
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
