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

import java.time.format.DateTimeFormatter

import controllers.routes
import javax.inject.Inject
import models.entities._
import models.{CheckMode, FullName, InternationalAddress, UKAddress, UserAnswers}
import pages._
import play.api.i18n.Messages
import play.twirl.api.{Html, HtmlFormat}
import uk.gov.hmrc.domain.Nino
import utils.CheckYourAnswersHelper.{indBeneficiaryName, trusteeName, _}
import utils.countryOptions.CountryOptions
import viewmodels.{AnswerRow, AnswerSection}

class CheckYourAnswersHelper @Inject()(countryOptions: CountryOptions)(userAnswers: UserAnswers, isSummary: Boolean = false)(implicit messages: Messages)  {

  def trustDetails : Option[Seq[AnswerSection]] = {
    val questions = Seq(
      trustName,
      whenTrustSetup,
      governedInsideTheUK,
      countryGoverningTrust,
      administrationInsideUK,
      countryAdministeringTrust,
      trustResidentInUK,
      establishedUnderScotsLaw,
      trustResidentOffshore,
      trustPreviouslyResident,
      registeringTrustFor5A,
      nonresidentType,
      inheritanceTaxAct,
      agentOtherThanBarrister
    ).flatten

    if (questions.nonEmpty) Some(Seq(AnswerSection(None, questions, Some(Messages("answerPage.section.trustsDetails.heading"))))) else None
  }

  def settlors : Option[Seq[AnswerSection]] = {

    val questions = Seq(
      setupAfterSettlorDied,
      settlorsName,
      settlorDateOfDeathYesNo,
      settlorDateOfDeath,
      settlorDateOfBirthYesNo,
      settlorsDateOfBirth,
      settlorsNINoYesNo,
      settlorNationalInsuranceNumber,
      settlorsLastKnownAddressYesNo,
      wasSettlorsAddressUKYesNo,
      settlorsUKAddress,
      settlorsInternationalAddress
    ).flatten

    if (questions.nonEmpty) Some(Seq(AnswerSection(None, questions, Some(Messages("answerPage.section.settlors.heading"))))) else None
  }

  def trustees : Option[Seq[AnswerSection]] = {
    for {
      trustees <- userAnswers.get(Trustees)
      indexed = trustees.zipWithIndex
    } yield indexed.map {
      case (trustee, index) =>

        val isLead = userAnswers.get(IsThisLeadTrusteePage(index)).get
        val trusteeIndividualOrBusinessMessagePrefix = if (isLead) "leadTrusteeIndividualOrBusiness" else "trusteeIndividualOrBusiness"
        val trusteeFullNameMessagePrefix = if (isLead) "leadTrusteesName" else "trusteesName"

        val questions = Seq(
          isThisLeadTrustee(index),
          trusteeIndividualOrBusiness(index, trusteeIndividualOrBusinessMessagePrefix),
          trusteeFullName(index, trusteeFullNameMessagePrefix),
          trusteesDateOfBirth(index),
          trusteeAUKCitizen(index),
          trusteesNino(index),
          trusteeLiveInTheUK(index),
          trusteesUkAddress(index),
          telephoneNumber(index)
        ).flatten


        val sectionKey = if (index == 0) Some(Messages("answerPage.section.trustees.heading")) else None

        AnswerSection(
          headingKey = Some(Messages("answerPage.section.trustee.subheading") + " " + (index + 1)),
          rows = questions,
          sectionKey = sectionKey
        )
    }
  }

  private var displaySectionHeading: Boolean = true

  def individualBeneficiaries : Option[Seq[AnswerSection]] = {
    for {
      beneficiaries <- userAnswers.get(IndividualBeneficiaries)
      indexed = beneficiaries.zipWithIndex
    } yield indexed.map {
      case (beneficiary, index) =>

        val questions = Seq(
          individualBeneficiaryName(index),
          individualBeneficiaryDateOfBirthYesNo(index),
          individualBeneficiaryDateOfBirth(index),
          individualBeneficiaryIncomeYesNo(index),
          individualBeneficiaryIncome(index),
          individualBeneficiaryNationalInsuranceYesNo(index),
          individualBeneficiaryNationalInsuranceNumber(index),
          individualBeneficiaryAddressYesNo(index),
          individualBeneficiaryAddressUKYesNo(index),
          individualBeneficiaryAddressUK(index),
          individualBeneficiaryVulnerableYesNo(index)
        ).flatten

        val sectionKey = if (index == 0 && displaySectionHeading) {
          displaySectionHeading = false
          Some(Messages("answerPage.section.beneficiaries.heading"))
        } else {
          None
        }

        AnswerSection(
          headingKey = Some(Messages("answerPage.section.individualBeneficiary.subheading") + " " + (index + 1)),
          rows = questions,
          sectionKey = sectionKey)
    }
  }

