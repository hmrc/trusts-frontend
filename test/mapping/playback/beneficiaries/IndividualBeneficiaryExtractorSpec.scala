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

package mapping.playback.beneficiaries

import java.time.LocalDate

import base.SpecBaseHelpers
import generators.Generators
import mapping.playback.PlaybackExtractor
import mapping.registration.PassportType
import models.core.pages.{InternationalAddress, UKAddress}
import models.playback.http._
import models.playback.{MetaData, UserAnswers}
import org.joda.time.DateTime
import org.scalatest.{EitherValues, FreeSpec, MustMatchers}
import pages.register.beneficiaries.individual._

class IndividualBeneficiaryExtractorSpec extends FreeSpec with MustMatchers
  with EitherValues with Generators with SpecBaseHelpers {

  def generateIndividual(index: Int) = DisplayTrustIndividualDetailsType(
    lineNo = s"$index",
    bpMatchStatus = Some("01"),
    name = NameType(s"First Name $index", None, s"Last Name $index"),
    dateOfBirth = Some(DateTime.parse("2070-02-01")),
    vulnerableBeneficiary = true,
    beneficiaryType = Some("Director"),
    beneficiaryDiscretion = index match {
      case 0 => Some(false)
      case _ => None
    },
    beneficiaryShareOfIncome = index match {
      case 0 => Some("98")
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

  val individualExtractor : PlaybackExtractor[Option[List[DisplayTrustIndividualDetailsType]]] =
    injector.instanceOf[IndividualBeneficiaryExtractor]

  "Company Beneficiary Extractor" - {

    "when no individual" - {

      "must return user answers" in {

        val individual = None

        val ua = UserAnswers("fakeId")

        val extraction = individualExtractor.extract(ua, individual)

        extraction mustBe 'left

      }

    }

    "when there are companies" - {

      "with minimum data must return user answers updated" in {
        val individual = List(DisplayTrustIndividualDetailsType(
          lineNo = s"1",
          bpMatchStatus = Some("01"),
          organisationName = s"Company 1",
          beneficiaryDiscretion = None,
          beneficiaryShareOfIncome = None,
          identification = None,
          entityStart = "2019-11-26"
        ))

        val ua = UserAnswers("fakeId")

        val extraction = individualExtractor.extract(ua, Some(individual))

        extraction.right.value.get(CompanyBeneficiaryNamePage(0)).get mustBe "Company 1"
        extraction.right.value.get(CompanyBeneficiaryMetaData(0)).get mustBe MetaData("1", Some("01"), "2019-11-26")
        extraction.right.value.get(CompanyBeneficiaryDiscretionYesNoPage(0)).get mustBe true
        extraction.right.value.get(CompanyBeneficiaryShareOfIncomePage(0)) mustNot be(defined)
        extraction.right.value.get(CompanyBeneficiaryUtrPage(0)) mustNot be(defined)
        extraction.right.value.get(CompanyBeneficiaryUtrPage(0)) mustNot be(defined)
        extraction.right.value.get(CompanyBeneficiaryAddressYesNoPage(0)).get mustBe false
        extraction.right.value.get(CompanyBeneficiaryAddressUKYesNoPage(0)) mustNot be(defined)
        extraction.right.value.get(CompanyBeneficiaryAddressPage(0)) mustNot be(defined)
      }

      "with full data must return user answers updated" in {
        val individuals = (for(index <- 0 to 2) yield generateIndividual(index)).toList

        val ua = UserAnswers("fakeId")

        val extraction = individualExtractor.extract(ua, Some(individuals))

        extraction mustBe 'right

        extraction.right.value.get(CompanyBeneficiaryNamePage(0)).get mustBe "Company 0"
        extraction.right.value.get(CompanyBeneficiaryNamePage(1)).get mustBe "Company 1"
        extraction.right.value.get(CompanyBeneficiaryNamePage(2)).get mustBe "Company 2"

        extraction.right.value.get(CompanyBeneficiaryMetaData(0)).get mustBe MetaData("0", Some("01"), "2019-11-26")
        extraction.right.value.get(CompanyBeneficiaryMetaData(1)).get mustBe MetaData("1", Some("01"), "2019-11-26")
        extraction.right.value.get(CompanyBeneficiaryMetaData(2)).get mustBe MetaData("2", Some("01"), "2019-11-26")

        extraction.right.value.get(CompanyBeneficiaryDiscretionYesNoPage(0)).get mustBe false
        extraction.right.value.get(CompanyBeneficiaryDiscretionYesNoPage(1)).get mustBe true
        extraction.right.value.get(CompanyBeneficiaryDiscretionYesNoPage(1)).get mustBe true

        extraction.right.value.get(CompanyBeneficiaryShareOfIncomePage(0)).get mustBe "98"
        extraction.right.value.get(CompanyBeneficiaryShareOfIncomePage(1)) mustNot be(defined)
        extraction.right.value.get(CompanyBeneficiaryShareOfIncomePage(2)) mustNot be(defined)

        extraction.right.value.get(CompanyBeneficiaryUtrPage(0)) mustNot be(defined)
        extraction.right.value.get(CompanyBeneficiaryUtrPage(1)).get mustBe "1234567890"
        extraction.right.value.get(CompanyBeneficiaryUtrPage(2)) mustNot be(defined)

        extraction.right.value.get(CompanyBeneficiarySafeIdPage(0)).get mustBe "8947584-94759745-84758745"
        extraction.right.value.get(CompanyBeneficiarySafeIdPage(1)).get mustBe "8947584-94759745-84758745"
        extraction.right.value.get(CompanyBeneficiarySafeIdPage(2)).get mustBe "8947584-94759745-84758745"

        extraction.right.value.get(CompanyBeneficiaryAddressYesNoPage(0)).get mustBe true
        extraction.right.value.get(CompanyBeneficiaryAddressYesNoPage(1)).get mustBe false
        extraction.right.value.get(CompanyBeneficiaryAddressYesNoPage(2)).get mustBe true

        extraction.right.value.get(CompanyBeneficiaryAddressPage(0)).get mustBe InternationalAddress("line 0", "line2", None, "DE")
        extraction.right.value.get(CompanyBeneficiaryAddressPage(1)) mustNot be(defined)
        extraction.right.value.get(CompanyBeneficiaryAddressPage(2)).get mustBe UKAddress("line 2", "line2", None, None, "NE11NE")

        extraction.right.value.get(CompanyBeneficiaryAddressUKYesNoPage(0)).get mustBe false
        extraction.right.value.get(CompanyBeneficiaryAddressUKYesNoPage(1)) mustNot be(defined)
        extraction.right.value.get(CompanyBeneficiaryAddressUKYesNoPage(2)).get mustBe true
      }

    }

  }

}
