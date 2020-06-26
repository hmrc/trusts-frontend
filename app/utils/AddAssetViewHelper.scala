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

import models.Mode
import models.core.UserAnswers
import models.registration.pages.Status.Completed
import play.api.i18n.Messages
import sections.Assets
import viewmodels.addAnother._
import viewmodels.{AddRow, AddToRows}

class AddAssetViewHelper(userAnswers: UserAnswers, mode: Mode, draftId: String)(implicit messages: Messages) {

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
      case money: MoneyAssetViewModel => Some(parseMoney(money, index))
      case share: ShareAssetViewModel => Some(parseShare(share, index))
      case propertyOrLand: PropertyOrLandAssetViewModel => Some(parsePropertyOrLand(propertyOrLand, index))
      case partnership: PartnershipAssetViewModel => Some(parsePartnership(partnership, index))
      case other: OtherAssetViewModel => Some(parseOther(other, index))
      case _ => None
    }
  }

  private def parseMoney(mvm: MoneyAssetViewModel, index: Int) : AddRow = {
    AddRow(
      mvm.value,
      mvm.`type`.toString,
      controllers.routes.FeatureNotAvailableController.onPageLoad().url,
      controllers.register.asset.money.routes.RemoveMoneyAssetController.onPageLoad(index, draftId).url
    )
  }

  private def parseShare(mvm: ShareAssetViewModel, index : Int) : AddRow = {
    val defaultName = messages("entities.no.name.added")

    val removeRoute = if (mvm.inPortfolio) {
      controllers.register.asset.shares.routes.RemoveSharePortfolioAssetController.onPageLoad(index, draftId)
    } else {
      controllers.register.asset.shares.routes.RemoveShareCompanyNameAssetController.onPageLoad(index, draftId)
    }

    AddRow(mvm.name.getOrElse(defaultName), mvm.`type`.toString, "/trusts-registration/feature-not-available", removeRoute.url)
  }

  private def parsePropertyOrLand(mvm : PropertyOrLandAssetViewModel, index: Int) : AddRow = {
    val defaultAddressName = messages("entities.no.address.added")
    val defaultDescriptionName = messages("entities.no.description.added")

    val typeLabel : String = messages("addAssets.propertyOrLand")

    mvm match {
      case PropertyOrLandAssetUKAddressViewModel(_, address, _) => AddRow(
        address.getOrElse(defaultAddressName),
        typeLabel,
        controllers.routes.FeatureNotAvailableController.onPageLoad().url,
        controllers.register.asset.property_or_land.routes.RemovePropertyOrLandWithAddressUKController.onPageLoad(index, draftId).url
      )
      case PropertyOrLandAssetInternationalAddressViewModel(_, address, _) => AddRow(
        address.getOrElse(defaultAddressName),
        typeLabel,
        controllers.routes.FeatureNotAvailableController.onPageLoad().url,
        controllers.register.asset.property_or_land.routes.RemovePropertyOrLandWithAddressInternationalController.onPageLoad(index, draftId).url
      )
      case PropertyOrLandAssetAddressViewModel(_, address, _) => AddRow(
        address.getOrElse(defaultAddressName),
        typeLabel,
        controllers.routes.FeatureNotAvailableController.onPageLoad().url,
        controllers.routes.FeatureNotAvailableController.onPageLoad().url
      )
      case PropertyOrLandAssetDescriptionViewModel(_, description, _) => AddRow(
        description.getOrElse(defaultDescriptionName),
        typeLabel,
        controllers.routes.FeatureNotAvailableController.onPageLoad().url,
        controllers.register.asset.property_or_land.routes.RemovePropertyOrLandWithDescriptionController.onPageLoad(index, draftId).url
      )
      case PropertyOrLandDefaultViewModel(_, _) =>
        AddRow(
          messages("entities.propertyOrLand.default"),
          typeLabel,
          controllers.routes.FeatureNotAvailableController.onPageLoad().url,
          controllers.register.asset.routes.DefaultRemoveAssetController.onPageLoad(index, draftId).url
        )
    }

  }

  private def parseOther(other: OtherAssetViewModel, index: Int) : AddRow = {
    AddRow(
      other.description,
      other.`type`.toString,
      controllers.register.asset.other.routes.OtherAssetDescriptionController.onPageLoad(mode, index, draftId).url,
      controllers.routes.FeatureNotAvailableController.onPageLoad().url
    )
  }

  private def parsePartnership(partnership: PartnershipAssetViewModel, index: Int) : AddRow = {
    AddRow(
      partnership.description,
      partnership.`type`.toString,
      controllers.register.asset.partnership.routes.PartnershipDescriptionController.onPageLoad(mode, index, draftId).url,
      controllers.routes.FeatureNotAvailableController.onPageLoad().url
    )
  }

}
