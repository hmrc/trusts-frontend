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

package mapping.playback.beneficiaries

import com.google.inject.Inject
import mapping.playback.PlaybackExtractionErrors.{FailedToExtractData, PlaybackExtractionError}
import mapping.playback.{PlaybackExtractor, PlaybackImplicits}
import models.core.pages.{InternationalAddress, UKAddress}
import models.playback.http.DisplayTrustIndividualDetailsType
import models.playback.{MetaData, UserAnswers}
import models.registration.pages.RoleInCompany
import pages.register.beneficiaries.individual._
import play.api.Logger

import scala.util.{Failure, Success, Try}

class IndividualBeneficiaryExtractor @Inject() extends PlaybackExtractor[Option[List[DisplayTrustIndividualDetailsType]]] {

  import PlaybackImplicits._

  override def extract(answers: UserAnswers, data: Option[List[DisplayTrustIndividualDetailsType]]): Either[PlaybackExtractionError, UserAnswers] =
    {
      data match {
        case None => Left(FailedToExtractData("No Individual Beneficiary"))
        case Some(individual) =>

          val updated = individual.zipWithIndex.foldLeft[Try[UserAnswers]](Success(answers)){
            case (answers, (individualBeneficiary, index)) =>

            answers
              .flatMap(_.set(IndividualBeneficiaryNamePage(index), individualBeneficiary.name.convert))
              .flatMap(_.set(IndividualBeneficiaryRoleInCompanyPage(index), individualBeneficiary.beneficiaryType.map(mapRoleInCompany)))
              .flatMap(answers => extractDateOfBirth(individualBeneficiary, index, answers))
              .flatMap(answers => extractShareOfIncome(individualBeneficiary, index, answers))
              .flatMap(answers => extractNino(individualBeneficiary, index, answers))
              .flatMap(answers => extractAddress(individualBeneficiary, index, answers))
              .flatMap(answers => extractPassportIdCard(individualBeneficiary, index, answers))
              .flatMap(_.set(IndividualBeneficiaryVulnerableYesNoPage(index), individualBeneficiary.vulnerableBeneficiary))
              .flatMap {
                _.set(
                  IndividualBeneficiaryMetaData(index),
                  MetaData(
                    lineNo = individualBeneficiary.lineNo,
                    bpMatchStatus = individualBeneficiary.bpMatchStatus,  // TODO does this need to be filtered as ClassOfBeneficiary ?
                    entityStart = individualBeneficiary.entityStart
                  )
                )
              }
              .flatMap(_.set(IndividualBeneficiarySafeIdPage(index), individualBeneficiary.identification.flatMap(_.safeId)))
          }

          updated match {
            case Success(a) =>
              Right(a)
            case Failure(exception) =>
              Logger.warn(s"[IndividualBeneficiaryExtractor] failed to extract data due to ${exception.getMessage}")
              Left(FailedToExtractData(DisplayTrustIndividualDetailsType.toString))
          }
      }
    }


  private def mapRoleInCompany(role: String): RoleInCompany = {
    role match {
      case "Director" => RoleInCompany.Director
      case "Employee" => RoleInCompany.Employee
      case "NA" => RoleInCompany.NA
    }
  }

  private def extractDateOfBirth(individualBeneficiary: DisplayTrustIndividualDetailsType, index: Int, answers: UserAnswers) = {
    individualBeneficiary.dateOfBirth match {
      case Some(dob) =>
        answers.set(IndividualBeneficiaryDateOfBirthYesNoPage(index), true)
          .flatMap(_.set(IndividualBeneficiaryDateOfBirthPage(index), dob.convert))
      case None =>
        // Assumption that user answered no as utr is not provided
        answers.set(IndividualBeneficiaryDateOfBirthYesNoPage(index), false)
    }
  }

  private def extractShareOfIncome(individualBeneficiary: DisplayTrustIndividualDetailsType, index: Int, answers: UserAnswers) = {
    individualBeneficiary.beneficiaryShareOfIncome match {
      case Some(income) =>
        answers.set(IndividualBeneficiaryIncomeYesNoPage(index), false)
          .flatMap(_.set(IndividualBeneficiaryIncomePage(index), income))
      case None =>
        // Assumption that user answered yes as the share of income is not provided
        answers.set(IndividualBeneficiaryIncomeYesNoPage(index), true)
    }
  }

  private def extractNino(individualBeneficiary: DisplayTrustIndividualDetailsType, index: Int, answers: UserAnswers) = {
    individualBeneficiary.identification.flatMap(_.nino) match {
      case Some(nino) =>
        answers.set(IndividualBeneficiaryNationalInsuranceYesNoPage(index), true)
          .flatMap(_.set(IndividualBeneficiaryNationalInsuranceNumberPage(index), nino))
      case None =>
        // Assumption that user answered no as nino is not provided
        answers.set(IndividualBeneficiaryNationalInsuranceYesNoPage(index), false)
    }
  }

    private def extractPassportIdCard(individualBeneficiary: DisplayTrustIndividualDetailsType, index: Int, answers: UserAnswers) = {
      (individualBeneficiary.identification.flatMap(_.passport), individualBeneficiary.identification.flatMap(_.address)) match {
        case (Some(passportIdCard), _) =>
          answers.set(IndividualBeneficiaryPassportIDCardYesNoPage(index), true)
            .flatMap(_.set(IndividualBeneficiaryPassportIDCardPage(index), passportIdCard.convert))
        case (None, Some(address)) =>
          answers.set(IndividualBeneficiaryPassportIDCardYesNoPage(index), false)
        case (_,_) => Success(answers)
      }
    }

  private def extractAddress(individualBeneficiary: DisplayTrustIndividualDetailsType, index: Int, answers: UserAnswers) = {
    (individualBeneficiary.identification.flatMap(_.address.convert), individualBeneficiary.identification.flatMap(_.nino)) match {
      case (Some(uk: UKAddress), _) =>
        answers.set(IndividualBeneficiaryAddressPage(index), uk)
          .flatMap(_.set(IndividualBeneficiaryAddressYesNoPage(index), true))
          .flatMap(_.set(IndividualBeneficiaryAddressUKYesNoPage(index), true))
      case (Some(nonUk: InternationalAddress), _) =>
        answers.set(IndividualBeneficiaryAddressPage(index), nonUk)
          .flatMap(_.set(IndividualBeneficiaryAddressYesNoPage(index), true))
          .flatMap(_.set(IndividualBeneficiaryAddressUKYesNoPage(index), false))
      case (None, None) =>
        answers.set(IndividualBeneficiaryAddressYesNoPage(index), false)
      case (_,_) => Success(answers)
    }
  }
}