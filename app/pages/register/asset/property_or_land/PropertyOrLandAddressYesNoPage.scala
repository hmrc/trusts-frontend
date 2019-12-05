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

package pages.register.asset.property_or_land

import models.core.UserAnswers
import pages.QuestionPage
import play.api.libs.json.JsPath
import sections.Assets

import scala.util.Try

final case class PropertyOrLandAddressYesNoPage(index: Int) extends QuestionPage[Boolean] {

  override def path: JsPath = Assets.path \ index \ toString

  override def toString: String = "propertyOrLandAddressYesNo"

  override def cleanup(value: Option[Boolean], userAnswers: UserAnswers): Try[UserAnswers] =
    value match {
      case Some(false) =>
        userAnswers.remove(PropertyOrLandAddressUkYesNoPage(index))
          .flatMap(_.remove(PropertyOrLandInternationalAddressPage(index)))
          .flatMap(_.remove(PropertyOrLandUKAddressPage(index)))
      case Some(true) =>
        userAnswers.remove(PropertyOrLandDescriptionPage(index))
      case _ => super.cleanup(value, userAnswers)
    }
}