  def classOfBeneficiaries : Option[Seq[AnswerSection]] = {
    for {
      beneficiaries <- userAnswers.get(ClassOfBeneficiaries)
      indexed = beneficiaries.zipWithIndex
    } yield indexed.map {
      case (beneficiary, index) =>

        val questions = Seq(
          classBeneficiaryDescription(index)
        ).flatten

        val sectionKey = if (index == 0 && displaySectionHeading) {
          displaySectionHeading = false
          Some(Messages("answerPage.section.beneficiaries.heading"))
        } else {
          None
        }

        AnswerSection(
          Some(Messages("answerPage.section.classOfBeneficiary.subheading") + " " + (index + 1)),
          questions,
          sectionKey
        )
    }
  }


  def moneyAsset : Option[Seq[AnswerSection]] = {
    displaySectionHeading = true
    val questions = Seq(
      assetMoneyValue(0)
    ).flatten

    val sectionKey = if (displaySectionHeading) {
      displaySectionHeading = false
      Some(Messages("answerPage.section.assets.heading"))
    } else {
      None
    }

    if (questions.nonEmpty) {
      Some(
        Seq(
          AnswerSection(
            headingKey = Some(Messages("answerPage.section.moneyAsset.subheading")),
            rows = questions,
            sectionKey = sectionKey
          )
        )
      )
    } else {
      None
    }
  }


  def agentInternationalAddress: Option[AnswerRow] = userAnswers.get(AgentInternationalAddressPage) map {
    x =>
      AnswerRow(
        "site.address.international.checkYourAnswersLabel",
        internationalAddress(x, countryOptions),
        routes.AgentInternationalAddressController.onPageLoad(CheckMode).url,
        agencyName(userAnswers),
        isSummary = isSummary
      )
  }

  def classBeneficiaryDescription(index: Int): Option[AnswerRow] = userAnswers.get(ClassBeneficiaryDescriptionPage(index)) map {
    x =>
      AnswerRow(
        "classBeneficiaryDescription.checkYourAnswersLabel",

        HtmlFormat.escape(x),
        routes.ClassBeneficiaryDescriptionController.onPageLoad(CheckMode,index).url,
        isSummary = isSummary
      )
  }

  def agentUKAddress: Option[AnswerRow] = userAnswers.get(AgentUKAddressPage) map {
    x =>
      AnswerRow(
        "site.address.uk.checkYourAnswersLabel",
        ukAddress(x),
        routes.AgentUKAddressController.onPageLoad(CheckMode).url,
        agencyName(userAnswers),
        isSummary = isSummary
      )
  }

  def agentAddressYesNo: Option[AnswerRow] = userAnswers.get(AgentAddressYesNoPage) map {
    x =>
      AnswerRow(
        "agentAddressYesNo.checkYourAnswersLabel",
        yesOrNo(x),
        routes.AgentAddressYesNoController.onPageLoad(CheckMode).url,
        agencyName(userAnswers),
        isSummary = isSummary
      )
  }

  def individualBeneficiaryAddressUKYesNo(index: Int): Option[AnswerRow] = userAnswers.get(IndividualBeneficiaryAddressUKYesNoPage(index)) map {
    x =>
      AnswerRow(
        "individualBeneficiaryAddressUKYesNo.checkYourAnswersLabel",
        yesOrNo(x),
        routes.IndividualBeneficiaryAddressUKYesNoController.onPageLoad(CheckMode, index).url,
        indBeneficiaryName(index,userAnswers),
        isSummary = isSummary
      )
  }

