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
import mapping.reads.{LeadTrusteeIndividual, LeadTrusteeOrganisation, Trustees}
import mapping.Mapping
import models.core.UserAnswers
import pages.register.TrustNamePage
import pages.register.trustees.{TrusteeAddressInTheUKPage, TrusteesInternationalAddressPage, TrusteesUkAddressPage}
import play.api.Logger

class CorrespondenceMapper @Inject()(addressMapper: AddressMapper) extends Mapping[Correspondence] {

  override def build(userAnswers: UserAnswers): Option[Correspondence] = {

    userAnswers.get(TrustNamePage).flatMap {
      trustName =>
        userAnswers.get(Trustees).getOrElse(Nil) match {
          case Nil => None
          case list =>
            list.find(_.isLead).flatMap {
              case lti: LeadTrusteeIndividual =>
                buildAddressMapper(userAnswers, trustName, list.indexOf(lti), !lti.liveInUK, lti.telephoneNumber)
              case lto: LeadTrusteeOrganisation =>
                buildAddressMapper(userAnswers, trustName, list.indexOf(lto), !lto.liveInUK, lto.telephoneNumber)
              case _ =>
                Logger.info(s"[CorrespondenceMapper][build] unable to create correspondence due to unexpected lead trustee type")
                None
            }
        }
    }
  }

  private def buildAddressMapper(userAnswers: UserAnswers, trustName: String, index: Int, abroad: Boolean, telephone: String) = {
    addressMapper.build(
      userAnswers,
      TrusteeAddressInTheUKPage(index),
      TrusteesUkAddressPage(index),
      TrusteesInternationalAddressPage(index)
    ) map {
      address =>
        Correspondence(
          abroadIndicator = abroad,
          name = trustName,
          address = address,
          phoneNumber = telephone
        )
    }
  }

}