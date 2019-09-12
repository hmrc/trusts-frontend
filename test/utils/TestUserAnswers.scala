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

import java.time.LocalDate

import models.Matched.{Failed, Success}
import models.Status.Completed
import models.{AddABeneficiary, AddATrustee, AddAssets, FullName, IndividualOrBusiness, UKAddress, UserAnswers, WhatKindOfAsset}
import org.scalatest.TryValues
import pages._
import pages.deceased_settlor.{SettlorDateOfBirthYesNoPage, SettlorDateOfDeathYesNoPage, SettlorsLastKnownAddressYesNoPage, SettlorsNINoYesNoPage, SettlorsNamePage}
import pages.entitystatus._
import pages.trustees.{AddATrusteePage, IsThisLeadTrusteePage, TelephoneNumberPage, TrusteeAUKCitizenPage, TrusteeIndividualOrBusinessPage, TrusteeLiveInTheUKPage, TrusteesDateOfBirthPage, TrusteesNamePage, TrusteesNinoPage, TrusteesUkAddressPage}
import play.api.libs.json.Json

object TestUserAnswers extends TryValues {

  lazy val draftId = "id"
  lazy val userInternalId = "internalId"

  def emptyUserAnswers = UserAnswers(draftId, Json.obj(), internalAuthId = userInternalId)

  def withAgent(userAnswers: UserAnswers) : UserAnswers = {
      userAnswers
        .set(AgentARNPage, "SARN1234567").success.value
        .set(AgentNamePage, "Agency Name").success.value
        .set(AgentUKAddressPage, UKAddress("line1", Some("line2"), Some("line3"), "Newcastle", "ab1 1ab")).success.value
        .set(AgentTelephoneNumberPage, "+1234567890").success.value
        .set(AgentInternalReferencePage, "1234-5678").success.value
        .set(AgentAddressYesNoPage, true).success.value
  }

  def withLeadTrustee(userAnswers: UserAnswers) : UserAnswers = {
    val index = 0
    userAnswers
      .set(IsThisLeadTrusteePage(index), true).success.value
      .set(TrusteeIndividualOrBusinessPage(index), IndividualOrBusiness.Individual).success.value
      .set(TrusteesNamePage(index), FullName("first name",  Some("middle name"), "Last Name")).success.value
      .set(TrusteesDateOfBirthPage(index), LocalDate.of(1500,10,10)).success.value
      .set(TrusteeAUKCitizenPage(index), true).success.value
      .set(TrusteeLiveInTheUKPage(index), true).success.value
      .set(TrusteesNinoPage(index), "AB123456C").success.value
      .set(TelephoneNumberPage(index), "0191 1111111").success.value
      .set(TrusteesUkAddressPage(index), UKAddress("line1", None,None, "town", "NE65QA")).success.value
      .set(TrusteeStatus(index), Completed).success.value
  }

  def withIndividualBeneficiary(userAnswers: UserAnswers) : UserAnswers = {
    val index = 0
    userAnswers
      .set(IndividualBeneficiaryNamePage(index), FullName("first name", None, "last name")).success.value
      .set(IndividualBeneficiaryDateOfBirthYesNoPage(index), true).success.value
      .set(IndividualBeneficiaryDateOfBirthPage(index), LocalDate.of(1500,10,10)).success.value
      .set(IndividualBeneficiaryIncomeYesNoPage(index), false).success.value
      .set(IndividualBeneficiaryIncomePage(index), "100").success.value
      .set(IndividualBeneficiaryNationalInsuranceYesNoPage(index), true).success.value
      .set(IndividualBeneficiaryNationalInsuranceNumberPage(index), "AB123456C").success.value
      .set(IndividualBeneficiaryVulnerableYesNoPage(index), true).success.value
      .set(IndividualBeneficiaryStatus(index), Completed).success.value
  }

  def withDeceasedSettlor(userAnswers: UserAnswers) : UserAnswers = {
      userAnswers
      .set(SetupAfterSettlorDiedPage, true).success.value
      .set(SettlorsNamePage, FullName("First", None, "Last")).success.value
      .set(SettlorDateOfDeathYesNoPage, false).success.value
      .set(SettlorDateOfBirthYesNoPage, false).success.value
      .set(SettlorsNINoYesNoPage, false).success.value
      .set(SettlorsLastKnownAddressYesNoPage, false).success.value
      .set(DeceasedSettlorStatus, Completed).success.value
  }

  def withTrustDetails(userAnswers: UserAnswers) : UserAnswers = {
      userAnswers
      .set(TrustNamePage, "New Trust").success.value
      .set(WhenTrustSetupPage, LocalDate.of(1500,10,10)).success.value
      .set(GovernedInsideTheUKPage, true).success.value
      .set(AdministrationInsideUKPage, true).success.value
      .set(TrustResidentInUKPage, true).success.value
      .set(EstablishedUnderScotsLawPage, true).success.value
      .set(TrustResidentOffshorePage, false).success.value
      .set(TrustDetailsStatus, Completed).success.value
  }

  def withMoneyAsset(userAnswers : UserAnswers) : UserAnswers = {
    val index = 0
    userAnswers
      .set(WhatKindOfAssetPage(index), WhatKindOfAsset.Money).success.value
      .set(AssetMoneyValuePage(index), "2000").success.value
      .set(AssetStatus(index), Completed).success.value
  }

  def withDeclaration(userAnswers: UserAnswers) : UserAnswers = {
    userAnswers
      .set(DeclarationPage, FullName("First", None, "Last")).success.value
  }

  def withMatchingSuccess(userAnswers: UserAnswers) : UserAnswers = {
    userAnswers
      .set(TrustNamePage, "Existing Trust").success.value
      .set(TrustHaveAUTRPage, true).success.value
      .set(WhatIsTheUTRPage, "123456789").success.value
      .set(PostcodeForTheTrustPage, "NE981ZZ").success.value
      .set(ExistingTrustMatched, Success).success.value
  }

  def withMatchingFailed(userAnswers: UserAnswers) : UserAnswers = {
    userAnswers
      .set(ExistingTrustMatched, Failed).success.value
  }

  def withCompleteSections(userAnswers: UserAnswers) : UserAnswers = {
    userAnswers
      .set(AddATrusteePage, AddATrustee.NoComplete).success.value
      .set(AddABeneficiaryPage, AddABeneficiary.NoComplete).success.value
      .set(AddAssetsPage, AddAssets.NoComplete).success.value
  }

  def newTrustCompleteUserAnswers = {
    val emptyUserAnswers = TestUserAnswers.emptyUserAnswers
    val uaWithLead = TestUserAnswers.withLeadTrustee(emptyUserAnswers)
    val uaWithDeceased = TestUserAnswers.withDeceasedSettlor(uaWithLead)
    val uaWithIndBen = TestUserAnswers.withIndividualBeneficiary(uaWithDeceased)
    val uaWithTrustDetails = TestUserAnswers.withTrustDetails(uaWithIndBen)
    val userAnswers = TestUserAnswers.withMoneyAsset(uaWithTrustDetails)

    userAnswers
  }

}
