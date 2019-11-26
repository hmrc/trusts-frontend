/*
 * Copyright 2019 HM Revenue & Customs
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

import cats.kernel.Semigroup
import com.google.inject.Inject
import mapping.playback.PlaybackExtractionErrors._
import models.playback.UserAnswers
import play.api.Logger

class UserAnswersExtractor @Inject()(charity: CharityBeneficiaryExtractor) extends PlaybackAnswerCombiner {

  import models.playback.UserAnswersCombinator._

  override def extract(answers: UserAnswers): Either[PlaybackExtractionError, UserAnswers] = {

    val answersCombined = for {
      ua <- charity.extract(answers, Nil).right
      // add in further extractors here for LeadTrustee, Trustees, Beneficiaries etc
      ua2 <- charity.extract(answers, Nil).right
    } yield {
        for {
          combined <- Semigroup[UserAnswers].combineAllOption(List(ua, ua2))
        } yield combined
    }

    answersCombined match {
      case Left(error) =>
        Logger.error(s"[PlaybackToUserAnswers] failed to unpack data to user answers, failed for $error")
        Left(FailedToExtractData)
      case Right(None) =>
        Logger.error(s"[PlaybackToUserAnswers] failed to combine user answers")
        Left(FailedToCombineAnswers)
      case Right(Some(ua)) =>
        Right(ua)
    }
  }
}
