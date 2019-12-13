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

import base.SpecBaseHelpers
import generators.Generators
import mapping.playback.PlaybackExtractionErrors.FailedToExtractData
import mapping.playback.PlaybackExtractor
import models.core.pages.UKAddress
import models.playback.http._
import models.playback.{MetaData, UserAnswers}
import org.scalatest.{EitherValues, FreeSpec, MustMatchers}
import pages.register.beneficiaries.charity._
import pages.register.beneficiaries.company._
import pages.register.beneficiaries.other._
import pages.register.beneficiaries.trust._

class BeneficiaryExtractorSpec extends FreeSpec with MustMatchers
  with EitherValues with Generators with SpecBaseHelpers {

  val beneficiaryExtractor : PlaybackExtractor[DisplayTrustBeneficiaryType] =
    injector.instanceOf[BeneficiaryExtractor]

  "Beneficiary Extractor" - {

    "when no beneficiary" - {

      "must return an error" in {

        val beneficiary = DisplayTrustBeneficiaryType(None, None, None, None, None, None, None)

        val ua = UserAnswers("fakeId")

        val extraction = beneficiaryExtractor.extract(ua, beneficiary)

        extraction.left.value mustBe a[FailedToExtractData]

      }

    }

    "when there are beneficiaries of different type" - {

      "must return user answers updated" in {
        val beneficiary = DisplayTrustBeneficiaryType(
          individualDetails = None,
          company = Some(
            List(
              DisplayTrustCompanyType(
                lineNo = s"1",
                bpMatchStatus = Some("01"),
                organisationName = s"Company 1",
                beneficiaryDiscretion = Some(false),
                beneficiaryShareOfIncome = Some("10"),
                identification = Some(
                  DisplayTrustIdentificationOrgType(
                    safeId = Some("8947584-94759745-84758745"),
                    utr = Some(s"1234567890"),
                    address = Some(AddressType(s"line 1", "line 2", None, None, Some("NE11NE"), "GB"))
                  )
                ),
                entityStart = "2019-11-26"
              )
            )
          ),
          trust = Some(
            List(
              DisplayTrustBeneficiaryTrustType(
                lineNo = s"1",
                bpMatchStatus = Some("01"),
                organisationName = s"Trust 1",
                beneficiaryDiscretion = Some(false),
                beneficiaryShareOfIncome = Some("10"),
                identification = Some(
                  DisplayTrustIdentificationOrgType(
                    safeId = Some("8947584-94759745-84758745"),
                    utr = Some(s"1234567890"),
                    address = Some(AddressType(s"line 1", "line 2", None, None, Some("NE11NE"), "GB"))
                  )
                ),
                entityStart = "2019-11-26"
              )
            )
          ),
          charity = Some(
            List(
              DisplayTrustCharityType(
                lineNo = s"1",
                bpMatchStatus = Some("01"),
                organisationName = s"Charity 1",
                beneficiaryDiscretion = Some(false),
                beneficiaryShareOfIncome = Some("10"),
                identification = Some(
                  DisplayTrustIdentificationOrgType(
                    safeId = Some("8947584-94759745-84758745"),
                    utr = Some(s"1234567890"),
                    address = Some(AddressType(s"line 1", "line 2", None, None, Some("NE11NE"), "GB"))
                  )
                ),
                entityStart = "2019-11-26"
              )
            )
          ),
          unidentified = None,
          large = None,
          other = Some(
            List(
              DisplayTrustOtherType(
                lineNo = s"1",
                bpMatchStatus = Some("01"),
                description = s"Other 1",
                beneficiaryDiscretion = Some(false),
                beneficiaryShareOfIncome = Some("10"),
                address = Some(AddressType(s"line 1", "line 2", None, None, Some("NE11NE"), "GB")),
                entityStart = "2019-11-26"
              )
            )
          )
        )

        val ua = UserAnswers("fakeId")

        val extraction = beneficiaryExtractor.extract(ua, beneficiary)

        extraction.right.value.get(CompanyBeneficiaryNamePage(0)).get mustBe "Company 1"
        extraction.right.value.get(CompanyBeneficiaryDiscretionYesNoPage(0)).get mustBe false
        extraction.right.value.get(CompanyBeneficiaryShareOfIncomePage(0)).get mustBe "10"
        extraction.right.value.get(CompanyBeneficiaryAddressYesNoPage(0)).get mustBe true
        extraction.right.value.get(CompanyBeneficiaryAddressUKYesNoPage(0)).get mustBe true
        extraction.right.value.get(CompanyBeneficiaryAddressPage(0)).get mustBe UKAddress("line 1", "line 2", None, None, "NE11NE")
        extraction.right.value.get(CompanyBeneficiaryUtrPage(0)).get mustBe "1234567890"
        extraction.right.value.get(CompanyBeneficiaryMetaData(0)).get mustBe MetaData("1", Some("01"), "2019-11-26")
        extraction.right.value.get(CompanyBeneficiarySafeIdPage(0)) must be(defined)

        extraction.right.value.get(TrustBeneficiaryNamePage(0)).get mustBe "Trust 1"
        extraction.right.value.get(TrustBeneficiaryDiscretionYesNoPage(0)).get mustBe false
        extraction.right.value.get(TrustBeneficiaryShareOfIncomePage(0)).get mustBe "10"
        extraction.right.value.get(TrustBeneficiaryAddressYesNoPage(0)).get mustBe true
        extraction.right.value.get(TrustBeneficiaryAddressUKYesNoPage(0)).get mustBe true
        extraction.right.value.get(TrustBeneficiaryAddressPage(0)).get mustBe UKAddress("line 1", "line 2", None, None, "NE11NE")
        extraction.right.value.get(TrustBeneficiaryUtrPage(0)).get mustBe "1234567890"
        extraction.right.value.get(TrustBeneficiaryMetaData(0)).get mustBe MetaData("1", Some("01"), "2019-11-26")
        extraction.right.value.get(TrustBeneficiarySafeIdPage(0)) must be(defined)

        extraction.right.value.get(CharityBeneficiaryNamePage(0)).get mustBe "Charity 1"
        extraction.right.value.get(CharityBeneficiaryDiscretionYesNoPage(0)).get mustBe false
        extraction.right.value.get(CharityBeneficiaryShareOfIncomePage(0)).get mustBe "10"
        extraction.right.value.get(CharityBeneficiaryAddressYesNoPage(0)).get mustBe true
        extraction.right.value.get(CharityBeneficiaryAddressUKYesNoPage(0)).get mustBe true
        extraction.right.value.get(CharityBeneficiaryAddressPage(0)).get mustBe UKAddress("line 1", "line 2", None, None, "NE11NE")
        extraction.right.value.get(CharityBeneficiaryUtrPage(0)).get mustBe "1234567890"
        extraction.right.value.get(CharityBeneficiaryMetaData(0)).get mustBe MetaData("1", Some("01"), "2019-11-26")
        extraction.right.value.get(CharityBeneficiarySafeIdPage(0)) must be(defined)
        
        extraction.right.value.get(OtherBeneficiaryNamePage(0)).get mustBe "Other 1"
        extraction.right.value.get(OtherBeneficiaryDiscretionYesNoPage(0)).get mustBe false
        extraction.right.value.get(OtherBeneficiaryShareOfIncomePage(0)).get mustBe "10"
        extraction.right.value.get(OtherBeneficiaryAddressYesNoPage(0)).get mustBe true
        extraction.right.value.get(OtherBeneficiaryAddressUKYesNoPage(0)).get mustBe true
        extraction.right.value.get(OtherBeneficiaryAddressPage(0)).get mustBe UKAddress("line 1", "line 2", None, None, "NE11NE")
        extraction.right.value.get(OtherBeneficiaryMetaData(0)).get mustBe MetaData("1", Some("01"), "2019-11-26")
      }

    }

  }

}
