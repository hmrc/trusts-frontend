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

package mapping.playback.settlors

import java.time.LocalDate

import base.SpecBaseHelpers
import generators.Generators
import mapping.playback.PlaybackExtractor
import mapping.registration.PassportType
import models.core.pages.{FullName, IndividualOrBusiness, InternationalAddress, UKAddress}
import models.playback.http._
import models.playback.{MetaData, UserAnswers}
import org.joda.time.DateTime
import org.scalatest.{EitherValues, FreeSpec, MustMatchers}
import pages.register.settlors.living_settlor._

class SettlorIndividualExtractorSpec extends FreeSpec with MustMatchers
  with EitherValues with Generators with SpecBaseHelpers {

  def generateSettlorIndividual(index: Int) = DisplayTrustSettlor(
    lineNo = s"$index",
    bpMatchStatus = Some("01"),
    name = NameType(s"First Name $index", None, s"Last Name $index"),
    dateOfBirth = Some(DateTime.parse("2070-02-01")),
    identification = Some(
      DisplayTrustIdentificationType(
        safeId = Some("8947584-94759745-84758745"),
        nino = index match {
          case 0 => Some(s"${index}234567890")
          case _ => None
        },
        passport = index match {
          case 2 => Some(PassportType("KSJDFKSDHF6456545147852369QWER", LocalDate.of(2020,2,2), "DE"))
          case _ => None
        },
        address = index match {
          case 1 => Some(AddressType(s"line $index", "line2", None, None, None, "DE"))
          case 2 => Some(AddressType(s"line $index", "line2", None, None, Some("NE11NE"), "GB"))
          case _ => None
        }
      )
    ),
    entityStart = "2019-11-26"
  )

  val settlorIndividualExtractor : PlaybackExtractor[Option[List[DisplayTrustSettlor]]] =
    injector.instanceOf[SettlorIndividualExtractor]

  "Settlor Individual Extractor" - {

    "when no living settlor individuals" - {

      "must return user answers" in {

        val trusts = None

        val ua = UserAnswers("fakeId")

        val extraction = settlorIndividualExtractor.extract(ua, trusts)

        extraction mustBe 'left

      }

    }

    "when there are living settlor individuals" - {

      "with minimum data must return user answers updated" in {
        val trust = List(DisplayTrustSettlor(
          lineNo = s"1",
          bpMatchStatus = Some("01"),
          name = NameType("First Name", None, "Last Name"),
          dateOfBirth = None,
          identification = None,
          entityStart = "2019-11-26"
        ))

        val ua = UserAnswers("fakeId")

        val extraction = settlorIndividualExtractor.extract(ua, Some(trust))

        extraction.right.value.get(SettlorIndividualOrBusinessPage(0)).get mustBe IndividualOrBusiness.Individual
        extraction.right.value.get(SettlorIndividualNamePage(0)).get mustBe FullName("First Name", None, "Last Name")
        extraction.right.value.get(SettlorIndividualNINOYesNoPage(0)).get mustBe false
        extraction.right.value.get(SettlorIndividualNINOPage(0)) mustNot be(defined)
        extraction.right.value.get(SettlorIndividualAddressYesNoPage(0)).get mustBe false
        extraction.right.value.get(SettlorIndividualAddressUKYesNoPage(0)) mustNot be(defined)
        extraction.right.value.get(SettlorIndividualAddressUKPage(0)) mustNot be(defined)
        extraction.right.value.get(SettlorIndividualAddressInternationalPage(0)) mustNot be(defined)
        extraction.right.value.get(SettlorIndividualPassportIDCardYesNoPage(0)).get mustBe false
        extraction.right.value.get(SettlorIndividualPassportIDCardPage(0)) mustNot be(defined)
        extraction.right.value.get(SettlorSafeIdPage(0)) mustNot be(defined)
        extraction.right.value.get(SettlorMetaData(0)).get mustBe MetaData("1", Some("01"), "2019-11-26")
      }

      "with full data must return user answers updated" in {
        val trusts = (for(index <- 0 to 2) yield generateSettlorIndividual(index)).toList

        val ua = UserAnswers("fakeId")

        val extraction = settlorIndividualExtractor.extract(ua, Some(trusts))

        extraction mustBe 'right

        extraction.right.value.get(SettlorIndividualNamePage(0)).get mustBe FullName("First Name 0", None, "Last Name 0")
        extraction.right.value.get(SettlorIndividualNamePage(1)).get mustBe FullName("First Name 1", None, "Last Name 1")
        extraction.right.value.get(SettlorIndividualNamePage(2)).get mustBe FullName("First Name 2", None, "Last Name 2")

        extraction.right.value.get(SettlorMetaData(0)).get mustBe MetaData("0", Some("01"), "2019-11-26")
        extraction.right.value.get(SettlorMetaData(1)).get mustBe MetaData("1", Some("01"), "2019-11-26")
        extraction.right.value.get(SettlorMetaData(2)).get mustBe MetaData("2", Some("01"), "2019-11-26")

        extraction.right.value.get(SettlorIndividualNINOYesNoPage(0)).get mustBe true
        extraction.right.value.get(SettlorIndividualNINOYesNoPage(1)).get mustBe false
        extraction.right.value.get(SettlorIndividualNINOYesNoPage(2)).get mustBe false

        extraction.right.value.get(SettlorIndividualNINOPage(0)).get mustBe "0234567890"
        extraction.right.value.get(SettlorIndividualNINOPage(1)) mustNot be(defined)
        extraction.right.value.get(SettlorIndividualNINOPage(2)) mustNot be(defined)

        extraction.right.value.get(SettlorIndividualAddressYesNoPage(0)).get mustBe false
        extraction.right.value.get(SettlorIndividualAddressYesNoPage(1)).get mustBe true
        extraction.right.value.get(SettlorIndividualAddressYesNoPage(2)).get mustBe true

        extraction.right.value.get(SettlorIndividualAddressInternationalPage(0)) mustNot be(defined)
        extraction.right.value.get(SettlorIndividualAddressUKPage(0)) mustNot be(defined)
        extraction.right.value.get(SettlorIndividualAddressInternationalPage(1)).get mustBe InternationalAddress("line 1", "line2", None, "DE")
        extraction.right.value.get(SettlorIndividualAddressUKPage(1)) mustNot be(defined)
        extraction.right.value.get(SettlorIndividualAddressInternationalPage(2)) mustNot be(defined)
        extraction.right.value.get(SettlorIndividualAddressUKPage(2)).get mustBe UKAddress("line 2", "line2", None, None, "NE11NE")

        extraction.right.value.get(SettlorIndividualAddressUKYesNoPage(0)) mustNot be(defined)
        extraction.right.value.get(SettlorIndividualAddressUKYesNoPage(1)).get mustBe false
        extraction.right.value.get(SettlorIndividualAddressUKYesNoPage(2)).get mustBe true

        extraction.right.value.get(SettlorIndividualPassportIDCardYesNoPage(0)).get mustBe false
        extraction.right.value.get(SettlorIndividualPassportIDCardPage(0)) mustNot be(defined)
        extraction.right.value.get(SettlorIndividualPassportIDCardYesNoPage(1)).get mustBe false
        extraction.right.value.get(SettlorIndividualPassportIDCardPage(1)) mustNot be(defined)
        extraction.right.value.get(SettlorIndividualPassportIDCardYesNoPage(2)).get mustBe true
        extraction.right.value.get(SettlorIndividualPassportIDCardPage(2)).get.country mustBe "DE"

        extraction.right.value.get(SettlorSafeIdPage(0)).get mustBe "8947584-94759745-84758745"
        extraction.right.value.get(SettlorSafeIdPage(1)).get mustBe "8947584-94759745-84758745"
        extraction.right.value.get(SettlorSafeIdPage(2)).get mustBe "8947584-94759745-84758745"

      }

    }

  }

}
