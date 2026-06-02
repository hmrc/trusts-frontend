/*
 * Copyright 2026 HM Revenue & Customs
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

package services.settlors

import base.RegistrationSpecBase
import models.settlor.{IncompleteSettlorData, InvalidSettlorData, MissingSettlorData}
import play.api.libs.json._

class RegistrationSettlorValidationServiceSpec extends RegistrationSpecBase {

  val service = new RegistrationSettlorValidationService()

  private def deceasedSettlorJson(name: JsObject) = Json.obj("trust/entities/deceased" -> Json.obj("name" -> name))

  private def individualSettlorsJson(settlors: JsArray) =
    Json.obj("trust/entities/settlors" -> Json.obj("settlor" -> settlors))

  private def companySettlorsJson(companies: JsArray) =
    Json.obj("trust/entities/settlors" -> Json.obj("settlorCompany" -> companies))

  private def mixedSettlorsJson(settlors: JsArray, companies: JsArray) =
    Json.obj("trust/entities/settlors" -> Json.obj("settlor" -> settlors, "settlorCompany" -> companies))

  private val fullName      = Json.obj("firstName" -> "John", "lastName" -> "Smith")
  private val firstNameOnly = Json.obj("firstName" -> "John")
  private val validCompany  = Json.obj("name" -> "Test Company Ltd")
  private val emptyObject   = Json.obj()

  "RegistrationSettlorValidationService.validate" must {

    "return no validation errors when all required fields are present for deceased settlor" in {
      val result = service.validate(deceasedSettlorJson(fullName))
      result mustBe List.empty
    }

    "return validation errors when deceased settlor last name missing" in {
      val result = service.validate(deceasedSettlorJson(firstNameOnly))
      result mustEqual List(IncompleteSettlorData("registration: deceased.name.lastName missing"))
    }

    "return no validation errors when all required fields are present for individual settlor" in {
      val result = service.validate(
        individualSettlorsJson(Json.arr(Json.obj("name" -> fullName)))
      )
      result mustBe List.empty
    }

    "return validation errors when individual settlor has last name missing" in {
      val result = service.validate(
        individualSettlorsJson(Json.arr(Json.obj("name" -> firstNameOnly)))
      )
      result mustEqual List(IncompleteSettlorData("registration: individualSettlor[0].name.lastName missing"))
    }

    "return validation errors for multiple individual settlors" in {
      val settlors = Json.arr(Json.obj("name" -> fullName), Json.obj("name" -> firstNameOnly))
      val result   = service.validate(individualSettlorsJson(settlors))
      result mustEqual List(IncompleteSettlorData("registration: individualSettlor[1].name.lastName missing"))
    }

    "return no validation errors when all required fields are present for company settlor" in {
      val result = service.validate(companySettlorsJson(Json.arr(validCompany)))
      result mustBe List.empty
    }

    "return validation errors when company settlor name is missing" in {
      val result = service.validate(companySettlorsJson(Json.arr(emptyObject)))
      result mustEqual List(IncompleteSettlorData("registration: companySettlor[0].name missing"))
    }

    "return validation errors for multiple company settlors" in {
      val companies = Json.arr(validCompany, emptyObject)
      val result    = service.validate(companySettlorsJson(companies))
      result mustEqual List(IncompleteSettlorData("registration: companySettlor[1].name missing"))
    }

    "return no validation errors when both individual and company settlors are present and valid" in {
      val result = service.validate(
        mixedSettlorsJson(Json.arr(Json.obj("name" -> fullName)), Json.arr(validCompany))
      )
      result mustBe List.empty
    }

    "return validation errors for mixed individual and company settlors with missing fields" in {
      val result = service.validate(
        mixedSettlorsJson(Json.arr(Json.obj("name" -> firstNameOnly)), Json.arr(emptyObject))
      )
      result must contain allOf (
        IncompleteSettlorData("registration: individualSettlor[0].name.lastName missing"),
        IncompleteSettlorData("registration: companySettlor[0].name missing")
      )
    }

    "return error when deceased settlor exists alongside individual settlors" in {
      val combined = deceasedSettlorJson(fullName) ++ individualSettlorsJson(Json.arr(Json.obj("name" -> fullName)))
      val result   = service.validate(combined)
      result mustEqual List(InvalidSettlorData("registration: deceased settlor cannot coexist with other settlors"))
    }

    "return error when deceased settlor exists alongside company settlors" in {
      val combined = deceasedSettlorJson(fullName) ++ companySettlorsJson(Json.arr(validCompany))
      val result   = service.validate(combined)
      result mustEqual List(InvalidSettlorData("registration: deceased settlor cannot coexist with other settlors"))
    }

    "return incomplete error when deceased settlor has no name object" in {
      val result = service.validate(
        Json.obj("trust/entities/deceased" -> emptyObject)
      )
      result mustEqual List(IncompleteSettlorData("registration: deceased.name missing"))
    }

    "return incomplete error when individual settlor has no name object" in {
      val result = service.validate(
        individualSettlorsJson(Json.arr(emptyObject))
      )
      result mustEqual List(IncompleteSettlorData("registration: individualSettlor[0].name missing"))
    }

    "return incomplete error when individual settlor array contains a non-object entry" in {
      val result = service.validate(
        individualSettlorsJson(Json.arr(JsString("not an object")))
      )
      result mustEqual List(IncompleteSettlorData("registration: individualSettlor[0] data missing"))
    }

    "return validation message when settlors object is empty" in {
      val result = service.validate(emptyObject)
      result mustEqual List(
        MissingSettlorData(
          "registration: no settlor information provided. Trust should have either a deceased settlor, an individual settlor or a company settlor"
        )
      )
    }
  }

}
