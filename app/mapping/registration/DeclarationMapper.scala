/*
 * Copyright 2023 HM Revenue & Customs
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

package mapping.registration

import _root_.pages.register.DeclarationPage
import models.core.http.{AddressType, Declaration}
import models.core.{UserAnswers, pages}
import repositories.RegistrationsRepository
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class DeclarationMapper @Inject()(registrationsRepository: RegistrationsRepository) {

  def build(userAnswers: UserAnswers, leadTrusteeAddress: AddressType)
           (implicit ec: ExecutionContext, hc: HeaderCarrier): Future[Option[Declaration]] = {

    val declaration: Option[pages.Declaration] = userAnswers.get(DeclarationPage)

    for {
      agentAddress <- registrationsRepository.getAgentAddress(userAnswers.draftId)
    } yield {
      declaration map { dec =>
        Declaration(
          name = dec.name,
          address = agentAddress.getOrElse(leadTrusteeAddress)
        )
      }
    }
  }
}
