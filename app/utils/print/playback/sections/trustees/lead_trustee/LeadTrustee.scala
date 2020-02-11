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

import models.core.pages.IndividualOrBusiness.Individual
import models.playback.UserAnswers
import pages.register.trust_details.{CorrespondenceAddressInTheUKPage, CorrespondenceAddressPage}
import pages.register.trustees._
import pages.register.trustees.individual.{TrusteeAddressInTheUKPage, TrusteeAddressPage}
import play.api.i18n.Messages
import utils.countryOptions.CountryOptions
import utils.print.playback.sections.AnswerRowConverter.{addressQuestion, yesNoQuestion}
import viewmodels.AnswerRow

trait LeadTrustee {

  def addressAnswers(index: Int,
                     userAnswers: UserAnswers,
                     countryOptions: CountryOptions,
                     name: String)(implicit messages: Messages): Seq[Option[AnswerRow]] = {

    val addressKey = userAnswers.get(TrusteeIndividualOrBusinessPage(index)) match {
      case Some(Individual) => "trusteeLiveInTheUK"
      case _ => "trusteeBusinessAddressInUK"
    }

    userAnswers.get(TrusteeAddressPage(index)) match {
      case Some(_) =>
        Seq(
          yesNoQuestion(TrusteeAddressInTheUKPage(index), userAnswers, addressKey, name),
          addressQuestion(TrusteeAddressPage(index), userAnswers, "trusteesUkAddress", name, countryOptions)
        )
      case _ =>
        Seq(
          yesNoQuestion(CorrespondenceAddressInTheUKPage, userAnswers, addressKey, name),
          addressQuestion(CorrespondenceAddressPage, userAnswers, "trusteesUkAddress", name, countryOptions)
        )
    }
  }
}