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

package mapping.playback.assets

import base.SpecBaseHelpers
import generators.Generators
import mapping.playback.PlaybackExtractor
import mapping.registration.{AddressType, PropertyLandType}
import models.core.pages.{InternationalAddress, UKAddress}
import models.playback.UserAnswers
import org.scalatest.{EitherValues, FreeSpec, MustMatchers}
import pages.register.asset.property_or_land._

class PropertyOrLandExtractorSpec extends FreeSpec with MustMatchers with EitherValues with Generators with SpecBaseHelpers {

  private val assets = List(
    PropertyLandType(
      buildingLandName = Some("Property 1"),
      address = Some(AddressType(
        "line1", "line2", None, None, Some("NE11NE"), "UK"
      )),
      valueFull = Some(95L),
      valuePrevious = Some(90L)
    ),
    PropertyLandType(
      buildingLandName = Some("Property 2"),
      address = Some(AddressType(
        "line1", "line2", None, None, None, "FR"
      )),
      valueFull = Some(85L),
      valuePrevious = Some(80L)
    ),
    PropertyLandType(
      buildingLandName = Some("Property 3"),
      address = None,
      valueFull = Some(75L),
      None
    )
  )

  val propertyLandExtractor: PlaybackExtractor[Option[List[PropertyLandType]]] =
    injector.instanceOf[PropertyLandExtractor]

  "PropertyLand Extractor" - {

    "when no others" - {

      "must return user answers" in {

        val ua = UserAnswers("fakeId")

        val extraction = propertyLandExtractor.extract(ua, None)

        extraction mustBe 'left

      }

    }

    "when data exists" in {

      val ua = UserAnswers("fakeId")

      val extraction = propertyLandExtractor.extract(ua, Some(assets))

      extraction.right.value.get(PropertyOrLandAddressYesNoPage(0)).get mustBe true
      extraction.right.value.get(PropertyOrLandAddressUkYesNoPage(0)).get mustBe true
      extraction.right.value.get(PropertyOrLandUKAddressPage(0)).get mustBe UKAddress("line1", "line2", None, None, "NE11NE")
      extraction.right.value.get(PropertyOrLandInternationalAddressPage(0)) must not be defined
      extraction.right.value.get(PropertyOrLandDescriptionPage(0)).get mustBe "Property 1"
      extraction.right.value.get(PropertyOrLandTotalValuePage(0)).get mustBe "95"
      extraction.right.value.get(TrustOwnAllThePropertyOrLandPage(0)).get mustBe false
      extraction.right.value.get(PropertyLandValueTrustPage(0)).get mustBe "90"

      extraction.right.value.get(PropertyOrLandAddressYesNoPage(1)).get mustBe true
      extraction.right.value.get(PropertyOrLandAddressUkYesNoPage(1)).get mustBe false
      extraction.right.value.get(PropertyOrLandInternationalAddressPage(1)).get mustBe InternationalAddress("line1", "line2", None, "FR")
      extraction.right.value.get(PropertyOrLandUKAddressPage(1)) must not be defined
      extraction.right.value.get(PropertyOrLandDescriptionPage(1)).get mustBe "Property 2"
      extraction.right.value.get(PropertyOrLandTotalValuePage(1)).get mustBe "85"
      extraction.right.value.get(TrustOwnAllThePropertyOrLandPage(1)).get mustBe false
      extraction.right.value.get(PropertyLandValueTrustPage(1)).get mustBe "80"

      extraction.right.value.get(PropertyOrLandAddressYesNoPage(2)).get mustBe false
      extraction.right.value.get(PropertyOrLandAddressUkYesNoPage(2)) must not be defined
      extraction.right.value.get(PropertyOrLandUKAddressPage(2)) must not be defined
      extraction.right.value.get(PropertyOrLandInternationalAddressPage(2)) must not be defined
      extraction.right.value.get(PropertyOrLandDescriptionPage(2)).get mustBe "Property 3"
      extraction.right.value.get(PropertyOrLandTotalValuePage(2)).get mustBe "75"
      extraction.right.value.get(TrustOwnAllThePropertyOrLandPage(2)).get mustBe true
      extraction.right.value.get(PropertyLandValueTrustPage(2)) mustBe None

    }

  }

}