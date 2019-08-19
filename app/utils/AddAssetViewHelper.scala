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
import sections.Assets
import viewmodels.addAnother._
import viewmodels.{AddRow, AddToRows}

class AddAssetViewHelper(userAnswers: UserAnswers, draftId: String)(implicit messages: Messages) {

  def rows: AddToRows = {

    val assets = userAnswers.get(Assets).toList.flatten.zipWithIndex

    val completed: List[AddRow] = assets.filter(_._1.status == Completed).flatMap(parseAsset)

    val inProgress: List[AddRow] = assets.filterNot(_._1.status == Completed).flatMap(parseAsset)

    AddToRows(inProgress, completed)
  }

  private def parseAsset(asset: (AssetViewModel, Int)): Option[AddRow] = {
    val vm = asset._1
    val index = asset._2

    vm match {
      case mvm: MoneyAssetViewModel => Some(parseMoney(mvm, index))
      case mvm: ShareAssetViewModel => Some(parseShare(mvm, index))
      case mvm: PropertyOrLandAssetViewModel => Some(parsePropertyOrLand(mvm, index))
      case _ => None
    }
  }

  private def parseMoney(mvm: MoneyAssetViewModel, index: Int) : AddRow = {
    val defaultValue = messages("entities.no.value.added")
    AddRow(
      mvm.value.getOrElse(defaultValue),
      mvm.`type`.toString,
      "#",
      controllers.money.routes.RemoveMoneyAssetController.onPageLoad(index, draftId).url
    )
  }

  private def parseShare(mvm: ShareAssetViewModel, index : Int) : AddRow = {
    val defaultName = messages("entities.no.name.added")

    val removeRoute = if (mvm.inPortfolio) {
      controllers.shares.routes.RemoveSharePortfolioAssetController.onPageLoad(index, draftId)
    } else {
      controllers.shares.routes.RemoveShareCompanyNameAssetController.onPageLoad(index, draftId)
    }

    AddRow(mvm.name.getOrElse(defaultName), mvm.`type`.toString, "#", removeRoute.url)
  }

  private def parsePropertyOrLand(mvm : PropertyOrLandAssetViewModel, index: Int) : AddRow = {
    val defaultAddressName = messages("entities.no.address.added")
    val defaultDescriptionName = messages("entities.no.description.added")

    val typeLabel : String = messages("addAssets.propertyOrLand")

    if (mvm.hasAddress.contains(true)) {
      AddRow(
        mvm.address.getOrElse(defaultAddressName),
        typeLabel,
        "#",
        controllers.property_or_land.routes.RemovePropertyOrLandWithAddressUKController.onPageLoad(index, draftId).url
      )
    } else {
      AddRow(
        mvm.description.getOrElse(defaultDescriptionName),
        typeLabel,
        "#",
        controllers.property_or_land.routes.RemovePropertyOrLandWithDescriptionController.onPageLoad(index, draftId).url
      )
    }
  }

}
