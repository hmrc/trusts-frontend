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
import mapping.registration.AssetMonetaryAmount
import models.playback.UserAnswers
import models.playback.http.{DisplaySharesType, DisplayTrustAssets}
import models.registration.pages.ShareClass._
import models.registration.pages.ShareType.{Quoted, Unquoted}
import org.scalatest.{EitherValues, FreeSpec, MustMatchers}
import pages.register.asset.money.AssetMoneyValuePage
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
          propertyOrLand = Nil,
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

        extraction.right.value.get(SharesInAPortfolioPage(1)).get mustBe false
        extraction.right.value.get(ShareCompanyNamePage(1)).get mustBe "Share 1"
        extraction.right.value.get(ShareUtrPage(1)).get mustBe "1234567890"
        extraction.right.value.get(SharesOnStockExchangePage(1)).get mustBe true
        extraction.right.value.get(ShareClassPage(1)).get mustBe Ordinary
        extraction.right.value.get(ShareQuantityInTrustPage(1)).get mustBe "1"
        extraction.right.value.get(ShareValueInTrustPage(1)).get mustBe "1"

        extraction.right.value.get(SharesInAPortfolioPage(2)).get mustBe true
        extraction.right.value.get(SharePortfolioNamePage(2)).get mustBe "Share 2"
        extraction.right.value.get(ShareUtrPage(2)) mustNot be(defined)
        extraction.right.value.get(SharesOnStockExchangePage(2)).get mustBe false
        extraction.right.value.get(ShareClassPage(2)).get mustBe Other
        extraction.right.value.get(SharePortfolioQuantityInTrustPage(2)).get mustBe "2"
        extraction.right.value.get(ShareValueInTrustPage(2)).get mustBe "2"
      }
    }
  }
}
