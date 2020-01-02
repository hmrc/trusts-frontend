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
import models.playback.http.DisplayTrustCompanyType
import models.playback.{MetaData, UserAnswers}
import pages.register.beneficiaries.company._
import play.api.Logger

import scala.util.{Failure, Success, Try}

class CompanyBeneficiaryExtractor @Inject() extends PlaybackExtractor[Option[List[DisplayTrustCompanyType]]] {

  import PlaybackImplicits._

  override def extract(answers: UserAnswers, data: Option[List[DisplayTrustCompanyType]]): Either[PlaybackExtractionError, UserAnswers] =
    {
      data match {
        case None => Left(FailedToExtractData("No Company Beneficiary"))
        case Some(charities) =>

          val updated = charities.zipWithIndex.foldLeft[Try[UserAnswers]](Success(answers)){
            case (answers, (companyBeneficiary, index)) =>

            answers
              .flatMap(_.set(CompanyBeneficiaryNamePage(index), companyBeneficiary.organisationName))
              .flatMap(answers => extractShareOfIncome(companyBeneficiary, index, answers))
              .flatMap(_.set(CompanyBeneficiaryUtrPage(index), companyBeneficiary.identification.flatMap(_.utr)))
              .flatMap(_.set(CompanyBeneficiarySafeIdPage(index), companyBeneficiary.identification.flatMap(_.safeId)))
              .flatMap(answers => extractAddress(companyBeneficiary, index, answers))
              .flatMap {
                _.set(
                  CompanyBeneficiaryMetaData(index),
                  MetaData(
                    lineNo = companyBeneficiary.lineNo,
                    bpMatchStatus = companyBeneficiary.bpMatchStatus,
                    entityStart = companyBeneficiary.entityStart
                  )
                )
              }
          }

          updated match {
            case Success(a) =>
              Right(a)
            case Failure(exception) =>
              Logger.warn(s"[CompanyBeneficiaryExtractor] failed to extract data due to ${exception.getMessage}")
              Left(FailedToExtractData(DisplayTrustCompanyType.toString))
          }
      }
    }

  private def extractShareOfIncome(companyBeneficiary: DisplayTrustCompanyType, index: Int, answers: UserAnswers) = {
    companyBeneficiary.beneficiaryShareOfIncome match {
      case Some(income) =>
        answers.set(CompanyBeneficiaryDiscretionYesNoPage(index), false)
          .flatMap(_.set(CompanyBeneficiaryShareOfIncomePage(index), income))
      case None =>
        // Assumption that user answered yes as the share of income is not provided
        answers.set(CompanyBeneficiaryDiscretionYesNoPage(index), true)
    }
  }

  private def extractAddress(companyBeneficiary: DisplayTrustCompanyType, index: Int, answers: UserAnswers) = {
    companyBeneficiary.identification.flatMap(_.address.convert) match {
      case Some(uk: UKAddress) =>
        answers.set(CompanyBeneficiaryAddressPage(index), uk)
          .flatMap(_.set(CompanyBeneficiaryAddressYesNoPage(index), true))
          .flatMap(_.set(CompanyBeneficiaryAddressUKYesNoPage(index), true))
      case Some(nonUk: InternationalAddress) =>
        answers.set(CompanyBeneficiaryAddressPage(index), nonUk)
          .flatMap(_.set(CompanyBeneficiaryAddressYesNoPage(index), true))
          .flatMap(_.set(CompanyBeneficiaryAddressUKYesNoPage(index), false))
      case None =>
        answers.set(CompanyBeneficiaryAddressYesNoPage(index), false)
    }
  }
}