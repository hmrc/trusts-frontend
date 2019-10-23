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
import models.IndividualOrBusiness.Individual
import models.{FullName, UKAddress}
import org.scalatest.{FreeSpec, MustMatchers, OptionValues}
import pages._
import pages.trustees._

class CorrespondenceMapperSpec extends FreeSpec with MustMatchers
  with OptionValues with Generators with SpecBaseHelpers {

  private val correspondenceMapper: Mapping[Correspondence] = injector.instanceOf[CorrespondenceMapper]

  "CorrespondenceMapper" - {

    "when user answers is empty" - {

      "must not be able to create Correspondence" in {
        val userAnswers = emptyUserAnswers

        correspondenceMapper.build(userAnswers) mustNot be(defined)
      }

    }

    "when user answers is not empty" - {

      "for a UK lead trustee individual" - {

        "must not be able to create a correspondence when do not have all answers" in {
          val address = UKAddress("First line", "Second line", None, None, "NE981ZZ")

          val userAnswers = emptyUserAnswers
            .set(TrustNamePage, "Trust of a Will").success.value
            .set(IsThisLeadTrusteePage(0), true).success.value
            .set(TrusteeIndividualOrBusinessPage(0), Individual).success.value
            .set(TrusteesNamePage(0), FullName("First", None, "Last")).success.value
            .set(TrusteesUkAddressPage(0), address).success.value
            .set(TelephoneNumberPage(0), "0191 222222").success.value

          correspondenceMapper.build(userAnswers) mustNot be(defined)
        }

        "must be able to create a correspondence when have all required answers" in {
          val address = UKAddress("First line", "Second line", None, None, "NE981ZZ")

          val userAnswers = emptyUserAnswers
            .set(TrustNamePage, "Trust of a Will").success.value
            .set(IsThisLeadTrusteePage(0), true).success.value
            .set(TrusteeIndividualOrBusinessPage(0), Individual).success.value
            .set(TrusteesNamePage(0), FullName("First", None, "Last")).success.value
            .set(TrusteesDateOfBirthPage(0), LocalDate.of(2010, 10, 10)).success.value
            .set(TrusteeAUKCitizenPage(0), true).success.value
            .set(TrusteeLiveInTheUKPage(0), true).success.value
            .set(TrusteesUkAddressPage(0), address).success.value
            .set(TelephoneNumberPage(0), "0191 222222").success.value

          correspondenceMapper.build(userAnswers).value mustBe Correspondence(
            abroadIndicator = false,
            name = "Trust of a Will",
            address = AddressType(
              line1 = "First line",
              line2 = "Second line",
              line3 = None,
              line4 = Some("Newcastle"),
              postCode = Some("NE981ZZ"),
              country = "GB"
            ),
            phoneNumber = "0191 222222"
          )
        }

      }

      "must not be able to create a correspondence for a UK lead trustee business" in {
        val userAnswers = emptyUserAnswers

        correspondenceMapper.build(userAnswers) mustNot be(defined)
      }

      "must not be able to create a correspondence for a Non-UK lead trustee individual" in {
        val userAnswers = emptyUserAnswers

        correspondenceMapper.build(userAnswers) mustNot be(defined)
      }

      "must not be able to create a correspondence for a Non-UK lead trustee business" in {
        val userAnswers = emptyUserAnswers

        correspondenceMapper.build(userAnswers) mustNot be(defined)
      }

    }

  }

}
