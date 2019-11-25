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
import models.registration.pages.Status.{Completed, InProgress}
import models.registration.pages.WhatKindOfAsset._
import org.scalatest.prop.PropertyChecks
import org.scalatest.{FreeSpec, MustMatchers}
import play.api.libs.json.{JsPath, JsSuccess, Json, KeyPathNode}
import viewmodels.addAnother._

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
            ShareAssetViewModel(Shares, false, None, InProgress)
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
            ShareAssetViewModel(Shares, false, Some("adam"), Completed)
          )
        }

      }

      "share portfolio" - {

        "to a view model that is not complete" in {

          val json = Json.parse(
            """
              |{
              |"whatKindOfAsset" : "Shares",
              |"sharesInAPortfolio" : true,
              |"status": "progress"
              |}
            """.stripMargin)

          json.validate[AssetViewModel] mustEqual JsSuccess(
            ShareAssetViewModel(Shares, true, None, InProgress)
          )

        }

        "to a view model that is complete" in {
          val json = Json.parse(
            """
              |{
              |"listedOnTheStockExchange" : true,
              |"name" : "adam",
              |"sharesInAPortfolio" : true,
              |"quantityInTheTrust" : "200",
              |"value" : "200",
              |"whatKindOfAsset" : "Shares",
              |"status": "completed"
              |}
            """.stripMargin)

          json.validate[AssetViewModel] mustEqual JsSuccess(
            ShareAssetViewModel(Shares, true, Some("adam"), Completed)
          )
        }

      }

      "property or land" - {

        "property or land with description" - {

          "to a view model that is not complete" in {
            val json = Json.parse(
              """
                |{
                |"propertyOrLandAddressYesNo": false,
                |"whatKindOfAsset" : "PropertyOrLand",
                |"status": "progress"
                |}
            """.stripMargin)

            json.validate[AssetViewModel] mustEqual JsSuccess(
              PropertyOrLandAssetDescriptionViewModel(PropertyOrLand, None, InProgress)
            )
          }

          "to a view model that is complete" in {
            val json = Json.parse(
              """
                |{
                |"propertyOrLandAddressYesNo": false,
                |"propertyOrLandDescription": "1 hectare",
                |"whatKindOfAsset" : "PropertyOrLand",
                |"status": "completed"
                |}
            """.stripMargin)

            json.validate[AssetViewModel] mustEqual JsSuccess(
              PropertyOrLandAssetDescriptionViewModel(PropertyOrLand, Some("1 hectare"), Completed)
            )
          }

        }

        "property or land with address" - {

          "uk address" - {

            "to a view model that is not complete" in {
              val json = Json.parse(
                """
                  |{
                  |"propertyOrLandAddressYesNo": true,
                  |"propertyOrLandAddressUKYesNo": true,
                  |"whatKindOfAsset" : "PropertyOrLand",
                  |"status": "progress"
                  |}
            """.stripMargin)

              json.validate[AssetViewModel] mustEqual JsSuccess(
                PropertyOrLandAssetUKAddressViewModel(`type` = PropertyOrLand, address = None, status = InProgress)
              )
            }

            "to a view model that is complete" in {
              val json = Json.parse(
                """
                  |{
                  |"propertyOrLandAddressYesNo": true,
                  |"propertyOrLandAddressUKYesNo": true,
                  |"address": {
                  | "line1": "line 1",
                  | "line2": "Newcastle",
                  | "postcode": "NE11TU"
                  |},
                  |"whatKindOfAsset" : "PropertyOrLand",
                  |"status": "completed"
                  |}
            """.stripMargin)

              json.validate[AssetViewModel] mustEqual JsSuccess(
                PropertyOrLandAssetUKAddressViewModel(PropertyOrLand, Some("line 1"), Completed)
              )
            }

          }
          "international address" - {

            "to a view model that is not complete" in {
              val json = Json.parse(
                """
                  |{
                  |"propertyOrLandAddressYesNo": true,
                  |"propertyOrLandAddressUKYesNo": false,
                  |"whatKindOfAsset" : "PropertyOrLand",
                  |"status": "progress"
                  |}
            """.stripMargin)

              json.validate[AssetViewModel] mustEqual JsSuccess(
                PropertyOrLandAssetInternationalAddressViewModel(`type` = PropertyOrLand, address = None, status = InProgress)
              )
            }

            "to a view model that is complete" in {
              val json = Json.parse(
                """
                  |{
                  |"propertyOrLandAddressYesNo": true,
                  |"propertyOrLandAddressUKYesNo": false,
                  |"address": {
                  | "line1": "line 1",
                  | "line2": "line 2",
                  | "country": "France"
                  |},
                  |"whatKindOfAsset" : "PropertyOrLand",
                  |"status": "completed"
                  |}
            """.stripMargin)

              json.validate[AssetViewModel] mustEqual JsSuccess(
                PropertyOrLandAssetInternationalAddressViewModel(PropertyOrLand, Some("line 1"), Completed)
              )
            }

          }
          "address" - {

            "to a view model that is not complete" in {
              val json = Json.parse(
                """
                  |{
                  |"propertyOrLandAddressYesNo": true,
                  |"whatKindOfAsset" : "PropertyOrLand",
                  |"status": "progress"
                  |}
            """.stripMargin)

              json.validate[AssetViewModel] mustEqual JsSuccess(
                PropertyOrLandAssetAddressViewModel(`type` = PropertyOrLand, address = None, status = InProgress),
                JsPath(List(KeyPathNode("propertyOrLandAddressYesNo")))
              )
            }

          }

        }

        "to default view model when no data provided" in {
          val json = Json.parse(
            """
              |{
              |"whatKindOfAsset" : "PropertyOrLand"
              |}
            """.stripMargin)

          json.validate[AssetViewModel] mustEqual JsSuccess(
            PropertyOrLandDefaultViewModel(`type` = PropertyOrLand, status = InProgress)
          )
        }

      }


      "to a default from any other type" in {
        val json = Json.obj(
          "whatKindOfAsset" -> Partnership.toString
        )

        json.validate[AssetViewModel] mustEqual JsSuccess(
          DefaultAssetsViewModel(Partnership, InProgress)
        )
      }
    }
  }

}
