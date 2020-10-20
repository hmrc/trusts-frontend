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

import com.google.inject.ImplementedBy
import connector.TrustConnector
import javax.inject.Inject
import mapping.registration.RegistrationMapper
import models.core.UserAnswers
import models.core.http.TrustResponse._
import models.core.http.{RegistrationTRNResponse, TrustResponse}
import models.requests.RegistrationDataRequest
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
                                        ) extends SubmissionService {

  override def submit(userAnswers: UserAnswers)
                     (implicit request: RegistrationDataRequest[_], hc: HeaderCarrier, ec: ExecutionContext): Future[TrustResponse] = {

    Logger.info("[SubmissionService][submit] submitting registration")

    lazy val sessionId: String = hc.sessionId.map(_.value).getOrElse("")

    registrationsRepository.getCorrespondenceAddress(userAnswers.draftId).flatMap {
      correspondenceAddress =>
        registrationsRepository.getTrustName(userAnswers.draftId).flatMap {
          trustName =>
            registrationMapper.build(userAnswers, correspondenceAddress, trustName) match {
              case Some(registration) =>
                registrationsRepository.addDraftRegistrationSections(userAnswers.draftId, Json.toJson(registration)).flatMap {
                  fullRegistrationJson =>
                    trustConnector.register(fullRegistrationJson, userAnswers.draftId) map {
                      case response@RegistrationTRNResponse(_) =>
                        Logger.info("[SubmissionService][submit] Registration successfully submitted.")
                        auditService.auditRegistrationSubmitted(fullRegistrationJson, userAnswers.draftId, response)
                        response
                      case AlreadyRegistered =>
                        Logger.warn("[SubmissionService][submit] Registration already submitted.")
                        auditService.auditRegistrationAlreadySubmitted(fullRegistrationJson, userAnswers.draftId)
                        AlreadyRegistered
                      case other =>
                        Logger.warn(s"[SubmissionService][submit] Registration submission failed.")
                        auditService.auditRegistrationSubmissionFailed(fullRegistrationJson, userAnswers.draftId)
                        other
                    }
                }.recover {
                  case e =>
                    Logger.error(s"[SubmissionService][submit] unable to register trust for session $sessionId due to exception: ${e.getMessage}")
                    auditService.auditRegistrationPreparationFailed(userAnswers, "Error adding draft registration sections.")
                    UnableToRegister()
                }
              case _ =>
                Logger.warn("[SubmissionService][submit] Unable to generate registration to submit.")
                auditService.auditRegistrationPreparationFailed(userAnswers, "Error mapping UserAnswers to Registration.")
                Future.failed(UnableToRegister())
            }
        }.recover {
          case e =>
            Logger.error(s"[SubmissionService][submit] unable to register trust for session $sessionId due to exception: ${e.getMessage}")
            auditService.auditRegistrationPreparationFailed(userAnswers, "Error retrieving trust name transformation.")
            UnableToRegister()
        }
    }.recover {
      case e =>
        Logger.error(s"[SubmissionService][submit] unable to register trust for session $sessionId due to exception: ${e.getMessage}")
        auditService.auditRegistrationPreparationFailed(userAnswers, "Error retrieving correspondence address transformation.")
        UnableToRegister()
    }
  }
}

  @ImplementedBy(classOf[DefaultSubmissionService])
  trait SubmissionService {

    def submit(userAnswers: UserAnswers)
              (implicit request: RegistrationDataRequest[_], hc: HeaderCarrier, ec: ExecutionContext): Future[TrustResponse]

  }


