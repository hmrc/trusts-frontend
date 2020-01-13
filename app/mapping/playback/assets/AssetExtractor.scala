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
import models.playback.UserAnswers
import models.playback.http.DisplayTrustAssets

class AssetExtractor @Inject()(sharesAssetExtractor: SharesAssetExtractor
                              ) extends PlaybackExtractor[DisplayTrustAssets] {

  override def extract(answers: UserAnswers, data: DisplayTrustAssets): Either[PlaybackExtractionError, UserAnswers] = {

    import models.playback.UserAnswersCombinator._

    val assets: List[UserAnswers] = List(
      sharesAssetExtractor.extract(answers, data.shares)
    ).collect {
      case Right(z) => z
    }
    
    assets match {
      case Nil => Left(FailedToExtractData("Asset Extraction Error"))
      case _ => assets.combine.map(Right.apply).getOrElse(Left(FailedToExtractData("Asset Extraction Error")))
    }
  }
}