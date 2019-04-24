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
import models.{CheckMode, InternationalAddress, UKAddress, UserAnswers}
import pages._
import play.api.i18n.Messages
import play.twirl.api.{Html, HtmlFormat}
import utils.CheckYourAnswersHelper.{trusteeName, _}
import utils.countryOptions.CountryOptions
import viewmodels.AnswerRow

class CheckYourAnswersHelper @Inject()(countryOptions: CountryOptions)(userAnswers: UserAnswers)(implicit messages: Messages) {

  def individualBeneficiaryDateOfBirth: Option[AnswerRow] = userAnswers.get(IndividualBeneficiaryDateOfBirthPage) map {
    x =>
      AnswerRow(
        "individualBeneficiaryDateOfBirth.checkYourAnswersLabel",
        HtmlFormat.escape(x.format(dateFormatter)),
        routes.IndividualBeneficiaryDateOfBirthController.onPageLoad(CheckMode).url
      )
  }

  def individualBeneficiaryDateOfBirthYesNo: Option[AnswerRow] = userAnswers.get(IndividualBeneficiaryDateOfBirthYesNoPage) map {
    x =>
      AnswerRow(
        "individualBeneficiaryDateOfBirthYesNo.checkYourAnswersLabel",
        yesOrNo(x),
        routes.IndividualBeneficiaryDateOfBirthYesNoController.onPageLoad(CheckMode).url
      )
  }

  def individualBeneficiaryName(index: Int): Option[AnswerRow] = userAnswers.get(IndividualBeneficiaryNamePage(index)) map {
    x =>
      AnswerRow(
        "individualBeneficiaryName.checkYourAnswersLabel",
        HtmlFormat.escape(s"${x.firstName} ${x.middleName.getOrElse("")} ${x.lastName}"),
        routes.IndividualBeneficiaryNameController.onPageLoad(CheckMode, index).url
      )
  }

  def wasSettlorsAddressUKYesNo: Option[AnswerRow] = userAnswers.get(WasSettlorsAddressUKYesNoPage) map {
    x =>
      AnswerRow(
        "wasSettlorsAddressUKYesNo.checkYourAnswersLabel",
        yesOrNo(x),
        routes.WasSettlorsAddressUKYesNoController.onPageLoad(CheckMode).url,
          deceasedSettlorName(userAnswers)
      )
  }

  def setupAfterSettlorDied: Option[AnswerRow] = userAnswers.get(SetupAfterSettlorDiedPage) map {
    x =>
      AnswerRow(
        "setupAfterSettlorDied.checkYourAnswersLabel",
        yesOrNo(x),
        routes.SetupAfterSettlorDiedController.onPageLoad(CheckMode).url
      )
  }

  def settlorsUKAddress: Option[AnswerRow] = userAnswers.get(SettlorsUKAddressPage) map {
    x =>
      AnswerRow(
        "settlorsUKAddress.checkYourAnswersLabel",
        ukAddress(x),
        routes.SettlorsUKAddressController.onPageLoad(CheckMode).url,
        deceasedSettlorName(userAnswers)
      )
  }

  def settlorsNINoYesNo: Option[AnswerRow] = userAnswers.get(SettlorsNINoYesNoPage) map {
    x =>
      AnswerRow(
        "settlorsNINoYesNo.checkYourAnswersLabel",
        yesOrNo(x),
        routes.SettlorsNINoYesNoController.onPageLoad(CheckMode).url,
        deceasedSettlorName(userAnswers)
      )
  }

  def settlorsName: Option[AnswerRow] = userAnswers.get(SettlorsNamePage) map {
    x =>
      AnswerRow(
        "settlorsName.checkYourAnswersLabel",
        HtmlFormat.escape(s"${x.firstName} ${x.lastName}"),
        routes.SettlorsNameController.onPageLoad(CheckMode).url
      )
  }

  def settlorsLastKnownAddressYesNo: Option[AnswerRow] = userAnswers.get(SettlorsLastKnownAddressYesNoPage) map {
    x =>
      AnswerRow(
        "settlorsLastKnownAddressYesNo.checkYourAnswersLabel",
        yesOrNo(x),
        routes.SettlorsLastKnownAddressYesNoController.onPageLoad(CheckMode).url,
        deceasedSettlorName(userAnswers)
      )
  }

  def settlorsInternationalAddress: Option[AnswerRow] = userAnswers.get(SettlorsInternationalAddressPage) map {
    x =>
      AnswerRow(
        "settlorsInternationalAddress.checkYourAnswersLabel",
        internationalAddress(x, countryOptions),
        routes.SettlorsInternationalAddressController.onPageLoad(CheckMode).url,
        deceasedSettlorName(userAnswers)
      )
  }

