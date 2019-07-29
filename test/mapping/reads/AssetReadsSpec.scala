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

package mapping.reads

import models.WhatKindOfAsset.Money
import models.{ShareClass, Status, WhatKindOfAsset}
import org.scalatest.{FreeSpec, MustMatchers}
import play.api.libs.json.{JsError, JsSuccess, Json}

class AssetReadsSpec extends FreeSpec with MustMatchers {

  "Asset" - {

    "must fail to deserialise" - {

      "from a money asset of the incorrect structure" in {
        val json = Json.obj(
          "whatKindOfAsset" -> "Property",
          "assetMoneyValue" -> "4000"
        )

        json.validate[Asset] mustBe a[JsError]
      }

      "from a share asset of the incorrect structure" in {

        val json = Json.parse(
          """
            |{
            |"listedOnTheStockExchange" : true,
            |"shareCompanyName" : "adam",
            |"sharesInAPortfolio" : false,
            |"quantityInTheTrust" : "200",
            |"value" : "200",
            |"whatKindOfAsset" : "Shares"
            |}
          """.stripMargin)

        json.validate[Asset] mustBe a[JsError]

      }

      "from a share portfolio asset of the incorrect structure" in {
        val json = Json.parse(
          """
            |{
            |"listedOnTheStockExchange" : true,
            |"sharesInAPortfolio" : true,
            |"value" : "290000",
            |"whatKindOfAsset" : "Shares",
            |"status" : "progress"
            |}
          """.stripMargin)

        json.validate[Asset] mustBe a[JsError]
      }

    }

    "must deserialise" - {

      "from a money asset" in {
        val json = Json.obj(
          "whatKindOfAsset" -> "Money",
          "assetMoneyValue" -> "4000"
        )

        json.validate[Asset] mustEqual JsSuccess(MoneyAsset(Money,"4000"))
      }

      "from a share asset" in {

        val json = Json.parse(
          """
            |{
            |"listedOnTheStockExchange" : true,
            |"shareCompanyName" : "adam",
            |"sharesInAPortfolio" : false,
            |"quantityInTheTrust" : "200",
            |"value" : "200",
            |"whatKindOfAsset" : "Shares",
            |"class" : "ordinary",
            |"status": "completed"
            |}
          """.stripMargin)

        json.validate[Asset] mustEqual JsSuccess(
          ShareAsset(
          listedOnTheStockExchange = true,
          shareCompanyName = "adam",
          sharesInAPortfolio = false,
          quantityInTheTrust = "200",
          value = "200",
          whatKindOfAsset = WhatKindOfAsset.Shares,
          `class` = ShareClass.Ordinary,
          status = Status.Completed
        ))

      }

      "from a share portfolio asset" in {
        val json = Json.parse(
          """
            |{
            |"listedOnTheStockExchange" : true,
            |"sharesInAPortfolio" : true,
            |"name" : "Adam",
            |"quantityInTheTrust" : "200",
            |"value" : "290000",
            |"whatKindOfAsset" : "Shares",
            |"status" : "completed"
            |}
          """.stripMargin)

        json.validate[Asset] mustEqual JsSuccess(
          SharePortfolioAsset(
            listedOnTheStockExchange = true,
            name = "Adam",
            sharesInAPortfolio = true,
            quantityInTheTrust = "200",
            value = "290000",
            whatKindOfAsset = WhatKindOfAsset.Shares,
            status = Status.Completed
          ))
      }
    }
  }

}
