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
import models.core.pages.{Address, InternationalAddress, UKAddress}
import models.playback.{AddressType, DisplayTrustCharityType, MetaData, UserAnswers}
import pages.beneficiaries.charity._
import play.api.Logger

import scala.util.{Failure, Success, Try}

class CharityBeneficiaryExtractor @Inject() extends PlaybackExtractor[List[DisplayTrustCharityType]] {

  import PlaybackAddressImplicits._

  override def extract(answers: UserAnswers, data: List[DisplayTrustCharityType]): Either[PlaybackExtractionError, Try[UserAnswers]] =
    {
      data match {
        case Nil => Right(Success(answers))
        case charities =>

          val updated = charities.zipWithIndex.foldLeft[Try[UserAnswers]](Success(answers)){ case (answers, (charityBeneficiary, index)) =>
            answers.flatMap(_.set(CharityBeneficiaryNamePage(index), charityBeneficiary.organisationName))
              .flatMap(_.set(CharityBeneficiaryDiscretionYesNoPage(index), charityBeneficiary.beneficiaryDiscretion))
              .flatMap(_.set(CharityBeneficiaryShareOfIncomePage(index), charityBeneficiary.beneficiaryShareOfIncome))
              .flatMap(_.set(CharityBeneficiaryUtrPage(index), charityBeneficiary.identification.flatMap(_.utr)))
              .flatMap(_.set(CharityBeneficiarySafeIdPage(index), charityBeneficiary.identification.flatMap(_.safeId)))
              .flatMap { answers =>
                charityBeneficiary.identification.flatMap(_.address.convert) match {
                  case Some(uk: UKAddress) =>
                    answers.set(CharityBeneficiaryAddressPage(index), uk)
                      .flatMap(_.set(CharityBeneficiaryAddressUKYesNoPage(index), true))
                  case Some(nonUk: InternationalAddress) =>
                    answers.set(CharityBeneficiaryAddressPage(index), nonUk)
                      .flatMap(_.set(CharityBeneficiaryAddressUKYesNoPage(index), false))
                  case _ => Success(answers)
                }
              }
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

          println(updated)

          updated match {
            case Success(a) =>
              Right(Success(a))
            case Failure(exception) =>
              Logger.warn(s"$exception")
              Left(FailedToExtractData(DisplayTrustCharityType.toString))
          }
      }
    }

//  def extractDiscretion(userAnswers: UserAnswers, charity: DisplayTrustCharityType): UserAnswers = {
//    charity.beneficiaryDiscretion match {
//      case Some(discretion) => userAnswers.set()
//      case None => ???
//    }
//  }

}