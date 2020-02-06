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

import models.playback.UserAnswers
import models.registration.pages.TrusteesBasedInTheUK._
import models.registration.pages.{NonResidentType, TrusteesBasedInTheUK}
import pages.register.agents.AgentOtherThanBarristerPage
import pages.register.settlors.SettlorsBasedInTheUKPage
import pages.register.{NonResidentTypePage, TrusteesBasedInTheUKPage, _}
import play.api.i18n.Messages
import play.api.mvc.Call
import play.twirl.api.HtmlFormat
import queries.Gettable
import utils.CheckAnswersFormatters
import utils.countryOptions.CountryOptions
import utils.print.playback.sections.AnswerRowConverter._
import viewmodels.{AnswerRow, AnswerSection}


object TrustDetails {

  def apply(userAnswers: UserAnswers,
            countryOptions: CountryOptions)
           (implicit messages: Messages): Seq[AnswerSection] = {

    Seq(
      AnswerSection(
        headingKey = None,
        Seq(
          stringQuestion(TrustNamePage, userAnswers, "trustName"),
          dateQuestion(WhenTrustSetupPage, userAnswers, "whenTrustSetup"),
          yesNoQuestion(GovernedInsideTheUKPage, userAnswers, "governedInsideTheUK"),
          countryQuestion(CountryGoverningTrustPage, userAnswers, "countryGoverningTrust", countryOptions = countryOptions),
          yesNoQuestion(AdministrationInsideUKPage, userAnswers, "administrationInsideUK"),
          countryQuestion(CountryAdministeringTrustPage, userAnswers, "countryAdministeringTrust", countryOptions = countryOptions),
          howManyTrusteesBasedInUkQuestion(EstablishedUnderScotsLawPage, userAnswers, "trustResidentInUK"),
          yesNoQuestion(EstablishedUnderScotsLawPage, userAnswers, "establishedUnderScotsLaw"),
          yesNoQuestion(TrustResidentOffshorePage, userAnswers, "trustResidentOffshore"),
          countryQuestion(TrustPreviouslyResidentPage, userAnswers, "trustPreviouslyResident", countryOptions = countryOptions),
          yesNoQuestion(RegisteringTrustFor5APage, userAnswers, "registeringTrustFor5A"),
          yesNoQuestion(InheritanceTaxActPage, userAnswers, "inheritanceTaxAct"),
          yesNoQuestion(AgentOtherThanBarristerPage, userAnswers, "agentOtherThanBarrister")
        ).flatten,
        sectionKey = Some(messages("answerPage.section.trustsDetails.heading"))
      )
    )

  }

  private def countryQuestion(query: Gettable[String], userAnswers: UserAnswers, labelKey: String,
                              messageArg: String = "", countryOptions: CountryOptions, changeRoute: Option[Call] = None)
                             (implicit messages: Messages): Option[AnswerRow] = {
    userAnswers.get(query) map { x =>
      AnswerRow(
        messages(s"$labelKey.checkYourAnswersLabel", messageArg),
        HtmlFormat.escape(CheckAnswersFormatters.country(x, countryOptions)),
        None,
        canEdit = false
      )
    }
  }

  private def howManyTrusteesBasedInUkQuestion(query: Gettable[Boolean], userAnswers: UserAnswers, labelKey: String,
                                               messageArg: String = "", changeRoute: Option[Call] = None)
                                     (implicit messages: Messages): Option[AnswerRow] = {
    Some(
      AnswerRow(
        messages(s"$labelKey.checkYourAnswersLabel", messageArg),
        HtmlFormat.escape(trusteesBasedInUk(userAnswers.get(query), messages)),
        None,
        canEdit = false
      )
    )
  }

  private def trusteesBasedInUk(data: Option[Boolean], messages: Messages) = {
    data match {
      case Some(_) =>
        messages("trustResidentInTheUK.yes")
      case None =>
        messages("trustResidentInTheUK.no")
    }
  }

}
