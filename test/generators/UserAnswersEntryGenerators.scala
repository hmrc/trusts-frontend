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

import models.core.pages._
import models.registration.pages._
import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary.arbitrary
import pages.register._
import pages.register.agents._
import pages.register.asset.money.AssetMoneyValuePage
import pages.register.asset.partnership._
import pages.register.asset.property_or_land._
import pages.register.asset.shares._
import pages.register.asset.{AddAnAssetYesNoPage, AddAssetsPage, WhatKindOfAssetPage}
import pages.register.settlors.living_settlor._
import pages.register.settlors.living_settlor.trust_type.{HoldoverReliefYesNoPage, KindOfTrustPage}
import play.api.libs.json.{JsValue, Json}

trait UserAnswersEntryGenerators extends PageGenerators with ModelGenerators {

  implicit lazy val arbitraryPartnershipStartDateUserAnswersEntry: Arbitrary[(PartnershipStartDatePage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[PartnershipStartDatePage]
        value <- arbitrary[Int].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryPartnershipDescriptionUserAnswersEntry: Arbitrary[(PartnershipDescriptionPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[PartnershipDescriptionPage]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryHoldoverReliefYesNoUserAnswersEntry: Arbitrary[(HoldoverReliefYesNoPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[HoldoverReliefYesNoPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryKindOfTrustUserAnswersEntry: Arbitrary[(KindOfTrustPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[KindOfTrustPage.type]
        value <- arbitrary[KindOfTrust].map(Json.toJson(_))
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
        value <- arbitrary[Declaration].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryAgentInternationalAddressUserAnswersEntry: Arbitrary[(AgentInternationalAddressPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[AgentInternationalAddressPage.type]
        value <- arbitrary[InternationalAddress].map(Json.toJson(_))
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

  implicit lazy val arbitraryAgentNameUserAnswersEntry: Arbitrary[(AgentNamePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[AgentNamePage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
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
}
