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

import models._
import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary.arbitrary
import pages._
import pages.property_or_land._
import pages.living_settlor.{SettlorIndividualAddressInternationalPage, SettlorIndividualAddressUKPage, SettlorIndividualAddressUKYesNoPage, SettlorIndividualAddressYesNoPage, SettlorIndividualDateOfBirthPage, SettlorIndividualDateOfBirthYesNoPage, SettlorIndividualIDCardPage, SettlorIndividualIDCardYesNoPage, SettlorIndividualNINOPage, SettlorIndividualNINOYesNoPage, SettlorIndividualNamePage, SettlorIndividualOrBusinessPage, SettlorIndividualPassportPage, SettlorIndividualPassportYesNoPage}
import pages.shares._
import play.api.libs.json.{JsValue, Json}

trait UserAnswersEntryGenerators extends PageGenerators with ModelGenerators {

  implicit lazy val arbitrarySettlorsBasedInTheUKUserAnswersEntry: Arbitrary[(SettlorsBasedInTheUKPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[SettlorsBasedInTheUKPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitrarySettlorHandoverReliefYesNoUserAnswersEntry: Arbitrary[(SettlorHandoverReliefYesNoPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[SettlorHandoverReliefYesNoPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitrarySettlorIndividualPassportYesNoUserAnswersEntry: Arbitrary[(SettlorIndividualPassportYesNoPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[SettlorIndividualPassportYesNoPage]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitrarySettlorIndividualPassportUserAnswersEntry: Arbitrary[(SettlorIndividualPassportPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[SettlorIndividualPassportPage]
        value <- arbitrary[PassportOrIdCardDetails].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitrarySettlorIndividualIDCardYesNoUserAnswersEntry: Arbitrary[(SettlorIndividualIDCardYesNoPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[SettlorIndividualIDCardYesNoPage]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitrarySettlorIndividualIDCardUserAnswersEntry: Arbitrary[(SettlorIndividualIDCardPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[SettlorIndividualIDCardPage]
        value <- arbitrary[PassportOrIdCardDetails].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitrarySettlorIndividualAddressUKYesNoUserAnswersEntry: Arbitrary[(SettlorIndividualAddressUKYesNoPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[SettlorIndividualAddressUKYesNoPage]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitrarySettlorIndividualAddressUKUserAnswersEntry: Arbitrary[(SettlorIndividualAddressUKPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[SettlorIndividualAddressUKPage]
        value <- arbitrary[UKAddress].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitrarySettlorIndividualAddressInternationalUserAnswersEntry: Arbitrary[(SettlorIndividualAddressInternationalPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[SettlorIndividualAddressInternationalPage]
        value <- arbitrary[InternationalAddress].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitrarySettlorIndividualNINOYesNoUserAnswersEntry: Arbitrary[(SettlorIndividualNINOYesNoPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[SettlorIndividualNINOYesNoPage]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitrarySettlorIndividualNINOUserAnswersEntry: Arbitrary[(SettlorIndividualNINOPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[SettlorIndividualNINOPage]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitrarySettlorIndividualAddressYesNoUserAnswersEntry: Arbitrary[(SettlorIndividualAddressYesNoPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[SettlorIndividualAddressYesNoPage]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitrarySettlorIndividualDateOfBirthUserAnswersEntry: Arbitrary[(SettlorIndividualDateOfBirthPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[SettlorIndividualDateOfBirthPage]
        value <- arbitrary[Int].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitrarySettlorIndividualDateOfBirthYesNoUserAnswersEntry: Arbitrary[(SettlorIndividualDateOfBirthYesNoPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[SettlorIndividualDateOfBirthYesNoPage]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitrarySettlorIndividualNameUserAnswersEntry: Arbitrary[(SettlorIndividualNamePage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[SettlorIndividualNamePage]
        value <- arbitrary[FullName].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitrarySettlorIndividualOrBusinessUserAnswersEntry: Arbitrary[(SettlorIndividualOrBusinessPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[SettlorIndividualOrBusinessPage]
        value <- arbitrary[SettlorIndividualOrBusiness].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryAddAnAssetYesNoUserAnswersEntry: Arbitrary[(AddAnAssetYesNoPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[AddAnAssetYesNoPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryPropertyOrLandAddressYesNoUserAnswersEntry: Arbitrary[(PropertyOrLandAddressYesNoPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[PropertyOrLandAddressYesNoPage]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryPropertyOrLandInternationalAddressUkYesNoUserAnswersEntry: Arbitrary[(PropertyOrLandInternationalAddressPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[PropertyOrLandInternationalAddressPage]
        value <- arbitrary[InternationalAddress].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryPropertyOrLandTotalValueUserAnswersEntry: Arbitrary[(PropertyOrLandTotalValuePage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[PropertyOrLandTotalValuePage]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryPropertyOrLandDescriptionUserAnswersEntry: Arbitrary[(PropertyOrLandDescriptionPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[PropertyOrLandDescriptionPage]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryPropertyOrLandAddressUkYesNoUserAnswersEntry: Arbitrary[(PropertyOrLandAddressUkYesNoPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[PropertyOrLandAddressUkYesNoPage]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryTrustOwnAllThePropertyOrLandUserAnswersEntry: Arbitrary[(TrustOwnAllThePropertyOrLandPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[TrustOwnAllThePropertyOrLandPage]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryShareCompanyNameUserAnswersEntry: Arbitrary[(ShareCompanyNamePage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[ShareCompanyNamePage]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryPropertyOrLandAddressUserAnswersEntry: Arbitrary[(PropertyOrLandUKAddressPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[PropertyOrLandUKAddressPage]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitrarySharesOnStockExchangeUserAnswersEntry: Arbitrary[(SharesOnStockExchangePage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[SharesOnStockExchangePage]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitrarySharesInAPortfolioUserAnswersEntry: Arbitrary[(SharesInAPortfolioPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[SharesInAPortfolioPage]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryShareValueInTrustUserAnswersEntry: Arbitrary[(ShareValueInTrustPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[ShareValueInTrustPage]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryShareQuantityInTrustUserAnswersEntry: Arbitrary[(ShareQuantityInTrustPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[ShareQuantityInTrustPage]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitrarySharePortfolioValueInTrustUserAnswersEntry: Arbitrary[(SharePortfolioValueInTrustPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[SharePortfolioValueInTrustPage]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitrarySharePortfolioQuantityInTrustUserAnswersEntry: Arbitrary[(SharePortfolioQuantityInTrustPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[SharePortfolioQuantityInTrustPage]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitrarySharePortfolioOnStockExchangeUserAnswersEntry: Arbitrary[(SharePortfolioOnStockExchangePage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[SharePortfolioOnStockExchangePage]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitrarySharePortfolioNameUserAnswersEntry: Arbitrary[(SharePortfolioNamePage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[SharePortfolioNamePage]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryShareClassUserAnswersEntry: Arbitrary[(ShareClassPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[ShareClassPage]
        value <- arbitrary[ShareClass].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryDeclarationUserAnswersEntry: Arbitrary[(DeclarationPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[DeclarationPage.type]
        value <- arbitrary[FullName].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryWhatTypeOfBeneficiaryUserAnswersEntry: Arbitrary[(WhatTypeOfBeneficiaryPage.type, JsValue)] =
    Arbitrary {
      for {
        page <- arbitrary[WhatTypeOfBeneficiaryPage.type]
        value <- arbitrary[WhatTypeOfBeneficiary].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryAgentInternationalAddressUserAnswersEntry: Arbitrary[(AgentInternationalAddressPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[AgentInternationalAddressPage.type]
        value <- arbitrary[InternationalAddress].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryClassBeneficiaryDescriptionUserAnswersEntry: Arbitrary[(ClassBeneficiaryDescriptionPage, JsValue)] =
    Arbitrary {
      for {
        page <- arbitrary[ClassBeneficiaryDescriptionPage]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryAgentUKAddressUserAnswersEntry: Arbitrary[(AgentUKAddressPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[AgentUKAddressPage.type]
        value <- arbitrary[UKAddress].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryAgentAddressYesNoUserAnswersEntry: Arbitrary[(AgentAddressYesNoPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[AgentAddressYesNoPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryIndividualBeneficiaryAddressUKYesNoUserAnswersEntry: Arbitrary[(IndividualBeneficiaryAddressUKYesNoPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[IndividualBeneficiaryAddressUKYesNoPage]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }


  implicit lazy val arbitraryAgentNameUserAnswersEntry: Arbitrary[(AgentNamePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[AgentNamePage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryAddABeneficiaryUserAnswersEntry: Arbitrary[(AddABeneficiaryPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[AddABeneficiaryPage.type]
        value <- arbitrary[AddABeneficiary].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryIndividualBeneficiaryVulnerableYesNoUserAnswersEntry: Arbitrary[(IndividualBeneficiaryVulnerableYesNoPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[IndividualBeneficiaryVulnerableYesNoPage]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryIndividualBeneficiaryAddressUKUserAnswersEntry: Arbitrary[(IndividualBeneficiaryAddressUKPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[IndividualBeneficiaryAddressUKPage]
        value <- arbitrary[UKAddress].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryIndividualBeneficiaryAddressYesNoUserAnswersEntry: Arbitrary[(IndividualBeneficiaryAddressYesNoPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[IndividualBeneficiaryAddressYesNoPage]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryIndividualBeneficiaryNationalInsuranceNumberUserAnswersEntry: Arbitrary[(IndividualBeneficiaryNationalInsuranceNumberPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[IndividualBeneficiaryNationalInsuranceNumberPage]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryIndividualBeneficiaryNationalInsuranceYesNoUserAnswersEntry: Arbitrary[(IndividualBeneficiaryNationalInsuranceYesNoPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[IndividualBeneficiaryNationalInsuranceYesNoPage]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryIndividualBeneficiaryIncomeUserAnswersEntry: Arbitrary[(IndividualBeneficiaryIncomePage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[IndividualBeneficiaryIncomePage]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryIndividualBeneficiaryIncomeYesNoUserAnswersEntry: Arbitrary[(IndividualBeneficiaryIncomeYesNoPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[IndividualBeneficiaryIncomeYesNoPage]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryIndividualBeneficiaryDateOfBirthUserAnswersEntry: Arbitrary[(IndividualBeneficiaryDateOfBirthPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[IndividualBeneficiaryDateOfBirthPage]
        value <- arbitrary[Int].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryIndividualBeneficiaryDateOfBirthYesNoUserAnswersEntry: Arbitrary[(IndividualBeneficiaryDateOfBirthYesNoPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[IndividualBeneficiaryDateOfBirthYesNoPage]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryIndividualBeneficiaryNameUserAnswersEntry: Arbitrary[(IndividualBeneficiaryNamePage, JsValue)] =
    Arbitrary {
      for {
        page <- arbitrary[IndividualBeneficiaryNamePage]
        value <- arbitrary[FullName].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryWasSettlorsAddressUKYesNoUserAnswersEntry: Arbitrary[(WasSettlorsAddressUKYesNoPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[WasSettlorsAddressUKYesNoPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitrarySetupAfterSettlorDiedUserAnswersEntry: Arbitrary[(SetupAfterSettlorDiedPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[SetupAfterSettlorDiedPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitrarySettlorsUKAddressUserAnswersEntry: Arbitrary[(SettlorsUKAddressPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[SettlorsUKAddressPage.type]
        value <- arbitrary[UKAddress].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitrarySettlorsNINoYesNoUserAnswersEntry: Arbitrary[(SettlorsNINoYesNoPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[SettlorsNINoYesNoPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitrarySettlorsNameUserAnswersEntry: Arbitrary[(SettlorsNamePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[SettlorsNamePage.type]
        value <- arbitrary[FullName].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitrarySettlorsLastKnownAddressYesNoUserAnswersEntry: Arbitrary[(SettlorsLastKnownAddressYesNoPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[SettlorsLastKnownAddressYesNoPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitrarySettlorsInternationalAddressUserAnswersEntry: Arbitrary[(SettlorsInternationalAddressPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[SettlorsInternationalAddressPage.type]
        value <- arbitrary[InternationalAddress].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitrarySettlorsDateOfBirthUserAnswersEntry: Arbitrary[(SettlorsDateOfBirthPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[SettlorsDateOfBirthPage.type]
        value <- arbitrary[Int].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitrarySettlorNationalInsuranceNumberUserAnswersEntry: Arbitrary[(SettlorNationalInsuranceNumberPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[SettlorNationalInsuranceNumberPage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitrarySettlorDateOfDeathYesNoUserAnswersEntry: Arbitrary[(SettlorDateOfDeathYesNoPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[SettlorDateOfDeathYesNoPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitrarySettlorDateOfDeathUserAnswersEntry: Arbitrary[(SettlorDateOfDeathPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[SettlorDateOfDeathPage.type]
        value <- arbitrary[Int].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitrarySettlorDateOfBirthYesNoUserAnswersEntry: Arbitrary[(SettlorDateOfBirthYesNoPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[SettlorDateOfBirthYesNoPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryAddAssetsUserAnswersEntry: Arbitrary[(AddAssetsPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[AddAssetsPage.type]
        value <- arbitrary[AddAssets].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryAssetMoneyValueUserAnswersEntry: Arbitrary[(AssetMoneyValuePage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[AssetMoneyValuePage]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }


  implicit lazy val arbitraryAgentInternalReferenceUserAnswersEntry: Arbitrary[(AgentInternalReferencePage.type, JsValue)] =
    Arbitrary {
      for {
        page <- arbitrary[AgentInternalReferencePage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      }
        yield (page, value)
    }

  implicit lazy val arbitraryWhatKindOfAssetUserAnswersEntry: Arbitrary[(WhatKindOfAssetPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[WhatKindOfAssetPage]
        value <- arbitrary[WhatKindOfAsset].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryAgentTelephoneNumberUserAnswersEntry: Arbitrary[(AgentTelephoneNumberPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[AgentTelephoneNumberPage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryTelephoneNumberUserAnswersEntry: Arbitrary[(TelephoneNumberPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[TelephoneNumberPage]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryTrusteesNinoUserAnswersEntry: Arbitrary[(TrusteesNinoPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[TrusteesNinoPage]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryTrusteeLiveInTheUKUserAnswersEntry: Arbitrary[(TrusteeLiveInTheUKPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[TrusteeLiveInTheUKPage]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryTrusteesUkAddressUserAnswersEntry: Arbitrary[(TrusteesUkAddressPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[TrusteesUkAddressPage]
        value <- arbitrary[UKAddress].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitrarytrusteeAUKCitizenUserAnswersEntry: Arbitrary[(TrusteeAUKCitizenPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[TrusteeAUKCitizenPage]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryTrusteesNameUserAnswersEntry: Arbitrary[(TrusteesNamePage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[TrusteesNamePage]
        value <- arbitrary[FullName].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryTrusteeIndividualOrBusinessUserAnswersEntry: Arbitrary[(TrusteeIndividualOrBusinessPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[TrusteeIndividualOrBusinessPage]
        value <- arbitrary[IndividualOrBusiness].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryIsThisLeadTrusteeUserAnswersEntry: Arbitrary[(IsThisLeadTrusteePage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[IsThisLeadTrusteePage]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryTrusteesDateOfBirthUserAnswersEntry: Arbitrary[(TrusteesDateOfBirthPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[TrusteesDateOfBirthPage]
        value <- arbitrary[Int].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryPostcodeForTheTrustUserAnswersEntry: Arbitrary[(PostcodeForTheTrustPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[PostcodeForTheTrustPage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryWhatIsTheUTRUserAnswersEntry: Arbitrary[(WhatIsTheUTRPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[WhatIsTheUTRPage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryTrustHaveAUTRUserAnswersEntry: Arbitrary[(TrustHaveAUTRPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[TrustHaveAUTRPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryTrustRegisteredOnlineUserAnswersEntry: Arbitrary[(TrustRegisteredOnlinePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[TrustRegisteredOnlinePage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryWhenTrustSetupUserAnswersEntry: Arbitrary[(WhenTrustSetupPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[WhenTrustSetupPage.type]
        value <- arbitrary[Int].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryAgentOtherThanBarristerUserAnswersEntry: Arbitrary[(AgentOtherThanBarristerPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[AgentOtherThanBarristerPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryInheritanceTaxActUserAnswersEntry: Arbitrary[(InheritanceTaxActPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[InheritanceTaxActPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryNonResidentTypeUserAnswersEntry: Arbitrary[(NonResidentTypePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[NonResidentTypePage.type]
        value <- arbitrary[NonResidentType].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryTrustPreviouslyResidentUserAnswersEntry: Arbitrary[(TrustPreviouslyResidentPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[TrustPreviouslyResidentPage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryTrustResidentOffshoreUserAnswersEntry: Arbitrary[(TrustResidentOffshorePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[TrustResidentOffshorePage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryRegisteringTrustFor5AUserAnswersEntry: Arbitrary[(RegisteringTrustFor5APage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[RegisteringTrustFor5APage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryEstablishedUnderScotsLawUserAnswersEntry: Arbitrary[(EstablishedUnderScotsLawPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[EstablishedUnderScotsLawPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryTrustResidentInUKUserAnswersEntry: Arbitrary[(TrustResidentInUKPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[TrustResidentInUKPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryCountryAdministeringTrustUserAnswersEntry: Arbitrary[(CountryAdministeringTrustPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[CountryAdministeringTrustPage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryAdministrationInsideUKUserAnswersEntry: Arbitrary[(AdministrationInsideUKPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[AdministrationInsideUKPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryCountryGoverningTrustUserAnswersEntry: Arbitrary[(CountryGoverningTrustPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[CountryGoverningTrustPage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryGovernedInsideTheUKUserAnswersEntry: Arbitrary[(GovernedInsideTheUKPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[GovernedInsideTheUKPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryTrustNameUserAnswersEntry: Arbitrary[(TrustNamePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[TrustNamePage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }
}
