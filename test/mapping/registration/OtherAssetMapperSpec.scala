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

package mapping.registration

import base.SpecBaseHelpers
import generators.Generators
import mapping.Mapping
import models.registration.pages.Status.Completed
import models.registration.pages.WhatKindOfAsset
import org.scalatest.{FreeSpec, MustMatchers, OptionValues}
import pages.entitystatus.AssetStatus
import pages.register.asset.WhatKindOfAssetPage
import pages.register.asset.other._

class OtherAssetMapperSpec extends FreeSpec with MustMatchers
  with OptionValues with Generators with SpecBaseHelpers {

  val otherAssetMapper: Mapping[List[OtherAssetType]] = injector.instanceOf[OtherAssetMapper]

  "OtherAssetMapper" - {

    "must not be able to create an other asset when no description or value in user answers" in {

      val userAnswers =
        emptyUserAnswers
          .set(WhatKindOfAssetPage(0), WhatKindOfAsset.Other).success.value

      otherAssetMapper.build(userAnswers) mustNot be(defined)
    }

    "must able to create an Other Asset" in {

      val userAnswers =
        emptyUserAnswers
          .set(WhatKindOfAssetPage(0), WhatKindOfAsset.Other).success.value
          .set(OtherAssetDescriptionPage(0), "Description").success.value
          .set(OtherAssetValuePage(0), "4000").success.value
          .set(AssetStatus(0), Completed).success.value

      otherAssetMapper.build(userAnswers).value mustBe List(OtherAssetType("Description", 4000))
    }

    "must able to create multiple Other Assets" in {

      val userAnswers =
        emptyUserAnswers
          .set(WhatKindOfAssetPage(0), WhatKindOfAsset.Other).success.value
          .set(OtherAssetDescriptionPage(0), "Description 1").success.value
          .set(OtherAssetValuePage(0), "4000").success.value
          .set(AssetStatus(0), Completed).success.value
          .set(WhatKindOfAssetPage(1), WhatKindOfAsset.Other).success.value
          .set(OtherAssetDescriptionPage(1), "Description 2").success.value
          .set(OtherAssetValuePage(1), "6000").success.value
          .set(AssetStatus(1), Completed).success.value

      otherAssetMapper.build(userAnswers).value mustBe List(
        OtherAssetType("Description 1", 4000),
        OtherAssetType("Description 2", 6000)
      )

      otherAssetMapper.build(userAnswers).value.length mustBe 2
    }
  }
}
