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

package utils.print.playback.sections.beneficiaries

import models.playback.UserAnswers
import pages.register.beneficiaries.other._
import play.api.i18n.Messages
import utils.countryOptions.CountryOptions
import viewmodels.AnswerSection

object OtherBeneficiary {

  import utils.print.playback.sections.AnswerRowConverter._

  def apply(index: Int, userAnswers: UserAnswers, countryOptions: CountryOptions)
           (implicit messages: Messages): Seq[AnswerSection] = {

    userAnswers.get(OtherBeneficiaryDescriptionPage(index)).map { name =>
      Seq(AnswerSection(
        headingKey = Some(messages("answerPage.section.otherBeneficiary.heading", index + 1)),
        Seq(
          stringQuestion(OtherBeneficiaryDescriptionPage(index), userAnswers, "otherBeneficiaryDescription"),
          yesNoQuestion(OtherBeneficiaryDiscretionYesNoPage(index), userAnswers, "otherBeneficiaryShareOfIncomeYesNo", name),
          stringQuestion(OtherBeneficiaryShareOfIncomePage(index), userAnswers, "otherBeneficiaryShareOfIncome", name),
          yesNoQuestion(OtherBeneficiaryAddressYesNoPage(index), userAnswers, "otherBeneficiaryAddressYesNo", name),
          yesNoQuestion(OtherBeneficiaryAddressUKYesNoPage(index), userAnswers, "otherBeneficiaryAddressUKYesNo", name),
          addressQuestion(OtherBeneficiaryAddressPage(index), userAnswers, "otherBeneficiaryAddress", countryOptions = countryOptions, messageArg = name)
        ).flatten,
        sectionKey = None
      ))
    }.getOrElse(Nil)
  }
}
