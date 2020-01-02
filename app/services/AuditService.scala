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

import auditing.{TrustAuditing, TrustRegistrationSubmissionAuditEvent}
import config.FrontendAppConfig
import javax.inject.Inject
import mapping.registration.Registration
import models.core.UserAnswers
import models.core.http.TrustResponse
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.audit.http.connector.AuditConnector

import scala.concurrent.ExecutionContext.Implicits._

class AuditService @Inject()(auditConnector: AuditConnector, config: FrontendAppConfig){

  def audit(event: String,
            registration: Registration,
            draftId: String,
            internalId: String,
            response: TrustResponse)(implicit hc: HeaderCarrier) = {

    if (config.auditSubmissions) {

      val auditPayload = TrustRegistrationSubmissionAuditEvent(
        registration = registration,
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

  def cannotSubmit(userAnswers: UserAnswers)(implicit hc: HeaderCarrier) = {

    if (config.auditCannotCreateRegistration) {

      auditConnector.sendExplicitAudit(
        TrustAuditing.CANNOT_SUBMIT_REGISTRATION,
        userAnswers
      )
    } else {
      ()
    }
  }

}
