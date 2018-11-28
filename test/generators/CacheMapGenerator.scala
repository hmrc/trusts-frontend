/*
 * Copyright 2018 HM Revenue & Customs
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

import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Arbitrary, Gen}
import pages._
import play.api.libs.json.JsValue
import uk.gov.hmrc.http.cache.client.CacheMap

trait CacheMapGenerator {
  self: Generators =>

  val generators: Seq[Gen[(Page, JsValue)]] =
    arbitrary[(TrustsAddressInternationalPage.type, JsValue)] ::
    arbitrary[(TrustAddressUKPage.type, JsValue)] ::
    arbitrary[(TrustNamePage.type, JsValue)] ::
    arbitrary[(TrustAddressUKYesNoPage.type, JsValue)] ::
    Nil

  implicit lazy val arbitraryCacheMap: Arbitrary[CacheMap] =
    Arbitrary {
      for {
        cacheId <- nonEmptyString
        data    <- generators match {
          case Nil => Gen.const(Map[Page, JsValue]())
          case _   => Gen.mapOf(oneOf(generators))
        }
      } yield CacheMap(
        cacheId,
        data.map {
          case (k, v) => ( k.toString, v )
        }
      )
    }
}