  def settlorsDateOfBirth: Option[AnswerRow] = userAnswers.get(SettlorsDateOfBirthPage) map {
    x =>
      AnswerRow(
        "settlorsDateOfBirth.checkYourAnswersLabel",
        HtmlFormat.escape(x.format(dateFormatter)),
        routes.SettlorsDateOfBirthController.onPageLoad(CheckMode).url,
        deceasedSettlorName(userAnswers)
      )
  }

  def settlorNationalInsuranceNumber: Option[AnswerRow] = userAnswers.get(SettlorNationalInsuranceNumberPage) map {
    x =>
      AnswerRow(
        "settlorNationalInsuranceNumber.checkYourAnswersLabel",
        HtmlFormat.escape(x),
        routes.SettlorNationalInsuranceNumberController.onPageLoad(CheckMode).url,
        deceasedSettlorName(userAnswers)
      )
  }

  def settlorDateOfDeathYesNo: Option[AnswerRow] = userAnswers.get(SettlorDateOfDeathYesNoPage) map {
    x =>
      AnswerRow(
        "settlorDateOfDeathYesNo.checkYourAnswersLabel",
        yesOrNo(x),
        routes.SettlorDateOfDeathYesNoController.onPageLoad(CheckMode).url,
        deceasedSettlorName(userAnswers)
      )
  }

  def settlorDateOfDeath: Option[AnswerRow] = userAnswers.get(SettlorDateOfDeathPage) map {
    x =>
      AnswerRow(
        "settlorDateOfDeath.checkYourAnswersLabel",
        HtmlFormat.escape(x.format(dateFormatter)),
        routes.SettlorDateOfDeathController.onPageLoad(CheckMode).url,
        deceasedSettlorName(userAnswers)
      )
  }

  def settlorDateOfBirthYesNo: Option[AnswerRow] = userAnswers.get(SettlorDateOfBirthYesNoPage) map {
    x =>
      AnswerRow(
        "settlorDateOfBirthYesNo.checkYourAnswersLabel",
        yesOrNo(x),
        routes.SettlorDateOfBirthYesNoController.onPageLoad(CheckMode).url,
        deceasedSettlorName(userAnswers)
      )
  }

  def assetMoneyValue(index: Int): Option[AnswerRow] = userAnswers.get(AssetMoneyValuePage(index)) map {
    x =>
      AnswerRow(
        "assetMoneyValue.checkYourAnswersLabel",
        HtmlFormat.escape(x),
        routes.AssetMoneyValueController.onPageLoad(CheckMode, index).url
      )
  }

  def whatKindOfAsset(index: Int): Option[AnswerRow] = userAnswers.get(WhatKindOfAssetPage(index)) map {
    x =>
      AnswerRow(
        "whatKindOfAsset.checkYourAnswersLabel",
        HtmlFormat.escape(messages(s"whatKindOfAsset.$x")),
        routes.WhatKindOfAssetController.onPageLoad(CheckMode, index).url
      )
  }

  def agentInternalReference: Option[AnswerRow] = userAnswers.get(AgentInternalReferencePage) map {
    x =>
      AnswerRow(
        "agentInternalReference.checkYourAnswersLabel",
        HtmlFormat.escape(x),
        routes.AgentInternalReferenceController.onPageLoad(CheckMode).url
      )
  }

  def agenciesTelephoneNumber: Option[AnswerRow] = userAnswers.get(AgentTelephoneNumberPage) map {
    x =>
      AnswerRow(
        "agentTelephoneNumber.checkYourAnswersLabel",
        HtmlFormat.escape(x),
        routes.AgentTelephoneNumberController.onPageLoad(CheckMode).url
      )
  }

  def trusteesNino(index: Int): Option[AnswerRow] = userAnswers.get(TrusteesNinoPage(index)) map {
    x =>
      AnswerRow(
        "trusteesNino.checkYourAnswersLabel",
        HtmlFormat.escape(x),
        routes.TrusteesNinoController.onPageLoad(CheckMode, index).url,
        trusteeName(index, userAnswers)
      )
  }

  def trusteeLiveInTheUK(index : Int): Option[AnswerRow] = userAnswers.get(TrusteeLiveInTheUKPage(index)) map {
    x =>
      AnswerRow(
        "trusteeLiveInTheUK.checkYourAnswersLabel",
        yesOrNo(x),
        routes.TrusteeLiveInTheUKController.onPageLoad(CheckMode, index).url,
        trusteeName(index, userAnswers)
      )
  }

