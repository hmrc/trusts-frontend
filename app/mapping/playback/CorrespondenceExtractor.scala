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

package mapping.playback

import com.google.inject.Inject
import mapping.playback.PlaybackExtractionErrors.{FailedToExtractData, PlaybackExtractionError}
import models.playback.UserAnswers
import models.playback.http.Correspondence
import pages.register._
import play.api.Logger

import scala.util.{Failure, Success, Try}

class CorrespondenceExtractor @Inject() extends PlaybackExtractor[Correspondence] {

  override def extract(answers: UserAnswers, data: Correspondence): Either[PlaybackExtractionError, UserAnswers] =
    {
      answers.set(TrustNamePage, data.name) match {
        case Success(a) =>
          Right(a)
        case Failure(exception) =>
          Logger.warn(s"[Correspondence] failed to extract data due to ${exception.getMessage}")
          Left(FailedToExtractData(Correspondence.toString))
      }

    }

}
