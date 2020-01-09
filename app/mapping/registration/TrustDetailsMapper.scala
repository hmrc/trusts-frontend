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

import mapping.TypeOfTrust.WillTrustOrIntestacyTrust
import mapping.{registration, _}
import models.core.UserAnswers
import models.registration.pages.KindOfTrust.{Deed, Employees, FlatManagement, HeritageMaintenanceFund, Intervivos}
import models.registration.pages.TrusteesBasedInTheUK.{InternationalAndUKTrustees, NonUkBasedTrustees, UKBasedTrustees}
import models.registration.pages.{KindOfTrust, NonResidentType, WhenTrustSetupPage}
import pages.entitystatus.DeceasedSettlorStatus
import pages.register._
import pages.register.agents.AgentOtherThanBarristerPage
import pages.register.settlors.SettlorsBasedInTheUKPage
import pages.register.settlors.living_settlor.trust_type.{HoldoverReliefYesNoPage, KindOfTrustPage}
import pages.register.trustees.TrusteesBasedInTheUKPage
import play.api.Logger
import sections.LivingSettlors

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
      startDateOption <- userAnswers.get(WhenTrustSetupPage)
      lawCountry = userAnswers.get(CountryGoverningTrustPage)
      administrationCountryOption <- administrationCountry(userAnswers)
      residentialStatusOption <- residentialStatus(userAnswers)
      typeOfTrust <- trustType(userAnswers)
    } yield {
      registration.TrustDetailsType(
        startDate = startDateOption,
        lawCountry = lawCountry,
        administrationCountry = Some(administrationCountryOption),
        residentialStatus = Some(residentialStatusOption),
        typeOfTrust = typeOfTrust,
        deedOfVariation = None,
        interVivos = userAnswers.get(HoldoverReliefYesNoPage),
        efrbsStartDate = None
      )
    }
  }

  private def administrationCountry(userAnswers: UserAnswers): Option[String] = {
    userAnswers.get(AdministrationInsideUKPage) match {
      case Some(true) =>
        Some("GB")
      case Some(false) =>
        userAnswers.get(CountryAdministeringTrustPage)
      case None =>
        Logger.info(s"[TrustDetailsMapper][build] unable to determine where trust is administered")
        None
    }
  }
  
  private def residentialStatus(userAnswers: UserAnswers): Option[ResidentialStatusType] = {
    userAnswers.get(TrusteesBasedInTheUKPage) match {
      case Some(UKBasedTrustees) =>
        ukResidentMap(userAnswers)
      case Some(NonUkBasedTrustees) =>
        nonUkResidentMap(userAnswers)
      case Some(InternationalAndUKTrustees) =>
        userAnswers.get(SettlorsBasedInTheUKPage) match {
          case Some(true) =>
            ukResidentMap(userAnswers)
          case Some(false) =>
            nonUkResidentMap(userAnswers)
          case  _ =>
            Logger.info("[TrustDetailsMapper][build] unable to determine if all settlors are based in the UK")
            None
        }
      case _ =>
        Logger.info(s"[TrustDetailsMapper][build] unable to determine where trust is resident")
        None
    }
  }

  private def nonUkResidentMap(userAnswers: UserAnswers) = {
    val registeringTrustFor5A = userAnswers.get(RegisteringTrustFor5APage)
    val nonResidentTypePage = userAnswers.get(NonResidentTypePage)
    val nonResTypeDES = nonResidentTypePage.map(NonResidentType.toDES)

    val nonUKConstruct: Option[NonUKType] = (registeringTrustFor5A, nonResTypeDES) match {
      case (Some(true), r@Some(_)) =>
        Some(
          NonUKType(
            sch5atcgga92 = true,
            s218ihta84 = None,
            agentS218IHTA84 = None,
            trusteeStatus = r)
        )

      case (Some(false), None) =>
        inheritanceTaxAndAgentBarristerMap(userAnswers)

      case (_, _) =>
        Logger.info(s"[TrustDetailsMapper][build] unable to build non UK resident or inheritance")
        None
    }

    nonUKConstruct match {
      case x if x.isDefined =>
        Some(ResidentialStatusType(None, x)
        )
      case _ =>
        Logger.info(s"[TrustDetailsMapper][build] unable to create residential status")
        None
    }
  }

  private def ukResidentMap(userAnswers: UserAnswers) = {
    val scotsLaw = userAnswers.get(EstablishedUnderScotsLawPage)
    val trustOffShoreYesNo = userAnswers.get(TrustResidentOffshorePage)
    val trustOffShoreCountry = userAnswers.get(TrustPreviouslyResidentPage)
    scotsLaw.map {
      scots =>
        ResidentialStatusType(
          uk = Some(UkType(
            scottishLaw = scots,
            preOffShore = trustOffShoreYesNo match {
              case Some(true) => trustOffShoreCountry
              case _ => None
            }
          )),
          nonUK = None
        )
    }
  }

  private def inheritanceTaxAndAgentBarristerMap(userAnswers: UserAnswers): Option[NonUKType] = {
    val s218ihta84 = userAnswers.get(InheritanceTaxActPage)
    val agentS218IHTA84 = userAnswers.get(AgentOtherThanBarristerPage)

    s218ihta84 match {
      case Some(_) =>
        Some(
          NonUKType(
            sch5atcgga92 = false,
            s218ihta84 = s218ihta84,
            agentS218IHTA84 = agentS218IHTA84,
            trusteeStatus = None)
        )

      case _ => None
    }
  }
}
