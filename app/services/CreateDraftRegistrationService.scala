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

import java.util.UUID

import akka.stream.Materializer
import javax.inject.Inject
import models.core.UserAnswers
import models.requests.{IdentifierRequest, OptionalDataRequest}
import repositories.RegistrationsRepository
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.audit.http.connector.AuditConnector

import scala.concurrent.{ExecutionContext, Future}

class CreateDraftRegistrationService @Inject()(
                                                registrationsRepository: RegistrationsRepository,
                                                auditConnector: AuditConnector
                                              )(implicit ec: ExecutionContext, m: Materializer) {

  private def build[A](request: OptionalDataRequest[A])(implicit hc : HeaderCarrier) : Future[String] = {
    val draftId = UUID.randomUUID().toString
    val userAnswers = UserAnswers(draftId = draftId, internalAuthId = request.internalId)

    registrationsRepository.set(userAnswers).map {
      _ =>
        draftId
    }
  }

  def create[A](request: IdentifierRequest[A])(implicit hc : HeaderCarrier) : Future[String] = {
    val transformed = OptionalDataRequest(request.request, request.identifier, None, request.affinityGroup, request.enrolments, request.agentARN)
    build(transformed)
  }

  def create[A](request : OptionalDataRequest[A])(implicit hc : HeaderCarrier) : Future[String] =
    build(request)

}

