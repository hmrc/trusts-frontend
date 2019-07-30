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

package viewmodels

import generators.{Generators, ModelGenerators}
import models.Status.{Completed, InProgress}
import models.WhatKindOfAsset.{Money, PropertyOrLand, Shares}
import org.scalatest.prop.PropertyChecks
import org.scalatest.{FreeSpec, MustMatchers}
import play.api.libs.json.{JsSuccess, Json}
import viewmodels.addAnother.{AssetViewModel, DefaultAssetsViewModel, MoneyAssetViewModel, ShareAssetViewModel}

class AssetViewModelSpec extends FreeSpec with MustMatchers with PropertyChecks with Generators with ModelGenerators {

  "Asset" - {

    "must deserialise" - {

      "money" - {

        "to a view model that is not complete" in {

          val json = Json.obj(
            "whatKindOfAsset" -> Money.toString
          )

          json.validate[AssetViewModel] mustEqual JsSuccess(
            MoneyAssetViewModel(Money, None, InProgress)
          )
        }

        "to a view model that is complete" in {
          val json = Json.obj(
            "whatKindOfAsset" -> Money.toString,
            "assetMoneyValue" -> "4000",
            "status" -> Completed.toString
          )

          json.validate[AssetViewModel] mustEqual JsSuccess(
            MoneyAssetViewModel(Money, Some("£4000"), Completed)
          )
        }

      }

      "share non-portfolio" - {

        "to a view model that is not complete" in {

          val json = Json.parse(
            """
              |{
              |"whatKindOfAsset" : "Shares",
              |"sharesInAPortfolio" : false,
              |"status": "progress"
              |}
            """.stripMargin)

          json.validate[AssetViewModel] mustEqual JsSuccess(
            ShareAssetViewModel(Shares, None, InProgress)
          )

        }

        "to a view model that is complete" in {
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

          json.validate[AssetViewModel] mustEqual JsSuccess(
            ShareAssetViewModel(Shares, Some("adam"), Completed)
          )
        }

      }

      "to a default from any other type" in {
        val json = Json.obj(
          "whatKindOfAsset" -> PropertyOrLand.toString
        )

        json.validate[AssetViewModel] mustEqual JsSuccess(
          DefaultAssetsViewModel(PropertyOrLand, InProgress)
        )
      }
    }
  }

}
