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

package navigation.navigators


import base.SpecBase
import controllers.routes
import generators.Generators
import models.WhatKindOfAsset.Money
import models.{NormalMode, UserAnswers, AddAssets}
import navigation.Navigator
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatest.prop.PropertyChecks
import pages._

trait AssetRoutes {

  self: PropertyChecks with Generators with SpecBase =>

  def assetRoutes()(implicit navigator: Navigator) = {


    "go to WhatKindOfAssetPage from AssetMoneyValue page when the amount submitted" in {

      val index = 0

      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          val assets = userAnswers.get(Assets).getOrElse(List.empty)

          navigator.nextPage(AssetMoneyValuePage(index), NormalMode)(userAnswers)
            .mustBe(routes.WhatKindOfAssetController.onPageLoad(NormalMode, assets.size))

      }
    }

    "go to AssetMoneyValuePage from WhatKindOfAsset page when the money option is selected" in {
      val index = 0

      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          val answers = userAnswers.set(WhatKindOfAssetPage(index), Money).success.value

          navigator.nextPage(WhatKindOfAssetPage(index), NormalMode)(answers)
            .mustBe(routes.AssetMoneyValueController.onPageLoad(NormalMode, index))
      }
    }


   "there is atleast one assets" must {

      "go to the WhatKindOfAssetPage from AddAssetsPage when selected add them now" in {

        val answers = UserAnswers(userAnswersId)
          .set(WhatKindOfAssetPage(0), Money).success.value
          .set(AddAssetsPage, AddAssets.YesNow).success.value

        navigator.nextPage(AddAssetsPage, NormalMode)(answers)
          .mustBe(routes.WhatKindOfAssetController.onPageLoad(NormalMode, 1))
      }
    }

    "go to RegistrationProgress from AddAssetsPage when selecting add them later" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          val answers = UserAnswers(userAnswersId)
            .set(WhatKindOfAssetPage(0), Money).success.value
            .set(AddAssetsPage, AddAssets.YesLater).success.value

          navigator.nextPage(AddAssetsPage, NormalMode)(answers)
            .mustBe(routes.TaskListController.onPageLoad())
      }
    }
  }
}