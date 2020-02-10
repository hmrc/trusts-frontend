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
import mapping.Mapping
import models.core.pages.{FullName, IndividualOrBusiness, UKAddress}
import org.scalatest.{FreeSpec, MustMatchers, OptionValues}
import pages.register.trustees._
import pages.register.trustees.organisation.{TrusteeOrgAddressUkPage, TrusteeOrgAddressUkYesNoPage, TrusteeOrgNamePage, TrusteeUtrYesNoPage, TrusteesUtrPage}


class LeadTrusteeMapperSpec extends FreeSpec with MustMatchers
  with OptionValues with Generators with SpecBaseHelpers {

  val leadTrusteeMapper: Mapping[LeadTrusteeType] = injector.instanceOf[LeadTrusteeMapper]

  "LeadTrusteeMapper" - {

    "when user answers is empty" - {
      "must not be able to create LeadTrusteeType" in {
        val userAnswers = emptyUserAnswers
        leadTrusteeMapper.build(userAnswers) mustNot be(defined)
      }
    }

    "when user answers is not empty " - {
      "must be able to create LeadTrusteeType with lead trustee individual" in {

        val index = 0
        val userAnswers = emptyUserAnswers
          .set(IsThisLeadTrusteePage(index), true).success.value
          .set(TrusteeIndividualOrBusinessPage(index), IndividualOrBusiness.Individual).success.value
          .set(TrusteesNamePage(index), FullName("first name",  Some("middle name"), "Last Name")).success.value
          .set(TrusteesDateOfBirthPage(index), LocalDate.of(1500,10,10)).success.value
          .set(TrusteeAUKCitizenPage(index), true).success.value
          .set(TrusteeAddressInTheUKPage(index), true).success.value
          .set(TrusteesNinoPage(index), "AB123456C").success.value
          .set(TelephoneNumberPage(index), "0191 1111111").success.value
          .set(TrusteesUkAddressPage(index), UKAddress("line1", "line2" ,None, None, "NE65QA")).success.value


        leadTrusteeMapper.build(userAnswers).value mustBe  LeadTrusteeType(
          leadTrusteeInd = Some(LeadTrusteeIndType(NameType("first name", Some("middle name"), "Last Name"),
            dateOfBirth = LocalDate.of(1500,10,10),
            phoneNumber = "0191 1111111",
            email = None,
            identification = IdentificationType(
              nino = Some("AB123456C"),
              None,
              None
            )
          ))
        )
      }

      "must be able to create LeadTrusteeType with lead trustee organisation" in {

        val index = 0
        val userAnswers = emptyUserAnswers
          .set(IsThisLeadTrusteePage(index), true).success.value
          .set(TrusteeIndividualOrBusinessPage(index), IndividualOrBusiness.Business).success.value
          .set(TrusteeUtrYesNoPage(index), true).success.value
          .set(TrusteeOrgNamePage(index), "Org Name").success.value
          .set(TrusteesUtrPage(index), "1234567890").success.value
          .set(TrusteeOrgAddressUkYesNoPage(index), true).success.value
          .set(TrusteeOrgAddressUkPage(index), UKAddress("line1", "line2" ,None, None, "NE65QA")).success.value
          .set(TelephoneNumberPage(index), "0191 1111111").success.value

        leadTrusteeMapper.build(userAnswers).value mustBe LeadTrusteeType(
          leadTrusteeOrg = Some(LeadTrusteeOrgType("Org Name",
            phoneNumber = "0191 1111111",
            email = None,
            identification = IdentificationOrgType(
              utr = Some("1234567890"),
              None
            )
          ))
        )
      }

      "must not able to create LeadTrusteeType with only trustee individual which is not lead." in {
        val index = 0
        val userAnswers = emptyUserAnswers
          .set(IsThisLeadTrusteePage(index), false).success.value
          .set(TrusteeIndividualOrBusinessPage(index), IndividualOrBusiness.Individual).success.value
          .set(TrusteesNamePage(index), FullName("first name",  Some("middle name"), "Last Name")).success.value
          .set(TrusteesDateOfBirthPage(index), LocalDate.of(1500,10,10)).success.value

        leadTrusteeMapper.build(userAnswers) mustNot be(defined)
      }

      "must not able to create LeadTrusteeType with only trustee organisation which is not lead." in {
        val index = 0
        val userAnswers = emptyUserAnswers
          .set(IsThisLeadTrusteePage(index), false).success.value
          .set(TrusteeIndividualOrBusinessPage(index), IndividualOrBusiness.Business).success.value
          .set(TrusteeOrgNamePage(index), "Org Name").success.value

        leadTrusteeMapper.build(userAnswers) mustNot be(defined)
      }

      "must be able to create LeadTrusteeType without telephone number for trustee individual" in {

        val index = 0
        val userAnswers = emptyUserAnswers
          .set(IsThisLeadTrusteePage(index), true).success.value
          .set(TrusteeIndividualOrBusinessPage(index), IndividualOrBusiness.Individual).success.value
          .set(TrusteesNamePage(index), FullName("first name",  Some("middle name"), "Last Name")).success.value
          .set(TrusteesDateOfBirthPage(index), LocalDate.of(1500,10,10)).success.value
          .set(TrusteeAUKCitizenPage(index), true).success.value
          .set(TrusteeAddressInTheUKPage(index), true).success.value
          .set(TrusteesNinoPage(index), "AB123456C").success.value
          .set(TrusteesUkAddressPage(index), UKAddress("line1", "line2",None, Some("line4"), "NE65QA")).success.value

         leadTrusteeMapper.build(userAnswers) mustNot be(defined)

      }

      "must be able to create LeadTrusteeType without telephone number for trustee organisation" in {

        val index = 0
        val userAnswers = emptyUserAnswers
          .set(IsThisLeadTrusteePage(index), true).success.value
          .set(TrusteeIndividualOrBusinessPage(index), IndividualOrBusiness.Business).success.value
          .set(TrusteeUtrYesNoPage(index), true).success.value
          .set(TrusteeOrgNamePage(index), "Org Name").success.value
          .set(TrusteesUtrPage(index), "1234567890").success.value
          .set(TrusteeOrgAddressUkYesNoPage(index), true).success.value
          .set(TrusteeOrgAddressUkPage(index), UKAddress("line1", "line2" ,None, None, "NE65QA")).success.value

        leadTrusteeMapper.build(userAnswers) mustNot be(defined)

      }

    }
  }

}
