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

package navigation.routes

import controllers.register.routes
import models.NormalMode
import models.core.UserAnswers
import models.registration.pages.AddAssets
import models.registration.pages.WhatKindOfAsset.{Business, Money, Other, Partnership, PropertyOrLand, Shares}
import pages.Page
import pages.register.asset.business.{BusinessAddressUkYesNoPage, BusinessDescriptionPage, BusinessInternationalAddressPage, BusinessNamePage, BusinessUkAddressPage, BusinessValuePage}
import pages.register.asset.money.AssetMoneyValuePage
import pages.register.asset.other.{OtherAssetDescriptionPage, OtherAssetValuePage}
import pages.register.asset.shares._
import pages.register.asset.{AddAnAssetYesNoPage, AddAssetsPage, WhatKindOfAssetPage}
import play.api.mvc.Call
import uk.gov.hmrc.auth.core.AffinityGroup

object AssetsRoutes {
  def route(draftId: String): PartialFunction[Page, AffinityGroup => UserAnswers => Call] = {
    case AssetMoneyValuePage(index) => _ => ua => assetMoneyValueRoute(ua, index, draftId)
    case WhatKindOfAssetPage(index) => _ => ua => whatKindOfAssetRoute(ua, index, draftId)
    case SharesInAPortfolioPage(index) => _ => ua => sharesInAPortfolio(ua, index, draftId)
    case SharePortfolioNamePage(index) => _ => ua => controllers.register.asset.shares.routes.SharePortfolioOnStockExchangeController.onPageLoad(NormalMode, index, draftId)
    case SharePortfolioOnStockExchangePage(index) => _ => ua => controllers.register.asset.shares.routes.SharePortfolioQuantityInTrustController.onPageLoad(NormalMode, index, draftId)
    case SharePortfolioQuantityInTrustPage(index) => _ => _ => controllers.register.asset.shares.routes.SharePortfolioValueInTrustController.onPageLoad(NormalMode, index, draftId)
    case SharePortfolioValueInTrustPage(index) => _ => _ => controllers.register.asset.shares.routes.ShareAnswerController.onPageLoad(index, draftId)
    case SharesOnStockExchangePage(index) => _ => _ => controllers.register.asset.shares.routes.ShareClassController.onPageLoad(NormalMode, index, draftId)
    case ShareClassPage(index) => _ => _ => controllers.register.asset.shares.routes.ShareQuantityInTrustController.onPageLoad(NormalMode, index, draftId)
    case AddAssetsPage => _ => addAssetsRoute(draftId)
    case AddAnAssetYesNoPage => _ => addAnAssetYesNoRoute(draftId)
    case ShareQuantityInTrustPage(index) => _ => _ => controllers.register.asset.shares.routes.ShareValueInTrustController.onPageLoad(NormalMode, index, draftId)
    case ShareValueInTrustPage(index) => _ => _ => controllers.register.asset.shares.routes.ShareAnswerController.onPageLoad(index, draftId)
    case ShareAnswerPage => _ => _ => controllers.register.asset.routes.AddAssetsController.onPageLoad(draftId)
    case ShareCompanyNamePage(index) => _ => _ => controllers.register.asset.shares.routes.SharesOnStockExchangeController.onPageLoad(NormalMode, index, draftId)
    case OtherAssetDescriptionPage(index) => _ => _ => controllers.register.asset.other.routes.OtherAssetValueController.onPageLoad(NormalMode, index, draftId)
    case OtherAssetValuePage(index) => _ => _ => controllers.register.asset.other.routes.OtherAssetAnswersController.onPageLoad(index, draftId)
    case BusinessNamePage(index) => _ => _ => controllers.register.asset.business.routes.BusinessDescriptionController.onPageLoad(NormalMode, index, draftId)
    case BusinessDescriptionPage(index) => _ => _ => controllers.register.asset.business.routes.BusinessAddressUkYesNoController.onPageLoad(NormalMode, index, draftId)
    case BusinessAddressUkYesNoPage(index) => _ => ua => AssetAddressUkYesNoRoute(ua, index, draftId)
    case BusinessUkAddressPage(index) => _ => _ => controllers.register.asset.business.routes.BusinessValueController.onPageLoad(NormalMode, index, draftId)
    case BusinessInternationalAddressPage(index) => _ => _ => controllers.register.asset.business.routes.BusinessValueController.onPageLoad(NormalMode, index, draftId)
    case BusinessValuePage(index) => _ => _ => controllers.register.asset.business.routes.BusinessAnswersController.onPageLoad(index, draftId)
  }

