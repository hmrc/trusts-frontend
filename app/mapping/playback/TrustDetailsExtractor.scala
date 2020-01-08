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

import com.google.inject.Inject
import mapping.playback.PlaybackExtractionErrors.{FailedToExtractData, PlaybackExtractionError}
import mapping.registration.{NonUKType, ResidentialStatusType, TrustDetailsType, UkType}
import models.playback.UserAnswers
import models.registration.pages.NonResidentType
import pages.register._
import pages.register.agents.AgentOtherThanBarristerPage
import play.api.Logger

import scala.util.{Failure, Success, Try}

class TrustDetailsExtractor @Inject() extends PlaybackExtractor[Option[TrustDetailsType]] {

  override def extract(answers: UserAnswers, data: Option[TrustDetailsType]): Either[PlaybackExtractionError, UserAnswers] =
    {
      data match {
        case None => Left(FailedToExtractData("No Trustee Details"))
        case trust =>

          val updated: Try[UserAnswers] = trust.foldLeft[Try[UserAnswers]](Success(answers)){
            case (answers, details) =>
              answers
                .flatMap(_.set(WhenTrustSetupPage, details.startDate))
                .flatMap(answers => extractGovernedBy(details, answers))
                .flatMap(answers => extractAdminBy(details, answers))
                .flatMap(answers => extractResidentialType(details, answers))
          }

          updated match {
            case Success(a) =>
              Right(a)
            case Failure(exception) =>
              Logger.warn(s"[TrustDetailsExtractor] failed to extract data due to ${exception.getMessage}")
              Left(FailedToExtractData(TrustDetailsType.toString))
          }
      }
    }

  private def extractGovernedBy(details: TrustDetailsType, answers: UserAnswers): Try[UserAnswers] =
    details.lawCountry match {
      case Some(country) => answers.set(GovernedInsideTheUKPage, false)
        .flatMap(_.set(CountryGoverningTrustPage, country))
      case _ => answers.set(GovernedInsideTheUKPage, true)
    }

  private def extractAdminBy(details: TrustDetailsType, answers: UserAnswers): Try[UserAnswers] =
    details.administrationCountry match {
      case Some(country) => answers.set(AdministrationInsideUKPage, false)
        .flatMap(_.set(CountryAdministeringTrustPage, country))
      case _ => answers.set(AdministrationInsideUKPage, true)
    }

  private def extractResidentialType(details: TrustDetailsType, answers: UserAnswers): Try[UserAnswers] =
    details.residentialStatus map {
      case ResidentialStatusType(Some(UkType(scottishLaw, preOffShore)), None) =>
        val extractOffShore = preOffShore match {
          case Some(country) => answers.set(TrustPreviouslyResidentPage, country)
              .flatMap(_.set(TrustResidentOffshorePage, true))
          case _ => answers.set(TrustResidentOffshorePage, false)
        }
        extractOffShore.flatMap(_.set(EstablishedUnderScotsLawPage, scottishLaw))
      case ResidentialStatusType(None, Some(NonUKType(sch5atcgga92, s218ihta84, agentS218IHTA84, trusteeStatus))) =>
        val registeringTrustFor5A = answers.set(RegisteringTrustFor5APage, sch5atcgga92)

        val inheritanceTax = s218ihta84 match {
          case Some(iht) => registeringTrustFor5A.flatMap(_.set(InheritanceTaxActPage, iht))
          case _ => registeringTrustFor5A
        }

        val agentInheritance = agentS218IHTA84 match {
          case Some(iht) => inheritanceTax.flatMap(_.set(AgentOtherThanBarristerPage, iht))
          case _ => inheritanceTax
        }

        trusteeStatus.map(NonResidentType.fromDES) match {
          case Some(status) => agentInheritance.flatMap(_.set(NonResidentTypePage, status))
          case _ => agentInheritance
        }
    } getOrElse Success(answers)

}