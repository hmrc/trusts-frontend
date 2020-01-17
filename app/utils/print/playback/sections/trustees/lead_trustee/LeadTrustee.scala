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
import pages.register.CorrespondenceAddressPage
import pages.register.trustees._
import play.api.i18n.Messages
import utils.countryOptions.CountryOptions
import utils.print.playback.sections.AnswerRowConverter.{addressQuestion, yesNoQuestion}
import viewmodels.AnswerRow

class LeadTrustee {

  def addressAnswers(index: Int,
                     userAnswers: UserAnswers,
                     countryOptions: CountryOptions,
                     name: String)(implicit messages: Messages): Seq[Option[AnswerRow]] = {

    userAnswers.get(TrusteeAddressPage(index)) match {
      case Some(_) =>
        Seq(
          yesNoQuestion(TrusteeAddressInTheUKPage(index), userAnswers, "trusteeLiveInTheUK", name),
          addressQuestion(TrusteeAddressPage(index), userAnswers, "trusteesUkAddress", name, countryOptions)
        )
      case _ =>
        Seq(
          addressQuestion(CorrespondenceAddressPage, userAnswers, "trusteesUkAddress", name, countryOptions)
        )
    }
  }
}
