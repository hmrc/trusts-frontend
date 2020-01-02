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

package mapping.playback

import com.google.inject.Inject
import mapping.playback.PlaybackExtractionErrors.{FailedToExtractData, PlaybackExtractionError}
import models.core.pages.{IndividualOrBusiness, InternationalAddress, UKAddress}
import models.playback.http.DisplayTrustLeadTrusteeIndType
import models.playback.{MetaData, UserAnswers}
import pages.register.trustees._
import play.api.Logger

import scala.util.{Failure, Success, Try}

class LeadTrusteeIndExtractor @Inject() extends PlaybackExtractor[Option[DisplayTrustLeadTrusteeIndType]] {

  import PlaybackImplicits._

  override def extract(answers: UserAnswers, data: Option[DisplayTrustLeadTrusteeIndType]): Either[PlaybackExtractionError, UserAnswers] =
    {
      data match {
        case None => Left(FailedToExtractData("No Lead Trustee Ind"))
        case leadTrustee =>

          val updated = leadTrustee.foldLeft[Try[UserAnswers]](Success(answers)){
            case (answers, leadTrustee) =>

              answers
                .flatMap(_.set(IsThisLeadTrusteePage(0), true))
                .flatMap(_.set(TrusteeIndividualOrBusinessPage(0), IndividualOrBusiness.Individual))
                .flatMap(_.set(TrusteesNamePage(0), leadTrustee.name.convert))
                .flatMap(_.set(TrusteesDateOfBirthPage(0), leadTrustee.dateOfBirth.convert))
                .flatMap(answers => extractNino(leadTrustee, answers))
                .flatMap(answers => extractPassportOrIDCard(leadTrustee, answers))
                .flatMap(answers => extractAddress(leadTrustee, answers))
                .flatMap(_.set(TelephoneNumberPage(0), leadTrustee.phoneNumber))
                .flatMap(_.set(EmailPage(0), leadTrustee.email))
                .flatMap(_.set(TrusteesSafeIdPage(0), leadTrustee.identification.safeId))
                .flatMap {
                  _.set(
                    LeadTrusteeMetaData(0),
                    MetaData(
                      lineNo = leadTrustee.lineNo,
                      bpMatchStatus = leadTrustee.bpMatchStatus,
                      entityStart = leadTrustee.entityStart
                    )
                  )
                }
            }

          updated match {
            case Success(a) =>
              Right(a)
            case Failure(exception) =>
              Logger.warn(s"[LeadTrusteeIndExtractor] failed to extract data due to ${exception.getMessage}")
              Left(FailedToExtractData(DisplayTrustLeadTrusteeIndType.toString))
          }
      }
    }

  private def extractNino(leadTrustee: DisplayTrustLeadTrusteeIndType, answers: UserAnswers) = {
    leadTrustee.identification.nino match {
      case Some(nino) =>
        answers.set(TrusteeAUKCitizenPage(0), true)
          .flatMap(_.set(TrusteesNinoPage(0), nino))
      case None =>
        // Assumption that user answered no as nino is not provided
        answers.set(TrusteeAUKCitizenPage(0), false)
    }
  }

  private def extractPassportOrIDCard(leadTrustee: DisplayTrustLeadTrusteeIndType, answers: UserAnswers) = {
    leadTrustee.identification.passport match {
      case Some(passport) =>
        answers.set(TrusteePassportIDCardPage(0), passport.convert)
      case None =>
        Success(answers)
    }
  }

  private def extractAddress(leadTrusteeInd: DisplayTrustLeadTrusteeIndType, answers: UserAnswers) = {
    leadTrusteeInd.identification.address.convert match {
      case Some(uk: UKAddress) =>
        answers.set(TrusteesUkAddressPage(0), uk)
          .flatMap(_.set(TrusteeLiveInTheUKPage(0), true))
      case Some(nonUk: InternationalAddress) =>
        answers.set(TrusteesInternationalAddressPage(0), nonUk)
          .flatMap(_.set(TrusteeLiveInTheUKPage(0), false))
      case None =>
        Success(answers)
    }
  }

}