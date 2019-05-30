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
import models.WhatKindOfAsset.{Money, PropertyOrLand}
import org.scalatest.prop.PropertyChecks
import org.scalatest.{FreeSpec, MustMatchers}
import play.api.libs.json.{JsSuccess, Json}
import viewmodels.addAnother.AssetViewModel

class AssetViewModelSpec extends FreeSpec with MustMatchers with PropertyChecks with Generators with ModelGenerators {

  "Asset" - {

    "must deserialise" - {

      "from a money asset to a view model that is not complete" in {

        val json = Json.obj(
          "whatKindOfAsset" -> Money.toString
        )

        json.validate[AssetViewModel] mustEqual JsSuccess(
          addAnother.MoneyAssetViewModel(Money, None, InProgress)
        )
      }

      "from a money asset to a view model that is complete" in {
          val json = Json.obj(
            "whatKindOfAsset" -> Money.toString,
            "assetMoneyValue" -> "4000",
            "status" -> Completed.toString
          )

          json.validate[AssetViewModel] mustEqual JsSuccess(
            addAnother.MoneyAssetViewModel(Money, Some("£4000"), Completed)
          )
      }

      "to a default from any other type" in {
        val json = Json.obj(
          "whatKindOfAsset" -> PropertyOrLand.toString
        )

        json.validate[AssetViewModel] mustEqual JsSuccess(
          addAnother.DefaultAssetsViewModel(PropertyOrLand, InProgress)
        )
      }
    }
  }

}
