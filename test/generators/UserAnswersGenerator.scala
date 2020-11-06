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

package generators

import models.core.UserAnswers
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Arbitrary, Gen}
import org.scalatest.TryValues
import pages._
import pages.register._
import pages.register.agents._
import pages.register.asset.money.AssetMoneyValuePage
import pages.register.asset.partnership._
import pages.register.asset.property_or_land._
import pages.register.asset.shares._
import pages.register.asset.{AddAnAssetYesNoPage, AddAssetsPage, WhatKindOfAssetPage}
import play.api.libs.json.{JsValue, Json}

trait UserAnswersGenerator extends TryValues {
  self: Generators =>

  import models.core.UserAnswerImplicits._

  val generators: Seq[Gen[(QuestionPage[_], JsValue)]] =
    arbitrary[(PartnershipStartDatePage, JsValue)] ::
    arbitrary[(PartnershipDescriptionPage, JsValue)] ::
    arbitrary[(AddAnAssetYesNoPage.type, JsValue)] ::
    arbitrary[(PropertyOrLandAddressYesNoPage, JsValue)] ::
    arbitrary[(PropertyOrLandAddressUkYesNoPage, JsValue)] ::
    arbitrary[(TrustOwnAllThePropertyOrLandPage, JsValue)] ::
    arbitrary[(PropertyOrLandInternationalAddressPage, JsValue)] ::
    arbitrary[(ShareCompanyNamePage, JsValue)] ::
    arbitrary[(SharesOnStockExchangePage, JsValue)] ::
    arbitrary[(SharesInAPortfolioPage, JsValue)] ::
    arbitrary[(ShareValueInTrustPage, JsValue)] ::
    arbitrary[(ShareQuantityInTrustPage, JsValue)] ::
    arbitrary[(SharePortfolioValueInTrustPage, JsValue)] ::
    arbitrary[(SharePortfolioQuantityInTrustPage, JsValue)] ::
    arbitrary[(SharePortfolioOnStockExchangePage, JsValue)] ::
    arbitrary[(SharePortfolioNamePage, JsValue)] ::
    arbitrary[(ShareClassPage, JsValue)] ::
    arbitrary[(PropertyOrLandUKAddressPage, JsValue)] ::
    arbitrary[(DeclarationPage.type, JsValue)] ::
    arbitrary[(AgentInternationalAddressPage.type, JsValue)] ::
    arbitrary[(AgentUKAddressPage.type, JsValue)] ::
    arbitrary[(AgentAddressYesNoPage.type, JsValue)] ::
    arbitrary[(AgentNamePage.type, JsValue)] ::
    arbitrary[(PropertyOrLandDescriptionPage, JsValue)] ::
    arbitrary[(PropertyOrLandTotalValuePage, JsValue)] ::
    arbitrary[(AddAssetsPage.type, JsValue)] ::
    arbitrary[(AssetMoneyValuePage, JsValue)] ::
    arbitrary[(AgentInternalReferencePage.type, JsValue)] ::
    arbitrary[(WhatKindOfAssetPage, JsValue)] ::
    arbitrary[(AgentTelephoneNumberPage.type, JsValue)] ::
    arbitrary[(PostcodeForTheTrustPage.type, JsValue)] ::
    arbitrary[(WhatIsTheUTRPage.type, JsValue)] ::
    arbitrary[(TrustHaveAUTRPage.type, JsValue)] ::
    arbitrary[(TrustRegisteredOnlinePage.type, JsValue)] ::
    Nil

  implicit lazy val arbitraryUserData: Arbitrary[UserAnswers] = {

    Arbitrary {
      for {
        id      <- nonEmptyString
        data    <- generators match {
          case Nil => Gen.const(Map[QuestionPage[_], JsValue]())
          case _   => Gen.mapOf(oneOf(generators))
        }
        internalId <- nonEmptyString
      } yield UserAnswers (
        draftId = id,
        data = data.foldLeft(Json.obj()) {
          case (obj, (path, value)) =>
            obj.setObject(path.path, value).get
        },
        internalAuthId = internalId
      )
    }
  }
}
