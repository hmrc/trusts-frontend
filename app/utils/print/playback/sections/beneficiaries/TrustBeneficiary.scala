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
import pages.register.beneficiaries.trust._
import play.api.i18n.Messages
import utils.countryOptions.CountryOptions
import utils.print.playback.sections.AnswerRowConverter.{addressQuestion, stringQuestion, utrQuestion, yesNoQuestion}
import viewmodels.AnswerSection

object TrustBeneficiary {

  def apply(index: Int, userAnswers: UserAnswers, countryOptions: CountryOptions)
           (implicit messages: Messages): Seq[AnswerSection] = {

    userAnswers.get(TrustBeneficiaryNamePage(index)).map { name =>
      Seq(AnswerSection(
        headingKey = Some(messages("answerPage.section.trustBeneficiary.heading", index + 1)),
        Seq(
          stringQuestion(TrustBeneficiaryNamePage(index), userAnswers, "trustBeneficiaryName"),
          yesNoQuestion(TrustBeneficiaryDiscretionYesNoPage(index), userAnswers, "trustBeneficiaryShareOfIncomeYesNo", name),
          stringQuestion(TrustBeneficiaryShareOfIncomePage(index), userAnswers, "trustBeneficiaryShareOfIncome", name),
          yesNoQuestion(TrustBeneficiaryAddressYesNoPage(index), userAnswers, "trustBeneficiaryAddressYesNo", name),
          yesNoQuestion(TrustBeneficiaryAddressUKYesNoPage(index), userAnswers, "trustBeneficiaryAddressUKYesNo", name),
          addressQuestion(TrustBeneficiaryAddressPage(index), userAnswers, "trustBeneficiaryAddress", countryOptions = countryOptions, messageArg = name),
          utrQuestion(TrustBeneficiaryUtrPage(index), userAnswers, "trustBeneficiaryUtr", name)
        ).flatten,
        sectionKey = None
      ))
    }.getOrElse(Nil)
  }
}
