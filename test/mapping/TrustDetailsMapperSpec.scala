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

package mapping

import java.time.LocalDate

import base.SpecBaseHelpers
import generators.Generators
import mapping.TypeOfTrust.WillTrustOrIntestacyTrust
import org.scalatest.{FreeSpec, MustMatchers, OptionValues}
import pages._

class TrustDetailsMapperSpec extends FreeSpec with MustMatchers
  with OptionValues with Generators with SpecBaseHelpers {

  val trustDetailsMapper : Mapping[TrustDetailsType] = injector.instanceOf[TrustDetailsMapper]

  "TrustDetailsMapper" - {

    "when user answers is empty" - {

      "must not be able to create TrustDetails" in {

        val userAnswers = emptyUserAnswers

        trustDetailsMapper.build(userAnswers) mustNot be(defined)
      }

      "must able to create TrustDetails for a UK resident trust" in {
        val date = LocalDate.of(2010, 10, 10)

        val userAnswers =
          emptyUserAnswers
            .set(TrustNamePage, "New Trust").success.value
            .set(WhenTrustSetupPage, date).success.value
            .set(GovernedInsideTheUKPage, true).success.value
            .set(AdministrationInsideUKPage, true).success.value
            .set(TrustResidentInUKPage, true).success.value
            .set(EstablishedUnderScotsLawPage, true).success.value
            .set(TrustResidentOffshorePage, false).success.value

        trustDetailsMapper.build(userAnswers).value mustBe TrustDetailsType(
          startDate = date,
          lawCountry = None,
          administrationCountry = Some("GB"),
          residentialStatus = Some(ResidentialStatusType(
            uk = Some(
              UkType(
                scottishLaw = true,
                preOffShore = None
              )
            ),
            nonUK = None
          )),
          typeOfTrust = WillTrustOrIntestacyTrust,
          deedOfVariation = None,
          interVivos = None,
          efrbsStartDate = None
        )

      }

      "must able to create TrustDetails for a UK resident trust with trust previously resident offshore " in {
        val date = LocalDate.of(2010, 10, 10)

        val userAnswers =
          emptyUserAnswers
            .set(TrustNamePage, "New Trust").success.value
            .set(WhenTrustSetupPage, date).success.value
            .set(GovernedInsideTheUKPage, true).success.value
            .set(AdministrationInsideUKPage, true).success.value
            .set(TrustResidentInUKPage, true).success.value
            .set(EstablishedUnderScotsLawPage, true).success.value
            .set(TrustResidentOffshorePage, true).success.value
            .set(TrustPreviouslyResidentPage, "FR").success.value

        trustDetailsMapper.build(userAnswers).value mustBe TrustDetailsType(
          startDate = date,
          lawCountry = None,
          administrationCountry = Some("GB"),
          residentialStatus = Some(ResidentialStatusType(
            uk = Some(
              UkType(
                scottishLaw = true,
                preOffShore = Some("FR")
              )
            ),
            nonUK = None
          )),
          typeOfTrust = WillTrustOrIntestacyTrust,
          deedOfVariation = None,
          interVivos = None,
          efrbsStartDate = None
        )

      }

    }

  }

}
