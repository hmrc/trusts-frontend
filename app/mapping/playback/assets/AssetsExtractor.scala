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

import mapping.playback.PlaybackExtractionErrors.{FailedToExtractData, InvalidExtractorState}
import mapping.playback.{PlaybackExtractionErrors, PlaybackExtractor}
import models.playback.UserAnswers
import models.playback.http.DisplayTrustAssets
import pages.register.asset.money.AssetMoneyValuePage
import models.playback.UserAnswersCombinator._

import scala.util.{Failure, Success, Try}

class AssetsExtractor extends PlaybackExtractor[DisplayTrustAssets] {
  override def extract(answers: UserAnswers, data: DisplayTrustAssets): Either[PlaybackExtractionErrors.PlaybackExtractionError, UserAnswers] =  {
    val assets: List[UserAnswers] = List(
      extractMonetaryAsset(answers, data)
    ).collect {
      case Right(x) => x
    }

    assets match {
      case Nil => Left(FailedToExtractData("Assets Extraction Error"))
      case _ => assets.combine.map(Right.apply).getOrElse(Left(FailedToExtractData("Assets Extraction Error")))
    }
  }

  private def extractMonetaryAsset(answers: UserAnswers, data: DisplayTrustAssets): Either[PlaybackExtractionErrors.PlaybackExtractionError, UserAnswers] = {
    (data.monetary match {
      case Some(monetary :: Nil) =>
        answers.set(AssetMoneyValuePage(0), monetary.assetMonetaryAmount.toString)
      case _ => Failure(InvalidExtractorState)
    }) match {
      case Success(a) => Right(a)
      case _ => Left(FailedToExtractData("Monetary Extraction Error"))
    }
  }
}
