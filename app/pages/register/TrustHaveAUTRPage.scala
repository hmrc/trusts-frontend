/*
 * Copyright 2026 HM Revenue & Customs
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

package pages.register

import models.core.TrustsFrontendUserAnswers
import pages.QuestionPage
import pages.register.suitability.{TaxLiabilityInCurrentTaxYearYesNoPage, UndeclaredTaxLiabilityYesNoPage}
import play.api.libs.json.JsPath

import scala.util.Try

case object TrustHaveAUTRPage extends QuestionPage[Boolean] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "trustHaveAUTR"

  override def cleanup[U <: TrustsFrontendUserAnswers[U]](value: Option[Boolean], userAnswers: U): Try[U] =
    value match {
      case Some(false) =>
        userAnswers
          .remove(WhatIsTheUTRPage)
          .flatMap(_.remove(MatchingNamePage))
          .flatMap(_.remove(PostcodeForTheTrustPage))
      case Some(true)  =>
        userAnswers
          .remove(TaxLiabilityInCurrentTaxYearYesNoPage)
          .flatMap(_.remove(UndeclaredTaxLiabilityYesNoPage))
      case _           =>
        super.cleanup(value, userAnswers)
    }

}