  def trusteesUkAddress(index: Int): Option[AnswerRow] = userAnswers.get(TrusteesUkAddressPage(index)) map {
    x =>
      AnswerRow(
        "trusteesUkAddress.checkYourAnswersLabel",
        ukAddress(x),
        routes.TrusteesUkAddressController.onPageLoad(CheckMode, index).url,
        trusteeName(index, userAnswers)
      )
  }

  def trusteesDateOfBirth(index : Int): Option[AnswerRow] = userAnswers.get(TrusteesDateOfBirthPage(index)) map {
    x =>
      AnswerRow(
        "trusteesDateOfBirth.checkYourAnswersLabel",
        HtmlFormat.escape(x.format(dateFormatter)),
        routes.TrusteesDateOfBirthController.onPageLoad(CheckMode, index).url,
        trusteeName(index, userAnswers)
      )
  }

  def telephoneNumber(index : Int): Option[AnswerRow] = userAnswers.get(TelephoneNumberPage(index)) map {
    x =>
      AnswerRow(
        "telephoneNumber.checkYourAnswersLabel",
        HtmlFormat.escape(x),
        routes.TelephoneNumberController.onPageLoad(CheckMode, index).url,
        trusteeName(index, userAnswers)
      )
  }

  def trusteeAUKCitizen(index : Int): Option[AnswerRow] = userAnswers.get(TrusteeAUKCitizenPage(index)) map {
    x =>
      AnswerRow(
        "trusteeAUKCitizen.checkYourAnswersLabel",
        yesOrNo(x),
        routes.TrusteeAUKCitizenController.onPageLoad(CheckMode,index).url,
        trusteeName(index, userAnswers)
      )
  }


  def trusteeFullName(index : Int, messagePrefix: String): Option[AnswerRow] = userAnswers.get(TrusteesNamePage(index)) map {
    x => AnswerRow(
      s"$messagePrefix.checkYourAnswersLabel",
      HtmlFormat.escape(s"${x.firstName} ${x.middleName.getOrElse("")} ${x.lastName}"),
      routes.TrusteesNameController.onPageLoad(CheckMode, index).url
    )
  }

  def trusteeIndividualOrBusiness(index : Int, messagePrefix: String): Option[AnswerRow] = userAnswers.get(TrusteeIndividualOrBusinessPage(index)) map {
    x =>
      AnswerRow(
        s"$messagePrefix.checkYourAnswersLabel",
        HtmlFormat.escape(messages(s"individualOrBusiness.$x")),
        routes.TrusteeIndividualOrBusinessController.onPageLoad(CheckMode, index).url
      )
  }

  def isThisLeadTrustee(index: Int): Option[AnswerRow] = userAnswers.get(IsThisLeadTrusteePage(index)) map {
    x =>
      AnswerRow(
        "isThisLeadTrustee.checkYourAnswersLabel",
        yesOrNo(x),
        routes.IsThisLeadTrusteeController.onPageLoad(CheckMode, index).url
      )
  }

  def postcodeForTheTrust: Option[AnswerRow] = userAnswers.get(PostcodeForTheTrustPage) map {
    x =>
      AnswerRow(
        "postcodeForTheTrust.checkYourAnswersLabel",
        HtmlFormat.escape(x),
        routes.PostcodeForTheTrustController.onPageLoad(CheckMode).url
      )
  }

  def whatIsTheUTR: Option[AnswerRow] = userAnswers.get(WhatIsTheUTRPage) map {
    x =>
      AnswerRow(
        "whatIsTheUTR.checkYourAnswersLabel",
        HtmlFormat.escape(x),
        routes.WhatIsTheUTRController.onPageLoad(CheckMode).url
      )
  }

  def trustHaveAUTR: Option[AnswerRow] = userAnswers.get(TrustHaveAUTRPage) map {
    x =>
      AnswerRow(
        "trustHaveAUTR.checkYourAnswersLabel",
        yesOrNo(x),
        routes.TrustHaveAUTRController.onPageLoad(CheckMode).url
      )
  }

  def trustRegisteredOnline: Option[AnswerRow] = userAnswers.get(TrustRegisteredOnlinePage) map {
    x =>
      AnswerRow(
        "trustRegisteredOnline.checkYourAnswersLabel",
        yesOrNo(x),
        routes.TrustRegisteredOnlineController.onPageLoad(CheckMode).url
      )
  }

  def whenTrustSetup: Option[AnswerRow] = userAnswers.get(WhenTrustSetupPage) map {
    x =>
      AnswerRow(
        "whenTrustSetup.checkYourAnswersLabel",
        HtmlFormat.escape(x.format(dateFormatter)),
        routes.WhenTrustSetupController.onPageLoad(CheckMode).url
      )
  }

