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

import java.time.LocalDate

import models.core.pages.{Address, FullName, InternationalAddress, UKAddress}
import models.playback.UserAnswers
import models.registration.pages.RoleInCompany
import pages.register.beneficiaries.individual._
import play.api.i18n.Messages
import play.api.libs.json.Reads
import play.api.mvc.Call
import play.twirl.api.HtmlFormat
import queries.Gettable
import utils.CheckYourAnswersHelper
import utils.CheckYourAnswersHelper.yesOrNo
import utils.countryOptions.CountryOptions
import viewmodels.{AnswerRow, AnswerSection}

object IndividualBeneficiary {


  def apply(index: Int,
            userAnswers: UserAnswers,
            countryOptions: CountryOptions)
           (implicit messages: Messages): Seq[AnswerSection] = {

    userAnswers.get(IndividualBeneficiaryNamePage(index)).map(displayName).map { name =>
      Seq(
        AnswerSection(
          headingKey = Some(messages("answerPage.section.individualBeneficiary.subheading") + s" ${index + 1}"),
          Seq(
            fullNameQuestion(IndividualBeneficiaryNamePage(index), userAnswers, "individualBeneficiaryName"),
            yesNoQuestion(IndividualBeneficiaryDateOfBirthYesNoPage(index), userAnswers, "individualBeneficiaryDateOfBirthYesNo", name),
            dateQuestion(IndividualBeneficiaryDateOfBirthPage(index), userAnswers, "individualBeneficiaryDateOfBirth", name),
            yesNoQuestion(IndividualBeneficiaryIncomeYesNoPage(index), userAnswers, "individualBeneficiaryIncomeYesNo", name),
            monetaryAmountQuestion(IndividualBeneficiaryIncomePage(index), userAnswers, labelKey = "individualBeneficiaryIncome", name ),
            yesNoQuestion(IndividualBeneficiaryNationalInsuranceYesNoPage(index), userAnswers, "individualBeneficiaryNationalInsuranceYesNo", name),
            ninoQuestion(IndividualBeneficiaryNationalInsuranceNumberPage(index), userAnswers, "individualBeneficiaryNationalInsuranceNumber", name),
            yesNoQuestion(IndividualBeneficiaryAddressYesNoPage(index), userAnswers, "individualBeneficiaryAddressYesNo", name),
            yesNoQuestion(IndividualBeneficiaryAddressUKYesNoPage(index), userAnswers, "individualBeneficiaryAddressUKYesNo", name),
            addressQuestion(IndividualBeneficiaryAddressPage(index), userAnswers, "individualBeneficiaryAddressUK", name, None, countryOptions),
            yesNoQuestion(IndividualBeneficiaryVulnerableYesNoPage(index), userAnswers, "individualBeneficiaryVulnerableYesNo", name),
            stringRow(IndividualBeneficiaryRoleInCompanyPage(index), userAnswers, "individualBeneficiaryRoleInCompany", name),
            yesNoQuestion(IndividualBeneficiaryPassportIDCardYesNoPage(index), userAnswers, "individualBeneficiaryPassportYesNo", name),
            stringRow(IndividualBeneficiaryPassportIDCardPage(index), userAnswers, "individualBeneficiaryPassport", name)
          ).flatten,
          sectionKey = None
        )
      )
    }.getOrElse(Nil)
  }

  def displayName(fullname: FullName) = {
    val middle = fullname.middleName.map(" " + _ + " ").getOrElse(" ")
    s"${fullname.firstName}${middle}${fullname.lastName}"
  }

  def ninoQuestion(query: Gettable[String], userAnswers: UserAnswers, labelKey: String,
                      messageArg: String = "", changeRoute: Option[Call] = None)
                     (implicit messages:Messages) = {
    userAnswers.get(query) map {x =>
      AnswerRow(
        messages(s"${labelKey}.checkYourAnswersLabel", messageArg),
        HtmlFormat.escape(CheckYourAnswersHelper.formatNino(x)),
        None
      )
    }
  }

  def addressQuestion(query: Gettable[Address], userAnswers: UserAnswers, labelKey: String,
                      messageArg: String = "", changeRoute: Option[Call] = None, countryOptions: CountryOptions)
                     (implicit messages:Messages) = {

    userAnswers.get(query) map {
      case x: UKAddress =>
        AnswerRow(
          messages(s"${labelKey}.checkYourAnswersLabel", messageArg),
          CheckYourAnswersHelper.ukAddress(x),
          None
        )
      case x: InternationalAddress =>
        AnswerRow(
          messages(s"${labelKey}.checkYourAnswersLabel", messageArg),
          CheckYourAnswersHelper.internationalAddress(x, countryOptions),
          None
        )
    }
  }

  def monetaryAmountQuestion(query: Gettable[String], userAnswers: UserAnswers, labelKey: String,
                             messageArg: String = "", changeRoute: Option[Call] = None)
                            (implicit messages:Messages) = {
    userAnswers.get(query) map {x =>
      AnswerRow(
        messages(s"${labelKey}.checkYourAnswersLabel", messageArg),
        CheckYourAnswersHelper.currency(x),
        None
      )
    }
  }

  def dateQuestion(query: Gettable[LocalDate], userAnswers: UserAnswers, labelKey: String,
                   messageArg: String = "", changeRoute: Option[Call] = None)
                  (implicit messages:Messages) = {
    userAnswers.get(query) map {x =>
      AnswerRow(
        messages(s"${labelKey}.checkYourAnswersLabel", messageArg),
        HtmlFormat.escape(x.format(CheckYourAnswersHelper.dateFormatter)),
        None
      )
    }
  }

  def yesNoQuestion(query: Gettable[Boolean], userAnswers: UserAnswers, labelKey: String,
    messageArg: String = "", changeRoute: Option[Call] = None)
  (implicit messages:Messages) = {
    userAnswers.get(query) map {x =>
      AnswerRow(
        messages(s"${labelKey}.checkYourAnswersLabel", messageArg),
        yesOrNo(x),
        None
      )
    }
  }
  def fullNameQuestion(query: Gettable[FullName], userAnswers: UserAnswers, labelKey: String,
                       messageArg: String = "", changeRoute: Option[Call] = None)
                      (implicit messages:Messages) = {
    userAnswers.get(query) map {x =>
      AnswerRow(
        messages(s"${labelKey}.checkYourAnswersLabel", messageArg),
        HtmlFormat.escape(displayName(x)),
        None
      )
    }
  }

  def stringRow[T](query: Gettable[T], userAnswers: UserAnswers, labelKey: String,
                    messageArg: String = "", changeRoute: Option[Call] = None)
                   (implicit messages:Messages, rds: Reads[T]) = {
    userAnswers.get(query) map {x =>
      AnswerRow(
        messages(s"${labelKey}.checkYourAnswersLabel", messageArg),
        HtmlFormat.escape(x.toString),
        None
      )
    }
  }

}
