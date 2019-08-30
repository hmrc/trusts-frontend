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

package generators

import models.UserAnswers
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Arbitrary, Gen}
import org.scalatest.TryValues
import pages._
import pages.property_or_land.{PropertyOrLandAddressUkYesNoPage, PropertyOrLandDescriptionPage, PropertyOrLandUKAddressPage, _}
import pages.living_settlor.{SettlorIndividualAddressInternationalPage, SettlorIndividualAddressUKPage, SettlorIndividualAddressUKYesNoPage, SettlorIndividualAddressYesNoPage, SettlorIndividualDateOfBirthPage, SettlorIndividualDateOfBirthYesNoPage, SettlorIndividualIDCardPage, SettlorIndividualIDCardYesNoPage, SettlorIndividualNINOPage, SettlorIndividualNINOYesNoPage, SettlorIndividualNamePage, SettlorIndividualOrBusinessPage, SettlorIndividualPassportPage, SettlorIndividualPassportYesNoPage}
import pages.shares._
import play.api.libs.json.{JsValue, Json}

trait UserAnswersGenerator extends TryValues {
  self: Generators =>

  val generators: Seq[Gen[(QuestionPage[_], JsValue)]] =
    arbitrary[(SettlorKindOfTrustPage.type, JsValue)] ::
    arbitrary[(SettlorIndividualPassportYesNoPage, JsValue)] ::
    arbitrary[(SettlorIndividualPassportPage, JsValue)] ::
    arbitrary[(SettlorIndividualIDCardYesNoPage, JsValue)] ::
    arbitrary[(SettlorIndividualIDCardPage, JsValue)] ::
    arbitrary[(SettlorIndividualAddressUKYesNoPage, JsValue)] ::
    arbitrary[(SettlorIndividualAddressUKPage, JsValue)] ::
    arbitrary[(SettlorIndividualAddressInternationalPage, JsValue)] ::
    arbitrary[(SettlorIndividualNINOYesNoPage, JsValue)] ::
    arbitrary[(SettlorIndividualNINOPage, JsValue)] ::
    arbitrary[(SettlorIndividualAddressYesNoPage, JsValue)] ::
    arbitrary[(SettlorIndividualDateOfBirthPage, JsValue)] ::
    arbitrary[(SettlorIndividualDateOfBirthYesNoPage, JsValue)] ::
    arbitrary[(SettlorIndividualNamePage, JsValue)] ::
    arbitrary[(SettlorIndividualOrBusinessPage, JsValue)] ::
    arbitrary[(AddAnAssetYesNoPage.type, JsValue)] ::
    arbitrary[(PropertyOrLandAddressYesNoPage, JsValue)] ::
    arbitrary[(PropertyOrLandAddressUkYesNoPage, JsValue)] ::
    arbitrary[(TrustOwnAllThePropertyOrLandPage, JsValue)] ::
    arbitrary[(PropertyOrLandInternationalAddressPage, JsValue)] ::
    arbitrary[(ShareCompanyNamePage, JsValue)] ::
    arbitrary[(SharesOnStockExchangePage, JsValue)] ::
    arbitrary[(SharesInAPortfolioPage, JsValue)] ::
    arbitrary[(ShareValueInTrustPage, JsValue)] ::
    arbitrary[(ShareQuantityInTrustPage, JsValue)] ::
    arbitrary[(SharePortfolioValueInTrustPage, JsValue)] ::
    arbitrary[(SharePortfolioQuantityInTrustPage, JsValue)] ::
    arbitrary[(SharePortfolioOnStockExchangePage, JsValue)] ::
    arbitrary[(SharePortfolioNamePage, JsValue)] ::
    arbitrary[(ShareClassPage, JsValue)] ::
    arbitrary[(PropertyOrLandUKAddressPage, JsValue)] ::
    arbitrary[(DeclarationPage.type, JsValue)] ::
    arbitrary[(WhatTypeOfBeneficiaryPage.type, JsValue)] ::
    arbitrary[(AgentInternationalAddressPage.type, JsValue)] ::
    arbitrary[(AgentUKAddressPage.type, JsValue)] ::
    arbitrary[(AgentAddressYesNoPage.type, JsValue)] ::
    arbitrary[(ClassBeneficiaryDescriptionPage, JsValue)] ::
    arbitrary[(AgentNamePage.type, JsValue)] ::
    arbitrary[(AddABeneficiaryPage.type, JsValue)] ::
    arbitrary[(PropertyOrLandDescriptionPage, JsValue)] ::
    arbitrary[(PropertyOrLandTotalValuePage, JsValue)] ::
    arbitrary[(IndividualBeneficiaryVulnerableYesNoPage, JsValue)] ::
    arbitrary[(IndividualBeneficiaryAddressUKPage, JsValue)] ::
    arbitrary[(IndividualBeneficiaryAddressYesNoPage, JsValue)] ::
    arbitrary[(IndividualBeneficiaryAddressUKYesNoPage, JsValue)] ::
    arbitrary[(IndividualBeneficiaryNationalInsuranceNumberPage, JsValue)] ::
    arbitrary[(IndividualBeneficiaryNationalInsuranceYesNoPage, JsValue)] ::
    arbitrary[(IndividualBeneficiaryIncomePage, JsValue)] ::
    arbitrary[(IndividualBeneficiaryIncomeYesNoPage, JsValue)] ::
    arbitrary[(IndividualBeneficiaryDateOfBirthPage, JsValue)] ::
    arbitrary[(IndividualBeneficiaryDateOfBirthYesNoPage, JsValue)] ::
    arbitrary[(IndividualBeneficiaryNamePage, JsValue)] ::
    arbitrary[(WasSettlorsAddressUKYesNoPage.type, JsValue)] ::
    arbitrary[(SetupAfterSettlorDiedPage.type, JsValue)] ::
    arbitrary[(SettlorsUKAddressPage.type, JsValue)] ::
    arbitrary[(SettlorsNINoYesNoPage.type, JsValue)] ::
    arbitrary[(SettlorsNamePage.type, JsValue)] ::
    arbitrary[(SettlorsLastKnownAddressYesNoPage.type, JsValue)] ::
    arbitrary[(SettlorsInternationalAddressPage.type, JsValue)] ::
    arbitrary[(SettlorsDateOfBirthPage.type, JsValue)] ::
    arbitrary[(SettlorNationalInsuranceNumberPage.type, JsValue)] ::
    arbitrary[(SettlorDateOfDeathYesNoPage.type, JsValue)] ::
    arbitrary[(SettlorDateOfDeathPage.type, JsValue)] ::
    arbitrary[(SettlorDateOfBirthYesNoPage.type, JsValue)] ::
    arbitrary[(AddAssetsPage.type, JsValue)] ::
    arbitrary[(AssetMoneyValuePage, JsValue)] ::
    arbitrary[(AgentInternalReferencePage.type, JsValue)] ::
    arbitrary[(WhatKindOfAssetPage, JsValue)] ::
    arbitrary[(AgentTelephoneNumberPage.type, JsValue)] ::
    arbitrary[(TrusteeLiveInTheUKPage, JsValue)] ::
    arbitrary[(TrusteesNinoPage, JsValue)] ::
    arbitrary[(TrusteesUkAddressPage, JsValue)] ::
    arbitrary[(TrusteeAUKCitizenPage, JsValue)] ::
    arbitrary[(TelephoneNumberPage, JsValue)] ::
    arbitrary[(TrusteeAUKCitizenPage, JsValue)] ::
    arbitrary[(TrusteesNamePage, JsValue)] ::
    arbitrary[(TrusteeIndividualOrBusinessPage, JsValue)] ::
    arbitrary[(IsThisLeadTrusteePage, JsValue)] ::
    arbitrary[(TrusteesDateOfBirthPage, JsValue)] ::
    arbitrary[(TrusteesNamePage, JsValue)] ::
    arbitrary[(TrusteeIndividualOrBusinessPage, JsValue)] ::
    arbitrary[(IsThisLeadTrusteePage, JsValue)] ::
    arbitrary[(PostcodeForTheTrustPage.type, JsValue)] ::
    arbitrary[(WhatIsTheUTRPage.type, JsValue)] ::
    arbitrary[(TrustHaveAUTRPage.type, JsValue)] ::
    arbitrary[(TrustRegisteredOnlinePage.type, JsValue)] ::
    arbitrary[(WhenTrustSetupPage.type, JsValue)] ::
    arbitrary[(AgentOtherThanBarristerPage.type, JsValue)] ::
    arbitrary[(InheritanceTaxActPage.type, JsValue)] ::
    arbitrary[(NonResidentTypePage.type, JsValue)] ::
    arbitrary[(TrustPreviouslyResidentPage.type, JsValue)] ::
    arbitrary[(TrustResidentOffshorePage.type, JsValue)] ::
    arbitrary[(RegisteringTrustFor5APage.type, JsValue)] ::
    arbitrary[(EstablishedUnderScotsLawPage.type, JsValue)] ::
    arbitrary[(TrustResidentInUKPage.type, JsValue)] ::
    arbitrary[(CountryAdministeringTrustPage.type, JsValue)] ::
    arbitrary[(AdministrationInsideUKPage.type, JsValue)] ::
    arbitrary[(CountryGoverningTrustPage.type, JsValue)] ::
    arbitrary[(GovernedInsideTheUKPage.type, JsValue)] ::
    arbitrary[(TrustNamePage.type, JsValue)] ::
    Nil

  implicit lazy val arbitraryUserData: Arbitrary[UserAnswers] = {

    import models._

    Arbitrary {
      for {
        id      <- nonEmptyString
        data    <- generators match {
          case Nil => Gen.const(Map[QuestionPage[_], JsValue]())
          case _   => Gen.mapOf(oneOf(generators))
        }
        internalId <- nonEmptyString
      } yield UserAnswers (
        draftId = id,
        data = data.foldLeft(Json.obj()) {
          case (obj, (path, value)) =>
            obj.setObject(path.path, value).get
        },
        internalAuthId = internalId
      )
    }
  }
}
