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

package pages.register.trustees

import models.core.UserAnswers
import pages.QuestionPage
import play.api.libs.json.JsPath
import sections.Trustees

import scala.util.Try

final case class IsThisLeadTrusteePage(index : Int) extends QuestionPage[Boolean] {

  override def path: JsPath = Trustees.path \ index \ toString

  override def toString: String = "isThisLeadTrustee"

  override def cleanup(value: Option[Boolean], userAnswers: UserAnswers): Try[UserAnswers] = {
    value match {
      case Some(false) =>
        userAnswers.remove(TrusteeIndividualOrBusinessPage(index))

          .flatMap(_.remove(TrusteesNamePage(index)))
          .flatMap(_.remove(TrusteesDateOfBirthPage(index)))
          .flatMap(_.remove(TrusteeAUKCitizenPage(index)))
          .flatMap(_.remove(TrusteesNinoPage(index)))
          .flatMap(_.remove(TrusteeAddressInTheUKPage(index)))
          .flatMap(_.remove(TrusteesUkAddressPage(index)))

          .flatMap(_.remove(TrusteeUtrYesNoPage(index)))
          .flatMap(_.remove(TrusteeOrgNamePage(index)))
          .flatMap(_.remove(TrusteesUtrPage(index)))
          .flatMap(_.remove(TrusteeOrgAddressUkYesNoPage(index)))
          .flatMap(_.remove(TrusteeOrgAddressUkPage(index)))
          .flatMap(_.remove(TrusteeOrgAddressInternationalPage(index)))

          .flatMap(_.remove(TelephoneNumberPage(index)))

      case _ => super.cleanup(value, userAnswers)
    }
  }
}
