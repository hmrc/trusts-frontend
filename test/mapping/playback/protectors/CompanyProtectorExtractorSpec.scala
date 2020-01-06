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

package mapping.playback.protectors

import base.SpecBaseHelpers
import generators.Generators
import mapping.playback.PlaybackExtractor
import models.core.pages.{InternationalAddress, UKAddress}
import models.playback.http._
import models.playback.{MetaData, UserAnswers}
import models.registration.pages.AddressOrUtr
import org.scalatest.{EitherValues, FreeSpec, MustMatchers}
import pages.register.protectors.company._

class CompanyProtectorExtractorSpec extends FreeSpec with MustMatchers
  with EitherValues with Generators with SpecBaseHelpers {

  def generateProtector(index: Int) = DisplayTrustProtectorCompany(
    lineNo = s"$index",
    bpMatchStatus = Some("01"),
    name = s"Company $index",
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
    entityStart = "2019-11-26"
  )

  val companyProtectorExtractor : PlaybackExtractor[Option[List[DisplayTrustProtectorCompany]]] =
    injector.instanceOf[CompanyProtectorExtractor]

  "Company Protector Extractor" - {

    "when no protectors" - {

      "must return user answers" in {

        val protectors = None

        val ua = UserAnswers("fakeId")

        val extraction = companyProtectorExtractor.extract(ua, protectors)

        extraction mustBe Right(ua)

      }

    }

    "when there are protectors" - {

      "with minimum data must return user answers updated" in {
        val protector = List(DisplayTrustProtectorCompany(
          lineNo = s"1",
          bpMatchStatus = Some("01"),
          name = s"Company 1",
          identification = None,
          entityStart = "2019-11-26"
        ))

        val ua = UserAnswers("fakeId")
        val extraction = companyProtectorExtractor.extract(ua, Some(protector))

        extraction.right.value.get(CompanyProtectorNamePage(0)).get mustBe "Company 1"
        extraction.right.value.get(CompanyProtectorSafeIdPage(0)) mustNot be(defined)
        extraction.right.value.get(CompanyProtectorAddressUKYesNoPage(0)) mustNot be(defined)
        extraction.right.value.get(CompanyProtectorAddressPage(0)) mustNot be(defined)
        extraction.right.value.get(CompanyProtectorUtrPage(0)) mustNot be(defined)
        extraction.right.value.get(CompanyProtectorMetaData(0)).get mustBe MetaData("1", Some("01"), "2019-11-26")
      }

      "with full data must return user answers updated" in {
        val protectors = (for(index <- 0 to 2) yield generateProtector(index)).toList

        val ua = UserAnswers("fakeId")

        val extraction = companyProtectorExtractor.extract(ua, Some(protectors))

        extraction mustBe 'right

        extraction.right.value.get(CompanyProtectorAddressOrUtrPage(0)).get mustBe AddressOrUtr.Address
        extraction.right.value.get(CompanyProtectorAddressOrUtrPage(1)).get mustBe AddressOrUtr.Utr
        extraction.right.value.get(CompanyProtectorAddressOrUtrPage(2)).get mustBe AddressOrUtr.Address

        extraction.right.value.get(CompanyProtectorNamePage(0)).get mustBe "Company 0"
        extraction.right.value.get(CompanyProtectorNamePage(1)).get mustBe "Company 1"
        extraction.right.value.get(CompanyProtectorNamePage(2)).get mustBe "Company 2"

        extraction.right.value.get(CompanyProtectorSafeIdPage(0)).get mustBe "8947584-94759745-84758745"
        extraction.right.value.get(CompanyProtectorSafeIdPage(1)).get mustBe "8947584-94759745-84758745"
        extraction.right.value.get(CompanyProtectorSafeIdPage(2)).get mustBe "8947584-94759745-84758745"

        extraction.right.value.get(CompanyProtectorAddressUKYesNoPage(0)).get mustBe false
        extraction.right.value.get(CompanyProtectorAddressUKYesNoPage(1)) mustNot be(defined)
        extraction.right.value.get(CompanyProtectorAddressUKYesNoPage(2)).get mustBe true

        extraction.right.value.get(CompanyProtectorAddressPage(0)).get mustBe InternationalAddress("line 0", "line2", None, "DE")
        extraction.right.value.get(CompanyProtectorAddressPage(1)) mustNot be(defined)
        extraction.right.value.get(CompanyProtectorAddressPage(2)).get mustBe UKAddress("line 2", "line2", None, None, "NE11NE")

        extraction.right.value.get(CompanyProtectorUtrPage(0)) mustNot be(defined)
        extraction.right.value.get(CompanyProtectorUtrPage(1)).get mustBe "1234567890"
        extraction.right.value.get(CompanyProtectorUtrPage(2)) mustNot be(defined)

        extraction.right.value.get(CompanyProtectorMetaData(0)).get mustBe MetaData("0", Some("01"), "2019-11-26")
        extraction.right.value.get(CompanyProtectorMetaData(1)).get mustBe MetaData("1", Some("01"), "2019-11-26")
        extraction.right.value.get(CompanyProtectorMetaData(2)).get mustBe MetaData("2", Some("01"), "2019-11-26")
      }
    }
  }
}
