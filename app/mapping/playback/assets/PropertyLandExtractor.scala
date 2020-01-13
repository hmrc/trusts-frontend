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
import models.core.pages.{InternationalAddress, UKAddress}
import models.playback.UserAnswers
import pages.register.asset.property_or_land._
import play.api.Logger
import PlaybackImplicits._

import scala.util.{Failure, Success, Try}

class PropertyLandExtractor @Inject() extends PlaybackExtractor[Option[List[PropertyLandType]]] {

  override def extract(answers: UserAnswers, data: Option[List[PropertyLandType]]): Either[PlaybackExtractionError, UserAnswers] = {
    data match {
      case None => Left(FailedToExtractData("No PropertyOrLand Asset"))
      case Some(assets) =>

        val updated = assets.zipWithIndex.foldLeft[Try[UserAnswers]](Success(answers)){
          case (answers, (asset, index)) =>

            answers
              .flatMap(_.set(PropertyOrLandDescriptionPage(index), asset.buildingLandName))
        }

        updated match {
          case Success(a) =>
            Right(a)
          case Failure(exception) =>
            Logger.warn(s"[PropertyLandExtractor] failed to extract data due to ${exception.getMessage}")
            Left(FailedToExtractData(PropertyLandType.toString))
        }
    }
  }

  private def extractAddress(data: PropertyLandType, userAnswers: UserAnswers) = {
    data.address.convert match {
      case Some(uk: UKAddress) =>
        userAnswers.set(???, uk)
          .flatMap(_.set(???, true))
      case Some(nonUk: InternationalAddress) =>
        userAnswers.set(???, nonUk)
          .flatMap(_.set(???, false))
      case None => Success(userAnswers)
    }
  }

}
