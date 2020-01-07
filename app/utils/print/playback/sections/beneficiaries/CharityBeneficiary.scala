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

package utils.print.playback.sections.beneficiaries

import models.core.pages.{InternationalAddress, UKAddress}
import models.playback.UserAnswers
import pages.register.beneficiaries.charity._
import play.api.i18n.Messages
import utils.CheckAnswersFormatters
import utils.countryOptions.CountryOptions
import viewmodels.{AnswerRow, AnswerSection}

object CharityBeneficiary {

  def apply(index: Int,
            userAnswers: UserAnswers,
            countryOptions: CountryOptions)
           (implicit messages: Messages): Seq[AnswerSection] = {

    userAnswers.get(CharityBeneficiaryNamePage(index)).map { charityName =>
        Seq(
          AnswerSection(
            headingKey = Some(messages("answerPage.section.charityBeneficiary.subheading", index + 1)),
            Seq(
              name(index, userAnswers),
              shareOfIncomeYesNo(index, userAnswers, charityName),
              shareOfIncome(index, userAnswers, charityName),
              addressYesNo(index, userAnswers, charityName),
              addressUKYesNo(index, userAnswers, charityName),
              address(index, userAnswers, countryOptions, charityName),
              utr(index, userAnswers, charityName)
            ).flatten,
            sectionKey = None
          )
        )
    }.getOrElse(Nil)
  }

  def name(index: Int, userAnswers: UserAnswers): Option[AnswerRow] =
    userAnswers.get(CharityBeneficiaryNamePage(index)) map {
    x =>
      AnswerRow(
        "charityBeneficiaryName.checkYourAnswersLabel",
        CheckAnswersFormatters.escape(x),
        None
      )
  }

  def shareOfIncomeYesNo(index: Int, userAnswers: UserAnswers, name: String)
                        (implicit messages: Messages): Option[AnswerRow] =

    userAnswers.get(CharityBeneficiaryDiscretionYesNoPage(index)) map {
      x =>
        AnswerRow(
          "charityBeneficiaryShareOfIncomeYesNo.checkYourAnswersLabel",
          CheckAnswersFormatters.yesOrNo(x),
          None,
          name
        )
    }

  def shareOfIncome(index: Int, userAnswers: UserAnswers, name: String)
                   (implicit messages: Messages): Option[AnswerRow] =

    userAnswers.get(CharityBeneficiaryShareOfIncomePage(index)) map {
      x =>
        AnswerRow(
          "charityBeneficiaryShareOfIncome.checkYourAnswersLabel",
          CheckAnswersFormatters.escape(x),
          None,
          name
        )
    }

  def addressYesNo(index: Int, userAnswers: UserAnswers, name: String)
                  (implicit messages: Messages): Option[AnswerRow] =

    userAnswers.get(CharityBeneficiaryAddressYesNoPage(index)) map {
      x =>
        AnswerRow(
          "charityBeneficiaryAddressYesNo.checkYourAnswersLabel",
          CheckAnswersFormatters.yesOrNo(x),
          None,
          name
        )
    }

  def addressUKYesNo(index: Int, userAnswers: UserAnswers, name: String)
                  (implicit messages: Messages): Option[AnswerRow] =

    userAnswers.get(CharityBeneficiaryAddressUKYesNoPage(index)) map {
      x =>
        AnswerRow(
          "charityBeneficiaryAddressUKYesNo.checkYourAnswersLabel",
          CheckAnswersFormatters.yesOrNo(x),
          None,
          name
        )
    }

  def address(index: Int, userAnswers: UserAnswers, countryOptions: CountryOptions, name: String)
             (implicit messages: Messages): Option[AnswerRow] =

    userAnswers.get(CharityBeneficiaryAddressPage(index)) map {
      case address: UKAddress =>
        AnswerRow(
          "charityBeneficiaryAddress.checkYourAnswersLabel",
          CheckAnswersFormatters.ukAddress(address),
          None,
          name
        )
      case address: InternationalAddress =>
        AnswerRow(
          "charityBeneficiaryAddress.checkYourAnswersLabel",
          CheckAnswersFormatters.internationalAddress(address, countryOptions),
          None,
          name
        )
    }

  def utr(index: Int, userAnswers: UserAnswers, name: String)
                    (implicit messages: Messages): Option[AnswerRow] =

    userAnswers.get(CharityBeneficiaryUtrPage(index)) map {
      x =>
        AnswerRow(
          "charityBeneficiaryUtr.checkYourAnswersLabel",
          CheckAnswersFormatters.utr(x),
          None,
          name
        )
    }

}
