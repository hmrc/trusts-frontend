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

package mapping.registration

import base.SpecBaseHelpers
import generators.Generators
import mapping.{AssetMonetaryAmount, Mapping}
import models.registration.pages.Status.Completed
import models.registration.pages.WhatKindOfAsset
import org.scalatest.{FreeSpec, MustMatchers, OptionValues}
import pages.entitystatus.AssetStatus
import pages.register.asset.WhatKindOfAssetPage
import pages.register.asset.money.AssetMoneyValuePage

class MoneyAssetMapperSpec extends FreeSpec with MustMatchers
  with OptionValues with Generators with SpecBaseHelpers {

  val moneyAssetMapper : Mapping[List[AssetMonetaryAmount]] = injector.instanceOf[MoneyAssetMapper]

  "MoneyAssetMapper" - {

    "must not be able to create a money asset when no value is in user answers" in {

      val userAnswers =
        emptyUserAnswers
          .set(WhatKindOfAssetPage(0), WhatKindOfAsset.Money).success.value

      moneyAssetMapper.build(userAnswers) mustNot be(defined)
    }

    "must able to create a Monetary Asset" in {

      val userAnswers =
        emptyUserAnswers
          .set(WhatKindOfAssetPage(0), WhatKindOfAsset.Money).success.value
          .set(AssetMoneyValuePage(0), "2000").success.value
          .set(AssetStatus(0), Completed).success.value

      moneyAssetMapper.build(userAnswers).value mustBe List(AssetMonetaryAmount(2000))
    }

    "must able to create multiple Monetary Assets" in {

      val userAnswers =
        emptyUserAnswers
          .set(WhatKindOfAssetPage(0), WhatKindOfAsset.Money).success.value
          .set(AssetMoneyValuePage(0), "2000").success.value
          .set(AssetStatus(0), Completed).success.value
          .set(WhatKindOfAssetPage(1), WhatKindOfAsset.Money).success.value
          .set(AssetMoneyValuePage(1), "2000").success.value
          .set(AssetStatus(1), Completed).success.value

      moneyAssetMapper.build(userAnswers).value.length mustBe 2
    }
  }
}
