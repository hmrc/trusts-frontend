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
import mapping.playback.PlaybackExtractor
import mapping.registration.AssetMonetaryAmount
import models.playback.UserAnswers
import models.playback.http.DisplayTrustAssets
import org.scalatest.{EitherValues, FreeSpec, MustMatchers}
import pages.register.asset.money.AssetMoneyValuePage

class AssetsExtractorSpec extends FreeSpec with MustMatchers
  with EitherValues with Generators with SpecBaseHelpers {

  val assetsExtractor: PlaybackExtractor[DisplayTrustAssets] =
    injector.instanceOf[AssetsExtractor]

  "Assets Extractor" - {
    "when no assets" - {
      "must return an error" in {
        val assets = DisplayTrustAssets(
          monetary = None,
          propertyOrLand = None,
          shares = None,
          business = None,
          partnerShip = None,
          other = None
        )
        val ua = UserAnswers("fakeId")

        val extraction = assetsExtractor.extract(ua, assets)

//        TODO: Restore the following behaviour once all asset types are supported
//        extraction mustBe Left(FailedToExtractData("Assets Extraction Error"))
          extraction mustBe 'Right
      }
    }

    "when there is a monetary amount" - {
      "must return that amount" in {
        val monetaryAmount = List(AssetMonetaryAmount(64000))
        val assets = DisplayTrustAssets(
          monetary = Some(monetaryAmount),
          propertyOrLand = None,
          shares = None,
          business = None,
          partnerShip = None,
          other = None
        )
        val ua = UserAnswers("fakeId")

        val extraction = assetsExtractor.extract(ua, assets)

        extraction mustBe 'Right
        extraction.right.value.get(AssetMoneyValuePage(0)).get mustBe "64000"
      }
    }
  }
}
