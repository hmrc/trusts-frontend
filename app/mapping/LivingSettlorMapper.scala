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
import pages.living_settlor._

abstract class LivingSettlorMapper @Inject()(nameMapper: NameMapper, addressMapper: AddressMapper) extends Mapping[Settlor] {

   def build(userAnswers: UserAnswers, index: Int): Option[Settlor] = {
    for {
      settlorIndividualName <- nameMapper.build(SettlorsNamePage, userAnswers)
      settlorIndividualDateOfBirth = userAnswers.get(SettlorsDateOfBirthPage)
      settlorIndividualIdentification = identification(index, userAnswers)
    } yield {
      Settlor(
        name = settlorIndividualName,
        dateOfBirth = settlorIndividualDateOfBirth,
        identification = settlorIndividualIdentification
      )
    }
  }

  private def identification(index: Int, userAnswers: UserAnswers): Option[IdentificationType] = {
    val settlorIndividualNinoYesNo = userAnswers.get(SettlorIndividualNINOYesNoPage(index))
    val settlorIndividualAddressYesNo = userAnswers.get(SettlorIndividualAddressYesNoPage(index))
    val settlorIndividualPassportYesNo = userAnswers.get(SettlorIndividualPassportYesNoPage(index))
    val settlorIndividualIdCardYesNo = userAnswers.get(SettlorIndividualIDCardYesNoPage(index))

    val settlorIndividualPassportPage = userAnswers.get(SettlorIndividualPassportPage(index))
    val settlorIndividualIdCardPage = userAnswers.get(SettlorIndividualIDCardPage(index))

    (settlorIndividualNinoYesNo, settlorIndividualAddressYesNo, settlorIndividualPassportYesNo, settlorIndividualIdCardYesNo) match {
      case (Some(true), _, _, _) => ninoMap(userAnswers)

      case (Some(false), Some(false), Some(true), Some(false)) =>
        val address: Option[AddressType] = addressMapper.build(userAnswers, WasSettlorsAddressUKYesNoPage, SettlorsUKAddressPage, SettlorsInternationalAddressPage)
        Some(IdentificationType(None, None, address, None))

      case (Some(false), Some(true), Some(false), Some(false)) =>
        val passport: Option[PassportType] = {
          settlorIndividualPassportPage.map { passport =>
            PassportType(passport.cardNumber, passport.expiryDate, passport.country)
          }
        }
        Some(IdentificationType(None, passport, None, None))

      case (Some(false), Some(false), Some(false), Some(true)) =>
        val idCard: Option[IdCardType] = {
          settlorIndividualIdCardPage.map {idCard =>
            IdCardType(idCard.cardNumber, idCard.expiryDate, idCard.country)
          }
        }
        Some(IdentificationType(None, None, None, idCard))

      case (_, _, _, _) => None
    }
  }

  private def ninoMap(userAnswers: UserAnswers): Option[IdentificationType] = {
    val settlorNino = userAnswers.get(SettlorNationalInsuranceNumberPage)

    settlorNino.map {
      nino =>
        IdentificationType(
          nino = settlorNino,
          address = None,
          passport = None,
          idCard = None
        )
    }
  }


}
