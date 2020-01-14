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

import models.playback.UserAnswers
import models.playback.http.DisplaySharesType
import models.registration.pages.ShareClass._
import models.registration.pages.{ShareClass, ShareType}
import models.registration.pages.ShareType.{Quoted, Unquoted}
import pages.QuestionPage
import pages.register.asset.shares._

import scala.util.{Success, Try}

class SharesAssetExtractor {

  def extract(answers: Try[UserAnswers], index: Int, share: DisplaySharesType): Try[UserAnswers] = {
    share.shareClass match {
      case Some(shareClass) =>
        shareClass match {
          case Other =>
            answers
              .flatMap(_.set(SharesInAPortfolioPage(index), true))
              .flatMap(_.set(SharePortfolioNamePage(index), share.orgName))
              .flatMap(answers => extractUtr(share.utr, index, answers))
              .flatMap(answers => extractAreSharesListedOnStockExchange(share.typeOfShare, index, answers))
              .flatMap(answers => extractShareClass(share.shareClass, index, answers))
              .flatMap(answers => extractQuantity(share.numberOfShares, SharePortfolioQuantityInTrustPage(index), answers))
              .flatMap(answers => extractValue(share.value, index, answers))
          case _ =>
            answers
              .flatMap(_.set(SharesInAPortfolioPage(index), false))
              .flatMap(_.set(ShareCompanyNamePage(index), share.orgName))
              .flatMap(answers => extractUtr(share.utr, index, answers))
              .flatMap(answers => extractAreSharesListedOnStockExchange(share.typeOfShare, index, answers))
              .flatMap(answers => extractShareClass(share.shareClass, index, answers))
              .flatMap(answers => extractQuantity(share.numberOfShares, ShareQuantityInTrustPage(index), answers))
              .flatMap(answers => extractValue(share.value, index, answers))
        }
      case _ =>
        answers
    }
  }

  private def extractAreSharesListedOnStockExchange(data: Option[ShareType], index: Int, answers: UserAnswers): Try[UserAnswers] = {
    data match {
      case Some(shareType) =>
        shareType match {
          case Quoted =>
            answers.set(SharesOnStockExchangePage(index), true)
          case Unquoted =>
            answers.set(SharesOnStockExchangePage(index), false)
        }
      case _ =>
        Success(answers)
    }
  }

  private def extractUtr(data: Option[String], index: Int, answers: UserAnswers) = {
    data match {
      case Some(utr) =>
        answers.set(ShareUtrPage(index), utr)
      case _ =>
        Success(answers)
    }
  }

  private def extractShareClass(data: Option[ShareClass], index: Int, answers: UserAnswers) = {
    data match {
      case Some(shareClass) =>
        answers.set(ShareClassPage(index), shareClass)
      case _ =>
        Success(answers)
    }
  }

  private def extractQuantity(data: Option[String], page: QuestionPage[String], answers: UserAnswers) = {
    data match {
      case Some(quantity) =>
        answers.set(page, quantity)
      case _ =>
        Success(answers)
    }
  }

  private def extractValue(data: Option[Long], index: Int, answers: UserAnswers) = {
    data match {
      case Some(value) =>
        answers.set(ShareValueInTrustPage(index), value.toString)
      case _ =>
        Success(answers)
    }
  }

}