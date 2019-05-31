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

package models.reads

import models.entities.{Asset, MoneyAsset}
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

    }

    "must deserialise" - {

      "from a money asset" in {
        val json = Json.obj(
          "whatKindOfAsset" -> "Money",
          "assetMoneyValue" -> "4000"
        )

        json.validate[Asset] mustEqual JsSuccess(MoneyAsset("4000"))
      }
    }
  }

}
