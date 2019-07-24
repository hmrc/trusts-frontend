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

package pages

import models.WhatKindOfAsset.Money
import models.{UserAnswers, WhatKindOfAsset}
import models.entities.Assets
import play.api.libs.json.JsPath

import scala.util.Try


final case class WhatKindOfAssetPage(index: Int) extends QuestionPage[WhatKindOfAsset] {

  override def path: JsPath = JsPath \ Assets \ index \ toString

  override def toString: String = "whatKindOfAsset"

//  override def cleanup(value: Option[WhatKindOfAsset], userAnswers: UserAnswers): Try[UserAnswers] = {
//    value match {
//      case Some(Money) =>
////        userAnswers.remove()
//      case _ => super.cleanup(value, userAnswers)
//    }
//  }
}
