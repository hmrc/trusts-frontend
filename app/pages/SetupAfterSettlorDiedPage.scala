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

package pages

import models.UserAnswers
import play.api.libs.json.JsPath

import scala.util.Try

case object SetupAfterSettlorDiedPage extends QuestionPage[Boolean] {

  override def path: JsPath = JsPath \ Settlors \toString

  override def toString: String = "setupAfterSettlorDied"

  override def cleanup(value: Option[Boolean], userAnswers: UserAnswers): Try[UserAnswers] = {
    value match {
      case Some(false) =>
        userAnswers.remove(SettlorsNamePage)
        .flatMap(_.remove(SettlorDateOfDeathYesNoPage))
        .flatMap(_.remove(SettlorDateOfDeathPage))
        .flatMap(_.remove(SettlorDateOfBirthYesNoPage))
        .flatMap(_.remove(SettlorsDateOfBirthPage))
        .flatMap(_.remove(SettlorsNINoYesNoPage))
        .flatMap(_.remove(SettlorNationalInsuranceNumberPage))
        .flatMap(_.remove(SettlorsLastKnownAddressYesNoPage))
        .flatMap(_.remove(WasSettlorsAddressUKYesNoPage))
        .flatMap(_.remove(SettlorsUKAddressPage))
        .flatMap(_.remove(SettlorsInternationalAddressPage))
      case _ => super.cleanup(value, userAnswers)
    }
  }
}
