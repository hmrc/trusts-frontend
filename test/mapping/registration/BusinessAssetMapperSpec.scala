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
import models.core.http.{AddressType, BusinessAssetType}
import models.core.pages.{InternationalAddress, UKAddress}
import models.registration.pages.Status.Completed
import models.registration.pages.WhatKindOfAsset
import org.scalatest.{FreeSpec, MustMatchers, OptionValues}
import pages.entitystatus.AssetStatus
import pages.register.asset.WhatKindOfAssetPage
import pages.register.asset.business._

class BusinessAssetMapperSpec extends FreeSpec with MustMatchers
  with OptionValues with Generators with SpecBaseHelpers {

  val businessAssetMapper: Mapping[List[BusinessAssetType]] = injector.instanceOf[BusinessAssetMapper]

  "BusinessAssetMapper" - {

    "must not be able to create a business asset when no description or value in user answers" in {

      val userAnswers =
        emptyUserAnswers
          .set(WhatKindOfAssetPage(0), WhatKindOfAsset.Business).success.value

      businessAssetMapper.build(userAnswers) mustNot be(defined)
    }

    "must able to create a Business Asset" - {

      "when UK address" in {

        val userAnswers =
          emptyUserAnswers
            .set(WhatKindOfAssetPage(0), WhatKindOfAsset.Business).success.value
            .set(BusinessNamePage(0), "Test").success.value
            .set(BusinessDescriptionPage(0), "Description").success.value
            .set(BusinessAddressUkYesNoPage(0), true).success.value
            .set(BusinessUkAddressPage(0), UKAddress("26", "Grangetown", Some("Tyne and Wear"), Some("Newcastle"), "Z99 2YY")).success.value
            .set(BusinessValuePage(0), "123").success.value
            .set(AssetStatus(0), Completed).success.value

        businessAssetMapper.build(userAnswers).value mustBe List(BusinessAssetType(
          "Test",
          "Description",
          AddressType("26","Grangetown", Some("Tyne and Wear"), Some("Newcastle"), Some("Z99 2YY"), "GB"),
          123L
        ))
      }

      "when international address" in {

        val userAnswers =
          emptyUserAnswers
            .set(WhatKindOfAssetPage(0), WhatKindOfAsset.Business).success.value
            .set(BusinessNamePage(0), "Test").success.value
            .set(BusinessDescriptionPage(0), "Description").success.value
            .set(BusinessAddressUkYesNoPage(0), false).success.value
            .set(BusinessInternationalAddressPage(0), InternationalAddress("1", "Broadway", Some("New York"), "US")).success.value
            .set(BusinessValuePage(0), "123").success.value
            .set(AssetStatus(0), Completed).success.value

        businessAssetMapper.build(userAnswers).value mustBe List(BusinessAssetType(
          "Test",
          "Description",
          AddressType("1","Broadway", Some("New York"), None, None, "US"),
          123L
        ))
      }
    }

    "must able to create multiple Business Assets" in {

      val userAnswers =
        emptyUserAnswers
          .set(WhatKindOfAssetPage(0), WhatKindOfAsset.Business).success.value
          .set(BusinessNamePage(0), "Test 1").success.value
          .set(BusinessDescriptionPage(0), "Description 1").success.value
          .set(BusinessAddressUkYesNoPage(0), true).success.value
          .set(BusinessUkAddressPage(0), UKAddress("26", "Grangetown", Some("Tyne and Wear"), Some("Newcastle"), "Z99 2YY")).success.value
          .set(BusinessValuePage(0), "123").success.value
          .set(AssetStatus(0), Completed).success.value
          .set(WhatKindOfAssetPage(1), WhatKindOfAsset.Business).success.value
          .set(BusinessNamePage(1), "Test 2").success.value
          .set(BusinessDescriptionPage(1), "Description 2").success.value
          .set(BusinessAddressUkYesNoPage(1), true).success.value
          .set(BusinessUkAddressPage(1), UKAddress("26", "Grangetown", Some("Tyne and Wear"), Some("Newcastle"), "Z99 2YY")).success.value
          .set(BusinessValuePage(1), "123").success.value
          .set(AssetStatus(1), Completed).success.value

      businessAssetMapper.build(userAnswers).value mustBe List(
        BusinessAssetType(
          "Test 1",
          "Description 1",
          AddressType("26","Grangetown", Some("Tyne and Wear"), Some("Newcastle"), Some("Z99 2YY"), "GB"),
          123L
        ),
        BusinessAssetType(
          "Test 2",
          "Description 2",
          AddressType("26","Grangetown", Some("Tyne and Wear"), Some("Newcastle"), Some("Z99 2YY"), "GB"),
          123L
        )
      )

      businessAssetMapper.build(userAnswers).value.length mustBe 2
    }
  }
}
