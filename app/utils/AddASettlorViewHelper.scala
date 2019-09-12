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

import models.Status.Completed
import models.UserAnswers
import play.api.i18n.Messages
import sections.Settlors
import viewmodels.addAnother._
import viewmodels.{AddRow, AddToRows}

class AddASettlorViewHelper(userAnswers: UserAnswers, draftId: String)(implicit messages: Messages) {

  def rows: AddToRows = {

    val settlors = userAnswers.get(Settlors).toList.flatten.zipWithIndex

    println("**********************************************")
    println(userAnswers.get(Settlors))
    println(settlors)

    val completed: List[AddRow] = settlors.filter(_._1.status == Completed).flatMap(parseSettlor)

    val inProgress: List[AddRow] = settlors.filterNot(_._1.status == Completed).flatMap(parseSettlor)

    AddToRows(inProgress, completed)
  }

  private def parseSettlor(settlor: (SettlorViewModel, Int)): Option[AddRow] = {
    val vm = settlor._1
    val index = settlor._2

    vm match {
      case mvm: SettlorLivingViewModel => Some(parseSettlorLiving(mvm, index))
      case _ => None
    }
  }

  private def parseSettlorLiving(mvm : SettlorLivingViewModel, index: Int) : AddRow = {
    val defaultName = messages("entities.no.name.added")

    val typeLabel : String = messages("addSettlor.individual")

    mvm match {
      case SettlorLivingIndividualViewModel(_, name, _) => AddRow(
        name.getOrElse(defaultName),
        typeLabel,
        "#",
        "#"
      )
      case SettlorLivingBusinessViewModel(_, name, _) => AddRow(
        name.getOrElse(defaultName),
        typeLabel,
        "#",
        "#"
      )
      case SettlorLivingNoNameViewModel(_, name, _) => AddRow(
        name.getOrElse(defaultName),
        typeLabel,
        "#",
        "#"
      )
      case SettlorLivingDefaultViewModel(_, _) => AddRow(
        messages("entities.settlorLiving.default"),
        typeLabel,
        "#",
        "#"
      )
    }

  }

}