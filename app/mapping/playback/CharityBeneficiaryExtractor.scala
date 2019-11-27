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
import mapping.playback.PlaybackExtractionErrors.{FailedToExtractData, PlaybackExtractionError}
import models.playback.{DisplayTrustCharityType, UserAnswers}
import pages.beneficiaries.charity.CharityBeneficiaryNamePage
import play.api.Logger

import scala.util.{Failure, Success, Try}

class CharityBeneficiaryExtractor @Inject() extends PlaybackExtractor[List[DisplayTrustCharityType]] {

  override def extract(answers: UserAnswers, data: List[DisplayTrustCharityType]): Either[PlaybackExtractionError, Try[UserAnswers]] =
    {
      data match {
        case Nil => Right(Success(answers))
        case charities =>

          val updated = charities.zipWithIndex.foldLeft[Try[UserAnswers]](Success(answers)){ (answers, y) =>
            answers.flatMap(_.set(CharityBeneficiaryNamePage(y._2), y._1.organisationName))
          }

          updated match {
            case Success(a) =>
              Right(Success(a))
            case Failure(exception) =>
              Logger.warn(s"$exception")
              Left(FailedToExtractData)
          }
      }
    }

}