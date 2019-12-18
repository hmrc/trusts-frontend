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

import com.google.inject.Inject
import mapping.playback.PlaybackExtractionErrors._
import mapping.playback.beneficiaries.BeneficiaryExtractor
import mapping.playback.settlors.SettlorExtractor
import models.playback.UserAnswers
import models.playback.http.GetTrust
import play.api.Logger

class UserAnswersExtractor @Inject()(beneficiary: BeneficiaryExtractor,
                                     leadTrustee: LeadTrusteeExtractor,
                                     settlors: SettlorExtractor
                                    ) extends PlaybackExtractor[GetTrust] {

  import models.playback.UserAnswersCombinator._

  override def extract(answers: UserAnswers, data: GetTrust): Either[PlaybackExtractionError, UserAnswers] = {

    val answersCombined = for {
      ua <- beneficiary.extract(answers, data.trust.entities.beneficiary).right
      ua1 <- leadTrustee.extract(answers, data.trust.entities.leadTrustee).right
      ua2 <- settlors.extract(answers, data.trust.entities).right
    } yield {
      List(ua, ua1, ua2).combine
    }

    answersCombined match {
      case Left(error) =>
        Logger.error(s"[PlaybackToUserAnswers] failed to unpack data to user answers, failed for $error")
        Left(error)
      case Right(None) =>
        Logger.error(s"[PlaybackToUserAnswers] failed to combine user answers")
        Left(FailedToCombineAnswers)
      case Right(Some(ua)) =>
        Right(ua)
    }
  }
}
