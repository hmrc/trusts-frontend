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

package utils

import models.{FullName, IndividualOrBusiness, UserAnswers}
import models.entities.Trustee
import pages.Trustees
import play.api.i18n.Messages
import viewmodels.TrusteeRow

class AddATrusteeViewHelper(userAnswers: UserAnswers)(implicit messages: Messages) {

  private def parseName(name : Option[FullName]) : String = {
    name match {
      case Some(x) => s"$x"
      case None => ""
    }
  }

  private def parseType(individualOrBusiness: Option[IndividualOrBusiness]) : String = {
    individualOrBusiness match {
      case Some(x) =>
        s"${messages("entity.trustee")} ${messages(s"individualOrBusiness.$x")}"
      case None =>
        messages("entity.trustee")
    }
  }

  private def parseTrustee(trustee : Trustee) : TrusteeRow = {
    TrusteeRow(
      parseName(trustee.name),
      parseType(trustee.`type`),
      "#",
      "#"
    )
  }

  def rows : List[TrusteeRow] = userAnswers.get(Trustees).toList.flatMap {
    trustees =>
      for(t <- trustees) yield parseTrustee(t)
  }

}