  def agentName: Option[AnswerRow] = userAnswers.get(AgentNamePage) map {
    x =>
      AnswerRow(
        "agentName.checkYourAnswersLabel",
        HtmlFormat.escape(x),
        routes.AgentNameController.onPageLoad(CheckMode).url,
        isSummary = isSummary
      )
  }

  def addABeneficiary: Option[AnswerRow] = userAnswers.get(AddABeneficiaryPage) map {
    x =>
      AnswerRow(
        "addABeneficiary.checkYourAnswersLabel",
        HtmlFormat.escape(messages(s"addABeneficiary.$x")),
        routes.AddABeneficiaryController.onPageLoad().url,
        isSummary = isSummary
      )
  }

  def individualBeneficiaryVulnerableYesNo(index: Int): Option[AnswerRow] = userAnswers.get(IndividualBeneficiaryVulnerableYesNoPage(index)) map {
    x =>
      AnswerRow(
        "individualBeneficiaryVulnerableYesNo.checkYourAnswersLabel",
        yesOrNo(x),
        routes.IndividualBeneficiaryVulnerableYesNoController.onPageLoad(CheckMode, index).url,
        indBeneficiaryName(index,userAnswers),
        isSummary = isSummary
      )
  }

  def individualBeneficiaryAddressUK(index: Int): Option[AnswerRow] = userAnswers.get(IndividualBeneficiaryAddressUKPage(index)) map {
    x =>
      AnswerRow(
        "individualBeneficiaryAddressUK.checkYourAnswersLabel",
        ukAddress(x),
        routes.IndividualBeneficiaryAddressUKController.onPageLoad(CheckMode, index).url,
        indBeneficiaryName(index,userAnswers),
        isSummary = isSummary
      )
  }

  def individualBeneficiaryAddressYesNo(index: Int): Option[AnswerRow] = userAnswers.get(IndividualBeneficiaryAddressYesNoPage(index)) map {
    x =>
      AnswerRow(
        "individualBeneficiaryAddressYesNo.checkYourAnswersLabel",
        yesOrNo(x),
        routes.IndividualBeneficiaryAddressYesNoController.onPageLoad(CheckMode, index).url,
        indBeneficiaryName(index,userAnswers),
        isSummary = isSummary
      )
  }

  def individualBeneficiaryNationalInsuranceNumber(index: Int): Option[AnswerRow] = userAnswers.get(IndividualBeneficiaryNationalInsuranceNumberPage(index)) map {
    x =>
      AnswerRow(
        "individualBeneficiaryNationalInsuranceNumber.checkYourAnswersLabel",
        HtmlFormat.escape(formatNino(x)),
        routes.IndividualBeneficiaryNationalInsuranceNumberController.onPageLoad(CheckMode, index).url,
        indBeneficiaryName(index,userAnswers),
        isSummary = isSummary
      )
  }

  def individualBeneficiaryNationalInsuranceYesNo(index: Int): Option[AnswerRow] = userAnswers.get(IndividualBeneficiaryNationalInsuranceYesNoPage(index)) map {
    x =>
      AnswerRow(
        "individualBeneficiaryNationalInsuranceYesNo.checkYourAnswersLabel",
        yesOrNo(x),
        routes.IndividualBeneficiaryNationalInsuranceYesNoController.onPageLoad(CheckMode, index).url,
        indBeneficiaryName(index,userAnswers),
        isSummary = isSummary
      )
  }

  def individualBeneficiaryIncome(index: Int): Option[AnswerRow] = userAnswers.get(IndividualBeneficiaryIncomePage(index)) map {
    x =>
      AnswerRow(
        "individualBeneficiaryIncome.checkYourAnswersLabel",
        HtmlFormat.escape(x),
        routes.IndividualBeneficiaryIncomeController.onPageLoad(CheckMode, index).url,
        indBeneficiaryName(index, userAnswers),
        isSummary = isSummary
      )
  }

  def individualBeneficiaryIncomeYesNo(index: Int): Option[AnswerRow] = userAnswers.get(IndividualBeneficiaryIncomeYesNoPage(index)) map {
    x =>
      AnswerRow(
        "individualBeneficiaryIncomeYesNo.checkYourAnswersLabel",
        yesOrNo(x),
        routes.IndividualBeneficiaryIncomeYesNoController.onPageLoad(CheckMode, index).url,
        indBeneficiaryName(index,userAnswers),
        isSummary = isSummary
      )
  }

