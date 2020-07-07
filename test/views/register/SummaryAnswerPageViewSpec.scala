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

package views.register

import java.time.{LocalDate, LocalDateTime}

import javax.print.Doc
import models.core.pages.{FullName, IndividualOrBusiness, UKAddress}
import models.registration.pages.AddAssets.NoComplete
import models.registration.pages.Status.Completed
import models.registration.pages.TrusteesBasedInTheUK.UKBasedTrustees
import models.registration.pages._
import pages.entitystatus._
import pages.register._
import pages.register.asset.money.AssetMoneyValuePage
import pages.register.asset.property_or_land._
import pages.register.asset.shares._
import pages.register.asset.{AddAssetsPage, WhatKindOfAssetPage}
import pages.register.beneficiaries.individual._
import pages.register.beneficiaries.{AddABeneficiaryPage, ClassBeneficiaryDescriptionPage}
import pages.register.settlors.SetUpAfterSettlorDiedYesNoPage
import pages.register.settlors.deceased_settlor._
import pages.register.trust_details._
import pages.register.trustees._
import pages.register.trustees.individual._
import uk.gov.hmrc.http.HeaderCarrier
import utils.TestUserAnswers
import utils.countryOptions.CountryOptions
import utils.print.register.PrintUserAnswersHelper
import views.behaviours.ViewBehaviours
import views.html.register.SummaryAnswerPageView

class SummaryAnswerPageViewSpec extends ViewBehaviours {
  val index = 0

