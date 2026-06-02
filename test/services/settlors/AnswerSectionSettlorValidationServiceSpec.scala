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

class AnswerSectionSettlorValidationServiceSpec extends RegistrationSpecBase {

  val service = new AnswerSectionSettlorValidationService()

  private def answerDeceasedJson(name: JsObject) = Json.obj(
    "data" -> Json.obj(
      "settlors" -> Json.obj("setUpByLivingSettlorYesNo" -> false, "deceased" -> Json.obj("name" -> name))
    )
  )

  private def answerLivingJson(living: JsArray) =
    Json.obj("data" -> Json.obj("settlors" -> Json.obj("setUpByLivingSettlorYesNo" -> true, "living" -> living)))

  private def answerEmptyLivingJson() =
    Json.obj("data" -> Json.obj("settlors" -> Json.obj("setUpByLivingSettlorYesNo" -> true)))

  private def answerEmptyDeceasedJson() =
    Json.obj("data" -> Json.obj("settlors" -> Json.obj("setUpByLivingSettlorYesNo" -> false)))

  private val fullName        = Json.obj("firstName" -> "John", "lastName" -> "Smith")
  private val firstNameOnly   = Json.obj("firstName" -> "John")
  private val validIndividual = Json.obj("individualOrBusiness" -> "individual", "name" -> fullName)
  private val validBusiness   = Json.obj("individualOrBusiness" -> "business", "businessName" -> "Test Company Ltd")
  private val emptyObject     = Json.obj()

  "AnswerSectionSettlorValidationService.validate" must {

    "return no validation errors when deceased settlor is valid and setUpByLivingSettlorYesNo is false" in {
      val result = service.validate(answerDeceasedJson(fullName))
      result mustBe List.empty
    }

    "return validation errors when deceased settlor data is missing" in {
      val result = service.validate(answerEmptyDeceasedJson())
      result mustEqual List(IncompleteSettlorData("answer section: deceased settlor data missing"))
    }

    "return no validation errors when living settlors are valid" in {
      val result = service.validate(answerLivingJson(Json.arr(validIndividual)))
      result mustBe List.empty
    }

    "return no validation errors when business settlors are valid in answer section" in {
      val result = service.validate(answerLivingJson(Json.arr(validBusiness)))
      result mustBe List.empty
    }

    "return no validation errors when mixed individual and business settlors are valid" in {
      val result =
        service.validate(answerLivingJson(Json.arr(validIndividual, validBusiness)))
      result mustBe List.empty
    }

    "return validation error when no living settlor information provided" in {
      val result = service.validate(answerEmptyLivingJson())
      result mustEqual List(IncompleteSettlorData("answer section: no living settlor information provided"))
    }

    "return validation errors when living individual settlor has missing last name" in {
      val invalidIndividual = Json.obj("individualOrBusiness" -> "individual", "name" -> firstNameOnly)
      val result            = service.validate(answerLivingJson(Json.arr(invalidIndividual)))
      result mustEqual List(IncompleteSettlorData("answer section: individualSettlor[0].name.lastName missing"))
    }

    "return validation errors when business settlor has missing name" in {
      val invalidBusiness = Json.obj("individualOrBusiness" -> "business")
      val result          = service.validate(answerLivingJson(Json.arr(invalidBusiness)))
      result mustEqual List(IncompleteSettlorData("answer section: companySettlor[0].businessName missing"))
    }

    "return validation errors when settlor has missing individualOrBusiness field" in {
      val invalidSettlor = Json.obj("name" -> fullName)
      val result         = service.validate(answerLivingJson(Json.arr(invalidSettlor)))
      result mustEqual List(IncompleteSettlorData("answer section: settlor[0] individualOrBusiness missing or invalid"))
    }

    "return invalid error when setUpByLivingSettlorYesNo flag is missing" in {
      val noFlag = Json.obj("data" -> Json.obj("settlors" -> emptyObject))
      val result = service.validate(noFlag)
      result mustEqual List(IncompleteSettlorData("answer section: setUpByLivingSettlorYesNo missing"))
    }

    "return incomplete error when individual settlor in living array has no name" in {
      val individualWithoutName = Json.obj("individualOrBusiness" -> "individual")
      val result                = service.validate(
        answerLivingJson(Json.arr(individualWithoutName))
      )
      result mustEqual List(IncompleteSettlorData("answer section: individualSettlor[0].name missing"))
    }

    "return validation message when settlors section is missing" in {
      val result = service.validate(Json.obj("data" -> emptyObject))
      result mustEqual List(MissingSettlorData("answer section: no settlors section found"))
    }
  }

}
