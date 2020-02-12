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
import models.core.pages.{Address, InternationalAddress, UKAddress}
import pages.register.trust_details.{CorrespondenceAbroadIndicatorPage, CorrespondenceAddressInTheUKPage, CorrespondenceAddressPage, CorrespondenceBpMatchStatusPage, CorrespondencePhoneNumberPage, TrustNamePage}

import scala.util.{Failure, Success}

class CorrespondenceExtractor @Inject() extends PlaybackExtractor[Correspondence] {

  import PlaybackImplicits._

  override def extract(answers: UserAnswers, data: Correspondence): Either[PlaybackExtractionError, UserAnswers] =
  {
    val updated = answers
      .set(CorrespondenceAbroadIndicatorPage, data.abroadIndicator)
      .flatMap(_.set(TrustNamePage, data.name))
      .flatMap(answers => extractAddress(data.address.convert, answers))
      .flatMap(_.set(CorrespondenceBpMatchStatusPage, data.bpMatchStatus))
      .flatMap(_.set(CorrespondencePhoneNumberPage, data.phoneNumber))

    updated match {
      case Success(a) =>
        Right(a)
      case Failure(exception) =>
        Logger.warn(s"[CorrespondenceExtractor] failed to extract data due to ${exception.getMessage}")
        Left(FailedToExtractData(Correspondence.toString))
    }
  }

  private def extractAddress(address: Address, answers: UserAnswers) = {
    address match {
      case uk: UKAddress => answers.set(CorrespondenceAddressPage, uk)
        .flatMap(_.set(CorrespondenceAddressInTheUKPage, true))
      case nonUk: InternationalAddress => answers.set(CorrespondenceAddressPage, nonUk)
        .flatMap(_.set(CorrespondenceAddressInTheUKPage, false))
    }
  }

}
