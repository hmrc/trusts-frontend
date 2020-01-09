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

package utils.print.playback.sections

import mapping.TypeOfTrust
import models.playback.UserAnswers
import models.registration.pages.SettlorKindOfTrust._
import pages.register.settlors.deceased_settlor.SetupAfterSettlorDiedPage
import pages.register.settlors.living_settlor.SettlorKindOfTrustPage
import play.api.i18n.Messages
import play.api.mvc.Call
import play.twirl.api.HtmlFormat
import viewmodels.{AnswerRow, AnswerSection}

object TrustDetails {

  def apply(userAnswers: UserAnswers)(implicit messages: Messages): AnswerSection =
    AnswerSection(
      headingKey = Some(messages("answerPage.section.trustDetails.heading")),
      Seq(
        trustTypeQuestion(userAnswers, "trustDetailsTrustType")
      ).flatten,
      sectionKey = None
    )

  private def trustTypeQuestion(userAnswers: UserAnswers, labelKey: String,
                                messageArg: String = "", changeRoute: Option[Call] = None)
                                          (implicit messages: Messages) = {

    getTrustType(userAnswers) match {
      case Some(x) =>
        Some(
          AnswerRow(
            messages(s"${labelKey}.checkYourAnswersLabel", messageArg),
            HtmlFormat.escape(x),
            None
          )
        )
      case _ => None
    }

  }

  private def getTrustType(userAnswers: UserAnswers): Option[String] = {
    userAnswers.get(SetupAfterSettlorDiedPage) match {
      case Some(true) =>
        Some(TypeOfTrust.WillTrustOrIntestacyTrust.toString)
      case Some(false) =>
        userAnswers.get(SettlorKindOfTrustPage).map {
          case Intervivos =>
            TypeOfTrust.IntervivosSettlementTrust.toString
          case Deed =>
            TypeOfTrust.DeedOfVariation.toString
          case Employees =>
            TypeOfTrust.EmployeeRelated.toString
          case FlatManagement =>
            TypeOfTrust.FlatManagementTrust.toString
          case HeritageMaintenanceFund =>
            TypeOfTrust.HeritageTrust.toString
        }
      case _ => None
    }
  }

}
