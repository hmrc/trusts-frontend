/*
 * Copyright 2019 HM Revenue & Customs
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

package utils

import javax.inject.Inject
import models.core.pages.{IndividualOrBusiness, InternationalAddress, UKAddress}
import models.playback.UserAnswers
import pages.register.beneficiaries.charity._
import pages.register.beneficiaries.classOfBeneficiary._
import pages.register.beneficiaries.company._
import pages.register.beneficiaries.other._
import pages.register.beneficiaries.trust._
import pages.register.settlors.deceased_settlor._
import pages.register.settlors.living_settlor._
import pages.register.trustees._
import play.api.i18n.Messages
import play.twirl.api.HtmlFormat
import utils.CheckYourAnswersHelper._
import utils.countryOptions.CountryOptions
import viewmodels.{AnswerRow, AnswerSection}

class PlaybackAnswersHelper @Inject()(countryOptions: CountryOptions)(userAnswers: UserAnswers)
                                     (implicit messages: Messages) {

  def trustee(index: Int): Option[Seq[AnswerSection]] = {

    userAnswers.get(IsThisLeadTrusteePage(index)) flatMap { isLeadTrustee =>
      userAnswers.get(TrusteeIndividualOrBusinessPage(index)) flatMap { individualOrBusiness =>
        if(isLeadTrustee) {
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

  def charityBeneficiary(index: Int): Option[Seq[AnswerSection]] = CharityBeneficiarySection(index, userAnswers, countryOptions)

}

object TrusteeOrganisation {

  def apply(index: Int, userAnswers: UserAnswers, countryOptions: CountryOptions)(implicit messages: Messages): Option[Seq[AnswerSection]] = {

    val questions = Seq(
    ).flatten

    if (name(index, userAnswers).nonEmpty) {
      Some(Seq(AnswerSection(
        headingKey = None,
        questions,
        sectionKey = Some(messages("answerPage.section.settlorCompany.heading"))
      )))
    } else {
      None
    }
  }

  def name(index: Int, userAnswers: UserAnswers): Option[AnswerRow] = userAnswers.get(TrusteeOrgNamePage(index)) map { x =>
    AnswerRow(
      "trusteeOrgName.checkYourAnswersLabel",
      HtmlFormat.escape(x),
      None
    )
  }

  def utrYesNo(index: Int, userAnswers: UserAnswers)(implicit messages: Messages): Option[AnswerRow] =
    userAnswers.get(TrusteeUTRYesNoPagePage(index)) map {
      x =>
        AnswerRow(
          "trusteeOrgUtrYesNo.checkYourAnswersLabel",
          yesOrNo(x),
          None
        )
    }

  def utr(index: Int, userAnswers: UserAnswers): Option[AnswerRow] = userAnswers.get(TrusteesUtrPage(index)) map {
    x =>
      AnswerRow(
        "trusteeUtr.checkYourAnswersLabel",
        HtmlFormat.escape(x.format(dateFormatter)),
        None
      )
  }

  def addressUKYesNo(index: Int, userAnswers: UserAnswers)
                    (implicit messages: Messages): Option[AnswerRow] = userAnswers.get(TrusteeLiveInTheUKPage(index)) map {
    x =>
      AnswerRow(
        "trusteeOrgAddressUKYesNo.checkYourAnswersLabel",
        yesOrNo(x),
        None
      )
  }

  def addressUK(index: Int, userAnswers: UserAnswers)
               (implicit messages: Messages): Option[AnswerRow] = userAnswers.get(TrusteesUkAddressPage(index)) map {
    x =>
      AnswerRow(
        "trusteeOrgUKAddress.checkYourAnswersLabel",
        ukAddress(x),
        None
      )
  }

  def nonUKAddress(index: Int, userAnswers: UserAnswers, countryOptions: CountryOptions)
                  (implicit messages: Messages): Option[AnswerRow] = userAnswers.get(TrusteesInternationalAddressPage(index)) map {
    x =>
      AnswerRow(
        "trusteeOrgNonUKAddress.checkYourAnswersLabel",
        internationalAddress(x, countryOptions),
        None
      )
  }

  def telephone(index: Int, userAnswers: UserAnswers)
               (implicit messages: Messages): Option[AnswerRow] = userAnswers.get(TelephoneNumberPage(index)) map {
    x =>
      AnswerRow(
        "trusteeTelephone.checkYourAnswersLabel",
        HtmlFormat.escape(x),
        None
      )
  }

  def email(index: Int, userAnswers: UserAnswers)
           (implicit messages: Messages): Option[AnswerRow] = userAnswers.get(EmailPage(index)) map {
    x =>
      AnswerRow(
        "trusteeEmail.checkYourAnswersLabel",
        HtmlFormat.escape(x),
        None
      )
  }
}

object LeadTrusteeBusiness {

  def apply(index: Int, userAnswers: UserAnswers, countryOptions: CountryOptions)(implicit messages: Messages): Option[Seq[AnswerSection]] = {
    if (name(index, userAnswers).nonEmpty) {
      Some(Seq(AnswerSection(
        headingKey = None,
        Seq(
          name(index, userAnswers),
          addressUKYesNo(index, userAnswers),
          addressUK(index, userAnswers),
          addressNonUK(index, userAnswers, countryOptions),
          telephone(index, userAnswers),
          email(index, userAnswers)
        ).flatten,
        sectionKey = Some(messages("answerPage.section.leadTrusteeIndividual.heading"))
      )))
    } else {
      None
    }
  }

  def name(index: Int, userAnswers: UserAnswers): Option[AnswerRow] = userAnswers.get(TrusteesNamePage(index)) map {
    x =>
      AnswerRow(
        "trusteeName.checkYourAnswersLabel",
        HtmlFormat.escape(s"${x.firstName} ${x.middleName.getOrElse("")} ${x.lastName}"),
        None
      )
  }

  def addressUKYesNo(index: Int, userAnswers: UserAnswers)
                    (implicit messages: Messages): Option[AnswerRow] = userAnswers.get(TrusteeLiveInTheUKPage(index)) map {
    x =>
      AnswerRow(
        "trusteeAddressUKYesNo.checkYourAnswersLabel",
        yesOrNo(x),
        None
      )
  }

  def addressUK(index: Int, userAnswers: UserAnswers)
               (implicit messages: Messages): Option[AnswerRow] = userAnswers.get(TrusteesUkAddressPage(index)) map {
    x =>
      AnswerRow(
        "trusteeAddressUK.checkYourAnswersLabel",
        ukAddress(x),
        None
      )
  }

  def addressNonUK(index: Int, userAnswers: UserAnswers, countryOptions: CountryOptions)
                  (implicit messages: Messages): Option[AnswerRow] = userAnswers.get(TrusteesInternationalAddressPage(index)) map {
    x =>
      AnswerRow(
        "trusteeAddressNonUK.checkYourAnswersLabel",
        internationalAddress(x, countryOptions),
        None
      )
  }

  def telephone(index: Int, userAnswers: UserAnswers)
               (implicit messages: Messages): Option[AnswerRow] = userAnswers.get(TelephoneNumberPage(index)) map {
    x =>
      AnswerRow(
        "trusteeTelephone.checkYourAnswersLabel",
        HtmlFormat.escape(x),
        None
      )
  }

  def email(index: Int, userAnswers: UserAnswers)
           (implicit messages: Messages): Option[AnswerRow] = userAnswers.get(EmailPage(index)) map {
    x =>
      AnswerRow(
        "trusteeEmail.checkYourAnswersLabel",
        HtmlFormat.escape(x),
        None
      )
  }

}

object LeadTrusteeIndividual {

  def apply(index: Int, userAnswers: UserAnswers, countryOptions: CountryOptions)(implicit messages: Messages): Option[Seq[AnswerSection]] = {
    if (name(index, userAnswers).nonEmpty) {
      Some(Seq(AnswerSection(
        headingKey = None,
        Seq(
          name(index, userAnswers),
          dateOfBirth(index, userAnswers),
          isUKCitizen(index, userAnswers),
          nino(index, userAnswers),
          trusteePassportOrIDCard(index, userAnswers, countryOptions),
          addressUKYesNo(index, userAnswers),
          addressUK(index, userAnswers, countryOptions),
          nonUKAddress(index, userAnswers, countryOptions),
          telephone(index, userAnswers),
          email(index, userAnswers)
        ).flatten,
        sectionKey = Some(messages("answerPage.section.leadTrusteeIndividual.heading"))
      )))
    } else {
      None
    }
  }

  def name(index: Int, userAnswers: UserAnswers): Option[AnswerRow] = userAnswers.get(TrusteesNamePage(index)) map {
    x =>
      AnswerRow(
        "trusteeName.checkYourAnswersLabel",
        HtmlFormat.escape(s"${x.firstName} ${x.middleName.getOrElse("")} ${x.lastName}"),
        None
      )
  }

  def dateOfBirth(index: Int, userAnswers: UserAnswers): Option[AnswerRow] = userAnswers.get(TrusteesDateOfBirthPage(index)) map {
    x =>
      AnswerRow(
        "trusteeDateOfBirth.checkYourAnswersLabel",
        HtmlFormat.escape(x.format(dateFormatter)),
        None
      )
  }

  def isUKCitizen(index: Int, userAnswers: UserAnswers)(implicit messages: Messages): Option[AnswerRow] =
    userAnswers.get(TrusteeAUKCitizenPage(index)) map {
      x =>
        AnswerRow(
          "trusteeUKCitizen.checkYourAnswersLabel",
          yesOrNo(x),
          None
        )
    }

  def nino(index: Int, userAnswers: UserAnswers): Option[AnswerRow] = userAnswers.get(TrusteesNinoPage(index)) map {
    x =>
      AnswerRow(
        "trusteeNationalInsuranceNumber.checkYourAnswersLabel",
        HtmlFormat.escape(formatNino(x)),
        None
      )
  }

  def trusteePassportOrIDCard(index: Int, userAnswers: UserAnswers, countryOptions: CountryOptions): Option[AnswerRow] =
    userAnswers.get(TrusteePassportIDCardPage(index)) map {
      x =>
        AnswerRow(
          "trusteeNationalInsuranceNumber.checkYourAnswersLabel",
          passportOrIDCard(x, countryOptions),
          None
        )
    }

  def addressUKYesNo(index: Int, userAnswers: UserAnswers)
                    (implicit messages: Messages): Option[AnswerRow] = userAnswers.get(TrusteeLiveInTheUKPage(index)) map {
    x =>
      AnswerRow(
        "trusteeAddressUKYesNo.checkYourAnswersLabel",
        yesOrNo(x),
        None
      )
  }

  def addressUK(index: Int, userAnswers: UserAnswers, countryOptions: CountryOptions)
               (implicit messages: Messages): Option[AnswerRow] = userAnswers.get(TrusteesUkAddressPage(index)) map {
    x =>
      AnswerRow(
        "trusteeUKAddress.checkYourAnswersLabel",
        ukAddress(x),
        None
      )
  }

  def nonUKAddress(index: Int, userAnswers: UserAnswers, countryOptions: CountryOptions)
                  (implicit messages: Messages): Option[AnswerRow] = userAnswers.get(TrusteesInternationalAddressPage(index)) map {
    x =>
      AnswerRow(
        "trusteeNonUKAddress.checkYourAnswersLabel",
        internationalAddress(x, countryOptions),
        None
      )
  }

  def telephone(index: Int, userAnswers: UserAnswers)
               (implicit messages: Messages): Option[AnswerRow] = userAnswers.get(TelephoneNumberPage(index)) map {
    x =>
      AnswerRow(
        "trusteeTelephone.checkYourAnswersLabel",
        HtmlFormat.escape(x),
        None
      )
  }

  def email(index: Int, userAnswers: UserAnswers)
           (implicit messages: Messages): Option[AnswerRow] = userAnswers.get(EmailPage(index)) map {
    x =>
      AnswerRow(
        "trusteeEmail.checkYourAnswersLabel",
        HtmlFormat.escape(x),
        None
      )
  }

}

object SettlorCompany {

  def apply(index: Int, userAnswers: UserAnswers, countryOptions: CountryOptions)(implicit messages: Messages): Option[Seq[AnswerSection]] = {

    val questions = Seq(
      name(index, userAnswers),
      utrYesNo(index, userAnswers),
      utr(index, userAnswers),
      addressYesNo(index, userAnswers),
      addressUKYesNo(index, userAnswers),
      addressUK(index, userAnswers),
      nonUKAddress(index, userAnswers, countryOptions)
    ).flatten

    if (name(index, userAnswers).nonEmpty) {
      Some(Seq(AnswerSection(
        headingKey = None,
        questions,
        sectionKey = Some(messages("answerPage.section.settlorCompany.heading"))
      )))
    } else {
      None
    }
  }

  def name(index: Int, userAnswers: UserAnswers): Option[AnswerRow] = userAnswers.get(SettlorIndividualNamePage(index)) map { x =>
    AnswerRow(
      "settlorCompanyName.checkYourAnswersLabel",
      HtmlFormat.escape(s"${x.firstName} ${x.middleName.getOrElse("")} ${x.lastName}"),
      None
    )
  }

  def utrYesNo(index: Int, userAnswers: UserAnswers)(implicit messages: Messages): Option[AnswerRow] =
    userAnswers.get(SettlorUtrYesNoPage(index)) map {
      x =>
        AnswerRow(
          "settlorCompanyUtrYesNo.checkYourAnswersLabel",
          yesOrNo(x),
          None
        )
    }

  def utr(index: Int, userAnswers: UserAnswers): Option[AnswerRow] = userAnswers.get(SettlorUtrPage(index)) map {
    x =>
      AnswerRow(
        "settlorCompanyUtr.checkYourAnswersLabel",
        HtmlFormat.escape(x.format(dateFormatter)),
        None
      )
  }

  def addressYesNo(index: Int, userAnswers: UserAnswers)
                  (implicit messages: Messages): Option[AnswerRow] = userAnswers.get(SettlorIndividualAddressYesNoPage(index)) map {
    x =>
      AnswerRow(
        "settlorCompanyAddressYesNo.checkYourAnswersLabel",
        yesOrNo(x),
        None
      )
  }

  def addressUKYesNo(index: Int, userAnswers: UserAnswers)
                    (implicit messages: Messages): Option[AnswerRow] = userAnswers.get(SettlorIndividualAddressUKYesNoPage(index)) map {
    x =>
      AnswerRow(
        "settlorCompanyAddressUKYesNo.checkYourAnswersLabel",
        yesOrNo(x),
        None
      )
  }

  def addressUK(index: Int, userAnswers: UserAnswers)
               (implicit messages: Messages): Option[AnswerRow] = userAnswers.get(SettlorIndividualAddressUKPage(index)) map {
    x =>
      AnswerRow(
        "settlorCompanyUKAddress.checkYourAnswersLabel",
        ukAddress(x),
        None
      )
  }

  def nonUKAddress(index: Int, userAnswers: UserAnswers, countryOptions: CountryOptions)
                  (implicit messages: Messages): Option[AnswerRow] = userAnswers.get(SettlorIndividualAddressInternationalPage(index)) map {
    x =>
      AnswerRow(
        "settlorCompanyNonUKAddress.checkYourAnswersLabel",
        internationalAddress(x, countryOptions),
        None
      )
  }

}

object DeceasedSettlor {

  def apply(userAnswers: UserAnswers, countryOptions: CountryOptions)(implicit messages: Messages): Option[Seq[AnswerSection]] = {

    val questions = Seq(
      setupAfterSettlorDied(userAnswers),
      deceasedSettlorsName(userAnswers),
      deceasedSettlorDateOfDeathYesNo(userAnswers),
      deceasedSettlorDateOfDeath(userAnswers),
      deceasedSettlorDateOfBirthYesNo(userAnswers),
      deceasedSettlorsDateOfBirth(userAnswers),
      deceasedSettlorsNINoYesNo(userAnswers),
      deceasedSettlorNationalInsuranceNumber(userAnswers),
      deceasedSettlorsLastKnownAddressYesNo(userAnswers),
      wasSettlorsAddressUKYesNo(userAnswers),
      deceasedSettlorsUKAddress(userAnswers),
      deceasedSettlorsInternationalAddress(userAnswers, countryOptions)
    ).flatten

    if (deceasedSettlorsName(userAnswers).nonEmpty) {
      Some(Seq(AnswerSection(
        headingKey = None,
        questions,
        sectionKey = Some(messages("answerPage.section.deceasedSettlor.heading"))
      )))
    } else {
      None
    }
  }

  def setupAfterSettlorDied(userAnswers: UserAnswers)(implicit messages: Messages): Option[AnswerRow] = userAnswers.get(SetupAfterSettlorDiedPage) map {
    x =>
      AnswerRow(
        "setupAfterSettlorDied.checkYourAnswersLabel",
        yesOrNo(x),
        None
      )
  }

  def deceasedSettlorsUKAddress(userAnswers: UserAnswers): Option[AnswerRow] = userAnswers.get(SettlorsUKAddressPage) map {
    x =>
      AnswerRow(
        "settlorsUKAddress.checkYourAnswersLabel",
        ukAddress(x),
        None
      )
  }

  def deceasedSettlorsNINoYesNo(userAnswers: UserAnswers)(implicit messages: Messages): Option[AnswerRow] = userAnswers.get(SettlorsNINoYesNoPage) map {
    x =>
      AnswerRow(
        "settlorsNINoYesNo.checkYourAnswersLabel",
        yesOrNo(x),
        None
      )
  }

  def deceasedSettlorsName(userAnswers: UserAnswers): Option[AnswerRow] = userAnswers.get(SettlorsNamePage) map {
    x =>
      AnswerRow(
        "settlorsName.checkYourAnswersLabel",
        HtmlFormat.escape(s"${x.firstName} ${x.middleName.getOrElse("")} ${x.lastName}"),
        None
      )
  }

  def deceasedSettlorsLastKnownAddressYesNo(userAnswers: UserAnswers)
                                           (implicit messages: Messages): Option[AnswerRow] = userAnswers.get(SettlorsLastKnownAddressYesNoPage) map {
    x =>
      AnswerRow(
        "settlorsLastKnownAddressYesNo.checkYourAnswersLabel",
        yesOrNo(x),
        None
      )
  }

  def deceasedSettlorsInternationalAddress(userAnswers: UserAnswers, countryOptions: CountryOptions): Option[AnswerRow] =
    userAnswers.get(SettlorsInternationalAddressPage) map {
      x =>
        AnswerRow(
          "settlorsInternationalAddress.checkYourAnswersLabel",
          internationalAddress(x, countryOptions),
          None
        )
    }

  def deceasedSettlorsDateOfBirth(userAnswers: UserAnswers): Option[AnswerRow] = userAnswers.get(SettlorsDateOfBirthPage) map {
    x =>
      AnswerRow(
        "settlorsDateOfBirth.checkYourAnswersLabel",
        HtmlFormat.escape(x.format(dateFormatter)),
        None
      )
  }

  def deceasedSettlorNationalInsuranceNumber(userAnswers: UserAnswers): Option[AnswerRow] = userAnswers.get(SettlorNationalInsuranceNumberPage) map {
    x =>
      AnswerRow(
        "settlorNationalInsuranceNumber.checkYourAnswersLabel",
        HtmlFormat.escape(formatNino(x)),
        None
      )
  }

  def deceasedSettlorDateOfDeathYesNo(userAnswers: UserAnswers)
                                     (implicit messages: Messages): Option[AnswerRow] = userAnswers.get(SettlorDateOfDeathYesNoPage) map {
    x =>
      AnswerRow(
        "settlorDateOfDeathYesNo.checkYourAnswersLabel",
        yesOrNo(x),
        None
      )
  }

  def deceasedSettlorDateOfDeath(userAnswers: UserAnswers): Option[AnswerRow] = userAnswers.get(SettlorDateOfDeathPage) map {
    x =>
      AnswerRow(
        "settlorDateOfDeath.checkYourAnswersLabel",
        HtmlFormat.escape(x.format(dateFormatter)),
        None
      )
  }

  def deceasedSettlorDateOfBirthYesNo(userAnswers: UserAnswers)
                                     (implicit messages: Messages): Option[AnswerRow] = userAnswers.get(SettlorDateOfBirthYesNoPage) map {
    x =>
      AnswerRow(
        "settlorDateOfBirthYesNo.checkYourAnswersLabel",
        yesOrNo(x),
        None
      )
  }

  def wasSettlorsAddressUKYesNo(userAnswers: UserAnswers)
                               (implicit messages: Messages): Option[AnswerRow] = userAnswers.get(WasSettlorsAddressUKYesNoPage) map {
    x =>
      AnswerRow(
        "wasSettlorsAddressUKYesNo.checkYourAnswersLabel",
        yesOrNo(x),
        None
      )
  }

  def deceasedSettlorName(userAnswers: UserAnswers): String = userAnswers.get(SettlorsNamePage).map(_.toString).getOrElse("")

}

object CompanyBeneficiary {

  def apply(index: Int, userAnswers: UserAnswers, countryOptions: CountryOptions)(implicit messages: Messages): Option[Seq[AnswerSection]] =
    if (name(index, userAnswers).nonEmpty) {
      Some(Seq(AnswerSection(
        headingKey = None,
        Seq(
          name(index, userAnswers),
          shareOfIncomeYesNo(index, userAnswers),
          shareOfIncome(index, userAnswers),
          addressYesNo(index, userAnswers),
          address(index, userAnswers, countryOptions)
        ).flatten,
        sectionKey = Some(messages("answerPage.section.companyBeneficiary.heading"))
      )))
    } else {
      None
    }

  def name(index: Int, userAnswers: UserAnswers): Option[AnswerRow] = userAnswers.get(CompanyBeneficiaryNamePage(index)) map {
    x =>
      AnswerRow(
        "companyBeneficiaryName.checkYourAnswersLabel",
        HtmlFormat.escape(x),
        None
      )
  }

  def shareOfIncomeYesNo(index: Int, userAnswers: UserAnswers)(implicit messages: Messages): Option[AnswerRow] =
    userAnswers.get(CompanyBeneficiaryDiscretionYesNoPage(index)) map {
      x =>
        AnswerRow(
          "companyBeneficiaryShareOfIncomeYesNo.checkYourAnswersLabel",
          yesOrNo(x),
          None
        )
    }

  def shareOfIncome(index: Int, userAnswers: UserAnswers): Option[AnswerRow] =
    userAnswers.get(CompanyBeneficiaryShareOfIncomePage(index)) map {
      x =>
        AnswerRow(
          "companyBeneficiaryShareOfIncome.checkYourAnswersLabel",
          HtmlFormat.escape(x),
          None
        )
    }

  def addressYesNo(index: Int, userAnswers: UserAnswers)(implicit messages: Messages): Option[AnswerRow] =
    userAnswers.get(CompanyBeneficiaryAddressYesNoPage(index)) map {
      x =>
        AnswerRow(
          "companyBeneficiaryAddressYesNo.checkYourAnswersLabel",
          yesOrNo(x),
          None
        )
    }

  def address(index: Int, userAnswers: UserAnswers, countryOptions: CountryOptions)(implicit messages: Messages): Option[AnswerRow] =
    userAnswers.get(CompanyBeneficiaryAddressPage(index)) map {
      case address: UKAddress => AnswerRow(
        "companyBeneficiaryAddress.checkYourAnswersLabel",
        ukAddress(address),
        None
      )
      case address: InternationalAddress => AnswerRow(
        "companyBeneficiaryAddress.checkYourAnswersLabel",
        internationalAddress(address, countryOptions),
        None
      )
    }

}

object TrustBeneficiary {

  def apply(index: Int, userAnswers: UserAnswers, countryOptions: CountryOptions)(implicit messages: Messages): Option[Seq[AnswerSection]] =
    if (name(index, userAnswers).nonEmpty) {
      Some(Seq(AnswerSection(
        headingKey = None,
        Seq(
          name(index, userAnswers),
          shareOfIncomeYesNo(index, userAnswers),
          shareOfIncome(index, userAnswers),
          addressYesNo(index, userAnswers),
          address(index, userAnswers, countryOptions)
        ).flatten,
        sectionKey = Some(messages("answerPage.section.companyBeneficiary.heading"))
      )))
    } else {
      None
    }

  def name(index: Int, userAnswers: UserAnswers): Option[AnswerRow] = userAnswers.get(TrustBeneficiaryNamePage(index)) map {
    x =>
      AnswerRow(
        "trustBeneficiaryName.checkYourAnswersLabel",
        HtmlFormat.escape(x),
        None
      )
  }

  def shareOfIncomeYesNo(index: Int, userAnswers: UserAnswers)(implicit messages: Messages): Option[AnswerRow] =
    userAnswers.get(TrustBeneficiaryDiscretionYesNoPage(index)) map {
      x =>
        AnswerRow(
          "trustBeneficiaryShareOfIncomeYesNo.checkYourAnswersLabel",
          yesOrNo(x),
          None
        )
    }

  def shareOfIncome(index: Int, userAnswers: UserAnswers): Option[AnswerRow] =
    userAnswers.get(TrustBeneficiaryShareOfIncomePage(index)) map {
      x =>
        AnswerRow(
          "trustBeneficiaryShareOfIncome.checkYourAnswersLabel",
          HtmlFormat.escape(x),
          None
        )
    }

  def addressYesNo(index: Int, userAnswers: UserAnswers)(implicit messages: Messages): Option[AnswerRow] =
    userAnswers.get(TrustBeneficiaryAddressYesNoPage(index)) map {
      x =>
        AnswerRow(
          "trustBeneficiaryAddressYesNo.checkYourAnswersLabel",
          yesOrNo(x),
          None
        )
    }

  def address(index: Int, userAnswers: UserAnswers, countryOptions: CountryOptions)(implicit messages: Messages): Option[AnswerRow] =
    userAnswers.get(TrustBeneficiaryAddressPage(index)) map {
      case address: UKAddress => AnswerRow(
        "trustBeneficiaryAddress.checkYourAnswersLabel",
        ukAddress(address),
        None
      )
      case address: InternationalAddress => AnswerRow(
        "trustBeneficiaryAddress.checkYourAnswersLabel",
        internationalAddress(address, countryOptions),
        None
      )
    }

}

object CharityBeneficiarySection {

  def apply(index: Int, userAnswers: UserAnswers, countryOptions: CountryOptions)(implicit messages: Messages): Option[Seq[AnswerSection]] = {
    if (name(index, userAnswers).nonEmpty) {
      Some(Seq(AnswerSection(
        headingKey = None,
        Seq(
          name(index, userAnswers),
          shareOfIncomeYesNo(index, userAnswers),
          shareOfIncome(index, userAnswers),
          addressYesNo(index, userAnswers),
          address(index, userAnswers, countryOptions)
        ).flatten,
        sectionKey = Some(messages("answerPage.section.charityBeneficiary.heading"))
      )))
    } else {
      None
    }
  }

  def name(index: Int, userAnswers: UserAnswers): Option[AnswerRow] = userAnswers.get(CharityBeneficiaryNamePage(index)) map {
    x =>
      AnswerRow(
        "charityName.checkYourAnswersLabel",
        HtmlFormat.escape(x),
        None
      )
  }

  def shareOfIncomeYesNo(index: Int, userAnswers: UserAnswers)(implicit messages: Messages): Option[AnswerRow] =
    userAnswers.get(CharityBeneficiaryDiscretionYesNoPage(index)) map {
      x =>
        AnswerRow(
          "charityShareOfIncomeYesNo.checkYourAnswersLabel",
          yesOrNo(x),
          None
        )
    }

  def shareOfIncome(index: Int, userAnswers: UserAnswers)(implicit messages: Messages): Option[AnswerRow] =
    userAnswers.get(CharityBeneficiaryShareOfIncomePage(index)) map {
      x =>
        AnswerRow(
          "charityShareOfIncomeYesNo.checkYourAnswersLabel",
          HtmlFormat.escape(x),
          None
        )
    }

  def addressYesNo(index: Int, userAnswers: UserAnswers)(implicit messages: Messages): Option[AnswerRow] =
    userAnswers.get(CharityBeneficiaryAddressYesNoPage(index)) map {
      x =>
        AnswerRow(
          "charityAddressYesNo.checkYourAnswersLabel",
          yesOrNo(x),
          None
        )
    }

  def address(index: Int, userAnswers: UserAnswers, countryOptions: CountryOptions)(implicit messages: Messages): Option[AnswerRow] =
    userAnswers.get(CharityBeneficiaryAddressPage(index)) map {
      case address: UKAddress => AnswerRow(
        "charityBeneficiaryAddress.checkYourAnswersLabel",
        ukAddress(address),
        None
      )
      case address: InternationalAddress => AnswerRow(
        "charityBeneficiaryAddress.checkYourAnswersLabel",
        internationalAddress(address, countryOptions),
        None
      )
    }

}

object ClassOfBeneficiary {

  def apply(index: Int, userAnswers: UserAnswers, countryOptions: CountryOptions)(implicit messages: Messages): Option[Seq[AnswerSection]] = {
    if (description(index, userAnswers).nonEmpty) {
      Some(Seq(AnswerSection(
        headingKey = None,
        Seq(
          description(index, userAnswers),
          shareOfIncomeYesNo(index, userAnswers),
          shareOfIncome(index, userAnswers)
        ).flatten,
        sectionKey = Some(messages("answerPage.section.charityBeneficiary.heading"))
      )))
    } else {
      None
    }
  }

  def description(index: Int, userAnswers: UserAnswers): Option[AnswerRow] = userAnswers.get(ClassOfBeneficiaryDescriptionPage(index)) map {
    x =>
      AnswerRow(
        "otherDescription.checkYourAnswersLabel",
        HtmlFormat.escape(x),
        None
      )
  }

  def shareOfIncomeYesNo(index: Int, userAnswers: UserAnswers)(implicit messages: Messages): Option[AnswerRow] =
    userAnswers.get(ClassOfBeneficiaryDiscretionYesNoPage(index)) map {
      x =>
        AnswerRow(
          "otherShareOfIncomeYesNo.checkYourAnswersLabel",
          yesOrNo(x),
          None
        )
    }

  def shareOfIncome(index: Int, userAnswers: UserAnswers)(implicit messages: Messages): Option[AnswerRow] =
    userAnswers.get(ClassOfBeneficiaryShareOfIncomePage(index)) map {
      x =>
        AnswerRow(
          "otherShareOfIncome.checkYourAnswersLabel",
          HtmlFormat.escape(x),
          None
        )
    }

}

object OtherBeneficiary {

  def apply(index: Int, userAnswers: UserAnswers, countryOptions: CountryOptions)(implicit messages: Messages): Option[Seq[AnswerSection]] = {
    if (description(index, userAnswers).nonEmpty) {
      Some(Seq(AnswerSection(
        headingKey = None,
        Seq(
          description(index, userAnswers),
          shareOfIncomeYesNo(index, userAnswers),
          shareOfIncome(index, userAnswers),
          addressYesNo(index, userAnswers),
          address(index, userAnswers, countryOptions)
        ).flatten,
        sectionKey = Some(messages("answerPage.section.charityBeneficiary.heading"))
      )))
    } else {
      None
    }
  }

  def description(index: Int, userAnswers: UserAnswers): Option[AnswerRow] = userAnswers.get(OtherBeneficiaryDescriptionPage(index)) map {
    x =>
      AnswerRow(
        "otherDescription.checkYourAnswersLabel",
        HtmlFormat.escape(x),
        None
      )
  }

  def shareOfIncomeYesNo(index: Int, userAnswers: UserAnswers)(implicit messages: Messages): Option[AnswerRow] =
    userAnswers.get(OtherBeneficiaryDiscretionYesNoPage(index)) map {
      x =>
        AnswerRow(
          "otherShareOfIncomeYesNo.checkYourAnswersLabel",
          yesOrNo(x),
          None
        )
    }

  def shareOfIncome(index: Int, userAnswers: UserAnswers)(implicit messages: Messages): Option[AnswerRow] =
    userAnswers.get(OtherBeneficiaryShareOfIncomePage(index)) map {
      x =>
        AnswerRow(
          "otherShareOfIncome.checkYourAnswersLabel",
          HtmlFormat.escape(x),
          None
        )
    }

  def addressYesNo(index: Int, userAnswers: UserAnswers)(implicit messages: Messages): Option[AnswerRow] =
    userAnswers.get(OtherBeneficiaryAddressYesNoPage(index)) map {
      x =>
        AnswerRow(
          "otherAddressYesNo.checkYourAnswersLabel",
          yesOrNo(x),
          None
        )
    }

  def address(index: Int, userAnswers: UserAnswers, countryOptions: CountryOptions)(implicit messages: Messages): Option[AnswerRow] =
    userAnswers.get(OtherBeneficiaryAddressPage(index)) map {
      case address: UKAddress => AnswerRow(
        "charityBeneficiaryAddress.checkYourAnswersLabel",
        ukAddress(address),
        None
      )
      case address: InternationalAddress => AnswerRow(
        "charityBeneficiaryAddress.checkYourAnswersLabel",
        internationalAddress(address, countryOptions),
        None
      )
    }

}
