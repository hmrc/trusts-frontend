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
import utils.CheckAnswersFormatters._
import utils.countryOptions.CountryOptions
import viewmodels.{AnswerRow, AnswerSection}
import utils.print.playback.sections.AnswerRowConverter._

object SettlorCompany {

  def apply(index: Int, userAnswers: UserAnswers, countryOptions: CountryOptions)(implicit messages: Messages): Option[Seq[AnswerSection]] = {

    userAnswers.get(SettlorBusinessNamePage(index)).flatMap { name =>
      Some(Seq(
        AnswerSection(
          headingKey = Some(messages("answerPage.section.settlorCompany.subheading") + s" ${index + 1}"),
          Seq(
            stringQuestion(SettlorBusinessNamePage(index), userAnswers, "settlorCompanyName"),
            yesNoQuestion(SettlorUtrYesNoPage(index), userAnswers, "settlorCompanyUtrYesNo", name),
            stringQuestion(SettlorUtrPage(index), userAnswers, "settlorCompanyUtr", name),
            yesNoQuestion(SettlorIndividualAddressYesNoPage(index), userAnswers, "settlorCompanyAddressYesNo", name),
            yesNoQuestion(SettlorIndividualAddressUKYesNoPage(index), userAnswers, "settlorCompanyAddressUKYesNo", name),
            addressQuestion(SettlorIndividualAddressUKPage(index), userAnswers, "trusteesUkAddress", name, countryOptions),
            addressUK(index, userAnswers),
            nonUKAddress(index, userAnswers, countryOptions)
          ).flatten,
          sectionKey = None
        )
      ))
    }

  }

//  def name(index: Int, userAnswers: UserAnswers): Option[AnswerRow] = userAnswers.get(SettlorBusinessNamePage(index)) map { x =>
//    AnswerRow(
//      "settlorCompanyName.checkYourAnswersLabel",
//      HtmlFormat.escape(x),
//      None
//    )
//  }

//  def utrYesNo(index: Int, userAnswers: UserAnswers)(implicit messages: Messages): Option[AnswerRow] =
//    userAnswers.get(SettlorUtrYesNoPage(index)) map {
//      x =>
//        AnswerRow(
//          "settlorCompanyUtrYesNo.checkYourAnswersLabel",
//          yesOrNo(x),
//          None
//        )
//    }
//
//  def utr(index: Int, userAnswers: UserAnswers): Option[AnswerRow] = userAnswers.get(SettlorUtrPage(index)) map {
//    x =>
//      AnswerRow(
//        "settlorCompanyUtr.checkYourAnswersLabel",
//        HtmlFormat.escape(x.format(dateFormatter)),
//        None
//      )
//  }

//  def addressYesNo(index: Int, userAnswers: UserAnswers)
//                  (implicit messages: Messages): Option[AnswerRow] = userAnswers.get(SettlorIndividualAddressYesNoPage(index)) map {
//    x =>
//      AnswerRow(
//        "settlorCompanyAddressYesNo.checkYourAnswersLabel",
//        yesOrNo(x),
//        None
//      )
//  }
//
//  def addressUKYesNo(index: Int, userAnswers: UserAnswers)
//                    (implicit messages: Messages): Option[AnswerRow] = userAnswers.get(SettlorIndividualAddressUKYesNoPage(index)) map {
//    x =>
//      AnswerRow(
//        "settlorCompanyAddressUKYesNo.checkYourAnswersLabel",
//        yesOrNo(x),
//        None
//      )
//  }

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
