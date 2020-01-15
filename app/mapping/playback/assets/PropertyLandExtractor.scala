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
              .flatMap(answers => extractAddress(index, asset, answers))
              .flatMap(_.set(PropertyOrLandTotalValuePage(index), asset.valueFull.map(_.toString)))
              .flatMap(answers => extractOwnership(index, asset, answers))
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

  private def extractAddress(index: Int, data: PropertyLandType, userAnswers: UserAnswers) = {
    data.address.convert match {
      case Some(uk: UKAddress) =>
        userAnswers.set(PropertyOrLandUKAddressPage(index), uk)
          .flatMap(_.set(PropertyOrLandAddressUkYesNoPage(index), true))
          .flatMap(_.set(PropertyOrLandAddressYesNoPage(index), true))
      case Some(nonUk: InternationalAddress) =>
        userAnswers.set(PropertyOrLandInternationalAddressPage(index), nonUk)
          .flatMap(_.set(PropertyOrLandAddressUkYesNoPage(index), false))
          .flatMap(_.set(PropertyOrLandAddressYesNoPage(index), true))
      case None => userAnswers.set(PropertyOrLandAddressYesNoPage(index), false)
    }
  }

  private def extractOwnership(index: Int, data: PropertyLandType, userAnswers: UserAnswers) = {
    data.valuePrevious match {
      case Some(value) => userAnswers.set(PropertyLandValueTrustPage(index), value.toString)
          .flatMap(_.set(TrustOwnAllThePropertyOrLandPage(index), false))
      case _ => userAnswers.set(TrustOwnAllThePropertyOrLandPage(index), true)
    }
  }

}
