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

package mapping

import javax.inject.Inject
import models.UserAnswers
import models.entities.{LeadTrusteeIndividual, Trustee, Trustees}
import pages.{AgentInternalReferencePage, _}

class DeclarationMapper @Inject()(nameMapper: NameMapper,
                                  addressMapper: AddressMapper) extends Mapping[Declaration] {

  override def build(userAnswers: UserAnswers): Option[Declaration] = {

    val declarationName = userAnswers.get(DeclarationPage)
    val agentInternalReference = userAnswers.get(AgentInternalReferencePage)
    val trustees: List[Trustee] = userAnswers.get(Trustees).getOrElse(List.empty[Trustee])

    val address = agentInternalReference match {
      case Some(_) => addressMapper.build(
        userAnswers,
        AgentAddressYesNoPage,
        AgentUKAddressPage,
        AgentInternationalAddressPage
      )
      case _ => {
        trustees match {
          case Nil => None
          case list =>
            list.find(_.isLead).flatMap {
              case lti: LeadTrusteeIndividual =>
                val index = list.indexOf(lti)
                addressMapper.build(
                  userAnswers,
                  TrusteeLiveInTheUKPage(index),
                  TrusteesUkAddressPage(index),
                  TrusteesInternationalAddressPage(index)
                )
            }
        }
      }
    }

    address flatMap {
      declarationAddress =>
        declarationName.map {
          decName =>
            Declaration(
              name = nameMapper.build(decName),
              address = declarationAddress
            )
        }
    }
  }


}