  def agentOtherThanBarrister: Option[AnswerRow] = userAnswers.get(AgentOtherThanBarristerPage) map {
    x => AnswerRow("agentOtherThanBarrister.checkYourAnswersLabel", yesOrNo(x), routes.AgentOtherThanBarristerController.onPageLoad(CheckMode).url)
  }

  def inheritanceTaxAct: Option[AnswerRow] = userAnswers.get(InheritanceTaxActPage) map {
    x => AnswerRow("inheritanceTaxAct.checkYourAnswersLabel", yesOrNo(x), routes.InheritanceTaxActController.onPageLoad(CheckMode).url)
  }

  def nonresidentType: Option[AnswerRow] = userAnswers.get(NonResidentTypePage) map {
    x => AnswerRow("nonresidentType.checkYourAnswersLabel", answer("nonresidentType", x), routes.NonResidentTypeController.onPageLoad(CheckMode).url)
  }

  def trustPreviouslyResident: Option[AnswerRow] = userAnswers.get(TrustPreviouslyResidentPage) map {
    x => AnswerRow("trustPreviouslyResident.checkYourAnswersLabel", escape(country(x, countryOptions)), routes.TrustPreviouslyResidentController.onPageLoad(CheckMode).url)
  }

  def trustResidentOffshore: Option[AnswerRow] = userAnswers.get(TrustResidentOffshorePage) map {
    x => AnswerRow("trustResidentOffshore.checkYourAnswersLabel", yesOrNo(x), routes.TrustResidentOffshoreController.onPageLoad(CheckMode).url)
  }

  def registeringTrustFor5A: Option[AnswerRow] = userAnswers.get(RegisteringTrustFor5APage) map {
    x => AnswerRow("registeringTrustFor5A.checkYourAnswersLabel", yesOrNo(x), routes.RegisteringTrustFor5AController.onPageLoad(CheckMode).url)
  }

  def establishedUnderScotsLaw: Option[AnswerRow] = userAnswers.get(EstablishedUnderScotsLawPage) map {
    x => AnswerRow("establishedUnderScotsLaw.checkYourAnswersLabel", yesOrNo(x), routes.EstablishedUnderScotsLawController.onPageLoad(CheckMode).url)
  }

  def trustResidentInUK: Option[AnswerRow] = userAnswers.get(TrustResidentInUKPage) map {
    x => AnswerRow("trustResidentInUK.checkYourAnswersLabel", yesOrNo(x), routes.TrustResidentInUKController.onPageLoad(CheckMode).url)
  }

  def countryAdministeringTrust: Option[AnswerRow] = userAnswers.get(CountryAdministeringTrustPage) map {
    x => AnswerRow("countryAdministeringTrust.checkYourAnswersLabel", escape(country(x, countryOptions)), routes.CountryAdministeringTrustController.onPageLoad(CheckMode).url)
  }

  def administrationInsideUK: Option[AnswerRow] = userAnswers.get(AdministrationInsideUKPage) map {
    x => AnswerRow("administrationInsideUK.checkYourAnswersLabel", yesOrNo(x), routes.AdministrationInsideUKController.onPageLoad(CheckMode).url)
  }

  def countryGoverningTrust: Option[AnswerRow] = userAnswers.get(CountryGoverningTrustPage) map {
    x => AnswerRow("countryGoverningTrust.checkYourAnswersLabel", escape(country(x, countryOptions)), routes.CountryGoverningTrustController.onPageLoad(CheckMode).url)
  }

  def governedInsideTheUK: Option[AnswerRow] = userAnswers.get(GovernedInsideTheUKPage) map {
    x => AnswerRow("governedInsideTheUK.checkYourAnswersLabel", yesOrNo(x), routes.GovernedInsideTheUKController.onPageLoad(CheckMode).url)
  }

  def trustName: Option[AnswerRow] = userAnswers.get(TrustNamePage) map {
    x => AnswerRow("trustName.checkYourAnswersLabel", escape(x), routes.TrustNameController.onPageLoad(CheckMode).url)
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

  private def country(code : String, countryOptions: CountryOptions) : String =
    countryOptions.options.find(_.value.equals(code)).map(_.label).getOrElse("")

  private def trusteeName(index: Int, userAnswers: UserAnswers): String =
    userAnswers.get(TrusteesNamePage(index)).get.toString

  private def answer[T](key : String, answer: T)(implicit messages: Messages) : Html =
    HtmlFormat.escape(messages(s"$key.$answer"))

  private def escape(x : String) = HtmlFormat.escape(x)

  private def deceasedSettlorName(userAnswers: UserAnswers): String =
    userAnswers.get(SettlorsNamePage).get.toString

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
