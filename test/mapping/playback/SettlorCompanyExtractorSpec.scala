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
import models.core.pages.{IndividualOrBusiness, InternationalAddress, UKAddress}
import models.playback.http._
import models.playback.{MetaData, UserAnswers}
import org.scalatest.{EitherValues, FreeSpec, MustMatchers}
import pages.register.settlors.living_settlor._

class SettlorCompanyExtractorSpec extends FreeSpec with MustMatchers
  with EitherValues with Generators with SpecBaseHelpers {

  def generateSettlorCompany(index: Int) = DisplayTrustSettlorCompany(
    lineNo = s"$index",
    bpMatchStatus = Some("01"),
    name = s"Company Settlor $index",
    companyType = index match {
      case 0 => Some("Trading")
      case 1 => Some("Investment")
      case _ => None
    },
    companyTime = index match {
      case 0 => Some(false)
      case 1 => Some(true)
      case _ => None
    },
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

  val settlorCompanyExtractor : PlaybackExtractor[Option[List[DisplayTrustSettlorCompany]]] =
    injector.instanceOf[SettlorCompanyExtractor]

  "Settlor Company Extractor" - {

    "when no settlor companies" - {

      "must return user answers" in {

        val trusts = None

        val ua = UserAnswers("fakeId")

        val extraction = settlorCompanyExtractor.extract(ua, trusts)

        extraction mustBe 'left

      }

    }

    "when there are settlor companies" - {

      "with minimum data must return user answers updated" in {
        val trust = List(DisplayTrustSettlorCompany(
          lineNo = s"1",
          bpMatchStatus = Some("01"),
          name = s"Company Settlor 1",
          companyType = None,
          companyTime = None,
          identification = None,
          entityStart = "2019-11-26"
        ))

        val ua = UserAnswers("fakeId")

        val extraction = settlorCompanyExtractor.extract(ua, Some(trust))

        extraction.right.value.get(SettlorIndividualOrBusinessPage(0)).get mustBe IndividualOrBusiness.Business
        extraction.right.value.get(SettlorBusinessNamePage(0)).get mustBe "Company Settlor 1"
        extraction.right.value.get(SettlorUtrYesNoPage(0)).get mustBe false
        extraction.right.value.get(SettlorUtrPage(0)) mustNot be(defined)
        extraction.right.value.get(SettlorIndividualAddressYesNoPage(0)).get mustBe false
        extraction.right.value.get(SettlorIndividualAddressUKYesNoPage(0)) mustNot be(defined)
        extraction.right.value.get(SettlorIndividualAddressUKPage(0)) mustNot be(defined)
        extraction.right.value.get(SettlorIndividualAddressInternationalPage(0)) mustNot be(defined)
        extraction.right.value.get(SettlorCompanyTypePage(0)) mustNot be(defined)
        extraction.right.value.get(SettlorCompanyTimePage(0)) mustNot be(defined)
        extraction.right.value.get(SettlorSafeIdPage(0)) mustNot be(defined)
        extraction.right.value.get(SettlorMetaData(0)).get mustBe MetaData("1", Some("01"), "2019-11-26")
      }

      "with full data must return user answers updated" in {
        val trusts = (for(index <- 0 to 2) yield generateSettlorCompany(index)).toList

        val ua = UserAnswers("fakeId")

        val extraction = settlorCompanyExtractor.extract(ua, Some(trusts))

        extraction mustBe 'right

        extraction.right.value.get(SettlorBusinessNamePage(0)).get mustBe "Company Settlor 0"
        extraction.right.value.get(SettlorBusinessNamePage(1)).get mustBe "Company Settlor 1"
        extraction.right.value.get(SettlorBusinessNamePage(2)).get mustBe "Company Settlor 2"

        extraction.right.value.get(SettlorMetaData(0)).get mustBe MetaData("0", Some("01"), "2019-11-26")
        extraction.right.value.get(SettlorMetaData(1)).get mustBe MetaData("1", Some("01"), "2019-11-26")
        extraction.right.value.get(SettlorMetaData(2)).get mustBe MetaData("2", Some("01"), "2019-11-26")

        extraction.right.value.get(SettlorCompanyTypePage(0)).get mustBe "Trading"
        extraction.right.value.get(SettlorCompanyTypePage(1)).get mustBe "Investment"
        extraction.right.value.get(SettlorCompanyTypePage(2)) mustNot be(defined)

        extraction.right.value.get(SettlorCompanyTimePage(0)).get mustBe false
        extraction.right.value.get(SettlorCompanyTimePage(1)).get mustBe true
        extraction.right.value.get(SettlorCompanyTimePage(2)) mustNot be(defined)

        extraction.right.value.get(SettlorUtrPage(0)) mustNot be(defined)
        extraction.right.value.get(SettlorUtrPage(1)).get mustBe "1234567890"
        extraction.right.value.get(SettlorUtrPage(2)) mustNot be(defined)

        extraction.right.value.get(SettlorSafeIdPage(0)).get mustBe "8947584-94759745-84758745"
        extraction.right.value.get(SettlorSafeIdPage(1)).get mustBe "8947584-94759745-84758745"
        extraction.right.value.get(SettlorSafeIdPage(2)).get mustBe "8947584-94759745-84758745"

        extraction.right.value.get(SettlorIndividualAddressYesNoPage(0)).get mustBe true
        extraction.right.value.get(SettlorIndividualAddressYesNoPage(1)).get mustBe false
        extraction.right.value.get(SettlorIndividualAddressYesNoPage(2)).get mustBe true

        extraction.right.value.get(SettlorIndividualAddressInternationalPage(0)).get mustBe InternationalAddress("line 0", "line2", None, "DE")
        extraction.right.value.get(SettlorIndividualAddressUKPage(0)) mustNot be(defined)
        extraction.right.value.get(SettlorIndividualAddressInternationalPage(1)) mustNot be(defined)
        extraction.right.value.get(SettlorIndividualAddressUKPage(1)) mustNot be(defined)
        extraction.right.value.get(SettlorIndividualAddressInternationalPage(2)) mustNot be(defined)
        extraction.right.value.get(SettlorIndividualAddressUKPage(2)).get mustBe UKAddress("line 2", "line2", None, None, "NE11NE")

        extraction.right.value.get(SettlorIndividualAddressUKYesNoPage(0)).get mustBe false
        extraction.right.value.get(SettlorIndividualAddressUKYesNoPage(1)) mustNot be(defined)
        extraction.right.value.get(SettlorIndividualAddressUKYesNoPage(2)).get mustBe true
      }

    }

  }

}
