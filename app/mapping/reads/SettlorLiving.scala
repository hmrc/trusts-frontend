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

import models.IndividualOrBusiness.Individual
import models.{FullName, IndividualOrBusiness}
import play.api.libs.json.{JsError, JsSuccess, Reads, __}

final case class SettlorLiving(override val individualOrBusiness: IndividualOrBusiness,
                               name: Option[FullName]) extends Settlor

object SettlorLiving {

  import play.api.libs.functional.syntax._

  implicit lazy val reads: Reads[SettlorLiving] = {

    val settlorLivingReads: Reads[SettlorLiving] = (
      (__ \ "individualOrBusiness").read[IndividualOrBusiness] and
        (__ \ "name").readNullable[FullName]

      )((individualOrBusiness, name) => SettlorLiving(individualOrBusiness, name))

    (__ \ "individualOrBusiness").read[IndividualOrBusiness].flatMap[IndividualOrBusiness] {
      individualOrBusiness: IndividualOrBusiness =>
        if (individualOrBusiness == Individual) {
          Reads(_ => JsSuccess(individualOrBusiness))
        } else {
          Reads(_ => JsError("SettlorIndividual must be of type `Individual`"))
        }
    }.andKeep(settlorLivingReads)

  }
}