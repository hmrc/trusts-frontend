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

package mapping.playback.beneficiaries

import com.google.inject.Inject
import mapping.playback.PlaybackExtractionErrors.{FailedToExtractData, PlaybackExtractionError}
import mapping.playback.{PlaybackExtractor, PlaybackImplicits}
import models.core.pages.{InternationalAddress, UKAddress}
import models.playback.http.DisplayTrustCharityType
import models.playback.{MetaData, UserAnswers}
import pages.register.beneficiaries.charity.{CharityBeneficiaryAddressYesNoPage, _}
import play.api.Logger

import scala.util.{Failure, Success, Try}

class CharityBeneficiaryExtractor @Inject() extends PlaybackExtractor[Option[List[DisplayTrustCharityType]]] {

  import PlaybackImplicits._

  override def extract(answers: UserAnswers, data: Option[List[DisplayTrustCharityType]]): Either[PlaybackExtractionError, UserAnswers] =
    {
      data match {
        case None => Left(FailedToExtractData("No Charity Beneficiary"))
        case Some(charities) =>

          val updated = charities.zipWithIndex.foldLeft[Try[UserAnswers]](Success(answers)){
            case (answers, (charityBeneficiary, index)) =>

            answers
              .flatMap(_.set(CharityBeneficiaryNamePage(index), charityBeneficiary.organisationName))
              .flatMap(answers => extractShareOfIncome(charityBeneficiary, index, answers))
              .flatMap(_.set(CharityBeneficiaryUtrPage(index), charityBeneficiary.identification.flatMap(_.utr)))
              .flatMap(_.set(CharityBeneficiarySafeIdPage(index), charityBeneficiary.identification.flatMap(_.safeId)))
              .flatMap(answers => extractAddress(charityBeneficiary, index, answers))
              .flatMap {
                _.set(
                  CharityBeneficiaryMetaData(index),
                  MetaData(
                    lineNo = charityBeneficiary.lineNo,
                    bpMatchStatus = charityBeneficiary.bpMatchStatus,
                    entityStart = charityBeneficiary.entityStart
                  )
                )
              }
          }

          updated match {
            case Success(a) =>
              Right(a)
            case Failure(exception) =>
              Logger.warn(s"[CharityBeneficiaryExtractor] failed to extract data due to ${exception.getMessage}")
              Left(FailedToExtractData(DisplayTrustCharityType.toString))
          }
      }
    }

  private def extractShareOfIncome(charityBeneficiary: DisplayTrustCharityType, index: Int, answers: UserAnswers) = {
    charityBeneficiary.beneficiaryShareOfIncome match {
      case Some(income) =>
        answers.set(CharityBeneficiaryDiscretionYesNoPage(index), false)
          .flatMap(_.set(CharityBeneficiaryShareOfIncomePage(index), income))
      case None =>
        // Assumption that user answered yes as the share of income is not provided
        answers.set(CharityBeneficiaryDiscretionYesNoPage(index), true)
    }
  }

  private def extractAddress(charityBeneficiary: DisplayTrustCharityType, index: Int, answers: UserAnswers) = {
    charityBeneficiary.identification.flatMap(_.address.convert) match {
      case Some(uk: UKAddress) =>
        answers.set(CharityBeneficiaryAddressPage(index), uk)
          .flatMap(_.set(CharityBeneficiaryAddressYesNoPage(index), true))
          .flatMap(_.set(CharityBeneficiaryAddressUKYesNoPage(index), true))
      case Some(nonUk: InternationalAddress) =>
        answers.set(CharityBeneficiaryAddressPage(index), nonUk)
          .flatMap(_.set(CharityBeneficiaryAddressYesNoPage(index), true))
          .flatMap(_.set(CharityBeneficiaryAddressUKYesNoPage(index), false))
      case None =>
        answers.set(CharityBeneficiaryAddressYesNoPage(index), false)
    }
  }
}