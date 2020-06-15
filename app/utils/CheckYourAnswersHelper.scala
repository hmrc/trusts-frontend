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

package utils

import controllers.register.settlors.living_settlor.routes
import javax.inject.Inject
import mapping.reads._
import models.NormalMode
import models.core.UserAnswers
import pages.register._
import pages.register.agents._
import pages.register.asset.WhatKindOfAssetPage
import pages.register.asset.money.AssetMoneyValuePage
import pages.register.asset.property_or_land._
import pages.register.asset.shares._
import pages.register.beneficiaries.individual._
import pages.register.beneficiaries.{AddABeneficiaryPage, ClassBeneficiaryDescriptionPage}
import pages.register.settlors.SetUpAfterSettlorDiedYesNoPage
import pages.register.settlors.deceased_settlor._
import pages.register.settlors.living_settlor._
import pages.register.settlors.living_settlor.trust_type._
import pages.register.trust_details._
import pages.register.trustees._
import pages.register.trustees.individual._
import pages.register.trustees.organisation._
import play.api.i18n.Messages
import play.twirl.api.HtmlFormat
import sections.LivingSettlors
import utils.CheckAnswersFormatters._
import utils.countryOptions.CountryOptions
import viewmodels.{AnswerRow, AnswerSection}

class CheckYourAnswersHelper @Inject()(countryOptions: CountryOptions)
                                      (userAnswers: UserAnswers,
                                       draftId: String,
                                       canEdit: Boolean)
                                      (implicit messages: Messages) {

  def settlorBusinessName(index: Int): Option[AnswerRow] = userAnswers.get(SettlorBusinessNamePage(index)) map {
    x =>
      AnswerRow(
        "settlorBusinessName.checkYourAnswersLabel",
        HtmlFormat.escape(x),
        Some(routes.SettlorBusinessNameController.onPageLoad(NormalMode, index, draftId).url),
        canEdit = canEdit
      )
  }

  def kindOfTrust: Option[AnswerRow] = userAnswers.get(KindOfTrustPage) map {
    x =>
      AnswerRow(
        "kindOfTrust.checkYourAnswersLabel",
        HtmlFormat.escape(messages(s"kindOfTrust.$x")),
        Some(routes.KindOfTrustController.onPageLoad(NormalMode, draftId).url),
        canEdit = canEdit
      )
  }

  def deedOfVariation: Option[AnswerRow] = userAnswers.get(HowDeedOfVariationCreatedPage) map {
    x =>
      AnswerRow(
        "howDeedOfVariationCreated.checkYourAnswersLabel",
        HtmlFormat.escape(messages(s"howDeedOfVariationCreated.$x")),
        Some(controllers.register.settlors.routes.HowDeedOfVariationCreatedController.onPageLoad(NormalMode, draftId).url),
        canEdit = canEdit
      )
  }

  def settlorsBasedInTheUK: Option[AnswerRow] = userAnswers.get(SettlorsBasedInTheUKPage) map {
    x =>
      AnswerRow(
        "settlorsBasedInTheUK.checkYourAnswersLabel",
        yesOrNo(x),
        Some(controllers.register.trust_details.routes.SettlorsBasedInTheUKController.onPageLoad(NormalMode, draftId).url),
        canEdit = canEdit
      )
  }

  def trusteesBasedInTheUK: Option[AnswerRow] = userAnswers.get(TrusteesBasedInTheUKPage) map {
    x =>
      AnswerRow(
        "trusteesBasedInTheUK.checkYourAnswersLabel",
        HtmlFormat.escape(messages(s"trusteesBasedInTheUK.$x")),
        Some(controllers.register.trust_details.routes.TrusteesBasedInTheUKController.onPageLoad(NormalMode, draftId).url),
        canEdit = canEdit
      )
  }

  def holdoverReliefYesNo: Option[AnswerRow] = userAnswers.get(HoldoverReliefYesNoPage) map {
    x =>
      AnswerRow(
        "holdoverReliefYesNo.checkYourAnswersLabel",
        yesOrNo(x),
        Some(routes.HoldoverReliefYesNoController.onPageLoad(NormalMode, draftId).url),
        canEdit = canEdit
      )
  }

  def settlorIndividualPassportYesNo(index: Int): Option[AnswerRow] = userAnswers.get(SettlorIndividualPassportYesNoPage(index)) map {
    x =>
      AnswerRow(
        "settlorIndividualPassportYesNo.checkYourAnswersLabel",
        yesOrNo(x),
        Some(routes.SettlorIndividualPassportYesNoController.onPageLoad(NormalMode, index, draftId).url),
        livingSettlorName(index, userAnswers),
        canEdit = canEdit
      )
  }

  def settlorIndividualPassport(index: Int): Option[AnswerRow] = userAnswers.get(SettlorIndividualPassportPage(index)) map {
    x =>
      AnswerRow(
        "settlorIndividualPassport.checkYourAnswersLabel",
        passportOrIDCard(x, countryOptions),
        Some(routes.SettlorIndividualPassportController.onPageLoad(NormalMode, index, draftId).url),
        livingSettlorName(index, userAnswers),
        canEdit = canEdit
      )
  }

  def settlorIndividualIDCardYesNo(index: Int): Option[AnswerRow] = userAnswers.get(SettlorIndividualIDCardYesNoPage(index)) map {
    x =>
      AnswerRow(
        "settlorIndividualIDCardYesNo.checkYourAnswersLabel",
        yesOrNo(x),
        Some(routes.SettlorIndividualIDCardYesNoController.onPageLoad(NormalMode, index, draftId).url),
        livingSettlorName(index, userAnswers),
        canEdit = canEdit
      )
  }

  def settlorIndividualIDCard(index: Int): Option[AnswerRow] = userAnswers.get(SettlorIndividualIDCardPage(index)) map {
    x =>
      AnswerRow(
        "settlorIndividualIDCard.checkYourAnswersLabel",
        passportOrIDCard(x, countryOptions),
        Some(routes.SettlorIndividualIDCardController.onPageLoad(NormalMode, index, draftId).url),
        livingSettlorName(index, userAnswers),
        canEdit = canEdit
      )
  }

  def settlorIndividualAddressUKYesNo(index: Int): Option[AnswerRow] = userAnswers.get(SettlorAddressUKYesNoPage(index)) map {
    x =>
      AnswerRow(
        "settlorIndividualAddressUKYesNo.checkYourAnswersLabel",
        yesOrNo(x),
        Some(routes.SettlorIndividualAddressUKYesNoController.onPageLoad(NormalMode, index, draftId).url),
        livingSettlorName(index, userAnswers),
        canEdit = canEdit
      )
  }

  def settlorIndividualAddressUK(index: Int): Option[AnswerRow] = userAnswers.get(SettlorAddressUKPage(index)) map {
    x =>
      AnswerRow(
        "settlorIndividualAddressUK.checkYourAnswersLabel",
        ukAddress(x),
        Some(routes.SettlorIndividualAddressUKController.onPageLoad(NormalMode, index, draftId).url),
        livingSettlorName(index, userAnswers),
        canEdit = canEdit
      )
  }

  def settlorIndividualAddressInternational(index: Int): Option[AnswerRow] = userAnswers.get(SettlorAddressInternationalPage(index)) map {
    x =>
      AnswerRow(
        "settlorIndividualAddressInternational.checkYourAnswersLabel",
        internationalAddress(x, countryOptions),
        Some(routes.SettlorIndividualAddressInternationalController.onPageLoad(NormalMode, index, draftId).url),
        livingSettlorName(index, userAnswers),
        canEdit = canEdit
      )
  }

  def settlorIndividualNINOYesNo(index: Int): Option[AnswerRow] = userAnswers.get(SettlorIndividualNINOYesNoPage(index)) map {
    x =>
      AnswerRow(
        "settlorIndividualNINOYesNo.checkYourAnswersLabel",
        yesOrNo(x),
        Some(routes.SettlorIndividualNINOYesNoController.onPageLoad(NormalMode, index, draftId).url),
        livingSettlorName(index, userAnswers),
        canEdit = canEdit
      )
  }

  def settlorIndividualNINO(index: Int): Option[AnswerRow] = userAnswers.get(SettlorIndividualNINOPage(index)) map {
    x =>
      AnswerRow(
        "settlorIndividualNINO.checkYourAnswersLabel",
        HtmlFormat.escape(formatNino(x)),
        Some(routes.SettlorIndividualNINOController.onPageLoad(NormalMode, index, draftId).url),
        livingSettlorName(index, userAnswers),
        canEdit = canEdit
      )
  }

  def settlorIndividualAddressYesNo(index: Int): Option[AnswerRow] = userAnswers.get(SettlorAddressYesNoPage(index)) map {
    x =>
      AnswerRow(
        "settlorIndividualAddressYesNo.checkYourAnswersLabel",
        yesOrNo(x),
        Some(routes.SettlorIndividualAddressYesNoController.onPageLoad(NormalMode, index, draftId).url),
        livingSettlorName(index, userAnswers),
        canEdit = canEdit
      )
  }

  def settlorIndividualDateOfBirth(index: Int): Option[AnswerRow] = userAnswers.get(SettlorIndividualDateOfBirthPage(index)) map {
    x =>
      AnswerRow(
        "settlorIndividualDateOfBirth.checkYourAnswersLabel",
        HtmlFormat.escape(x.format(dateFormatter)),
        Some(routes.SettlorIndividualDateOfBirthController.onPageLoad(NormalMode, index, draftId).url),
        livingSettlorName(index, userAnswers),
        canEdit = canEdit
      )
  }

  def settlorIndividualDateOfBirthYesNo(index: Int): Option[AnswerRow] = userAnswers.get(SettlorIndividualDateOfBirthYesNoPage(index)) map {
    x =>
      AnswerRow(
        "settlorIndividualDateOfBirthYesNo.checkYourAnswersLabel",
        yesOrNo(x),
        Some(routes.SettlorIndividualDateOfBirthYesNoController.onPageLoad(NormalMode, index, draftId).url),
        livingSettlorName(index, userAnswers),
        canEdit = canEdit
      )
  }

  def settlorIndividualName(index: Int): Option[AnswerRow] = userAnswers.get(SettlorIndividualNamePage(index)) map {
    x =>
      AnswerRow(
        "settlorIndividualName.checkYourAnswersLabel",
        HtmlFormat.escape(s"${x.firstName} ${x.middleName.getOrElse("")} ${x.lastName}"),
        Some(routes.SettlorIndividualNameController.onPageLoad(NormalMode, index, draftId).url),
        canEdit = canEdit
      )
  }

  def settlorIndividualOrBusiness(index: Int): Option[AnswerRow] = userAnswers.get(SettlorIndividualOrBusinessPage(index)) map {
    x =>
      AnswerRow(
        "settlorIndividualOrBusiness.checkYourAnswersLabel",
        HtmlFormat.escape(messages(s"settlorIndividualOrBusiness.$x")),
        Some(routes.SettlorIndividualOrBusinessController.onPageLoad(NormalMode, index, draftId).url),
        canEdit = canEdit
      )
  }

  def trustDetails: Option[Seq[AnswerSection]] = {
    val questions = Seq(
      trustName,
      whenTrustSetup,
      governedInsideTheUK,
      countryGoverningTrust,
      administrationInsideUK,
      countryAdministeringTrust,
      trusteesBasedInUK,
      settlorsBasedInTheUK,
      establishedUnderScotsLaw,
      trustResidentOffshore,
      trustPreviouslyResident,
      registeringTrustFor5A,
      nonresidentType,
      inheritanceTaxAct,
      agentOtherThanBarrister
    ).flatten

    if (questions.nonEmpty) Some(Seq(AnswerSection(None, questions, Some(messages("answerPage.section.trustsDetails.heading"))))) else None
  }

  def deceasedSettlor: Option[Seq[AnswerSection]] = {

    val questions = Seq(
      setUpAfterSettlorDied,
      deceasedSettlorsName,
      deceasedSettlorDateOfDeathYesNo,
      deceasedSettlorDateOfDeath,
      deceasedSettlorDateOfBirthYesNo,
      deceasedSettlorsDateOfBirth,
      deceasedSettlorsNINoYesNo,
      deceasedSettlorNationalInsuranceNumber,
      deceasedSettlorsLastKnownAddressYesNo,
      wasSettlorsAddressUKYesNo,
      deceasedSettlorsUKAddress,
      deceasedSettlorsInternationalAddress
    ).flatten

    if (deceasedSettlorsName.nonEmpty)
      Some(Seq(AnswerSection(
        headingKey = None,
        questions,
        sectionKey = Some(messages("answerPage.section.deceasedSettlor.heading"))
      )))
    else None
  }

  def livingSettlors: Option[Seq[AnswerSection]] = {

    for {
      livingSettlors <- userAnswers.get(LivingSettlors)
      indexed = livingSettlors.zipWithIndex
    } yield indexed.map {
      case (_, index) =>

        val questions = Seq(
          setUpAfterSettlorDied,
          kindOfTrust,
          holdoverReliefYesNo,
          settlorIndividualOrBusiness(index),
          settlorIndividualName(index),
          settlorIndividualDateOfBirthYesNo(index),
          settlorIndividualDateOfBirth(index),
          settlorIndividualNINOYesNo(index),
          settlorIndividualNINO(index),
          settlorIndividualAddressYesNo(index),
          settlorIndividualAddressUKYesNo(index),
          settlorIndividualAddressUK(index),
          settlorIndividualAddressInternational(index),
          settlorIndividualPassportYesNo(index),
          settlorIndividualPassport(index),
          settlorIndividualIDCardYesNo(index),
          settlorIndividualIDCard(index)
        ).flatten

        val sectionKey = if (index == 0) Some(messages("answerPage.section.settlors.heading")) else None

        AnswerSection(
          headingKey = Some(messages("answerPage.section.settlor.subheading", index + 1)),
          questions,
          sectionKey = sectionKey
        )
    }
  }

  def trustees: Option[Seq[AnswerSection]] = {
    for {
      trustees <- userAnswers.get(Trustees)
      indexed = trustees.zipWithIndex
    } yield indexed.map {
      case (trustee, index) =>

        val trusteeIndividualOrBusinessMessagePrefix = if (trustee.isLead) "leadTrusteeIndividualOrBusiness" else "trusteeIndividualOrBusiness"
        val trusteeFullNameMessagePrefix = if (trustee.isLead) "leadTrusteesName" else "trusteesName"
        val questions = trustee match {
          case _: TrusteeIndividual | _: LeadTrusteeIndividual =>
            Seq(
              trusteeIndividualOrBusiness(index, trusteeIndividualOrBusinessMessagePrefix),
              trusteeFullName(index, trusteeFullNameMessagePrefix),
              trusteesDateOfBirth(index),
              trusteeAUKCitizen(index),
              trusteesNino(index),
              trusteeLiveInTheUK(index),
              trusteesUkAddress(index),
              //TODO - international address, passport/ID card details etc.
              telephoneNumber(index)
            ).flatten
          case _: TrusteeOrganisation | _: LeadTrusteeOrganisation =>
            Seq(
              trusteeIndividualOrBusiness(index, trusteeIndividualOrBusinessMessagePrefix),
              trusteeUtrYesNo(index),
              trusteeOrgName(index),
              trusteeUtr(index),
              orgAddressInTheUkYesNo(index),
              trusteesOrgUkAddress(index),
              trusteeOrgInternationalAddress(index),
              orgTelephoneNumber(index)
            ).flatten
        }


        val sectionKey = if (index == 0) Some(messages("answerPage.section.trustees.heading")) else None

        AnswerSection(
          headingKey = Some(Messages("answerPage.section.trustee.subheading") + " " + (index + 1)),
          rows = questions,
          sectionKey = sectionKey
        )
    }
  }


  def individualBeneficiaries: Option[Seq[AnswerSection]] = {
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

  def classOfBeneficiaries(individualBeneficiariesExist: Boolean): Option[Seq[AnswerSection]] = {
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

  def money: Seq[AnswerSection] = {
    val answers = userAnswers.get(Assets).getOrElse(Nil).zipWithIndex.collect {
      case (x: MoneyAsset, index) => (x, index)
    }

    answers.flatMap {
      case o@(m, index) =>
        Seq(
          AnswerSection(
            Some(messages("answerPage.section.moneyAsset.subheading")),
            Seq(
              assetMoneyValue(index)
            ).flatten,
            None
          )
        )
    }
  }

  def shares: Seq[AnswerSection] = {
    val answers: Seq[(ShareAsset, Int)] = userAnswers.get(Assets).getOrElse(Nil).zipWithIndex.collect {
      case (x: ShareNonPortfolioAsset, index) => (x, index)
      case (x: SharePortfolioAsset, index) => (x, index)
    }

    answers.flatMap {
      case o@(m, index) =>
        m match {
          case _: ShareNonPortfolioAsset =>
            Seq(
              AnswerSection(
                Some(s"${messages("answerPage.section.shareAsset.subheading")} ${answers.indexOf(o) + 1}"),
                Seq(
                  sharesInAPortfolio(index),
                  shareCompanyName(index),
                  sharesOnStockExchange(index),
                  shareClass(index),
                  shareQuantityInTrust(index),
                  shareValueInTrust(index)
                ).flatten,
                None
              )
            )
          case _: SharePortfolioAsset =>
            Seq(
              AnswerSection(
                Some(s"${messages("answerPage.section.shareAsset.subheading")} ${answers.indexOf(o) + 1}"),
                Seq(
                  sharesInAPortfolio(index),
                  sharePortfolioName(index),
                  sharePortfolioOnStockExchange(index),
                  sharePortfolioQuantityInTrust(index),
                  sharePortfolioValueInTrust(index)
                ).flatten,
                None
              )
            )
          case _ => Nil
        }
    }

  }

  def propertyOrLand: Seq[AnswerSection] = {
    val answers: Seq[(PropertyOrLandAsset, Int)] = userAnswers.get(Assets).getOrElse(Nil).zipWithIndex.collect {
      case (x: PropertyOrLandAsset, index) => (x, index)
    }

    answers.flatMap {
      case o@(m, index) =>
        Seq(
          AnswerSection(
            Some(s"${messages("answerPage.section.propertyOrLandAsset.subheading")} ${answers.indexOf(o) + 1}"),
            Seq(
              propertyOrLandAddressYesNo(index),
              propertyOrLandDescription(index),
              propertyOrLandAddressUkYesNo(index),
              propertyOrLandUKAddress(index),
              propertyOrLandInternationalAddress(index),
              propertyOrLandTotalValue(index),
              trustOwnAllThePropertyOrLand(index),
              propertyLandValueTrust(index)
            ).flatten,
            None
          )
        )
      case _ => Nil
    }

  }

  def propertyOrLandAddressYesNo(index: Int): Option[AnswerRow] = userAnswers.get(PropertyOrLandAddressYesNoPage(index)) map {
    x =>
      AnswerRow(
        "propertyOrLandAddressYesNo.checkYourAnswersLabel",
        yesOrNo(x),
        Some(controllers.register.asset.property_or_land.routes.PropertyOrLandAddressYesNoController.onPageLoad(NormalMode, index, draftId).url),
        canEdit = canEdit
      )
  }

  def propertyLandValueTrust(index: Int): Option[AnswerRow] = userAnswers.get(PropertyLandValueTrustPage(index)) map {
    x =>
      AnswerRow(
        "propertyLandValueTrust.checkYourAnswersLabel",
        currency(x),
        Some(controllers.register.asset.property_or_land.routes.PropertyLandValueTrustController.onPageLoad(NormalMode, index, draftId).url),
        canEdit = canEdit
      )
  }

  def propertyOrLandDescription(index: Int): Option[AnswerRow] = userAnswers.get(PropertyOrLandDescriptionPage(index)) map {
    x =>
      AnswerRow(
        "propertyOrLandDescription.checkYourAnswersLabel",
        HtmlFormat.escape(x),
        Some(controllers.register.asset.property_or_land.routes.PropertyOrLandDescriptionController.onPageLoad(NormalMode, index, draftId).url),
        canEdit = canEdit
      )
  }

  def propertyOrLandAddressUkYesNo(index: Int): Option[AnswerRow] = userAnswers.get(PropertyOrLandAddressUkYesNoPage(index)) map {
    x =>
      AnswerRow(
        "propertyOrLandAddressUkYesNo.checkYourAnswersLabel",
        yesOrNo(x),
        Some(controllers.register.asset.property_or_land.routes.PropertyOrLandAddressUkYesNoController.onPageLoad(NormalMode, index, draftId).url),
        canEdit = canEdit
      )
  }

  def trustOwnAllThePropertyOrLand(index: Int): Option[AnswerRow] = userAnswers.get(TrustOwnAllThePropertyOrLandPage(index)) map {
    x =>
      AnswerRow(
        "trustOwnAllThePropertyOrLand.checkYourAnswersLabel",
        yesOrNo(x),
        Some(controllers.register.asset.property_or_land.routes.TrustOwnAllThePropertyOrLandController.onPageLoad(NormalMode, index, draftId).url),
        canEdit = canEdit
      )
  }

  def propertyOrLandUKAddress(index: Int): Option[AnswerRow] = userAnswers.get(PropertyOrLandUKAddressPage(index)) map {
    x =>
      AnswerRow(
        "propertyOrLandUKAddress.checkYourAnswersLabel",
        ukAddress(x),
        Some(controllers.register.asset.property_or_land.routes.PropertyOrLandUKAddressController.onPageLoad(NormalMode, index, draftId).url),
        canEdit = canEdit
      )
  }

  def propertyOrLandInternationalAddress(index: Int): Option[AnswerRow] = userAnswers.get(PropertyOrLandInternationalAddressPage(index)) map {
    x =>
      AnswerRow(
        "propertyOrLandInternationalAddress.checkYourAnswersLabel",
        internationalAddress(x, countryOptions),
        Some(controllers.register.asset.property_or_land.routes.PropertyOrLandInternationalAddressController.onPageLoad(NormalMode, index, draftId).url),
        canEdit = canEdit
      )
  }

  def propertyOrLandTotalValue(index: Int): Option[AnswerRow] = userAnswers.get(PropertyOrLandTotalValuePage(index)) map {
    x =>
      AnswerRow(
        "propertyOrLandTotalValue.checkYourAnswersLabel",
        currency(x),
        Some(controllers.register.asset.property_or_land.routes.PropertyOrLandTotalValueController.onPageLoad(NormalMode, index, draftId).url),
        canEdit = canEdit
      )
  }

  def agentInternationalAddress: Option[AnswerRow] = userAnswers.get(AgentInternationalAddressPage) map {
    x =>
      AnswerRow(
        "site.address.international.checkYourAnswersLabel",
        internationalAddress(x, countryOptions),
        Some(controllers.register.agents.routes.AgentInternationalAddressController.onPageLoad(NormalMode, draftId).url),
        agencyName(userAnswers),
        canEdit = canEdit
      )
  }

  def classBeneficiaryDescription(index: Int): Option[AnswerRow] = userAnswers.get(ClassBeneficiaryDescriptionPage(index)) map {
    x =>
      AnswerRow(
        "classBeneficiaryDescription.checkYourAnswersLabel",
        HtmlFormat.escape(x),
        Some(controllers.register.beneficiaries.routes.ClassBeneficiaryDescriptionController.onPageLoad(NormalMode, index, draftId).url),
        canEdit = canEdit
      )
  }

  def shareCompanyName(index: Int): Option[AnswerRow] = userAnswers.get(ShareCompanyNamePage(index)) map {
    x =>
      AnswerRow(
        "shareCompanyName.checkYourAnswersLabel",
        HtmlFormat.escape(x),
        Some(controllers.register.asset.shares.routes.ShareCompanyNameController.onPageLoad(NormalMode, index, draftId).url),
        canEdit = canEdit
      )
  }

  def sharesOnStockExchange(index: Int): Option[AnswerRow] = userAnswers.get(SharesOnStockExchangePage(index)) map {
    x =>
      AnswerRow(
        "sharesOnStockExchange.checkYourAnswersLabel",
        yesOrNo(x),
        Some(controllers.register.asset.shares.routes.SharesOnStockExchangeController.onPageLoad(NormalMode, index, draftId).url),
        shareCompName(index, userAnswers),
        canEdit = canEdit
      )
  }

  def sharesInAPortfolio(index: Int): Option[AnswerRow] = userAnswers.get(SharesInAPortfolioPage(index)) map {
    x =>
      AnswerRow(
        "sharesInAPortfolio.checkYourAnswersLabel",
        yesOrNo(x),
        Some(controllers.register.asset.shares.routes.SharesInAPortfolioController.onPageLoad(NormalMode, index, draftId).url),
        canEdit = canEdit
      )
  }

  def shareValueInTrust(index: Int): Option[AnswerRow] = userAnswers.get(ShareValueInTrustPage(index)) map {
    x =>
      AnswerRow(
        "shareValueInTrust.checkYourAnswersLabel",
        currency(x),
        Some(controllers.register.asset.shares.routes.ShareValueInTrustController.onPageLoad(NormalMode, index, draftId).url),
        shareCompName(index, userAnswers),
        canEdit = canEdit
      )
  }

  def shareQuantityInTrust(index: Int): Option[AnswerRow] = userAnswers.get(ShareQuantityInTrustPage(index)) map {
    x =>
      AnswerRow(
        "shareQuantityInTrust.checkYourAnswersLabel",
        HtmlFormat.escape(x),
        Some(controllers.register.asset.shares.routes.ShareQuantityInTrustController.onPageLoad(NormalMode, index, draftId).url),
        shareCompName(index, userAnswers),
        canEdit = canEdit
      )
  }

  def sharePortfolioValueInTrust(index: Int): Option[AnswerRow] = userAnswers.get(SharePortfolioValueInTrustPage(index)) map {
    x =>
      AnswerRow(
        "sharePortfolioValueInTrust.checkYourAnswersLabel",
        currency(x),
        Some(controllers.register.asset.shares.routes.SharePortfolioValueInTrustController.onPageLoad(NormalMode, index, draftId).url),
        canEdit = canEdit
      )
  }

  def sharePortfolioQuantityInTrust(index: Int): Option[AnswerRow] = userAnswers.get(SharePortfolioQuantityInTrustPage(index)) map {
    x =>
      AnswerRow(
        "sharePortfolioQuantityInTrust.checkYourAnswersLabel",
        HtmlFormat.escape(x),
        Some(controllers.register.asset.shares.routes.SharePortfolioQuantityInTrustController.onPageLoad(NormalMode, index, draftId).url),
        canEdit = canEdit
      )
  }

  def sharePortfolioOnStockExchange(index: Int): Option[AnswerRow] = userAnswers.get(SharePortfolioOnStockExchangePage(index)) map {
    x =>
      AnswerRow(
        "sharePortfolioOnStockExchange.checkYourAnswersLabel",
        yesOrNo(x),
        Some(controllers.register.asset.shares.routes.SharePortfolioOnStockExchangeController.onPageLoad(NormalMode, index, draftId).url),
        canEdit = canEdit
      )
  }

  def sharePortfolioName(index: Int): Option[AnswerRow] = userAnswers.get(SharePortfolioNamePage(index)) map {
    x =>
      AnswerRow(
        "sharePortfolioName.checkYourAnswersLabel",
        HtmlFormat.escape(x),
        Some(controllers.register.asset.shares.routes.SharePortfolioNameController.onPageLoad(NormalMode, index, draftId).url),
        canEdit = canEdit
      )
  }

  def shareClass(index: Int): Option[AnswerRow] = userAnswers.get(ShareClassPage(index)) map {
    x =>
      AnswerRow(
        "shareClass.checkYourAnswersLabel",
        HtmlFormat.escape(messages(s"shareClass.$x")),
        Some(controllers.register.asset.shares.routes.ShareClassController.onPageLoad(NormalMode, index, draftId).url),
        canEdit = canEdit
      )
  }

  def agentUKAddress: Option[AnswerRow] = userAnswers.get(AgentUKAddressPage) map {
    x =>
      AnswerRow(
        "site.address.uk.checkYourAnswersLabel",
        ukAddress(x),
        Some(controllers.register.agents.routes.AgentUKAddressController.onPageLoad(NormalMode, draftId).url),
        agencyName(userAnswers),
        canEdit = canEdit
      )
  }

  def agentAddressYesNo: Option[AnswerRow] = userAnswers.get(AgentAddressYesNoPage) map {
    x =>
      AnswerRow(
        "agentAddressYesNo.checkYourAnswersLabel",
        yesOrNo(x),
        Some(controllers.register.agents.routes.AgentAddressYesNoController.onPageLoad(NormalMode, draftId).url),
        agencyName(userAnswers),
        canEdit = canEdit
      )
  }

  def individualBeneficiaryAddressUKYesNo(index: Int): Option[AnswerRow] = userAnswers.get(IndividualBeneficiaryAddressUKYesNoPage(index)) map {
    x =>
      AnswerRow(
        "individualBeneficiaryAddressUKYesNo.checkYourAnswersLabel",
        yesOrNo(x),
        Some(controllers.register.beneficiaries.routes.IndividualBeneficiaryAddressUKYesNoController.onPageLoad(NormalMode, index, draftId).url),
        indBeneficiaryName(index, userAnswers),
        canEdit = canEdit
      )
  }

  def agentName: Option[AnswerRow] = userAnswers.get(AgentNamePage) map {
    x =>
      AnswerRow(
        "agentName.checkYourAnswersLabel",
        HtmlFormat.escape(x),
        Some(controllers.register.agents.routes.AgentNameController.onPageLoad(NormalMode, draftId).url),
        canEdit = canEdit
      )
  }

  def addABeneficiary: Option[AnswerRow] = userAnswers.get(AddABeneficiaryPage) map {
    x =>
      AnswerRow(
        "addABeneficiary.checkYourAnswersLabel",
        HtmlFormat.escape(messages(s"addABeneficiary.$x")),
        Some(controllers.register.beneficiaries.routes.AddABeneficiaryController.onPageLoad(draftId).url),
        canEdit = canEdit
      )
  }

  def individualBeneficiaryVulnerableYesNo(index: Int): Option[AnswerRow] = userAnswers.get(IndividualBeneficiaryVulnerableYesNoPage(index)) map {
    x =>
      AnswerRow(
        "individualBeneficiaryVulnerableYesNo.checkYourAnswersLabel",
        yesOrNo(x),
        Some(controllers.register.beneficiaries.routes.IndividualBeneficiaryVulnerableYesNoController.onPageLoad(NormalMode, index, draftId).url),
        indBeneficiaryName(index, userAnswers),
        canEdit = canEdit
      )
  }

  def individualBeneficiaryAddressUK(index: Int): Option[AnswerRow] = userAnswers.get(IndividualBeneficiaryAddressUKPage(index)) map {
    x =>
      AnswerRow(
        "individualBeneficiaryAddressUK.checkYourAnswersLabel",
        ukAddress(x),
        Some(controllers.register.beneficiaries.routes.IndividualBeneficiaryAddressUKController.onPageLoad(NormalMode, index, draftId).url),
        indBeneficiaryName(index, userAnswers),
        canEdit = canEdit
      )
  }

  def individualBeneficiaryAddressYesNo(index: Int): Option[AnswerRow] = userAnswers.get(IndividualBeneficiaryAddressYesNoPage(index)) map {
    x =>
      AnswerRow(
        "individualBeneficiaryAddressYesNo.checkYourAnswersLabel",
        yesOrNo(x),
        Some(controllers.register.beneficiaries.routes.IndividualBeneficiaryAddressYesNoController.onPageLoad(NormalMode, index, draftId).url),
        indBeneficiaryName(index, userAnswers),
        canEdit = canEdit
      )
  }

  def individualBeneficiaryNationalInsuranceNumber(index: Int): Option[AnswerRow] = userAnswers.get(IndividualBeneficiaryNationalInsuranceNumberPage(index)) map {
    x =>
      AnswerRow(
        "individualBeneficiaryNationalInsuranceNumber.checkYourAnswersLabel",
        HtmlFormat.escape(formatNino(x)),
        Some(controllers.register.beneficiaries.routes.IndividualBeneficiaryNationalInsuranceNumberController.onPageLoad(NormalMode, index, draftId).url),
        indBeneficiaryName(index, userAnswers),
        canEdit = canEdit
      )
  }

  def individualBeneficiaryNationalInsuranceYesNo(index: Int): Option[AnswerRow] = userAnswers.get(IndividualBeneficiaryNationalInsuranceYesNoPage(index)) map {
    x =>
      AnswerRow(
        "individualBeneficiaryNationalInsuranceYesNo.checkYourAnswersLabel",
        yesOrNo(x),
        Some(controllers.register.beneficiaries.routes.IndividualBeneficiaryNationalInsuranceYesNoController.onPageLoad(NormalMode, index, draftId).url),
        indBeneficiaryName(index, userAnswers),
        canEdit = canEdit
      )
  }

  def individualBeneficiaryIncome(index: Int): Option[AnswerRow] = userAnswers.get(IndividualBeneficiaryIncomePage(index)) map {
    x =>
      AnswerRow(
        "individualBeneficiaryIncome.checkYourAnswersLabel",
        HtmlFormat.escape(x),
        Some(controllers.register.beneficiaries.routes.IndividualBeneficiaryIncomeController.onPageLoad(NormalMode, index, draftId).url),
        indBeneficiaryName(index, userAnswers),
        canEdit = canEdit
      )
  }

  def individualBeneficiaryIncomeYesNo(index: Int): Option[AnswerRow] = userAnswers.get(IndividualBeneficiaryIncomeYesNoPage(index)) map {
    x =>
      AnswerRow(
        "individualBeneficiaryIncomeYesNo.checkYourAnswersLabel",
        yesOrNo(x),
        Some(controllers.register.beneficiaries.routes.IndividualBeneficiaryIncomeYesNoController.onPageLoad(NormalMode, index, draftId).url),
        indBeneficiaryName(index, userAnswers),
        canEdit = canEdit
      )
  }

  def individualBeneficiaryDateOfBirth(index: Int): Option[AnswerRow] = userAnswers.get(IndividualBeneficiaryDateOfBirthPage(index)) map {
    x =>
      AnswerRow(
        "individualBeneficiaryDateOfBirth.checkYourAnswersLabel",
        HtmlFormat.escape(x.format(dateFormatter)),
        Some(controllers.register.beneficiaries.routes.IndividualBeneficiaryDateOfBirthController.onPageLoad(NormalMode, index, draftId).url),
        indBeneficiaryName(index, userAnswers),
        canEdit = canEdit
      )
  }

  def individualBeneficiaryDateOfBirthYesNo(index: Int): Option[AnswerRow] = userAnswers.get(IndividualBeneficiaryDateOfBirthYesNoPage(index)) map {
    x =>
      AnswerRow(
        "individualBeneficiaryDateOfBirthYesNo.checkYourAnswersLabel",
        yesOrNo(x),
        Some(controllers.register.beneficiaries.routes.IndividualBeneficiaryDateOfBirthYesNoController.onPageLoad(NormalMode, index, draftId).url),
        indBeneficiaryName(index, userAnswers),
        canEdit = canEdit
      )
  }

  def individualBeneficiaryName(index: Int): Option[AnswerRow] = userAnswers.get(IndividualBeneficiaryNamePage(index)) map {
    x =>
      AnswerRow(
        "individualBeneficiaryName.checkYourAnswersLabel",
        HtmlFormat.escape(s"${x.firstName} ${x.middleName.getOrElse("")} ${x.lastName}"),
        Some(controllers.register.beneficiaries.routes.IndividualBeneficiaryNameController.onPageLoad(NormalMode, index, draftId).url),
        canEdit = canEdit
      )
  }

  def wasSettlorsAddressUKYesNo: Option[AnswerRow] = userAnswers.get(WasSettlorsAddressUKYesNoPage) map {
    x =>
      AnswerRow(
        "wasSettlorsAddressUKYesNo.checkYourAnswersLabel",
        yesOrNo(x),
        Some(controllers.register.settlors.deceased_settlor.routes.WasSettlorsAddressUKYesNoController.onPageLoad(NormalMode, draftId).url),
        deceasedSettlorName(userAnswers),
        canEdit = canEdit
      )
  }

  def setUpAfterSettlorDied: Option[AnswerRow] = userAnswers.get(SetUpAfterSettlorDiedYesNoPage) map {
    x =>
      AnswerRow(
        "setUpAfterSettlorDied.checkYourAnswersLabel",
        yesOrNo(x),
        Some(controllers.register.settlors.routes.SetUpAfterSettlorDiedController.onPageLoad(NormalMode, draftId).url),
        canEdit = canEdit
      )
  }

  def setUpInAddition: Option[AnswerRow] = userAnswers.get(SetUpInAdditionToWillTrustYesNoPage) map {
    x =>
      AnswerRow(
        "setUpInAdditionToWillTrustYesNo.checkYourAnswersLabel",
        yesOrNo(x),
        Some(controllers.register.settlors.routes.AdditionToWillTrustYesNoController.onPageLoad(NormalMode, draftId).url),
        canEdit = canEdit
      )
  }

  def deceasedSettlorsUKAddress: Option[AnswerRow] = userAnswers.get(SettlorsUKAddressPage) map {
    x =>
      AnswerRow(
        "settlorsUKAddress.checkYourAnswersLabel",
        ukAddress(x),
        Some(controllers.register.settlors.deceased_settlor.routes.SettlorsUKAddressController.onPageLoad(NormalMode, draftId).url),
        deceasedSettlorName(userAnswers),
        canEdit = canEdit
      )
  }

  def deceasedSettlorsNINoYesNo: Option[AnswerRow] = userAnswers.get(SettlorsNationalInsuranceYesNoPage) map {
    x =>
      AnswerRow(
        "settlorsNationalInsuranceYesNo.checkYourAnswersLabel",
        yesOrNo(x),
        Some(controllers.register.settlors.deceased_settlor.routes.SettlorsNINoYesNoController.onPageLoad(NormalMode, draftId).url),
        deceasedSettlorName(userAnswers),
        canEdit = canEdit
      )
  }

  def deceasedSettlorsName: Option[AnswerRow] = userAnswers.get(SettlorsNamePage) map {
    x =>
      AnswerRow(
        "settlorsName.checkYourAnswersLabel",
        HtmlFormat.escape(s"${x.firstName} ${x.middleName.getOrElse("")} ${x.lastName}"),
        Some(controllers.register.settlors.deceased_settlor.routes.SettlorsNameController.onPageLoad(NormalMode, draftId).url),
        canEdit = canEdit
      )
  }

  def deceasedSettlorsLastKnownAddressYesNo: Option[AnswerRow] = userAnswers.get(SettlorsLastKnownAddressYesNoPage) map {
    x =>
      AnswerRow(
        "settlorsLastKnownAddressYesNo.checkYourAnswersLabel",
        yesOrNo(x),
        Some(controllers.register.settlors.deceased_settlor.routes.SettlorsLastKnownAddressYesNoController.onPageLoad(NormalMode, draftId).url),
        deceasedSettlorName(userAnswers),
        canEdit = canEdit
      )
  }

  def deceasedSettlorsInternationalAddress: Option[AnswerRow] = userAnswers.get(SettlorsInternationalAddressPage) map {
    x =>
      AnswerRow(
        "settlorsInternationalAddress.checkYourAnswersLabel",
        internationalAddress(x, countryOptions),
        Some(controllers.register.settlors.deceased_settlor.routes.SettlorsInternationalAddressController.onPageLoad(NormalMode, draftId).url),
        deceasedSettlorName(userAnswers),
        canEdit = canEdit
      )
  }

  def deceasedSettlorsDateOfBirth: Option[AnswerRow] = userAnswers.get(SettlorsDateOfBirthPage) map {
    x =>
      AnswerRow(
        "settlorsDateOfBirth.checkYourAnswersLabel",
        HtmlFormat.escape(x.format(dateFormatter)),
        Some(controllers.register.settlors.deceased_settlor.routes.SettlorsDateOfBirthController.onPageLoad(NormalMode, draftId).url),
        deceasedSettlorName(userAnswers),
        canEdit = canEdit
      )
  }

  def deceasedSettlorNationalInsuranceNumber: Option[AnswerRow] = userAnswers.get(SettlorNationalInsuranceNumberPage) map {
    x =>
      AnswerRow(
        "settlorNationalInsuranceNumber.checkYourAnswersLabel",
        HtmlFormat.escape(formatNino(x)),
        Some(controllers.register.settlors.deceased_settlor.routes.SettlorNationalInsuranceNumberController.onPageLoad(NormalMode, draftId).url),
        deceasedSettlorName(userAnswers),
        canEdit = canEdit
      )
  }

  def deceasedSettlorDateOfDeathYesNo: Option[AnswerRow] = userAnswers.get(SettlorDateOfDeathYesNoPage) map {
    x =>
      AnswerRow(
        "settlorDateOfDeathYesNo.checkYourAnswersLabel",
        yesOrNo(x),
        Some(controllers.register.settlors.deceased_settlor.routes.SettlorDateOfDeathYesNoController.onPageLoad(NormalMode, draftId).url),
        deceasedSettlorName(userAnswers),
        canEdit = canEdit
      )
  }

  def deceasedSettlorDateOfDeath: Option[AnswerRow] = userAnswers.get(SettlorDateOfDeathPage) map {
    x =>
      AnswerRow(
        "settlorDateOfDeath.checkYourAnswersLabel",
        HtmlFormat.escape(x.format(dateFormatter)),
        Some(controllers.register.settlors.deceased_settlor.routes.SettlorDateOfDeathController.onPageLoad(NormalMode, draftId).url),
        deceasedSettlorName(userAnswers),
        canEdit = canEdit
      )
  }

  def deceasedSettlorDateOfBirthYesNo: Option[AnswerRow] = userAnswers.get(SettlorDateOfBirthYesNoPage) map {
    x =>
      AnswerRow(
        "settlorDateOfBirthYesNo.checkYourAnswersLabel",
        yesOrNo(x),
        Some(controllers.register.settlors.deceased_settlor.routes.SettlorDateOfBirthYesNoController.onPageLoad(NormalMode, draftId).url),
        deceasedSettlorName(userAnswers),
        canEdit = canEdit
      )
  }

  def assetMoneyValue(index: Int): Option[AnswerRow] = userAnswers.get(AssetMoneyValuePage(index)) map {
    x =>
      AnswerRow(
        "assetMoneyValue.checkYourAnswersLabel",
        currency(x),
        Some(controllers.register.asset.money.routes.AssetMoneyValueController.onPageLoad(NormalMode, index, draftId).url),
        canEdit = canEdit
      )
  }

  def whatKindOfAsset(index: Int): Option[AnswerRow] = userAnswers.get(WhatKindOfAssetPage(index)) map {
    x =>
      AnswerRow(
        "whatKindOfAsset.checkYourAnswersLabel",
        HtmlFormat.escape(messages(s"whatKindOfAsset.$x")),
        Some(controllers.register.asset.routes.WhatKindOfAssetController.onPageLoad(NormalMode, index, draftId).url),
        canEdit = canEdit
      )
  }

  def agentInternalReference: Option[AnswerRow] = userAnswers.get(AgentInternalReferencePage) map {
    x =>
      AnswerRow(
        "agentInternalReference.checkYourAnswersLabel",
        HtmlFormat.escape(x),
        Some(controllers.register.agents.routes.AgentInternalReferenceController.onPageLoad(NormalMode, draftId).url),
        canEdit = canEdit
      )
  }

  def agenciesTelephoneNumber: Option[AnswerRow] = userAnswers.get(AgentTelephoneNumberPage) map {
    x =>
      AnswerRow(
        "agentTelephoneNumber.checkYourAnswersLabel",
        HtmlFormat.escape(x),
        Some(controllers.register.agents.routes.AgentTelephoneNumberController.onPageLoad(NormalMode, draftId).url),
        agencyName(userAnswers),
        canEdit = canEdit
      )
  }

  def trusteesNino(index: Int): Option[AnswerRow] = userAnswers.get(TrusteesNinoPage(index)) map {
    x =>
      AnswerRow(
        "trusteesNino.checkYourAnswersLabel",
        HtmlFormat.escape(formatNino(x)),
        Some(controllers.register.trustees.individual.routes.TrusteesNinoController.onPageLoad(NormalMode, index, draftId).url),
        trusteeName(index, userAnswers),
        canEdit = canEdit
      )
  }

  def trusteeLiveInTheUK(index: Int): Option[AnswerRow] = userAnswers.get(TrusteeAddressInTheUKPage(index)) map {
    x =>
      AnswerRow(
        "trusteeLiveInTheUK.checkYourAnswersLabel",
        yesOrNo(x),
        Some(controllers.register.trustees.individual.routes.TrusteeLiveInTheUKController.onPageLoad(NormalMode, index, draftId).url),
        trusteeName(index, userAnswers),
        canEdit = canEdit
      )
  }

  def trusteesUkAddress(index: Int): Option[AnswerRow] = userAnswers.get(TrusteesUkAddressPage(index)) map {
    x =>
      AnswerRow(
        "trusteesUkAddress.checkYourAnswersLabel",
        ukAddress(x),
        Some(controllers.register.trustees.individual.routes.TrusteesUkAddressController.onPageLoad(NormalMode, index, draftId).url),
        trusteeName(index, userAnswers),
        canEdit = canEdit
      )
  }

  def trusteesDateOfBirth(index: Int): Option[AnswerRow] = userAnswers.get(TrusteesDateOfBirthPage(index)) map {
    x =>
      AnswerRow(
        "trusteesDateOfBirth.checkYourAnswersLabel",
        HtmlFormat.escape(x.format(dateFormatter)),
        Some(controllers.register.trustees.individual.routes.TrusteesDateOfBirthController.onPageLoad(NormalMode, index, draftId).url),
        trusteeName(index, userAnswers),
        canEdit = canEdit
      )
  }

  def telephoneNumber(index: Int): Option[AnswerRow] = userAnswers.get(TelephoneNumberPage(index)) map {
    x =>
      AnswerRow(
        "telephoneNumber.checkYourAnswersLabel",
        HtmlFormat.escape(x),
        Some(controllers.register.trustees.individual.routes.TelephoneNumberController.onPageLoad(NormalMode, index, draftId).url),
        trusteeName(index, userAnswers),
        canEdit = canEdit
      )
  }

  def orgTelephoneNumber(index: Int): Option[AnswerRow] = userAnswers.get(TelephoneNumberPage(index)) map {
    x =>
      AnswerRow(
        "telephoneNumber.checkYourAnswersLabel",
        HtmlFormat.escape(x),
        Some(controllers.register.trustees.organisation.routes.TrusteeOrgTelephoneNumberController.onPageLoad(NormalMode, index, draftId).url),
        orgName(index, userAnswers),
        canEdit = canEdit
      )
  }

  def trusteeAUKCitizen(index: Int): Option[AnswerRow] = userAnswers.get(TrusteeAUKCitizenPage(index)) map {
    x =>
      AnswerRow(
        "trusteeAUKCitizen.checkYourAnswersLabel",
        yesOrNo(x),
        Some(controllers.register.trustees.individual.routes.TrusteeAUKCitizenController.onPageLoad(NormalMode, index, draftId).url),
        trusteeName(index, userAnswers),
        canEdit = canEdit
      )
  }


  def trusteeFullName(index: Int, messagePrefix: String): Option[AnswerRow] = userAnswers.get(TrusteesNamePage(index)) map {
    x =>
      AnswerRow(
        s"$messagePrefix.checkYourAnswersLabel",
        HtmlFormat.escape(s"${x.firstName} ${x.middleName.getOrElse("")} ${x.lastName}"),
        Some(controllers.register.trustees.individual.routes.TrusteesNameController.onPageLoad(NormalMode, index, draftId).url),
        canEdit = canEdit
      )
  }

  def trusteeOrgName(index: Int): Option[AnswerRow] = userAnswers.get(TrusteeOrgNamePage(index)) map {
    x =>
      AnswerRow(
        "trusteeBusinessName.checkYourAnswersLabel",
        HtmlFormat.escape(x),
        Some(controllers.register.trustees.organisation.routes.TrusteeBusinessNameController.onPageLoad(NormalMode, index, draftId).url),
        canEdit = canEdit
      )
  }

  def trusteeIndividualOrBusiness(index: Int, messagePrefix: String): Option[AnswerRow] = userAnswers.get(TrusteeIndividualOrBusinessPage(index)) map {
    x =>
      AnswerRow(
        s"$messagePrefix.checkYourAnswersLabel",
        HtmlFormat.escape(messages(s"individualOrBusiness.$x")),
        Some(controllers.register.trustees.routes.TrusteeIndividualOrBusinessController.onPageLoad(NormalMode, index, draftId).url),
        canEdit = canEdit
      )
  }

  def trusteeUtrYesNo(index: Int): Option[AnswerRow] = userAnswers.get(TrusteeUtrYesNoPage(index)) map {
    x =>
      AnswerRow(
        "leadTrusteeUtrYesNo.checkYourAnswersLabel",
        yesOrNo(x),
        Some(controllers.register.trustees.organisation.routes.TrusteeUtrYesNoController.onPageLoad(NormalMode, index, draftId).url),
        canEdit = canEdit
      )
  }

  def trusteeUtr(index: Int): Option[AnswerRow] = userAnswers.get(TrusteesUtrPage(index)) map {
    x =>
      AnswerRow(
        "trusteeUtr.checkYourAnswersLabel",
        HtmlFormat.escape(x),
        Some(controllers.register.trustees.organisation.routes.TrusteeUtrController.onPageLoad(NormalMode, index, draftId).url),
        orgName(index, userAnswers),
        canEdit = canEdit
      )
  }

  def isThisLeadTrustee(index: Int): Option[AnswerRow] = userAnswers.get(IsThisLeadTrusteePage(index)) map {
    x =>
      AnswerRow(
        "isThisLeadTrustee.checkYourAnswersLabel",
        yesOrNo(x),
        Some(controllers.register.trustees.routes.IsThisLeadTrusteeController.onPageLoad(NormalMode, index, draftId).url),
        canEdit = canEdit
      )
  }

  def orgAddressInTheUkYesNo(index: Int): Option[AnswerRow] = userAnswers.get(TrusteeOrgAddressUkYesNoPage(index)) map {
    x =>
      AnswerRow(
        "trusteeOrgAddressUkYesNo.checkYourAnswersLabel",
        yesOrNo(x),
        Some(controllers.register.trustees.organisation.routes.TrusteeOrgAddressUkYesNoController.onPageLoad(NormalMode, index, draftId).url),
        orgName(index, userAnswers),
        canEdit = canEdit
      )
  }

  def trusteesOrgUkAddress(index: Int): Option[AnswerRow] = userAnswers.get(TrusteeOrgAddressUkPage(index)) map {
    x =>
      AnswerRow(
        "trusteesOrgUkAddress.checkYourAnswersLabel",
        ukAddress(x),
        Some(controllers.register.trustees.organisation.routes.TrusteesOrgUkAddressController.onPageLoad(NormalMode, index, draftId).url),
        orgName(index, userAnswers),
        canEdit = canEdit
      )
  }

  def trusteeOrgInternationalAddress(index: Int): Option[AnswerRow] = userAnswers.get(TrusteeOrgAddressInternationalPage(index)) map {
    x =>
      AnswerRow(
        "trusteeOrgAddressInternational.checkYourAnswersLabel",
        internationalAddress(x, countryOptions),
        Some(controllers.register.trustees.organisation.routes.TrusteeOrgAddressInternationalController.onPageLoad(NormalMode, index, draftId).url),
        orgName(index, userAnswers),
        canEdit = canEdit
      )
  }


  def postcodeForTheTrust: Option[AnswerRow] = userAnswers.get(PostcodeForTheTrustPage) map {
    x =>
      AnswerRow(
        "postcodeForTheTrust.checkYourAnswersLabel",
        HtmlFormat.escape(x),
        Some(controllers.register.routes.PostcodeForTheTrustController.onPageLoad(NormalMode, draftId).url),
        canEdit = canEdit
      )
  }

  def whatIsTheUTR: Option[AnswerRow] = userAnswers.get(WhatIsTheUTRPage) map {
    x =>
      AnswerRow(
        "whatIsTheUTR.checkYourAnswersLabel",
        HtmlFormat.escape(x),
        Some(controllers.register.routes.WhatIsTheUTRController.onPageLoad(NormalMode, draftId).url),
        canEdit = canEdit
      )
  }

  def trustHaveAUTR: Option[AnswerRow] = userAnswers.get(TrustHaveAUTRPage) map {
    x =>
      AnswerRow(
        "trustHaveAUTR.checkYourAnswersLabel",
        yesOrNo(x),
        Some(controllers.register.routes.TrustHaveAUTRController.onPageLoad(NormalMode, draftId).url),
        canEdit = canEdit
      )
  }

  def trustRegisteredOnline: Option[AnswerRow] = userAnswers.get(TrustRegisteredOnlinePage) map {
    x =>
      AnswerRow(
        "trustRegisteredOnline.checkYourAnswersLabel",
        yesOrNo(x),
        Some(controllers.register.routes.TrustRegisteredOnlineController.onPageLoad(NormalMode, draftId).url),
        canEdit = canEdit
      )
  }

  def whenTrustSetup: Option[AnswerRow] = userAnswers.get(WhenTrustSetupPage) map {
    x =>
      AnswerRow(
        "whenTrustSetup.checkYourAnswersLabel",
        HtmlFormat.escape(x.format(dateFormatter)),
        Some(controllers.register.trust_details.routes.WhenTrustSetupController.onPageLoad(NormalMode, draftId).url),
        canEdit = canEdit
      )
  }

  def agentOtherThanBarrister: Option[AnswerRow] = userAnswers.get(AgentOtherThanBarristerPage) map {
    x => AnswerRow("agentOtherThanBarrister.checkYourAnswersLabel", yesOrNo(x), Some(controllers.register.trust_details.routes.AgentOtherThanBarristerController.onPageLoad(NormalMode, draftId).url), canEdit = canEdit)
  }

  def inheritanceTaxAct: Option[AnswerRow] = userAnswers.get(InheritanceTaxActPage) map {
    x => AnswerRow("inheritanceTaxAct.checkYourAnswersLabel", yesOrNo(x), Some(controllers.register.trust_details.routes.InheritanceTaxActController.onPageLoad(NormalMode, draftId).url), canEdit = canEdit)
  }

  def nonresidentType: Option[AnswerRow] = userAnswers.get(NonResidentTypePage) map {
    x => AnswerRow("nonresidentType.checkYourAnswersLabel", answer("nonresidentType", x), Some(controllers.register.trust_details.routes.NonResidentTypeController.onPageLoad(NormalMode, draftId).url), canEdit = canEdit)
  }

  def trustPreviouslyResident: Option[AnswerRow] = userAnswers.get(TrustPreviouslyResidentPage) map {
    x => AnswerRow("trustPreviouslyResident.checkYourAnswersLabel", escape(country(x, countryOptions)), Some(controllers.register.trust_details.routes.TrustPreviouslyResidentController.onPageLoad(NormalMode, draftId).url), canEdit = canEdit)
  }

  def trustResidentOffshore: Option[AnswerRow] = userAnswers.get(TrustResidentOffshorePage) map {
    x => AnswerRow("trustResidentOffshore.checkYourAnswersLabel", yesOrNo(x), Some(controllers.register.trust_details.routes.TrustResidentOffshoreController.onPageLoad(NormalMode, draftId).url), canEdit = canEdit)
  }

  def establishedUnderScotsLaw: Option[AnswerRow] = userAnswers.get(EstablishedUnderScotsLawPage) map {
    x => AnswerRow("establishedUnderScotsLaw.checkYourAnswersLabel", yesOrNo(x), Some(controllers.register.trust_details.routes.EstablishedUnderScotsLawController.onPageLoad(NormalMode, draftId).url), canEdit = canEdit)
  }

  def trusteesBasedInUK: Option[AnswerRow] = userAnswers.get(TrusteesBasedInTheUKPage) map {
    x => AnswerRow("trusteesBasedInTheUK.checkYourAnswersLabel", answer("trusteesBasedInTheUK", x), Some(controllers.register.trust_details.routes.TrusteesBasedInTheUKController.onPageLoad(NormalMode, draftId).url),canEdit = canEdit)
  }

  def countryAdministeringTrust: Option[AnswerRow] = userAnswers.get(CountryAdministeringTrustPage) map {
    x => AnswerRow("countryAdministeringTrust.checkYourAnswersLabel", escape(country(x, countryOptions)), Some(controllers.register.trust_details.routes.CountryAdministeringTrustController.onPageLoad(NormalMode, draftId).url),canEdit = canEdit)
  }

  def administrationInsideUK: Option[AnswerRow] = userAnswers.get(AdministrationInsideUKPage) map {
    x => AnswerRow("administrationInsideUK.checkYourAnswersLabel", yesOrNo(x), Some(controllers.register.trust_details.routes.AdministrationInsideUKController.onPageLoad(NormalMode, draftId).url),canEdit = canEdit)
  }

  def countryGoverningTrust: Option[AnswerRow] = userAnswers.get(CountryGoverningTrustPage) map {
    x => AnswerRow("countryGoverningTrust.checkYourAnswersLabel", escape(country(x, countryOptions)), Some(controllers.register.trust_details.routes.CountryGoverningTrustController.onPageLoad(NormalMode, draftId).url),canEdit = canEdit)
  }

  def governedInsideTheUK: Option[AnswerRow] = userAnswers.get(GovernedInsideTheUKPage) map {
    x => AnswerRow("governedInsideTheUK.checkYourAnswersLabel", yesOrNo(x), Some(controllers.register.trust_details.routes.GovernedInsideTheUKController.onPageLoad(NormalMode, draftId).url),canEdit = canEdit)
  }

  def trustName: Option[AnswerRow] = userAnswers.get(TrustNamePage) map {
    x => AnswerRow("trustName.checkYourAnswersLabel", escape(x), Some(controllers.register.trust_details.routes.TrustNameController.onPageLoad(NormalMode, draftId).url),canEdit = canEdit)
  }

  def registeringTrustFor5A: Option[AnswerRow] = userAnswers.get(RegisteringTrustFor5APage) map {
    x => AnswerRow("registeringTrustFor5A.checkYourAnswersLabel", yesOrNo(x), Some(controllers.register.trust_details.routes.RegisteringTrustFor5AController.onPageLoad(NormalMode, draftId).url),canEdit = canEdit)
  }

}