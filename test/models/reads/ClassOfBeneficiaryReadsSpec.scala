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

import generators.{Generators, ModelGenerators}
import models.Status.Completed
import models.entities.ClassOfBeneficiary
import org.scalatest.prop.PropertyChecks
import org.scalatest.{FreeSpec, MustMatchers}
import play.api.libs.json.{JsSuccess, Json}

class ClassOfBeneficiaryReadsSpec extends FreeSpec with MustMatchers with PropertyChecks with Generators with ModelGenerators {

  "ClassOfBeneficiary" - {

    "must deserialise" - {

      "from a class of beneficiary that's in progress" in {
          val json = Json.obj(
            "description" -> "Grandchildren",
            "status" -> Completed.toString
          )

          json.validate[ClassOfBeneficiary] mustEqual JsSuccess(ClassOfBeneficiary(Some("Grandchildren"), Completed))
      }

    }

  }

}
