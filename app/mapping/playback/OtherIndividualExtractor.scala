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
import mapping.registration.PassportType
import models.core.pages.{Address, InternationalAddress, UKAddress}
import models.playback.http.{DisplayTrustIdentificationType, DisplayTrustNaturalPersonType}
import models.playback.{MetaData, UserAnswers}
import pages.register.natural.individual._
import play.api.Logger

import scala.util.{Failure, Success, Try}

class OtherIndividualExtractor @Inject() extends PlaybackExtractor[Option[List[DisplayTrustNaturalPersonType]]] {

  import PlaybackImplicits._

  override def extract(answers: UserAnswers, data: Option[List[DisplayTrustNaturalPersonType]]): Either[PlaybackExtractionError, UserAnswers] =
    {
      data match {
        case None => Left(FailedToExtractData("No Other Individual"))
        case Some(individual) =>

          val updated = individual.zipWithIndex.foldLeft[Try[UserAnswers]](Success(answers)){
            case (answers, (individual, index)) =>

            answers
              .flatMap(_.set(OtherIndividualNamePage(index), individual.name.convert))
              .flatMap(answers => extractDateOfBirth(individual, index, answers))
              .flatMap(answers => extractIdentification(individual, index, answers))
              .flatMap {
                _.set(
                  OtherIndividualMetaData(index),
                  MetaData(
                    lineNo = individual.lineNo,
                    bpMatchStatus = individual.bpMatchStatus,
                    entityStart = individual.entityStart
                  )
                )
              }
              .flatMap(_.set(OtherIndividualSafeIdPage(index), individual.identification.flatMap(_.safeId)))
          }

          updated match {
            case Success(a) =>
              Right(a)
            case Failure(exception) =>
              Logger.warn(s"[OtherIndividualExtractor] failed to extract data due to ${exception.getMessage}")
              Left(FailedToExtractData(DisplayTrustNaturalPersonType.toString))
          }
      }
    }

  private def extractIdentification(individual: DisplayTrustNaturalPersonType, index: Int, answers: UserAnswers) = {
    individual.identification map {

      case DisplayTrustIdentificationType(_, Some(nino), None, None) =>
        answers.set(OtherIndividualNationalInsuranceYesNoPage(index), true)
          .flatMap(_.set(OtherIndividualNationalInsuranceNumberPage(index), nino))

      case DisplayTrustIdentificationType(_, None, None, Some(address)) =>
        answers.set(OtherIndividualNationalInsuranceYesNoPage(index), false)
          .flatMap(_.set(OtherIndividualPassportIDCardYesNoPage(index), false))
          .flatMap(answers => extractAddress(address.convert, index, answers))

      case DisplayTrustIdentificationType(_, None, Some(passport), Some(address)) =>
        answers.set(OtherIndividualNationalInsuranceYesNoPage(index), false)
          .flatMap(answers => extractAddress(address.convert, index, answers))
          .flatMap(answers => extractPassportIdCard(passport, index, answers))

      case DisplayTrustIdentificationType(_, None, Some(passport), None) =>
        Logger.error(s"[OtherIndividualExtractor] only passport identification returned in DisplayTrustOrEstate api")
        case object InvalidExtractorState extends RuntimeException
        Failure(InvalidExtractorState)

    } getOrElse {
      answers.set(OtherIndividualNationalInsuranceYesNoPage(index), false)
        .flatMap(_.set(OtherIndividualAddressYesNoPage(index), false))
    }
  }

  private def extractDateOfBirth(individual: DisplayTrustNaturalPersonType, index: Int, answers: UserAnswers) = {
    individual.dateOfBirth match {
      case Some(dob) =>
        answers.set(OtherIndividualDateOfBirthYesNoPage(index), true)
          .flatMap(_.set(OtherIndividualDateOfBirthPage(index), dob.convert))
      case None =>
        // Assumption that user answered no as dob is not provided
        answers.set(OtherIndividualDateOfBirthYesNoPage(index), false)
    }
  }

  private def extractPassportIdCard(passport: PassportType, index: Int, answers: UserAnswers) = {
      answers.set(OtherIndividualPassportIDCardYesNoPage(index), true)
        .flatMap(_.set(OtherIndividualPassportIDCardPage(index), passport.convert))
  }

  private def extractAddress(address: Address, index: Int, answers: UserAnswers) = {
    address match {
      case uk: UKAddress =>
        answers.set(OtherIndividualAddressPage(index), uk)
          .flatMap(_.set(OtherIndividualAddressYesNoPage(index), true))
          .flatMap(_.set(OtherIndividualAddressUKYesNoPage(index), true))
      case nonUk: InternationalAddress =>
        answers.set(OtherIndividualAddressPage(index), nonUk)
          .flatMap(_.set(OtherIndividualAddressYesNoPage(index), true))
          .flatMap(_.set(OtherIndividualAddressUKYesNoPage(index), false))
    }
  }

}
