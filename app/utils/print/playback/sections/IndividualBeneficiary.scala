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
import models.registration.pages.RoleInCompany
import pages.register.beneficiaries.individual._
import play.api.i18n.Messages
import play.api.mvc.Call
import play.twirl.api.HtmlFormat
import queries.Gettable
import utils.CheckAnswersFormatters
import utils.countryOptions.CountryOptions
import utils.print.playback.sections.AnswerRowConverter._
import viewmodels.{AnswerRow, AnswerSection}

object IndividualBeneficiary {

  def apply(index: Int,
            userAnswers: UserAnswers,
            countryOptions: CountryOptions)
           (implicit messages: Messages): Seq[AnswerSection] = {

    userAnswers.get(IndividualBeneficiaryNamePage(index)).map(CheckAnswersFormatters.fullName).map { name =>
      Seq(
        AnswerSection(
          headingKey = Some(messages("answerPage.section.individualBeneficiary.subheading") + s" ${index + 1}"),
          Seq(
            fullNameQuestion(IndividualBeneficiaryNamePage(index), userAnswers, "individualBeneficiaryName"),
            roleInCompanyQuestion(IndividualBeneficiaryRoleInCompanyPage(index), userAnswers, "individualBeneficiaryRoleInCompany", name),
            yesNoQuestion(IndividualBeneficiaryDateOfBirthYesNoPage(index), userAnswers, "individualBeneficiaryDateOfBirthYesNo", name),
            dateQuestion(IndividualBeneficiaryDateOfBirthPage(index), userAnswers, "individualBeneficiaryDateOfBirth", name),
            yesNoQuestion(IndividualBeneficiaryIncomeYesNoPage(index), userAnswers, "individualBeneficiaryIncomeYesNo", name),
            monetaryAmountQuestion(IndividualBeneficiaryIncomePage(index), userAnswers, "individualBeneficiaryIncome", name ),
            yesNoQuestion(IndividualBeneficiaryNationalInsuranceYesNoPage(index), userAnswers, "individualBeneficiaryNationalInsuranceYesNo", name),
            ninoQuestion(IndividualBeneficiaryNationalInsuranceNumberPage(index), userAnswers, "individualBeneficiaryNationalInsuranceNumber", name),
            yesNoQuestion(IndividualBeneficiaryAddressYesNoPage(index), userAnswers, "individualBeneficiaryAddressYesNo", name),
            yesNoQuestion(IndividualBeneficiaryAddressUKYesNoPage(index), userAnswers, "individualBeneficiaryAddressUKYesNo", name),
            addressQuestion(IndividualBeneficiaryAddressPage(index), userAnswers, "individualBeneficiaryAddressUK", name, countryOptions),
            yesNoQuestion(IndividualBeneficiaryPassportIDCardYesNoPage(index), userAnswers, "individualBeneficiaryPassportIDCardYesNo", name),
            passportOrIdCardQuestion(IndividualBeneficiaryPassportIDCardPage(index), userAnswers, "individualBeneficiaryPassportIDCard", name, countryOptions),
            yesNoQuestion(IndividualBeneficiaryVulnerableYesNoPage(index), userAnswers, "individualBeneficiaryVulnerableYesNo", name)
          ).flatten,
          sectionKey = None
        )
      )
    }.getOrElse(Nil)
  }

  private def roleInCompanyQuestion(query: Gettable[RoleInCompany], userAnswers: UserAnswers, labelKey: String,
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
