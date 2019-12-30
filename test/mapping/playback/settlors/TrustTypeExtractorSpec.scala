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

package mapping.playback.settlors

import java.time.LocalDate

import base.SpecBaseHelpers
import generators.Generators
import mapping.{DeedOfVariation, TypeOfTrust}
import mapping.playback.PlaybackExtractor
import mapping.registration.TrustDetailsType
import models.playback.http._
import models.playback.UserAnswers
import models.registration.pages.SettlorKindOfTrust
import org.scalatest.{EitherValues, FreeSpec, MustMatchers}
import pages.register.settlors.{SettlorAdditionToWillTrustYesNoPage, SettlorHowDeedOfVariationCreatedPage}
import pages.register.settlors.living_settlor._

class TrustTypeExtractorSpec extends FreeSpec with MustMatchers
  with EitherValues with Generators with SpecBaseHelpers {


  val trustTypeExtractor : PlaybackExtractor[Option[DisplayTrust]] =
    injector.instanceOf[TrustTypeExtractor]

  "Trust Type Extractor" - {

      "when no trust type" - {

        "must return user answers" in {

          val trusts = None

          val ua = UserAnswers("fakeId")

          val extraction = trustTypeExtractor.extract(ua, trusts)

          extraction mustBe 'left

        }

      }

      "when there is a trust type of 'Deed of Variation Trust or Family Arrangement' in addition to a Will Trust" - {

        "with minimum data must return user answers updated" in {

          val trust = DisplayTrust(
            details = TrustDetailsType(
            startDate = LocalDate.parse("1970-02-01"),
            lawCountry = None,
            administrationCountry = None,
            residentialStatus = None,
            typeOfTrust = TypeOfTrust.DeedOfVariation,
            deedOfVariation = Some("Addition to the will trust"),
            interVivos = None,
            efrbsStartDate = None),
            entities = DisplayTrustEntitiesType(None,
              DisplayTrustBeneficiaryType(None, None, None, None, None, None, None),
              None,
              DisplayTrustLeadTrusteeType(None, None),
              None,
              None,
              None
            ),
            assets = DisplayTrustAssets(None, None, None, None, None, None)
          )

          val ua = UserAnswers("fakeId")

          val extraction = trustTypeExtractor.extract(ua, Some(trust))

          extraction.right.value.get(SettlorKindOfTrustPage).get mustBe SettlorKindOfTrust.Deed
          extraction.right.value.get(SettlorAdditionToWillTrustYesNoPage).get mustBe true
          extraction.right.value.get(SettlorHowDeedOfVariationCreatedPage) mustNot be(defined)

        }

      }

    "when there is a trust type of 'Deed of Variation Trust or Family Arrangement' not in addition to a Will Trust" - {

      "with minimum data must return user answers updated" in {

        val trust = DisplayTrust(
          details = TrustDetailsType(
            startDate = LocalDate.parse("1970-02-01"),
            lawCountry = None,
            administrationCountry = None,
            residentialStatus = None,
            typeOfTrust = TypeOfTrust.DeedOfVariation,
            deedOfVariation = Some("Replaced the will trust"),
            interVivos = None,
            efrbsStartDate = None),
          entities = DisplayTrustEntitiesType(None,
            DisplayTrustBeneficiaryType(None, None, None, None, None, None, None),
            None,
            DisplayTrustLeadTrusteeType(None, None),
            None,
            None,
            None
          ),
          assets = DisplayTrustAssets(None, None, None, None, None, None)
        )

        val ua = UserAnswers("fakeId")

        val extraction = trustTypeExtractor.extract(ua, Some(trust))

        extraction.right.value.get(SettlorKindOfTrustPage).get mustBe SettlorKindOfTrust.Deed
        extraction.right.value.get(SettlorAdditionToWillTrustYesNoPage).get mustBe false
        extraction.right.value.get(SettlorHowDeedOfVariationCreatedPage).get mustBe DeedOfVariation.ReplacedWill

      }

    }

    "when there is a trust type of 'Inter vivos Settlement'" - {

      "with minimum data must return user answers updated" in {

        val trust = DisplayTrust(
          details = TrustDetailsType(
            startDate = LocalDate.parse("1970-02-01"),
            lawCountry = None,
            administrationCountry = None,
            residentialStatus = None,
            typeOfTrust = TypeOfTrust.IntervivosSettlementTrust,
            deedOfVariation = None,
            interVivos = Some(true),
            efrbsStartDate = None),
          entities = DisplayTrustEntitiesType(None,
            DisplayTrustBeneficiaryType(None, None, None, None, None, None, None),
            None,
            DisplayTrustLeadTrusteeType(None, None),
            None,
            None,
            None
          ),
          assets = DisplayTrustAssets(None, None, None, None, None, None)
        )

        val ua = UserAnswers("fakeId")

        val extraction = trustTypeExtractor.extract(ua, Some(trust))

        extraction.right.value.get(SettlorKindOfTrustPage).get mustBe SettlorKindOfTrust.Intervivos
        extraction.right.value.get(SettlorHandoverReliefYesNoPage).get mustBe true
        extraction.right.value.get(SettlorAdditionToWillTrustYesNoPage) mustNot be(defined)
        extraction.right.value.get(SettlorHowDeedOfVariationCreatedPage) mustNot be(defined)

      }

    }

    "when there is a trust type of 'Employment Related'" - {

      "with minimum data must return user answers updated" in {

        val trust = DisplayTrust(
          details = TrustDetailsType(
            startDate = LocalDate.parse("1970-02-01"),
            lawCountry = None,
            administrationCountry = None,
            residentialStatus = None,
            typeOfTrust = TypeOfTrust.EmployeeRelated,
            deedOfVariation = None,
            interVivos = None,
            efrbsStartDate = None),
          entities = DisplayTrustEntitiesType(None,
            DisplayTrustBeneficiaryType(None, None, None, None, None, None, None),
            None,
            DisplayTrustLeadTrusteeType(None, None),
            None,
            None,
            None
          ),
          assets = DisplayTrustAssets(None, None, None, None, None, None)
        )

        val ua = UserAnswers("fakeId")

        val extraction = trustTypeExtractor.extract(ua, Some(trust))

        extraction.right.value.get(SettlorKindOfTrustPage).get mustBe SettlorKindOfTrust.Employees
        extraction.right.value.get(SettlorHandoverReliefYesNoPage) mustNot be(defined)
        extraction.right.value.get(SettlorAdditionToWillTrustYesNoPage) mustNot be(defined)
        extraction.right.value.get(SettlorHowDeedOfVariationCreatedPage) mustNot be(defined)

      }

    }

    "when there is a trust type of 'Flat Management Company or Sinking Fund'" - {

      "with minimum data must return user answers updated" in {

        val trust = DisplayTrust(
          details = TrustDetailsType(
            startDate = LocalDate.parse("1970-02-01"),
            lawCountry = None,
            administrationCountry = None,
            residentialStatus = None,
            typeOfTrust = TypeOfTrust.FlatManagementTrust,
            deedOfVariation = None,
            interVivos = None,
            efrbsStartDate = None),
          entities = DisplayTrustEntitiesType(None,
            DisplayTrustBeneficiaryType(None, None, None, None, None, None, None),
            None,
            DisplayTrustLeadTrusteeType(None, None),
            None,
            None,
            None
          ),
          assets = DisplayTrustAssets(None, None, None, None, None, None)
        )

        val ua = UserAnswers("fakeId")

        val extraction = trustTypeExtractor.extract(ua, Some(trust))

        extraction.right.value.get(SettlorKindOfTrustPage).get mustBe SettlorKindOfTrust.FlatManagement
        extraction.right.value.get(SettlorHandoverReliefYesNoPage) mustNot be(defined)
        extraction.right.value.get(SettlorAdditionToWillTrustYesNoPage) mustNot be(defined)
        extraction.right.value.get(SettlorHowDeedOfVariationCreatedPage) mustNot be(defined)

      }

    }

    "when there is a trust type of 'Heritage Maintenance Fund'" - {

      "with minimum data must return user answers updated" in {

        val trust = DisplayTrust(
          details = TrustDetailsType(
            startDate = LocalDate.parse("1970-02-01"),
            lawCountry = None,
            administrationCountry = None,
            residentialStatus = None,
            typeOfTrust = TypeOfTrust.HeritageTrust,
            deedOfVariation = None,
            interVivos = None,
            efrbsStartDate = None),
          entities = DisplayTrustEntitiesType(None,
            DisplayTrustBeneficiaryType(None, None, None, None, None, None, None),
            None,
            DisplayTrustLeadTrusteeType(None, None),
            None,
            None,
            None
          ),
          assets = DisplayTrustAssets(None, None, None, None, None, None)
        )

        val ua = UserAnswers("fakeId")

        val extraction = trustTypeExtractor.extract(ua, Some(trust))

        extraction.right.value.get(SettlorKindOfTrustPage).get mustBe SettlorKindOfTrust.HeritageMaintenanceFund
        extraction.right.value.get(SettlorHandoverReliefYesNoPage) mustNot be(defined)
        extraction.right.value.get(SettlorAdditionToWillTrustYesNoPage) mustNot be(defined)
        extraction.right.value.get(SettlorHowDeedOfVariationCreatedPage) mustNot be(defined)

      }

    }

  }

}
