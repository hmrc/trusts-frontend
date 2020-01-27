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

import models.core.pages.{Address, FullName, IndividualOrBusiness, InternationalAddress, UKAddress}
import models.playback.UserAnswers
import models.registration.pages.{KindOfTrust, PassportOrIdCardDetails}
import play.api.i18n.Messages
import play.api.libs.json.Reads
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
        None,
        canEdit = false
      )
    }
  }

  def utrQuestion(query: Gettable[String], userAnswers: UserAnswers, labelKey: String,
                   messageArg: String = "", changeRoute: Option[Call] = None)
                  (implicit messages:Messages) = {
    userAnswers.get(query) map {x =>
      AnswerRow(
        messages(s"${labelKey}.checkYourAnswersLabel", messageArg),
        CheckAnswersFormatters.utr(x),
        None,
        canEdit = false
      )
    }
  }

  def addressQuestion[T <: Address](query: Gettable[T], userAnswers: UserAnswers, labelKey: String,
                      messageArg: String = "", countryOptions: CountryOptions, changeRoute: Option[Call] = None)
                     (implicit messages:Messages, reads: Reads[T]) = {
    userAnswers.get(query) map { x =>
        AnswerRow(
          messages(s"${labelKey}.checkYourAnswersLabel", messageArg),
          CheckAnswersFormatters.addressFormatter(x, countryOptions),
          None,
          canEdit = false
        )
    }
  }

  def internationalAddressQuestion(query: Gettable[InternationalAddress], userAnswers: UserAnswers, labelKey: String,
                        messageArg: String = "", countryOptions: CountryOptions, changeRoute: Option[Call] = None)
                       (implicit messages:Messages) = {
    userAnswers.get(query) map {
      international =>
        AnswerRow(
          messages(s"${labelKey}.checkYourAnswersLabel", messageArg),
          CheckAnswersFormatters.internationalAddress(international, countryOptions),
          None,
          canEdit = false
        )
    }
  }

  def ukAddressQuestion(query: Gettable[UKAddress], userAnswers: UserAnswers, labelKey: String,
  messageArg: String = "", countryOptions: CountryOptions, changeRoute: Option[Call] = None)
  (implicit messages:Messages) = {
    userAnswers.get(query) map {
      uk =>
        AnswerRow(
          messages(s"${labelKey}.checkYourAnswersLabel", messageArg),
          CheckAnswersFormatters.ukAddress(uk),
          None,
          canEdit = false
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
        None,
        canEdit = false
      )
    }
  }

  def percentageQuestion(query: Gettable[String], userAnswers: UserAnswers, labelKey: String,
                             messageArg: String = "", changeRoute: Option[Call] = None)
                            (implicit messages:Messages) = {
    userAnswers.get(query) map {x =>
      AnswerRow(
        messages(s"${labelKey}.checkYourAnswersLabel", messageArg),
        CheckAnswersFormatters.percentage(x),
        None,
        canEdit = false
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
        None,
        canEdit = false
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
        None,
        canEdit = false
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
        None,
        canEdit = false
      )
    }
  }

  def stringQuestion[T](query: Gettable[T], userAnswers: UserAnswers, labelKey: String,
                        messageArg: String = "", changeRoute: Option[Call] = None)
                       (implicit messages:Messages, rds: Reads[T]): Option[AnswerRow] = {
    userAnswers.get(query) map {x =>
      AnswerRow(
        messages(s"${labelKey}.checkYourAnswersLabel", messageArg),
        HtmlFormat.escape(x.toString),
        None,
        canEdit = false
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
        None,
        canEdit = false
      )
    }
  }

  def individualOrBusinessQuestion(query: Gettable[IndividualOrBusiness], userAnswers: UserAnswers, labelKey: String,
                                   messageArg: String = "", changeRoute: Option[Call] = None)
                              (implicit messages: Messages) = {
    userAnswers.get(query) map { x =>
      AnswerRow(
        messages(s"${labelKey}.checkYourAnswersLabel", messageArg),
        HtmlFormat.escape(x.toString.capitalize),
        None,
        canEdit = false
      )
    }
  }

  def kindOfTrustQuestion(query: Gettable[KindOfTrust], userAnswers: UserAnswers, labelKey: String,
                          messageArg: String = "", changeRoute: Option[Call] = None)
                         (implicit messages: Messages) = {
    userAnswers.get(query) map { x =>
      AnswerRow(
        messages(s"${labelKey}.checkYourAnswersLabel", messageArg),
        HtmlFormat.escape(CheckAnswersFormatters.kindOfTrust(x, messages)),
        None,
        canEdit = false
      )
    }
  }

}
