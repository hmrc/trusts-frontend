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

import models.core.UserAnswers
import models.requests.MatchingAndSuitabilityDataRequest
import play.api.i18n.Messages
import repositories.RegistrationsRepository
import uk.gov.hmrc.http.HeaderCarrier
import viewmodels.RegistrationAnswerSections

import java.util.UUID
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class DraftRegistrationService @Inject() (registrationsRepository: RegistrationsRepository)(implicit
  ec: ExecutionContext
) {

  def create[A](request: MatchingAndSuitabilityDataRequest[A])(implicit hc: HeaderCarrier): Future[String] = {

    val draftId     = UUID.randomUUID().toString
    val userAnswers = UserAnswers(
      draftId = draftId,
      data = request.userAnswers.data,
      internalAuthId = request.internalId
    )

    registrationsRepository.set(userAnswers, request.affinityGroup).map { _ =>
      draftId
    }
  }

  def getAnswerSections(
    draftId: String
  )(implicit hc: HeaderCarrier, messages: Messages): Future[RegistrationAnswerSections] =
    registrationsRepository.getAnswerSections(draftId)

}