  private def AssetAddressUkYesNoRoute(userAnswers: UserAnswers, index : Int, draftId: String) : Call = {
    userAnswers.get(BusinessAddressUkYesNoPage(index)) match {
      case Some(true) =>
        controllers.register.asset.business.routes.BusinessUkAddressController.onPageLoad(NormalMode, index, draftId)
      case Some(false) =>
        controllers.register.asset.business.routes.BusinessInternationalAddressController.onPageLoad(NormalMode, index, draftId)
      case _=>
        routes.SessionExpiredController.onPageLoad()
    }
  }

  private def sharesInAPortfolio(userAnswers: UserAnswers, index : Int, draftId: String) : Call = {
    userAnswers.get(SharesInAPortfolioPage(index)) match {
      case Some(true) =>
        controllers.register.asset.shares.routes.SharePortfolioNameController.onPageLoad(NormalMode, index, draftId)
      case Some(false) =>
        controllers.register.asset.shares.routes.ShareCompanyNameController.onPageLoad(NormalMode, index, draftId)
      case _=>
        routes.SessionExpiredController.onPageLoad()
    }
  }

  private def addAnAssetYesNoRoute(draftId: String)(userAnswers: UserAnswers) : Call = userAnswers.get(AddAnAssetYesNoPage) match {
    case Some(false) => routes.TaskListController.onPageLoad(draftId)
    case Some(true) => controllers.register.asset.routes.WhatKindOfAssetController.onPageLoad(NormalMode, 0, draftId)
    case _ => routes.SessionExpiredController.onPageLoad()
  }

  private def addAssetsRoute(draftId: String)(answers: UserAnswers) = {
    val addAnother = answers.get(AddAssetsPage)

    def routeToAssetIndex = {
      val assets = answers.get(sections.Assets).getOrElse(List.empty)
      assets match {
        case Nil =>
          controllers.register.asset.routes.WhatKindOfAssetController.onPageLoad(NormalMode, 0, draftId)
        case t if t.nonEmpty =>
          controllers.register.asset.routes.WhatKindOfAssetController.onPageLoad(NormalMode, t.size, draftId)
      }
    }

    addAnother match {
      case Some(AddAssets.YesNow) =>
        routeToAssetIndex
      case Some(AddAssets.YesLater) =>
        routes.TaskListController.onPageLoad(draftId)
      case Some(AddAssets.NoComplete) =>
        routes.TaskListController.onPageLoad(draftId)
      case _ => routes.SessionExpiredController.onPageLoad()
    }
  }

  private def assetMoneyValueRoute(answers: UserAnswers, index: Int, draftId: String) = {
    val assets = answers.get(sections.Assets).getOrElse(List.empty)
    assets match  {
      case Nil => controllers.register.asset.routes.WhatKindOfAssetController.onPageLoad(NormalMode, 0, draftId)
      case _ => controllers.register.asset.routes.AddAssetsController.onPageLoad(draftId)
    }
  }

  private def whatKindOfAssetRoute(answers: UserAnswers, index: Int, draftId: String) =
    answers.get(WhatKindOfAssetPage(index)) match {
      case Some(Money) =>
        controllers.register.asset.money.routes.AssetMoneyValueController.onPageLoad(NormalMode, index, draftId)
      case Some(Shares) =>
        controllers.register.asset.shares.routes.SharesInAPortfolioController.onPageLoad(NormalMode, index, draftId)
      case Some(PropertyOrLand) =>
        controllers.register.asset.property_or_land.routes.PropertyOrLandAddressYesNoController.onPageLoad(NormalMode, index, draftId)
      case Some(Business) =>
        controllers.register.asset.business.routes.BusinessNameController.onPageLoad(NormalMode, index, draftId)
      case Some(Partnership) =>
        controllers.register.asset.partnership.routes.PartnershipDescriptionController.onPageLoad(NormalMode, index, draftId)
      case Some(Other) =>
        controllers.register.asset.other.routes.OtherAssetDescriptionController.onPageLoad(NormalMode, index, draftId)
      case _ =>
        controllers.routes.FeatureNotAvailableController.onPageLoad()
    }
}