  "SummaryAnswerPage view" must {

    val userAnswers =
      TestUserAnswers.emptyUserAnswers
        .set(TrustNamePage, "New Trust").success.value
        .set(WhenTrustSetupPage, LocalDate.of(2010, 10, 10)).success.value
        .set(GovernedInsideTheUKPage, true).success.value
        .set(AdministrationInsideUKPage, true).success.value
        .set(TrusteesBasedInTheUKPage, UKBasedTrustees).success.value
        .set(EstablishedUnderScotsLawPage, true).success.value
        .set(TrustResidentOffshorePage, false).success.value
        .set(TrustDetailsStatus, Completed).success.value

        .set(IndividualBeneficiaryNamePage(index), FullName("BenFirst", None, "BenLast")).success.value
        .set(IndividualBeneficiaryDateOfBirthYesNoPage(index), true).success.value
        .set(IndividualBeneficiaryDateOfBirthPage(index), LocalDate.of(2010, 10, 10)).success.value
        .set(IndividualBeneficiaryIncomeYesNoPage(index), true).success.value
        .set(IndividualBeneficiaryIncomePage(index), "100").success.value
        .set(IndividualBeneficiaryNationalInsuranceYesNoPage(index), true).success.value
        .set(IndividualBeneficiaryNationalInsuranceNumberPage(index), "AB123456C").success.value
        .set(IndividualBeneficiaryAddressYesNoPage(index), true).success.value
        .set(IndividualBeneficiaryAddressUKYesNoPage(index), true).success.value
        .set(IndividualBeneficiaryAddressUKPage(index), UKAddress("Line1", "Line2", None, None, "NE62RT")).success.value
        .set(IndividualBeneficiaryVulnerableYesNoPage(index), true).success.value
        .set(IndividualBeneficiaryStatus(index), Status.Completed).success.value

        .set(ClassBeneficiaryDescriptionPage(index), "Class of beneficary description").success.value
        .set(ClassBeneficiaryStatus(index), Status.Completed).success.value
        .set(AddABeneficiaryPage, AddABeneficiary.NoComplete).success.value

        .set(IsThisLeadTrusteePage(index), true).success.value
        .set(TrusteeIndividualOrBusinessPage(index), IndividualOrBusiness.Individual).success.value
        .set(TrusteesNamePage(index), FullName("TrusteeFirst", None, "TrusteeLast")).success.value
        .set(TrusteesDateOfBirthPage(index), LocalDate.of(2010, 10, 10)).success.value
        .set(TrusteeAUKCitizenPage(index), true).success.value
        .set(TrusteesNinoPage(index), "AB123456C").success.value
        .set(TelephoneNumberPage(index), "0191 1111111").success.value
        .set(TrusteeAddressInTheUKPage(index), true).success.value
        .set(TrusteesUkAddressPage(index), UKAddress("line1", "line2", Some("line3"), Some("line4"), "AB1 1AB")).success.value
        .set(TrusteeStatus(index), Status.Completed).success.value
        .set(AddATrusteePage, AddATrustee.NoComplete).success.value

        .set(SetUpAfterSettlorDiedYesNoPage, true).success.value
        .set(SettlorsNamePage, FullName("First", None, "Last")).success.value
        .set(SettlorDateOfDeathYesNoPage, true).success.value
        .set(SettlorDateOfDeathPage, LocalDate.of(2010, 10, 10)).success.value
        .set(SettlorDateOfBirthYesNoPage, true).success.value
        .set(SettlorsDateOfBirthPage, LocalDate.of(2010, 10, 10)).success.value
        .set(SettlorsNationalInsuranceYesNoPage, true).success.value
        .set(SettlorNationalInsuranceNumberPage, "AB123456C").success.value
        .set(SettlorsLastKnownAddressYesNoPage, true).success.value
        .set(WasSettlorsAddressUKYesNoPage, true).success.value
        .set(SettlorsUKAddressPage, UKAddress("Line1", "Line2", None, None, "NE1 1ZZ")).success.value
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

    val name = "First Last"
    val benName = "BenFirst BenLast"
    val trusteeName = "TrusteeFirst TrusteeLast"
    val yes = "Yes"
    val no = "No"

    val view = viewFor[SummaryAnswerPageView](Some(userAnswers))

    val app = applicationBuilder().build()

    val helper = app.injector.instanceOf[PrintUserAnswersHelper]

    implicit val hc: HeaderCarrier = HeaderCarrier()

    val summary = helper.summary(fakeDraftId, userAnswers)

    val orgDoc = {
      summary.map {
        sections =>
          val applyOrganisationView = view.apply(sections, isAgent = false, "")(fakeRequest, messages)

          behave like normalPage(applyOrganisationView, "summaryAnswerPage", "paragraph1", "paragraph2")

          asDocument(applyOrganisationView)
      }
    }

    val agentDoc = {
      summary.map {
        sections =>
          val applyAgentView = view.apply(sections, isAgent = true, "agentClientReference")(fakeRequest, messages)

          asDocument(applyAgentView)
      }
    }

    "assert header content for Agent user" in {
      agentDoc.map(assertContainsText(_, messages("answerPage.agentClientRef", "agentClientReference")))
    }

    "assert correct number of headers and subheaders for Agent user" in {
      agentDoc.map {
        doc =>
          val wrapper = doc.getElementById("wrapper")
          val headers = wrapper.getElementsByTag("h2")
          val subHeaders = wrapper.getElementsByTag("h3")

          headers.size mustBe 5
          subHeaders.size mustBe 3
      }
    }

    "assert correct number of headers and subheaders for Organisation user" in {
      orgDoc.map {
        doc =>
          val wrapper = doc.getElementById("wrapper")
          val headers = wrapper.getElementsByTag("h2")
          val subHeaders = wrapper.getElementsByTag("h3")

          headers.size mustBe 4
          subHeaders.size mustBe 3
      }
    }

    "assert question labels for Trust details" in {
      orgDoc.map {
        doc =>
          assertContainsQuestionAnswerPair(doc, messages("trustName.checkYourAnswersLabel"), "New Trust")
          assertContainsQuestionAnswerPair(doc, messages("whenTrustSetup.checkYourAnswersLabel"), "10 October 2010")
      }
    }

    "assert question labels for Individual Beneficiaries" in {
      orgDoc.map {
        doc =>
          assertContainsQuestionAnswerPair(doc, messages("individualBeneficiaryName.checkYourAnswersLabel"), benName)
          assertContainsQuestionAnswerPair(doc, messages("individualBeneficiaryDateOfBirthYesNo.checkYourAnswersLabel", benName), yes)
          assertContainsQuestionAnswerPair(doc, messages("individualBeneficiaryDateOfBirth.checkYourAnswersLabel", benName), "10 October 2010")
          assertContainsQuestionAnswerPair(doc, messages("individualBeneficiaryIncomeYesNo.checkYourAnswersLabel", benName), yes)
          assertContainsQuestionAnswerPair(doc, messages("individualBeneficiaryIncome.checkYourAnswersLabel", benName), "100%")
          assertContainsQuestionAnswerPair(doc, messages("individualBeneficiaryNationalInsuranceYesNo.checkYourAnswersLabel", benName), yes)
          assertContainsQuestionAnswerPair(doc, messages("individualBeneficiaryNationalInsuranceNumber.checkYourAnswersLabel", benName), "AB 12 34 56 C")
          assertContainsQuestionAnswerPair(doc, messages("individualBeneficiaryAddressYesNo.checkYourAnswersLabel", benName), yes)
          assertContainsQuestionAnswerPair(doc, messages("individualBeneficiaryAddressUKYesNo.checkYourAnswersLabel", benName), yes)
          assertContainsText(doc, messages("individualBeneficiaryVulnerableYesNo.checkYourAnswersLabel", benName))
          assertContainsQuestionAnswerPair(doc, messages("individualBeneficiaryAddressUK.checkYourAnswersLabel", benName), "Line1 Line2 NE62RT")
      }
    }

    "assert question labels for Class Of Beneficiaries" in {
      orgDoc.map {
        doc =>
          assertContainsQuestionAnswerPair(doc, messages("classBeneficiaryDescription.checkYourAnswersLabel"), "Class of beneficary description")
      }
    }

    "assert question labels for Trustees" in {
      orgDoc.map {
        doc =>
          assertContainsQuestionAnswerPair(doc, messages("leadTrusteeIndividualOrBusiness.checkYourAnswersLabel"), "Individual")
          assertContainsQuestionAnswerPair(doc, messages("leadTrusteesName.checkYourAnswersLabel"), trusteeName)
          assertContainsQuestionAnswerPair(doc, messages("trusteesDateOfBirth.checkYourAnswersLabel", trusteeName), "10 October 2010")
          assertContainsQuestionAnswerPair(doc, messages("trusteeAUKCitizen.checkYourAnswersLabel", trusteeName), yes)
          assertContainsQuestionAnswerPair(doc, messages("trusteesNino.checkYourAnswersLabel", trusteeName), "AB 12 34 56 C")
          assertContainsQuestionAnswerPair(doc, messages("telephoneNumber.checkYourAnswersLabel", trusteeName), "0191 1111111")
          assertContainsQuestionAnswerPair(doc, messages("trusteeLiveInTheUK.checkYourAnswersLabel", trusteeName), yes)
          assertContainsQuestionAnswerPair(doc, messages("trusteesUkAddress.checkYourAnswersLabel", trusteeName), "line1 line2 line3 line4 AB1 1AB")
      }
    }

    "assert question labels for Settlors" in {
      orgDoc.map {
        doc =>
          assertContainsQuestionAnswerPair(doc, messages("setUpAfterSettlorDied.checkYourAnswersLabel"), yes)
          assertContainsQuestionAnswerPair(doc, messages("settlorsName.checkYourAnswersLabel"), name)
          assertContainsQuestionAnswerPair(doc, messages("settlorDateOfBirthYesNo.checkYourAnswersLabel", name), yes)
          assertContainsQuestionAnswerPair(doc, messages("settlorsDateOfBirth.checkYourAnswersLabel", name), "10 October 2010")
          assertContainsQuestionAnswerPair(doc, messages("settlorDateOfDeathYesNo.checkYourAnswersLabel", name), yes)
          assertContainsQuestionAnswerPair(doc, messages("settlorDateOfDeath.checkYourAnswersLabel", name), "10 October 2010")
          assertContainsQuestionAnswerPair(doc, messages("settlorsNationalInsuranceYesNo.checkYourAnswersLabel", name), yes)
          assertContainsQuestionAnswerPair(doc, messages("settlorNationalInsuranceNumber.checkYourAnswersLabel", name), "AB 12 34 56 C")
          assertContainsQuestionAnswerPair(doc, messages("settlorsLastKnownAddressYesNo.checkYourAnswersLabel", name), yes)
          assertContainsQuestionAnswerPair(doc, messages("wasSettlorsAddressUKYesNo.checkYourAnswersLabel", name), yes)
          assertContainsQuestionAnswerPair(doc, messages("settlorsUKAddress.checkYourAnswersLabel", name), "Line1 Line2 NE1 1ZZ")
      }
    }
  }
}
