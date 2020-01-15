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

import mapping.playback.PlaybackImplicits._
import mapping.registration.PropertyLandType
import models.core.pages.{InternationalAddress, UKAddress}
import models.playback.UserAnswers
import pages.register.asset.property_or_land._

import scala.util.Try

class PropertyOrLandAssetExtractor {

  def extract(answers: Try[UserAnswers], index: Int, data: PropertyLandType): Try[UserAnswers] = {
    answers
      .flatMap(_.set(PropertyOrLandDescriptionPage(index), data.buildingLandName))
      .flatMap(answers => extractAddress(index, data, answers))
      .flatMap(_.set(PropertyOrLandTotalValuePage(index), data.valueFull.map(_.toString)))
      .flatMap(answers => extractOwnership(index, data, answers))
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
