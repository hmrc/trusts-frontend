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

package mapping.playback

import base.SpecBaseHelpers
import generators.Generators
import mapping.playback.PlaybackExtractionErrors.FailedToExtractData
import models.core.pages.IndividualOrBusiness
import models.playback.http._
import models.playback.{MetaData, UserAnswers}
import org.scalatest.{EitherValues, FreeSpec, MustMatchers}
import pages.register.trustees._

class TrusteeOrgExtractorSpec extends FreeSpec with MustMatchers with EitherValues with Generators with SpecBaseHelpers {

  val trusteeOrgExtractor : PlaybackExtractor[Option[DisplayTrustTrusteeOrgType]] =
    injector.instanceOf[TrusteeOrgExtractor]

  "Trustee Org Extractor" - {

    "when no trustee org" - {

      "must return an error" in {

        val trustee = None

        val ua = UserAnswers("fakeId")

        val extraction = trusteeOrgExtractor.extract(ua, trustee)

        extraction.left.value mustBe a[FailedToExtractData]
      }

    }

    "when there is a trustee organisation" - {

      "which has utr, return user answers updated" in {

        val trustee = DisplayTrustTrusteeOrgType(
          lineNo = "1",
          bpMatchStatus = Some("01"),
          name = "org1",
          phoneNumber = None,
          email = None,
          identification =
            Some(DisplayTrustIdentificationOrgType(
              safeId = Some("8947584-94759745-84758745"),
              utr = Some("1234567890"),
              address = None
            )),
          entityStart = "2019-11-26"
        )

        val ua = UserAnswers("fakeId")

        val extraction = trusteeOrgExtractor.extract(ua, Some(trustee))

        extraction.right.value.get(IsThisLeadTrusteePage(0)).get mustBe false
        extraction.right.value.get(TrusteeIndividualOrBusinessPage(0)).get mustBe IndividualOrBusiness.Business
        extraction.right.value.get(TrusteeOrgNamePage(0)).get mustBe "org1"
        extraction.right.value.get(TrusteeUTRYesNoPagePage(0)).get mustBe true
        extraction.right.value.get(TrusteesUtrPage(0)).get mustBe "1234567890"
        extraction.right.value.get(TrusteeAddressYesNoPage(0)) must not be defined
        extraction.right.value.get(TrusteeAddressInTheUKPage(0)) must not be defined
        extraction.right.value.get(TrusteesUkAddressPage(0)) must not be defined
        extraction.right.value.get(TrusteesInternationalAddressPage(0)) must not be defined
        extraction.right.value.get(TelephoneNumberPage(0)) must not be defined
        extraction.right.value.get(EmailPage(0)) must not be defined
        extraction.right.value.get(TrusteeMetaData(0)).get mustBe MetaData("1", Some("01"), "2019-11-26")
        extraction.right.value.get(TrusteesSafeIdPage(0)) must be(defined)
      }

      "which has no utr and no address, return user answers updated" in {

        val trustee = DisplayTrustTrusteeOrgType(
          lineNo = s"1",
          bpMatchStatus = Some("01"),
          name = "org1",
          phoneNumber = None,
          email = None,
          identification =
            Some(DisplayTrustIdentificationOrgType(
              safeId = Some("8947584-94759745-84758745"),
              utr = None,
              address = None
            )),
          entityStart = "2019-11-26"
        )

        val ua = UserAnswers("fakeId")

        val extraction = trusteeOrgExtractor.extract(ua, Some(trustee))

        extraction.right.value.get(IsThisLeadTrusteePage(0)).get mustBe false
        extraction.right.value.get(TrusteeIndividualOrBusinessPage(0)).get mustBe IndividualOrBusiness.Business
        extraction.right.value.get(TrusteeOrgNamePage(0)).get mustBe "org1"
        extraction.right.value.get(TrusteesUtrPage(0)) must not be defined
        extraction.right.value.get(TrusteeUTRYesNoPagePage(0)).get mustBe false
        extraction.right.value.get(TrusteeAddressYesNoPage(0)).get mustBe false
        extraction.right.value.get(TrusteeAddressInTheUKPage(0)) must not be defined
        extraction.right.value.get(TrusteesUkAddressPage(0)) must not be defined
        extraction.right.value.get(TrusteesInternationalAddressPage(0)) must not be defined
        extraction.right.value.get(TelephoneNumberPage(0)) must not be defined
        extraction.right.value.get(EmailPage(0)) must not be defined
        extraction.right.value.get(TrusteeMetaData(0)).get mustBe MetaData("1", Some("01"), "2019-11-26")
        extraction.right.value.get(TrusteesSafeIdPage(0)) must be(defined)
      }

      "which is UK registered, return user answers updated" in {

        val trustee = DisplayTrustTrusteeOrgType(
          lineNo = s"1",
          bpMatchStatus = Some("01"),
          name = "org1",
          phoneNumber = Some("+441234567890"),
          email = Some("test@test.com"),
          identification =
            Some(DisplayTrustIdentificationOrgType(
              safeId = Some("8947584-94759745-84758745"),
              utr = Some("1234567890"),
              address = Some(AddressType("line 1", "line2", None, None, Some("NE11NE"), "GB"))
            )),
          entityStart = "2019-11-26"
        )

        val ua = UserAnswers("fakeId")

        val extraction = trusteeOrgExtractor.extract(ua, Some(trustee))

        extraction.right.value.get(IsThisLeadTrusteePage(0)).get mustBe false
        extraction.right.value.get(TrusteeIndividualOrBusinessPage(0)).get mustBe IndividualOrBusiness.Business
        extraction.right.value.get(TrusteeOrgNamePage(0)).get mustBe "org1"
        extraction.right.value.get(TrusteeUTRYesNoPagePage(0)).get mustBe true
        extraction.right.value.get(TrusteesUtrPage(0)).get mustBe "1234567890"
        extraction.right.value.get(TrusteeAddressInTheUKPage(0)).get mustBe true
        extraction.right.value.get(TrusteesUkAddressPage(0)) must be(defined)
        extraction.right.value.get(TrusteesUkAddressPage(0)).get.postcode mustBe "NE11NE"
        extraction.right.value.get(TrusteesInternationalAddressPage(0)) mustNot be(defined)
        extraction.right.value.get(TelephoneNumberPage(0)).get mustBe "+441234567890"
        extraction.right.value.get(EmailPage(0)).get mustBe "test@test.com"
        extraction.right.value.get(LeadTrusteeMetaData(0)).get mustBe MetaData("1", Some("01"), "2019-11-26")
        extraction.right.value.get(TrusteesSafeIdPage(0)) must be(defined)
      }

      "which is not UK registered, return user answers updated" in {

        val trustee = DisplayTrustTrusteeOrgType(
          lineNo = s"1",
          bpMatchStatus = Some("01"),
          name = "org1",
          phoneNumber = Some("+441234567890"),
          email = Some("test@test.com"),
          identification =
            Some(DisplayTrustIdentificationOrgType(
              safeId = Some("8947584-94759745-84758745"),
              utr = None,
              address = Some(AddressType("line 1", "line2", None, None, None, "FR"))
            )),
          entityStart = "2019-11-26"
        )

        val ua = UserAnswers("fakeId")

        val extraction = trusteeOrgExtractor.extract(ua, Some(trustee))

        extraction.right.value.get(IsThisLeadTrusteePage(0)).get mustBe false
        extraction.right.value.get(TrusteeIndividualOrBusinessPage(0)).get mustBe IndividualOrBusiness.Business
        extraction.right.value.get(TrusteeOrgNamePage(0)).get mustBe "org1"
        extraction.right.value.get(TrusteeUTRYesNoPagePage(0)).get mustBe false
        extraction.right.value.get(TrusteesUtrPage(0)) mustNot be(defined)
        extraction.right.value.get(TrusteeAddressInTheUKPage(0)).get mustBe false
        extraction.right.value.get(TrusteesUkAddressPage(0)) must not be defined
        extraction.right.value.get(TrusteesInternationalAddressPage(0)) mustBe defined
        extraction.right.value.get(TelephoneNumberPage(0)).get mustBe "+441234567890"
        extraction.right.value.get(EmailPage(0)).get mustBe "test@test.com"
        extraction.right.value.get(LeadTrusteeMetaData(0)).get mustBe MetaData("1", Some("01"), "2019-11-26")
        extraction.right.value.get(TrusteesSafeIdPage(0)) must be(defined)
      }

    }

  }

}
