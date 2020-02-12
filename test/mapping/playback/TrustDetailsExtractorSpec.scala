/*
 * Copyright 2020 HM Revenue & Customs
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

package mapping.playback

import java.time.LocalDate

import base.SpecBaseHelpers
import generators.Generators
import mapping.DeedOfVariation.ReplacedWill
import mapping.TypeOfTrust.WillTrustOrIntestacyTrust
import mapping.registration.{NonUKType, ResidentialStatusType, TrustDetailsType, UkType}
import models.playback.UserAnswers
import models.registration.pages.NonResidentType
import models.registration.pages.NonResidentType.Domiciled
import org.scalatest.{EitherValues, FreeSpec, MustMatchers}
import pages.register._
import pages.register.agents.AgentOtherThanBarristerPage
import pages.register.trust_details.{AdministrationInsideUKPage, CountryAdministeringTrustPage, CountryGoverningTrustPage, EstablishedUnderScotsLawPage, GovernedInsideTheUKPage, InheritanceTaxActPage, NonResidentTypePage, RegisteringTrustFor5APage, TrustPreviouslyResidentPage, TrustResidentOffshorePage, WhenTrustSetupPage}

class TrustDetailsExtractorSpec extends FreeSpec with MustMatchers with EitherValues with Generators with SpecBaseHelpers {

  val trusteeDetailsExtractor : PlaybackExtractor[TrustDetailsType] =
    injector.instanceOf[TrustDetailsExtractor]

  "Trust Details Extractor" - {

    "when there is trust details" - {

      "uk" in {

        val trust = TrustDetailsType(
          startDate = LocalDate.of(2019, 6, 1),
          lawCountry = None,
          administrationCountry = Some("GB"),
          residentialStatus = Some(ResidentialStatusType(Some(UkType(true, None)), None)),
          typeOfTrust = WillTrustOrIntestacyTrust,
          deedOfVariation = Some(ReplacedWill),
          interVivos = Some(true),
          efrbsStartDate = Some(LocalDate.of(2018, 4, 20))
        )

        val ua = UserAnswers("fakeId")

        val extraction = trusteeDetailsExtractor.extract(ua, trust)

        extraction.right.value.get(WhenTrustSetupPage).get mustBe LocalDate.of(2019, 6, 1)
        extraction.right.value.get(GovernedInsideTheUKPage).get mustBe true
        extraction.right.value.get(CountryGoverningTrustPage) must not be defined
        extraction.right.value.get(AdministrationInsideUKPage).get mustBe true
        extraction.right.value.get(CountryAdministeringTrustPage) must not be defined
        extraction.right.value.get(EstablishedUnderScotsLawPage).get mustBe true
        extraction.right.value.get(TrustResidentOffshorePage).get mustBe false
        extraction.right.value.get(TrustPreviouslyResidentPage) must not be defined
      }

      "non uk" in {

        val trust = TrustDetailsType(
          startDate = LocalDate.of(2019, 6, 1),
          lawCountry = Some("FR"),
          administrationCountry = Some("IT"),
          residentialStatus = Some(ResidentialStatusType(None, Some(NonUKType(false, Some(false), Some(true), Some(NonResidentType.toDES(Domiciled)))))),
          typeOfTrust = WillTrustOrIntestacyTrust,
          deedOfVariation = Some(ReplacedWill),
          interVivos = Some(true),
          efrbsStartDate = Some(LocalDate.of(2018, 4, 20))
        )

        val ua = UserAnswers("fakeId")

        val extraction = trusteeDetailsExtractor.extract(ua, trust)

        extraction.right.value.get(WhenTrustSetupPage).get mustBe LocalDate.of(2019, 6, 1)
        extraction.right.value.get(GovernedInsideTheUKPage).get mustBe false
        extraction.right.value.get(CountryGoverningTrustPage).get mustBe "FR"
        extraction.right.value.get(AdministrationInsideUKPage).get mustBe false
        extraction.right.value.get(CountryAdministeringTrustPage).get mustBe "IT"
        extraction.right.value.get(RegisteringTrustFor5APage).get mustBe false
        extraction.right.value.get(NonResidentTypePage).get mustBe Domiciled
        extraction.right.value.get(InheritanceTaxActPage).get mustBe false
        extraction.right.value.get(AgentOtherThanBarristerPage).get mustBe true

      }

    }

  }

}
