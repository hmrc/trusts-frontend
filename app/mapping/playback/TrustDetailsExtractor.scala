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
import mapping.registration.TrustDetailsType
import models.playback.UserAnswers
import pages.register._
import play.api.Logger

import scala.util.{Failure, Success, Try}

class TrustDetailsExtractor @Inject() extends PlaybackExtractor[Option[TrustDetailsType]] {

  override def extract(answers: UserAnswers, data: Option[TrustDetailsType]): Either[PlaybackExtractionError, UserAnswers] =
    {
      data match {
        case None => Left(FailedToExtractData("No Trustee Details"))
        case trust =>

          val updated: Try[UserAnswers] = trust.foldLeft[Try[UserAnswers]](Success(answers)){
            case (answers, details) =>
              answers
                .flatMap(_.set(WhenTrustSetupPage, details.startDate))
                .flatMap(answers => extractGovernedBy(details, answers))
          }

          updated match {
            case Success(a) =>
              Right(a)
            case Failure(exception) =>
              Logger.warn(s"[TrustDetailsExtractor] failed to extract data due to ${exception.getMessage}")
              Left(FailedToExtractData(TrustDetailsType.toString))
          }
      }
    }

  private def extractGovernedBy(details: TrustDetailsType, answers: UserAnswers): Try[UserAnswers] =
    details.lawCountry match {
      case Some(country) => answers.set(GovernedInsideTheUKPage, false)
        .flatMap(_.set(CountryGoverningTrustPage, country))
      case _ => answers.set(GovernedInsideTheUKPage, true)
    }

}