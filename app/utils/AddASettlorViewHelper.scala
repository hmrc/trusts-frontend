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

package utils

import controllers.register.settlors.living_settlor.routes
import models.core.UserAnswers
import models.registration.pages.Status._
import play.api.i18n.Messages
import sections.{DeceasedSettlor, LivingSettlors}
import viewmodels.addAnother.{SettlorViewModel, _}
import viewmodels.{AddRow, AddToRows}

class AddASettlorViewHelper(userAnswers: UserAnswers, draftId: String)(implicit messages: Messages) {

  def rows: AddToRows =
    deceasedSettlors match {
      case Some(s: SettlorViewModel) if s.status equals Completed =>
        AddToRows(
          inProgress = livingSettlors._2.flatMap(parseSettlor),
          complete = livingSettlors._1.flatMap(parseSettlor) :+ parseToRows(s, 0)
        )
      case Some(s: SettlorViewModel) if s.status equals InProgress =>
        AddToRows(
          inProgress = livingSettlors._2.flatMap(parseSettlor) :+ parseToRows(s, 0),
          complete = livingSettlors._1.flatMap(parseSettlor)
        )
      case _ =>
        AddToRows(
          inProgress = livingSettlors._2.flatMap(parseSettlor),
          complete = livingSettlors._1.flatMap(parseSettlor)
        )
    }

  val livingSettlors =
    userAnswers.get(LivingSettlors)
      .toList
      .flatten
      .zipWithIndex
      .partition(_._1.status == Completed)

  val deceasedSettlors =
    userAnswers.get(DeceasedSettlor)

  private def parseSettlor(settlor: (SettlorViewModel, Int)): Option[AddRow] = {
    val vm = settlor._1
    val index = settlor._2

    Some(parseToRows(vm, index))
  }

  private def parseToRows(mvm: SettlorViewModel, index: Int): AddRow = {
    val defaultName = messages("entities.no.name.added")

    mvm match {
      case SettlorLivingIndividualViewModel(_, name, _) => AddRow(
        name,
        messages("entity.settlor.individual"),
        controllers.routes.FeatureNotAvailableController.onPageLoad().url,
        removeUrl = routes.RemoveSettlorController.onPageLoad(index, draftId).url
      )
      case SettlorDeceasedIndividualViewModel(_, name, _) => AddRow(
        name,
        messages("entity.settlor.deceased"),
        controllers.routes.FeatureNotAvailableController.onPageLoad().url,
        removeUrl = controllers.register.settlors.deceased_settlor.routes.RemoveSettlorController.onPageLoad(draftId).url
      )
      case DefaultSettlorViewModel(_, _) => AddRow(
        defaultName,
        messages("entity.settlor.individual"),
        controllers.routes.FeatureNotAvailableController.onPageLoad().url,
        removeUrl = routes.RemoveSettlorController.onPageLoad(index, draftId).url
      )
    }

  }


}