  def individualBeneficiaryDateOfBirth(index: Int): Option[AnswerRow] = userAnswers.get(IndividualBeneficiaryDateOfBirthPage(index)) map {
    x =>
      AnswerRow(
        "individualBeneficiaryDateOfBirth.checkYourAnswersLabel",
        HtmlFormat.escape(x.format(dateFormatter)),
        routes.IndividualBeneficiaryDateOfBirthController.onPageLoad(CheckMode, index).url,
        indBeneficiaryName(index,userAnswers),
        isSummary = isSummary
      )
  }

  def individualBeneficiaryDateOfBirthYesNo(index: Int): Option[AnswerRow] = userAnswers.get(IndividualBeneficiaryDateOfBirthYesNoPage(index)) map {
    x =>
      AnswerRow(
        "individualBeneficiaryDateOfBirthYesNo.checkYourAnswersLabel",
        yesOrNo(x),
        routes.IndividualBeneficiaryDateOfBirthYesNoController.onPageLoad(CheckMode, index).url,
        indBeneficiaryName(index,userAnswers),
        isSummary = isSummary
      )
  }

  def individualBeneficiaryName(index: Int): Option[AnswerRow] = userAnswers.get(IndividualBeneficiaryNamePage(index)) map {
    x =>
      AnswerRow(
        "individualBeneficiaryName.checkYourAnswersLabel",
        HtmlFormat.escape(s"${x.firstName} ${x.middleName.getOrElse("")} ${x.lastName}"),
        routes.IndividualBeneficiaryNameController.onPageLoad(CheckMode, index).url,
        isSummary = isSummary
      )
  }

  def wasSettlorsAddressUKYesNo: Option[AnswerRow] = userAnswers.get(WasSettlorsAddressUKYesNoPage) map {
    x =>
      AnswerRow(
        "wasSettlorsAddressUKYesNo.checkYourAnswersLabel",
        yesOrNo(x),
        routes.WasSettlorsAddressUKYesNoController.onPageLoad(CheckMode).url,
        deceasedSettlorName(userAnswers),
        isSummary = isSummary
      )
  }

  def setupAfterSettlorDied: Option[AnswerRow] = userAnswers.get(SetupAfterSettlorDiedPage) map {
    x =>
      AnswerRow(
        "setupAfterSettlorDied.checkYourAnswersLabel",
        yesOrNo(x),
        routes.SetupAfterSettlorDiedController.onPageLoad(CheckMode).url,
        isSummary = isSummary
      )
  }

  def settlorsUKAddress: Option[AnswerRow] = userAnswers.get(SettlorsUKAddressPage) map {
    x =>
      AnswerRow(
        "settlorsUKAddress.checkYourAnswersLabel",
        ukAddress(x),
        routes.SettlorsUKAddressController.onPageLoad(CheckMode).url,
        deceasedSettlorName(userAnswers),
        isSummary = isSummary
      )
  }

  def settlorsNINoYesNo: Option[AnswerRow] = userAnswers.get(SettlorsNINoYesNoPage) map {
    x =>
      AnswerRow(
        "settlorsNINoYesNo.checkYourAnswersLabel",
        yesOrNo(x),
        routes.SettlorsNINoYesNoController.onPageLoad(CheckMode).url,
        deceasedSettlorName(userAnswers),
        isSummary = isSummary
      )
  }

  def settlorsName: Option[AnswerRow] = userAnswers.get(SettlorsNamePage) map {
    x =>
      AnswerRow(
        "settlorsName.checkYourAnswersLabel",
        HtmlFormat.escape(s"${x.firstName} ${x.lastName}"),
        routes.SettlorsNameController.onPageLoad(CheckMode).url,
        isSummary = isSummary
      )
  }

  def settlorsLastKnownAddressYesNo: Option[AnswerRow] = userAnswers.get(SettlorsLastKnownAddressYesNoPage) map {
    x =>
      AnswerRow(
        "settlorsLastKnownAddressYesNo.checkYourAnswersLabel",
        yesOrNo(x),
        routes.SettlorsLastKnownAddressYesNoController.onPageLoad(CheckMode).url,
        deceasedSettlorName(userAnswers),
        isSummary = isSummary
      )
  }

