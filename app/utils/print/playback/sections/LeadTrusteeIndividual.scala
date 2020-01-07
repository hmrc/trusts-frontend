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
import utils.countryOptions.CountryOptions
import utils.print.playback.sections.AnswerRowConverter._
import viewmodels.AnswerSection

object LeadTrusteeIndividual {

  def apply(index: Int, userAnswers: UserAnswers, countryOptions: CountryOptions)(implicit messages: Messages): Seq[AnswerSection] = {
    Seq(AnswerSection(
      headingKey = Some("answerPage.section.leadTrusteeIndividual.heading"),
      Seq(
        fullNameQuestion(TrusteesNamePage(index), userAnswers, "trusteeName"),
        dateQuestion(TrusteesDateOfBirthPage(index), userAnswers, "trusteeDateOfBirth"),
        yesNoQuestion(TrusteeAUKCitizenPage(index), userAnswers, "trusteeUKCitizen"),
        ninoQuestion(TrusteesNinoPage(index), userAnswers, "trusteeNationalInsuranceNumber"),
        passportOrIdCardQuestion(TrusteePassportIDCardPage(index), userAnswers, "trusteePassportOrIdCard", "", countryOptions),
        yesNoQuestion(TrusteeAddressInTheUKPage(index), userAnswers, "trusteeAddressUKYesNo"),
        addressQuestion(TrusteesUkAddressPage(index), userAnswers, "trusteeUKAddress", "", countryOptions),
        addressQuestion(TrusteesInternationalAddressPage(index), userAnswers, "trusteeNonUKAddress", "", countryOptions),
        stringQuestion(TelephoneNumberPage(index), userAnswers, "trusteeTelephone"),
        stringQuestion(EmailPage(index), userAnswers, "trusteeEmail")
      ).flatten,
      sectionKey = None
    ))
  }
}
