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

import models.Status.{Completed, InProgress}
import models.{IndividualOrBusiness, UserAnswers}
import play.api.i18n.Messages
import sections.Trustees
import viewmodels._
import viewmodels.addAnother.TrusteeViewModel

class AddATrusteeViewHelper(userAnswers: UserAnswers)(implicit messages: Messages) {

  private def render(trustee : TrusteeViewModel) : AddRow = {

    val name = trustee.name.map(_.toString).getOrElse(messages("entities.no.name.added"))

    def renderForLead(message : String) = s"${messages("entities.lead")} $message"

    val trusteeType = trustee.`type` match {
      case Some(k : IndividualOrBusiness) =>
        val key = messages(s"entities.trustee.$k")

        if(trustee.isLead) renderForLead(key) else key
      case None =>
        s"${messages("entities.trustee")}"
    }

    AddRow(name, trusteeType, "#", "#")
  }

  def rows : AddToRows = {
    val trustees = userAnswers.get(Trustees).toList.flatten

    val complete = trustees.filter(_.status == Completed).map(render)

    val inProgress = trustees.filter(_.status == InProgress).map(render)

    AddToRows(inProgress, complete)
  }

}
