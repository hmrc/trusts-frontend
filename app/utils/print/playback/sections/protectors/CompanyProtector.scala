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

package utils.print.playback.sections.protectors

import models.playback.UserAnswers
import pages.register.protectors.company._
import play.api.i18n.Messages
import utils.countryOptions.CountryOptions
import utils.print.playback.sections.AnswerRowConverter
import viewmodels.AnswerSection

object CompanyProtector {

  import AnswerRowConverter._

  def apply(index: Int, userAnswers: UserAnswers, countryOptions: CountryOptions)(implicit messages: Messages): Seq[AnswerSection] =
    userAnswers.get(CompanyProtectorNamePage(index)).map { protectorName =>
      Seq(AnswerSection(
        headingKey = Some(messages("answerPage.section.companyProtector.subheading", index + 1)),
        Seq(
          stringQuestion(CompanyProtectorNamePage(index), userAnswers, "companyProtectorName", protectorName),
          addressOrUtrQuestion(CompanyProtectorAddressOrUtrPage(index), userAnswers, "companyProtectorAddressOrUtr", protectorName),
          stringQuestion(CompanyProtectorUtrPage(index), userAnswers, "companyProtectorUtr", protectorName),
          yesNoQuestion(CompanyProtectorAddressUKYesNoPage(index), userAnswers, "companyProtectorAddressUkYesNo", protectorName),
          addressQuestion(CompanyProtectorAddressPage(index), userAnswers, "companyProtectorAddress", protectorName, countryOptions)
        ).flatten,
        None
      ))
    }.getOrElse(Nil)



}