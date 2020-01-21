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
import mapping.playback.PlaybackExtractionErrors.{FailedToExtractData, InvalidExtractorState, PlaybackExtractionError}
import mapping.playback.PlaybackExtractor
import mapping.playback.PlaybackImplicits._
import models.core.pages.{Address, Description, InternationalAddress, UKAddress}
import models.playback.http.{DisplayTrustCompanyType, DisplayTrustIdentificationOrgType, DisplayTrustLargeType}
import models.playback.{MetaData, UserAnswers}
import pages.register.beneficiaries.large._
import play.api.Logger

import scala.util.{Failure, Success, Try}

class LargeBeneficiaryExtractor @Inject() extends PlaybackExtractor[Option[List[DisplayTrustLargeType]]] {

  override def extract(answers: UserAnswers, data: Option[List[DisplayTrustLargeType]]): Either[PlaybackExtractionError, UserAnswers] = {
    data match {
      case None => Left(FailedToExtractData("No Large Beneficiary"))
      case Some(largeBeneficiaries) =>

        val updated = largeBeneficiaries.zipWithIndex.foldLeft[Try[UserAnswers]](Success(answers)) {
          case (answers, (largeBeneficiary, index)) =>

            answers
              .flatMap(_.set(LargeBeneficiaryNamePage(index), largeBeneficiary.organisationName))
              .flatMap(answers => extractShareOfIncome(largeBeneficiary, index, answers))
              .flatMap(answers => extractIdentification(largeBeneficiary.identification, index, answers))
              .flatMap(
                _.set(
                  LargeBeneficiaryDescriptionPage(index),
                  Description(
                    largeBeneficiary.description,
                    largeBeneficiary.description1,
                    largeBeneficiary.description2,
                    largeBeneficiary.description3,
                    largeBeneficiary.description4
                  )
                )
              )
              .flatMap(answers => extractNumberOfBeneficiary(largeBeneficiary, index, answers))
              .flatMap(_.set(LargeBeneficiarySafeIdPage(index), largeBeneficiary.identification.flatMap(_.safeId)))
              .flatMap {
                _.set(
                  LargeBeneficiaryMetaData(index),
                  MetaData(
                    lineNo = largeBeneficiary.lineNo,
                    bpMatchStatus = largeBeneficiary.bpMatchStatus,
                    entityStart = largeBeneficiary.entityStart
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

  private def extractIdentification(identification: Option[DisplayTrustIdentificationOrgType], index: Int, answers: UserAnswers) = {
    identification map {
      case DisplayTrustIdentificationOrgType(_, Some(utr), None) =>
        answers.set(LargeBeneficiaryUtrPage(index), utr)
          .flatMap(_.set(LargeBeneficiaryAddressYesNoPage(index), false))

      case DisplayTrustIdentificationOrgType(_, None, Some(address)) =>
        extractAddress(address.convert, index, answers)

      case _ =>
        Logger.error(s"[LargeBeneficiaryExtractor] only both utr and address parsed")
        Failure(InvalidExtractorState)

    } getOrElse {
      answers.set(LargeBeneficiaryAddressYesNoPage(index), false)
    }
  }

  private def extractShareOfIncome(largeBeneficiary: DisplayTrustLargeType, index: Int, answers: UserAnswers) = {
    largeBeneficiary.beneficiaryShareOfIncome match {
      case Some(income) =>
        answers.set(LargeBeneficiaryDiscretionYesNoPage(index), false)
          .flatMap(_.set(LargeBeneficiaryShareOfIncomePage(index), income))
      case None =>
        // Assumption that user answered yes as the share of income is not provided
        answers.set(LargeBeneficiaryDiscretionYesNoPage(index), true)
    }
  }

  private def extractNumberOfBeneficiary(largeBeneficiary: DisplayTrustLargeType, index: Int, answers: UserAnswers): Try[UserAnswers] = {
    largeBeneficiary.numberOfBeneficiary.toInt match {
      case x if 1 to 100 contains x => answers.set(LargeBeneficiaryNumberOfBeneficiariesPage(index), "1 to 100")
      case x if 101 to 200 contains x => answers.set(LargeBeneficiaryNumberOfBeneficiariesPage(index), "101 to 200")
      case x if 201 to 500 contains x => answers.set(LargeBeneficiaryNumberOfBeneficiariesPage(index), "201 to 500")
      case x if 501 to 1000 contains x => answers.set(LargeBeneficiaryNumberOfBeneficiariesPage(index), "501 to 1,000")
      case _ => answers.set(LargeBeneficiaryNumberOfBeneficiariesPage(index), "Over 1,001")
    }
  }

  private def extractAddress(address: Address, index: Int, answers: UserAnswers) = {
    address match {
      case uk: UKAddress =>
        answers.set(LargeBeneficiaryAddressPage(index), uk)
          .flatMap(_.set(LargeBeneficiaryAddressYesNoPage(index), true))
          .flatMap(_.set(LargeBeneficiaryAddressUKYesNoPage(index), true))
      case nonUk: InternationalAddress =>
        answers.set(LargeBeneficiaryAddressPage(index), nonUk)
          .flatMap(_.set(LargeBeneficiaryAddressYesNoPage(index), true))
          .flatMap(_.set(LargeBeneficiaryAddressUKYesNoPage(index), false))
    }
  }
}