  def settlorsInternationalAddress: Option[AnswerRow] = userAnswers.get(SettlorsInternationalAddressPage) map {
    x =>
      AnswerRow(
        "settlorsInternationalAddress.checkYourAnswersLabel",
        internationalAddress(x, countryOptions),
        routes.SettlorsInternationalAddressController.onPageLoad(CheckMode).url,
        deceasedSettlorName(userAnswers),
        isSummary = isSummary
      )
  }

  def settlorsDateOfBirth: Option[AnswerRow] = userAnswers.get(SettlorsDateOfBirthPage) map {
    x =>
      AnswerRow(
        "settlorsDateOfBirth.checkYourAnswersLabel",
        HtmlFormat.escape(x.format(dateFormatter)),
        routes.SettlorsDateOfBirthController.onPageLoad(CheckMode).url,
        deceasedSettlorName(userAnswers),
        isSummary = isSummary
      )
  }

  def settlorNationalInsuranceNumber: Option[AnswerRow] = userAnswers.get(SettlorNationalInsuranceNumberPage) map {
    x =>
      AnswerRow(
        "settlorNationalInsuranceNumber.checkYourAnswersLabel",
        HtmlFormat.escape(formatNino(x)),
        routes.SettlorNationalInsuranceNumberController.onPageLoad(CheckMode).url,
        deceasedSettlorName(userAnswers),
        isSummary = isSummary
      )
  }

  def settlorDateOfDeathYesNo: Option[AnswerRow] = userAnswers.get(SettlorDateOfDeathYesNoPage) map {
    x =>
      AnswerRow(
        "settlorDateOfDeathYesNo.checkYourAnswersLabel",
        yesOrNo(x),
        routes.SettlorDateOfDeathYesNoController.onPageLoad(CheckMode).url,
        deceasedSettlorName(userAnswers),
        isSummary = isSummary
      )
  }

  def settlorDateOfDeath: Option[AnswerRow] = userAnswers.get(SettlorDateOfDeathPage) map {
    x =>
      AnswerRow(
        "settlorDateOfDeath.checkYourAnswersLabel",
        HtmlFormat.escape(x.format(dateFormatter)),
        routes.SettlorDateOfDeathController.onPageLoad(CheckMode).url,
        deceasedSettlorName(userAnswers),
        isSummary = isSummary
      )
  }

  def settlorDateOfBirthYesNo: Option[AnswerRow] = userAnswers.get(SettlorDateOfBirthYesNoPage) map {
    x =>
      AnswerRow(
        "settlorDateOfBirthYesNo.checkYourAnswersLabel",
        yesOrNo(x),
        routes.SettlorDateOfBirthYesNoController.onPageLoad(CheckMode).url,
        deceasedSettlorName(userAnswers),
        isSummary = isSummary
      )
  }

  def assetMoneyValue(index: Int): Option[AnswerRow] = userAnswers.get(AssetMoneyValuePage(index)) map {
    x =>
      AnswerRow(
        "assetMoneyValue.checkYourAnswersLabel",
        HtmlFormat.escape("£"+x),
        routes.AssetMoneyValueController.onPageLoad(CheckMode, index).url,
        isSummary = isSummary
      )
  }

  def whatKindOfAsset(index: Int): Option[AnswerRow] = userAnswers.get(WhatKindOfAssetPage(index)) map {
    x =>
      AnswerRow(
        "whatKindOfAsset.checkYourAnswersLabel",
        HtmlFormat.escape(messages(s"whatKindOfAsset.$x")),
        routes.WhatKindOfAssetController.onPageLoad(CheckMode, index).url,
        isSummary = isSummary
      )
  }

  def agentInternalReference: Option[AnswerRow] = userAnswers.get(AgentInternalReferencePage) map {
    x =>
      AnswerRow(
        "agentInternalReference.checkYourAnswersLabel",
        HtmlFormat.escape(x),
        routes.AgentInternalReferenceController.onPageLoad(CheckMode).url,
        isSummary = isSummary
      )
  }

  def agenciesTelephoneNumber: Option[AnswerRow] = userAnswers.get(AgentTelephoneNumberPage) map {
    x =>
      AnswerRow(
        "agentTelephoneNumber.checkYourAnswersLabel",
        HtmlFormat.escape(x),
        routes.AgentTelephoneNumberController.onPageLoad(CheckMode).url,
        isSummary = isSummary
      )
  }

