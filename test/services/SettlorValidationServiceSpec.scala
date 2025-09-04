/*
 * Copyright 2025 HM Revenue & Customs
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

package services

import base.RegistrationSpecBase
import play.api.libs.json._

class SettlorValidationServiceSpec extends RegistrationSpecBase {

  val service = new SettlorValidationService()
  
  private def deceasedSettlorJson(name: JsObject) = Json.obj("trust/entities/deceased" -> Json.obj("name" -> name))
  private def individualSettlorsJson(settlors: JsArray) = Json.obj("trust/entities/settlors" -> Json.obj("settlor" -> settlors))
  private def companySettlorsJson(companies: JsArray) = Json.obj("trust/entities/settlors" -> Json.obj("settlorCompany" -> companies))
  private def mixedSettlorsJson(settlors: JsArray, companies: JsArray) = Json.obj("trust/entities/settlors" -> Json.obj("settlor" -> settlors, "settlorCompany" -> companies))
  
  private def answerDeceasedJson(name: JsObject) = Json.obj("data" -> Json.obj("settlors" -> Json.obj("setUpByLivingSettlorYesNo" -> false, "deceased" -> Json.obj("name" -> name))))
  private def answerLivingJson(living: JsArray) = Json.obj("data" -> Json.obj("settlors" -> Json.obj("setUpByLivingSettlorYesNo" -> true, "living" -> living)))
  private def answerCompanyJson(companies: JsArray) = Json.obj("data" -> Json.obj("settlors" -> Json.obj("setUpByLivingSettlorYesNo" -> true, "settlorCompany" -> companies)))
  private def answerEmptyLivingJson() = Json.obj("data" -> Json.obj("settlors" -> Json.obj("setUpByLivingSettlorYesNo" -> true)))
  private def answerEmptyDeceasedJson() = Json.obj("data" -> Json.obj("settlors" -> Json.obj("setUpByLivingSettlorYesNo" -> false)))

  private val fullName = Json.obj("firstName" -> "John", "lastName" -> "Smith")
  private val firstNameOnly = Json.obj("firstName" -> "John")

  private val validCompany = Json.obj("name" -> "Test Company Ltd")
  private val emptyObject = Json.obj()

  "SettlorValidationService.validateRegistrationSettlorComponent" must {

    "return no validation errors when all required fields are present for deceased settlor" in {
      val result = service.validateRegistrationSettlorComponent(Some(deceasedSettlorJson(fullName)))
      result mustBe List.empty
    }

    "return validation errors when deceased settlor last name missing" in {
      val result = service.validateRegistrationSettlorComponent(Some(deceasedSettlorJson(firstNameOnly)))
      result mustEqual List("registration: deceased.name.lastName missing")
    }

    "return no validation errors when all required fields are present for individual settlor" in {
      val result = service.validateRegistrationSettlorComponent(Some(individualSettlorsJson(Json.arr(Json.obj("name" -> fullName)))))
      result mustBe List.empty
    }

    "return validation errors when individual settlor has last name missing" in {
      val result = service.validateRegistrationSettlorComponent(Some(individualSettlorsJson(Json.arr(Json.obj("name" -> firstNameOnly)))))
      result mustEqual List("registration: settlor[0].name.lastName missing")
    }

    "return validation errors for multiple individual settlors" in {
      val settlors = Json.arr(Json.obj("name" -> fullName), Json.obj("name" -> firstNameOnly))
      val result = service.validateRegistrationSettlorComponent(Some(individualSettlorsJson(settlors)))
      result mustEqual List("registration: settlor[1].name.lastName missing")
    }

    "return no validation errors when all required fields are present for company settlor" in {
      val result = service.validateRegistrationSettlorComponent(Some(companySettlorsJson(Json.arr(validCompany))))
      result mustBe List.empty
    }

    "return validation errors when company settlor name is missing" in {
      val result = service.validateRegistrationSettlorComponent(Some(companySettlorsJson(Json.arr(emptyObject))))
      result mustEqual List("registration: settlorCompany[0].name missing")
    }

    "return validation errors for multiple company settlors" in {
      val companies = Json.arr(validCompany, emptyObject)
      val result = service.validateRegistrationSettlorComponent(Some(companySettlorsJson(companies)))
      result mustEqual List("registration: settlorCompany[1].name missing")
    }

    "return no validation errors when both individual and company settlors are present and valid" in {
      val result = service.validateRegistrationSettlorComponent(
        Some(mixedSettlorsJson(Json.arr(Json.obj("name" -> fullName)), Json.arr(validCompany)))
      )
      result mustBe List.empty
    }

    "return validation errors for mixed individual and company settlors with missing fields" in {
      val result = service.validateRegistrationSettlorComponent(
        Some(mixedSettlorsJson(Json.arr(Json.obj("name" -> firstNameOnly)), Json.arr(emptyObject)))
      )
      result must contain allOf(
        "registration: settlor[0].name.lastName missing",
        "registration: settlorCompany[0].name missing"
      )
    }

    "return error when deceased settlor exists alongside individual settlors" in {
      val combined = deceasedSettlorJson(fullName) ++ individualSettlorsJson(Json.arr(Json.obj("name" -> fullName)))
      val result = service.validateRegistrationSettlorComponent(Some(combined))
      result mustEqual List("registration: deceased settlor cannot coexist with other settlors")
    }

    "return error when deceased settlor exists alongside company settlors" in {
      val combined = deceasedSettlorJson(fullName) ++ companySettlorsJson(Json.arr(validCompany))
      val result = service.validateRegistrationSettlorComponent(Some(combined))
      result mustEqual List("registration: deceased settlor cannot coexist with other settlors")
    }

    "return validation message when settlors data is None" in {
      val result = service.validateRegistrationSettlorComponent(None)
      result mustEqual List("registration: no settlor information provided")
    }

    "return validation message when settlors object is empty" in {
      val result = service.validateRegistrationSettlorComponent(Some(emptyObject))
      result mustEqual List("registration: no settlor information provided. Trust should have either a deceased settlor, an individual settlor or a company settlor")
    }
  }

  "SettlorValidationService.validateAnswerSectionSettlorComponent" must {

    "return no validation errors when deceased settlor is valid and setUpByLivingSettlorYesNo is false" in {
      val result = service.validateAnswerSectionSettlorComponent(Some(answerDeceasedJson(fullName)))
      result mustBe List.empty
    }

    "return validation errors when deceased settlor data is missing" in {
      val result = service.validateAnswerSectionSettlorComponent(Some(answerEmptyDeceasedJson()))
      result mustEqual List("answer section: deceased settlor data missing")
    }

    "return no validation errors when living settlors are valid" in {
      val result = service.validateAnswerSectionSettlorComponent(Some(answerLivingJson(Json.arr(Json.obj("name" -> fullName)))))
      result mustBe List.empty
    }

    "return no validation errors when company settlors are valid in answer section" in {
      val result = service.validateAnswerSectionSettlorComponent(Some(answerCompanyJson(Json.arr(validCompany))))
      result mustBe List.empty
    }

    "return validation error when no living settlor information provided" in {
      val result = service.validateAnswerSectionSettlorComponent(Some(answerEmptyLivingJson()))
      result mustEqual List("answer section: no living settlor information provided")
    }

    "return validation errors when living settlor has last name missing" in {
      val result = service.validateAnswerSectionSettlorComponent(Some(answerLivingJson(Json.arr(Json.obj("name" -> firstNameOnly)))))
      result mustEqual List("answer section: living[0].name.lastName missing")
    }

    "return validation message when answer section data is None" in {
      val result = service.validateAnswerSectionSettlorComponent(None)
      result mustEqual List("answer section: no data provided")
    }

    "return validation message when settlors section is missing" in {
      val result = service.validateAnswerSectionSettlorComponent(Some(Json.obj("data" -> emptyObject)))
      result mustEqual List("answer section: no settlors section found")
    }
  }
}
