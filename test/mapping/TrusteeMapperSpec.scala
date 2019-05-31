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

package mapping

import java.time.LocalDate

import base.SpecBaseHelpers
import generators.Generators
import models.{FullName, IndividualOrBusiness, UKAddress}
import org.scalatest.{FreeSpec, MustMatchers, OptionValues}
import pages._

class TrusteeMapperSpec extends FreeSpec with MustMatchers
  with OptionValues with Generators with SpecBaseHelpers {

  val trusteeMapper: Mapping[List[TrusteeType]] = injector.instanceOf[TrusteeMapper]

  "TrusteeMapper" - {

    "when user answers is empty" - {

      "must not be able to create TrusteeType" in {
        val userAnswers = emptyUserAnswers
        trusteeMapper.build(userAnswers) mustNot be(defined)
      }
    }

    "when user answers is not empty" - {

      "must be able to create a Trustee Individual with minimum data" in {
        val index = 0
        val userAnswers =
          emptyUserAnswers
            .set(IsThisLeadTrusteePage(index), false).success.value
            .set(TrusteeIndividualOrBusinessPage(index), IndividualOrBusiness.Individual).success.value
            .set(TrusteesNamePage(index), FullName("first name", None, "last name")).success.value
            .set(TrusteesDateOfBirthPage(index), LocalDate.of(2010, 10, 10)).success.value

        trusteeMapper.build(userAnswers).value.head mustBe TrusteeType(
          trusteeInd = Some(TrusteeIndividualType(
            name = NameType("first name", None, "last name"),
            dateOfBirth = Some(LocalDate.of(2010, 10, 10)),
            phoneNumber = None,
            identification = None)
          ),
          None
        )
      }

      "must be able to list of  a Trustee Individual with minimum data" in {
        val index0 = 0
        val index1 = 1

        val userAnswers =
          emptyUserAnswers
            .set(IsThisLeadTrusteePage(index0), false).success.value
            .set(TrusteeIndividualOrBusinessPage(index0), IndividualOrBusiness.Individual).success.value
            .set(TrusteesNamePage(index0), FullName("first name", None, "last name")).success.value
            .set(TrusteesDateOfBirthPage(index0), LocalDate.of(2010, 10, 10)).success.value
            .set(IsThisLeadTrusteePage(index1), false).success.value
            .set(TrusteeIndividualOrBusinessPage(index1), IndividualOrBusiness.Individual).success.value
            .set(TrusteesNamePage(index1), FullName("second name", None, "second name")).success.value
            .set(TrusteesDateOfBirthPage(index1), LocalDate.of(2015, 10, 10)).success.value

        trusteeMapper.build(userAnswers).value mustBe List(TrusteeType(
          trusteeInd = Some(TrusteeIndividualType(
            name = NameType("first name", None, "last name"),
            dateOfBirth = Some(LocalDate.of(2010, 10, 10)),
            phoneNumber = None,
            identification = None)
          ),
          None
        ),
          TrusteeType(
            trusteeInd = Some(TrusteeIndividualType(
              name = NameType("second name", None, "second name"),
              dateOfBirth = Some(LocalDate.of(2015, 10, 10)),
              phoneNumber = None,
              identification = None)
            ),
            None
          ))
      }

      "must not able to create a Trustee Individual when there is only LeadTrustee" in {
        val index = 0
        val userAnswers = emptyUserAnswers
          .set(IsThisLeadTrusteePage(index), true).success.value
          .set(TrusteeIndividualOrBusinessPage(index), IndividualOrBusiness.Individual).success.value
          .set(TrusteesNamePage(index), FullName("first name",  Some("middle name"), "Last Name")).success.value
          .set(TrusteesDateOfBirthPage(index), LocalDate.of(1500,10,10)).success.value
          .set(TrusteeAUKCitizenPage(index), true).success.value
          .set(TrusteeLiveInTheUKPage(index), true).success.value
          .set(TrusteesNinoPage(index), "AB123456C").success.value
          .set(TelephoneNumberPage(index), "0191 1111111").success.value
          .set(TrusteesUkAddressPage(index), UKAddress("line1", None,None, "town", "NE65QA")).success.value

        trusteeMapper.build(userAnswers) mustNot be(defined)

      }
    }
  }

}