  def trusteesNino(index: Int): Option[AnswerRow] = userAnswers.get(TrusteesNinoPage(index)) map {
    x =>
      AnswerRow(
        "trusteesNino.checkYourAnswersLabel",
        HtmlFormat.escape(formatNino(x)),
        routes.TrusteesNinoController.onPageLoad(CheckMode, index).url,
        trusteeName(index, userAnswers),
        isSummary = isSummary
      )
  }

  def trusteeLiveInTheUK(index : Int): Option[AnswerRow] = userAnswers.get(TrusteeLiveInTheUKPage(index)) map {
    x =>
      AnswerRow(
        "trusteeLiveInTheUK.checkYourAnswersLabel",
        yesOrNo(x),
        routes.TrusteeLiveInTheUKController.onPageLoad(CheckMode, index).url,
        trusteeName(index, userAnswers),
        isSummary = isSummary
      )
  }

  def trusteesUkAddress(index: Int): Option[AnswerRow] = userAnswers.get(TrusteesUkAddressPage(index)) map {
    x =>
      AnswerRow(
        "trusteesUkAddress.checkYourAnswersLabel",
        ukAddress(x),
        routes.TrusteesUkAddressController.onPageLoad(CheckMode, index).url,
        trusteeName(index, userAnswers),
        isSummary = isSummary
      )
  }

  def trusteesDateOfBirth(index : Int): Option[AnswerRow] = userAnswers.get(TrusteesDateOfBirthPage(index)) map {
    x =>
      AnswerRow(
        "trusteesDateOfBirth.checkYourAnswersLabel",
        HtmlFormat.escape(x.format(dateFormatter)),
        routes.TrusteesDateOfBirthController.onPageLoad(CheckMode, index).url,
        trusteeName(index, userAnswers),
        isSummary = isSummary
      )
  }

  def telephoneNumber(index : Int): Option[AnswerRow] = userAnswers.get(TelephoneNumberPage(index)) map {
    x =>
      AnswerRow(
        "telephoneNumber.checkYourAnswersLabel",
        HtmlFormat.escape(x),
        routes.TelephoneNumberController.onPageLoad(CheckMode, index).url,
        trusteeName(index, userAnswers),
        isSummary = isSummary
      )
  }

  def trusteeAUKCitizen(index : Int): Option[AnswerRow] = userAnswers.get(TrusteeAUKCitizenPage(index)) map {
    x =>
      AnswerRow(
        "trusteeAUKCitizen.checkYourAnswersLabel",
        yesOrNo(x),
        routes.TrusteeAUKCitizenController.onPageLoad(CheckMode,index).url,
        trusteeName(index, userAnswers),
        isSummary = isSummary
      )
  }


  def trusteeFullName(index : Int, messagePrefix: String): Option[AnswerRow] = userAnswers.get(TrusteesNamePage(index)) map {
    x => AnswerRow(
      s"$messagePrefix.checkYourAnswersLabel",
      HtmlFormat.escape(s"${x.firstName} ${x.middleName.getOrElse("")} ${x.lastName}"),
      routes.TrusteesNameController.onPageLoad(CheckMode, index).url,
      isSummary = isSummary
    )
  }

  def trusteeIndividualOrBusiness(index : Int, messagePrefix: String): Option[AnswerRow] = userAnswers.get(TrusteeIndividualOrBusinessPage(index)) map {
    x =>
      AnswerRow(
        s"$messagePrefix.checkYourAnswersLabel",
        HtmlFormat.escape(messages(s"individualOrBusiness.$x")),
        routes.TrusteeIndividualOrBusinessController.onPageLoad(CheckMode, index).url,
        isSummary = isSummary
      )
  }

  def isThisLeadTrustee(index: Int): Option[AnswerRow] = userAnswers.get(IsThisLeadTrusteePage(index)) map {
    x =>
      AnswerRow(
        "isThisLeadTrustee.checkYourAnswersLabel",
        yesOrNo(x),
        routes.IsThisLeadTrusteeController.onPageLoad(CheckMode, index).url,
        isSummary = isSummary
      )
  }

