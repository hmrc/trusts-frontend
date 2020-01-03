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
import models.playback.http.DisplayTrustEntitiesType

class TrusteeExtractor @Inject()(trusteesExtractor: TrusteesExtractor) extends PlaybackExtractor[DisplayTrustEntitiesType] {

  override def extract(answers: UserAnswers, data: DisplayTrustEntitiesType): Either[PlaybackExtractionError, UserAnswers] = {

    import models.playback.UserAnswersCombinator._

    val trusteesCombined = for {
      trustees <- data.trustees
      companies = trustees.flatMap(_.trusteeOrg)
      individuals = trustees.flatMap(_.trusteeInd)
    } yield  companies ++ individuals

    val trustees: List[UserAnswers] = List(
      trusteesExtractor.extract(answers, trusteesCombined)
    ).collect {
      case Right(z) => z
    }

    trustees match {
      case Nil => Left(FailedToExtractData("Trustee Extraction Error"))
      case _ => trustees.combine.map(Right.apply).getOrElse(Left(FailedToExtractData("Trustee Extraction Error")))
    }
  }

}