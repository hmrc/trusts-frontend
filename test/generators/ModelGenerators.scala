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

package generators

import models._
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Arbitrary, Gen}

trait ModelGenerators {

  implicit lazy val arbitraryTrusteesUkAddress: Arbitrary[TrusteesUkAddress] =
    Arbitrary {
      for {
        field1 <- arbitrary[String]
        field2 <- arbitrary[String]
      } yield TrusteesUkAddress(field1, field2)
    }

  implicit lazy val arbitraryAddATrustee: Arbitrary[AddATrustee] =
    Arbitrary {
      Gen.oneOf(AddATrustee.values.toSeq)
    }

  implicit lazy val arbitraryTrusteeOrIndividual: Arbitrary[IndividualOrBusiness] =
    Arbitrary {
      Gen.oneOf(IndividualOrBusiness.values.toSeq)
    }

  implicit lazy val arbitraryNonResidentType: Arbitrary[NonResidentType] =
    Arbitrary {
      Gen.oneOf(NonResidentType.values.toSeq)
    }

}
