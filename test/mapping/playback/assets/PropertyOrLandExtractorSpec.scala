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
import models.playback.UserAnswers
import org.scalatest.{EitherValues, FreeSpec, MustMatchers}

class PropertyOrLandExtractorSpec extends FreeSpec with MustMatchers with EitherValues with Generators with SpecBaseHelpers {

  private val asset = PropertyLandType(
    buildingLandName = Some("Property Name"),
    address = Some(AddressType(
      "line1", "line2", None, None, None, "FR"
    )),
    valueFull = 95,
    valuePrevious = 90
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

  }

}

