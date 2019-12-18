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

import base.SpecBaseHelpers
import generators.Generators
import mapping.playback.PlaybackExtractionErrors.FailedToExtractData
import mapping.playback.PlaybackExtractor
import models.core.pages.{FullName, IndividualOrBusiness}
import models.playback.http._
import models.playback.{MetaData, UserAnswers}
import org.scalatest.{EitherValues, FreeSpec, MustMatchers}
import pages.register.settlors.deceased_settlor._
import pages.register.settlors.living_settlor._

class SettlorExtractorSpec extends FreeSpec with MustMatchers
  with EitherValues with Generators with SpecBaseHelpers {

  val settlorExtractor: PlaybackExtractor[DisplayTrustEntitiesType] =
    injector.instanceOf[SettlorExtractor]

  "Settlor Extractor" - {

    "when no setllors" - {

      "must return an error" in {

        val entities = DisplayTrustEntitiesType(None,
          DisplayTrustBeneficiaryType(None, None, None, None, None, None, None),
          None, DisplayTrustLeadTrusteeType(None, None),
          None, None, None)

        val ua = UserAnswers("fakeId")

        val extraction = settlorExtractor.extract(ua, entities)

        extraction.left.value mustBe a[FailedToExtractData]

      }

    }

    "when there are settlors of different types" - {

      "must return user answers updated" in {

        val entities = DisplayTrustEntitiesType(
          naturalPerson = None,
          beneficiary = DisplayTrustBeneficiaryType(
            individualDetails = None,
            company = None,
            trust = None,
            charity = None,
            unidentified = None,
            large = None,
            other = None),
          deceased = Some(DisplayTrustWillType(
            lineNo = "1",
            bpMatchStatus = Some("01"),
            name = NameType("First Name", None, "Last Name"),
            dateOfBirth = None,
            dateOfDeath = None,
            identification = None,
            entityStart = "2019-11-26"
          )),
          leadTrustee = DisplayTrustLeadTrusteeType(leadTrusteeInd = None, leadTrusteeOrg = None),
          trustees = None,
          protectors = None,
          settlors = Some(DisplayTrustSettlors(
            settlor = None,
            settlorCompany = Some(List(DisplayTrustSettlorCompany(
              lineNo = s"1",
              bpMatchStatus = Some("01"),
              name = s"Company Settlor 1",
              companyType = Some("Trading"),
              companyTime = Some(false),
              identification = Some(
                DisplayTrustIdentificationOrgType(
                  safeId = Some("8947584-94759745-84758745"),
                  utr = Some("1234567890"),
                  address = None
                )
              ),
              entityStart = "2019-11-26"
            )))
          ))
        )

        val ua = UserAnswers("fakeId")

        val extraction = settlorExtractor.extract(ua, entities)

        extraction.right.value.get(SettlorsNamePage).get mustBe FullName("First Name", None, "Last Name")
        extraction.right.value.get(SettlorDateOfDeathYesNoPage).get mustBe false
        extraction.right.value.get(SettlorDateOfDeathPage) mustNot be(defined)
        extraction.right.value.get(SettlorDateOfBirthYesNoPage).get mustBe false
        extraction.right.value.get(SettlorsDateOfBirthPage) mustNot be(defined)
        extraction.right.value.get(SettlorsNINoYesNoPage).get mustBe false
        extraction.right.value.get(SettlorNationalInsuranceNumberPage) mustNot be(defined)
        extraction.right.value.get(SettlorsLastKnownAddressYesNoPage) mustNot be(defined)
        extraction.right.value.get(SettlorsUKAddressPage) mustNot be(defined)
        extraction.right.value.get(SettlorsInternationalAddressPage) mustNot be(defined)
        extraction.right.value.get(SettlorsPassportIDCardPage) mustNot be(defined)

        extraction.right.value.get(SettlorIndividualOrBusinessPage(0)).get mustBe IndividualOrBusiness.Business
        extraction.right.value.get(SettlorBusinessNamePage(0)).get mustBe "Company Settlor 1"
        extraction.right.value.get(SettlorUtrYesNoPage(0)).get mustBe true
        extraction.right.value.get(SettlorUtrPage(0)).get mustBe "1234567890"
        extraction.right.value.get(SettlorIndividualAddressYesNoPage(0)).get mustBe false
        extraction.right.value.get(SettlorIndividualAddressUKYesNoPage(0)) mustNot be(defined)
        extraction.right.value.get(SettlorIndividualAddressUKPage(0)) mustNot be(defined)
        extraction.right.value.get(SettlorIndividualAddressInternationalPage(0)) mustNot be(defined)
        extraction.right.value.get(SettlorCompanyTypePage(0)).get mustBe "Trading"
        extraction.right.value.get(SettlorCompanyTimePage(0)).get mustBe false
        extraction.right.value.get(SettlorSafeIdPage(0)).get mustBe "8947584-94759745-84758745"
        extraction.right.value.get(SettlorMetaData(0)).get mustBe MetaData("1", Some("01"), "2019-11-26")

      }

    }

  }

}
