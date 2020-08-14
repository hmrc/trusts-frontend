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
import mapping.Mapping
import mapping.reads.{LeadTrusteeIndividual, LeadTrusteeOrganisation, Trustees}
import models.core.UserAnswers
import pages.register.trust_details.TrustNamePage
import pages.register.trustees.individual._
import pages.register.trustees.organisation._
import play.api.Logger

class CorrespondenceMapper @Inject()(addressMapper: AddressMapper) {

  def build(userAnswers: UserAnswers, leadTrusteeAddress: AddressType, leadTrusteeTelephoneNumber: String): Option[Correspondence] = {

    userAnswers.get(TrustNamePage).map {
      trustName =>
        Correspondence(
          abroadIndicator = !leadTrusteeAddress.isInUk,
          name = trustName,
          address = leadTrusteeAddress,
          phoneNumber = leadTrusteeTelephoneNumber
        )
    }
  }
}