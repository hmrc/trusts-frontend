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
import pages.deceased_settlor.{SettlorDateOfBirthYesNoPage, SettlorDateOfDeathPage, SettlorDateOfDeathYesNoPage, SettlorNationalInsuranceNumberPage, SettlorsDateOfBirthPage, SettlorsInternationalAddressPage, SettlorsLastKnownAddressYesNoPage, SettlorsNINoYesNoPage, SettlorsNamePage, SettlorsUKAddressPage, WasSettlorsAddressUKYesNoPage}
import pages.entitystatus.DeceasedSettlorStatus
import pages.living_settlor.SettlorIndividualOrBusinessPage
import play.api.libs.json.JsPath
import sections.{LivingSettlors, Settlors}

import scala.util.Try

case object SetupAfterSettlorDiedPage extends QuestionPage[Boolean] {

  override def path: JsPath = Settlors.path \toString

  override def toString: String = "setupAfterSettlorDied"

  override def cleanup(value: Option[Boolean], userAnswers: UserAnswers): Try[UserAnswers] = {
    value match {
      case Some(false) =>
        // becoming a living settlor
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
        .flatMap(_.remove(DeceasedSettlorStatus))
      case Some(true) =>
        // becoming a will type trust
        userAnswers.remove(SettlorKindOfTrustPage)
          .flatMap(_.remove(SettlorHandoverReliefYesNoPage))
          .flatMap(_.remove(LivingSettlors))
      case _ => super.cleanup(value, userAnswers)
    }
  }
}
