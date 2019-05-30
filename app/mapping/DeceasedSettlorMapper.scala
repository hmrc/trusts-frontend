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

import mapping.TypeOfTrust.WillTrustOrIntestacyTrust
import models.{FullName, NonResidentType, UserAnswers}
import pages.{NonResidentTypePage, RegisteringTrustFor5APage, _}

class DeceasedSettlorMapper extends Mapping[WillType] {

  override def build(userAnswers: UserAnswers): Option[WillType] = {
    for {
      settlorsName <- settlorsNameMap(userAnswers)
      settlorsDateOfBirth = userAnswers.get(SettlorsDateOfBirthPage)
      settlorDateOfDeath = userAnswers.get(SettlorDateOfDeathPage)
      settlorIdentification = identificationStatus(userAnswers)
    } yield {
      WillType(
        name = settlorsName,
        dateOfBirth = settlorsDateOfBirth,
        dateOfDeath = settlorDateOfDeath,
        identification = settlorIdentification
      )
    }
  }

  private def settlorsNameMap(userAnswers: UserAnswers): Option[NameType] = {
    val settlorName: Option[FullName] = userAnswers.get(SettlorsNamePage)
    settlorName.map{
      name =>
        NameType(
          firstName = name.firstName,
          middleName = name.middleName,
          lastName = name.lastName)
    }
  }

  private def identificationStatus(userAnswers: UserAnswers): Option[Identification] = {
    val settlorNinoYesNo = userAnswers.get(SettlorsNINoYesNoPage)
    val settlorsLastKnownAddressYesNo = userAnswers.get(SettlorsLastKnownAddressYesNoPage)
    (settlorNinoYesNo.isDefined, settlorsLastKnownAddressYesNo.isDefined) match {
      case (true, _) => ninoMap(userAnswers)
      case (false, true) => addressMap(userAnswers)
      case (_, _) => None
    }
  }

  private def ninoMap(userAnswers: UserAnswers): Option[Identification] = {
    val settlorNino = userAnswers.get(SettlorNationalInsuranceNumberPage)
    settlorNino.map {
      nino => Identification(
        nino = settlorNino,
        address = None)
    }
  }

  private def addressMap(userAnswers: UserAnswers): Option[Identification] = {
    val wasSettlorsAddressUKYesNo = userAnswers.get(WasSettlorsAddressUKYesNoPage)
    settlorNino.map {
      nino => Identification(
        nino = settlorNino,
        address = None)
    }
  }

//  private def administrationCountry(userAnswers: UserAnswers): Option[String] = {
//    userAnswers.get(AdministrationInsideUKPage) match {
//      case Some(true) =>
//        Some("GB")
//      case Some(false) =>
//        userAnswers.get(CountryAdministeringTrustPage)
//      case None => None
//    }
//  }
//
//  private def residentialStatus(userAnswers: UserAnswers): Option[ResidentialStatusType] = {
//
//
//    userAnswers.get(TrustResidentInUKPage) match {
//      case Some(true) =>
//        ukResidentMap(userAnswers)
//      case Some(false) =>
//        nonUkResidentMap(userAnswers)
//      case _ =>
//        None
//    }
//  }
//
//  private def nonUkResidentMap(userAnswers: UserAnswers) = {
//    val registeringTrustFor5A = userAnswers.get(RegisteringTrustFor5APage)
//    val nonResidentTypePage = userAnswers.get(NonResidentTypePage)
//    val nonResTypeDES = nonResidentTypePage.map(NonResidentType.toDES)
//
//    val nonUKConstruct: Option[NonUKType] = (registeringTrustFor5A, nonResTypeDES) match {
//      case (Some(true), r@Some(_)) =>
//        Some(
//          NonUKType(
//            sch5atcgga92 = true,
//            s218ihta84 = None,
//            agentS218IHTA84 = None,
//            trusteeStatus = r)
//        )
//
//      case (Some(false), None) =>
//        inheritanceTaxAndAgentBarristerMap(userAnswers)
//
//      case (_, _) => None
//    }
//
//    nonUKConstruct match {
//      case x if x.isDefined =>
//        Some(ResidentialStatusType(None, x)
//        )
//      case _ => None
//    }
//  }
//
//  private def ukResidentMap(userAnswers: UserAnswers) = {
//    val scotsLaw = userAnswers.get(EstablishedUnderScotsLawPage)
//    val trustOffShoreYesNo = userAnswers.get(TrustResidentOffshorePage)
//    val trustOffShoreCountry = userAnswers.get(TrustPreviouslyResidentPage)
//    scotsLaw.map {
//      scots =>
//        ResidentialStatusType(
//          uk = Some(UkType(
//            scottishLaw = scots,
//            preOffShore = trustOffShoreYesNo match {
//              case Some(true) => trustOffShoreCountry
//              case _ => None
//            }
//          )),
//          nonUK = None
//        )
//    }
//  }
//
//  private def inheritanceTaxAndAgentBarristerMap(userAnswers: UserAnswers): Option[NonUKType] = {
//    val s218ihta84 = userAnswers.get(InheritanceTaxActPage)
//    val agentS218IHTA84 = userAnswers.get(AgentOtherThanBarristerPage)
//
//    s218ihta84 match {
//      case Some(_) =>
//        Some(
//          NonUKType(
//            sch5atcgga92 = false,
//            s218ihta84 = s218ihta84,
//            agentS218IHTA84 = agentS218IHTA84,
//            trusteeStatus = None)
//        )
//
//      case _ => None
//    }
//  }

}
