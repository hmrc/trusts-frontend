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
import mapping.playback.{PlaybackExtractor, PlaybackImplicits}
import mapping.registration.PropertyLandType
import models.playback.UserAnswers

class PropertyLandExtractor @Inject() extends PlaybackExtractor[Option[List[PropertyLandType]]] {

  import PlaybackImplicits._

  override def extract(answers: UserAnswers, data: Option[List[PropertyLandType]]): Either[PlaybackExtractionError, UserAnswers] =
    data match {
      case None => Left(FailedToExtractData("No PropertyOrLand Asset"))
    }

}
