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

package mapping.playback.protectors

import com.google.inject.Inject
import mapping.playback.PlaybackExtractionErrors.{FailedToExtractData, PlaybackExtractionError}
import mapping.playback.{PlaybackExtractor, PlaybackImplicits}
import models.core.pages.{InternationalAddress, UKAddress}
import models.playback.http.{DisplayTrustProtector, DisplayTrustProtectorCompany}
import models.playback.{MetaData, UserAnswers}
import models.registration.pages.AddressOrUtr
import pages.register.protectors.company._
import play.api.Logger

import scala.util.{Failure, Success, Try}

class CompanyProtectorExtractor @Inject() extends PlaybackExtractor[Option[List[DisplayTrustProtectorCompany]]] {

  import PlaybackImplicits._

  override def extract(answers: UserAnswers, data: Option[List[DisplayTrustProtectorCompany]]): Either[PlaybackExtractionError, UserAnswers] =
    {
      data match {
        case None => Right(answers)
        case Some(protectors) =>
          val updated = protectors.zipWithIndex.foldLeft[Try[UserAnswers]](Success(answers)){
            case (answers, (companyProtector, index)) =>
              answers
                .flatMap(_.set(CompanyProtectorNamePage(index), companyProtector.name))
                .flatMap(_.set(CompanyProtectorSafeIdPage(index), companyProtector.identification.flatMap(_.safeId)))
                .flatMap(answers => extractUtrOrAddress(companyProtector, index, answers))
                .flatMap {
                  _.set(
                    CompanyProtectorMetaData(index),
                    MetaData(
                      lineNo = companyProtector.lineNo,
                      bpMatchStatus = companyProtector.bpMatchStatus,
                      entityStart = companyProtector.entityStart
                    )
                  )
                }
          }

          updated match {
            case Success(a) =>
              Right(a)
            case Failure(exception) =>
              Logger.warn(s"[IndividualProtectorExtractor] failed to extract data due to ${exception.getMessage}")
              Left(FailedToExtractData(DisplayTrustProtector.toString))
          }
      }
    }

  private def extractUtrOrAddress(companyProtector: DisplayTrustProtectorCompany, index: Int, answers: UserAnswers) = {
    companyProtector.identification.flatMap(_.address.convert) match {
      case Some(uk: UKAddress) =>
        answers.set(CompanyProtectorAddressOrUtrPage(index), AddressOrUtr.Address)
          .flatMap(_.set(CompanyProtectorAddressPage(index), uk))
          .flatMap(_.set(CompanyProtectorAddressUKYesNoPage(index), true))
      case Some(nonUk: InternationalAddress) =>
        answers.set(CompanyProtectorAddressOrUtrPage(index), AddressOrUtr.Address)
          .flatMap(_.set(CompanyProtectorAddressPage(index), nonUk))
          .flatMap(_.set(CompanyProtectorAddressUKYesNoPage(index), false))
      case None =>
        answers.set(CompanyProtectorAddressOrUtrPage(index), AddressOrUtr.Utr)
          .flatMap(_.set(CompanyProtectorUtrPage(index), companyProtector.identification.flatMap(_.utr)))
    }
  }
}