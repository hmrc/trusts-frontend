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
import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary.arbitrary
import pages._
import play.api.libs.json.{JsValue, Json}

trait UserAnswersEntryGenerators extends PageGenerators with ModelGenerators {

  implicit lazy val arbitraryInheritanceTaxActUserAnswersEntry: Arbitrary[(InheritanceTaxActPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[InheritanceTaxActPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryNon-residentTypeUserAnswersEntry: Arbitrary[(Non-residentTypePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[Non-residentTypePage.type]
        value <- arbitrary[Non-residentType].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryTrustPreviouslyResidentUserAnswersEntry: Arbitrary[(TrustPreviouslyResidentPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[TrustPreviouslyResidentPage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryTrustResidentOffshoreUserAnswersEntry: Arbitrary[(TrustResidentOffshorePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[TrustResidentOffshorePage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryRegisteringTrustFor5AUserAnswersEntry: Arbitrary[(RegisteringTrustFor5APage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[RegisteringTrustFor5APage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryEstablishedUnderScotsLawUserAnswersEntry: Arbitrary[(EstablishedUnderScotsLawPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[EstablishedUnderScotsLawPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryTrustResidentInUKUserAnswersEntry: Arbitrary[(TrustResidentInUKPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[TrustResidentInUKPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryCountryAdministeringTrustUserAnswersEntry: Arbitrary[(CountryAdministeringTrustPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[CountryAdministeringTrustPage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryAdministrationOutsideUKUserAnswersEntry: Arbitrary[(AdministrationOutsideUKPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[AdministrationOutsideUKPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryCountryGoverningTrustUserAnswersEntry: Arbitrary[(CountryGoverningTrustPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[CountryGoverningTrustPage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryGovernedOutsideTheUKUserAnswersEntry: Arbitrary[(GovernedOutsideTheUKPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[GovernedOutsideTheUKPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryTrustNameUserAnswersEntry: Arbitrary[(TrustNamePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[TrustNamePage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }
}
