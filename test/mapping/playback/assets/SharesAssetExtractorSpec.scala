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
import models.playback.UserAnswers
import models.playback.http._
import models.registration.pages.ShareClass
import models.registration.pages.ShareClass._
import models.registration.pages.ShareType._
import org.scalatest.{EitherValues, FreeSpec, MustMatchers}
import pages.register.asset.shares._

class SharesAssetExtractorSpec extends FreeSpec with MustMatchers
  with EitherValues with Generators with SpecBaseHelpers {

  def generateAsset(index: Int) = DisplaySharesType(
    numberOfShares = s"$index",
    orgName = s"Share $index",
    shareClass = index match {
      case 0 => Ordinary
      case _ => Other
    },
    typeOfShare = index match {
      case 0 => Quoted
      case _ => Unquoted
    },
    value = index.toLong
  )

  val sharesExtractor : PlaybackExtractor[Option[List[DisplaySharesType]]] =
    injector.instanceOf[SharesAssetExtractor]

  "Shares Asset Extractor" - {

    "when no shares" - {

      "must return user answers" in {

        val shares = None

        val ua = UserAnswers("fakeId")

        val extraction = sharesExtractor.extract(ua, shares)

        extraction mustBe 'left

      }

    }

    "when there are shares" - {

      "with minimum data must return user answers updated" in {
        val shares = List(
          DisplaySharesType(
            numberOfShares = "1",
            orgName = "Share 1",
            shareClass = Ordinary,
            typeOfShare = Quoted,
            value = 1
          )
        )

        val ua = UserAnswers("fakeId")

        val extraction = sharesExtractor.extract(ua, Some(shares))

        extraction.right.value.get(SharesInAPortfolioPage(0)).get mustBe false
        extraction.right.value.get(ShareCompanyNamePage(0)).get mustBe "Share 1"
        extraction.right.value.get(SharesOnStockExchangePage(0)).get mustBe true
        extraction.right.value.get(ShareClassPage(0)).get mustBe ShareClass.Ordinary
        extraction.right.value.get(ShareQuantityInTrustPage(0)).get mustBe "1"
        extraction.right.value.get(ShareValueInTrustPage(0)).get mustBe "1"
      }

      "with full data must return user answers updated" in {
        val charities = (for(index <- 0 to 2) yield generateAsset(index)).toList

        val ua = UserAnswers("fakeId")

        val extraction = sharesExtractor.extract(ua, Some(charities))

        extraction mustBe 'right

        extraction.right.value.get(SharesInAPortfolioPage(0)).get mustBe false
        extraction.right.value.get(ShareCompanyNamePage(0)).get mustBe "Share 0"
        extraction.right.value.get(SharesOnStockExchangePage(0)).get mustBe true
        extraction.right.value.get(ShareClassPage(0)).get mustBe Ordinary
        extraction.right.value.get(ShareQuantityInTrustPage(0)).get mustBe "0"
        extraction.right.value.get(ShareValueInTrustPage(0)).get mustBe "0"

        extraction.right.value.get(SharesInAPortfolioPage(1)).get mustBe true
        extraction.right.value.get(SharePortfolioNamePage(1)).get mustBe "Share 1"
        extraction.right.value.get(SharesOnStockExchangePage(1)).get mustBe false
        extraction.right.value.get(ShareClassPage(1)).get mustBe Other
        extraction.right.value.get(SharePortfolioQuantityInTrustPage(1)).get mustBe "1"
        extraction.right.value.get(ShareValueInTrustPage(1)).get mustBe "1"

        extraction.right.value.get(SharesInAPortfolioPage(2)).get mustBe true
        extraction.right.value.get(SharePortfolioNamePage(2)).get mustBe "Share 2"
        extraction.right.value.get(SharesOnStockExchangePage(2)).get mustBe false
        extraction.right.value.get(ShareClassPage(2)).get mustBe Other
        extraction.right.value.get(SharePortfolioQuantityInTrustPage(2)).get mustBe "2"
        extraction.right.value.get(ShareValueInTrustPage(2)).get mustBe "2"
      }

    }

  }

}
