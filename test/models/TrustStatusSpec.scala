/*
 * Copyright 2019 HM Revenue & Customs
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

package models

import models.playback.{Closed, Processing, TrustStatus}
import org.scalatest.{MustMatchers, WordSpec}
import play.api.libs.json.{JsError, Json}

class TrustStatusSpec extends WordSpec with MustMatchers {

  "trust status" must {
    "read from Json" when {
      "processing" in {

        val json = """{
                     |
                     |  "responseHeader": {
                     |    "status": "In Processing",
                     |    "formBundleNo": "1"
                     |  }
                     |}
      """.stripMargin

        Json.parse(json).as[TrustStatus] mustBe Processing
      }

      "closed" in {

        val json = """{
                     |
                     |  "responseHeader": {
                     |    "status": "Closed",
                     |    "formBundleNo": "1"
                     |  }
                     |}
                 """.stripMargin

        Json.parse(json).as[TrustStatus] mustBe Closed
      }
    }

    "return failure when not given expected response" in {

      val json = """{
                   |
                   |  "responseHeader": {
                   |    "status": "SomethingElse",
                   |    "formBundleNo": "1"
                   |  }
                   |}
                 """.stripMargin

      Json.parse(json).validate[TrustStatus] mustBe JsError("Unexpected Status")
    }
  }

}
