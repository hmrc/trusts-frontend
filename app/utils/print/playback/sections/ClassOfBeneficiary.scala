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
import pages.register.beneficiaries.classOfBeneficiary.{ClassOfBeneficiaryDescriptionPage, ClassOfBeneficiaryDiscretionYesNoPage, ClassOfBeneficiaryShareOfIncomePage}
import play.api.i18n.Messages
import utils.CheckAnswersFormatters
import utils.countryOptions.CountryOptions
import viewmodels.{AnswerRow, AnswerSection}

object ClassOfBeneficiary {

  def apply(index: Int, userAnswers: UserAnswers, countryOptions: CountryOptions)(implicit messages: Messages): Option[Seq[AnswerSection]] = {
    if (description(index, userAnswers).nonEmpty) {
      Some(Seq(AnswerSection(
        headingKey = None,
        Seq(
          description(index, userAnswers),
          shareOfIncomeYesNo(index, userAnswers),
          shareOfIncome(index, userAnswers)
        ).flatten,
        sectionKey = Some(messages("answerPage.section.charityBeneficiary.heading"))
      )))
    } else {
      None
    }
  }

  def description(index: Int, userAnswers: UserAnswers): Option[AnswerRow] = userAnswers.get(ClassOfBeneficiaryDescriptionPage(index)) map {
    x =>
      AnswerRow(
        "otherDescription.checkYourAnswersLabel",
        CheckAnswersFormatters.escape(x),
        None
      )
  }

  def shareOfIncomeYesNo(index: Int, userAnswers: UserAnswers)(implicit messages: Messages): Option[AnswerRow] =
    userAnswers.get(ClassOfBeneficiaryDiscretionYesNoPage(index)) map {
      x =>
        AnswerRow(
          "otherShareOfIncomeYesNo.checkYourAnswersLabel",
          CheckAnswersFormatters.yesOrNo(x),
          None
        )
    }

  def shareOfIncome(index: Int, userAnswers: UserAnswers)(implicit messages: Messages): Option[AnswerRow] =
    userAnswers.get(ClassOfBeneficiaryShareOfIncomePage(index)) map {
      x =>
        AnswerRow(
          "otherShareOfIncome.checkYourAnswersLabel",
          CheckAnswersFormatters.escape(x),
          None
        )
    }

}
