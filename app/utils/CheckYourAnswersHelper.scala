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
import models.{CheckMode, InternationalAddress, UKAddress, UserAnswers}
import pages._
import utils.CheckYourAnswersHelper._
import play.api.i18n.Messages
import play.twirl.api.{Html, HtmlFormat}
import uk.gov.hmrc.domain.Nino
import utils.countryOptions.CountryOptions
import viewmodels.{AnswerRow, AnswerSection}

class CheckYourAnswersHelper @Inject()(countryOptions: CountryOptions)(userAnswers: UserAnswers, draftId: String, canEdit: Boolean = true)(implicit messages: Messages) {

  def propertyOrLandDescription(index: Int): Option[AnswerRow] = userAnswers.get(PropertyOrLandDescriptionPage(index)) map {
    x =>
      AnswerRow(
        "propertyOrLandDescription.checkYourAnswersLabel",
        HtmlFormat.escape(x),
        routes.PropertyOrLandDescriptionController.onPageLoad(CheckMode, index, draftId).url
      )
  }

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

        val trusteeIndividualOrBusinessMessagePrefix = if (trustee.isLead) "leadTrusteeIndividualOrBusiness" else "trusteeIndividualOrBusiness"
        val trusteeFullNameMessagePrefix = if (trustee.isLead) "leadTrusteesName" else "trusteesName"
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

