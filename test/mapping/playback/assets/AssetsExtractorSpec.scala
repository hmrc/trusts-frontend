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

package mapping.playback.assets

import base.SpecBaseHelpers
import generators.Generators
import mapping.playback.PlaybackExtractionErrors.FailedToExtractData
import mapping.playback.PlaybackExtractor
import mapping.registration.{AddressType, AssetMonetaryAmount, PropertyLandType}
import models.core.pages.{InternationalAddress, UKAddress}
import models.playback.UserAnswers
import models.playback.http.{DisplaySharesType, DisplayTrustAssets}
import models.registration.pages.ShareClass._
import models.registration.pages.ShareType.{Quoted, Unquoted}
import org.scalatest.{EitherValues, FreeSpec, MustMatchers}
import pages.register.asset.money.AssetMoneyValuePage
import pages.register.asset.property_or_land._
import pages.register.asset.shares._

class AssetsExtractorSpec extends FreeSpec with MustMatchers
  with EitherValues with Generators with SpecBaseHelpers {

  val assetsExtractor: PlaybackExtractor[DisplayTrustAssets] =
    injector.instanceOf[AssetsExtractor]

  "Assets Extractor" - {
    "when no assets" - {
      "must return an error" in {
        val assets = DisplayTrustAssets(
          monetary = Nil,
          propertyOrLand = Nil,
          shares = Nil,
          business = Nil,
          partnerShip = Nil,
          other = Nil
        )
        val ua = UserAnswers("fakeId")

        val extraction = assetsExtractor.extract(ua, assets)
        extraction mustBe Left(FailedToExtractData("Extraction error - No assets"))
      }
    }

    "when there are assets of different type" - {
      "must return user answers updated" in {
        val assets = DisplayTrustAssets(
          monetary = List(AssetMonetaryAmount(64000)),
          propertyOrLand = List(
            PropertyLandType(
              buildingLandName = Some("Property 1"),
              address = Some(AddressType(
                "line1", "line2", None, None, Some("NE11NE"), "UK"
              )),
              valueFull = Some(95L),
              valuePrevious = Some(90L)
            ),
            PropertyLandType(
              buildingLandName = Some("Property 2"),
              address = Some(AddressType(
                "line1", "line2", None, None, None, "FR"
              )),
              valueFull = Some(85L),
              valuePrevious = Some(80L)
            ),
            PropertyLandType(
              buildingLandName = Some("Property 3"),
              address = None,
              valueFull = Some(75L),
              None
            )
          ),
          shares = List(
            DisplaySharesType(
              numberOfShares = Some("1"),
              orgName = "Share 1",
              utr = Some("1234567890"),
              shareClass = Some(Ordinary),
              typeOfShare = Some(Quoted),
              value = Some(1)
            ),
            DisplaySharesType(
              numberOfShares = Some("2"),
              orgName = "Share 2",
              utr = None,
              shareClass = Some(Other),
              typeOfShare = Some(Unquoted),
              value = Some(2)
            )
          ),
          business = Nil,
          partnerShip = Nil,
          other = Nil
        )
        val ua = UserAnswers("fakeId")

        val extraction = assetsExtractor.extract(ua, assets)

        extraction mustBe 'Right

        extraction.right.value.get(AssetMoneyValuePage(0)).get mustBe "64000"

        extraction.right.value.get(PropertyOrLandAddressYesNoPage(0)).get mustBe true
        extraction.right.value.get(PropertyOrLandAddressUkYesNoPage(0)).get mustBe true
        extraction.right.value.get(PropertyOrLandUKAddressPage(0)).get mustBe UKAddress("line1", "line2", None, None, "NE11NE")
        extraction.right.value.get(PropertyOrLandInternationalAddressPage(0)) must not be defined
        extraction.right.value.get(PropertyOrLandDescriptionPage(0)).get mustBe "Property 1"
        extraction.right.value.get(PropertyOrLandTotalValuePage(0)).get mustBe "95"
        extraction.right.value.get(TrustOwnAllThePropertyOrLandPage(0)).get mustBe false
        extraction.right.value.get(PropertyLandValueTrustPage(0)).get mustBe "90"

        extraction.right.value.get(PropertyOrLandAddressYesNoPage(1)).get mustBe true
        extraction.right.value.get(PropertyOrLandAddressUkYesNoPage(1)).get mustBe false
        extraction.right.value.get(PropertyOrLandInternationalAddressPage(1)).get mustBe InternationalAddress("line1", "line2", None, "FR")
        extraction.right.value.get(PropertyOrLandUKAddressPage(1)) must not be defined
        extraction.right.value.get(PropertyOrLandDescriptionPage(1)).get mustBe "Property 2"
        extraction.right.value.get(PropertyOrLandTotalValuePage(1)).get mustBe "85"
        extraction.right.value.get(TrustOwnAllThePropertyOrLandPage(1)).get mustBe false
        extraction.right.value.get(PropertyLandValueTrustPage(1)).get mustBe "80"

        extraction.right.value.get(PropertyOrLandAddressYesNoPage(2)).get mustBe false
        extraction.right.value.get(PropertyOrLandAddressUkYesNoPage(2)) must not be defined
        extraction.right.value.get(PropertyOrLandUKAddressPage(2)) must not be defined
        extraction.right.value.get(PropertyOrLandInternationalAddressPage(2)) must not be defined
        extraction.right.value.get(PropertyOrLandDescriptionPage(2)).get mustBe "Property 3"
        extraction.right.value.get(PropertyOrLandTotalValuePage(2)).get mustBe "75"
        extraction.right.value.get(TrustOwnAllThePropertyOrLandPage(2)).get mustBe true
        extraction.right.value.get(PropertyLandValueTrustPage(2)) mustBe None

        extraction.right.value.get(SharesInAPortfolioPage(4)).get mustBe false
        extraction.right.value.get(ShareCompanyNamePage(4)).get mustBe "Share 1"
        extraction.right.value.get(ShareUtrPage(4)).get mustBe "1234567890"
        extraction.right.value.get(SharesOnStockExchangePage(4)).get mustBe true
        extraction.right.value.get(ShareClassPage(4)).get mustBe Ordinary
        extraction.right.value.get(ShareQuantityInTrustPage(4)).get mustBe "1"
        extraction.right.value.get(ShareValueInTrustPage(4)).get mustBe "1"

        extraction.right.value.get(SharesInAPortfolioPage(5)).get mustBe true
        extraction.right.value.get(SharePortfolioNamePage(5)).get mustBe "Share 2"
        extraction.right.value.get(ShareUtrPage(5)) mustNot be(defined)
        extraction.right.value.get(SharesOnStockExchangePage(5)).get mustBe false
        extraction.right.value.get(ShareClassPage(5)).get mustBe Other
        extraction.right.value.get(SharePortfolioQuantityInTrustPage(5)).get mustBe "2"
        extraction.right.value.get(ShareValueInTrustPage(5)).get mustBe "2"
      }
    }
  }
}
