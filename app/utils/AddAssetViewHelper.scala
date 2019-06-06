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
import viewmodels.addAnother.{AssetViewModel, MoneyAssetViewModel}
import viewmodels.{AddRow, AddToRows}

class AddAssetViewHelper(userAnswers: UserAnswers)(implicit  messages: Messages) {

  private def parseAsset(asset: AssetViewModel) : Option[AddRow] = asset match {
    case mvm : MoneyAssetViewModel =>
      Some(AddRow(mvm.value.getOrElse(""), mvm.`type`.toString, "#", "#"))
    case _ =>
      None
  }

  def rows : AddToRows = {
    val assets = userAnswers.get(viewmodels.Assets).toList.flatten

    val completed : List[AddRow] = assets.filter(_.status == Completed).flatMap(parseAsset)

    val inProgress : List[AddRow] = assets.filterNot(_.status == Completed).flatMap(parseAsset)

    AddToRows(inProgress, completed)
  }

}