        AnswerSection(Some(Messages("answerPage.section.individualBeneficiary.subheading") + " " + (index + 1)),
          questions, if (index == 0) {
            Some(Messages("answerPage.section.beneficiaries.heading"))
          } else None)
    }
  }

  def classOfBeneficiaries(individualBeneficiariesExist: Boolean) : Option[Seq[AnswerSection]] = {
    for {
      beneficiaries <- userAnswers.get(ClassOfBeneficiaries)
      indexed = beneficiaries.zipWithIndex
    } yield indexed.map {
      case (beneficiary, index) =>

        val questions = Seq(
          classBeneficiaryDescription(index)
        ).flatten

        val sectionKey = if (index == 0 && !individualBeneficiariesExist) {
          Some(Messages("answerPage.section.beneficiaries.heading"))
        } else None

        AnswerSection(Some(Messages("answerPage.section.classOfBeneficiary.subheading") + " " + (index + 1)),
          questions, sectionKey)
    }
  }

  def moneyAsset : Option[Seq[AnswerSection]] = {
    val questions = Seq(
      assetMoneyValue(0)
    ).flatten

    if (questions.nonEmpty) Some(Seq(AnswerSection(Some(Messages("answerPage.section.moneyAsset.subheading")),
      questions, Some(Messages("answerPage.section.assets.heading"))
      ))) else None
  }

  def agentInternationalAddress: Option[AnswerRow] = userAnswers.get(AgentInternationalAddressPage) map {
    x =>
      AnswerRow(
        "site.address.international.checkYourAnswersLabel",
        internationalAddress(x, countryOptions),
        routes.AgentInternationalAddressController.onPageLoad(CheckMode, draftId).url,
        agencyName(userAnswers),
        canEdit = canEdit
      )
  }

  def classBeneficiaryDescription(index: Int): Option[AnswerRow] = userAnswers.get(ClassBeneficiaryDescriptionPage(index)) map {
    x =>
      AnswerRow(
        "classBeneficiaryDescription.checkYourAnswersLabel",

        HtmlFormat.escape(x),
        routes.ClassBeneficiaryDescriptionController.onPageLoad(CheckMode,index, draftId).url,
        canEdit = canEdit
      )
  }

  def sharesOnStockExchange: Option[AnswerRow] = userAnswers.get(SharesOnStockExchangePage) map {
    x =>
      AnswerRow(
        "sharesOnStockExchange.checkYourAnswersLabel",
        yesOrNo(x),
        routes.SharesOnStockExchangeController.onPageLoad(CheckMode, draftId).url
      )
  }

  def sharesInAPortfolio: Option[AnswerRow] = userAnswers.get(SharesInAPortfolioPage) map {
    x =>
      AnswerRow(
        "sharesInAPortfolio.checkYourAnswersLabel",
        yesOrNo(x),
        routes.SharesInAPortfolioController.onPageLoad(CheckMode, draftId).url
      )
  }

  def shareValueInTrust: Option[AnswerRow] = userAnswers.get(ShareValueInTrustPage) map {
    x =>
      AnswerRow(
        "shareValueInTrust.checkYourAnswersLabel",
        HtmlFormat.escape(x),
        routes.ShareValueInTrustController.onPageLoad(CheckMode, draftId).url
      )
  }

  def shareQuantityInTrust: Option[AnswerRow] = userAnswers.get(ShareQuantityInTrustPage) map {
    x =>
      AnswerRow(
        "shareQuantityInTrust.checkYourAnswersLabel",
        HtmlFormat.escape(x),
        routes.ShareQuantityInTrustController.onPageLoad(CheckMode, draftId).url
      )
  }

  def sharePortfolioValueInTrust: Option[AnswerRow] = userAnswers.get(SharePortfolioValueInTrustPage) map {
    x =>
      AnswerRow(
        "sharePortfolioValueInTrust.checkYourAnswersLabel",
        HtmlFormat.escape(x),
        routes.SharePortfolioValueInTrustController.onPageLoad(CheckMode, draftId).url
      )
  }

  def sharePortfolioQuantityInTrust: Option[AnswerRow] = userAnswers.get(SharePortfolioQuantityInTrustPage) map {
    x =>
      AnswerRow(
        "sharePortfolioQuantityInTrust.checkYourAnswersLabel",
        HtmlFormat.escape(x),
        routes.SharePortfolioQuantityInTrustController.onPageLoad(CheckMode, draftId).url
      )
  }

  def sharePortfolioOnStockExchange: Option[AnswerRow] = userAnswers.get(SharePortfolioOnStockExchangePage) map {
    x =>
      AnswerRow(
        "sharePortfolioOnStockExchange.checkYourAnswersLabel",
        yesOrNo(x),
        routes.SharePortfolioOnStockExchangeController.onPageLoad(CheckMode, draftId).url
      )
  }

  def sharePortfolioName: Option[AnswerRow] = userAnswers.get(SharePortfolioNamePage) map {
    x =>
      AnswerRow(
        "sharePortfolioName.checkYourAnswersLabel",
        HtmlFormat.escape(x),
        routes.SharePortfolioNameController.onPageLoad(CheckMode, draftId).url
      )
  }

  def shareClass: Option[AnswerRow] = userAnswers.get(ShareClassPage) map {
    x =>
      AnswerRow(
        "shareClass.checkYourAnswersLabel",
        HtmlFormat.escape(messages(s"shareClass.$x")),
        routes.ShareClassController.onPageLoad(CheckMode, draftId).url
      )
  }

  def agentUKAddress: Option[AnswerRow] = userAnswers.get(AgentUKAddressPage) map {
    x =>
      AnswerRow(
        "site.address.uk.checkYourAnswersLabel",
        ukAddress(x),
        routes.AgentUKAddressController.onPageLoad(CheckMode, draftId).url,
        agencyName(userAnswers),
        canEdit = canEdit
      )
  }

  def agentAddressYesNo: Option[AnswerRow] = userAnswers.get(AgentAddressYesNoPage) map {
    x =>
      AnswerRow(
        "agentAddressYesNo.checkYourAnswersLabel",
        yesOrNo(x),
        routes.AgentAddressYesNoController.onPageLoad(CheckMode, draftId).url,
        agencyName(userAnswers),
        canEdit = canEdit
      )
  }

  def individualBeneficiaryAddressUKYesNo(index: Int): Option[AnswerRow] = userAnswers.get(IndividualBeneficiaryAddressUKYesNoPage(index)) map {
    x =>
      AnswerRow(
        "individualBeneficiaryAddressUKYesNo.checkYourAnswersLabel",
        yesOrNo(x),
        routes.IndividualBeneficiaryAddressUKYesNoController.onPageLoad(CheckMode, index, draftId).url,
        indBeneficiaryName(index,userAnswers),
        canEdit = canEdit
      )
  }

  def agentName: Option[AnswerRow] = userAnswers.get(AgentNamePage) map {
    x =>
      AnswerRow(
        "agentName.checkYourAnswersLabel",
        HtmlFormat.escape(x),
        routes.AgentNameController.onPageLoad(CheckMode, draftId).url,
        canEdit = canEdit
      )
  }

  def addABeneficiary: Option[AnswerRow] = userAnswers.get(AddABeneficiaryPage) map {
    x =>
      AnswerRow(
        "addABeneficiary.checkYourAnswersLabel",
        HtmlFormat.escape(messages(s"addABeneficiary.$x")),
        routes.AddABeneficiaryController.onPageLoad(draftId).url,
        canEdit = canEdit
      )
  }

  def individualBeneficiaryVulnerableYesNo(index: Int): Option[AnswerRow] = userAnswers.get(IndividualBeneficiaryVulnerableYesNoPage(index)) map {
    x =>
      AnswerRow(
        "individualBeneficiaryVulnerableYesNo.checkYourAnswersLabel",
        yesOrNo(x),
        routes.IndividualBeneficiaryVulnerableYesNoController.onPageLoad(CheckMode, index, draftId).url,
        indBeneficiaryName(index,userAnswers),
        canEdit = canEdit
      )
  }

  def individualBeneficiaryAddressUK(index: Int): Option[AnswerRow] = userAnswers.get(IndividualBeneficiaryAddressUKPage(index)) map {
    x =>
      AnswerRow(
        "individualBeneficiaryAddressUK.checkYourAnswersLabel",
        ukAddress(x),
        routes.IndividualBeneficiaryAddressUKController.onPageLoad(CheckMode, index, draftId).url,
        indBeneficiaryName(index,userAnswers),
        canEdit = canEdit
      )
  }

  def individualBeneficiaryAddressYesNo(index: Int): Option[AnswerRow] = userAnswers.get(IndividualBeneficiaryAddressYesNoPage(index)) map {
    x =>
      AnswerRow(
        "individualBeneficiaryAddressYesNo.checkYourAnswersLabel",
        yesOrNo(x),
        routes.IndividualBeneficiaryAddressYesNoController.onPageLoad(CheckMode, index, draftId).url,
        indBeneficiaryName(index,userAnswers),
        canEdit = canEdit
      )
  }

  def individualBeneficiaryNationalInsuranceNumber(index: Int): Option[AnswerRow] = userAnswers.get(IndividualBeneficiaryNationalInsuranceNumberPage(index)) map {
    x =>
      AnswerRow(
        "individualBeneficiaryNationalInsuranceNumber.checkYourAnswersLabel",
        HtmlFormat.escape(formatNino(x)),
        routes.IndividualBeneficiaryNationalInsuranceNumberController.onPageLoad(CheckMode, index, draftId).url,
        indBeneficiaryName(index,userAnswers),
        canEdit = canEdit
      )
  }

  def individualBeneficiaryNationalInsuranceYesNo(index: Int): Option[AnswerRow] = userAnswers.get(IndividualBeneficiaryNationalInsuranceYesNoPage(index)) map {
    x =>
      AnswerRow(
        "individualBeneficiaryNationalInsuranceYesNo.checkYourAnswersLabel",
        yesOrNo(x),
        routes.IndividualBeneficiaryNationalInsuranceYesNoController.onPageLoad(CheckMode, index, draftId).url,
        indBeneficiaryName(index,userAnswers),
        canEdit = canEdit
      )
  }

  def individualBeneficiaryIncome(index: Int): Option[AnswerRow] = userAnswers.get(IndividualBeneficiaryIncomePage(index)) map {
    x =>
      AnswerRow(
        "individualBeneficiaryIncome.checkYourAnswersLabel",
        HtmlFormat.escape(x),
        routes.IndividualBeneficiaryIncomeController.onPageLoad(CheckMode, index, draftId).url,
        indBeneficiaryName(index, userAnswers),
        canEdit = canEdit
      )
  }

  def individualBeneficiaryIncomeYesNo(index: Int): Option[AnswerRow] = userAnswers.get(IndividualBeneficiaryIncomeYesNoPage(index)) map {
    x =>
      AnswerRow(
        "individualBeneficiaryIncomeYesNo.checkYourAnswersLabel",
        yesOrNo(x),
        routes.IndividualBeneficiaryIncomeYesNoController.onPageLoad(CheckMode, index, draftId).url,
        indBeneficiaryName(index,userAnswers),
        canEdit = canEdit
      )
  }

  def individualBeneficiaryDateOfBirth(index: Int): Option[AnswerRow] = userAnswers.get(IndividualBeneficiaryDateOfBirthPage(index)) map {
    x =>
      AnswerRow(
        "individualBeneficiaryDateOfBirth.checkYourAnswersLabel",
        HtmlFormat.escape(x.format(dateFormatter)),
        routes.IndividualBeneficiaryDateOfBirthController.onPageLoad(CheckMode, index, draftId).url,
        indBeneficiaryName(index,userAnswers),
        canEdit = canEdit
      )
  }

  def individualBeneficiaryDateOfBirthYesNo(index: Int): Option[AnswerRow] = userAnswers.get(IndividualBeneficiaryDateOfBirthYesNoPage(index)) map {
    x =>
      AnswerRow(
        "individualBeneficiaryDateOfBirthYesNo.checkYourAnswersLabel",
        yesOrNo(x),
        routes.IndividualBeneficiaryDateOfBirthYesNoController.onPageLoad(CheckMode, index, draftId).url,
        indBeneficiaryName(index,userAnswers),
        canEdit = canEdit
      )
  }

  def individualBeneficiaryName(index: Int): Option[AnswerRow] = userAnswers.get(IndividualBeneficiaryNamePage(index)) map {
    x =>
      AnswerRow(
        "individualBeneficiaryName.checkYourAnswersLabel",
        HtmlFormat.escape(s"${x.firstName} ${x.middleName.getOrElse("")} ${x.lastName}"),
        routes.IndividualBeneficiaryNameController.onPageLoad(CheckMode, index, draftId).url,
        canEdit = canEdit
      )
  }

  def wasSettlorsAddressUKYesNo: Option[AnswerRow] = userAnswers.get(WasSettlorsAddressUKYesNoPage) map {
    x =>
      AnswerRow(
        "wasSettlorsAddressUKYesNo.checkYourAnswersLabel",
        yesOrNo(x),
        routes.WasSettlorsAddressUKYesNoController.onPageLoad(CheckMode, draftId).url,
        deceasedSettlorName(userAnswers),
        canEdit = canEdit
      )
  }

  def setupAfterSettlorDied: Option[AnswerRow] = userAnswers.get(SetupAfterSettlorDiedPage) map {
    x =>
      AnswerRow(
        "setupAfterSettlorDied.checkYourAnswersLabel",
        yesOrNo(x),
        routes.SetupAfterSettlorDiedController.onPageLoad(CheckMode, draftId).url,
        canEdit = canEdit
      )
  }

  def settlorsUKAddress: Option[AnswerRow] = userAnswers.get(SettlorsUKAddressPage) map {
    x =>
      AnswerRow(
        "settlorsUKAddress.checkYourAnswersLabel",
        ukAddress(x),
        routes.SettlorsUKAddressController.onPageLoad(CheckMode, draftId).url,
        deceasedSettlorName(userAnswers),
        canEdit = canEdit
      )
  }

  def settlorsNINoYesNo: Option[AnswerRow] = userAnswers.get(SettlorsNINoYesNoPage) map {
    x =>
      AnswerRow(
        "settlorsNINoYesNo.checkYourAnswersLabel",
        yesOrNo(x),
        routes.SettlorsNINoYesNoController.onPageLoad(CheckMode, draftId).url,
        deceasedSettlorName(userAnswers),
        canEdit = canEdit
      )
  }

  def settlorsName: Option[AnswerRow] = userAnswers.get(SettlorsNamePage) map {
    x =>
      AnswerRow(
        "settlorsName.checkYourAnswersLabel",
        HtmlFormat.escape(s"${x.firstName} ${x.middleName.getOrElse("")} ${x.lastName}"),
        routes.SettlorsNameController.onPageLoad(CheckMode, draftId).url,
        canEdit = canEdit
      )
  }

  def settlorsLastKnownAddressYesNo: Option[AnswerRow] = userAnswers.get(SettlorsLastKnownAddressYesNoPage) map {
    x =>
      AnswerRow(
        "settlorsLastKnownAddressYesNo.checkYourAnswersLabel",
        yesOrNo(x),
        routes.SettlorsLastKnownAddressYesNoController.onPageLoad(CheckMode, draftId).url,
        deceasedSettlorName(userAnswers),
        canEdit = canEdit
      )
  }

  def settlorsInternationalAddress: Option[AnswerRow] = userAnswers.get(SettlorsInternationalAddressPage) map {
    x =>
      AnswerRow(
        "settlorsInternationalAddress.checkYourAnswersLabel",
        internationalAddress(x, countryOptions),
        routes.SettlorsInternationalAddressController.onPageLoad(CheckMode, draftId).url,
        deceasedSettlorName(userAnswers),
        canEdit = canEdit
      )
  }

  def settlorsDateOfBirth: Option[AnswerRow] = userAnswers.get(SettlorsDateOfBirthPage) map {
    x =>
      AnswerRow(
        "settlorsDateOfBirth.checkYourAnswersLabel",
        HtmlFormat.escape(x.format(dateFormatter)),
        routes.SettlorsDateOfBirthController.onPageLoad(CheckMode, draftId).url,
        deceasedSettlorName(userAnswers),
        canEdit = canEdit
      )
  }

  def settlorNationalInsuranceNumber: Option[AnswerRow] = userAnswers.get(SettlorNationalInsuranceNumberPage) map {
    x =>
      AnswerRow(
        "settlorNationalInsuranceNumber.checkYourAnswersLabel",
        HtmlFormat.escape(formatNino(x)),
        routes.SettlorNationalInsuranceNumberController.onPageLoad(CheckMode, draftId).url,
        deceasedSettlorName(userAnswers),
        canEdit = canEdit
      )
  }

  def settlorDateOfDeathYesNo: Option[AnswerRow] = userAnswers.get(SettlorDateOfDeathYesNoPage) map {
    x =>
      AnswerRow(
        "settlorDateOfDeathYesNo.checkYourAnswersLabel",
        yesOrNo(x),
        routes.SettlorDateOfDeathYesNoController.onPageLoad(CheckMode, draftId).url,
        deceasedSettlorName(userAnswers),
        canEdit = canEdit
      )
  }

  def settlorDateOfDeath: Option[AnswerRow] = userAnswers.get(SettlorDateOfDeathPage) map {
    x =>
      AnswerRow(
        "settlorDateOfDeath.checkYourAnswersLabel",
        HtmlFormat.escape(x.format(dateFormatter)),
        routes.SettlorDateOfDeathController.onPageLoad(CheckMode, draftId).url,
        deceasedSettlorName(userAnswers),
        canEdit = canEdit
      )
  }

  def settlorDateOfBirthYesNo: Option[AnswerRow] = userAnswers.get(SettlorDateOfBirthYesNoPage) map {
    x =>
      AnswerRow(
        "settlorDateOfBirthYesNo.checkYourAnswersLabel",
        yesOrNo(x),
        routes.SettlorDateOfBirthYesNoController.onPageLoad(CheckMode, draftId).url,
        deceasedSettlorName(userAnswers),
        canEdit = canEdit
      )
  }

  def assetMoneyValue(index: Int): Option[AnswerRow] = userAnswers.get(AssetMoneyValuePage(index)) map {
    x =>
      AnswerRow(
        "assetMoneyValue.checkYourAnswersLabel",
        HtmlFormat.escape(s"£$x"),
        routes.AssetMoneyValueController.onPageLoad(CheckMode, index, draftId).url,
        canEdit = canEdit
      )
  }

  def whatKindOfAsset(index: Int): Option[AnswerRow] = userAnswers.get(WhatKindOfAssetPage(index)) map {
    x =>
      AnswerRow(
        "whatKindOfAsset.checkYourAnswersLabel",
        HtmlFormat.escape(messages(s"whatKindOfAsset.$x")),
        routes.WhatKindOfAssetController.onPageLoad(CheckMode, index, draftId).url,
        canEdit = canEdit
      )
  }

  def agentInternalReference: Option[AnswerRow] = userAnswers.get(AgentInternalReferencePage) map {
    x =>
      AnswerRow(
        "agentInternalReference.checkYourAnswersLabel",
        HtmlFormat.escape(x),
        routes.AgentInternalReferenceController.onPageLoad(CheckMode, draftId).url,
        canEdit = canEdit
      )
  }

  def agenciesTelephoneNumber: Option[AnswerRow] = userAnswers.get(AgentTelephoneNumberPage) map {
    x =>
      AnswerRow(
        "agentTelephoneNumber.checkYourAnswersLabel",
        HtmlFormat.escape(x),
        routes.AgentTelephoneNumberController.onPageLoad(CheckMode, draftId).url,
        canEdit = canEdit
      )
  }

  def trusteesNino(index: Int): Option[AnswerRow] = userAnswers.get(TrusteesNinoPage(index)) map {
    x =>
      AnswerRow(
        "trusteesNino.checkYourAnswersLabel",
        HtmlFormat.escape(formatNino(x)),
        routes.TrusteesNinoController.onPageLoad(CheckMode, index, draftId).url,
        trusteeName(index, userAnswers),
        canEdit = canEdit
      )
  }

  def trusteeLiveInTheUK(index : Int): Option[AnswerRow] = userAnswers.get(TrusteeLiveInTheUKPage(index)) map {
    x =>
      AnswerRow(
        "trusteeLiveInTheUK.checkYourAnswersLabel",
        yesOrNo(x),
        routes.TrusteeLiveInTheUKController.onPageLoad(CheckMode, index, draftId).url,
        trusteeName(index, userAnswers),
        canEdit = canEdit
      )
  }

  def trusteesUkAddress(index: Int): Option[AnswerRow] = userAnswers.get(TrusteesUkAddressPage(index)) map {
    x =>
      AnswerRow(
        "trusteesUkAddress.checkYourAnswersLabel",
        ukAddress(x),
        routes.TrusteesUkAddressController.onPageLoad(CheckMode, index, draftId).url,
        trusteeName(index, userAnswers),
        canEdit = canEdit
      )
  }

  def trusteesDateOfBirth(index : Int): Option[AnswerRow] = userAnswers.get(TrusteesDateOfBirthPage(index)) map {
    x =>
      AnswerRow(
        "trusteesDateOfBirth.checkYourAnswersLabel",
        HtmlFormat.escape(x.format(dateFormatter)),
        routes.TrusteesDateOfBirthController.onPageLoad(CheckMode, index, draftId).url,
        trusteeName(index, userAnswers),
        canEdit = canEdit
      )
  }

  def telephoneNumber(index : Int): Option[AnswerRow] = userAnswers.get(TelephoneNumberPage(index)) map {
    x =>
      AnswerRow(
        "telephoneNumber.checkYourAnswersLabel",
        HtmlFormat.escape(x),
        routes.TelephoneNumberController.onPageLoad(CheckMode, index, draftId).url,
        trusteeName(index, userAnswers),
        canEdit = canEdit
      )
  }

  def trusteeAUKCitizen(index : Int): Option[AnswerRow] = userAnswers.get(TrusteeAUKCitizenPage(index)) map {
    x =>
      AnswerRow(
        "trusteeAUKCitizen.checkYourAnswersLabel",
        yesOrNo(x),
        routes.TrusteeAUKCitizenController.onPageLoad(CheckMode,index, draftId).url,
        trusteeName(index, userAnswers),
        canEdit = canEdit
      )
  }


  def trusteeFullName(index : Int, messagePrefix: String): Option[AnswerRow] = userAnswers.get(TrusteesNamePage(index)) map {
    x => AnswerRow(
      s"$messagePrefix.checkYourAnswersLabel",
      HtmlFormat.escape(s"${x.firstName} ${x.middleName.getOrElse("")} ${x.lastName}"),
      routes.TrusteesNameController.onPageLoad(CheckMode, index, draftId).url,
      canEdit = canEdit
    )
  }

  def trusteeIndividualOrBusiness(index : Int, messagePrefix: String): Option[AnswerRow] = userAnswers.get(TrusteeIndividualOrBusinessPage(index)) map {
    x =>
      AnswerRow(
        s"$messagePrefix.checkYourAnswersLabel",
        HtmlFormat.escape(messages(s"individualOrBusiness.$x")),
        routes.TrusteeIndividualOrBusinessController.onPageLoad(CheckMode, index, draftId).url,
        canEdit = canEdit
      )
  }

  def isThisLeadTrustee(index: Int): Option[AnswerRow] = userAnswers.get(IsThisLeadTrusteePage(index)) map {
    x =>
      AnswerRow(
        "isThisLeadTrustee.checkYourAnswersLabel",
        yesOrNo(x),
        routes.IsThisLeadTrusteeController.onPageLoad(CheckMode, index, draftId).url,
        canEdit = canEdit
      )
  }

  def postcodeForTheTrust: Option[AnswerRow] = userAnswers.get(PostcodeForTheTrustPage) map {
    x =>
      AnswerRow(
        "postcodeForTheTrust.checkYourAnswersLabel",
        HtmlFormat.escape(x),
        routes.PostcodeForTheTrustController.onPageLoad(CheckMode, draftId).url,
        canEdit = canEdit
      )
  }

  def whatIsTheUTR: Option[AnswerRow] = userAnswers.get(WhatIsTheUTRPage) map {
    x =>
      AnswerRow(
        "whatIsTheUTR.checkYourAnswersLabel",
        HtmlFormat.escape(x),
        routes.WhatIsTheUTRController.onPageLoad(CheckMode, draftId).url,
        canEdit = canEdit
      )
  }

  def trustHaveAUTR: Option[AnswerRow] = userAnswers.get(TrustHaveAUTRPage) map {
    x =>
      AnswerRow(
        "trustHaveAUTR.checkYourAnswersLabel",
        yesOrNo(x),
        routes.TrustHaveAUTRController.onPageLoad(CheckMode, draftId).url,
        canEdit = canEdit
      )
  }

  def trustRegisteredOnline: Option[AnswerRow] = userAnswers.get(TrustRegisteredOnlinePage) map {
    x =>
      AnswerRow(
        "trustRegisteredOnline.checkYourAnswersLabel",
        yesOrNo(x),
        routes.TrustRegisteredOnlineController.onPageLoad(CheckMode, draftId).url,
        canEdit = canEdit
      )
  }

  def whenTrustSetup: Option[AnswerRow] = userAnswers.get(WhenTrustSetupPage) map {
    x =>
      AnswerRow(
        "whenTrustSetup.checkYourAnswersLabel",
        HtmlFormat.escape(x.format(dateFormatter)),
        routes.WhenTrustSetupController.onPageLoad(CheckMode, draftId).url,
        canEdit = canEdit
      )
  }

  def agentOtherThanBarrister: Option[AnswerRow] = userAnswers.get(AgentOtherThanBarristerPage) map {
    x => AnswerRow("agentOtherThanBarrister.checkYourAnswersLabel", yesOrNo(x), routes.AgentOtherThanBarristerController.onPageLoad(CheckMode, draftId).url, canEdit = canEdit)
  }

  def inheritanceTaxAct: Option[AnswerRow] = userAnswers.get(InheritanceTaxActPage) map {
    x => AnswerRow("inheritanceTaxAct.checkYourAnswersLabel", yesOrNo(x), routes.InheritanceTaxActController.onPageLoad(CheckMode, draftId).url, canEdit = canEdit)
  }

  def nonresidentType: Option[AnswerRow] = userAnswers.get(NonResidentTypePage) map {
    x => AnswerRow("nonresidentType.checkYourAnswersLabel", answer("nonresidentType", x), routes.NonResidentTypeController.onPageLoad(CheckMode, draftId).url, canEdit = canEdit)
  }

  def trustPreviouslyResident: Option[AnswerRow] = userAnswers.get(TrustPreviouslyResidentPage) map {
    x => AnswerRow("trustPreviouslyResident.checkYourAnswersLabel", escape(country(x, countryOptions)), routes.TrustPreviouslyResidentController.onPageLoad(CheckMode, draftId).url, canEdit = canEdit)
  }

  def trustResidentOffshore: Option[AnswerRow] = userAnswers.get(TrustResidentOffshorePage) map {
    x => AnswerRow("trustResidentOffshore.checkYourAnswersLabel", yesOrNo(x), routes.TrustResidentOffshoreController.onPageLoad(CheckMode, draftId).url, canEdit = canEdit)
  }

  def establishedUnderScotsLaw: Option[AnswerRow] = userAnswers.get(EstablishedUnderScotsLawPage) map {
    x => AnswerRow("establishedUnderScotsLaw.checkYourAnswersLabel", yesOrNo(x), routes.EstablishedUnderScotsLawController.onPageLoad(CheckMode, draftId).url, canEdit = canEdit)
  }

  def trustResidentInUK: Option[AnswerRow] = userAnswers.get(TrustResidentInUKPage) map {
    x => AnswerRow("trustResidentInUK.checkYourAnswersLabel", yesOrNo(x), routes.TrustResidentInUKController.onPageLoad(CheckMode, draftId).url, canEdit = canEdit)
  }

  def countryAdministeringTrust: Option[AnswerRow] = userAnswers.get(CountryAdministeringTrustPage) map {
    x => AnswerRow("countryAdministeringTrust.checkYourAnswersLabel", escape(country(x, countryOptions)), routes.CountryAdministeringTrustController.onPageLoad(CheckMode, draftId).url, canEdit = canEdit)
  }

  def administrationInsideUK: Option[AnswerRow] = userAnswers.get(AdministrationInsideUKPage) map {
    x => AnswerRow("administrationInsideUK.checkYourAnswersLabel", yesOrNo(x), routes.AdministrationInsideUKController.onPageLoad(CheckMode, draftId).url, canEdit = canEdit)
  }

  def countryGoverningTrust: Option[AnswerRow] = userAnswers.get(CountryGoverningTrustPage) map {
    x => AnswerRow("countryGoverningTrust.checkYourAnswersLabel", escape(country(x, countryOptions)), routes.CountryGoverningTrustController.onPageLoad(CheckMode, draftId).url, canEdit = canEdit)
  }

  def governedInsideTheUK: Option[AnswerRow] = userAnswers.get(GovernedInsideTheUKPage) map {
    x => AnswerRow("governedInsideTheUK.checkYourAnswersLabel", yesOrNo(x), routes.GovernedInsideTheUKController.onPageLoad(CheckMode, draftId).url, canEdit = canEdit)
  }

  def trustName: Option[AnswerRow] = userAnswers.get(TrustNamePage) map {
    x => AnswerRow("trustName.checkYourAnswersLabel", escape(x), routes.TrustNameController.onPageLoad(CheckMode, draftId).url, canEdit = canEdit)
  }

  def registeringTrustFor5A: Option[AnswerRow] = userAnswers.get(RegisteringTrustFor5APage) map {
    x => AnswerRow("registeringTrustFor5A.checkYourAnswersLabel", yesOrNo(x), routes.RegisteringTrustFor5AController.onPageLoad(CheckMode, draftId).url, canEdit = canEdit)
  }

}

