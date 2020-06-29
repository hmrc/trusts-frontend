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
import models.core.pages.UKAddress
import models.registration.pages.Status.Completed
import models.registration.pages.WhatKindOfAsset
import org.scalatest.{FreeSpec, MustMatchers, OptionValues}
import pages.entitystatus.AssetStatus
import pages.register.asset.WhatKindOfAssetPage
import pages.register.asset.business._
import pages.register.asset.other._

class BusinessAssetMapperSpec extends FreeSpec with MustMatchers
  with OptionValues with Generators with SpecBaseHelpers {

  val businessAssetMapper: Mapping[List[BusinessAssetType]] = injector.instanceOf[BusinessAssetType]

  "BusinessAssetMapper" - {

    "must not be able to create an business asset when no description or value in user answers" in {

      val userAnswers =
        emptyUserAnswers
          .set(WhatKindOfAssetPage(0), WhatKindOfAsset.Business).success.value

      businessAssetMapper.build(userAnswers) mustNot be(defined)
    }

    "must able to create an Business Asset" in {

      val address = UKAddress("26", "Grangetown", Some("Tyne and Wear"), Some("Newcastle"), "Z99 2YY")

      val userAnswers =
        emptyUserAnswers
          .set(WhatKindOfAssetPage(0), WhatKindOfAsset.Business).success.value
          .set(AssetNamePage(0), "Test").success.value
          .set(AssetDescriptionPage(0), "Description").success.value
          .set(AssetAddressUkYesNoPage(0), true).success.value
          .set(AssetUkAddressPage(0), UKAddress("26", "Grangetown", Some("Tyne and Wear"), Some("Newcastle"), "Z99 2YY")).success.value
          .set(CurrentValuePage(0), "123").success.value
          .set(AssetStatus(0), Completed).success.value

      businessAssetMapper.build(userAnswers).value mustBe List(BusinessAssetType(
        "Test", "Description",
          AddressType("26","Grangetown", Some("Tyne and Wear"), Some("Newcastle"), Some("Z99 2YY"), "GB"), 123L))
    }

    "must able to create multiple Business Assets" in {

      val userAnswers =
        emptyUserAnswers
          .set(WhatKindOfAssetPage(0), WhatKindOfAsset.Business).success.value
          .set(AssetNamePage(0), "Test").success.value
          .set(AssetDescriptionPage(0), "Description").success.value
          .set(AssetAddressUkYesNoPage(0), true).success.value
          .set(AssetUkAddressPage(0), UKAddress("26", "Grangetown", Some("Tyne and Wear"), Some("Newcastle"), "Z99 2YY")).success.value
          .set(CurrentValuePage(0), "123").success.value
          .set(AssetStatus(0), Completed).success.value
          .set(AssetNamePage(1), "Test").success.value
          .set(AssetDescriptionPage(1), "Description").success.value
          .set(AssetAddressUkYesNoPage(1), true).success.value
          .set(AssetUkAddressPage(1), UKAddress("26", "Grangetown", Some("Tyne and Wear"), Some("Newcastle"), "Z99 2YY")).success.value
          .set(CurrentValuePage(1), "123").success.value
          .set(AssetStatus(1), Completed).success.value

      businessAssetMapper.build(userAnswers).value mustBe List(
        BusinessAssetType(
          "Test", "Description",
          AddressType("26","Grangetown", Some("Tyne and Wear"), Some("Newcastle"), Some("Z99 2YY"), "GB"), 123L),
      BusinessAssetType(
        "Test", "Description",
        AddressType("26","Grangetown", Some("Tyne and Wear"), Some("Newcastle"), Some("Z99 2YY"), "GB"), 123L)
      )

      businessAssetMapper.build(userAnswers).value.length mustBe 2
    }
  }
}
