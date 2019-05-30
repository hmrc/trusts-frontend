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
import pages._

class DeceasedSettlorMapper @Inject()(nameMapper: NameMapper, addressMapper: AddressMapper) extends Mapping[WillType] {

  override def build(userAnswers: UserAnswers): Option[WillType] = {
    for {
      settlorsName <- nameMapper.build(SettlorsNamePage, userAnswers)
      settlorsDateOfBirth = userAnswers.get(SettlorsDateOfBirthPage)
      settlorDateOfDeath = userAnswers.get(SettlorDateOfDeathPage)
      settlorIdentification = identificationStatus(userAnswers)
    } yield {
      WillType(
        name = settlorsName,
        dateOfBirth = settlorsDateOfBirth,
        dateOfDeath = settlorDateOfDeath,
        identification = settlorIdentification
      )
    }
  }

  private def identificationStatus(userAnswers: UserAnswers): Option[Identification] = {
    val settlorNinoYesNo = userAnswers.get(SettlorsNINoYesNoPage)
    val settlorsLastKnownAddressYesNo = userAnswers.get(SettlorsLastKnownAddressYesNoPage)
    (settlorNinoYesNo, settlorsLastKnownAddressYesNo) match {
      case (Some(true), _) => ninoMap(userAnswers)
      case (Some(false), Some(true)) =>
        val address = addressMapper.build(userAnswers, WasSettlorsAddressUKYesNoPage, SettlorsUKAddressPage, SettlorsInternationalAddressPage)
        Some(Identification(None, address))
      case (_, _) => None
    }
  }

  private def ninoMap(userAnswers: UserAnswers): Option[Identification] = {
    val settlorNino = userAnswers.get(SettlorNationalInsuranceNumberPage)
    settlorNino.map {
      nino =>
        Identification(
          nino = settlorNino,
          address = None)
    }
  }


}
