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

package views

import java.time.{LocalDate, LocalDateTime}

import models.AddAssets.NoComplete
import models.Status.Completed
import models.{AddABeneficiary, AddATrustee, FullName, IndividualOrBusiness, Status, UKAddress, WhatKindOfAsset}
import pages._
import pages.deceased_settlor.{SettlorDateOfBirthYesNoPage, SettlorDateOfDeathPage, SettlorDateOfDeathYesNoPage, SettlorNationalInsuranceNumberPage, SettlorsDateOfBirthPage, SettlorsLastKnownAddressYesNoPage, SettlorsNINoYesNoPage, SettlorsNamePage, SettlorsUKAddressPage}
import pages.entitystatus._
import pages.property_or_land._
import pages.shares._
import utils.countryOptions.CountryOptions
import utils.{CheckYourAnswersHelper, DateFormat, PrintUserAnswersHelper, TestUserAnswers}
import views.behaviours.ViewBehaviours
import views.html.ConfirmationAnswerPageView
import utils.AccessibilityHelper._

class ConfirmationAnswerPageViewSpec extends ViewBehaviours {
  val index = 0

  "ConfirmationAnswerPage view" must {

    val userAnswers =
      TestUserAnswers.emptyUserAnswers
        .set(TrustNamePage, "New Trust").success.value
        .set(WhenTrustSetupPage, LocalDate.of(2010, 10, 10)).success.value
        .set(GovernedInsideTheUKPage, true).success.value
        .set(AdministrationInsideUKPage, true).success.value
        .set(TrustResidentInUKPage, true).success.value
        .set(EstablishedUnderScotsLawPage, true).success.value
        .set(TrustResidentOffshorePage, false).success.value
        .set(TrustDetailsStatus, Completed).success.value

        .set(IndividualBeneficiaryNamePage(index), FullName("BenFirst", None, "BenLast")).success.value
        .set(IndividualBeneficiaryDateOfBirthYesNoPage(index),true).success.value
        .set(IndividualBeneficiaryDateOfBirthPage(index),LocalDate.of(2010, 10, 10)).success.value
        .set(IndividualBeneficiaryIncomeYesNoPage(index),true).success.value
        .set(IndividualBeneficiaryIncomePage(index),"100").success.value
        .set(IndividualBeneficiaryNationalInsuranceYesNoPage(index),true).success.value
        .set(IndividualBeneficiaryNationalInsuranceNumberPage(index),"AB123456C").success.value
        .set(IndividualBeneficiaryAddressYesNoPage(index),true).success.value
        .set(IndividualBeneficiaryAddressUKYesNoPage(index),true).success.value
        .set(IndividualBeneficiaryAddressUKPage(index),UKAddress("Line1",None, None, "TownOrCity","NE62RT" )).success.value
        .set(IndividualBeneficiaryVulnerableYesNoPage(index),true).success.value
        .set(IndividualBeneficiaryStatus(index), Status.Completed).success.value

        .set(ClassBeneficiaryDescriptionPage(index),"Class of beneficary description").success.value
        .set(ClassBeneficiaryStatus(index), Status.Completed).success.value
        .set(AddABeneficiaryPage, AddABeneficiary.NoComplete).success.value

        .set(IsThisLeadTrusteePage(index), true).success.value
        .set(TrusteeIndividualOrBusinessPage(index), IndividualOrBusiness.Individual).success.value
        .set(TrusteesNamePage(index), FullName("TrusteeFirst", None, "TrusteeLast")).success.value
        .set(TrusteesDateOfBirthPage(index), LocalDate.of(2010, 10, 10)).success.value
        .set(TrusteeAUKCitizenPage(index), true).success.value
        .set(TrusteesNinoPage(index), "AB123456C").success.value
        .set(TelephoneNumberPage(index), "0191 1111111").success.value
        .set(TrusteeLiveInTheUKPage(index), true).success.value
        .set(TrusteesUkAddressPage(index), UKAddress("line1", Some("line2"), Some("line3"), "town or city", "AB1 1AB")).success.value
        .set(TrusteeStatus(index), Status.Completed).success.value
        .set(AddATrusteePage, AddATrustee.NoComplete).success.value

        .set(SetupAfterSettlorDiedPage, true).success.value
        .set(SettlorsNamePage, FullName("First", None, "Last")).success.value
        .set(SettlorDateOfDeathYesNoPage, true).success.value
        .set(SettlorDateOfDeathPage, LocalDate.of(2010, 10, 10)).success.value
        .set(SettlorDateOfBirthYesNoPage, true).success.value
        .set(SettlorsDateOfBirthPage, LocalDate.of(2010, 10, 10)).success.value
        .set(SettlorsNINoYesNoPage, true).success.value
        .set(SettlorNationalInsuranceNumberPage, "AB123456C").success.value
        .set(SettlorsLastKnownAddressYesNoPage, true).success.value
        .set(WasSettlorsAddressUKYesNoPage, true).success.value
        .set(SettlorsUKAddressPage, UKAddress("Line1", None, None, "Town", "NE1 1ZZ")).success.value
        .set(DeceasedSettlorStatus, Status.Completed).success.value

        .set(WhatKindOfAssetPage(index), WhatKindOfAsset.Money).success.value
        .set(AssetMoneyValuePage(index), "100").success.value
        .set(AssetStatus(index), Completed).success.value
        .set(WhatKindOfAssetPage(1), WhatKindOfAsset.Shares).success.value
        .set(SharesInAPortfolioPage(1), true).success.value
        .set(SharePortfolioNamePage(1), "Company").success.value
        .set(SharePortfolioOnStockExchangePage(1), true).success.value
        .set(SharePortfolioQuantityInTrustPage(1), "1234").success.value
        .set(SharePortfolioValueInTrustPage(1), "4000").success.value
        .set(AssetStatus(1), Completed).success.value
        .set(WhatKindOfAssetPage(2), WhatKindOfAsset.PropertyOrLand).success.value
        .set(PropertyOrLandAddressYesNoPage(2), false).success.value
        .set(PropertyOrLandDescriptionPage(2), "Town House").success.value
        .set(PropertyOrLandTotalValuePage(2), "10000").success.value
        .set(TrustOwnAllThePropertyOrLandPage(2), false).success.value
        .set(PropertyLandValueTrustPage(2), "10").success.value
        .set(AssetStatus(2), Completed).success.value
        .set(AddAssetsPage, NoComplete).success.value

        .set(RegistrationTRNPage, "XNTRN000000001").success.value
        .set(RegistrationSubmissionDatePage, LocalDateTime.of(2010, 10, 10, 13, 10, 10)).success.value

    val trnDateTime : String = DateFormat.formatDate(LocalDateTime.of(2010, 10, 10, 13, 10, 10), "d MMMM yyyy")
    val name = "First Last"
    val benName = "BenFirst BenLast"
    val trusteeName = "TrusteeFirst TrusteeLast"
    val yes = "Yes"
    val no = "No"

    val view = viewFor[ConfirmationAnswerPageView](Some(userAnswers))

    val countryOptions = injector.instanceOf[CountryOptions]

    val sections = new PrintUserAnswersHelper(countryOptions).summary(fakeDraftId, userAnswers)

    val applyView = view.apply(sections, formatTRN("XNTRN000000001"), trnDateTime)(fakeRequest, messages)

    behave like normalPage(applyView, "confirmationAnswerPage")

    val doc = asDocument(applyView)

    "assert header content" in {
      assertContainsText(doc, messages("confirmationAnswerPage.paragraph1", formatTRN("XNTRN000000001")))
      assertContainsText(doc, messages("confirmationAnswerPage.paragraph2", trnDateTime))
    }

    "assert correct number of headers and subheaders" in {
      val wrapper = doc.getElementById("wrapper")
      val headers = wrapper.getElementsByTag("h2")
      val subHeaders = wrapper.getElementsByTag("h3")

      headers.size mustBe 5
      subHeaders.size mustBe 6
    }

    "assert question labels for Trusts" in {
      assertContainsQuestionAnswerPair(doc, messages("trustName.checkYourAnswersLabel"), "New Trust")
      assertContainsQuestionAnswerPair(doc, messages("whenTrustSetup.checkYourAnswersLabel"), "10 October 2010")
      assertContainsQuestionAnswerPair(doc, messages("governedInsideTheUK.checkYourAnswersLabel"), yes)
      assertContainsQuestionAnswerPair(doc, messages("administrationInsideUK.checkYourAnswersLabel"), yes)
      assertContainsQuestionAnswerPair(doc, messages("trustResidentInUK.checkYourAnswersLabel"), yes)
      assertContainsQuestionAnswerPair(doc, messages("establishedUnderScotsLaw.checkYourAnswersLabel"), yes)
      assertContainsQuestionAnswerPair(doc, messages("trustResidentOffshore.checkYourAnswersLabel"), no)
    }

    "assert question labels for Individual Beneficiaries" in {
      assertContainsQuestionAnswerPair(doc, messages("individualBeneficiaryName.checkYourAnswersLabel"), benName)
      assertContainsQuestionAnswerPair(doc, messages("individualBeneficiaryDateOfBirthYesNo.checkYourAnswersLabel", benName), yes)
      assertContainsQuestionAnswerPair(doc, messages("individualBeneficiaryDateOfBirth.checkYourAnswersLabel", benName), "10 October 2010")
      assertContainsQuestionAnswerPair(doc, messages("individualBeneficiaryIncomeYesNo.checkYourAnswersLabel", benName), yes)
      assertContainsQuestionAnswerPair(doc, messages("individualBeneficiaryIncome.checkYourAnswersLabel", benName), "100")
      assertContainsQuestionAnswerPair(doc, messages("individualBeneficiaryNationalInsuranceYesNo.checkYourAnswersLabel", benName), yes)
      assertContainsQuestionAnswerPair(doc, messages("individualBeneficiaryNationalInsuranceNumber.checkYourAnswersLabel", benName), "AB 12 34 56 C")
      assertContainsQuestionAnswerPair(doc, messages("individualBeneficiaryAddressYesNo.checkYourAnswersLabel", benName), yes)
      assertContainsQuestionAnswerPair(doc, messages("individualBeneficiaryAddressUKYesNo.checkYourAnswersLabel", benName), yes)
      assertContainsText(doc, messages("individualBeneficiaryVulnerableYesNo.checkYourAnswersLabel", benName))
      assertContainsQuestionAnswerPair(doc, messages("individualBeneficiaryAddressUK.checkYourAnswersLabel", benName), "Line1 TownOrCity NE62RT")
    }

    "assert question labels for Class Of Beneficiaries" in {
      assertContainsQuestionAnswerPair(doc, messages("classBeneficiaryDescription.checkYourAnswersLabel"), "Class of beneficary description")
    }

    "assert question labels for Trustees" in {
      assertContainsQuestionAnswerPair(doc, messages("isThisLeadTrustee.checkYourAnswersLabel"), yes)
      assertContainsQuestionAnswerPair(doc, messages("leadTrusteeIndividualOrBusiness.checkYourAnswersLabel"), "Individual")
      assertContainsQuestionAnswerPair(doc, messages("leadTrusteesName.checkYourAnswersLabel"), trusteeName)
      assertContainsQuestionAnswerPair(doc, messages("trusteesDateOfBirth.checkYourAnswersLabel", trusteeName), "10 October 2010")
      assertContainsQuestionAnswerPair(doc, messages("trusteeAUKCitizen.checkYourAnswersLabel", trusteeName), yes)
      assertContainsQuestionAnswerPair(doc, messages("trusteesNino.checkYourAnswersLabel", trusteeName), "AB 12 34 56 C")
      assertContainsQuestionAnswerPair(doc, messages("telephoneNumber.checkYourAnswersLabel", trusteeName), "0191 1111111")
      assertContainsQuestionAnswerPair(doc, messages("trusteeLiveInTheUK.checkYourAnswersLabel", trusteeName), yes)
      assertContainsQuestionAnswerPair(doc, messages("trusteesUkAddress.checkYourAnswersLabel", trusteeName), "line1 line2 line3 town or city AB1 1AB")
    }

    "assert question labels for Settlors" in {
      assertContainsQuestionAnswerPair(doc, messages("setupAfterSettlorDied.checkYourAnswersLabel"), yes)
      assertContainsQuestionAnswerPair(doc, messages("settlorsName.checkYourAnswersLabel"), name)
      assertContainsQuestionAnswerPair(doc, messages("settlorDateOfBirthYesNo.checkYourAnswersLabel", name), yes)
      assertContainsQuestionAnswerPair(doc, messages("settlorsDateOfBirth.checkYourAnswersLabel", name), "10 October 2010")
      assertContainsQuestionAnswerPair(doc, messages("settlorDateOfDeathYesNo.checkYourAnswersLabel", name), yes)
      assertContainsQuestionAnswerPair(doc, messages("settlorDateOfDeath.checkYourAnswersLabel", name), "10 October 2010")
      assertContainsQuestionAnswerPair(doc, messages("settlorsNINoYesNo.checkYourAnswersLabel", name), yes)
      assertContainsQuestionAnswerPair(doc, messages("settlorNationalInsuranceNumber.checkYourAnswersLabel", name), "AB 12 34 56 C")
      assertContainsQuestionAnswerPair(doc, messages("settlorsLastKnownAddressYesNo.checkYourAnswersLabel", name), yes)
      assertContainsQuestionAnswerPair(doc, messages("wasSettlorsAddressUKYesNo.checkYourAnswersLabel", name), yes)
      assertContainsQuestionAnswerPair(doc, messages("settlorsUKAddress.checkYourAnswersLabel", name), "Line1 Town NE1 1ZZ")
    }

    "assert question labels for Money Assets" in {
      assertContainsQuestionAnswerPair(doc, messages("assetMoneyValue.checkYourAnswersLabel"), "£100")
    }

    "assert question labels for share assets" in {
      assertContainsQuestionAnswerPair(doc, messages("sharePortfolioName.checkYourAnswersLabel"), "Company")
      assertContainsQuestionAnswerPair(doc, messages("sharePortfolioOnStockExchange.checkYourAnswersLabel"), yes)
      assertContainsQuestionAnswerPair(doc, messages("sharePortfolioQuantityInTrust.checkYourAnswersLabel"), "1234")
      assertContainsQuestionAnswerPair(doc, messages("sharePortfolioValueInTrust.checkYourAnswersLabel"), "£4000")
      assertContainsQuestionAnswerPair(doc, messages("sharesInAPortfolio.checkYourAnswersLabel"), yes)
      assertContainsQuestionAnswerPair(doc, messages("sharesInAPortfolio.checkYourAnswersLabel"), yes)
    }

    "assert question labels for property or land assets" in {
      assertContainsQuestionAnswerPair(doc, messages("propertyOrLandAddressYesNo.checkYourAnswersLabel"), no)
      assertContainsQuestionAnswerPair(doc, messages("propertyOrLandDescription.checkYourAnswersLabel"), "Town House")
      assertContainsQuestionAnswerPair(doc, messages("propertyOrLandTotalValue.checkYourAnswersLabel"), "£10000")
      assertContainsQuestionAnswerPair(doc, messages("trustOwnAllThePropertyOrLand.checkYourAnswersLabel"), no)
      assertContainsQuestionAnswerPair(doc, messages("propertyLandValueTrust.checkYourAnswersLabel"), "£10")
    }

  }
}
