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
import viewmodels.AnswerSection
import utils.print.playback.sections.AnswerRowConverter._

object TrusteeIndividual {

  def apply(index: Int,
            userAnswers: UserAnswers,
            countryOptions: CountryOptions)
           (implicit messages: Messages): Seq[AnswerSection] = {

    userAnswers.get(TrusteesNamePage(index)).map(CheckAnswersFormatters.fullName).map { name =>
      Seq(
        AnswerSection(
          headingKey = Some(messages("answerPage.section.trustee.subheading") + s" ${index + 1}"),
          Seq(
            yesNoQuestion(IsThisLeadTrusteePage(index), userAnswers, "isThisLeadTrustee"),
            individualOrBusinessQuestion(TrusteeIndividualOrBusinessPage(index), userAnswers, "trusteeIndividualOrBusiness"),
            fullNameQuestion(TrusteesNamePage(index), userAnswers, "trusteesName"),
            dateQuestion(TrusteesDateOfBirthPage(index), userAnswers, "trusteesDateOfBirth", name)
          ).flatten,
          sectionKey = None
        )
      )
    }.getOrElse(Nil)
  }

}
