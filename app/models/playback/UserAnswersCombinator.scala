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

package models.playback

import java.time.LocalDateTime

import cats.kernel.Semigroup
import mapping.playback.PlaybackExtractionErrors.FailedToCombineAnswers

import scala.util.{Failure, Success, Try}

object UserAnswersCombinator {

  implicit val userAnswersSemigroup : Semigroup[Try[UserAnswers]] = new Semigroup[Try[UserAnswers]] {

    override def combine(x: Try[UserAnswers], y: Try[UserAnswers]): Try[UserAnswers] = {

      for {
        ua1 <- x
        ua2 <- y
      } yield {

        UserAnswers(
          data = ua1.data ++ ua2.data,
          internalAuthId = ua1.internalAuthId,
          updatedAt = LocalDateTime.now
        )

      }
    }
  }

  implicit class Combinator(answers: List[Try[UserAnswers]]) {

    def combine = {
      Semigroup[Try[UserAnswers]].combineAllOption(answers) match {
        case Some(userAnswers) => userAnswers
        case None => Failure(FailedToCombineAnswers)
      }
    }

  }

  implicit class UserAnswersCollector(answers: List[Try[UserAnswers]]) {

    def collectAnswers : List[UserAnswers] = answers.collect {
      case Success(answer) => answer
    }

  }

}
