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
import models.core.pages.{InternationalAddress, UKAddress}
import models.playback.http.DisplayTrustBeneficiaryTrustType
import models.playback.{MetaData, UserAnswers}
import pages.register.beneficiaries.trust._
import play.api.Logger

import scala.util.{Failure, Success, Try}

class TrustBeneficiaryExtractor @Inject() extends PlaybackExtractor[Option[List[DisplayTrustBeneficiaryTrustType]]] {

  import PlaybackImplicits._

  override def extract(answers: UserAnswers, data: Option[List[DisplayTrustBeneficiaryTrustType]]): Either[PlaybackExtractionError, UserAnswers] =
    {
      data match {
        case None => Right(answers)
        case Some(trusts) =>

          val updated = trusts.zipWithIndex.foldLeft[Try[UserAnswers]](Success(answers)){
            case (answers, (trustBeneficiary, index)) =>

            answers
              .flatMap(_.set(TrustBeneficiaryNamePage(index), trustBeneficiary.organisationName))
              .flatMap(answers => extractShareOfIncome(trustBeneficiary, index, answers))
              .flatMap(_.set(TrustBeneficiaryUtrPage(index), trustBeneficiary.identification.flatMap(_.utr)))
              .flatMap(_.set(TrustBeneficiarySafeIdPage(index), trustBeneficiary.identification.flatMap(_.safeId)))
              .flatMap(answers => extractAddress(trustBeneficiary, index, answers))
              .flatMap {
                _.set(
                  TrustBeneficiaryMetaData(index),
                  MetaData(
                    lineNo = trustBeneficiary.lineNo,
                    bpMatchStatus = trustBeneficiary.bpMatchStatus,
                    entityStart = trustBeneficiary.entityStart
                  )
                )
              }
          }

          updated match {
            case Success(a) =>
              Right(a)
            case Failure(exception) =>
              Logger.warn(s"[TrustBeneficiaryExtractor] failed to extract data due to ${exception.getMessage}")
              Left(FailedToExtractData(DisplayTrustBeneficiaryTrustType.toString))
          }
      }
    }

  private def extractShareOfIncome(trustBeneficiary: DisplayTrustBeneficiaryTrustType, index: Int, answers: UserAnswers) = {
    trustBeneficiary.beneficiaryShareOfIncome match {
      case Some(income) =>
        answers.set(TrustBeneficiaryDiscretionYesNoPage(index), false)
          .flatMap(_.set(TrustBeneficiaryShareOfIncomePage(index), income))
      case None =>
        // Assumption that user answered yes as the share of income is not provided
        answers.set(TrustBeneficiaryDiscretionYesNoPage(index), true)
    }
  }

  private def extractAddress(trustBeneficiary: DisplayTrustBeneficiaryTrustType, index: Int, answers: UserAnswers) = {
    trustBeneficiary.identification.flatMap(_.address.convert) match {
      case Some(uk: UKAddress) =>
        answers.set(TrustBeneficiaryAddressPage(index), uk)
          .flatMap(_.set(TrustBeneficiaryAddressYesNoPage(index), true))
          .flatMap(_.set(TrustBeneficiaryAddressUKYesNoPage(index), true))
      case Some(nonUk: InternationalAddress) =>
        answers.set(TrustBeneficiaryAddressPage(index), nonUk)
          .flatMap(_.set(TrustBeneficiaryAddressYesNoPage(index), true))
          .flatMap(_.set(TrustBeneficiaryAddressUKYesNoPage(index), false))
      case None =>
        answers.set(TrustBeneficiaryAddressYesNoPage(index), false)
    }
  }
}