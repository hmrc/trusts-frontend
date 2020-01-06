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

import models.core.pages.IndividualOrBusiness
import models.playback.UserAnswers
import pages.register.trustees.{IsThisLeadTrusteePage, TrusteeIndividualOrBusinessPage}
import play.api.i18n.Messages
import utils.countryOptions.CountryOptions
import utils.print.playback.sections._
import utils.print.playback.sections.protectors.{CompanyProtector, IndividualProtector}
import viewmodels.AnswerSection

class PlaybackAnswersHelper(countryOptions: CountryOptions, userAnswers: UserAnswers)
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

  def beneficiaries : Seq[AnswerSection] = {

    val beneficiaries = Seq(
      individualBeneficiaries,
      charityBeneficiaries,
      companyBeneficiaries
    ).flatten

    if (beneficiaries.nonEmpty) {
      Seq(
        Seq(AnswerSection(sectionKey = Some("answerPage.section.beneficiaries.heading"))),
        beneficiaries
      ).flatten
    } else {
      Nil
    }
  }

  private def companyBeneficiaries : Seq[AnswerSection] = {
    val size = userAnswers.get(_root_.sections.beneficiaries.CompanyBeneficiaries).map(_.value.size).getOrElse(0)

    size match {
      case 0 => Nil
      case _ =>
        (for (index <- 0 to size) yield CompanyBeneficiary(index, userAnswers, countryOptions)).flatten
    }
  }

  private def charityBeneficiaries : Seq[AnswerSection] = {
    val size = userAnswers.get(_root_.sections.beneficiaries.CharityBeneficiaries).map(_.value.size).getOrElse(0)

    size match {
      case 0 => Nil
      case _ =>
        (for (index <- 0 to size) yield CharityBeneficiary(index, userAnswers, countryOptions)).flatten
    }
  }

  private def individualBeneficiaries : Seq[AnswerSection] = {
    val size = userAnswers.get(_root_.sections.beneficiaries.IndividualBeneficiaries).map(_.size).getOrElse(0)

    size match {
      case 0 => Nil
      case _ =>
        (for (index <- 0 to size) yield IndividualBeneficiary(index, userAnswers, countryOptions)).flatten
    }
  }

  def protectors : Seq[AnswerSection] = {

    val protectors: Seq[AnswerSection] = individualProtectors ++ companyProtectors

    if (protectors.nonEmpty) {
      Seq(
        Seq(AnswerSection(sectionKey = Some(messages("answerPage.section.protectors.heading")))),
        protectors
      ).flatten
    } else {
      Nil
    }
  }

  private def individualProtectors : Seq[AnswerSection] = {
    val size = userAnswers.get(_root_.sections.protectors.IndividualProtectors).map(_.value.size).getOrElse(0)

    size match {
      case 0 => Nil
      case _ =>
        (for (index <- 0 to size) yield IndividualProtector(index, userAnswers, countryOptions)).flatten
    }
  }

  private def companyProtectors : Seq[AnswerSection] = {
    val size = userAnswers.get(_root_.sections.protectors.CompanyProtectors).map(_.value.size).getOrElse(0)

    size match {
      case 0 => Nil
      case _ =>
        (for (index <- 0 to size) yield CompanyProtector(index, userAnswers, countryOptions)).flatten
    }
  }

}
