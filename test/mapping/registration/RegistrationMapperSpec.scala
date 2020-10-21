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
import mapping.TypeOfTrust.{EmployeeRelated, FlatManagementTrust, HeritageTrust, IntervivosSettlementTrust, WillTrustOrIntestacyTrust}
import org.scalatest.{FreeSpec, MustMatchers, OptionValues}
import uk.gov.hmrc.http.HeaderCarrier
import utils.TestUserAnswers

class RegistrationMapperSpec extends FreeSpec with MustMatchers
  with OptionValues with Generators with SpecBaseHelpers {

  private val correspondenceAddress = AddressType("line1", "line2", None, None, Some("AA1 1AA"), "GB")

  lazy val registrationMapper: RegistrationMapper = injector.instanceOf[RegistrationMapper]

  val trustName = "Trust Name"
  implicit val hc: HeaderCarrier = HeaderCarrier()

  "RegistrationMapper" - {

    "when user answers is empty" - {

      "must not be able to create Registration" in {

        val userAnswers = TestUserAnswers.emptyUserAnswers

        registrationMapper.build(userAnswers, correspondenceAddress, trustName) mustNot be(defined)
      }
    }

  }
}
