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

package mapping

import base.SpecBaseHelpers
import generators.Generators
import models.Status.Completed
import models.WhatKindOfAsset
import org.scalatest.{FreeSpec, MustMatchers, OptionValues}
import pages.entitystatus.AssetStatus
import pages.{AssetMoneyValuePage, WhatKindOfAssetPage}

class AssetMapperSpec extends FreeSpec with MustMatchers
  with OptionValues with Generators with SpecBaseHelpers {

  val assetMapper: Mapping[Assets] = injector.instanceOf[AssetMapper]

  "AssetMapper" - {

    "when user answers is empty" - {

      "must be able to create an empty AssetDetails" in {

        val userAnswers = emptyUserAnswers

        assetMapper.build(userAnswers) mustNot be(defined)
      }
    }

    "when user answers is not empty " - {

      "must not be able to create a money asset when no value is in user answers" in {

        val userAnswers =
          emptyUserAnswers
            .set(WhatKindOfAssetPage(0), WhatKindOfAsset.Money).success.value

        assetMapper.build(userAnswers) mustNot be(defined)
      }

      "must able to create a Monetary Asset" in {

        val userAnswers =
          emptyUserAnswers
            .set(WhatKindOfAssetPage(0), WhatKindOfAsset.Money).success.value
            .set(AssetMoneyValuePage(0), "2000").success.value
            .set(AssetStatus(0), Completed).success.value

        assetMapper.build(userAnswers).value mustBe Assets(
          monetary = Some(
            List(
              AssetMonetaryAmount(2000)
            )
          ),
          propertyOrLand = None,
          shares = None,
          business = None,
          partnerShip = None,
          other = None
        )
      }
    }
  }
}