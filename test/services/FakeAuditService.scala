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

package services

import config.FrontendAppConfig

import javax.inject.Inject
import models.core.UserAnswers
import models.core.http.RegistrationTRNResponse
import models.requests.RegistrationDataRequest
import play.api.libs.json.JsValue
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.audit.http.connector.AuditConnector

import scala.concurrent.ExecutionContext

class FakeAuditService @Inject() (auditConnector: AuditConnector, config: FrontendAppConfig)(ec: ExecutionContext)
    extends AuditService(auditConnector, config)(ec) {

  override def auditRegistrationSubmitted(payload: JsValue, draftId: String, response: RegistrationTRNResponse)(implicit
    request: RegistrationDataRequest[_],
    hc: HeaderCarrier
  ): Unit = ()

  override def auditRegistrationAlreadySubmitted(payload: JsValue, draftId: String)(implicit
    request: RegistrationDataRequest[_],
    hc: HeaderCarrier
  ): Unit = ()

  override def auditRegistrationSubmissionFailed(payload: JsValue, draftId: String)(implicit
    request: RegistrationDataRequest[_],
    hc: HeaderCarrier
  ): Unit = ()

  override def auditRegistrationPreparationFailed(userAnswers: UserAnswers, errorReason: String)(implicit
    request: RegistrationDataRequest[_],
    hc: HeaderCarrier
  ): Unit = ()

}
