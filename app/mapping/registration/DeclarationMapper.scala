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

package mapping.registration

import javax.inject.Inject
import models.core.{UserAnswers, http}
import models.core.http.{AddressType, Declaration}
import pages.register.DeclarationPage
import pages.register.agents.{AgentAddressYesNoPage, AgentInternalReferencePage, AgentInternationalAddressPage, AgentUKAddressPage}

class DeclarationMapper @Inject()(addressMapper: AddressMapper) {

  def build(userAnswers: UserAnswers, leadTrusteeAddress: AddressType): Option[Declaration] = {

    val declaration = userAnswers.get(DeclarationPage)
    val agentInternalReference = userAnswers.get(AgentInternalReferencePage)

    val address = agentInternalReference match {
      case Some(_) => getAgentAddress(userAnswers)
      case _ => Some(leadTrusteeAddress)
    }

    address flatMap {
      declarationAddress =>
        declaration.map {
          dec =>
            http.Declaration(
              name = dec.name,
              address = declarationAddress
            )
        }
    }
  }

  private def getAgentAddress(userAnswers: UserAnswers): Option[AddressType] = {
    addressMapper.build(
      userAnswers,
      AgentAddressYesNoPage,
      AgentUKAddressPage,
      AgentInternationalAddressPage
    )
  }
}