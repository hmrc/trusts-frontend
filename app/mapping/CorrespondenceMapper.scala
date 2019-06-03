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
import models.entities.{LeadTrusteeIndividual, Trustees}
import pages.{TrustNamePage, TrusteeLiveInTheUKPage, TrusteesInternationalAddressPage, TrusteesUkAddressPage}
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
                val index = list.indexOf(lti)
                val abroad = userAnswers.get(TrusteeLiveInTheUKPage(index)).contains(false)
                val telephone = lti.telephoneNumber

                addressMapper.build(
                  userAnswers,
                  TrusteeLiveInTheUKPage(index),
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
              case _ =>
                Logger.info(s"[CorrespondenceMapper][build] unable to create correspondence due to not having a Lead Trustee Individual")
                None
            }

        }
    }
  }

}