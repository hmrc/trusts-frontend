/*
 * Copyright 2021 HM Revenue & Customs
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

import controllers.register.asset._
import javax.inject.Inject
import models.Mode
import models.core.UserAnswers
import models.registration.pages.Status.Completed
import play.api.Logging
import play.api.i18n.Messages
import sections.Assets
import services.AuditService
import uk.gov.hmrc.http.HeaderCarrier
import utils.CheckAnswersFormatters.currencyFormat
import viewmodels.addAnother._
import viewmodels.{AddRow, AddToRows}

class AddAssetViewHelper @Inject()(auditService: AuditService)
                                  (userAnswers: UserAnswers, mode: Mode, draftId: String)
                                  (implicit messages: Messages, hc: HeaderCarrier) extends Logging {

  def rows: AddToRows = {

    auditService.auditUserAnswers(userAnswers)

    val assets = userAnswers.get(Assets).toList.flatten.zipWithIndex

    val completed: List[AddRow] = assets.filter(_._1.status == Completed).flatMap(parseAsset)

    val inProgress: List[AddRow] = assets.filterNot(_._1.status == Completed).flatMap(parseAsset)

    AddToRows(inProgress, completed)
  }

  private def parseAsset(asset: (AssetViewModel, Int)): Option[AddRow] = {
    val vm = asset._1
    val index = asset._2

    vm match {
      case money: MoneyAssetViewModel =>
        logger.info(s"[parseAssets][Session ID: ${Session.id(hc)}]: ${money.status} Money asset at index $index")
        Some(parseMoney(money, index))
      case share: ShareAssetViewModel =>
        logger.info(s"[parseAssets][Session ID: ${Session.id(hc)}]: ${share.status} Shares asset at index $index")
        Some(parseShare(share, index))
      case propertyOrLand: PropertyOrLandAssetViewModel =>
        logger.info(s"[parseAssets][Session ID: ${Session.id(hc)}]: ${propertyOrLand.status} Property or land asset at index $index")
        Some(parsePropertyOrLand(propertyOrLand, index))
      case business: BusinessAssetViewModel =>
        logger.info(s"[parseAssets][Session ID: ${Session.id(hc)}]: ${business.status} Business asset at index $index")
        Some(parseBusiness(business, index))
      case partnership: PartnershipAssetViewModel =>
        logger.info(s"[parseAssets][Session ID: ${Session.id(hc)}]: ${partnership.status} Partnership asset at index $index")
        Some(parsePartnership(partnership, index))
      case other: OtherAssetViewModel =>
        logger.info(s"[parseAssets][Session ID: ${Session.id(hc)}]: ${other.status} Other asset at index $index")
        Some(parseOther(other, index))
      case _ =>
        logger.warn(s"[parseAssets][Session ID: ${Session.id(hc)}]: Unknown asset at index $index")
        None
    }
  }

  private val defaultValue = messages("entities.no.value.added")
  private val defaultName = messages("entities.no.name.added")
  private val defaultDescription = messages("entities.no.description.added")
  private val defaultAddress = messages("entities.no.address.added")

  private def parseMoney(mvm: MoneyAssetViewModel, index: Int) : AddRow = {
    AddRow(
      mvm.value match {
        case Some(value) => currencyFormat(value)
        case _ => defaultValue
      },
      mvm.`type`.toString,
      money.routes.AssetMoneyValueController.onPageLoad(mode, index, draftId).url,
      routes.RemoveAssetYesNoController.onPageLoad(index, draftId).url
    )
  }

  private def parseShare(svm: ShareAssetViewModel, index : Int) : AddRow = {
    AddRow(
      svm.name.getOrElse(defaultName),
      svm.`type`.toString,
      if (svm.status == Completed) {
        shares.routes.ShareAnswerController.onPageLoad(index, draftId).url
      } else {
        shares.routes.SharesInAPortfolioController.onPageLoad(mode, index, draftId).url
      },
      routes.RemoveAssetYesNoController.onPageLoad(index, draftId).url
    )
  }

  private def parsePropertyOrLand(plvm : PropertyOrLandAssetViewModel, index: Int) : AddRow = {
    AddRow(
      name = plvm match {
        case PropertyOrLandAssetUKAddressViewModel(_, address, _) => address.getOrElse(defaultAddress)
        case PropertyOrLandAssetInternationalAddressViewModel(_, address, _) => address.getOrElse(defaultAddress)
        case PropertyOrLandAssetAddressViewModel(_, address, _) => address.getOrElse(defaultAddress)
        case PropertyOrLandAssetDescriptionViewModel(_, description, _) => description.getOrElse(defaultDescription)
        case _ => messages("entities.propertyOrLand.default")
      },
      messages("addAssets.propertyOrLand"),
      changeUrl = if (plvm.status == Completed) {
        property_or_land.routes.PropertyOrLandAnswerController.onPageLoad(index, draftId).url
      } else {
        property_or_land.routes.PropertyOrLandAddressYesNoController.onPageLoad(mode, index, draftId).url
      },
      removeUrl = routes.RemoveAssetYesNoController.onPageLoad(index, draftId).url
    )

  }

  private def parseOther(ovm: OtherAssetViewModel, index: Int) : AddRow = {
    AddRow(
      name = ovm.description.getOrElse(defaultDescription),
      typeLabel = ovm.`type`.toString,
      changeUrl = if (ovm.status == Completed) {
        other.routes.OtherAssetAnswersController.onPageLoad(index, draftId).url
      } else {
        other.routes.OtherAssetDescriptionController.onPageLoad(mode, index, draftId).url
      },
      removeUrl = routes.RemoveAssetYesNoController.onPageLoad(index, draftId).url
    )
  }

  private def parsePartnership(pvm: PartnershipAssetViewModel, index: Int) : AddRow = {
    AddRow(
      name = pvm.description.getOrElse(defaultDescription),
      typeLabel = pvm.`type`.toString,
      changeUrl = if (pvm.status == Completed) {
        partnership.routes.PartnershipAnswerController.onPageLoad(index, draftId).url
      } else {
        partnership.routes.PartnershipDescriptionController.onPageLoad(mode, index, draftId).url
      },
      removeUrl = routes.RemoveAssetYesNoController.onPageLoad(index, draftId).url
    )
  }

  private def parseBusiness(bvm: BusinessAssetViewModel, index: Int) : AddRow = {
    AddRow(
      name = bvm.name.getOrElse(defaultName),
      typeLabel = bvm.`type`.toString,
      changeUrl = if (bvm.status == Completed) {
        business.routes.BusinessAnswersController.onPageLoad(index, draftId).url
      } else {
        business.routes.BusinessNameController.onPageLoad(mode, index, draftId).url
      },
      removeUrl = routes.RemoveAssetYesNoController.onPageLoad(index, draftId).url
    )
  }

}
