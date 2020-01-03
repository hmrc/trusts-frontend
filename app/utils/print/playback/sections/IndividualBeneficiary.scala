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

import models.NormalMode
import models.core.pages.{FullName, InternationalAddress, UKAddress}
import models.playback.UserAnswers
import java.time.LocalDate

import pages.register.beneficiaries.charity._
import pages.register.beneficiaries.individual._
import play.api.i18n.Messages
import play.api.libs.json.Reads
import play.api.mvc.Call
import play.twirl.api.{Html, HtmlFormat}
import queries.Gettable
import utils.CheckYourAnswersHelper
import utils.CheckYourAnswersHelper.{dateFormatter, formatNino, indBeneficiaryName, internationalAddress, ukAddress, yesOrNo}
import utils.countryOptions.CountryOptions
import viewmodels.{AnswerRow, AnswerSection}

object IndividualBeneficiary {


  def apply(index: Int,
            userAnswers: UserAnswers,
            countryOptions: CountryOptions)
           (implicit messages: Messages): Seq[AnswerSection] = {

    val x = userAnswers.get(IndividualBeneficiaryNamePage(index)).map(displayName).map {name =>
      Seq(
        AnswerSection(
          headingKey = Some(messages("answerPage.section.individualBeneficiary.subheading", index + 1)),
          Seq(
            fullNameQuestion(IndividualBeneficiaryNamePage(index), userAnswers, "individualBeneficiaryName"),
            yesNoQuestion(IndividualBeneficiaryDateOfBirthYesNoPage(index), userAnswers, "individualBeneficiaryDateOfBirthYesNo", name),
            dateQuestion(IndividualBeneficiaryDateOfBirthPage(index), userAnswers, "individualBeneficiaryDateOfBirth", name),
            yesNoQuestion(IndividualBeneficiaryIncomeYesNoPage(index), userAnswers, "individualBeneficiaryIncomeYesNo", name)
            individualBeneficiaryIncome(index, userAnswers),
            yesNoQuestion(IndividualBeneficiaryNationalInsuranceYesNoPage(index), userAnswers, "individualBeneficiaryNationalInsuranceYesNo", name)
            individualBeneficiaryNationalInsuranceNumber(index, userAnswers),
            yesNoQuestion(IndividualBeneficiaryAddressYesNoPage(index), userAnswers, "individualBeneficiaryAddressYesNo", name),
            yesNoQuestion(IndividualBeneficiaryAddressUKYesNoPage(index), userAnswers, "individualBeneficiaryAddressUKYesNo", name)
            individualBeneficiaryAddressUK(index, userAnswers),
            yesNoQuestion(IndividualBeneficiaryVulnerableYesNoPage(index), userAnswers, "individualBeneficiaryVulnerableYesNo", name)
          ).flatten,
          sectionKey = None
        )
      )
    }.getOrElse(Nil)
  }

  def question[T](renderer: T => Html)
                 (query: Gettable[T],
                  userAnswers: UserAnswers,
                  labelKey: String,
                  messageArg: String = "",
                  changeRoute: Option[Call] = None)
                   (implicit messages:Messages, reads : Reads[T]) = {
    userAnswers.get[T](query) map {
      x =>
        AnswerRow(
          messages(s"${labelKey}.checkYourAnswersLabel", messageArg),
          renderer(x),
          None
        )
    }
  }



  def displayName(fullName: FullName)= s"${fullName.firstName} ${fullName.middleName.getOrElse("")} ${fullName.lastName}"

  val dateFormatter = (x:LocalDate) => HtmlFormat.escape(x.format(dateFormatter))
  def yesNoFormatter(implicit messages: Messages) = yesOrNo(_)
  val fullNameFormatter = (x: FullName) => HtmlFormat.escape(displayName(x))
  val monetaryAmountFormatter = CheckYourAnswersHelper.currency _

  def monetaryAmountQuestion(implicit messages:Messages) = question(monetaryAmountFormatter)
  def dateQuestion(implicit messages:Messages) = question(dateFormatter)
  def yesNoQuestion(implicit messages:Messages) = question(yesNoFormatter)
  def fullNameQuestion(implicit messages:Messages) = question(fullNameFormatter)

}
