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

package mapping.registration

import java.time.LocalDate

import mapping.TypeOfTrust.WillTrustOrIntestacyTrust
import mapping.{registration, _}
import models.core.UserAnswers
import models.registration.pages.KindOfTrust.{Deed, Employees, FlatManagement, HeritageMaintenanceFund, Intervivos}
import models.registration.pages.TrusteesBasedInTheUK.{InternationalAndUKTrustees, NonUkBasedTrustees, UKBasedTrustees}
import models.registration.pages.{DeedOfVariation, KindOfTrust, NonResidentType}
import pages.entitystatus.DeceasedSettlorStatus
import pages.register.settlors.living_settlor.trust_type.{EfrbsStartDatePage, HoldoverReliefYesNoPage, HowDeedOfVariationCreatedPage, KindOfTrustPage, SetUpInAdditionToWillTrustYesNoPage}
import pages.register.trust_details.{AgentOtherThanBarristerPage, SettlorsBasedInTheUKPage, _}
import play.api.Logger
import sections.settlors.LivingSettlors

class TrustDetailsMapper extends Mapping[TrustDetailsType] {

  private def trustType(userAnswers: UserAnswers): Option[TypeOfTrust] = {
    val settlors = (
      userAnswers.get(LivingSettlors),
      userAnswers.get(DeceasedSettlorStatus)
    )

    settlors match {
      case (Some(_), Some(_)) =>
        Logger.info("[TrustDetailsMapper] - Cannot build trust type for Deed of variation yet")
        None
      case (Some(_), None) =>
        userAnswers.get(KindOfTrustPage).map(mapTrustTypeToDes)
      case (None, Some(_)) =>
        Some(WillTrustOrIntestacyTrust)
      case (None, None) =>
        Logger.info("[TrustDetailsMapper] - Cannot build trust type due to no settlors")
        None
    }

  }

  private def deedOfVariation(userAnswers: UserAnswers): Option[DeedOfVariation] =
    userAnswers.get(HowDeedOfVariationCreatedPage) orElse (userAnswers.get(SetUpInAdditionToWillTrustYesNoPage) match {
      case Some(true) => Some(DeedOfVariation.AdditionToWill)
      case _ => None
    })

  private def mapTrustTypeToDes(kind: KindOfTrust): TypeOfTrust = {
    kind match {
      case Intervivos => TypeOfTrust.IntervivosSettlementTrust
      case Deed => TypeOfTrust.DeedOfVariation
      case Employees => TypeOfTrust.EmployeeRelated
      case FlatManagement => TypeOfTrust.FlatManagementTrust
      case HeritageMaintenanceFund => TypeOfTrust.HeritageTrust
    }
  }

  override def build(userAnswers: UserAnswers): Option[TrustDetailsType] = {
    for {
      typeOfTrust <- trustType(userAnswers)
    } yield {
      registration.TrustDetailsType(
        startDate = LocalDate.now,
        lawCountry = None,
        administrationCountry = None,
        residentialStatus = None,
        typeOfTrust = typeOfTrust,
        deedOfVariation = deedOfVariation(userAnswers),
        interVivos = userAnswers.get(HoldoverReliefYesNoPage),
        efrbsStartDate = userAnswers.get(EfrbsStartDatePage)
      )
    }
  }

}
