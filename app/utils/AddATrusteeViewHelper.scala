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

import models.UserAnswers
import models.entities.{LeadTrusteeIndividual, Trustee, TrusteeIndividual}
import pages.Trustees
import play.api.i18n.Messages
import viewmodels.{AddRow, AddToRows}

class AddATrusteeViewHelper(userAnswers: UserAnswers)(implicit messages: Messages) {

  private def parseTrustee(trustee : Trustee) : AddRow = {

    val name = trustee match {
      case t : LeadTrusteeIndividual => t.name.toString
      case t : TrusteeIndividual => t.name.toString
      case _ => ""
    }

    val trusteeType = trustee match {
      case _ : LeadTrusteeIndividual => s"${messages("entities.lead")} ${messages("entities.trustee.individual")}"
      case _ : TrusteeIndividual => s"${messages("entities.trustee.individual")}"
      case _ => "Trustee"
    }

    AddRow(name, trusteeType, "#", "#")
  }

  def rows : AddToRows = {
    val trustees = userAnswers.get(Trustees).toList.flatten

    val complete = trustees.map(parseTrustee)

//    val inProgress = trustees.filterNot(_.isComplete).map(parseTrustee)
    val inProgress = Nil

    AddToRows(inProgress, complete)
  }

}
