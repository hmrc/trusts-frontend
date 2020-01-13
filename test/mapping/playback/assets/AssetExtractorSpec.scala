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
import models.playback.UserAnswers
import models.playback.http._
import models.registration.pages.ShareClass
import models.registration.pages.ShareClass.Ordinary
import models.registration.pages.ShareType.Quoted
import org.scalatest.{EitherValues, FreeSpec, MustMatchers}
import pages.register.asset.shares._

class AssetExtractorSpec extends FreeSpec with MustMatchers
  with EitherValues with Generators with SpecBaseHelpers {

  val assetExtractor : PlaybackExtractor[DisplayTrustAssets] =
    injector.instanceOf[AssetExtractor]

  "Asset Extractor" - {

    "when no asset" - {

      "must return an error" in {

        val beneficiary = DisplayTrustAssets(None, None, None, None, None, None)

        val ua = UserAnswers("fakeId")

        val extraction = assetExtractor.extract(ua, beneficiary)

        extraction.left.value mustBe a[FailedToExtractData]

      }

    }

    "when there are assets of different type" - {

      "must return user answers updated" in {
        val asset = DisplayTrustAssets(
          monetary = None,
          propertyOrLand = None,
          shares = Some(
            List(
              DisplaySharesType(
                numberOfShares = "1",
                orgName = "Share 1",
                shareClass = Ordinary,
                typeOfShare = Quoted,
                value = 1
              )
            )
          ),
          business = None,
          partnerShip = None,
          other = None
        )

        val ua = UserAnswers("fakeId")

        val extraction = assetExtractor.extract(ua, asset)

        extraction.right.value.get(SharesInAPortfolioPage(0)).get mustBe false
        extraction.right.value.get(ShareCompanyNamePage(0)).get mustBe "Share 1"
        extraction.right.value.get(SharesOnStockExchangePage(0)).get mustBe true
        extraction.right.value.get(ShareClassPage(0)).get mustBe ShareClass.Ordinary
        extraction.right.value.get(ShareQuantityInTrustPage(0)).get mustBe "1"
        extraction.right.value.get(ShareValueInTrustPage(0)).get mustBe "1"

      }

    }

  }

}
