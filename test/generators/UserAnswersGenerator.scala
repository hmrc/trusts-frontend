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

package generators

import models.core.UserAnswers
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Arbitrary, Gen}
import org.scalatest.TryValues
import pages._
import pages.register.agents._
import pages.register.asset.money.AssetMoneyValuePage
import pages.register.asset.{AddAnAssetYesNoPage, AddAssetsPage, WhatKindOfAssetPage}
import pages.register.settlors.deceased_settlor._
import pages.register.settlors.living_settlor._
import pages.register.asset.property_or_land._
import pages.register.asset.shares._
import pages.register.beneficiaries.individual._
import pages.register.beneficiaries.{AddABeneficiaryPage, ClassBeneficiaryDescriptionPage, WhatTypeOfBeneficiaryPage}
import pages.playback.{DeclarationWhatNextPage, WhatIsTheUTRVariationPage}
import pages.register.settlors.living_settlor.trust_type._
import pages.register._
import pages.register.settlors._
import pages.register.trust_details.{AdministrationInsideUKPage, CountryAdministeringTrustPage, CountryGoverningTrustPage, EstablishedUnderScotsLawPage, GovernedInsideTheUKPage, InheritanceTaxActPage, NonResidentTypePage, RegisteringTrustFor5APage, SettlorsBasedInTheUKPage, TrustNamePage, TrustPreviouslyResidentPage, TrustResidentOffshorePage, TrusteesBasedInTheUKPage, WhenTrustSetupPage}
import pages.register.trustees._
import pages.register.trustees.individual.{TrusteeAUKCitizenPage, TrusteeAddressInTheUKPage, TrusteesDateOfBirthPage, TrusteesNamePage, TrusteesNinoPage, TrusteesUkAddressPage}
import play.api.libs.json.{JsValue, Json}

trait UserAnswersGenerator extends TryValues {
  self: Generators =>

  import models.core.UserAnswerImplicits._

  val generators: Seq[Gen[(QuestionPage[_], JsValue)]] =
    arbitrary[(DeclarationWhatNextPage.type, JsValue)] ::
    arbitrary[(WhatIsTheUTRVariationPage.type, JsValue)] ::
    arbitrary[(KindOfTrustPage.type, JsValue)] ::
    arbitrary[(SettlorsBasedInTheUKPage.type, JsValue)] ::
    arbitrary[(TrusteesBasedInTheUKPage.type, JsValue)] ::
    arbitrary[(HoldoverReliefYesNoPage.type, JsValue)] ::
    arbitrary[(SettlorBusinessNamePage, JsValue)] ::
    arbitrary[(RemoveSettlorPage, JsValue)] ::
    arbitrary[(SettlorIndividualPassportYesNoPage, JsValue)] ::
    arbitrary[(SettlorIndividualPassportPage, JsValue)] ::
    arbitrary[(SettlorIndividualIDCardYesNoPage, JsValue)] ::
    arbitrary[(SettlorIndividualIDCardPage, JsValue)] ::
    arbitrary[(SettlorAddressUKYesNoPage, JsValue)] ::
    arbitrary[(SettlorAddressUKPage, JsValue)] ::
    arbitrary[(SettlorAddressInternationalPage, JsValue)] ::
    arbitrary[(SettlorIndividualNINOYesNoPage, JsValue)] ::
    arbitrary[(SettlorIndividualNINOPage, JsValue)] ::
    arbitrary[(SettlorAddressYesNoPage, JsValue)] ::
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
    arbitrary[(SetUpAfterSettlorDiedYesNoPage.type, JsValue)] ::
    arbitrary[(SettlorsUKAddressPage.type, JsValue)] ::
    arbitrary[(SettlorsNationalInsuranceYesNoPage.type, JsValue)] ::
    arbitrary[(SettlorsNamePage.type, JsValue)] ::
    arbitrary[(SettlorsLastKnownAddressYesNoPage.type, JsValue)] ::
    arbitrary[(SettlorsInternationalAddressPage.type, JsValue)] ::
    arbitrary[(SettlorsDateOfBirthPage.type, JsValue)] ::
    arbitrary[(SettlorNationalInsuranceNumberPage.type, JsValue)] ::
    arbitrary[(SettlorDateOfDeathYesNoPage.type, JsValue)] ::
    arbitrary[(SettlorDateOfDeathPage.type, JsValue)] ::
    arbitrary[(SettlorDateOfBirthYesNoPage.type, JsValue)] ::
    arbitrary[(AddASettlorPage.type, JsValue)] ::
    arbitrary[(AddAssetsPage.type, JsValue)] ::
    arbitrary[(AssetMoneyValuePage, JsValue)] ::
    arbitrary[(AgentInternalReferencePage.type, JsValue)] ::
    arbitrary[(WhatKindOfAssetPage, JsValue)] ::
    arbitrary[(AgentTelephoneNumberPage.type, JsValue)] ::
    arbitrary[(TrusteeAddressInTheUKPage, JsValue)] ::
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
    arbitrary[(CountryAdministeringTrustPage.type, JsValue)] ::
    arbitrary[(AdministrationInsideUKPage.type, JsValue)] ::
    arbitrary[(CountryGoverningTrustPage.type, JsValue)] ::
    arbitrary[(GovernedInsideTheUKPage.type, JsValue)] ::
    arbitrary[(TrustNamePage.type, JsValue)] ::
    Nil

  implicit lazy val arbitraryUserData: Arbitrary[UserAnswers] = {

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
