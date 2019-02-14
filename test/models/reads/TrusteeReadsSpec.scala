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

import models.FullName
import models.TrusteeIndividualOrBusiness.Individual
import models.entities.Trustee
import org.scalatest.prop.PropertyChecks
import org.scalatest.{MustMatchers, WordSpec}
import play.api.libs.json.{JsObject, JsString, Json}

class TrusteeReadsSpec extends WordSpec with MustMatchers with PropertyChecks {

  val trusteeWithoutMiddleName: JsObject = Json.obj(
    "trusteeIndividualOrBusiness" -> JsString("individual"),
    "trusteesName" ->
      Json.obj(
      "firstName" -> JsString("First"),
      "lastName" -> JsString("Last")
      )
  )

  val trusteeWithMiddleName: JsObject = Json.obj(
    "trusteeIndividualOrBusiness" -> JsString("individual"),
    "trusteesName" ->
      Json.obj(
        "firstName" -> JsString("First"),
        "middleName" -> JsString("Middle"),
        "lastName" -> JsString("Last")
      )
  )

  val emptyTrustee: JsObject = Json.obj()


  "Trustee" must {

    "serialise trustee without middle name" in {
      val result = trusteeWithoutMiddleName.as[Trustee]

      result.`type` mustBe Some(Individual)
      result.name mustBe Some(FullName("First", None, "Last"))
    }

    "serialise trustee with middle name" in {
      val result = trusteeWithMiddleName.as[Trustee]

      result.`type` mustBe Some(Individual)
      result.name mustBe Some(FullName("First", Some("Middle"), "Last"))
    }

    "serialise an empty object to a trustee" in {
      val result = emptyTrustee.as[Trustee]

      result.`type` mustBe None
      result.name mustBe None
    }

  }

}
