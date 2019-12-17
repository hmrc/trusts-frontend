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

package mapping.playback

import com.google.inject.Inject
import mapping.playback.PlaybackExtractionErrors.{FailedToExtractData, PlaybackExtractionError}
import models.core.pages.{FullName, InternationalAddress, UKAddress}
import models.playback.http.DisplayTrustWillType
import models.playback.{MetaData, UserAnswers}
import java.time.LocalDate

import models.registration.pages.PassportOrIdCardDetails
import pages.register.settlors.deceased_settlor._
import play.api.Logger

import scala.util.{Failure, Success, Try}

class DeceasedSettlorExtractor @Inject() extends PlaybackExtractor[Option[DisplayTrustWillType]] {

  import PlaybackImplicits._

  override def extract(answers: UserAnswers, data: Option[DisplayTrustWillType]): Either[PlaybackExtractionError, UserAnswers] =
    {
      data match {
        case None => Left(FailedToExtractData("No Deceased Settlor"))
        case deceasedSettlor =>

          val updated = deceasedSettlor.foldLeft[Try[UserAnswers]](Success(answers)){
            case (answers, deceasedSettlor) =>

            answers
              .flatMap(_.set(SettlorsNamePage, FullName(deceasedSettlor.name.firstName, deceasedSettlor.name.middleName, deceasedSettlor.name.lastName )))
              .flatMap(answers => extractDateOfDeath(deceasedSettlor, answers))
              .flatMap(answers => extractDateOfBirth(deceasedSettlor, answers))
              .flatMap(answers => extractNino(deceasedSettlor, answers))
              .flatMap(answers => extractAddress(deceasedSettlor, answers))
              .flatMap(answers => extractPassportIdCard(deceasedSettlor, answers))
              .flatMap(_.set(SettlorsSafeIdPage, deceasedSettlor.identification.flatMap(_.safeId)))
              .flatMap {
                _.set(
                  SettlorsMetaData,
                  MetaData(
                    lineNo = deceasedSettlor.lineNo,
                    bpMatchStatus = deceasedSettlor.bpMatchStatus,
                    entityStart = deceasedSettlor.entityStart
                  )
                )
              }
          }

          updated match {
            case Success(a) =>
              Right(a)
            case Failure(exception) =>
              Logger.warn(s"[DeceasedSettlorExtractor] failed to extract data due to ${exception.getMessage}")
              Left(FailedToExtractData(DisplayTrustWillType.toString))
          }
      }
    }

  private def extractDateOfDeath(deceasedSettlor: DisplayTrustWillType, answers: UserAnswers) = {
    deceasedSettlor.dateOfDeath match {
      case Some(dateOfDeath) =>
        answers.set(SettlorDateOfDeathYesNoPage, true)
          .flatMap(_.set(SettlorDateOfDeathPage, LocalDate.of(dateOfDeath.getYear, dateOfDeath.getMonthOfYear, dateOfDeath.getDayOfMonth)))
      case None =>
        // Assumption that user answered no as the date of death is not provided
        answers.set(SettlorDateOfDeathYesNoPage, false)
    }
  }

  private def extractDateOfBirth(deceasedSettlor: DisplayTrustWillType, answers: UserAnswers) = {
    deceasedSettlor.dateOfBirth match {
      case Some(dateOfBirth) =>
        answers.set(SettlorDateOfBirthYesNoPage, true)
          .flatMap(_.set(SettlorsDateOfBirthPage, LocalDate.of(dateOfBirth.getYear, dateOfBirth.getMonthOfYear, dateOfBirth.getDayOfMonth)))
      case None =>
        // Assumption that user answered no as the date of birth is not provided
        answers.set(SettlorDateOfBirthYesNoPage, false)
    }
  }

  private def extractNino(deceasedSettlor: DisplayTrustWillType, answers: UserAnswers) = {
    deceasedSettlor.identification.flatMap(_.nino) match {
      case Some(nino) =>
        answers.set(SettlorNationalInsuranceNumberPage, nino)
          .flatMap(_.set(SettlorsNINoYesNoPage, true))
      case None =>
        // Assumption that user answered no as nino is not provided
        answers.set(SettlorsNINoYesNoPage, false)
    }
  }

  private def extractAddress(deceasedSettlor: DisplayTrustWillType, answers: UserAnswers) = {
    deceasedSettlor.identification.flatMap(_.address.convert) match {
      case Some(uk: UKAddress) =>
        answers.set(SettlorsUKAddressPage, uk)
          .flatMap(_.set(SettlorsLastKnownAddressYesNoPage, true))
          .flatMap(_.set(WasSettlorsAddressUKYesNoPage, true))
      case Some(nonUk: InternationalAddress) =>
        answers.set(SettlorsInternationalAddressPage, nonUk)
          .flatMap(_.set(SettlorsLastKnownAddressYesNoPage, true))
          .flatMap(_.set(WasSettlorsAddressUKYesNoPage, false))
      case None => Try(answers)
    }
  }

  private def extractPassportIdCard(deceasedSettlor: DisplayTrustWillType, answers: UserAnswers) = {
    deceasedSettlor.identification.flatMap(_.passport) match {
      case Some(passportIdCard) =>
        answers.set(SettlorsPassportIDCardPage, PassportOrIdCardDetails(passportIdCard.countryOfIssue, passportIdCard.number, passportIdCard.expirationDate))
      case None => Try(answers)
    }
  }


}