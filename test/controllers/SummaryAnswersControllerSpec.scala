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

package controllers

import java.time.{LocalDate, ZoneOffset}

import base.SpecBase
import models.AddAssets.NoComplete
import models.Status.Completed
import models.{AddABeneficiary, AddATrustee, FullName, IndividualOrBusiness, InternationalAddress, Status, UKAddress, UserAnswers, WhatKindOfAsset}
import navigation.TaskListNavigator
import pages._
import pages.entitystatus._
import play.api.i18n.Messages
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.CheckYourAnswersHelper
import utils.countryOptions.CountryOptions
import viewmodels.AnswerSection
import views.html.SummaryAnswerPageView

class SummaryAnswersControllerSpec extends SpecBase {

  val index = 0

  "SummaryAnswersController Controller" must {

    "return OK and the correct view for a GET when tasklist completed" in {

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

          .set(IndividualBeneficiaryNamePage(index), FullName("first name", None, "last name")).success.value
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
          .set(TrusteesNamePage(index), FullName("First", None, "Trustee")).success.value
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


      val countryOptions = injector.instanceOf[CountryOptions]
      val checkYourAnswersHelper = new CheckYourAnswersHelper(countryOptions)(userAnswers, canEdit = true)
      val leadTrusteeIndividualOrBusinessMessagePrefix = "leadTrusteeIndividualOrBusiness"
      val leadTrusteeFullNameMessagePrefix = "leadTrusteesName"

      val expectedSections = Seq(
        AnswerSection(
          None,
          Seq(
            checkYourAnswersHelper.trustName.value,
            checkYourAnswersHelper.whenTrustSetup.value,
            checkYourAnswersHelper.governedInsideTheUK.value,
            checkYourAnswersHelper.administrationInsideUK.value,
            checkYourAnswersHelper.trustResidentInUK.value,
            checkYourAnswersHelper.establishedUnderScotsLaw.value,
            checkYourAnswersHelper.trustResidentOffshore.value
          ),
          Some(Messages("answerPage.section.trustsDetails.heading"))
        ),
        AnswerSection(
          None,
          Seq(checkYourAnswersHelper.setupAfterSettlorDied.value,
            checkYourAnswersHelper.settlorsName.value,
            checkYourAnswersHelper.settlorDateOfDeathYesNo.value,
            checkYourAnswersHelper.settlorDateOfDeath.value,
            checkYourAnswersHelper.settlorDateOfBirthYesNo.value,
            checkYourAnswersHelper.settlorsDateOfBirth.value,
            checkYourAnswersHelper.settlorsNINoYesNo.value,
            checkYourAnswersHelper.settlorNationalInsuranceNumber.value,
            checkYourAnswersHelper.settlorsLastKnownAddressYesNo.value,
            checkYourAnswersHelper.wasSettlorsAddressUKYesNo.value,
            checkYourAnswersHelper.settlorsUKAddress.value
          ),
          Some(Messages("answerPage.section.settlors.heading"))
        ),
        AnswerSection(
          Some(Messages("answerPage.section.trustee.subheading") + " " + (index + 1)),
          Seq(
            checkYourAnswersHelper.isThisLeadTrustee(index).value,
            checkYourAnswersHelper.trusteeIndividualOrBusiness(index, leadTrusteeIndividualOrBusinessMessagePrefix).value,
            checkYourAnswersHelper.trusteeFullName(index, leadTrusteeFullNameMessagePrefix).value,
            checkYourAnswersHelper.trusteesDateOfBirth(index).value,
            checkYourAnswersHelper.trusteeAUKCitizen(index).value,
            checkYourAnswersHelper.trusteesNino(index).value,
            checkYourAnswersHelper.trusteeLiveInTheUK(index).value,
            checkYourAnswersHelper.trusteesUkAddress(index).value,
            checkYourAnswersHelper.telephoneNumber(index).value
          ),
          Some(Messages("answerPage.section.trustees.heading"))
        ),
        AnswerSection(
          Some(Messages("answerPage.section.individualBeneficiary.subheading") + " " + (index + 1)),
          Seq(
            checkYourAnswersHelper.individualBeneficiaryName(index).value,
            checkYourAnswersHelper.individualBeneficiaryDateOfBirthYesNo(index).value,
            checkYourAnswersHelper.individualBeneficiaryDateOfBirth(index).value,
            checkYourAnswersHelper.individualBeneficiaryIncomeYesNo(index).value,
            checkYourAnswersHelper.individualBeneficiaryIncome(index).value,
            checkYourAnswersHelper.individualBeneficiaryNationalInsuranceYesNo(index).value,
            checkYourAnswersHelper.individualBeneficiaryNationalInsuranceNumber(index).value,
            checkYourAnswersHelper.individualBeneficiaryAddressYesNo(index).value,
            checkYourAnswersHelper.individualBeneficiaryAddressUKYesNo(index).value,
            checkYourAnswersHelper.individualBeneficiaryAddressUK(index).value,
            checkYourAnswersHelper.individualBeneficiaryVulnerableYesNo(index).value
          ),
          Some(Messages("answerPage.section.beneficiaries.heading"))
        ),
        AnswerSection(
          Some(Messages("answerPage.section.classOfBeneficiary.subheading") + " " + (index + 1)),
          Seq(
            checkYourAnswersHelper.classBeneficiaryDescription(index).value
          ),
          None
        ),
        AnswerSection(
          Some(Messages("answerPage.section.moneyAsset.subheading")),
          Seq(
            checkYourAnswersHelper.assetMoneyValue(index).value
          ),
          Some(Messages("answerPage.section.assets.heading"))
        )
      )

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, routes.SummaryAnswerPageController.onPageLoad().url)

      val result = route(application, request).value

      val view = application.injector.instanceOf[SummaryAnswerPageView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(expectedSections)(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to tasklist page when tasklist not completed" in {

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

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, routes.SummaryAnswerPageController.onPageLoad().url)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.TaskListController.onPageLoad().url

      application.stop()

    }

  }
}
