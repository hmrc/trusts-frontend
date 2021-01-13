/*
 * Copyright 2021 HM Revenue & Customs
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

import javax.inject.Inject
import models.core.UserAnswers
import models.requests.{IdentifierRequest, OptionalRegistrationDataRequest}
import repositories.RegistrationsRepository
import uk.gov.hmrc.http.HeaderCarrier
import utils.Session
import viewmodels.RegistrationAnswerSections

import scala.concurrent.{ExecutionContext, Future}

class DraftRegistrationService @Inject()(registrationsRepository: RegistrationsRepository)
                                        (implicit ec: ExecutionContext) {

  private def build[A](request: OptionalRegistrationDataRequest[A])(implicit hc: HeaderCarrier): Future[String] = {
    val draftId = UUID.randomUUID().toString
    val userAnswers = UserAnswers(draftId = draftId, internalAuthId = request.internalId)

    registrationsRepository.set(userAnswers).map {
      _ =>
        draftId
    }
  }

  def create[A](request: IdentifierRequest[A])(implicit hc: HeaderCarrier): Future[String] = {
    val transformed = OptionalRegistrationDataRequest(request.request, request.identifier, Session.id(hc), None, request.affinityGroup, request.enrolments, request.agentARN)
    build(transformed)
  }

  def create[A](request: OptionalRegistrationDataRequest[A])(implicit hc: HeaderCarrier): Future[String] =
    build(request)

  def getAnswerSections(draftId: String)(implicit hc: HeaderCarrier): Future[RegistrationAnswerSections] =
    registrationsRepository.getAnswerSections(draftId)

}

