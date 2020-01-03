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

package mapping.playback.settlors

import mapping.TypeOfTrust
import mapping.DeedOfVariation
import mapping.playback.PlaybackExtractionErrors.{FailedToExtractData, PlaybackExtractionError}
import mapping.playback.PlaybackExtractor
import models.playback.UserAnswers
import models.playback.http.{DisplayTrust, DisplayTrustWillType}
import models.registration.pages.SettlorKindOfTrust
import pages.register.settlors.{SettlorAdditionToWillTrustYesNoPage, SettlorHowDeedOfVariationCreatedPage}
import pages.register.settlors.living_settlor.{SettlorHandoverReliefYesNoPage, SettlorKindOfTrustPage}
import play.api.Logger

import scala.util.{Failure, Success, Try}

class TrustTypeExtractor extends PlaybackExtractor[Option[DisplayTrust]] {

  override def extract(answers: UserAnswers, data: Option[DisplayTrust]): Either[PlaybackExtractionError, UserAnswers] =
  {
    data match {
      case None => Left(FailedToExtractData("No Trust Details"))
      case displayTrust =>

        val updated = displayTrust.foldLeft[Try[UserAnswers]](Success(answers)){
          case (answers, trust) =>
            answers
              .flatMap(answers => extractTrustType(trust, answers))
        }

        updated match {
          case Success(a) =>
            Right(a)
          case Failure(exception) =>
            Logger.warn(s"[TrustTypeExtractor] failed to extract data due to ${exception.getMessage}")
            Left(FailedToExtractData(DisplayTrustWillType.toString))
        }
    }
  }

  private def extractTrustType(trust: DisplayTrust, answers: UserAnswers) = {
    trust.details.typeOfTrust match {
      case TypeOfTrust.DeedOfVariation =>
        answers.set(SettlorKindOfTrustPage, SettlorKindOfTrust.Deed)
          .flatMap(answers => extractDeedOfVariation(trust, answers))

      case TypeOfTrust.IntervivosSettlementTrust =>
        answers.set(SettlorKindOfTrustPage, SettlorKindOfTrust.Intervivos)
          .flatMap(_.set(SettlorHandoverReliefYesNoPage, trust.details.interVivos))

      case TypeOfTrust.EmployeeRelated =>
        answers.set(SettlorKindOfTrustPage, SettlorKindOfTrust.Employees)

      case TypeOfTrust.FlatManagementTrust =>
        answers.set(SettlorKindOfTrustPage, SettlorKindOfTrust.FlatManagement)

      case TypeOfTrust.HeritageTrust =>
        answers.set(SettlorKindOfTrustPage, SettlorKindOfTrust.HeritageMaintenanceFund)

      case TypeOfTrust.WillTrustOrIntestacyTrust =>
        Success(answers)
    }
  }

  private def extractDeedOfVariation(trust: DisplayTrust, answers: UserAnswers) = {
    trust.details.deedOfVariation match {
      case Some(DeedOfVariation.AdditionToWill) =>
        answers.set(SettlorAdditionToWillTrustYesNoPage, true)
      case Some(_) =>
        answers.set(SettlorAdditionToWillTrustYesNoPage, false)
          .flatMap(_.set(SettlorHowDeedOfVariationCreatedPage, trust.details.deedOfVariation))
      case _ =>
        Success(answers)
    }
  }

}