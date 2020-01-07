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

package mapping.playback.beneficiaries

import base.SpecBaseHelpers
import generators.Generators
import mapping.playback.PlaybackExtractor
import models.core.pages.{Description, InternationalAddress, UKAddress}
import models.playback.http._
import models.playback.{MetaData, UserAnswers}
import org.scalatest.{EitherValues, FreeSpec, MustMatchers}
import pages.register.beneficiaries.company._
import pages.register.beneficiaries.large._

class LargeBeneficiaryExtractorSpec extends FreeSpec with MustMatchers
  with EitherValues with Generators with SpecBaseHelpers {

  def generateLargeBeneficiary(index: Int) = DisplayTrustLargeType(
    lineNo = s"$index",
    bpMatchStatus = Some("01"),
    organisationName = s"Large $index",
    description = s"Description $index",
    description1 = None,
    description2 = None,
    description3 = None,
    description4 = None,
    numberOfBeneficiary = s"$index",
    identification = Some(
      DisplayTrustIdentificationOrgType(
        safeId = Some("8947584-94759745-84758745"),
        utr = index match {
          case 1 => Some(s"${index}234567890")
          case _ => None
        },
        address = index match {
          case 0 => Some(AddressType(s"line $index", "line2", None, None, None, "DE"))
          case 2 => Some(AddressType(s"line $index", "line2", None, None, Some("NE11NE"), "GB"))
          case _ => None
        }
      )
    ),
    beneficiaryDiscretion = index match {
      case 0 => Some(false)
      case _ => None
    },
    beneficiaryShareOfIncome = index match {
      case 0 => Some("98")
      case _ => None
    },
    entityStart = "2019-11-26"
  )

  val largeBeneficiaryExtractor : PlaybackExtractor[Option[List[DisplayTrustLargeType]]] =
    injector.instanceOf[LargeBeneficiaryExtractor]

  "Large Beneficiary Extractor" - {

    "when no large beneficiaries" - {

      "must return user answers" in {

        val largeBeneficiaries = None

        val ua = UserAnswers("fakeId")

        val extraction = largeBeneficiaryExtractor.extract(ua, largeBeneficiaries)

        extraction mustBe 'left

      }

    }

    "when there are large beneficiaries" - {

      "with minimum data must return user answers updated" in {
        val largeBeneficiary = List(
          DisplayTrustLargeType(
            lineNo = s"1",
            bpMatchStatus = Some("01"),
            organisationName = "Large 1",
            description = s"Description",
            description1 = Some("Description 1"),
            description2 = None,
            description3 = None,
            description4 = None,
            numberOfBeneficiary = "1",
            identification = Some(
              DisplayTrustIdentificationOrgType(
                safeId = Some("8947584-94759745-84758745"),
                utr = None,
                address = Some(AddressType(s"line 1", "line 2", None, None, Some("NE11NE"), "GB"))
              )
            ),
            beneficiaryDiscretion = Some(false),
            beneficiaryShareOfIncome = Some("10"),
            entityStart = "2019-11-26"
          )
        )

        val ua = UserAnswers("fakeId")

        val extraction = largeBeneficiaryExtractor.extract(ua, Some(largeBeneficiary))

        extraction.right.value.get(LargeBeneficiaryNamePage(0)).get mustBe "Large 1"
        extraction.right.value.get(LargeBeneficiaryDescriptionPage(0)).get mustBe Description("Description", Some("Description 1"), None, None, None)
        extraction.right.value.get(LargeBeneficiaryDiscretionYesNoPage(0)).get mustBe false
        extraction.right.value.get(LargeBeneficiaryShareOfIncomePage(0)).get mustBe "10"
        extraction.right.value.get(LargeBeneficiaryAddressYesNoPage(0)).get mustBe true
        extraction.right.value.get(LargeBeneficiaryAddressUKYesNoPage(0)).get mustBe true
        extraction.right.value.get(LargeBeneficiaryAddressPage(0)).get mustBe UKAddress("line 1", "line 2", None, None, "NE11NE")
        extraction.right.value.get(LargeBeneficiaryUtrPage(0)) mustNot be(defined)
        extraction.right.value.get(LargeBeneficiaryMetaData(0)).get mustBe MetaData("1", Some("01"), "2019-11-26")
        extraction.right.value.get(LargeBeneficiarySafeIdPage(0)) must be(defined)
        extraction.right.value.get(LargeBeneficiaryNumberOfBeneficiariesPage(0)).get mustBe "1"
      }

      "with full data must return user answers updated" in {
        val largeBeneficiaries = (for(index <- 0 to 2) yield generateLargeBeneficiary(index)).toList

        val ua = UserAnswers("fakeId")

        val extraction = largeBeneficiaryExtractor.extract(ua, Some(largeBeneficiaries))

        extraction mustBe 'right

        extraction.right.value.get(LargeBeneficiaryNamePage(0)).get mustBe "Large 0"
        extraction.right.value.get(LargeBeneficiaryDescriptionPage(0)).get mustBe Description("Description 0", None, None, None, None)
        extraction.right.value.get(LargeBeneficiaryDiscretionYesNoPage(0)).get mustBe false
        extraction.right.value.get(LargeBeneficiaryShareOfIncomePage(0)).get mustBe "98"
        extraction.right.value.get(LargeBeneficiaryAddressYesNoPage(0)).get mustBe true
        extraction.right.value.get(LargeBeneficiaryAddressUKYesNoPage(0)).get mustBe false
        extraction.right.value.get(LargeBeneficiaryAddressPage(0)).get mustBe InternationalAddress("line 0", "line2", None, "DE")
        extraction.right.value.get(LargeBeneficiaryUtrPage(0)) mustNot be(defined)
        extraction.right.value.get(LargeBeneficiaryMetaData(0)).get mustBe MetaData("0", Some("01"), "2019-11-26")
        extraction.right.value.get(LargeBeneficiarySafeIdPage(0)).get mustBe "8947584-94759745-84758745"
        extraction.right.value.get(LargeBeneficiaryNumberOfBeneficiariesPage(0)).get mustBe "0"

        extraction.right.value.get(LargeBeneficiaryNamePage(1)).get mustBe "Large 1"
        extraction.right.value.get(LargeBeneficiaryDescriptionPage(1)).get mustBe Description("Description 1", None, None, None, None)
        extraction.right.value.get(LargeBeneficiaryDiscretionYesNoPage(1)).get mustBe true
        extraction.right.value.get(LargeBeneficiaryShareOfIncomePage(1)) mustNot be(defined)
        extraction.right.value.get(LargeBeneficiaryAddressYesNoPage(1)).get mustBe false
        extraction.right.value.get(LargeBeneficiaryAddressUKYesNoPage(1)) mustNot be(defined)
        extraction.right.value.get(LargeBeneficiaryAddressPage(1)) mustNot be(defined)
        extraction.right.value.get(LargeBeneficiaryUtrPage(1)).get mustBe "1234567890"
        extraction.right.value.get(LargeBeneficiaryMetaData(1)).get mustBe MetaData("1", Some("01"), "2019-11-26")
        extraction.right.value.get(LargeBeneficiarySafeIdPage(1)).get mustBe "8947584-94759745-84758745"
        extraction.right.value.get(LargeBeneficiaryNumberOfBeneficiariesPage(1)).get mustBe "1"

        extraction.right.value.get(LargeBeneficiaryNamePage(2)).get mustBe "Large 2"
        extraction.right.value.get(LargeBeneficiaryDescriptionPage(2)).get mustBe Description("Description 2", None, None, None, None)
        extraction.right.value.get(LargeBeneficiaryDiscretionYesNoPage(2)).get mustBe true
        extraction.right.value.get(LargeBeneficiaryShareOfIncomePage(2)) mustNot be(defined)
        extraction.right.value.get(LargeBeneficiaryAddressYesNoPage(2)).get mustBe true
        extraction.right.value.get(LargeBeneficiaryAddressUKYesNoPage(2)).get mustBe true
        extraction.right.value.get(LargeBeneficiaryAddressPage(2)).get mustBe UKAddress("line 2", "line2", None, None, "NE11NE")
        extraction.right.value.get(LargeBeneficiaryUtrPage(2)) mustNot be(defined)
        extraction.right.value.get(LargeBeneficiaryMetaData(2)).get mustBe MetaData("2", Some("01"), "2019-11-26")
        extraction.right.value.get(LargeBeneficiarySafeIdPage(2)).get mustBe "8947584-94759745-84758745"
        extraction.right.value.get(LargeBeneficiaryNumberOfBeneficiariesPage(2)).get mustBe "2"
      }

    }

  }

}
