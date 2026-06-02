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

package base

import play.api.libs.json.{JsObject, JsValue, Json}

trait Fixtures {

  val jsonReturnedByGetRequestPieces: JsObject = Json
    .parse(
      """
        |{
        |  "trust/entities/settlors": {
        |    "settlor": [
        |      {
        |        "aliveAtRegistration": false,
        |        "name": {
        |          "firstName": "Mark",
        |          "lastName": "B"
        |        },
        |        "identification": {
        |          "address": {
        |            "line1": "123",
        |            "line2": "Test address",
        |            "postCode": "AB1 1AB",
        |            "country": "GB"
        |          }
        |        },
        |        "countryOfResidence": "GB",
        |        "nationality": "GB"
        |      }
        |    ]
        |  }
        |}
      """.stripMargin
    )
    .as[JsObject]

  val validGetDraftSettlorsJson: JsValue = Json.parse(
    """
      |{
      |  "_id": "193af51f-a9b1-4aec-9932-a7a32c33dc77",
      |  "data": {
      |    "settlors": {
      |      "setUpByLivingSettlorYesNo": false,
      |      "deceased": {
      |        "name": {
      |          "firstName": "Will",
      |          "middleName": "James",
      |          "lastName": "Graham"
      |        },
      |        "dateOfDeathYesNo": true,
      |        "dateOfDeath": "2017-03-13",
      |        "dateOfBirthYesNo": true,
      |        "settlorsDateOfBirth": "1957-03-13",
      |        "countryOfNationalityYesNo": true,
      |        "countryOfNationalityInTheUkYesNo": false,
      |        "countryOfNationality": "JO",
      |        "countryOfResidenceYesNo": true,
      |        "countryOfResidenceInTheUkYesNo": false,
      |        "countryOfResidence": "LV",
      |        "status": "completed"
      |      }
      |    }
      |  },
      |  "internalId": "Int-2b56bf2a-0d8e-4aec-ba40-a1d88b66013f",
      |  "isTaxable": false
      |}
      |""".stripMargin
  )

}
