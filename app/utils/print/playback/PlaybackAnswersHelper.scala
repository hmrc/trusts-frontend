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

import javax.inject.Inject
import models.core.pages.IndividualOrBusiness
import models.playback.UserAnswers
import pages.register.trustees.{IsThisLeadTrusteePage, TrusteeIndividualOrBusinessPage}
import play.api.Logger
import play.api.i18n.Messages
import utils.countryOptions.CountryOptions
import utils.print.playback.sections._
import viewmodels.AnswerSection

class PlaybackAnswersHelper @Inject()(countryOptions: CountryOptions)
                                     (userAnswers: UserAnswers)
                                     (implicit messages: Messages) {

  def trustee(index: Int): Option[Seq[AnswerSection]] = {

    userAnswers.get(IsThisLeadTrusteePage(index)) flatMap { isLeadTrustee =>
      userAnswers.get(TrusteeIndividualOrBusinessPage(index)) flatMap { individualOrBusiness =>
        if (isLeadTrustee) {
          individualOrBusiness match {
            case IndividualOrBusiness.Individual => LeadTrusteeIndividual(index, userAnswers, countryOptions)
            case IndividualOrBusiness.Business => LeadTrusteeBusiness(index, userAnswers, countryOptions)
          }
        } else {
          TrusteeOrganisation(index, userAnswers, countryOptions)
        }
      }
    }

  }

  def deceasedSettlor: Option[Seq[AnswerSection]] = DeceasedSettlor(userAnswers, countryOptions)

  def charityBeneficiaries : Option[Seq[AnswerSection]] = {
    val path = _root_.sections.beneficiaries.CharityBeneficiaries.toString
    val size = (userAnswers.data \\ path).size

    size match {
      case 0 => None
      case _ =>
        val sections = (for (index <- 0 to size) yield {
          val sec = CharityBeneficiary(index, userAnswers, countryOptions)

          Logger.debug(s"[Generated following answers $sec]")
          sec
        }).flatten

        Some(sections.flatten)
    }
  }

}
