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
import models.core.pages.UKAddress
import models.playback.http._
import models.playback.{MetaData, UserAnswers}
import org.scalatest.{EitherValues, FreeSpec, MustMatchers}
import pages.register.beneficiaries.charity._

class CharityBeneficiaryExtractorSpec extends FreeSpec with MustMatchers
  with EitherValues with Generators with SpecBaseHelpers {

  def generateCharity(index: Int) = DisplayTrustCharityType(
    lineNo = s"$index",
    bpMatchStatus = Some("01"),
    organisationName = s"Charity $index",
    beneficiaryDiscretion = Some(false), // This value is inferred in the extractor
    beneficiaryShareOfIncome = Some("98"),
    identification = Some(
      DisplayTrustIdentificationOrgType(
        safeId = Some("8947584-94759745-84758745"),
        utr = Some(s"${index}234567890"),
        address = Some(AddressType(s"line $index", "line2", None, None, Some("NE11NE"), "GB"))
      )
    ),
    entityStart = "2019-11-26"
  )

  val charityExtractor : PlaybackExtractor[Option[List[DisplayTrustCharityType]]] =
    injector.instanceOf[CharityBeneficiaryExtractor]

  "Charity Beneficiary Extractor" - {

    "when no charities" - {

      "must return user answers" in {

        val charities = None

        val ua = UserAnswers("fakeId")

        val extraction = charityExtractor.extract(ua, charities)

        extraction mustBe 'right

      }

    }

    "when there are charities" - {

      "with minimum data must return user answers updated" in {
        val charity = List(DisplayTrustCharityType(
          lineNo = s"1",
          bpMatchStatus = Some("01"),
          organisationName = s"Charity 1",
          beneficiaryDiscretion = None,
          beneficiaryShareOfIncome = None,
          identification = None,
          entityStart = "2019-11-26"
        ))

        val ua = UserAnswers("fakeId")

        val extraction = charityExtractor.extract(ua, Some(charity))

        extraction.right.value.success.value.get(CharityBeneficiaryNamePage(0)).get mustBe "Charity 1"
        extraction.right.value.success.value.get(CharityBeneficiaryMetaData(0)).get mustBe MetaData("1", Some("01"), "2019-11-26")
        extraction.right.value.success.value.get(CharityBeneficiaryDiscretionYesNoPage(0)).get mustBe true
        extraction.right.value.success.value.get(CharityBeneficiaryShareOfIncomePage(0)) mustNot be(defined)
        extraction.right.value.success.value.get(CharityBeneficiaryUtrPage(0)) mustNot be(defined)
        extraction.right.value.success.value.get(CharityBeneficiarySafeIdPage(0)) mustNot be(defined)
        extraction.right.value.success.value.get(CharityBeneficiaryAddressPage(0)) mustNot be(defined)
        extraction.right.value.success.value.get(CharityBeneficiaryAddressUKYesNoPage(0)) mustNot be(defined)
      }

      "with full data must return user answers updated" in {
        val charities = (for(index <- 0 to 2) yield generateCharity(index)).toList

        val ua = UserAnswers("fakeId")

        val extraction = charityExtractor.extract(ua, Some(charities))

        extraction mustBe 'right

        extraction.right.value.success.value.get(CharityBeneficiaryNamePage(0)).get mustBe "Charity 0"
        extraction.right.value.success.value.get(CharityBeneficiaryNamePage(1)).get mustBe "Charity 1"

        extraction.right.value.success.value.get(CharityBeneficiaryMetaData(0)).get mustBe MetaData("0", Some("01"), "2019-11-26")

        extraction.right.value.success.value.get(CharityBeneficiaryDiscretionYesNoPage(0)).get mustBe false
        extraction.right.value.success.value.get(CharityBeneficiaryDiscretionYesNoPage(1)).get mustBe false

        extraction.right.value.success.value.get(CharityBeneficiaryShareOfIncomePage(0)).get mustBe "98"
        extraction.right.value.success.value.get(CharityBeneficiaryShareOfIncomePage(1)).get mustBe "98"

        extraction.right.value.success.value.get(CharityBeneficiaryUtrPage(0)).get mustBe "0234567890"
        extraction.right.value.success.value.get(CharityBeneficiaryUtrPage(1)).get mustBe "1234567890"

        extraction.right.value.success.value.get(CharityBeneficiarySafeIdPage(0)).get mustBe "8947584-94759745-84758745"
        extraction.right.value.success.value.get(CharityBeneficiarySafeIdPage(1)).get mustBe "8947584-94759745-84758745"

        extraction.right.value.success.value.get(CharityBeneficiaryAddressPage(0)).get mustBe UKAddress("line 0", "line2", None, None, "NE11NE")
        extraction.right.value.success.value.get(CharityBeneficiaryAddressPage(1)).get mustBe UKAddress("line 1", "line2", None, None, "NE11NE")

        extraction.right.value.success.value.get(CharityBeneficiaryAddressUKYesNoPage(0)).get mustBe true
        extraction.right.value.success.value.get(CharityBeneficiaryAddressUKYesNoPage(1)).get mustBe true
      }

    }

  }

}
