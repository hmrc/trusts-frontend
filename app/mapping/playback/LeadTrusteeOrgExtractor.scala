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
import models.core.pages.{IndividualOrBusiness, InternationalAddress, UKAddress}
import models.playback.http.DisplayTrustLeadTrusteeOrgType
import models.playback.{MetaData, UserAnswers}
import pages.register.trustees._
import play.api.Logger

import scala.util.{Failure, Success, Try}

class LeadTrusteeOrgExtractor @Inject() extends PlaybackExtractor[Option[DisplayTrustLeadTrusteeOrgType]] {

  import PlaybackImplicits._

  override def extract(answers: UserAnswers, data: Option[DisplayTrustLeadTrusteeOrgType]): Either[PlaybackExtractionError, UserAnswers] =
    {
      data match {
        case None => Left(FailedToExtractData("No Lead Trustee Org"))
        case leadTrustee =>

          val updated = leadTrustee.foldLeft[Try[UserAnswers]](Success(answers)){
            case (answers, leadTrustee) =>

              answers
                .flatMap(_.set(IsThisLeadTrusteePage(0), true))
                .flatMap(_.set(TrusteeIndividualOrBusinessPage(0), IndividualOrBusiness.Business))
                .flatMap(_.set(TrusteeOrgNamePage(0), leadTrustee.name))
                .flatMap(answers => extractUtr(leadTrustee, answers))
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
              Logger.warn(s"[LeadTrusteeOrgExtractor] failed to extract data due to ${exception.getMessage}")
              Left(FailedToExtractData(DisplayTrustLeadTrusteeOrgType.toString))
          }
      }
    }

  private def extractUtr(leadTrustee: DisplayTrustLeadTrusteeOrgType, answers: UserAnswers) = {
    leadTrustee.identification.utr match {
      case Some(utr) =>
        answers.set(TrusteeAUKBusinessPage(0), true)
          .flatMap(_.set(TrusteesUtrPage(0), utr))
      case None =>
        // Assumption that user answered no as utr is not provided
        answers.set(TrusteeAUKBusinessPage(0), false)
    }
  }

  private def extractAddress(leadTrusteeOrg: DisplayTrustLeadTrusteeOrgType, answers: UserAnswers) = {
    leadTrusteeOrg.identification.address.convert match {
      case Some(uk: UKAddress) =>
        answers.set(TrusteesUkAddressPage(0), uk)
          .flatMap(_.set(TrusteeLiveInTheUKPage(0), true))
      case Some(nonUk: InternationalAddress) =>
        answers.set(TrusteesInternationalAddressPage(0), nonUk)
          .flatMap(_.set(TrusteeLiveInTheUKPage(0), false))
      case None => Success(answers)
    }
  }

}