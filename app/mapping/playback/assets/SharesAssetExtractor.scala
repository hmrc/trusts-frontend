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

package mapping.playback.assets

import com.google.inject.Inject
import mapping.playback.PlaybackExtractionErrors.{FailedToExtractData, PlaybackExtractionError}
import mapping.playback.PlaybackExtractor
import models.playback.UserAnswers
import models.playback.http.{DisplaySharesType, DisplayTrustTrusteeOrgType}
import models.registration.pages.ShareClass._
import models.registration.pages.ShareType
import models.registration.pages.ShareType.{Quoted, Unquoted}
import pages.register.asset.shares._
import play.api.Logger

import scala.util.{Failure, Success, Try}

class SharesAssetExtractor @Inject() extends PlaybackExtractor[Option[List[DisplaySharesType]]] {

  override def extract(answers: UserAnswers, data: Option[List[DisplaySharesType]]): Either[PlaybackExtractionError, UserAnswers] = {
    data match {
      case None => Left(FailedToExtractData("No Shares Asset"))
      case Some(shares) =>

        Logger.debug(s"[SharesAssetExtractor] extracting $shares")

        val updated = shares.zipWithIndex.foldLeft[Try[UserAnswers]](Success(answers)){
          case (answers, (share, index)) =>

            share.shareClass match {
              case Other =>
                answers
                  .flatMap(_.set(SharesInAPortfolioPage(index), true))
                  .flatMap(_.set(SharePortfolioNamePage(index), share.orgName))
                  .flatMap(answers => extractAreSharesListedOnStockExchange(share.typeOfShare, index, answers))
                  .flatMap(_.set(ShareClassPage(index), share.shareClass))
                  .flatMap(_.set(SharePortfolioQuantityInTrustPage(index), share.numberOfShares))
                  .flatMap(_.set(ShareValueInTrustPage(index), share.value.toString))
              case _ =>
                answers
                  .flatMap(_.set(SharesInAPortfolioPage(index), false))
                  .flatMap(_.set(ShareCompanyNamePage(index), share.orgName))
                  .flatMap(answers => extractAreSharesListedOnStockExchange(share.typeOfShare, index, answers))
                  .flatMap(_.set(ShareClassPage(index), share.shareClass))
                  .flatMap(_.set(ShareQuantityInTrustPage(index), share.numberOfShares))
                  .flatMap(_.set(ShareValueInTrustPage(index), share.value.toString))
            }
          }

        updated match {
          case Success(a) =>
            Right(a)
          case Failure(exception) =>
            Logger.warn(s"[SharesAssetExtractor] failed to extract data due to ${exception.getMessage}")
            Left(FailedToExtractData(DisplayTrustTrusteeOrgType.toString))
        }
    }
  }

  private def extractAreSharesListedOnStockExchange(shareType: ShareType, index: Int, answers: UserAnswers): Try[UserAnswers] = {
    shareType match {
      case Quoted =>
        answers.set(SharesOnStockExchangePage(index), true)
      case Unquoted =>
        answers.set(SharesOnStockExchangePage(index), false)
    }
  }

}