  def postcodeForTheTrust: Option[AnswerRow] = userAnswers.get(PostcodeForTheTrustPage) map {
    x =>
      AnswerRow(
        "postcodeForTheTrust.checkYourAnswersLabel",
        HtmlFormat.escape(x),
        routes.PostcodeForTheTrustController.onPageLoad(CheckMode).url,
        isSummary = isSummary
      )
  }

  def whatIsTheUTR: Option[AnswerRow] = userAnswers.get(WhatIsTheUTRPage) map {
    x =>
      AnswerRow(
        "whatIsTheUTR.checkYourAnswersLabel",
        HtmlFormat.escape(x),
        routes.WhatIsTheUTRController.onPageLoad(CheckMode).url,
        isSummary = isSummary
      )
  }

  def trustHaveAUTR: Option[AnswerRow] = userAnswers.get(TrustHaveAUTRPage) map {
    x =>
      AnswerRow(
        "trustHaveAUTR.checkYourAnswersLabel",
        yesOrNo(x),
        routes.TrustHaveAUTRController.onPageLoad(CheckMode).url,
        isSummary = isSummary
      )
  }

  def trustRegisteredOnline: Option[AnswerRow] = userAnswers.get(TrustRegisteredOnlinePage) map {
    x =>
      AnswerRow(
        "trustRegisteredOnline.checkYourAnswersLabel",
        yesOrNo(x),
        routes.TrustRegisteredOnlineController.onPageLoad(CheckMode).url,
        isSummary = isSummary
      )
  }

  def whenTrustSetup: Option[AnswerRow] = userAnswers.get(WhenTrustSetupPage) map {
    x =>
      AnswerRow(
        "whenTrustSetup.checkYourAnswersLabel",
        HtmlFormat.escape(x.format(dateFormatter)),
        routes.WhenTrustSetupController.onPageLoad(CheckMode).url,
        isSummary = isSummary
      )
  }

  def agentOtherThanBarrister: Option[AnswerRow] = userAnswers.get(AgentOtherThanBarristerPage) map {
    x => AnswerRow("agentOtherThanBarrister.checkYourAnswersLabel", yesOrNo(x), routes.AgentOtherThanBarristerController.onPageLoad(CheckMode).url, isSummary = isSummary)
  }

  def inheritanceTaxAct: Option[AnswerRow] = userAnswers.get(InheritanceTaxActPage) map {
    x => AnswerRow("inheritanceTaxAct.checkYourAnswersLabel", yesOrNo(x), routes.InheritanceTaxActController.onPageLoad(CheckMode).url, isSummary = isSummary)
  }

  def nonresidentType: Option[AnswerRow] = userAnswers.get(NonResidentTypePage) map {
    x => AnswerRow("nonresidentType.checkYourAnswersLabel", answer("nonresidentType", x), routes.NonResidentTypeController.onPageLoad(CheckMode).url, isSummary = isSummary)
  }

  def trustPreviouslyResident: Option[AnswerRow] = userAnswers.get(TrustPreviouslyResidentPage) map {
    x => AnswerRow("trustPreviouslyResident.checkYourAnswersLabel", escape(country(x, countryOptions)), routes.TrustPreviouslyResidentController.onPageLoad(CheckMode).url, isSummary = isSummary)
  }

  def trustResidentOffshore: Option[AnswerRow] = userAnswers.get(TrustResidentOffshorePage) map {
    x => AnswerRow("trustResidentOffshore.checkYourAnswersLabel", yesOrNo(x), routes.TrustResidentOffshoreController.onPageLoad(CheckMode).url, isSummary = isSummary)
  }

  def registeringTrustFor5A: Option[AnswerRow] = userAnswers.get(RegisteringTrustFor5APage) map {
    x => AnswerRow("registeringTrustFor5A.checkYourAnswersLabel", yesOrNo(x), routes.RegisteringTrustFor5AController.onPageLoad(CheckMode).url, isSummary = isSummary)
  }

  def establishedUnderScotsLaw: Option[AnswerRow] = userAnswers.get(EstablishedUnderScotsLawPage) map {
    x => AnswerRow("establishedUnderScotsLaw.checkYourAnswersLabel", yesOrNo(x), routes.EstablishedUnderScotsLawController.onPageLoad(CheckMode).url, isSummary = isSummary)
  }

