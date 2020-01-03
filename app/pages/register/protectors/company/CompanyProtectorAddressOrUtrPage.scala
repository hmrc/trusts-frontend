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

package pages.register.protectors.company

import models.core.UserAnswers
import models.registration.pages.AddressOrUtr
import models.registration.pages.AddressOrUtr.{Address, Utr}
import pages.QuestionPage
import play.api.libs.json.JsPath
import sections.protectors.{CompanyProtectors, Protectors}

import scala.util.Try

case class CompanyProtectorAddressOrUtrPage(index: Int) extends QuestionPage[AddressOrUtr] {

  override def path: JsPath = JsPath \ Protectors \ CompanyProtectors \ index \ toString

  override def toString: String = "addressOrUtr"

  override def cleanup(value: Option[AddressOrUtr], userAnswers: UserAnswers): Try[UserAnswers] =
    value match {
      case Some(Address) => userAnswers.remove(CompanyProtectorUtrPage(index))
      case Some(Utr) => userAnswers.remove(CompanyProtectorAddressUKYesNoPage(index))
        .flatMap(_.remove(CompanyProtectorAddressPage(index)))
      case _ => super.cleanup(value, userAnswers)
    }
}
