/*
 * Copyright 2023 HM Revenue & Customs
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

    val registrationPiecesString: String = """
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

    val transformedRegistrationPiecesString: String = """
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

    val registrationPiecesStringWithoutAliveAtRegistration: String = """
                                                               |{
                                                               |  "trust/entities/settlors": {
                                                               |    "settlor": []
                                                               |  }
                                                               |}
                                                               |""".stripMargin

  "removeAliveAtRegistrationFromJson" when {
    "parsed Json in the correct format with the aliveAtRegistration field return the same Json with the field removed" in {
        val registrationPiecesJson: JsObject = Json.parse(registrationPiecesString).as[JsObject]
        JsonTransformers.removeAliveAtRegistrationFromJson(registrationPiecesJson) mustBe Some(Json.parse(transformedRegistrationPiecesString))
      }

    "parsed Json without trust/entities/settlors field return None" in {
      val registrationPiecesJson: JsObject = Json.parse("{}").as[JsObject]
      JsonTransformers.removeAliveAtRegistrationFromJson(registrationPiecesJson) mustBe None
    }

    "parsed Json with trust/entities/settlors and settlors should return the settlors josn unchanged if aliveAtRegistration not present" in {
      val registrationPiecesJson: JsObject = Json.parse(registrationPiecesStringWithoutAliveAtRegistration).as[JsObject]
      JsonTransformers.removeAliveAtRegistrationFromJson(registrationPiecesJson) mustBe Some(Json.parse("""{"settlor": []}"""))
    }
  }

  "checkIfAliveAtRegistrationFieldPresent" when {

    "parsed Json which contains the nested field aliveAtRegistration return true" in {
      val registrationPiecesJson: JsObject = Json.parse(registrationPiecesString).as[JsObject]
      JsonTransformers.checkIfAliveAtRegistrationFieldPresent(registrationPiecesJson) mustBe(true)
    }

    "parsed Json which does not contains the nested field aliveAtRegistration return false" in {
      val registrationPiecesJson: JsObject = Json.parse(transformedRegistrationPiecesString).as[JsObject]
      JsonTransformers.checkIfAliveAtRegistrationFieldPresent(registrationPiecesJson) mustBe(false)
    }

  }

}