  def trustResidentInUK: Option[AnswerRow] = userAnswers.get(TrustResidentInUKPage) map {
    x => AnswerRow("trustResidentInUK.checkYourAnswersLabel", yesOrNo(x), routes.TrustResidentInUKController.onPageLoad(CheckMode).url, isSummary = isSummary)
  }

  def countryAdministeringTrust: Option[AnswerRow] = userAnswers.get(CountryAdministeringTrustPage) map {
    x => AnswerRow("countryAdministeringTrust.checkYourAnswersLabel", escape(country(x, countryOptions)), routes.CountryAdministeringTrustController.onPageLoad(CheckMode).url, isSummary = isSummary)
  }

  def administrationInsideUK: Option[AnswerRow] = userAnswers.get(AdministrationInsideUKPage) map {
    x => AnswerRow("administrationInsideUK.checkYourAnswersLabel", yesOrNo(x), routes.AdministrationInsideUKController.onPageLoad(CheckMode).url, isSummary = isSummary)
  }

  def countryGoverningTrust: Option[AnswerRow] = userAnswers.get(CountryGoverningTrustPage) map {
    x => AnswerRow("countryGoverningTrust.checkYourAnswersLabel", escape(country(x, countryOptions)), routes.CountryGoverningTrustController.onPageLoad(CheckMode).url, isSummary = isSummary)
  }

  def governedInsideTheUK: Option[AnswerRow] = userAnswers.get(GovernedInsideTheUKPage) map {
    x => AnswerRow("governedInsideTheUK.checkYourAnswersLabel", yesOrNo(x), routes.GovernedInsideTheUKController.onPageLoad(CheckMode).url, isSummary = isSummary)
  }

  def trustName: Option[AnswerRow] = userAnswers.get(TrustNamePage) map {
    x => AnswerRow("trustName.checkYourAnswersLabel", escape(x), routes.TrustNameController.onPageLoad(CheckMode).url, isSummary = isSummary)
  }



}

object CheckYourAnswersHelper {

  private val dateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy")

  private def yesOrNo(answer: Boolean)(implicit messages: Messages): Html =
    if (answer) {
      HtmlFormat.escape(messages("site.yes"))
    } else {
      HtmlFormat.escape(messages("site.no"))
    }

  private def formatNino(nino: String): String = Nino(nino).formatted

  private def country(code : String, countryOptions: CountryOptions) : String =
    countryOptions.options.find(_.value.equals(code)).map(_.label).getOrElse("")

  private def trusteeName(index: Int, userAnswers: UserAnswers): String =
    userAnswers.get(TrusteesNamePage(index)).get.toString

  private def answer[T](key : String, answer: T)(implicit messages: Messages) : Html =
    HtmlFormat.escape(messages(s"$key.$answer"))

  private def escape(x : String) = HtmlFormat.escape(x)

  private def deceasedSettlorName(userAnswers: UserAnswers): String =
    userAnswers.get(SettlorsNamePage).get.toString

  private def indBeneficiaryName(index: Int, userAnswers: UserAnswers): String = {
    userAnswers.get(IndividualBeneficiaryNamePage(index)).get.toString
  }

  private def agencyName(userAnswers: UserAnswers): String = {
    userAnswers.get(AgentNamePage).get.toString
  }

  private def ukAddress(address: UKAddress): Html = {
    val lines =
      Seq(
        Some(HtmlFormat.escape(address.line1)),
        address.line2.map(HtmlFormat.escape),
        address.line3.map(HtmlFormat.escape),
        Some(HtmlFormat.escape(address.townOrCity)),
        Some(HtmlFormat.escape(address.postcode))
      ).flatten

    Html(lines.mkString("<br />"))
  }

  private def internationalAddress(address: InternationalAddress, countryOptions: CountryOptions): Html = {
    val lines =
      Seq(
        Some(HtmlFormat.escape(address.line1)),
        Some(HtmlFormat.escape(address.line2)),
        address.line3.map(HtmlFormat.escape),
        address.line4.map(HtmlFormat.escape),
        Some(country(address.country, countryOptions))
      ).flatten

    Html(lines.mkString("<br />"))
  }
}
