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

  "SettlorValidationService.validateSettlorComponents" must {

    "return no validation errors when all required fields are present for deceased settlor" in {
      val deceasedSettlor = Json.obj(
        "deceased" -> Json.obj(
          "name" -> Json.obj(
            "firstName" -> "Will",
            "lastName" -> "Graham"
          ),
          "dateOfBirth" -> "1957-03-13",
          "dateOfDeath" -> "2017-03-13",
          "identification" -> Json.obj(
            "nino" -> "AA123456A"
          ),
          "countryOfResidence" -> "GB",
          "nationality" -> "GB"
        )
      )

      val result = service.validateSettlorComponents(Some(deceasedSettlor))

      result mustBe List.empty
    }

    "return validation errors when fields are missing for deceased settlor" in {
      val deceasedSettlor = Json.obj(
        "deceased" -> Json.obj(
          "name" -> Json.obj(
            "firstName" -> "Will"
          )
        )
      )

      val result = service.validateSettlorComponents(Some(deceasedSettlor))

      result must contain allOf(
        "deceased.name.lastName missing",
        "deceased.dateOfBirth missing",
        "deceased.identification missing",
        "deceased.countryOfResidence missing",
        "deceased.nationality missing",
        "deceased.dateOfDeath missing"
      )
    }

    "return no validation errors when all required fields are present for individual settlor" in {
      val individualSettlors = Json.obj(
        "settlor" -> Json.arr(
          Json.obj(
            "name" -> Json.obj(
              "firstName" -> "John",
              "lastName" -> "Smith"
            ),
            "dateOfBirth" -> "1980-01-01",
            "identification" -> Json.obj(
              "nino" -> "BB123456B"
            ),
            "countryOfResidence" -> "GB",
            "nationality" -> "GB"
          )
        )
      )

      val result = service.validateSettlorComponents(Some(individualSettlors))

      result mustBe List.empty
    }

    "return validation errors when individual settlor has missing fields" in {
      val individualSettlors = Json.obj(
        "settlor" -> Json.arr(
          Json.obj(
            "name" -> Json.obj(
              "firstName" -> "John"
            )
          )
        )
      )

      val result = service.validateSettlorComponents(Some(individualSettlors))

      result must contain allOf(
        "settlor[0].name.lastName missing",
        "settlor[0].dateOfBirth missing",
        "settlor[0].identification missing",
        "settlor[0].countryOfResidence missing",
        "settlor[0].nationality missing"
      )
    }

    "return validation errors for multiple individual settlors" in {
      val individualSettlors = Json.obj(
        "settlor" -> Json.arr(
          Json.obj(
            "name" -> Json.obj(
              "firstName" -> "John"
            )
          ),
          Json.obj(
            "name" -> Json.obj(
              "firstName" -> "Jane",
              "lastName" -> "Smith"
            )
          )
        )
      )

      val result = service.validateSettlorComponents(Some(individualSettlors))

      result must contain allOf(
        "settlor[0].name.lastName missing",
        "settlor[0].dateOfBirth missing",
        "settlor[1].dateOfBirth missing",
        "settlor[1].identification missing"
      )
    }

    "return no validation errors when all required fields are present for company settlor" in {
      val companySettlors = Json.obj(
        "settlorCompany" -> Json.arr(
          Json.obj(
            "name" -> "Test Company Ltd",
            "companyType" -> "Trading",
            "identification" -> Json.obj(
              "utr" -> "1234567890"
            ),
            "countryOfResidence" -> "GB"
          )
        )
      )

      val result = service.validateSettlorComponents(Some(companySettlors))

      result mustBe List.empty
    }

    "return validation errors when company settlor has missing fields" in {
      val companySettlors = Json.obj(
        "settlorCompany" -> Json.arr(
          Json.obj(
            "name" -> "Test Company Ltd"
          )
        )
      )

      val result = service.validateSettlorComponents(Some(companySettlors))

      result must contain allOf(
        "settlorCompany[0].companyType missing",
        "settlorCompany[0].identification missing",
        "settlorCompany[0].countryOfResidence missing"
      )
    }

    "return validation errors for multiple company settlors" in {
      val companySettlors = Json.obj(
        "settlorCompany" -> Json.arr(
          Json.obj(
            "name" -> "Company One Ltd"
          ),
          Json.obj(
            "name" -> "Company Two Ltd",
            "companyType" -> "Investment"
          )
        )
      )

      val result = service.validateSettlorComponents(Some(companySettlors))

      result must contain allOf(
        "settlorCompany[0].companyType missing",
        "settlorCompany[0].identification missing",
        "settlorCompany[1].identification missing",
        "settlorCompany[1].countryOfResidence missing"
      )
    }

    "return no validation errors when both individual and company settlors are present and valid" in {
      val mixedSettlors = Json.obj(
        "settlor" -> Json.arr(
          Json.obj(
            "name" -> Json.obj(
              "firstName" -> "John",
              "lastName" -> "Smith"
            ),
            "dateOfBirth" -> "1980-01-01",
            "identification" -> Json.obj(
              "nino" -> "BB123456B"
            ),
            "countryOfResidence" -> "GB",
            "nationality" -> "GB"
          )
        ),
        "settlorCompany" -> Json.arr(
          Json.obj(
            "name" -> "Test Company Ltd",
            "companyType" -> "Trading",
            "identification" -> Json.obj(
              "utr" -> "1234567890"
            ),
            "countryOfResidence" -> "GB"
          )
        )
      )

      val result = service.validateSettlorComponents(Some(mixedSettlors))

      result mustBe List.empty
    }

    "return validation errors for mixed individual and company settlors with missing fields" in {
      val mixedInvalidSettlors = Json.obj(
        "settlor" -> Json.arr(
          Json.obj(
            "name" -> Json.obj(
              "firstName" -> "John"
            )
          )
        ),
        "settlorCompany" -> Json.arr(
          Json.obj(
            "name" -> "Test Company Ltd"
          )
        )
      )

      val result = service.validateSettlorComponents(Some(mixedInvalidSettlors))

      result must contain allOf(
        "settlor[0].name.lastName missing",
        "settlor[0].dateOfBirth missing",
        "settlor[0].identification missing",
        "settlor[0].countryOfResidence missing",
        "settlor[0].nationality missing",
        "settlorCompany[0].companyType missing",
        "settlorCompany[0].identification missing",
        "settlorCompany[0].countryOfResidence missing"
      )
    }

    "return error when deceased settlor exists alongside individual settlors" in {
      val invalidSettlors = Json.obj(
        "deceased" -> Json.obj(
          "name" -> Json.obj(
            "firstName" -> "Will",
            "lastName" -> "Graham"
          ),
          "dateOfBirth" -> "1957-03-13",
          "dateOfDeath" -> "2017-03-13",
          "identification" -> Json.obj(
            "nino" -> "AA123456A"
          ),
          "countryOfResidence" -> "GB",
          "nationality" -> "GB"
        ),
        "settlor" -> Json.arr(
          Json.obj(
            "name" -> Json.obj(
              "firstName" -> "John",
              "lastName" -> "Smith"
            ),
            "dateOfBirth" -> "1980-01-01"
          )
        )
      )

      val result = service.validateSettlorComponents(Some(invalidSettlors))

      result mustEqual List("deceased settlor cannot coexist with other settlors")
    }

    "return validation message when settlors data is None" in {
      val result = service.validateSettlorComponents(None)

      result mustEqual List("no settlor information provided. Trust should have either a deceased settlor, an individual settlor or a company settlor")
    }

    "return validation message when settlors object is empty" in {
      val emptySettlors = Json.obj()

      val result = service.validateSettlorComponents(Some(emptySettlors))

      result mustEqual List("no settlor information provided. Trust should have either a deceased settlor, an individual settlor or a company settlor")
    }
  }
}