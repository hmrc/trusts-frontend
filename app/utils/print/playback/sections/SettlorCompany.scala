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
import pages.register.settlors.living_settlor._
import play.api.i18n.Messages
import play.twirl.api.HtmlFormat
import utils.CheckYourAnswersHelper.{dateFormatter, internationalAddress, ukAddress, yesOrNo}
import utils.countryOptions.CountryOptions
import viewmodels.{AnswerRow, AnswerSection}

object SettlorCompany {

  def apply(index: Int, userAnswers: UserAnswers, countryOptions: CountryOptions)(implicit messages: Messages): Option[Seq[AnswerSection]] = {

    val questions = Seq(
      name(index, userAnswers),
      utrYesNo(index, userAnswers),
      utr(index, userAnswers),
      addressYesNo(index, userAnswers),
      addressUKYesNo(index, userAnswers),
      addressUK(index, userAnswers),
      nonUKAddress(index, userAnswers, countryOptions)
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

  def name(index: Int, userAnswers: UserAnswers): Option[AnswerRow] = userAnswers.get(SettlorIndividualNamePage(index)) map { x =>
    AnswerRow(
      "settlorCompanyName.checkYourAnswersLabel",
      HtmlFormat.escape(s"${x.firstName} ${x.middleName.getOrElse("")} ${x.lastName}"),
      None
    )
  }

  def utrYesNo(index: Int, userAnswers: UserAnswers)(implicit messages: Messages): Option[AnswerRow] =
    userAnswers.get(SettlorUtrYesNoPage(index)) map {
      x =>
        AnswerRow(
          "settlorCompanyUtrYesNo.checkYourAnswersLabel",
          yesOrNo(x),
          None
        )
    }

  def utr(index: Int, userAnswers: UserAnswers): Option[AnswerRow] = userAnswers.get(SettlorUtrPage(index)) map {
    x =>
      AnswerRow(
        "settlorCompanyUtr.checkYourAnswersLabel",
        HtmlFormat.escape(x.format(dateFormatter)),
        None
      )
  }

  def addressYesNo(index: Int, userAnswers: UserAnswers)
                  (implicit messages: Messages): Option[AnswerRow] = userAnswers.get(SettlorIndividualAddressYesNoPage(index)) map {
    x =>
      AnswerRow(
        "settlorCompanyAddressYesNo.checkYourAnswersLabel",
        yesOrNo(x),
        None
      )
  }

  def addressUKYesNo(index: Int, userAnswers: UserAnswers)
                    (implicit messages: Messages): Option[AnswerRow] = userAnswers.get(SettlorIndividualAddressUKYesNoPage(index)) map {
    x =>
      AnswerRow(
        "settlorCompanyAddressUKYesNo.checkYourAnswersLabel",
        yesOrNo(x),
        None
      )
  }

  def addressUK(index: Int, userAnswers: UserAnswers)
               (implicit messages: Messages): Option[AnswerRow] = userAnswers.get(SettlorIndividualAddressUKPage(index)) map {
    x =>
      AnswerRow(
        "settlorCompanyUKAddress.checkYourAnswersLabel",
        ukAddress(x),
        None
      )
  }

  def nonUKAddress(index: Int, userAnswers: UserAnswers, countryOptions: CountryOptions)
                  (implicit messages: Messages): Option[AnswerRow] = userAnswers.get(SettlorIndividualAddressInternationalPage(index)) map {
    x =>
      AnswerRow(
        "settlorCompanyNonUKAddress.checkYourAnswersLabel",
        internationalAddress(x, countryOptions),
        None
      )
  }

}
