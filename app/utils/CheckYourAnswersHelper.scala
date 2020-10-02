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

import controllers.register.settlors.living_settlor.business.{routes => businessRoutes}
import controllers.register.settlors.living_settlor.routes
import javax.inject.Inject
import mapping.reads._
import models.NormalMode
import models.core.UserAnswers
import models.registration.Matched.Success
import pages.register._
import pages.register.agents._
import pages.register.asset.WhatKindOfAssetPage
import pages.register.asset.business._
import pages.register.asset.money.AssetMoneyValuePage
import pages.register.asset.other.{OtherAssetDescriptionPage, OtherAssetValuePage}
import pages.register.asset.partnership.{PartnershipDescriptionPage, PartnershipStartDatePage}
import pages.register.asset.property_or_land._
import pages.register.asset.shares._
import pages.register.settlors.SetUpAfterSettlorDiedYesNoPage
import pages.register.settlors.deceased_settlor._
import pages.register.settlors.living_settlor._
import pages.register.settlors.living_settlor.business._
import pages.register.settlors.living_settlor.trust_type._
import play.api.i18n.Messages
import play.twirl.api.HtmlFormat
import repositories.RegistrationsRepository
import sections.settlors.LivingSettlors
import utils.CheckAnswersFormatters._
import utils.countryOptions.CountryOptions
import viewmodels.{AnswerRow, AnswerSection}

