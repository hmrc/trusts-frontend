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
import mapping.Mapping
import models.core.pages.UKAddress
import models.registration.pages.WhatKindOfAsset
import org.scalatest.{FreeSpec, MustMatchers, OptionValues}
import pages.register.asset.WhatKindOfAssetPage
import pages.register.asset.property_or_land._

class PropertyOrLandMapperSpec extends FreeSpec with MustMatchers
  with OptionValues with Generators with SpecBaseHelpers {

  val propertyOrLandMapper : Mapping[List[PropertyLandType]] = injector.instanceOf[PropertyOrLandMapper]

  "propertyOrLandMapper" - {

    "must not be able to create a property or land asset when missing values in user answers" in {

      val userAnswers = emptyUserAnswers
        .set(WhatKindOfAssetPage(0), WhatKindOfAsset.PropertyOrLand).success.value
        .set(PropertyOrLandAddressYesNoPage(0), true).success.value

      propertyOrLandMapper.build(userAnswers) mustNot be(defined)

    }

    "must be able to create a property or land Asset with address" in {

      val userAnswers = emptyUserAnswers
        .set(WhatKindOfAssetPage(0), WhatKindOfAsset.PropertyOrLand).success.value
        .set(PropertyOrLandAddressYesNoPage(0), true).success.value
        .set(PropertyOrLandAddressUkYesNoPage(0), true).success.value
        .set(PropertyOrLandUKAddressPage(0), UKAddress("26", "Grangetown", Some("Tyne and Wear"), Some("Newcastle"), "Z99 2YY")).success.value
        .set(PropertyOrLandTotalValuePage(0), "1000").success.value
        .set(TrustOwnAllThePropertyOrLandPage(0), false).success.value
        .set(PropertyLandValueTrustPage(0), "750").success.value


      propertyOrLandMapper.build(userAnswers).value mustBe
        List(
          PropertyLandType(
            buildingLandName = None,
            address = Some(
              AddressType(
                line1 = "26",
                line2 = "Grangetown",
                line3 = Some("Tyne and Wear"),
                line4 = Some("Newcastle"),
                postCode = Some("Z99 2YY"),
                country = "GB"
              )),
            valueFull = 1000L,
            valuePrevious = 750L

          )
        )
    }

    "must be able to create a property or land Asset with a property description and owns full value" in {

      val userAnswers = emptyUserAnswers
        .set(WhatKindOfAssetPage(0), WhatKindOfAsset.PropertyOrLand).success.value
        .set(PropertyOrLandAddressYesNoPage(0), false).success.value
        .set(PropertyOrLandDescriptionPage(0), "Property Or Land").success.value
        .set(PropertyOrLandTotalValuePage(0), "1000").success.value
        .set(TrustOwnAllThePropertyOrLandPage(0), true).success.value


      propertyOrLandMapper.build(userAnswers).value mustBe
        List(
          PropertyLandType(
            buildingLandName = Some("Property Or Land"),
            address = None,
            valueFull = 1000L,
            valuePrevious = 1000L

          )
        )
    }

    "must be able to create multiple Share Assets" in {
      val userAnswers = emptyUserAnswers

        .set(WhatKindOfAssetPage(0), WhatKindOfAsset.PropertyOrLand).success.value
        .set(PropertyOrLandAddressYesNoPage(0), true).success.value
        .set(PropertyOrLandAddressUkYesNoPage(0), true).success.value
        .set(PropertyOrLandUKAddressPage(0), UKAddress("26", "Grangetown", Some("Tyne and Wear"), Some("Newcastle"), "Z99 2YY")).success.value
        .set(PropertyOrLandTotalValuePage(0), "1000").success.value
        .set(TrustOwnAllThePropertyOrLandPage(0), true).success.value
        .set(PropertyLandValueTrustPage(0), "750").success.value
        .set(WhatKindOfAssetPage(1), WhatKindOfAsset.PropertyOrLand).success.value
        .set(PropertyOrLandAddressYesNoPage(1), true).success.value
        .set(PropertyOrLandAddressUkYesNoPage(1), true).success.value
        .set(PropertyOrLandUKAddressPage(1), UKAddress("26", "Grangetown", Some("Tyne and Wear"), Some("Newcastle"), "Z99 2YY")).success.value
        .set(PropertyOrLandTotalValuePage(1), "1000").success.value
        .set(TrustOwnAllThePropertyOrLandPage(1), true).success.value
        .set(PropertyLandValueTrustPage(1), "750").success.value

      propertyOrLandMapper.build(userAnswers).value.length mustBe 2
    }
  }

}
