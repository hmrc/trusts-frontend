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

package mapping

import models.{Enumerable, WithName}

sealed trait TypeOfTrust

object TypeOfTrust extends Enumerable.Implicits {

  case object WillTrustOrIntestacyTrust extends WithName("Will Trust or Intestacy Trust") with TypeOfTrust

  val values: Set[TypeOfTrust] = Set(
    WillTrustOrIntestacyTrust
  )

  implicit val enumerable: Enumerable[TypeOfTrust] =
    Enumerable(values.toSeq.map(v => v.toString -> v): _*)
}



