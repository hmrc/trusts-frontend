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

package mapping.playback

import base.SpecBaseHelpers
import generators.Generators
import models.playback.{DisplayTrustCharityType, DisplayTrustIdentificationOrgType, UserAnswers}
import org.scalatest.{EitherValues, FreeSpec, MustMatchers}
import pages.beneficiaries.charity.CharityBeneficiaryNamePage

class CharityBeneficiaryExtractorSpec extends FreeSpec with MustMatchers
  with EitherValues with Generators with SpecBaseHelpers {

  def generateCharity(index: Int) = DisplayTrustCharityType(
    lineNo = s"$index",
    bpMatchStatus = Some("01"),
    organisationName = s"Charity $index",
    beneficiaryDiscretion = Some(true),
    beneficiaryShareOfIncome = Some("98"),
    identification = Some(
      DisplayTrustIdentificationOrgType(
        safeId = Some("8947584-94759745-84758745"),
        utr = Some(s"${index}234567890"),
        address = None
      )
    ),
    entityStart = "2019-11-26"
  )

  val charityExtractor : PlaybackExtractor[List[DisplayTrustCharityType]] =
    injector.instanceOf[CharityBeneficiaryExtractor]

  "Charity Beneficiary Extractor" - {

    "when no charities" - {

      "must return user answers" in {

        val charities : List[DisplayTrustCharityType] = Nil

        val ua = UserAnswers("fakeId")

        val extraction = charityExtractor.extract(ua, charities)

        extraction mustBe 'right

      }

    }

    "when there are charities" - {

      "must return user answers updated" in {
        val charities = (for(index <- 0 to 2) yield generateCharity(index)).toList

        val ua = UserAnswers("fakeId")

        val extraction = charityExtractor.extract(ua, charities)

        extraction mustBe 'right

        extraction.right.value.success.value.get(CharityBeneficiaryNamePage(0)).get mustBe "Charity 0"
        extraction.right.value.success.value.get(CharityBeneficiaryNamePage(1)).get mustBe "Charity 1"
      }

    }

  }

}
