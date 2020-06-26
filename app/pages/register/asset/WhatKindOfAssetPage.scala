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

package pages.register.asset

import models.core.UserAnswers
import models.registration.pages.WhatKindOfAsset
import models.registration.pages.WhatKindOfAsset.{Business, Money, Other, Partnership, PropertyOrLand, Shares}
import pages.QuestionPage
import pages.entitystatus.AssetStatus
import pages.register.asset.money.AssetMoneyValuePage
import pages.register.asset.other._
import pages.register.asset.partnership.{PartnershipDescriptionPage, PartnershipStartDatePage}
import pages.register.asset.property_or_land._
import pages.register.asset.shares._
import play.api.libs.json.JsPath
import sections.Assets

import scala.util.Try

final case class WhatKindOfAssetPage(index: Int) extends QuestionPage[WhatKindOfAsset] {

  override def path: JsPath = JsPath \ Assets \ index \ toString

  override def toString: String = "whatKindOfAsset"

  override def cleanup(value: Option[WhatKindOfAsset], userAnswers: UserAnswers): Try[UserAnswers] = {
    value match {
      case Some(Money) =>

        removeShare(userAnswers)
          .flatMap(removePropertyOrLand)
          .flatMap(removePartnership)
          .flatMap(removeOther)

      case Some(Shares) =>

        removeMoney(userAnswers)
          .flatMap(removePropertyOrLand)
          .flatMap(removePartnership)
          .flatMap(removeOther)

      case Some(PropertyOrLand) =>

        removeMoney(userAnswers)
          .flatMap(removeShare)
          .flatMap(removePartnership)
          .flatMap(removeOther)

      case Some(Business) =>

        removeMoney(userAnswers)
          .flatMap(removePropertyOrLand)
          .flatMap(removeShare)
          .flatMap(removePartnership)
          .flatMap(removeOther)

      case Some(Partnership) =>

        removeMoney(userAnswers)
          .flatMap(removePropertyOrLand)
          .flatMap(removeShare)
          .flatMap(removeOther)

      case Some(Other) =>

        removeMoney(userAnswers)
          .flatMap(removePropertyOrLand)
          .flatMap(removePartnership)
          .flatMap(removeShare)

      case _ => super.cleanup(value, userAnswers)
    }
  }

  private def removeMoney(userAnswers: UserAnswers) : Try[UserAnswers] = {
    userAnswers.remove(AssetMoneyValuePage(index))
      .flatMap(_.remove(AssetStatus(index)))
  }

  private def removeShare(userAnswers: UserAnswers): Try[UserAnswers] = {
    userAnswers.remove(SharesInAPortfolioPage(index))
      .flatMap(_.remove(ShareCompanyNamePage(index)))
      .flatMap(_.remove(SharesOnStockExchangePage(index)))
      .flatMap(_.remove(ShareClassPage(index)))
      .flatMap(_.remove(ShareQuantityInTrustPage(index)))
      .flatMap(_.remove(ShareValueInTrustPage(index)))
      .flatMap(_.remove(SharePortfolioNamePage(index)))
      .flatMap(_.remove(SharePortfolioOnStockExchangePage(index)))
      .flatMap(_.remove(SharePortfolioQuantityInTrustPage(index)))
      .flatMap(_.remove(SharePortfolioValueInTrustPage(index)))
      .flatMap(_.remove(AssetStatus(index)))
  }

  private def removePropertyOrLand(userAnswers: UserAnswers) : Try[UserAnswers] = {
    userAnswers.remove(PropertyOrLandAddressYesNoPage(index))
      .flatMap(_.remove(PropertyOrLandAddressUkYesNoPage(index)))
      .flatMap(_.remove(PropertyOrLandUKAddressPage(index)))
      .flatMap(_.remove(PropertyOrLandInternationalAddressPage(index)))
      .flatMap(_.remove(PropertyOrLandTotalValuePage(index)))
      .flatMap(_.remove(TrustOwnAllThePropertyOrLandPage(index)))
      .flatMap(_.remove(PropertyOrLandDescriptionPage(index)))
      .flatMap(_.remove(PropertyLandValueTrustPage(index)))
      .flatMap(_.remove(AssetStatus(index)))
  }

  private def removeOther(userAnswers: UserAnswers): Try[UserAnswers] = {
    userAnswers.remove(OtherAssetDescriptionPage(index))
      .flatMap(_.remove(OtherAssetValuePage(index)))
  }

  private def removePartnership(userAnswers: UserAnswers): Try[UserAnswers] = {
    userAnswers.remove(PartnershipDescriptionPage(index))
      .flatMap(_.remove(PartnershipStartDatePage(index)))
  }

}
