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

package viewmodels

import java.time.LocalDate

import generators.{Generators, ModelGenerators}
import models.core.pages.{FullName, IndividualOrBusiness}
import models.registration.pages.Status.Completed
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatest.prop.PropertyChecks
import org.scalatest.{FreeSpec, MustMatchers}
import play.api.libs.json.{JsSuccess, Json}
import viewmodels.addAnother.TrusteeViewModel

class TrusteeViewModelSpec extends FreeSpec with MustMatchers with PropertyChecks with Generators with ModelGenerators {

  "Trustee" - {

    "must deserialise" - {

      "from a trustee individual to a view model" in {

        forAll(arbitrary[LocalDate], arbitrary[FullName], Gen.const(IndividualOrBusiness.Individual)) {
          (date, fullName, individual) =>

            val json = Json.obj(
              "name" -> fullName,
              "dateOfBirth" -> date,
              "isThisLeadTrustee" -> false,
              "individualOrBusiness" -> individual.toString,
              "status" -> Completed.toString
            )

            json.validate[TrusteeViewModel] mustEqual JsSuccess(
              addAnother.TrusteeViewModel(isLead = false, Some(fullName.toString), Some(individual), Completed)
            )
        }
      }
      "from a trustee individual with no name to a view model" in {

        forAll(arbitrary[LocalDate], Gen.const(IndividualOrBusiness.Individual)) {
          (date, individual) =>

            val json = Json.obj(
              "dateOfBirth" -> date,
              "isThisLeadTrustee" -> false,
              "individualOrBusiness" -> individual.toString,
              "status" -> Completed.toString
            )

            json.validate[TrusteeViewModel] mustEqual JsSuccess(
              addAnother.TrusteeViewModel(isLead = false, None, Some(individual), Completed)
            )
        }
      }
      "from a trustee business to a view model" in {

        forAll(arbitrary[LocalDate], arbitrary[String], Gen.const(IndividualOrBusiness.Business)) {
          (date, businessName, business) =>

            val json = Json.obj(
              "name" -> businessName,
              "dateOfBirth" -> date,
              "isThisLeadTrustee" -> false,
              "individualOrBusiness" -> business.toString,
              "status" -> Completed.toString
            )

            json.validate[TrusteeViewModel] mustEqual JsSuccess(
              addAnother.TrusteeViewModel(isLead = false, Some(businessName), Some(business), Completed)
            )
        }

      }
      "from a trustee business with no name to a view model" in {

        forAll(arbitrary[LocalDate], Gen.const(IndividualOrBusiness.Business)) {
          (date, business) =>

            val json = Json.obj(
              "dateOfBirth" -> date,
              "isThisLeadTrustee" -> false,
              "individualOrBusiness" -> business.toString,
              "status" -> Completed.toString
            )

            json.validate[TrusteeViewModel] mustEqual JsSuccess(
              addAnother.TrusteeViewModel(isLead = false, None, Some(business), Completed)
            )
        }

      }
    }
  }
}
