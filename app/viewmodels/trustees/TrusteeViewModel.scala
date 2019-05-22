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

package viewmodels.trustees

import models.{FullName, IndividualOrBusiness}
import viewmodels.Tag.InProgress
import viewmodels.{Tag, trustees}

final case class TrusteeViewModel(isLead : Boolean,
                                    name : Option[FullName],
                                  `type` : Option[IndividualOrBusiness],
                                  status : Tag)

object TrusteeViewModel {

  import play.api.libs.functional.syntax._
  import play.api.libs.json._

  implicit lazy val reads : Reads[TrusteeViewModel] = (
    (__ \ "isThisLeadTrustee").readWithDefault[Boolean](false) and
      (__ \ "individualOrBusiness").readNullable[IndividualOrBusiness] and
      (__ \ "name").readNullable[FullName] and
      (__ \ "status").readWithDefault[Tag](InProgress)
    )(
      (isLead, individualOrBusiness, name, status) => {
        trustees.TrusteeViewModel(isLead, name, individualOrBusiness, status)
      }
    )

}