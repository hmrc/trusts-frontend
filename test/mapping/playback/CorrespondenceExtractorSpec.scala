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

package mapping.playback

import base.SpecBaseHelpers
import generators.Generators
import mapping.playback.PlaybackExtractionErrors.FailedToExtractData
import mapping.registration.AddressType
import models.playback.UserAnswers
import models.playback.http.Correspondence
import org.scalatest.{EitherValues, FreeSpec, MustMatchers}
import pages.register._

class CorrespondenceExtractorSpec extends FreeSpec with MustMatchers with EitherValues with Generators with SpecBaseHelpers {

  val correspondenceExtractor: PlaybackExtractor[Correspondence] =
    injector.instanceOf[CorrespondenceExtractor]

  "Correspondence Extractor" - {

    "when there is correspondence" - {
      
      val correspondence = Correspondence(
        abroadIndicator = false,
        name = "Trust Ltd",
        address = AddressType(
          "line1", "line2", None, None, None, "UK"
        ),
        bpMatchStatus = None,
        phoneNumber = "1225645"
      )

      val ua = UserAnswers("fakeId")

      val extraction = correspondenceExtractor.extract(ua, correspondence)

      extraction.right.value.get(TrustNamePage).get mustBe "Trust Ltd"

    }

  }

}
