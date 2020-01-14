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

import com.google.inject.Inject
import mapping.playback.PlaybackExtractionErrors.{FailedToExtractData, PlaybackExtractionError}
import mapping.playback.PlaybackExtractor
import mapping.registration.AssetMonetaryAmount
import models.playback.UserAnswers
import models.playback.http.{Asset, DisplaySharesType, DisplayTrustAssets}
import pages.register.asset.money.AssetMoneyValuePage
import play.api.Logger

import scala.util.{Failure, Success, Try}

class AssetsExtractor @Inject()(sharesAssetExtractor: SharesAssetExtractor) extends PlaybackExtractor[DisplayTrustAssets] {
  override def extract(answers: UserAnswers, data: DisplayTrustAssets): Either[PlaybackExtractionError, UserAnswers] =  {

    val assets: List[Asset] =
      data.monetary ++
      data.propertyOrLand ++
      data.shares ++
      data.business ++
      data.partnerShip ++
      data.other

    assets match {
      case Nil =>
        Left(FailedToExtractData("Extraction error - No assets"))
      case _ =>
        extractAssets(answers, assets)
    }
  }

  def extractAssets(answers: UserAnswers, data: List[Asset]): Either[PlaybackExtractionError, UserAnswers] = {
    val updated = data.zipWithIndex.foldLeft[Try[UserAnswers]](Success(answers)){
      case (answers, (asset, index)) =>

        asset match {
          case x : AssetMonetaryAmount => extractMonetaryAsset(answers, index, x)
          case x : DisplaySharesType => sharesAssetExtractor.extract(answers, index, x)
          case _ =>
            // TODO: Restore this behaviour once all assets types are supported.
            // Failure(new RuntimeException("Unexpected asset type"))
            answers
        }
    }

    updated match {
      case Success(a) =>
        Right(a)
      case Failure(_) =>
        Logger.warn(s"[AssetsExtractor] failed to extract data")
        Left(FailedToExtractData(DisplayTrustAssets.toString))
    }
  }

  private def extractMonetaryAsset(answers: Try[UserAnswers], index: Int, asset: AssetMonetaryAmount): Try[UserAnswers] = {
    answers.flatMap(_.set(AssetMoneyValuePage(index), asset.assetMonetaryAmount.toString))
  }
}
