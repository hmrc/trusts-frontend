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

import java.time.{LocalDate, LocalDateTime, ZoneOffset}

import models.AddAssets.NoComplete
import models.Status.Completed
import models.{AddABeneficiary, AddATrustee, FullName, IndividualOrBusiness, Status, UKAddress, UserAnswers, WhatKindOfAsset}
import pages.entitystatus._
import pages._
import utils.{CheckYourAnswersHelper, DateFormat}
import views.behaviours.ViewBehaviours
import views.html.ConfirmationAnswerPageView
import utils.countryOptions.CountryOptions

class ConfirmationAnswerPageViewSpec extends ViewBehaviours {
  val index = 0

  "ConfirmationAnswerPage view" must {

    val userAnswers =
      UserAnswers(userAnswersId)

        .set(TrustNamePage, "New Trust").success.value
        .set(WhenTrustSetupPage, LocalDate.of(2010, 10, 10)).success.value
        .set(GovernedInsideTheUKPage, true).success.value
        .set(AdministrationInsideUKPage, true).success.value
        .set(TrustResidentInUKPage, true).success.value
        .set(EstablishedUnderScotsLawPage, true).success.value
        .set(TrustResidentOffshorePage, false).success.value
        .set(TrustDetailsStatus, Completed).success.value

        .set(IndividualBeneficiaryNamePage(index), FullName("First", None, "Last")).success.value
        .set(IndividualBeneficiaryDateOfBirthYesNoPage(index),true).success.value
        .set(IndividualBeneficiaryDateOfBirthPage(index),LocalDate.now(ZoneOffset.UTC)).success.value
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
        .set(TrusteesNamePage(index), FullName("First", None, "Last")).success.value
        .set(TrusteesDateOfBirthPage(index), LocalDate.now(ZoneOffset.UTC)).success.value
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
        .set(SettlorDateOfDeathPage, LocalDate.now).success.value
        .set(SettlorDateOfBirthYesNoPage, true).success.value
        .set(SettlorsDateOfBirthPage, LocalDate.now).success.value
        .set(SettlorsNINoYesNoPage, true).success.value
        .set(SettlorNationalInsuranceNumberPage, "AB123456C").success.value
        .set(SettlorsLastKnownAddressYesNoPage, true).success.value
        .set(WasSettlorsAddressUKYesNoPage, true).success.value
        .set(SettlorsUKAddressPage, UKAddress("Line1", None, None, "Town", "NE1 1ZZ")).success.value
        .set(DeceasedSettlorStatus, Status.Completed).success.value

        .set(WhatKindOfAssetPage(index), WhatKindOfAsset.Money).success.value
        .set(AssetMoneyValuePage(index), "100").success.value
        .set(AssetStatus(index), Completed).success.value
        .set(AddAssetsPage, NoComplete).success.value

        .set(RegistrationTRNPage, "XNTRN000000001").success.value
        .set(RegistrationSubmissionDatePage, LocalDateTime.now).success.value

    val trnDateTime : String = DateFormat.formatDate(LocalDateTime.now, "d MMMM yyyy")
    val name = "First Last"

    val view = viewFor[ConfirmationAnswerPageView](Some(userAnswers))

    val countryOptions = injector.instanceOf[CountryOptions]

    val checkYourAnswersHelper = new CheckYourAnswersHelper(countryOptions)(userAnswers, canEdit = true)
    val trustDetails = checkYourAnswersHelper.trustDetails.getOrElse(Nil)
    val trustees = checkYourAnswersHelper.trustees.getOrElse(Nil)
    val settlors = checkYourAnswersHelper.settlors.getOrElse(Nil)
    val individualBeneficiaries = checkYourAnswersHelper.individualBeneficiaries.getOrElse(Nil)
    val individualBeneficiariesExist: Boolean = individualBeneficiaries.nonEmpty
    val classOfBeneficiaries = checkYourAnswersHelper.classOfBeneficiaries(individualBeneficiariesExist).getOrElse(Nil)
    val moneyAsset = checkYourAnswersHelper.moneyAsset.getOrElse(Nil)
    val sections =  trustDetails ++ settlors ++ trustees ++ individualBeneficiaries ++ classOfBeneficiaries ++ moneyAsset

    val applyView = view.apply(sections, "XNTRN000000001", trnDateTime)(fakeRequest, messages)

    behave like normalPage(applyView, "confirmationAnswerPage")

    val doc = asDocument(applyView)

    "assert correct number of headers and subheaders" in {
      val wrapper = doc.getElementById("wrapper")
      val headers = wrapper.getElementsByTag("h2")
      val subHeaders = wrapper.getElementsByTag("h3")


      assertContainsText(doc, messages("confirmationAnswerPage.paragraph1", "XNTRN000000001"))
      assertContainsText(doc, messages("confirmationAnswerPage.paragraph2", trnDateTime))
      headers.size mustBe 5
      subHeaders.size mustBe 4
    }

    "assert question labels for Trusts" in {
      assertContainsText(doc, messages("trustName.checkYourAnswersLabel"))
      assertContainsText(doc, messages("whenTrustSetup.checkYourAnswersLabel"))
      assertContainsText(doc, messages("governedInsideTheUK.checkYourAnswersLabel"))
      assertContainsText(doc, messages("administrationInsideUK.checkYourAnswersLabel"))
      assertContainsText(doc, messages("trustResidentInUK.checkYourAnswersLabel"))
      assertContainsText(doc, messages("establishedUnderScotsLaw.checkYourAnswersLabel"))
      assertContainsText(doc, messages("trustResidentOffshore.checkYourAnswersLabel"))
    }

    "assert question labels for Individual Beneficiaries" in {
      assertContainsText(doc, messages("individualBeneficiaryName.checkYourAnswersLabel"))
      assertContainsText(doc, messages("individualBeneficiaryDateOfBirthYesNo.checkYourAnswersLabel", name))
      assertContainsText(doc, messages("individualBeneficiaryDateOfBirth.checkYourAnswersLabel", name))
      assertContainsText(doc, messages("individualBeneficiaryIncomeYesNo.checkYourAnswersLabel", name))
      assertContainsText(doc, messages("individualBeneficiaryIncome.checkYourAnswersLabel", name))
      assertContainsText(doc, messages("individualBeneficiaryNationalInsuranceYesNo.checkYourAnswersLabel", name))
      assertContainsText(doc, messages("individualBeneficiaryNationalInsuranceNumber.checkYourAnswersLabel", name))

      assertContainsText(doc, messages("individualBeneficiaryAddressYesNo.checkYourAnswersLabel", name))
      assertContainsText(doc, messages("individualBeneficiaryAddressUK.checkYourAnswersLabel", name))
      assertContainsText(doc, messages("site.address.uk.checkYourAnswersLabel", name))
      assertContainsText(doc, messages("individualBeneficiaryVulnerableYesNo.checkYourAnswersLabel", name))
    }

    "assert question labels for Class Of Beneficiaries" in {
      assertContainsText(doc, messages("classBeneficiaryDescription.checkYourAnswersLabel"))
    }

    "assert question labels for Trustees" in {
      assertContainsText(doc, messages("isThisLeadTrustee.checkYourAnswersLabel"))
      assertContainsText(doc, messages("leadTrusteeIndividualOrBusiness.checkYourAnswersLabel"))
      assertContainsText(doc, messages("leadTrusteesName.checkYourAnswersLabel"))
      assertContainsText(doc, messages("trusteesDateOfBirth.checkYourAnswersLabel", name))
      assertContainsText(doc, messages("trusteeAUKCitizen.checkYourAnswersLabel", name))
      assertContainsText(doc, messages("trusteesNino.checkYourAnswersLabel", name))
      assertContainsText(doc, messages("telephoneNumber.checkYourAnswersLabel", name))
      assertContainsText(doc, messages("trusteeLiveInTheUK.checkYourAnswersLabel", name))
      assertContainsText(doc, messages("trusteesUkAddress.checkYourAnswersLabel", name))
    }

    "assert question labels for Settlors" in {
      assertContainsText(doc, messages("settlorsName.checkYourAnswersLabel"))
      assertContainsText(doc, messages("settlorDateOfBirthYesNo.checkYourAnswersLabel", name))
      assertContainsText(doc, messages("settlorsDateOfBirth.checkYourAnswersLabel", name))
      assertContainsText(doc, messages("settlorDateOfDeathYesNo.checkYourAnswersLabel", name))
      assertContainsText(doc, messages("settlorDateOfDeath.checkYourAnswersLabel", name))
      assertContainsText(doc, messages("settlorsNINoYesNo.checkYourAnswersLabel", name))
      assertContainsText(doc, messages("settlorNationalInsuranceNumber.checkYourAnswersLabel", name))
      assertContainsText(doc, messages("settlorsLastKnownAddressYesNo.checkYourAnswersLabel", name))
      assertContainsText(doc, messages("wasSettlorsAddressUKYesNo.checkYourAnswersLabel", name))
      assertContainsText(doc, messages("settlorsUKAddress.checkYourAnswersLabel", name))
    }

    "assert question labels for Money Assets" in {
      assertContainsText(doc, messages("assetMoneyValue.checkYourAnswersLabel"))
    }

  }
}
