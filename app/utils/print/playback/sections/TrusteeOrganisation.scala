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

import models.playback.UserAnswers
import pages.register.trustees._
import play.api.i18n.Messages
import play.twirl.api.HtmlFormat
import utils.CheckYourAnswersHelper.{dateFormatter, internationalAddress, ukAddress, yesOrNo}
import utils.countryOptions.CountryOptions
import viewmodels.{AnswerRow, AnswerSection}

object TrusteeOrganisation {

  def apply(index: Int, userAnswers: UserAnswers, countryOptions: CountryOptions)(implicit messages: Messages): Option[Seq[AnswerSection]] = {

    val questions = Seq(
    ).flatten

    if (name(index, userAnswers).nonEmpty) {
      Some(Seq(AnswerSection(
        headingKey = None,
        questions,
        sectionKey = Some(messages("answerPage.section.settlorCompany.heading"))
      )))
    } else {
      None
    }
  }

  def name(index: Int, userAnswers: UserAnswers): Option[AnswerRow] = userAnswers.get(TrusteeOrgNamePage(index)) map { x =>
    AnswerRow(
      "trusteeOrgName.checkYourAnswersLabel",
      HtmlFormat.escape(x),
      None
    )
  }

  def utrYesNo(index: Int, userAnswers: UserAnswers)(implicit messages: Messages): Option[AnswerRow] =
    userAnswers.get(TrusteeUTRYesNoPagePage(index)) map {
      x =>
        AnswerRow(
          "trusteeOrgUtrYesNo.checkYourAnswersLabel",
          yesOrNo(x),
          None
        )
    }

  def utr(index: Int, userAnswers: UserAnswers): Option[AnswerRow] = userAnswers.get(TrusteesUtrPage(index)) map {
    x =>
      AnswerRow(
        "trusteeUtr.checkYourAnswersLabel",
        HtmlFormat.escape(x.format(dateFormatter)),
        None
      )
  }

  def addressUKYesNo(index: Int, userAnswers: UserAnswers)
                    (implicit messages: Messages): Option[AnswerRow] = userAnswers.get(TrusteeAddressInTheUKPage(index)) map {
    x =>
      AnswerRow(
        "trusteeOrgAddressUKYesNo.checkYourAnswersLabel",
        yesOrNo(x),
        None
      )
  }

  def addressUK(index: Int, userAnswers: UserAnswers)
               (implicit messages: Messages): Option[AnswerRow] = userAnswers.get(TrusteesUkAddressPage(index)) map {
    x =>
      AnswerRow(
        "trusteeOrgUKAddress.checkYourAnswersLabel",
        ukAddress(x),
        None
      )
  }

  def nonUKAddress(index: Int, userAnswers: UserAnswers, countryOptions: CountryOptions)
                  (implicit messages: Messages): Option[AnswerRow] = userAnswers.get(TrusteesInternationalAddressPage(index)) map {
    x =>
      AnswerRow(
        "trusteeOrgNonUKAddress.checkYourAnswersLabel",
        internationalAddress(x, countryOptions),
        None
      )
  }

  def telephone(index: Int, userAnswers: UserAnswers)
               (implicit messages: Messages): Option[AnswerRow] = userAnswers.get(TelephoneNumberPage(index)) map {
    x =>
      AnswerRow(
        "trusteeTelephone.checkYourAnswersLabel",
        HtmlFormat.escape(x),
        None
      )
  }

  def email(index: Int, userAnswers: UserAnswers)
           (implicit messages: Messages): Option[AnswerRow] = userAnswers.get(EmailPage(index)) map {
    x =>
      AnswerRow(
        "trusteeEmail.checkYourAnswersLabel",
        HtmlFormat.escape(x),
        None
      )
  }
}
