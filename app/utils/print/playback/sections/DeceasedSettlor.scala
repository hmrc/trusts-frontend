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
import pages.register.settlors.deceased_settlor._
import play.api.i18n.Messages
import utils.CheckAnswersFormatters
import utils.countryOptions.CountryOptions
import viewmodels.AnswerSection

object DeceasedSettlor {

  import utils.print.playback.sections.AnswerRowConverter._

  def apply(userAnswers: UserAnswers, countryOptions: CountryOptions)(implicit messages: Messages): Seq[AnswerSection] = {
    userAnswers.get(SettlorsNamePage).map(CheckAnswersFormatters.fullName).map { name =>
      Seq(
        AnswerSection(
          headingKey = None,
          rows = Seq(
            fullNameQuestion(SettlorsNamePage, userAnswers, "settlorsName"),

            yesNoQuestion(SettlorDateOfDeathYesNoPage, userAnswers, "settlorDateOfDeathYesNo", name),
            dateQuestion(SettlorDateOfDeathPage, userAnswers, "settlorDateOfDeath", name),

            yesNoQuestion(SettlorDateOfBirthYesNoPage, userAnswers, "settlorDateOfBirthYesNo", name),
            dateQuestion(SettlorsDateOfBirthPage, userAnswers, "settlorsDateOfBirth", name),

            yesNoQuestion(SettlorsNationalInsuranceYesNoPage, userAnswers, "settlorsNationalInsuranceYesNo", name),
            ninoQuestion(SettlorNationalInsuranceNumberPage, userAnswers, "settlorNationalInsuranceNumber", name),

            yesNoQuestion(SettlorsLastKnownAddressYesNoPage, userAnswers, "settlorsLastKnownAddressYesNo", name),
            yesNoQuestion(WasSettlorsAddressUKYesNoPage, userAnswers, "wasSettlorsAddressUKYesNo", name),
            ukAddressQuestion(SettlorsUKAddressPage, userAnswers, "settlorsUKAddress", name, countryOptions),
            internationalAddressQuestion(SettlorsInternationalAddressPage, userAnswers, "settlorsInternationalAddress", name, countryOptions),
            passportOrIdCardQuestion(SettlorsPassportIDCardPage, userAnswers, "settlorsPassportOrIdCard", name, countryOptions)

          ).flatten,
          sectionKey = None
        )
      )
    }.getOrElse(Nil)
  }

}
