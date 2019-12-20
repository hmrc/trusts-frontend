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

import com.google.inject.Inject
import mapping.playback.PlaybackExtractionErrors.{FailedToExtractData, PlaybackExtractionError}
import mapping.playback.{PlaybackExtractor, PlaybackImplicits}
import models.core.pages.{IndividualOrBusiness, InternationalAddress, UKAddress}
import models.playback.http.{DisplayTrustCharityType, DisplayTrustSettlor}
import models.playback.{MetaData, UserAnswers}
import models.registration.pages.Status.Completed
import pages.entitystatus.LivingSettlorStatus
import pages.register.settlors.living_settlor._
import play.api.Logger

import scala.util.{Failure, Success, Try}

class SettlorIndividualExtractor @Inject() extends PlaybackExtractor[Option[List[DisplayTrustSettlor]]] {

  import PlaybackImplicits._

  override def extract(answers: UserAnswers, data: Option[List[DisplayTrustSettlor]]): Either[PlaybackExtractionError, UserAnswers] =
    {
      data match {
        case None => Left(FailedToExtractData("No Settlor Individual"))
        case Some(individuals) =>

          val updated = individuals.zipWithIndex.foldLeft[Try[UserAnswers]](Success(answers)){
            case (answers, (settlorIndividual, index)) =>

              answers
              .flatMap(_.set(SettlorIndividualOrBusinessPage(index), IndividualOrBusiness.Individual))
              .flatMap(_.set(SettlorIndividualNamePage(index), settlorIndividual.name.convert))
              .flatMap(answers => extractDateOfBirth(settlorIndividual, index, answers))
              .flatMap(answers => extractNino(settlorIndividual, index, answers))
              .flatMap(answers => extractAddress(settlorIndividual, index, answers))
              .flatMap(answers => extractPassportIdCard(settlorIndividual, index, answers))
              .flatMap {
                _.set(
                  SettlorMetaData(index),
                  MetaData(
                    lineNo = settlorIndividual.lineNo,
                    bpMatchStatus = settlorIndividual.bpMatchStatus,
                    entityStart = settlorIndividual.entityStart
                  )
                )
              }
              .flatMap(_.set(SettlorSafeIdPage(index), settlorIndividual.identification.flatMap(_.safeId)))
              .flatMap(_.set(LivingSettlorStatus(index), Completed))

          }

          updated match {
            case Success(a) =>
              Right(a)
            case Failure(exception) =>
              Logger.warn(s"[SettlorIndividualExtractor] failed to extract data due to ${exception.getMessage}")
              Left(FailedToExtractData(DisplayTrustCharityType.toString))
          }
      }
    }

  private def extractDateOfBirth(settlorIndividual: DisplayTrustSettlor, index: Int, answers: UserAnswers) = {
    settlorIndividual.dateOfBirth match {
      case Some(dob) =>
        answers.set(SettlorIndividualDateOfBirthYesNoPage(index), true)
          .flatMap(_.set(SettlorIndividualDateOfBirthPage(index), dob.convert))
      case None =>
        // Assumption that user answered no as utr is not provided
        answers.set(SettlorIndividualDateOfBirthYesNoPage(index), false)
    }
  }

  private def extractNino(settlorIndividual: DisplayTrustSettlor, index: Int, answers: UserAnswers) = {
    settlorIndividual.identification.flatMap(_.nino) match {
      case Some(nino) =>
        answers.set(SettlorIndividualNINOYesNoPage(index), true)
          .flatMap(_.set(SettlorIndividualNINOPage(index), nino))
      case None =>
        // Assumption that user answered no as utr is not provided
        answers.set(SettlorIndividualNINOYesNoPage(index), false)
    }
  }

  private def extractAddress(settlorIndividual: DisplayTrustSettlor, index: Int, answers: UserAnswers) = {
    settlorIndividual.identification.flatMap(_.address.convert) match {
      case Some(uk: UKAddress) =>
        answers.set(SettlorIndividualAddressUKPage(index), uk)
          .flatMap(_.set(SettlorIndividualAddressYesNoPage(index), true))
          .flatMap(_.set(SettlorIndividualAddressUKYesNoPage(index), true))
      case Some(nonUk: InternationalAddress) =>
        answers.set(SettlorIndividualAddressInternationalPage(index), nonUk)
          .flatMap(_.set(SettlorIndividualAddressYesNoPage(index), true))
          .flatMap(_.set(SettlorIndividualAddressUKYesNoPage(index), false))
      case None =>
        answers.set(SettlorIndividualAddressYesNoPage(index), false)
    }
  }

  private def extractPassportIdCard(settlorIndividual: DisplayTrustSettlor, index: Int, answers: UserAnswers) = {
    settlorIndividual.identification.flatMap(_.passport) match {
      case Some(passportIdCard) =>
        answers.set(SettlorIndividualPassportIDCardYesNoPage(index), true)
          .flatMap(_.set(SettlorIndividualPassportIDCardPage(index), passportIdCard.convert))
      case None =>
        answers.set(SettlorIndividualPassportIDCardYesNoPage(index), false)
    }
  }


}