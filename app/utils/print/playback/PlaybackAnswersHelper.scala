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

package utils.print.playback

import models.playback.UserAnswers
import play.api.i18n.Messages
import utils.countryOptions.CountryOptions
import utils.print.playback.sections._
import viewmodels.AnswerSection

class PlaybackAnswersHelper(countryOptions: CountryOptions, userAnswers: UserAnswers)
                           (implicit messages: Messages) {
  def leadTrustee: Seq[AnswerSection] = {
    LeadTrusteeIndividual(0, userAnswers, countryOptions)
  }


  def charityBeneficiaries : Seq[AnswerSection] = {
    val size = userAnswers.get(_root_.sections.beneficiaries.CharityBeneficiaries).map(_.value.size).getOrElse(0)

    size match {
      case 0 => Nil
      case _ =>
        (for (index <- 0 to size) yield CharityBeneficiary(index, userAnswers, countryOptions)).flatten
    }
  }

  def individualBeneficiaries : Seq[AnswerSection] = {
    val size = userAnswers.get(_root_.sections.beneficiaries.IndividualBeneficiaries).map(_.size).getOrElse(0)

    size match {
      case 0 => Nil
      case _ =>
        (for (index <- 0 to size) yield IndividualBeneficiary(index, userAnswers, countryOptions)).flatten
    }
  }
}
