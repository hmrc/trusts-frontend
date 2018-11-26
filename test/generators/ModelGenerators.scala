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

import models._
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Arbitrary, Gen}

trait ModelGenerators {

  implicit lazy val arbitraryInternationalTrustsAddress: Arbitrary[InternationalAddress] =
    Arbitrary {
      for {
        line1 <- arbitrary[String]
        line2 <- arbitrary[String]
        line3 <- arbitrary[String]
        country <- arbitrary[String]
      } yield InternationalAddress(line1, line1, Some(line3), country)
    }

  implicit lazy val arbitraryTrustAddressUK: Arbitrary[AddressUK] =
    Arbitrary {
      for {
        line1 <- arbitrary[String]
        line2 <- arbitrary[String]
        line3 <- arbitrary[String]
        town <- arbitrary[String]
        postcode <- arbitrary[String]
      } yield AddressUK(line1, Some(line2), Some(line3), town, postcode)
    }
}
