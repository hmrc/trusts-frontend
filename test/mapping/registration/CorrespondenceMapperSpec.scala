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

import base.SpecBaseHelpers
import generators.Generators
import models.core.pages.{InternationalAddress, UKAddress}
import org.scalatest.{FreeSpec, MustMatchers, OptionValues}
import pages.register.trust_details.TrustNamePage

class CorrespondenceMapperSpec extends FreeSpec with MustMatchers
  with OptionValues with Generators with SpecBaseHelpers {

  private val correspondenceMapper: CorrespondenceMapper = injector.instanceOf[CorrespondenceMapper]

  private val addressMapper: AddressMapper = injector.instanceOf[AddressMapper]
  private val leadTrusteeUkAddress = addressMapper.build(UKAddress("First line", "Second line", None, Some("Newcastle"), "NE981ZZ"))
  private val leadTrusteeInternationalAddress = addressMapper.build(InternationalAddress("First line", "Second line", None, "DE"))
  private val leadTrusteePhoneNumber = "0191 222222"

  "CorrespondenceMapper" - {

    "when user answers is empty" - {

      "must not be able to create Correspondence" in {
        val userAnswers = emptyUserAnswers

        correspondenceMapper.build(userAnswers, leadTrusteeUkAddress, leadTrusteePhoneNumber) mustNot be(defined)
      }

    }

    "when user answers is not empty" - {

      "must not be able to create a correspondence when do not have all answers" in {
        correspondenceMapper.build(emptyUserAnswers, leadTrusteeUkAddress, leadTrusteePhoneNumber) mustNot be(defined)
      }

      "must be able to create a correspondence for a UK lead trustee" - {
        val userAnswers = emptyUserAnswers
          .set(TrustNamePage, "Trust of a Will").success.value

        correspondenceMapper.build(userAnswers, leadTrusteeUkAddress, leadTrusteePhoneNumber).value mustBe Correspondence(
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

      "must be able to create a correspondence when have all required answers" in {
        val address = InternationalAddress("First line", "Second line", None, "DE")

        val userAnswers = emptyUserAnswers
          .set(TrustNamePage, "Trust of a Will").success.value

        correspondenceMapper.build(userAnswers, leadTrusteeInternationalAddress, leadTrusteePhoneNumber).value mustBe Correspondence(
          abroadIndicator = true,
          name = "Trust of a Will",
          address = AddressType(
            line1 = "First line",
            line2 = "Second line",
            line3 = None,
            line4 = None,
            postCode = None,
            country = "DE"
          ),
          phoneNumber = "0191 222222"
        )
      }
    }
  }
}
