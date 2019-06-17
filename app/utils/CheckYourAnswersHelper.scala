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
import models.{CheckMode, FullName, InternationalAddress, UKAddress, UserAnswers}
import pages._
import play.api.i18n.Messages
import play.twirl.api.{Html, HtmlFormat}
import uk.gov.hmrc.domain.Nino
import utils.CheckYourAnswersHelper.{indBeneficiaryName, trusteeName, _}
import utils.countryOptions.CountryOptions
import viewmodels.AnswerRow

class CheckYourAnswersHelper @Inject()(countryOptions: CountryOptions)(userAnswers: UserAnswers, draftId : String)
                                      (implicit messages: Messages)  {

  def agentInternationalAddress: Option[AnswerRow] = userAnswers.get(AgentInternationalAddressPage) map {
    x =>
      AnswerRow(
        "site.address.international.checkYourAnswersLabel",
        internationalAddress(x, countryOptions),
        routes.AgentInternationalAddressController.onPageLoad(CheckMode, draftId).url,
        agencyName(userAnswers)
      )
  }

  def classBeneficiaryDescription(index: Int): Option[AnswerRow] = userAnswers.get(ClassBeneficiaryDescriptionPage(index)) map {
    x =>
      AnswerRow(
        "classBeneficiaryDescription.checkYourAnswersLabel",

        HtmlFormat.escape(x),
        routes.ClassBeneficiaryDescriptionController.onPageLoad(CheckMode,index, draftId).url
      )
  }

  def agentUKAddress: Option[AnswerRow] = userAnswers.get(AgentUKAddressPage) map {
    x =>
      AnswerRow(
        "site.address.uk.checkYourAnswersLabel",
        ukAddress(x),
        routes.AgentUKAddressController.onPageLoad(CheckMode, draftId).url,
        agencyName(userAnswers)
      )
  }

  def agentAddressYesNo: Option[AnswerRow] = userAnswers.get(AgentAddressYesNoPage) map {
    x =>
      AnswerRow(
        "agentAddressYesNo.checkYourAnswersLabel",
        yesOrNo(x),
        routes.AgentAddressYesNoController.onPageLoad(CheckMode, draftId).url,
        agencyName(userAnswers)
      )
  }

  def individualBeneficiaryAddressUKYesNo(index: Int): Option[AnswerRow] = userAnswers.get(IndividualBeneficiaryAddressUKYesNoPage(index)) map {
    x =>
      AnswerRow(
        "individualBeneficiaryAddressUKYesNo.checkYourAnswersLabel",
        yesOrNo(x),
        routes.IndividualBeneficiaryAddressUKYesNoController.onPageLoad(CheckMode, index, draftId).url,
        indBeneficiaryName(index,userAnswers)
      )
  }

  def agentName: Option[AnswerRow] = userAnswers.get(AgentNamePage) map {
    x =>
      AnswerRow(
        "agentName.checkYourAnswersLabel",
        HtmlFormat.escape(x),
        routes.AgentNameController.onPageLoad(CheckMode, draftId).url
      )
  }

  def addABeneficiary: Option[AnswerRow] = userAnswers.get(AddABeneficiaryPage) map {
    x =>
      AnswerRow(
        "addABeneficiary.checkYourAnswersLabel",
        HtmlFormat.escape(messages(s"addABeneficiary.$x")),
        routes.AddABeneficiaryController.onPageLoad(draftId).url
      )
  }

  def individualBeneficiaryVulnerableYesNo(index: Int): Option[AnswerRow] = userAnswers.get(IndividualBeneficiaryVulnerableYesNoPage(index)) map {
    x =>
      AnswerRow(
        "individualBeneficiaryVulnerableYesNo.checkYourAnswersLabel",
        yesOrNo(x),
        routes.IndividualBeneficiaryVulnerableYesNoController.onPageLoad(CheckMode, index, draftId).url,
        indBeneficiaryName(index,userAnswers)
      )
  }

  def individualBeneficiaryAddressUK(index: Int): Option[AnswerRow] = userAnswers.get(IndividualBeneficiaryAddressUKPage(index)) map {
    x =>
      AnswerRow(
        "individualBeneficiaryAddressUK.checkYourAnswersLabel",
        ukAddress(x),
        routes.IndividualBeneficiaryAddressUKController.onPageLoad(CheckMode, index, draftId).url,
        indBeneficiaryName(index,userAnswers)
      )
  }

  def individualBeneficiaryAddressYesNo(index: Int): Option[AnswerRow] = userAnswers.get(IndividualBeneficiaryAddressYesNoPage(index)) map {
    x =>
      AnswerRow(
        "individualBeneficiaryAddressYesNo.checkYourAnswersLabel",
        yesOrNo(x),
        routes.IndividualBeneficiaryAddressYesNoController.onPageLoad(CheckMode, index, draftId).url,
        indBeneficiaryName(index,userAnswers)
      )
  }

  def individualBeneficiaryNationalInsuranceNumber(index: Int): Option[AnswerRow] = userAnswers.get(IndividualBeneficiaryNationalInsuranceNumberPage(index)) map {
    x =>
      AnswerRow(
        "individualBeneficiaryNationalInsuranceNumber.checkYourAnswersLabel",
        HtmlFormat.escape(formatNino(x)),
        routes.IndividualBeneficiaryNationalInsuranceNumberController.onPageLoad(CheckMode, index, draftId).url,
        indBeneficiaryName(index,userAnswers)
      )
  }

  def individualBeneficiaryNationalInsuranceYesNo(index: Int): Option[AnswerRow] = userAnswers.get(IndividualBeneficiaryNationalInsuranceYesNoPage(index)) map {
    x =>
      AnswerRow(
        "individualBeneficiaryNationalInsuranceYesNo.checkYourAnswersLabel",
        yesOrNo(x),
        routes.IndividualBeneficiaryNationalInsuranceYesNoController.onPageLoad(CheckMode, index, draftId).url,
        indBeneficiaryName(index,userAnswers)
      )
  }

  def individualBeneficiaryIncome(index: Int): Option[AnswerRow] = userAnswers.get(IndividualBeneficiaryIncomePage(index)) map {
    x =>
      AnswerRow(
        "individualBeneficiaryIncome.checkYourAnswersLabel",
        HtmlFormat.escape(x),
        routes.IndividualBeneficiaryIncomeController.onPageLoad(CheckMode, index, draftId).url,
        indBeneficiaryName(index, userAnswers)
      )
  }

  def individualBeneficiaryIncomeYesNo(index: Int): Option[AnswerRow] = userAnswers.get(IndividualBeneficiaryIncomeYesNoPage(index)) map {
    x =>
      AnswerRow(
        "individualBeneficiaryIncomeYesNo.checkYourAnswersLabel",
        yesOrNo(x),
        routes.IndividualBeneficiaryIncomeYesNoController.onPageLoad(CheckMode, index, draftId).url,
        indBeneficiaryName(index,userAnswers)
      )
  }

  def individualBeneficiaryDateOfBirth(index: Int): Option[AnswerRow] = userAnswers.get(IndividualBeneficiaryDateOfBirthPage(index)) map {
    x =>
      AnswerRow(
        "individualBeneficiaryDateOfBirth.checkYourAnswersLabel",
        HtmlFormat.escape(x.format(dateFormatter)),
        routes.IndividualBeneficiaryDateOfBirthController.onPageLoad(CheckMode, index, draftId).url,
        indBeneficiaryName(index,userAnswers)
      )
  }

  def individualBeneficiaryDateOfBirthYesNo(index: Int): Option[AnswerRow] = userAnswers.get(IndividualBeneficiaryDateOfBirthYesNoPage(index)) map {
    x =>
      AnswerRow(
        "individualBeneficiaryDateOfBirthYesNo.checkYourAnswersLabel",
        yesOrNo(x),
        routes.IndividualBeneficiaryDateOfBirthYesNoController.onPageLoad(CheckMode, index, draftId).url,
        indBeneficiaryName(index,userAnswers)
      )
  }

  def individualBeneficiaryName(index: Int): Option[AnswerRow] = userAnswers.get(IndividualBeneficiaryNamePage(index)) map {
    x =>
      AnswerRow(
        "individualBeneficiaryName.checkYourAnswersLabel",
        HtmlFormat.escape(s"${x.firstName} ${x.middleName.getOrElse("")} ${x.lastName}"),
        routes.IndividualBeneficiaryNameController.onPageLoad(CheckMode, index, draftId).url
      )
  }

  def wasSettlorsAddressUKYesNo: Option[AnswerRow] = userAnswers.get(WasSettlorsAddressUKYesNoPage) map {
    x =>
      AnswerRow(
        "wasSettlorsAddressUKYesNo.checkYourAnswersLabel",
        yesOrNo(x),
        routes.WasSettlorsAddressUKYesNoController.onPageLoad(CheckMode, draftId).url,
          deceasedSettlorName(userAnswers)
      )
  }

  def setupAfterSettlorDied: Option[AnswerRow] = userAnswers.get(SetupAfterSettlorDiedPage) map {
    x =>
      AnswerRow(
        "setupAfterSettlorDied.checkYourAnswersLabel",
        yesOrNo(x),
        routes.SetupAfterSettlorDiedController.onPageLoad(CheckMode, draftId).url
      )
  }

  def settlorsUKAddress: Option[AnswerRow] = userAnswers.get(SettlorsUKAddressPage) map {
    x =>
      AnswerRow(
        "settlorsUKAddress.checkYourAnswersLabel",
        ukAddress(x),
        routes.SettlorsUKAddressController.onPageLoad(CheckMode, draftId).url,
        deceasedSettlorName(userAnswers)
      )
  }

  def settlorsNINoYesNo: Option[AnswerRow] = userAnswers.get(SettlorsNINoYesNoPage) map {
    x =>
      AnswerRow(
        "settlorsNINoYesNo.checkYourAnswersLabel",
        yesOrNo(x),
        routes.SettlorsNINoYesNoController.onPageLoad(CheckMode, draftId).url,
        deceasedSettlorName(userAnswers)
      )
  }

  def settlorsName: Option[AnswerRow] = userAnswers.get(SettlorsNamePage) map {
    x =>
      AnswerRow(
        "settlorsName.checkYourAnswersLabel",
        HtmlFormat.escape(s"${x.firstName} ${x.lastName}"),
        routes.SettlorsNameController.onPageLoad(CheckMode, draftId).url
      )
  }

  def settlorsLastKnownAddressYesNo: Option[AnswerRow] = userAnswers.get(SettlorsLastKnownAddressYesNoPage) map {
    x =>
      AnswerRow(
        "settlorsLastKnownAddressYesNo.checkYourAnswersLabel",
        yesOrNo(x),
        routes.SettlorsLastKnownAddressYesNoController.onPageLoad(CheckMode, draftId).url,
        deceasedSettlorName(userAnswers)
      )
  }

  def settlorsInternationalAddress: Option[AnswerRow] = userAnswers.get(SettlorsInternationalAddressPage) map {
    x =>
      AnswerRow(
        "settlorsInternationalAddress.checkYourAnswersLabel",
        internationalAddress(x, countryOptions),
        routes.SettlorsInternationalAddressController.onPageLoad(CheckMode, draftId).url,
        deceasedSettlorName(userAnswers)
      )
  }

  def settlorsDateOfBirth: Option[AnswerRow] = userAnswers.get(SettlorsDateOfBirthPage) map {
    x =>
      AnswerRow(
        "settlorsDateOfBirth.checkYourAnswersLabel",
        HtmlFormat.escape(x.format(dateFormatter)),
        routes.SettlorsDateOfBirthController.onPageLoad(CheckMode, draftId).url,
        deceasedSettlorName(userAnswers)
      )
  }

  def settlorNationalInsuranceNumber: Option[AnswerRow] = userAnswers.get(SettlorNationalInsuranceNumberPage) map {
    x =>
      AnswerRow(
        "settlorNationalInsuranceNumber.checkYourAnswersLabel",
        HtmlFormat.escape(formatNino(x)),
        routes.SettlorNationalInsuranceNumberController.onPageLoad(CheckMode, draftId).url,
        deceasedSettlorName(userAnswers)
      )
  }

  def settlorDateOfDeathYesNo: Option[AnswerRow] = userAnswers.get(SettlorDateOfDeathYesNoPage) map {
    x =>
      AnswerRow(
        "settlorDateOfDeathYesNo.checkYourAnswersLabel",
        yesOrNo(x),
        routes.SettlorDateOfDeathYesNoController.onPageLoad(CheckMode, draftId).url,
        deceasedSettlorName(userAnswers)
      )
  }

  def settlorDateOfDeath: Option[AnswerRow] = userAnswers.get(SettlorDateOfDeathPage) map {
    x =>
      AnswerRow(
        "settlorDateOfDeath.checkYourAnswersLabel",
        HtmlFormat.escape(x.format(dateFormatter)),
        routes.SettlorDateOfDeathController.onPageLoad(CheckMode, draftId).url,
        deceasedSettlorName(userAnswers)
      )
  }

  def settlorDateOfBirthYesNo: Option[AnswerRow] = userAnswers.get(SettlorDateOfBirthYesNoPage) map {
    x =>
      AnswerRow(
        "settlorDateOfBirthYesNo.checkYourAnswersLabel",
        yesOrNo(x),
        routes.SettlorDateOfBirthYesNoController.onPageLoad(CheckMode, draftId).url,
        deceasedSettlorName(userAnswers)
      )
  }

  def assetMoneyValue(index: Int): Option[AnswerRow] = userAnswers.get(AssetMoneyValuePage(index)) map {
    x =>
      AnswerRow(
        "assetMoneyValue.checkYourAnswersLabel",
        HtmlFormat.escape(x),
        routes.AssetMoneyValueController.onPageLoad(CheckMode, index, draftId).url
      )
  }

  def whatKindOfAsset(index: Int): Option[AnswerRow] = userAnswers.get(WhatKindOfAssetPage(index)) map {
    x =>
      AnswerRow(
        "whatKindOfAsset.checkYourAnswersLabel",
        HtmlFormat.escape(messages(s"whatKindOfAsset.$x")),
        routes.WhatKindOfAssetController.onPageLoad(CheckMode, index, draftId).url
      )
  }

  def agentInternalReference: Option[AnswerRow] = userAnswers.get(AgentInternalReferencePage) map {
    x =>
      AnswerRow(
        "agentInternalReference.checkYourAnswersLabel",
        HtmlFormat.escape(x),
        routes.AgentInternalReferenceController.onPageLoad(CheckMode, draftId).url
      )
  }

  def agenciesTelephoneNumber: Option[AnswerRow] = userAnswers.get(AgentTelephoneNumberPage) map {
    x =>
      AnswerRow(
        "agentTelephoneNumber.checkYourAnswersLabel",
        HtmlFormat.escape(x),
        routes.AgentTelephoneNumberController.onPageLoad(CheckMode, draftId).url
      )
  }

  def trusteesNino(index: Int): Option[AnswerRow] = userAnswers.get(TrusteesNinoPage(index)) map {
    x =>
      AnswerRow(
        "trusteesNino.checkYourAnswersLabel",
        HtmlFormat.escape(formatNino(x)),
        routes.TrusteesNinoController.onPageLoad(CheckMode, index, draftId).url,
        trusteeName(index, userAnswers)
      )
  }

  def trusteeLiveInTheUK(index : Int): Option[AnswerRow] = userAnswers.get(TrusteeLiveInTheUKPage(index)) map {
    x =>
      AnswerRow(
        "trusteeLiveInTheUK.checkYourAnswersLabel",
        yesOrNo(x),
        routes.TrusteeLiveInTheUKController.onPageLoad(CheckMode, index, draftId).url,
        trusteeName(index, userAnswers)
      )
  }

  def trusteesUkAddress(index: Int): Option[AnswerRow] = userAnswers.get(TrusteesUkAddressPage(index)) map {
    x =>
      AnswerRow(
        "trusteesUkAddress.checkYourAnswersLabel",
        ukAddress(x),
        routes.TrusteesUkAddressController.onPageLoad(CheckMode, index, draftId).url,
        trusteeName(index, userAnswers)
      )
  }

  def trusteesDateOfBirth(index : Int): Option[AnswerRow] = userAnswers.get(TrusteesDateOfBirthPage(index)) map {
    x =>
      AnswerRow(
        "trusteesDateOfBirth.checkYourAnswersLabel",
        HtmlFormat.escape(x.format(dateFormatter)),
        routes.TrusteesDateOfBirthController.onPageLoad(CheckMode, index, draftId).url,
        trusteeName(index, userAnswers)
      )
  }

  def telephoneNumber(index : Int): Option[AnswerRow] = userAnswers.get(TelephoneNumberPage(index)) map {
    x =>
      AnswerRow(
        "telephoneNumber.checkYourAnswersLabel",
        HtmlFormat.escape(x),
        routes.TelephoneNumberController.onPageLoad(CheckMode, index, draftId).url,
        trusteeName(index, userAnswers)
      )
  }

  def trusteeAUKCitizen(index : Int): Option[AnswerRow] = userAnswers.get(TrusteeAUKCitizenPage(index)) map {
    x =>
      AnswerRow(
        "trusteeAUKCitizen.checkYourAnswersLabel",
        yesOrNo(x),
        routes.TrusteeAUKCitizenController.onPageLoad(CheckMode,index, draftId).url,
        trusteeName(index, userAnswers)
      )
  }


  def trusteeFullName(index : Int, messagePrefix: String): Option[AnswerRow] = userAnswers.get(TrusteesNamePage(index)) map {
    x => AnswerRow(
      s"$messagePrefix.checkYourAnswersLabel",
      HtmlFormat.escape(s"${x.firstName} ${x.middleName.getOrElse("")} ${x.lastName}"),
      routes.TrusteesNameController.onPageLoad(CheckMode, index, draftId).url
    )
  }

  def trusteeIndividualOrBusiness(index : Int, messagePrefix: String): Option[AnswerRow] = userAnswers.get(TrusteeIndividualOrBusinessPage(index)) map {
    x =>
      AnswerRow(
        s"$messagePrefix.checkYourAnswersLabel",
        HtmlFormat.escape(messages(s"individualOrBusiness.$x")),
        routes.TrusteeIndividualOrBusinessController.onPageLoad(CheckMode, index, draftId).url
      )
  }

  def isThisLeadTrustee(index: Int): Option[AnswerRow] = userAnswers.get(IsThisLeadTrusteePage(index)) map {
    x =>
      AnswerRow(
        "isThisLeadTrustee.checkYourAnswersLabel",
        yesOrNo(x),
        routes.IsThisLeadTrusteeController.onPageLoad(CheckMode, index, draftId).url
      )
  }

  def postcodeForTheTrust: Option[AnswerRow] = userAnswers.get(PostcodeForTheTrustPage) map {
    x =>
      AnswerRow(
        "postcodeForTheTrust.checkYourAnswersLabel",
        HtmlFormat.escape(x),
        routes.PostcodeForTheTrustController.onPageLoad(CheckMode, draftId).url
      )
  }

  def whatIsTheUTR: Option[AnswerRow] = userAnswers.get(WhatIsTheUTRPage) map {
    x =>
      AnswerRow(
        "whatIsTheUTR.checkYourAnswersLabel",
        HtmlFormat.escape(x),
        routes.WhatIsTheUTRController.onPageLoad(CheckMode, draftId).url
      )
  }

  def trustHaveAUTR: Option[AnswerRow] = userAnswers.get(TrustHaveAUTRPage) map {
    x =>
      AnswerRow(
        "trustHaveAUTR.checkYourAnswersLabel",
        yesOrNo(x),
        routes.TrustHaveAUTRController.onPageLoad(CheckMode, draftId).url
      )
  }

  def trustRegisteredOnline: Option[AnswerRow] = userAnswers.get(TrustRegisteredOnlinePage) map {
    x =>
      AnswerRow(
        "trustRegisteredOnline.checkYourAnswersLabel",
        yesOrNo(x),
        routes.TrustRegisteredOnlineController.onPageLoad(CheckMode, draftId).url
      )
  }

  def whenTrustSetup: Option[AnswerRow] = userAnswers.get(WhenTrustSetupPage) map {
    x =>
      AnswerRow(
        "whenTrustSetup.checkYourAnswersLabel",
        HtmlFormat.escape(x.format(dateFormatter)),
        routes.WhenTrustSetupController.onPageLoad(CheckMode, draftId).url
      )
  }

  def agentOtherThanBarrister: Option[AnswerRow] = userAnswers.get(AgentOtherThanBarristerPage) map {
    x => AnswerRow("agentOtherThanBarrister.checkYourAnswersLabel", yesOrNo(x), routes.AgentOtherThanBarristerController.onPageLoad(CheckMode, draftId).url)
  }

  def inheritanceTaxAct: Option[AnswerRow] = userAnswers.get(InheritanceTaxActPage) map {
    x => AnswerRow("inheritanceTaxAct.checkYourAnswersLabel", yesOrNo(x), routes.InheritanceTaxActController.onPageLoad(CheckMode, draftId).url)
  }

  def nonresidentType: Option[AnswerRow] = userAnswers.get(NonResidentTypePage) map {
    x => AnswerRow("nonresidentType.checkYourAnswersLabel", answer("nonresidentType", x), routes.NonResidentTypeController.onPageLoad(CheckMode, draftId).url)
  }

  def trustPreviouslyResident: Option[AnswerRow] = userAnswers.get(TrustPreviouslyResidentPage) map {
    x => AnswerRow("trustPreviouslyResident.checkYourAnswersLabel", escape(country(x, countryOptions)), routes.TrustPreviouslyResidentController.onPageLoad(CheckMode, draftId).url)
  }

  def trustResidentOffshore: Option[AnswerRow] = userAnswers.get(TrustResidentOffshorePage) map {
    x => AnswerRow("trustResidentOffshore.checkYourAnswersLabel", yesOrNo(x), routes.TrustResidentOffshoreController.onPageLoad(CheckMode, draftId).url)
  }

  def registeringTrustFor5A: Option[AnswerRow] = userAnswers.get(RegisteringTrustFor5APage) map {
    x => AnswerRow("registeringTrustFor5A.checkYourAnswersLabel", yesOrNo(x), routes.RegisteringTrustFor5AController.onPageLoad(CheckMode, draftId).url)
  }

  def establishedUnderScotsLaw: Option[AnswerRow] = userAnswers.get(EstablishedUnderScotsLawPage) map {
    x => AnswerRow("establishedUnderScotsLaw.checkYourAnswersLabel", yesOrNo(x), routes.EstablishedUnderScotsLawController.onPageLoad(CheckMode, draftId).url)
  }

  def trustResidentInUK: Option[AnswerRow] = userAnswers.get(TrustResidentInUKPage) map {
    x => AnswerRow("trustResidentInUK.checkYourAnswersLabel", yesOrNo(x), routes.TrustResidentInUKController.onPageLoad(CheckMode, draftId).url)
  }

  def countryAdministeringTrust: Option[AnswerRow] = userAnswers.get(CountryAdministeringTrustPage) map {
    x => AnswerRow("countryAdministeringTrust.checkYourAnswersLabel", escape(country(x, countryOptions)), routes.CountryAdministeringTrustController.onPageLoad(CheckMode, draftId).url)
  }

  def administrationInsideUK: Option[AnswerRow] = userAnswers.get(AdministrationInsideUKPage) map {
    x => AnswerRow("administrationInsideUK.checkYourAnswersLabel", yesOrNo(x), routes.AdministrationInsideUKController.onPageLoad(CheckMode, draftId).url)
  }

  def countryGoverningTrust: Option[AnswerRow] = userAnswers.get(CountryGoverningTrustPage) map {
    x => AnswerRow("countryGoverningTrust.checkYourAnswersLabel", escape(country(x, countryOptions)), routes.CountryGoverningTrustController.onPageLoad(CheckMode, draftId).url)
  }

  def governedInsideTheUK: Option[AnswerRow] = userAnswers.get(GovernedInsideTheUKPage) map {
    x => AnswerRow("governedInsideTheUK.checkYourAnswersLabel", yesOrNo(x), routes.GovernedInsideTheUKController.onPageLoad(CheckMode, draftId).url)
  }

  def trustName: Option[AnswerRow] = userAnswers.get(TrustNamePage) map {
    x => AnswerRow("trustName.checkYourAnswersLabel", escape(x), routes.TrustNameController.onPageLoad(CheckMode, draftId).url)
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
