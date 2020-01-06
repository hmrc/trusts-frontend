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

import java.time.LocalDate

import models.core.pages.{Address, FullName, InternationalAddress, UKAddress}
import models.playback.UserAnswers
import models.registration.pages.{AddressOrUtr, PassportOrIdCardDetails}
import play.api.i18n.Messages
import play.api.mvc.Call
import play.twirl.api.HtmlFormat
import queries.Gettable
import utils.CheckAnswersFormatters
import utils.countryOptions.CountryOptions
import viewmodels.AnswerRow

object AnswerRowConverter {

  def ninoQuestion(query: Gettable[String], userAnswers: UserAnswers, labelKey: String,
                   messageArg: String = "", changeRoute: Option[Call] = None)
                  (implicit messages:Messages) = {
    userAnswers.get(query) map {x =>
      AnswerRow(
        messages(s"${labelKey}.checkYourAnswersLabel", messageArg),
        HtmlFormat.escape(CheckAnswersFormatters.formatNino(x)),
        None
      )
    }
  }

  def addressQuestion(query: Gettable[Address], userAnswers: UserAnswers, labelKey: String,
                      messageArg: String = "", countryOptions: CountryOptions, changeRoute: Option[Call] = None)
                     (implicit messages:Messages) = {
    userAnswers.get(query) map {
      case x: UKAddress =>
        AnswerRow(
          messages(s"${labelKey}.checkYourAnswersLabel", messageArg),
          CheckAnswersFormatters.ukAddress(x),
          None
        )
      case x: InternationalAddress =>
        AnswerRow(
          messages(s"${labelKey}.checkYourAnswersLabel", messageArg),
          CheckAnswersFormatters.internationalAddress(x, countryOptions),
          None
        )
    }
  }

  def monetaryAmountQuestion(query: Gettable[String], userAnswers: UserAnswers, labelKey: String,
                             messageArg: String = "", changeRoute: Option[Call] = None)
                            (implicit messages:Messages) = {
    userAnswers.get(query) map {x =>
      AnswerRow(
        messages(s"${labelKey}.checkYourAnswersLabel", messageArg),
        CheckAnswersFormatters.currency(x),
        None
      )
    }
  }

  def dateQuestion(query: Gettable[LocalDate], userAnswers: UserAnswers, labelKey: String,
                   messageArg: String = "", changeRoute: Option[Call] = None)
                  (implicit messages:Messages) = {
    userAnswers.get(query) map {x =>
      AnswerRow(
        messages(s"${labelKey}.checkYourAnswersLabel", messageArg),
        HtmlFormat.escape(x.format(CheckAnswersFormatters.dateFormatter)),
        None
      )
    }
  }

  def yesNoQuestion(query: Gettable[Boolean], userAnswers: UserAnswers, labelKey: String,
                    messageArg: String = "", changeRoute: Option[Call] = None)
                   (implicit messages:Messages) = {
    userAnswers.get(query) map {x =>
      AnswerRow(
        messages(s"${labelKey}.checkYourAnswersLabel", messageArg),
        CheckAnswersFormatters.yesOrNo(x),
        None
      )
    }
  }

  def fullNameQuestion(query: Gettable[FullName], userAnswers: UserAnswers, labelKey: String,
                       messageArg: String = "", changeRoute: Option[Call] = None)
                      (implicit messages:Messages) = {
    userAnswers.get(query) map {x =>
      AnswerRow(
        messages(s"${labelKey}.checkYourAnswersLabel", messageArg),
        HtmlFormat.escape(CheckAnswersFormatters.fullName(x)),
        None
      )
    }
  }

  def stringQuestion(query: Gettable[String], userAnswers: UserAnswers, labelKey: String,
                       messageArg: String = "", changeRoute: Option[Call] = None)
                      (implicit messages:Messages) = {
    userAnswers.get(query) map {x =>
      AnswerRow(
        messages(s"${labelKey}.checkYourAnswersLabel", messageArg),
        HtmlFormat.escape(x),
        None
      )
    }
  }

  def passportOrIdCardQuestion(query: Gettable[PassportOrIdCardDetails], userAnswers: UserAnswers, labelKey: String,
                               messageArg: String = "", countryOptions: CountryOptions, changeRoute: Option[Call] = None)
                              (implicit messages: Messages) = {
    userAnswers.get(query) map {x =>
      AnswerRow(
        messages(s"${labelKey}.checkYourAnswersLabel", messageArg),
        CheckAnswersFormatters.passportOrIDCard(x, countryOptions),
        None
      )
    }
  }

  def addressOrUtrQuestion(query: Gettable[AddressOrUtr], userAnswers: UserAnswers, labelKey: String,
                           messageArg: String = "", changeRoute: Option[Call] = None)
                          (implicit messages:Messages) = {
    userAnswers.get(query) map {x =>
      AnswerRow(
        messages(s"${labelKey}.checkYourAnswersLabel", messageArg),
        CheckAnswersFormatters.addressOrUtr(x),
        None
      )
    }
  }

}
