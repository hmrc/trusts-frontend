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

package generators

import models.FirstTaxYearAvailable
import models.core.pages._
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Arbitrary, Gen}

import java.time.LocalDate

trait ModelGenerators {

  implicit lazy val arbitraryFullName: Arbitrary[FullName] = {
    Arbitrary {
      for {
        str <- arbitrary[String]
      } yield {
        FullName(str, Some(str), str)
      }
    }
  }

  implicit lazy val arbitraryDeclaration: Arbitrary[Declaration] = {
    Arbitrary {
      for {
        str <- arbitrary[String]
      } yield {
        models.core.pages.Declaration(FullName(str, Some(str), str), Some(str))
      }
    }
  }

  implicit lazy val arbitraryLocalDate: Arbitrary[LocalDate] =
    Arbitrary {
      Gen.const(LocalDate.of(2010, 10, 10))
    }
    
  implicit lazy val arbitraryFirstTaxYearAvailable: Arbitrary[FirstTaxYearAvailable] = {
    Arbitrary {
      for {
        yearsAgo <- arbitrary[Int]
        earlierYearsToDeclare <- arbitrary[Boolean]
      } yield {
        FirstTaxYearAvailable(yearsAgo, earlierYearsToDeclare)
      }
    }
  }

}