object CheckYourAnswersHelper {

  val dateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy")

  def yesOrNo(answer: Boolean)(implicit messages: Messages): Html = {
    if (answer) {
      HtmlFormat.escape(messages("site.yes"))
    } else {
      HtmlFormat.escape(messages("site.no"))
    }
  }

  def formatNino(nino: String): String = Nino(nino).formatted

  def country(code : String, countryOptions: CountryOptions) : String =
    countryOptions.options.find(_.value.equals(code)).map(_.label).getOrElse("")

  def trusteeName(index: Int, userAnswers: UserAnswers): String =
    userAnswers.get(TrusteesNamePage(index)).get.toString

  def answer[T](key : String, answer: T)(implicit messages: Messages) : Html =
    HtmlFormat.escape(messages(s"$key.$answer"))

  def escape(x : String) = HtmlFormat.escape(x)

  def deceasedSettlorName(userAnswers: UserAnswers): String =
    userAnswers.get(SettlorsNamePage).get.toString

  def indBeneficiaryName(index: Int, userAnswers: UserAnswers): String = {
    userAnswers.get(IndividualBeneficiaryNamePage(index)).get.toString
  }

  def agencyName(userAnswers: UserAnswers): String = {
    userAnswers.get(AgentNamePage).get.toString
  }

  def ukAddress(address: UKAddress): Html = {
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

  def internationalAddress(address: InternationalAddress, countryOptions: CountryOptions): Html = {
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
