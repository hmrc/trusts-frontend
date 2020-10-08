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

  private lazy val intervivosUserAnswers = {
    val emptyUserAnswers = TestUserAnswers.emptyUserAnswers
    val asset = TestUserAnswers.withMoneyAsset(emptyUserAnswers)
    val intervivosSettlementTrust = TestUserAnswers.withInterVivosTrust(asset)
    val userAnswers = TestUserAnswers.withDeclaration(intervivosSettlementTrust)
    userAnswers
  }

  private lazy val heritageUserAnswers = {
    val emptyUserAnswers = TestUserAnswers.emptyUserAnswers
    val asset = TestUserAnswers.withMoneyAsset(emptyUserAnswers)
    val heritageSettlementTrust = TestUserAnswers.withHeritageTrust(asset)
    val userAnswers = TestUserAnswers.withDeclaration(heritageSettlementTrust)

    userAnswers
  }

  private lazy val flatManagementUserAnswers = {
    val emptyUserAnswers = TestUserAnswers.emptyUserAnswers
    val asset = TestUserAnswers.withMoneyAsset(emptyUserAnswers)
    val flatManagementTrust = TestUserAnswers.withFlatManagementTrust(asset)
    val userAnswers = TestUserAnswers.withDeclaration(flatManagementTrust)

    userAnswers
  }

  private def employmentRelatedUserAnswers(efrbsStartDate: LocalDate) = {
    val emptyUserAnswers = TestUserAnswers.emptyUserAnswers
    val asset = TestUserAnswers.withMoneyAsset(emptyUserAnswers)
    val flatManagementTrust = TestUserAnswers.withEmploymentRelatedTrust(asset, efrbsStartDate)
    val userAnswers = TestUserAnswers.withDeclaration(flatManagementTrust)

    userAnswers
  }

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


    "when user answers is not empty" - {


      "must generate an Intervivos trust" in {
        val userAnswers = intervivosUserAnswers

        val result = registrationMapper.build(userAnswers, correspondenceAddress, trustName).value

        result.agentDetails mustNot be(defined)
        result.matchData mustNot be(defined)
        result.declaration mustBe a[Declaration]

        result.trust.details.typeOfTrust mustBe IntervivosSettlementTrust
        result.trust.details.deedOfVariation mustNot be(defined)
        result.trust.details.interVivos mustBe Some(true)
        result.trust.details.efrbsStartDate mustNot be(defined)

        result.trust.entities.deceased mustNot be(defined)
        result.trust.entities.settlors.value.settlor.value mustNot be(empty)
        result.trust.entities.settlors.value.settlorCompany mustNot be(defined)
      }

      "must generate a Heritage trust for repair of historic buildings " in {
        val userAnswers = heritageUserAnswers

        val result = registrationMapper.build(userAnswers, correspondenceAddress, trustName).value

        result.agentDetails mustNot be(defined)
        result.matchData mustNot be(defined)
        result.declaration mustBe a[Declaration]

        result.trust.details.typeOfTrust mustBe HeritageTrust
        result.trust.details.deedOfVariation mustNot be(defined)
        result.trust.details.interVivos mustNot be(defined)
        result.trust.details.efrbsStartDate mustNot be(defined)

        result.trust.entities.deceased mustNot be(defined)
        result.trust.entities.settlors.value.settlor.value mustNot be(empty)
        result.trust.entities.settlors.value.settlorCompany mustNot be(defined)
      }


      "must generate a Flat management trust for a building or building with tenants " in {
        val userAnswers = flatManagementUserAnswers

        val result = registrationMapper.build(userAnswers, correspondenceAddress, trustName).value

        result.agentDetails mustNot be(defined)
        result.matchData mustNot be(defined)
        result.declaration mustBe a[Declaration]

        result.trust.details.typeOfTrust mustBe FlatManagementTrust
        result.trust.details.deedOfVariation mustNot be(defined)
        result.trust.details.interVivos mustNot be(defined)
        result.trust.details.efrbsStartDate mustNot be(defined)

        result.trust.entities.deceased mustNot be(defined)
        result.trust.entities.settlors.value.settlor.value mustNot be(empty)
        result.trust.entities.settlors.value.settlorCompany mustNot be(defined)
      }

      "must generate an Employment related trust for an Employer Financed RBS " in {
        val date = LocalDate.of(2010, 10, 10)

        val userAnswers = employmentRelatedUserAnswers(date)

        val result = registrationMapper.build(userAnswers, correspondenceAddress, trustName).value

        result.agentDetails mustNot be(defined)
        result.matchData mustNot be(defined)
        result.declaration mustBe a[Declaration]

        result.trust.details.typeOfTrust mustBe EmployeeRelated
        result.trust.details.deedOfVariation mustNot be(defined)
        result.trust.details.interVivos mustNot be(defined)
        result.trust.details.efrbsStartDate mustBe Some(date)

        result.trust.entities.deceased mustNot be(defined)
        result.trust.entities.settlors.value.settlor.value mustNot be(empty)
        result.trust.entities.settlors.value.settlorCompany mustNot be(defined)
      }
    }
  }
}
