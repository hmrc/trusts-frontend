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
import models.core.pages.{Address, IndividualOrBusiness, InternationalAddress, UKAddress}
import models.playback.http.{DisplayTrustCharityType, DisplayTrustSettlor, DisplayTrustSettlorCompany, LivingSettlor}
import models.playback.{MetaData, UserAnswers}
import models.registration.pages.Status.Completed
import pages.register.settlors.living_settlor._
import pages.entitystatus.LivingSettlorStatus
import play.api.Logger

import scala.util.{Failure, Success, Try}

class LivingSettlorExtractor @Inject() extends PlaybackExtractor[Option[List[LivingSettlor]]] {

  import PlaybackImplicits._

  override def extract(answers: UserAnswers, data: Option[List[LivingSettlor]]): Either[PlaybackExtractionError, UserAnswers] =
    {
      data match {
        case None => Left(FailedToExtractData("No Living Settlors"))
        case Some(settlors) =>

          val updated = settlors.zipWithIndex.foldLeft[Try[UserAnswers]](Success(answers)){
            case (answers, (settlor, index)) =>

              settlor match {
                case x : DisplayTrustSettlorCompany => extractSettlorCompany(answers, index, x)
                case x : DisplayTrustSettlor => extractSettlorIndividual(answers, index, x)
                case _ => Failure(new RuntimeException("Unexpected settlor type"))
              }
          }

          updated match {
            case Success(a) =>
              Right(a)
            case Failure(exception) =>
              Logger.warn(s"[SettlorCompanyExtractor] failed to extract data due to ${exception.getMessage}")
              Left(FailedToExtractData(DisplayTrustCharityType.toString))
          }
      }
    }

  def extractSettlorIndividual(answers: Try[UserAnswers], index: Int, individual : DisplayTrustSettlor) = {
    answers
      .flatMap(_.set(SettlorIndividualOrBusinessPage(index), IndividualOrBusiness.Individual))
      .flatMap(_.set(SettlorIndividualNamePage(index), individual.name.convert))
      .flatMap(answers => extractDateOfBirth(individual, index, answers))
      .flatMap(answers => extractNino(individual, index, answers))
      .flatMap(answers => extractAddress(individual.identification.flatMap(_.address.convert), index, answers))
      .flatMap(answers => extractPassportIdCard(individual, index, answers))
      .flatMap {
        _.set(
          SettlorMetaData(index),
          MetaData(
            lineNo = individual.lineNo,
            bpMatchStatus = individual.bpMatchStatus,
            entityStart = individual.entityStart
          )
        )
      }
      .flatMap(_.set(SettlorSafeIdPage(index), individual.identification.flatMap(_.safeId)))
      .flatMap(_.set(LivingSettlorStatus(index), Completed))
  }

  def extractSettlorCompany(answers: Try[UserAnswers], index: Int, company : DisplayTrustSettlorCompany) = {
    answers
      .flatMap(_.set(SettlorIndividualOrBusinessPage(index), IndividualOrBusiness.Business))
      .flatMap(_.set(SettlorBusinessNamePage(index), company.name))
      .flatMap(answers => extractUtr(company, index, answers))
      .flatMap(answers => extractAddress(company.identification.flatMap(_.address.convert), index, answers))
      .flatMap {
        _.set(
          SettlorMetaData(index),
          MetaData(
            lineNo = company.lineNo,
            bpMatchStatus = company.bpMatchStatus,
            entityStart = company.entityStart
          )
        )
      }
      .flatMap(_.set(SettlorCompanyTypePage(index), company.companyType))
      .flatMap(_.set(SettlorCompanyTimePage(index), company.companyTime))
      .flatMap(_.set(SettlorSafeIdPage(index), company.identification.flatMap(_.safeId)))
      .flatMap(_.set(LivingSettlorStatus(index), Completed))
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

  private def extractPassportIdCard(settlorIndividual: DisplayTrustSettlor, index: Int, answers: UserAnswers) = {
    settlorIndividual.identification.flatMap(_.passport) match {
      case Some(passportIdCard) =>
        answers.set(SettlorIndividualPassportIDCardYesNoPage(index), true)
          .flatMap(_.set(SettlorIndividualPassportIDCardPage(index), passportIdCard.convert))
      case None =>
        answers.set(SettlorIndividualPassportIDCardYesNoPage(index), false)
    }
  }

  private def extractUtr(settlorCompany: DisplayTrustSettlorCompany, index: Int, answers: UserAnswers) = {
    settlorCompany.identification.flatMap(_.utr) match {
      case Some(utr) =>
        answers.set(SettlorUtrYesNoPage(index), true)
          .flatMap(_.set(SettlorUtrPage(index), utr))
      case None =>
        // Assumption that user answered no as utr is not provided
        answers.set(SettlorUtrYesNoPage(index), false)
    }
  }

  private def extractAddress(address: Option[Address], index: Int, answers: UserAnswers) = {
    address match {
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
}