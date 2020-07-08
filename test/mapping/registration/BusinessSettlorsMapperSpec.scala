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

package mapping.registration

import java.time.LocalDate

import base.SpecBaseHelpers
import generators.Generators
import mapping._
import models.core.pages.{IndividualOrBusiness, InternationalAddress, UKAddress}
import models.registration.pages.{KindOfBusiness, KindOfTrust}
import org.scalatest.{FreeSpec, MustMatchers, OptionValues}
import pages.register.settlors.living_settlor._
import pages.register.settlors.living_settlor.business._
import pages.register.settlors.living_settlor.trust_type.KindOfTrustPage

class BusinessSettlorsMapperSpec extends FreeSpec with MustMatchers
  with OptionValues with Generators with SpecBaseHelpers  {

  val businessSettlorsMapper: Mapping[List[SettlorCompany]] = injector.instanceOf[BusinessSettlorsMapper]

  "BusinessSettlorsMapper" - {

    "when user answers is empty" - {

      "must not be able to create a Settlor" in {

        val userAnswers = emptyUserAnswers

        businessSettlorsMapper.build(userAnswers) mustNot be(defined)
      }
    }

    "when user answers is not empty " - {

      "must be able to create a Settlor with minimal data journey" in {

        val userAnswers =
          emptyUserAnswers
            .set(SettlorIndividualOrBusinessPage(0), IndividualOrBusiness.Business).success.value
            .set(SettlorBusinessNamePage(0), "Business name").success.value
            .set(SettlorBusinessUtrYesNoPage(0), true).success.value
            .set(SettlorBusinessUtrPage(0), "1234567890").success.value


          businessSettlorsMapper.build(userAnswers).value mustBe List(SettlorCompany(
            name = "Business name",
            companyType = None,
            companyTime = None,
            identification = Some(IdentificationOrgType(Some("1234567890"), None))
          )
        )
      }

      "must be able to create a Settlor with a UK address" in {

        val userAnswers =
          emptyUserAnswers
            .set(SettlorIndividualOrBusinessPage(0), IndividualOrBusiness.Business).success.value
            .set(SettlorBusinessNamePage(0), "Business name").success.value
            .set(SettlorBusinessUtrYesNoPage(0), false).success.value
            .set(SettlorBusinessAddressYesNoPage(0), true).success.value
            .set(SettlorBusinessAddressUKYesNoPage(0), true).success.value
            .set(SettlorBusinessAddressUKPage(0), UKAddress("line1", "line2", Some("line3"), Some("Newcastle"), "ab1 1ab")).success.value

          businessSettlorsMapper.build(userAnswers).value mustBe List(SettlorCompany(
            name = "Business name",
            companyType = None,
            companyTime = None,
            identification = Some(IdentificationOrgType(None, Some(AddressType("line1", "line2", Some("line3"), Some("Newcastle"), Some("ab1 1ab"), "GB"))))
          )
        )
      }
    }

      "must be able to create a Settlor with an International address " in {

        val userAnswers =
          emptyUserAnswers
            .set(SettlorIndividualOrBusinessPage(0), IndividualOrBusiness.Business).success.value
            .set(SettlorBusinessNamePage(0), "Business name").success.value
            .set(SettlorBusinessUtrYesNoPage(0), false).success.value
            .set(SettlorBusinessAddressYesNoPage(0), true).success.value
            .set(SettlorBusinessAddressUKYesNoPage(0), false).success.value
            .set(SettlorBusinessAddressInternationalPage(0), InternationalAddress("line1", "line2", Some("line3"), "FR")).success.value

          businessSettlorsMapper.build(userAnswers).value mustBe List(SettlorCompany(
            name = "Business name",
            companyType = None,
            companyTime = None,
            identification = Some(IdentificationOrgType(None, Some(AddressType("line1", "line2", Some("line3"), None, None, "FR"))))
          )
        )
      }

    "must be able to create a Settlor with company time and type" in {

      val userAnswers =
        emptyUserAnswers
          .set(KindOfTrustPage, KindOfTrust.Employees).success.value
          .set(SettlorIndividualOrBusinessPage(0), IndividualOrBusiness.Business).success.value
          .set(SettlorBusinessNamePage(0), "Business name").success.value
          .set(SettlorBusinessUtrYesNoPage(0), true).success.value
          .set(SettlorBusinessUtrPage(0), "1234567890").success.value
          .set(SettlorBusinessTypePage(0), KindOfBusiness.Investment).success.value
          .set(SettlorBusinessTimeYesNoPage(0), true).success.value


      businessSettlorsMapper.build(userAnswers).value mustBe List(SettlorCompany(
        name = "Business name",
        companyType = Some("Investment"),
        companyTime = Some(true),
        identification = Some(IdentificationOrgType(Some("1234567890"), None))
      )
      )
    }

    "must be able to create multiple Settlors" in {

      val userAnswers =
        emptyUserAnswers
          .set(SettlorIndividualOrBusinessPage(0), IndividualOrBusiness.Business).success.value
          .set(SettlorBusinessNamePage(0), "Business name").success.value
          .set(SettlorBusinessUtrYesNoPage(0), true).success.value
          .set(SettlorBusinessUtrPage(0), "1234567890").success.value
          .set(SettlorIndividualOrBusinessPage(1), IndividualOrBusiness.Business).success.value
          .set(SettlorBusinessNamePage(1), "Another business").success.value
          .set(SettlorBusinessUtrYesNoPage(1), true).success.value
          .set(SettlorBusinessUtrPage(1), "0987654321").success.value

      businessSettlorsMapper.build(userAnswers).value mustBe List(
        SettlorCompany(
          name = "Business name",
          companyType = None,
          companyTime = None,
          identification = Some(IdentificationOrgType(Some("1234567890"), None))
        ),
        SettlorCompany(
          name = "Another business",
          companyType = None,
          companyTime = None,
          identification = Some(IdentificationOrgType(Some("0987654321"), None))
        )
      )
    }
  }
}
