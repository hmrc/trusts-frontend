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

import auditing.{RegistrationErrorAuditEvent, TrustAuditing}
import com.google.inject.ImplementedBy
import connector.TrustConnector
import javax.inject.Inject
import mapping.registration.RegistrationMapper
import models.core.UserAnswers
import models.core.http.TrustResponse._
import models.core.http.{RegistrationTRNResponse, TrustResponse}
import play.api.Logger
import play.api.libs.json.Json
import repositories.RegistrationsRepository
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}


class DefaultSubmissionService @Inject()(
                                          registrationMapper: RegistrationMapper,
                                          trustConnector: TrustConnector,
                                          auditService: AuditService,
                                          registrationsRepository: RegistrationsRepository
                                        )
  extends SubmissionService {

  override def submit(userAnswers: UserAnswers)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[TrustResponse] = {

    Logger.info("[SubmissionService][submit] submitting registration")

    registrationMapper.build(userAnswers) match {
      case Some(registration) =>
        registrationsRepository.addDraftRegistrationSections(userAnswers.draftId, Json.toJson(registration)) flatMap {
          fullRegistrationJson =>
            trustConnector.register(fullRegistrationJson, userAnswers.draftId) map {
              case response@RegistrationTRNResponse(_) =>

                auditService.audit(
                  event = TrustAuditing.TRUST_REGISTRATION_SUBMITTED,
                  registration = registration,
                  draftId = userAnswers.draftId,
                  internalId = userAnswers.internalAuthId,
                  response = response
                )

                response
              case AlreadyRegistered =>

                auditService.audit(
                  event = TrustAuditing.TRUST_REGISTRATION_SUBMITTED,
                  registration = registration,
                  draftId = userAnswers.draftId,
                  internalId = userAnswers.internalAuthId,
                  response = RegistrationErrorAuditEvent(403, "ALREADY_REGISTERED", "Trust is already registered.")
                )
                AlreadyRegistered
              case other =>
                auditService.audit(
                  event = TrustAuditing.TRUST_REGISTRATION_SUBMITTED,
                  registration = registration,
                  draftId = userAnswers.draftId,
                  internalId = userAnswers.internalAuthId,
                  response = RegistrationErrorAuditEvent(500, "INTERNAL_SERVER_ERROR", "Internal Server Error.")
                )
                other
            }
        }
      case None =>

        auditService.cannotSubmit(
          userAnswers
        )

        Logger.warn("[SubmissionService][submit] Unable to generate registration to submit.")
        Future.failed(UnableToRegister())
      }
    }
}

@ImplementedBy(classOf[DefaultSubmissionService])
trait SubmissionService {

  def submit(userAnswers: UserAnswers)(implicit hc: HeaderCarrier, ec: ExecutionContext) : Future[TrustResponse]

}
