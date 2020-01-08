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
import utils.CheckAnswersFormatters
import utils.countryOptions.CountryOptions
import utils.print.playback.sections.AnswerRowConverter.{addressQuestion, dateQuestion, fullNameQuestion, individualOrBusinessQuestion, ninoQuestion, passportOrIdCardQuestion, yesNoQuestion}
import viewmodels.AnswerSection

object LeadTrusteeIndividual {

  def apply(index: Int, userAnswers: UserAnswers, countryOptions: CountryOptions)(implicit messages: Messages): Option[Seq[AnswerSection]] = {

    userAnswers.get(TrusteesNamePage(index)).map(CheckAnswersFormatters.fullName).flatMap { name =>
      Some(Seq(
        AnswerSection(
          headingKey = Some(messages("answerPage.section.trustee.subheading") + s" ${index + 1}"),
          Seq(
            yesNoQuestion(IsThisLeadTrusteePage(index), userAnswers, "isThisLeadTrustee"),
            individualOrBusinessQuestion(TrusteeIndividualOrBusinessPage(index), userAnswers, "trusteeIndividualOrBusiness"),
            fullNameQuestion(TrusteesNamePage(index), userAnswers, "trusteesName"),
            yesNoQuestion(TrusteeDateOfBirthYesNoPage(index), userAnswers, "trusteeDateOfBirthYesNo", name),
            dateQuestion(TrusteesDateOfBirthPage(index), userAnswers, "trusteesDateOfBirth", name),
            yesNoQuestion(TrusteeNinoYesNoPage(index), userAnswers, "trusteeNinoYesNo", name),
            ninoQuestion(TrusteesNinoPage(index), userAnswers, "trusteesNino", name),
            yesNoQuestion(TrusteeAddressYesNoPage(index), userAnswers, "trusteeUkAddressYesNo", name),
            yesNoQuestion(TrusteeLiveInTheUKPage(index), userAnswers, "trusteeLiveInTheUK", name),
            addressQuestion(TrusteeAddressPage(index), userAnswers, "trusteesUkAddress", name, countryOptions),
            yesNoQuestion(TrusteePassportIDCardYesNoPage(index), userAnswers, "trusteePassportOrIdCardYesNo", name),
            passportOrIdCardQuestion(TrusteePassportIDCardPage(index), userAnswers, "trusteePassportOrIdCard", name, countryOptions)
          ).flatten,
          sectionKey = Some(messages("answerPage.section.trustees.heading"))
        ))
      )
    }
  }

}
