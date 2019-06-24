/*
 * Copyright 2019 HM Revenue & Customs
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
import mapping.RegistrationMapper
import models.{TrustResponse, UnableToRegister, UserAnswers}
import play.api.Logger
import play.api.libs.json.Json
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.audit.http.connector.AuditConnector
import utils.TrustAuditing

import scala.concurrent.{ExecutionContext, Future}


class DefaultSubmissionService @Inject()(
                                          registrationMapper: RegistrationMapper,
                                          trustConnector: TrustConnector,
                                          auditConnector: AuditConnector
                                        )
  extends SubmissionService {

  override def submit(userAnswers: UserAnswers)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[TrustResponse] = {

    Logger.info("[SubmissionService][submit] submitting registration")

    registrationMapper.build(userAnswers) match {
      case Some(registration) => trustConnector.register(registration)
      case None =>

        auditConnector.sendExplicitAudit(
          TrustAuditing.CANNOT_CREATE_REGISTRATION,
          Map(
            "draftId" -> userAnswers.draftId,
            "internalAuthId" -> userAnswers.internalAuthId,
            "userAnswers" -> Json.stringify(Json.toJson(userAnswers))
          )
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
