/*
 * Copyright 2024 HM Revenue & Customs
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

package utils

import org.scalatestplus.play.PlaySpec
import play.api.libs.json.{JsObject, Json}

class JsonTransformersSpec extends PlaySpec {

  val singleSettlorsWithAliveAtRegField: String = """
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
                                             |""".stripMargin

  val singleSettlorsWithoutAliveAtRegField: String = """
                                                       |{
                                                       |  "trust/entities/settlors": {
                                                       |    "settlor": [
                                                       |      {
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
                                                       |""".stripMargin

  val transformedSingleSettlors: String = """
                                                                   |{
                                                                   | "settlor": [
                                                                   |   {
                                                                   |     "name": {
                                                                   |       "firstName": "Mark",
                                                                   |       "lastName": "B"
                                                                   |     },
                                                                   |     "identification": {
                                                                   |       "address": {
                                                                   |         "line1": "123",
                                                                   |         "line2": "Test address",
                                                                   |         "postCode": "AB1 1AB",
                                                                   |         "country": "GB"
                                                                   |       }
                                                                   |     },
                                                                   |     "countryOfResidence": "GB",
                                                                   |     "nationality": "GB"
                                                                   |   }
                                                                   | ]
                                                                   |}
                                                                   |""".stripMargin

  val multipleSettlorsWithAliveAtRegField: String = """
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
                                                          |      },
                                                          |      {
                                                          |        "aliveAtRegistration": false,
                                                          |        "name": {
                                                          |          "firstName": "Mark",
                                                          |          "lastName": "BC"
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
                                                          |      },
                                                          |      {
                                                          |        "name": {
                                                          |          "firstName": "Mark",
                                                          |          "lastName": "BC"
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
                                                          |""".stripMargin

  val multipleSettlorsWithoutAliveAtRegField: String = """
                                                      |{
                                                      |  "trust/entities/settlors": {
                                                      |    "settlor": [
                                                      |      {
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
                                                      |      },
                                                      |      {
                                                      |        "name": {
                                                      |          "firstName": "Mark",
                                                      |          "lastName": "BC"
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
                                                      |      },
                                                      |      {
                                                      |        "name": {
                                                      |          "firstName": "Mark",
                                                      |          "lastName": "BC"
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
                                                      |""".stripMargin

  val transformedRegistrationPiecesMultipleSettlor: String = """
                                                                   |{
                                                                   |    "settlor": [
                                                                   |      {
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
                                                                   |      },
                                                                   |      {
                                                                   |        "name": {
                                                                   |          "firstName": "Mark",
                                                                   |          "lastName": "BC"
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
                                                                   |      },
                                                                   |      {
                                                                   |        "name": {
                                                                   |          "firstName": "Mark",
                                                                   |          "lastName": "BC"
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
                                                                   |}
                                                                   |""".stripMargin

  val businessSettlorRegistrationJson: String =
    """
      |{
      | "trust/entities/settlors": {
      |   "settlorCompany": [
      |     {
      |       "name": "Lennon Ltd",
      |       "companyType": "Trading",
      |       "companyTime": true,
      |       "identification": {
      |         "utr": "1234567890"
      |       },
      |       "countryOfResidence": "GB"
      |     }
      |   ]
      | }
      |}
      |""".stripMargin

  "removeAliveAtRegistrationFromJson" when {

    "there is a single settlor in the registration Json" must {
      "return the expected Json when the aliveAtRegistration field is present" in {
        val registrationPiecesJson: JsObject = Json.parse(singleSettlorsWithAliveAtRegField).as[JsObject]
        JsonTransformers.removeAliveAtRegistrationFromJson(registrationPiecesJson) mustBe Some(
          Json.parse(transformedSingleSettlors)
        )
      }

      "return the expected Json when the the aliveAtRegistration field is not present" in {
        val registrationPiecesJson: JsObject = Json.parse(singleSettlorsWithoutAliveAtRegField).as[JsObject]
        JsonTransformers.removeAliveAtRegistrationFromJson(registrationPiecesJson) mustBe Some(
          Json.parse(transformedSingleSettlors)
        )
      }
    }

    "there are multiple settlors in the registration Json" must {
      "return the expected Json when the aliveAtRegistration field is present" in {
        val registrationPiecesJson: JsObject = Json.parse(multipleSettlorsWithAliveAtRegField).as[JsObject]
        JsonTransformers.removeAliveAtRegistrationFromJson(registrationPiecesJson) mustBe Some(
          Json.parse(transformedRegistrationPiecesMultipleSettlor)
        )
      }

      "return the expected Json when the aliveAtRegistration field is not present" in {
        val registrationPiecesJson: JsObject = Json.parse(multipleSettlorsWithoutAliveAtRegField).as[JsObject]
        JsonTransformers.removeAliveAtRegistrationFromJson(registrationPiecesJson) mustBe Some(
          Json.parse(transformedRegistrationPiecesMultipleSettlor)
        )
      }
    }

    "parsed Json without trust/entities/settlors field return None" in {
      val registrationPiecesJson: JsObject = Json.parse("{}").as[JsObject]
      JsonTransformers.removeAliveAtRegistrationFromJson(registrationPiecesJson) mustBe None
    }

    "parsed Json with the trust/entities/settlors field but no settlor field return false" in {
      val registrationPiecesJson: JsObject = Json.parse(businessSettlorRegistrationJson).as[JsObject]
      JsonTransformers.removeAliveAtRegistrationFromJson(registrationPiecesJson) mustBe None
    }
  }

  "checkIfAliveAtRegistrationFieldPresent" when {

    "there is a single settlor in the registration Json" must {
      "return true when the aliveAtRegistration field is present" in {
        val registrationPiecesJson: JsObject = Json.parse(singleSettlorsWithAliveAtRegField).as[JsObject]
        JsonTransformers.checkIfAliveAtRegistrationFieldPresent(registrationPiecesJson) mustBe true
      }

      "return false when the aliveAtRegistration field is not present" in {
        val registrationPiecesJson: JsObject = Json.parse(singleSettlorsWithoutAliveAtRegField).as[JsObject]
        JsonTransformers.checkIfAliveAtRegistrationFieldPresent(registrationPiecesJson) mustBe false
      }
    }

    "there are multiple settlor in the registration Json" must {
      "return true when parsed Json with multiple settlors where some contain the nested field aliveAtRegistration" in {
        val registrationPiecesJson: JsObject = Json.parse(multipleSettlorsWithAliveAtRegField).as[JsObject]
        JsonTransformers.checkIfAliveAtRegistrationFieldPresent(registrationPiecesJson) mustBe true
      }

      "return false when parsed Json with multiple settlors where none contain the nested field aliveAtRegistration" in {
        val registrationPiecesJson: JsObject = Json.parse(multipleSettlorsWithoutAliveAtRegField).as[JsObject]
        JsonTransformers.checkIfAliveAtRegistrationFieldPresent(registrationPiecesJson) mustBe false
      }
    }

    "parsed Json without the trust/entities/settlors field return false" in {
      val registrationPiecesJson: JsObject = Json.parse("{}").as[JsObject]
      JsonTransformers.checkIfAliveAtRegistrationFieldPresent(registrationPiecesJson) mustBe false
    }

    "parsed Json with the trust/entities/settlors field but no settlor field return false" in {
      val registrationPiecesJson: JsObject = Json.parse(businessSettlorRegistrationJson).as[JsObject]
      JsonTransformers.checkIfAliveAtRegistrationFieldPresent(registrationPiecesJson) mustBe false
    }
  }

}
