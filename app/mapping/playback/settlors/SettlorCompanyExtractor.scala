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
import models.playback.http.{DisplayTrustCharityType, DisplayTrustSettlorCompany}
import models.playback.{MetaData, UserAnswers}
import pages.register.settlors.living_settlor._
import play.api.Logger

import scala.util.{Failure, Success, Try}

class SettlorCompanyExtractor @Inject() extends PlaybackExtractor[Option[List[DisplayTrustSettlorCompany]]] {

  import PlaybackImplicits._

  override def extract(answers: UserAnswers, data: Option[List[DisplayTrustSettlorCompany]]): Either[PlaybackExtractionError, UserAnswers] =
    {
      data match {
        case None => Left(FailedToExtractData("No Settlor Company"))
        case Some(companies) =>

          val updated = companies.zipWithIndex.foldLeft[Try[UserAnswers]](Success(answers)){
            case (answers, (settlorCompany, index)) =>

            answers
              .flatMap(_.set(SettlorIndividualOrBusinessPage(index), IndividualOrBusiness.Business))
              .flatMap(_.set(SettlorBusinessNamePage(index), settlorCompany.name))
              .flatMap(answers => extractUtr(settlorCompany, index, answers))
              .flatMap(answers => extractAddress(settlorCompany, index, answers))
              .flatMap {
                _.set(
                  SettlorMetaData(index),
                  MetaData(
                    lineNo = settlorCompany.lineNo,
                    bpMatchStatus = settlorCompany.bpMatchStatus,
                    entityStart = settlorCompany.entityStart
                  )
                )
              }
              .flatMap(_.set(SettlorCompanyTypePage(index), settlorCompany.companyType))
              .flatMap(_.set(SettlorCompanyTimePage(index), settlorCompany.companyTime))
              .flatMap(_.set(SettlorSafeIdPage(index), settlorCompany.identification.flatMap(_.safeId)))
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

  private def extractAddress(settlorCompany: DisplayTrustSettlorCompany, index: Int, answers: UserAnswers) = {
    settlorCompany.identification.flatMap(_.address.convert) match {
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