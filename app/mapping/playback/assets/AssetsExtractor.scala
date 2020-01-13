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
import mapping.playback.PlaybackExtractionErrors.FailedToExtractData
import mapping.playback.{PlaybackExtractionErrors, PlaybackExtractor}
import models.playback.UserAnswers
import models.playback.UserAnswersCombinator._
import models.playback.http.DisplayTrustAssets
import pages.register.asset.money.AssetMoneyValuePage

import scala.util.Success

class AssetsExtractor @Inject() extends PlaybackExtractor[DisplayTrustAssets] {
  override def extract(answers: UserAnswers, data: DisplayTrustAssets): Either[PlaybackExtractionErrors.PlaybackExtractionError, UserAnswers] =  {
    val assets: List[UserAnswers] = List(
      extractMonetaryAsset(answers, data)
    ).collect {
      case Right(x) => x
    }

    assets match {
      case Nil => Left(AssetsExtractionError)
      case _ => assets.combine.map(Right.apply).getOrElse(Left(AssetsExtractionError))
    }
  }

  private object AssetsExtractionError extends FailedToExtractData("Assets Extraction Error")
  private object MonetaryExtractionError extends FailedToExtractData("Monetary Extraction Error")

  private def extractMonetaryAsset(answers: UserAnswers, data: DisplayTrustAssets): Either[PlaybackExtractionErrors.PlaybackExtractionError, UserAnswers] = {
    data.monetary match {
      case Some(monetary :: Nil) =>
        answers.set(AssetMoneyValuePage(0), monetary.assetMonetaryAmount.toString) match {
         case Success(a) => Right(a)
         case _ => Left(MonetaryExtractionError)
        }
      case _ => Left(MonetaryExtractionError)
    }
  }
}
