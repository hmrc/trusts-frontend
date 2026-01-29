/*
 * Copyright 2024 HM Revenue & Customs
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

package utils

import models.core.{Settlor, Settlors}
import play.api.Logging
import play.api.libs.json.{JsArray, JsObject, JsSuccess, JsValue, __}

object JsonTransformers extends Logging {

  def removeAliveAtRegistrationFromJson(registrationPieces: JsObject): Option[JsValue] =
    registrationPieces
      .transform(
        (__ \ "trust/entities/settlors").json.pick.andThen(
          (__ \ "settlor").json
            .update(
              __.read[JsArray].map { case JsArray(elements: Iterable[JsValue]) =>
                JsArray {
                  elements.map { case JsObject(p) =>
                    JsObject(p.toMap - "aliveAtRegistration")
                  }
                }
              }
            )
        )
      )
      .asOpt

  def checkIfAliveAtRegistrationFieldPresent(registrationPieces: JsObject): Boolean =
    registrationPieces
      .transform(
        (__ \ "trust/entities/settlors").json.pick
      ) match {
      case JsSuccess(value, _) =>
        value
          .as[Settlors]
          .settlor
          .getOrElse(List.empty[Settlor])
          .map(_.aliveAtRegistration.isDefined)
          .exists(identity)
      case _                   =>
        logger.info(s"[checkIfAliveAtRegistrationFieldPresent]: trust/entities/settlors not found in reg Json.")
        false
    }

}
