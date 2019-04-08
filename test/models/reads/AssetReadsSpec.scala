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

import models.WhatKindOfAsset.Money
import models.entities.Asset
import org.scalatest.{MustMatchers, WordSpec}
import play.api.libs.json.Json

class AssetReadsSpec extends WordSpec with MustMatchers {

  val emptyJson = Json.parse("{}")

  val moneyJson = Json.parse(
    """
      |{
      | "whatKindOfAsset": "Money",
      | "assetMoneyValue" : "4000"
      |}
    """.stripMargin
  )

  "Assets" when {

    "type of asset is answered" must {

      "serialise money asset type" in {
        val result = moneyJson.as[Asset]
        result mustBe Asset(Some(Money), Some("4000"))
      }

    }

    "type of asset is not answered" must {

      "serialise an empty object" in {
        val result = emptyJson.as[Asset]
        result mustBe Asset(None,None)
      }

    }

  }

}
