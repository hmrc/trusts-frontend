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
import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary.arbitrary
import pages._
import play.api.libs.json.{JsValue, Json}

trait UserAnswersEntryGenerators extends PageGenerators with ModelGenerators {

  implicit lazy val arbitraryTelephoneNumberUserAnswersEntry: Arbitrary[(TelephoneNumberPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[TelephoneNumberPage]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitrarytrusteeAUKCitizenUserAnswersEntry: Arbitrary[(TrusteeAUKCitizenPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[TrusteeAUKCitizenPage]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryTrusteesNameUserAnswersEntry: Arbitrary[(TrusteesNamePage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[TrusteesNamePage]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryTrusteeIndividualOrBusinessUserAnswersEntry: Arbitrary[(TrusteeIndividualOrBusinessPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[TrusteeIndividualOrBusinessPage]
        value <- arbitrary[IndividualOrBusiness].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryIsThisLeadTrusteeUserAnswersEntry: Arbitrary[(IsThisLeadTrusteePage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[IsThisLeadTrusteePage]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryTrusteesDateOfBirthUserAnswersEntry: Arbitrary[(TrusteesDateOfBirthPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[TrusteesDateOfBirthPage]
        value <- arbitrary[Int].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryPostcodeForTheTrustUserAnswersEntry: Arbitrary[(PostcodeForTheTrustPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[PostcodeForTheTrustPage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryWhatIsTheUTRUserAnswersEntry: Arbitrary[(WhatIsTheUTRPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[WhatIsTheUTRPage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryTrustHaveAUTRUserAnswersEntry: Arbitrary[(TrustHaveAUTRPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[TrustHaveAUTRPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryTrustRegisteredOnlineUserAnswersEntry: Arbitrary[(TrustRegisteredOnlinePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[TrustRegisteredOnlinePage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryWhenTrustSetupUserAnswersEntry: Arbitrary[(WhenTrustSetupPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[WhenTrustSetupPage.type]
        value <- arbitrary[Int].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryAgentOtherThanBarristerUserAnswersEntry: Arbitrary[(AgentOtherThanBarristerPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[AgentOtherThanBarristerPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryInheritanceTaxActUserAnswersEntry: Arbitrary[(InheritanceTaxActPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[InheritanceTaxActPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryNonResidentTypeUserAnswersEntry: Arbitrary[(NonResidentTypePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[NonResidentTypePage.type]
        value <- arbitrary[NonResidentType].map(Json.toJson(_))
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

  implicit lazy val arbitraryAdministrationInsideUKUserAnswersEntry: Arbitrary[(AdministrationInsideUKPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[AdministrationInsideUKPage.type]
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

  implicit lazy val arbitraryGovernedInsideTheUKUserAnswersEntry: Arbitrary[(GovernedInsideTheUKPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[GovernedInsideTheUKPage.type]
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
