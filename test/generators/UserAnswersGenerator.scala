/*
 * Copyright 2026 HM Revenue & Customs
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

package generators

import models.core.UserAnswers
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Arbitrary, Gen}
import org.scalatest.TryValues
import pages._
import pages.register._
import play.api.libs.json.{JsValue, Json}

trait UserAnswersGenerator extends TryValues {
  self: Generators =>

  import models.core.UserAnswerImplicits._

  val generators: Seq[Gen[(QuestionPage[_], JsValue)]] =
    arbitrary[(DeclarationPage.type, JsValue)] ::
      arbitrary[(PostcodeForTheTrustPage.type, JsValue)] ::
      arbitrary[(WhatIsTheUTRPage.type, JsValue)] ::
      arbitrary[(TrustHaveAUTRPage.type, JsValue)] ::
      arbitrary[(TrustRegisteredOnlinePage.type, JsValue)] ::
      Nil

  implicit lazy val arbitraryUserData: Arbitrary[UserAnswers] =
    Arbitrary {
      for {
        id         <- nonEmptyString
        data       <- generators match {
                        case Nil => Gen.const(Map[QuestionPage[_], JsValue]())
                        case _   => Gen.mapOf(oneOf(generators))
                      }
        internalId <- nonEmptyString
      } yield UserAnswers(
        draftId = id,
        data = data.foldLeft(Json.obj()) { case (obj, (path, value)) =>
          obj.setObject(path.path, value).get
        },
        internalAuthId = internalId
      )
    }

}
