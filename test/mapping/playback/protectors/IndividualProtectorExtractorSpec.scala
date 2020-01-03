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

import java.time.LocalDate

import base.SpecBaseHelpers
import generators.Generators
import mapping.playback.PlaybackExtractor
import mapping.registration.PassportType
import models.core.pages.{FullName, InternationalAddress, UKAddress}
import models.playback.http._
import models.playback.{MetaData, UserAnswers}
import org.joda.time.DateTime
import org.scalatest.{EitherValues, FreeSpec, MustMatchers}
import pages.register.protectors.individual._

class IndividualProtectorExtractorSpec extends FreeSpec with MustMatchers
  with EitherValues with Generators with SpecBaseHelpers {

  def generateProtector(index: Int) = DisplayTrustProtector(
    lineNo = s"$index",
    bpMatchStatus = Some("01"),
    name = NameType(s"First Name $index", None, s"Last Name $index"),
    dateOfBirth = index match {
      case 0 => Some(DateTime.parse("1970-02-01"))
      case _ => None
    },
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

  val individualProtectorExtractor : PlaybackExtractor[Option[List[DisplayTrustProtector]]] =
    injector.instanceOf[IndividualProtectorExtractor]

  "Individual Protector Extractor" - {

    "when no protectors" - {

      "must return user answers" in {

        val protectors = None

        val ua = UserAnswers("fakeId")

        val extraction = individualProtectorExtractor.extract(ua, protectors)

        extraction mustBe Right(ua)

      }

    }

    "when there are protectors" - {

      "with minimum data must return user answers updated" in {
        val protector = List(DisplayTrustProtector(
          lineNo = s"1",
          bpMatchStatus = Some("01"),
          name = NameType(s"First Name", None, s"Last Name"),
          dateOfBirth = Some(DateTime.parse("1970-02-01")),
          identification = None,
          entityStart = "2019-11-26"
        ))

        val ua = UserAnswers("fakeId")
        val extraction = individualProtectorExtractor.extract(ua, Some(protector))

        extraction.right.value.get(IndividualProtectorNamePage(0)).get mustBe FullName("First Name", None, "Last Name")
        extraction.right.value.get(IndividualProtectorNINOYesNoPage(0)).get mustBe false
        extraction.right.value.get(IndividualProtectorNINOPage(0)) mustNot be(defined)
        extraction.right.value.get(IndividualProtectorSafeIdPage(0)) mustNot be(defined)
        extraction.right.value.get(IndividualProtectorPassportIDCardYesNoPage(0)) mustNot be(defined)
        extraction.right.value.get(IndividualProtectorPassportIDCardPage(0)) mustNot be(defined)
        extraction.right.value.get(IndividualProtectorAddressYesNoPage(0)).get mustBe false
        extraction.right.value.get(IndividualProtectorAddressUKYesNoPage(0)) mustNot be(defined)
        extraction.right.value.get(IndividualProtectorAddressUKPage(0)) mustNot be(defined)
        extraction.right.value.get(IndividualProtectorAddressInternationalPage(0)) mustNot be(defined)
        extraction.right.value.get(IndividualProtectorMetaData(0)).get mustBe MetaData("1", Some("01"), "2019-11-26")
      }

      "with full data must return user answers updated" in {
        val protectors = (for(index <- 0 to 2) yield generateProtector(index)).toList

        val ua = UserAnswers("fakeId")

        val extraction = individualProtectorExtractor.extract(ua, Some(protectors))

        extraction mustBe 'right

        extraction.right.value.get(IndividualProtectorNamePage(0)).get mustBe FullName("First Name 0", None, "Last Name 0")
        extraction.right.value.get(IndividualProtectorNamePage(1)).get mustBe FullName("First Name 1", None, "Last Name 1")
        extraction.right.value.get(IndividualProtectorNamePage(2)).get mustBe FullName("First Name 2", None, "Last Name 2")

        extraction.right.value.get(IndividualProtectorNINOYesNoPage(0)).get mustBe true
        extraction.right.value.get(IndividualProtectorNINOYesNoPage(1)).get mustBe false
        extraction.right.value.get(IndividualProtectorNINOYesNoPage(2)).get mustBe false

        extraction.right.value.get(IndividualProtectorNINOPage(0)).get mustBe "0234567890"
        extraction.right.value.get(IndividualProtectorNINOPage(1)) mustNot be(defined)
        extraction.right.value.get(IndividualProtectorNINOPage(2)) mustNot be(defined)

        extraction.right.value.get(IndividualProtectorSafeIdPage(0)).get mustBe "8947584-94759745-84758745"
        extraction.right.value.get(IndividualProtectorSafeIdPage(1)).get mustBe "8947584-94759745-84758745"
        extraction.right.value.get(IndividualProtectorSafeIdPage(2)).get mustBe "8947584-94759745-84758745"

        extraction.right.value.get(IndividualProtectorPassportIDCardYesNoPage(0)) mustNot be(defined)
        extraction.right.value.get(IndividualProtectorPassportIDCardYesNoPage(1)).get mustBe false
        extraction.right.value.get(IndividualProtectorPassportIDCardYesNoPage(2)).get mustBe true

        extraction.right.value.get(IndividualProtectorPassportIDCardPage(0)) mustNot be(defined)
        extraction.right.value.get(IndividualProtectorPassportIDCardPage(1)) mustNot be(defined)
        extraction.right.value.get(IndividualProtectorPassportIDCardPage(2)).get.country mustBe "DE"

        extraction.right.value.get(IndividualProtectorAddressYesNoPage(0)) mustNot be(defined)
        extraction.right.value.get(IndividualProtectorAddressYesNoPage(1)).get mustBe true
        extraction.right.value.get(IndividualProtectorAddressYesNoPage(2)).get mustBe true

        extraction.right.value.get(IndividualProtectorAddressUKYesNoPage(0)) mustNot be(defined)
        extraction.right.value.get(IndividualProtectorAddressUKYesNoPage(1)).get mustBe false
        extraction.right.value.get(IndividualProtectorAddressUKYesNoPage(2)).get mustBe true

        extraction.right.value.get(IndividualProtectorAddressUKPage(0)) mustNot be(defined)
        extraction.right.value.get(IndividualProtectorAddressUKPage(1)) mustNot be(defined)
        extraction.right.value.get(IndividualProtectorAddressUKPage(2)).get mustBe UKAddress("line 2", "line2", None, None, "NE11NE")

        extraction.right.value.get(IndividualProtectorAddressInternationalPage(0)) mustNot be(defined)
        extraction.right.value.get(IndividualProtectorAddressInternationalPage(1)).get mustBe InternationalAddress("line 1", "line2", None, "DE")
        extraction.right.value.get(IndividualProtectorAddressInternationalPage(2)) mustNot be(defined)

        extraction.right.value.get(IndividualProtectorMetaData(0)).get mustBe MetaData("0", Some("01"), "2019-11-26")
        extraction.right.value.get(IndividualProtectorMetaData(1)).get mustBe MetaData("1", Some("01"), "2019-11-26")
        extraction.right.value.get(IndividualProtectorMetaData(2)).get mustBe MetaData("2", Some("01"), "2019-11-26")
      }

    }

  }

}
