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

import mapping.DeedOfVariation
import models.playback.UserAnswers
import pages.register.settlors.SetUpAfterSettlorDiedYesNoPage
import pages.register.settlors.living_settlor.trust_type._
import play.api.i18n.Messages
import play.api.mvc.Call
import play.twirl.api.HtmlFormat
import queries.Gettable
import viewmodels.{AnswerRow, AnswerSection}

object TrustType {

  import utils.print.playback.sections.AnswerRowConverter._

  def apply(userAnswers: UserAnswers)(implicit messages: Messages): AnswerSection =
    AnswerSection(
      headingKey = Some(messages("answerPage.section.trustType.heading")),
      Seq(
        yesNoQuestion(SetUpAfterSettlorDiedYesNoPage, userAnswers, "setUpAfterSettlorDied"),
        kindOfTrustQuestion(KindOfTrustPage, userAnswers, "kindOfTrust"),
        yesNoQuestion(SetUpInAdditionToWillTrustYesNoPage, userAnswers, "setupInAdditionToWillTrustYesNo"),
        deedOfVariationQuestion(HowDeedOfVariationCreatedPage, userAnswers, "howDeedOfVariationCreated"),
        yesNoQuestion(HoldoverReliefYesNoPage, userAnswers, "holdoverReliefYesNo"),
        yesNoQuestion(EfrbsYesNoPage, userAnswers, "employerFinancedRetirementBenefitsSchemeYesNo"),
        dateQuestion(EfrbsStartDatePage, userAnswers, "employerFinancedRetirementBenefitsSchemeStartDate")
      ).flatten,
      sectionKey = None
    )

  private def deedOfVariationQuestion(query: Gettable[DeedOfVariation], userAnswers: UserAnswers, labelKey: String,
                    messageArg: String = "", changeRoute: Option[Call] = None)
                   (implicit messages:Messages) = {
    userAnswers.get(query) map {x =>
      AnswerRow(
        messages(s"${labelKey}.checkYourAnswersLabel", messageArg),
        HtmlFormat.escape(x.toString),
        None
      )
    }
  }

}
