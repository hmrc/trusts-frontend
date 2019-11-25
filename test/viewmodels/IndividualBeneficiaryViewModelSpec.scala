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
import models.core.pages.FullName
import models.registration.pages.Status.Completed
import org.scalatest.prop.PropertyChecks
import org.scalatest.{FreeSpec, MustMatchers}
import play.api.libs.json.{JsSuccess, Json}
import viewmodels.addAnother.IndividualBeneficiaryViewModel

class IndividualBeneficiaryViewModelSpec extends FreeSpec with MustMatchers with PropertyChecks with Generators with ModelGenerators  {

  "IndividualBeneficiary" - {

    "must deserialise" - {

      "from an individual beneficiary" in {
        val json = Json.obj(
          "name" -> Json.obj(
            "firstName" -> "First",
            "lastName" -> "Last"
          ),
          "status" -> Completed.toString
        )

        json.validate[IndividualBeneficiaryViewModel] mustEqual JsSuccess(
          IndividualBeneficiaryViewModel(
            Some(FullName("First", None, "Last")),
            Completed
          )
        )
      }

    }

  }

}
