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

package utils.print.playback.sections.trustees.lead_trustee

import models.playback.UserAnswers
import pages.register.trustees._
import pages.register.trustees.organisation.{TrusteeOrgNamePage, TrusteeUtrYesNoPage, TrusteesUtrPage}
import play.api.i18n.Messages
import utils.countryOptions.CountryOptions
import utils.print.playback.sections.AnswerRowConverter.{stringQuestion, yesNoQuestion}
import viewmodels.AnswerSection

object LeadTrusteeBusiness extends LeadTrustee {

  def apply(index: Int, userAnswers: UserAnswers, countryOptions: CountryOptions)(implicit messages: Messages): Option[Seq[AnswerSection]] = {

    userAnswers.get(TrusteeOrgNamePage(index)).flatMap { name =>
      Some(
        Seq(
          AnswerSection(
            headingKey = Some(messages("answerPage.section.leadTrustee.subheading")),
            Seq(
              yesNoQuestion(TrusteeUtrYesNoPage(index), userAnswers, "leadTrusteeUtrYesNo", name),
              stringQuestion(TrusteeOrgNamePage(index), userAnswers, "trusteeBusinessName"),
              stringQuestion(TrusteesUtrPage(index), userAnswers, "trusteeUtr", name)
            ).flatten ++
              addressAnswers(index, userAnswers, countryOptions, name).flatten ++
              Seq(yesNoQuestion(TrusteeEmailYesNoPage(index), userAnswers, "trusteeEmailAddressYesNo", name),
                stringQuestion(EmailPage(index), userAnswers, "trusteeEmailAddress", name),
                stringQuestion(TelephoneNumberPage(index), userAnswers, "trusteeTelephoneNumber", name)
              ).flatten,
            sectionKey = Some(messages("answerPage.section.trustees.heading"))
          )
        )
      )
    }
  }

}
