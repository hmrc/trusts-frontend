/*
 * Copyright 2020 HM Revenue & Customs
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

package viewmodels.addAnother

import models.core.pages.IndividualOrBusiness
import models.core.pages.IndividualOrBusiness.Business
import models.registration.pages.Status
import models.registration.pages.Status.InProgress

sealed trait SettlorBusinessViewModel extends SettlorViewModel

final case class SettlorBusinessTypeViewModel(`type` : IndividualOrBusiness,
                                                  name : String,
                                                  override val status : Status) extends SettlorBusinessViewModel
object SettlorBusinessViewModel {

  import play.api.libs.functional.syntax._
  import play.api.libs.json._

  implicit lazy val reads: Reads[SettlorBusinessViewModel] = {

    val businessNameReads: Reads[SettlorBusinessViewModel] =
    {
      (__ \ "individualOrBusiness").read[IndividualOrBusiness].filter(x => x == Business).flatMap { _ =>
        ((__ \ "name").read[String] and
          (__ \ "status").readWithDefault[Status](InProgress)
          ) ((name, status) => {
          SettlorBusinessTypeViewModel(
            Business,
            name,
            status)
        })
      }
    }

    businessNameReads

  }

}