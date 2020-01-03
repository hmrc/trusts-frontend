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

package utils.print.playback.sections

import models.core.pages.{InternationalAddress, UKAddress}
import models.playback.UserAnswers
import pages.register.protectors.individual._
import play.api.i18n.Messages
import play.twirl.api.HtmlFormat
import utils.CheckYourAnswersHelper.{internationalAddress, ukAddress, yesOrNo}
import utils.countryOptions.CountryOptions
import viewmodels.{AnswerRow, AnswerSection}

object IndividualProtector {

  import mapping.playback.PlaybackImplicits

  def apply(index: Int, userAnswers: UserAnswers, countryOptions: CountryOptions)(implicit messages: Messages): Option[Seq[AnswerSection]] =
    if (name(index, userAnswers).nonEmpty) {
      Some(Seq(AnswerSection(
        headingKey = None,
        Seq(
          name(index, userAnswers),
          dateOfBirthYesNo(index, userAnswers),
          dateOfBirth(index, userAnswers),
          ninoYesNo(index, userAnswers),
          nino(index, userAnswers),
          addressYesNo(index, userAnswers),
          address(index, userAnswers, countryOptions)
        ).flatten,
        sectionKey = Some(messages("answerPage.section.individualProtector.heading"))
      )))
    } else {
      None
    }

  def name(index: Int, userAnswers: UserAnswers): Option[AnswerRow] = userAnswers.get(IndividualProtectorNamePage(index)) map {
    x =>
      AnswerRow(
        "individualProtectorName.checkYourAnswersLabel",
        CheckYourAnswersFormatters.fullName(x),
        None
      )
  }

  def dateOfBirthYesNo(index: Int, userAnswers: UserAnswers)(implicit messages: Messages): Option[AnswerRow] =
    userAnswers.get(IndividualProtectorDateOfBirthYesNoPage(index)) map {
      x =>
        AnswerRow(
          "individualProtectorDateOfBirthYesNo.checkYourAnswersLabel",
          CheckYourAnswersFormatters.yesOrNo(x),
          None
        )
    }

  def dateOfBirth(index: Int, userAnswers: UserAnswers): Option[AnswerRow] =
    userAnswers.get(IndividualProtectorDateOfBirthPage(index)) map {
      x =>
        AnswerRow(
          "individualProtectorDateOfBirth.checkYourAnswersLabel",
          CheckAnswersFormatters.escape(x),
          None
        )
    }

  def ninoYesNo(index: Int, userAnswers: UserAnswers)(implicit messages: Messages): Option[AnswerRow] =
    userAnswers.get(IndividualProtectorNINOYesNoPage(index)) map {
      x =>
        AnswerRow(
          "individualProtectorNINOYesNo.checkYourAnswersLabel",
          CheckAnswersFormatters.yesOrNo(x),
          None
        )
    }

  def nino(index: Int, userAnswers: UserAnswers): Option[AnswerRow] =
    userAnswers.get(IndividualProtectorNINOPage(index)) map {
      x =>
        AnswerRow(
          "individualProtectorNINO.checkYourAnswersLabel",
          CheckAnswersFormatters.escape(x),
          None
        )
    }

  def addressYesNo(index: Int, userAnswers: UserAnswers)(implicit messages: Messages): Option[AnswerRow] =
    userAnswers.get(IndividualProtectorAddressYesNoPage(index)) map {
      x =>
        AnswerRow(
          "individualProtectorAddressYesNo.checkYourAnswersLabel",
          CheckAnswersFormatters.yesOrNo(x),
          None
        )
    }

  def addressUkYesNo(index: Int, userAnswers: UserAnswers)(implicit messages: Messages): Option[AnswerRow] =
    userAnswers.get(IndividualProtectorAddressUKYesNoPage(index)) map {
      x =>
        AnswerRow(
          "individualProtectorAddressUkYesNo.checkYourAnswersLabel",
          CheckAnswersFormatters.yesOrNo(x),
          None
        )
    }

  def address(index: Int, userAnswers: UserAnswers, countryOptions: CountryOptions)(implicit messages: Messages): Option[AnswerRow] =
    userAnswers.get(IndividualProtectorAddressPage(index)) map {
      case address: UKAddress => AnswerRow(
        "individualProtectorAddress.checkYourAnswersLabel",
        ukAddress(address),
        None
      )
      case address: InternationalAddress => AnswerRow(
        "individualProtectorAddress.checkYourAnswersLabel",
        internationalAddress(address, countryOptions),
        None
      )
    }

}
