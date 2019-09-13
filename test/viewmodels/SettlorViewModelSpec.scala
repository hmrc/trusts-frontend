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
import models.IndividualOrBusiness.{Business, Individual}
import org.scalatest.prop.PropertyChecks
import org.scalatest.{FreeSpec, MustMatchers}
import play.api.libs.json.{JsPath, JsSuccess, Json, KeyPathNode}
import viewmodels.addAnother._

class SettlorViewModelSpec extends FreeSpec with MustMatchers with PropertyChecks with Generators with ModelGenerators {

  "Settlor" - {

    "must deserialise" - {

      "living settlor individual with name" - {

        "to a view model that is complete" in {
          val json = Json.parse(
            """
              |{
              |"individualOrBusiness" : "individual",
              |"name": {
              | "firstName": "Richy",
              | "lastName": "Jassal"
              |},
              |"dateOfBirthYesNo" : false,
              |"ninoYesNo" : true,
              |"nino" : "NH111111A",
              |"status": "completed"
              |}
            """.stripMargin)

          json.validate[SettlorViewModel] mustEqual JsSuccess(
            SettlorLivingIndividualViewModel(`type` = Individual, name = "Richy Jassal", status = Completed)
          )
        }

      }

      "to default view model when no data provided" in {
        val json = Json.parse(
          """
            |{
            |"individualOrBusiness" : "individual"
            |}
          """.stripMargin)

        json.validate[SettlorViewModel] mustEqual JsSuccess(
          DefaultSettlorViewModel(`type` = Individual, status = InProgress)
        )
      }

    }
  }

}