class CheckYourAnswersHelper @Inject()(countryOptions: CountryOptions)
                                      (userAnswers: UserAnswers,
                                       draftId: String,
                                       canEdit: Boolean)
                                      (implicit messages: Messages) {

  def partnershipStartDate(index: Int): Option[AnswerRow] = userAnswers.get(PartnershipStartDatePage(index)) map {
    x =>
      AnswerRow(
        "partnershipStartDate.checkYourAnswersLabel",
        HtmlFormat.escape(x.format(dateFormatter)),
        Some(controllers.register.asset.partnership.routes.PartnershipStartDateController.onPageLoad(NormalMode, index, draftId).url),
        canEdit = canEdit
      )
  }

  def partnershipDescription(index: Int): Option[AnswerRow] = userAnswers.get(PartnershipDescriptionPage(index)) map {
    x =>
      AnswerRow(
        "partnershipDescription.checkYourAnswersLabel",
        HtmlFormat.escape(x),
        Some(controllers.register.asset.partnership.routes.PartnershipDescriptionController.onPageLoad(NormalMode, index, draftId).url),
        canEdit = canEdit
      )
  }

  def assetAddressUkYesNo(index: Int): Option[AnswerRow] = userAnswers.get(BusinessAddressUkYesNoPage(index)) map {
    x =>
      AnswerRow(
        "assetAddressUkYesNo.checkYourAnswersLabel",
        yesOrNo(x),
        Some(controllers.register.asset.business.routes.BusinessAddressUkYesNoController.onPageLoad(NormalMode, index, draftId).url),
        canEdit = canEdit
      )
  }

  def assetDescription(index: Int): Option[AnswerRow] = userAnswers.get(BusinessDescriptionPage(index)) map {
    x =>
      AnswerRow(
        "assetDescription.checkYourAnswersLabel",
        HtmlFormat.escape(x),
        Some(controllers.register.asset.business.routes.BusinessDescriptionController.onPageLoad(NormalMode, index, draftId).url),
        canEdit = canEdit
      )
  }

  def assetNamePage(index: Int): Option[AnswerRow] = userAnswers.get(BusinessNamePage(index)) map {
    x =>
      AnswerRow(
        "assetName.checkYourAnswersLabel",
        HtmlFormat.escape(x),
        Some(controllers.register.asset.business.routes.BusinessNameController.onPageLoad(NormalMode, index, draftId).url),
        canEdit = canEdit
      )
  }

  def assetInternationalAddress(index: Int): Option[AnswerRow] = userAnswers.get(BusinessInternationalAddressPage(index)) map {
    x =>
      AnswerRow(
        "assetInternationalAddress.checkYourAnswersLabel",
        internationalAddress(x, countryOptions),
        Some(controllers.register.asset.business.routes.BusinessInternationalAddressController.onPageLoad(NormalMode, index, draftId).url),
        assetName(index, userAnswers),
        canEdit = canEdit
      )
  }

  def assetUkAddress(index: Int): Option[AnswerRow] = userAnswers.get(BusinessUkAddressPage(index)) map {
    x =>
      AnswerRow(
        "assetUkAddress.checkYourAnswersLabel",
        ukAddress(x),
        Some(controllers.register.asset.business.routes.BusinessUkAddressController.onPageLoad(NormalMode, index, draftId).url),
        assetName(index, userAnswers),
        canEdit = canEdit
      )
  }

  def currentValue(index: Int): Option[AnswerRow] = userAnswers.get(BusinessValuePage(index)) map {
    x =>
      AnswerRow(
        "currentValue.checkYourAnswersLabel",
        currency(x),
        Some(controllers.register.asset.business.routes.BusinessValueController.onPageLoad(NormalMode, index, draftId).url),
        canEdit = canEdit
      )
  }

  def settlorBusinessName(index: Int): Option[AnswerRow] = userAnswers.get(SettlorBusinessNamePage(index)) map {
    x =>
      AnswerRow(
        "settlorBusinessName.checkYourAnswersLabel",
        HtmlFormat.escape(x),
        Some(businessRoutes.SettlorBusinessNameController.onPageLoad(NormalMode, index, draftId).url),
        canEdit = canEdit
      )
  }

  def settlorBusinessUtrYesNo(index: Int): Option[AnswerRow] = userAnswers.get(SettlorBusinessUtrYesNoPage(index)) map {
    x =>
      AnswerRow(
        "settlorBusinessUtrYesNo.checkYourAnswersLabel",
        yesOrNo(x),
        Some(businessRoutes.SettlorBusinessUtrYesNoController.onPageLoad(NormalMode, index, draftId).url),
        businessSettlorName(index, userAnswers),
        canEdit = canEdit
      )
  }

  def settlorBusinessUtr(index: Int): Option[AnswerRow] = userAnswers.get(SettlorBusinessUtrPage(index)) map {
    x =>
      AnswerRow(
        "settlorBusinessUtr.checkYourAnswersLabel",
        HtmlFormat.escape(x),
        Some(businessRoutes.SettlorBusinessUtrController.onPageLoad(NormalMode, index, draftId).url),
        businessSettlorName(index, userAnswers),
        canEdit = canEdit
      )
  }

  def settlorBusinessAddressYesNo(index: Int): Option[AnswerRow] = userAnswers.get(SettlorBusinessAddressYesNoPage(index)) map {
    x =>
      AnswerRow(
        "settlorBusinessAddressYesNo.checkYourAnswersLabel",
        yesOrNo(x),
        Some(businessRoutes.SettlorBusinessAddressYesNoController.onPageLoad(NormalMode, index, draftId).url),
        businessSettlorName(index, userAnswers),
        canEdit = canEdit
      )
  }

  def settlorBusinessAddressUkYesNo(index: Int): Option[AnswerRow] = userAnswers.get(SettlorBusinessAddressUKYesNoPage(index)) map {
    x =>
      AnswerRow(
        "settlorBusinessAddressUKYesNo.checkYourAnswersLabel",
        yesOrNo(x),
        Some(businessRoutes.SettlorBusinessAddressUKYesNoController.onPageLoad(NormalMode, index, draftId).url),
        businessSettlorName(index, userAnswers),
        canEdit = canEdit
      )
  }

  def settlorBusinessAddressUk(index: Int): Option[AnswerRow] = userAnswers.get(SettlorBusinessAddressUKPage(index)) map {
    x =>
      AnswerRow(
        "settlorBusinessAddressUK.checkYourAnswersLabel",
        ukAddress(x),
        Some(businessRoutes.SettlorBusinessAddressUKController.onPageLoad(NormalMode, index, draftId).url),
        businessSettlorName(index, userAnswers),
        canEdit = canEdit
      )
  }

  def settlorBusinessAddressInternational(index: Int): Option[AnswerRow] = userAnswers.get(SettlorBusinessAddressInternationalPage(index)) map {
    x =>
      AnswerRow(
        "settlorBusinessAddressInternational.checkYourAnswersLabel",
        internationalAddress(x, countryOptions),
        Some(businessRoutes.SettlorBusinessAddressInternationalController.onPageLoad(NormalMode, index, draftId).url),
        businessSettlorName(index, userAnswers),
        canEdit = canEdit
      )
  }

  def settlorBusinessType(index: Int): Option[AnswerRow] = userAnswers.get(SettlorBusinessTypePage(index)) map {
    x =>
      AnswerRow(
        "settlorBusinessType.checkYourAnswersLabel",
        HtmlFormat.escape(messages(s"kindOfBusiness.$x")),
        Some(businessRoutes.SettlorBusinessTypeController.onPageLoad(NormalMode, index, draftId).url),
        businessSettlorName(index, userAnswers),
        canEdit = canEdit
      )
  }

  def settlorBusinessTimeYesNo(index: Int): Option[AnswerRow] = userAnswers.get(SettlorBusinessTimeYesNoPage(index)) map {
    x =>
      AnswerRow(
        "settlorBusinessTimeYesNo.checkYourAnswersLabel",
        yesOrNo(x),
        Some(businessRoutes.SettlorBusinessTimeYesNoController.onPageLoad(NormalMode, index, draftId).url),
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

  def efrbsYesNo: Option[AnswerRow] = userAnswers.get(EfrbsYesNoPage) map {
    x =>
      AnswerRow(
        "employerFinancedRbsYesNo.checkYourAnswersLabel",
        yesOrNo(x),
        Some(routes.EmployerFinancedRbsYesNoController.onPageLoad(NormalMode, draftId).url),
        canEdit = canEdit
      )
  }

  def efrbsStartDate: Option[AnswerRow] = userAnswers.get(EfrbsStartDatePage) map {
    x =>
      AnswerRow(
        "employerFinancedRbsStartDate.checkYourAnswersLabel",
        HtmlFormat.escape(x.format(dateFormatter)),
        Some(routes.EmployerFinancedRbsStartDateController.onPageLoad(NormalMode, draftId).url),
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
    val isExistingTrust: Boolean = userAnswers.get(ExistingTrustMatched).contains(Success)

    val existingTrustRows = if (isExistingTrust) {
      Seq(
        trustRegisteredWithUkAddress,
        postcodeForTheTrust,
        whatIsTheUTR
      )
    } else {
      Nil
    }

    val questions = (trustName(canEdit) +: (existingTrustRows)).flatten

    if (questions.nonEmpty) Some(Seq(AnswerSection(None, questions, Some(messages("answerPage.section.trustsDetails.heading"))))) else None
  }

  def deceasedSettlor: Option[Seq[AnswerSection]] = {

    val questions = Seq(
      setUpAfterSettlorDied,
      setUpInAddition,
      deedOfVariation,
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
          deedOfVariation,
          setUpInAddition,
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
          settlorIndividualIDCard(index),
          settlorBusinessName(index),
          settlorBusinessUtrYesNo(index),
          settlorBusinessUtr(index),
          settlorBusinessAddressYesNo(index),
          settlorBusinessAddressUkYesNo(index),
          settlorBusinessAddressUk(index),
          settlorBusinessAddressInternational(index),
          settlorBusinessType(index),
          settlorBusinessTimeYesNo(index)
        ).flatten

        val sectionKey = if (index == 0) Some(messages("answerPage.section.settlors.heading")) else None

        AnswerSection(
          headingKey = Some(messages("answerPage.section.settlor.subheading", index + 1)),
          questions,
          sectionKey = sectionKey
        )
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

  def businessAsset: Seq[AnswerSection] = {
    val answers: Seq[(BusinessAsset, Int)] = userAnswers.get(Assets).getOrElse(Nil).zipWithIndex.collect {
      case (x: BusinessAsset, index) => (x, index)
    }

    answers.flatMap {
      case o@(m, index) =>
        Seq(
          AnswerSection(
            Some(s"${messages("answerPage.section.businessAsset.subheading")} ${answers.indexOf(o) + 1}"),
            Seq(
              assetNamePage(index),
              assetDescription(index),
              assetAddressUkYesNo(index),
              assetUkAddress(index),
              assetInternationalAddress(index),
              currentValue(index)
            ).flatten,
            None
          )
        )
      case _ => Nil
    }

  }

  def other: Seq[AnswerSection] = {
    val answers = userAnswers.get(Assets).getOrElse(Nil).zipWithIndex.collect {
      case (x: OtherAsset, index) => (x, index)
    }

    answers.flatMap {
      case o@(m, index) =>
        Seq(
          AnswerSection(
            Some(s"${messages("answerPage.section.otherAsset.subheading")} ${answers.indexOf(o) + 1}"),
            Seq(
              otherAssetDescription(index),
              otherAssetValue(index, m.description)
            ).flatten,
            None
          )
        )
    }
  }

  def partnership: Seq[AnswerSection] = {
    val answers = userAnswers.get(Assets).getOrElse(Nil).zipWithIndex.collect {
      case (x: PartnershipAsset, index) => (x, index)
    }

    answers.flatMap {
      case o@(m, index) =>
        Seq(
          AnswerSection(
            Some(s"${messages("answerPage.section.partnershipAsset.subheading")} ${answers.indexOf(o) + 1}"),
            Seq(
              partnershipDescription(index),
              partnershipStartDate(index)
            ).flatten,
            None
          )
        )
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

  def agentName: Option[AnswerRow] = userAnswers.get(AgentNamePage) map {
    x =>
      AnswerRow(
        "agentName.checkYourAnswersLabel",
        HtmlFormat.escape(x),
        Some(controllers.register.agents.routes.AgentNameController.onPageLoad(NormalMode, draftId).url),
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

  def trustName(canEdit: Boolean = canEdit): Option[AnswerRow] = userAnswers.get(MatchingNamePage) map {
    x => AnswerRow("trustName.checkYourAnswersLabel", escape(x), Some(controllers.register.routes.MatchingNameController.onPageLoad(draftId).url),canEdit = canEdit)
  }

  def otherAssetDescription(index: Int): Option[AnswerRow] = userAnswers.get(OtherAssetDescriptionPage(index)) map {
    x =>
      AnswerRow(
        "assets.other.description.checkYourAnswersLabel",
        escape(x),
        Some(controllers.register.asset.other.routes.OtherAssetDescriptionController.onPageLoad(NormalMode, index, draftId).url),
        canEdit = canEdit
      )
  }

  def otherAssetValue(index: Int, description: String): Option[AnswerRow] = userAnswers.get(OtherAssetValuePage(index)) map {
    x =>
      AnswerRow(
        "assets.other.value.checkYourAnswersLabel",
        currency(x),
        Some(controllers.register.asset.other.routes.OtherAssetValueController.onPageLoad(NormalMode, index, draftId).url),
        description,
        canEdit = canEdit
      )
  }

  def trustRegisteredWithUkAddress: Option[AnswerRow] = userAnswers.get(TrustRegisteredWithUkAddressYesNoPage) map {
    x =>
      AnswerRow(
        "trustRegisteredWithUkAddress.checkYourAnswersLabel",
        yesOrNo(x),
        canEdit = canEdit
      )
  }

}