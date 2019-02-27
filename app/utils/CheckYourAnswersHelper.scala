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

package utils

import java.time.format.DateTimeFormatter

import controllers.routes
import models.{CheckMode, UserAnswers}
import pages._
import play.api.i18n.Messages
import play.twirl.api.{Html, HtmlFormat}
import utils.CheckYourAnswersHelper._
import viewmodels.AnswerRow

class CheckYourAnswersHelper(userAnswers: UserAnswers)(implicit messages: Messages) {

  def getTrusteeFirstLastName(index: Int): String = userAnswers.get(TrusteesNamePage(index)).get.toString

  def trusteeLiveInTheUK(index : Int): Option[AnswerRow] = userAnswers.get(TrusteeLiveInTheUKPage(index)) map {
    x =>
      AnswerRow(
        "trusteeLiveInTheUK.checkYourAnswersLabel",
        yesOrNo(x),
        routes.TrusteeLiveInTheUKController.onPageLoad(CheckMode, index).url,
        getTrusteeFirstLastName(index)
      )
  }

  def trusteesDateOfBirth(index : Int): Option[AnswerRow] = userAnswers.get(TrusteesDateOfBirthPage(index)) map {
    x =>
      AnswerRow(
        "trusteesDateOfBirth.checkYourAnswersLabel",
        HtmlFormat.escape(x.format(dateFormatter)),
        routes.TrusteesDateOfBirthController.onPageLoad(CheckMode, index).url,
        getTrusteeFirstLastName(index)
      )
  }

  def trusteeAUKCitizen(index : Int): Option[AnswerRow] = userAnswers.get(TrusteeAUKCitizenPage(index)) map {
    x =>
      AnswerRow(
        "trusteeAUKCitizen.checkYourAnswersLabel",
        yesOrNo(x),
        routes.TrusteeAUKCitizenController.onPageLoad(CheckMode,index).url,
        getTrusteeFirstLastName(index)
      )
  }

  def trusteeFullName(index : Int): Option[AnswerRow] = userAnswers.get(TrusteesNamePage(index)) map {
    x => AnswerRow(
      "trusteesName.checkYourAnswersLabel",
      HtmlFormat.escape(s"${x.firstName} ${x.middleName.getOrElse("")} ${x.lastName}"),
      routes.TrusteesNameController.onPageLoad(CheckMode, index).url
    )
  }

  def trusteeIndividualOrBusiness(index : Int): Option[AnswerRow] = userAnswers.get(TrusteeIndividualOrBusinessPage(index)) map {
    x =>
      AnswerRow(
        "trusteeIndividualOrBusiness.checkYourAnswersLabel",
        HtmlFormat.escape(messages(s"individualOrBusiness.$x")),
        routes.TrusteeIndividualOrBusinessController.onPageLoad(CheckMode, index).url
      )
  }

  def isThisLeadTrustee(index: Int): Option[AnswerRow] = userAnswers.get(IsThisLeadTrusteePage(index)) map {
    x =>
      AnswerRow(
        "isThisLeadTrustee.checkYourAnswersLabel",
        yesOrNo(x),
        routes.IsThisLeadTrusteeController.onPageLoad(CheckMode, index).url
      )
  }

  def postcodeForTheTrust: Option[AnswerRow] = userAnswers.get(PostcodeForTheTrustPage) map {
    x =>
      AnswerRow(
        "postcodeForTheTrust.checkYourAnswersLabel",
        HtmlFormat.escape(x),
        routes.PostcodeForTheTrustController.onPageLoad(CheckMode).url
      )
  }

  def whatIsTheUTR: Option[AnswerRow] = userAnswers.get(WhatIsTheUTRPage) map {
    x =>
      AnswerRow(
        "whatIsTheUTR.checkYourAnswersLabel",
        HtmlFormat.escape(x),
        routes.WhatIsTheUTRController.onPageLoad(CheckMode).url
      )
  }

  def trustHaveAUTR: Option[AnswerRow] = userAnswers.get(TrustHaveAUTRPage) map {
    x =>
      AnswerRow(
        "trustHaveAUTR.checkYourAnswersLabel",
        yesOrNo(x),
        routes.TrustHaveAUTRController.onPageLoad(CheckMode).url
      )
  }

  def trustRegisteredOnline: Option[AnswerRow] = userAnswers.get(TrustRegisteredOnlinePage) map {
    x =>
      AnswerRow(
        "trustRegisteredOnline.checkYourAnswersLabel",
        yesOrNo(x),
        routes.TrustRegisteredOnlineController.onPageLoad(CheckMode).url
      )
  }

  def whenTrustSetup: Option[AnswerRow] = userAnswers.get(WhenTrustSetupPage) map {
    x =>
      AnswerRow(
        "whenTrustSetup.checkYourAnswersLabel",
        HtmlFormat.escape(x.format(dateFormatter)),
        routes.WhenTrustSetupController.onPageLoad(CheckMode).url
      )
  }

  def agentOtherThanBarrister: Option[AnswerRow] = userAnswers.get(AgentOtherThanBarristerPage) map {
    x => AnswerRow("agentOtherThanBarrister.checkYourAnswersLabel", yesOrNo(x), routes.AgentOtherThanBarristerController.onPageLoad(CheckMode).url)
  }

  def inheritanceTaxAct: Option[AnswerRow] = userAnswers.get(InheritanceTaxActPage) map {
    x => AnswerRow("inheritanceTaxAct.checkYourAnswersLabel", yesOrNo(x), routes.InheritanceTaxActController.onPageLoad(CheckMode).url)
  }

  def nonresidentType: Option[AnswerRow] = userAnswers.get(NonResidentTypePage) map {
    x => AnswerRow("nonresidentType.checkYourAnswersLabel", answer("nonresidentType", x), routes.NonResidentTypeController.onPageLoad(CheckMode).url)
  }

  def trustPreviouslyResident: Option[AnswerRow] = userAnswers.get(TrustPreviouslyResidentPage) map {
    x => AnswerRow("trustPreviouslyResident.checkYourAnswersLabel", escape(x), routes.TrustPreviouslyResidentController.onPageLoad(CheckMode).url)
  }

  def trustResidentOffshore: Option[AnswerRow] = userAnswers.get(TrustResidentOffshorePage) map {
    x => AnswerRow("trustResidentOffshore.checkYourAnswersLabel", yesOrNo(x), routes.TrustResidentOffshoreController.onPageLoad(CheckMode).url)
  }

  def registeringTrustFor5A: Option[AnswerRow] = userAnswers.get(RegisteringTrustFor5APage) map {
    x => AnswerRow("registeringTrustFor5A.checkYourAnswersLabel", yesOrNo(x), routes.RegisteringTrustFor5AController.onPageLoad(CheckMode).url)
  }

  def establishedUnderScotsLaw: Option[AnswerRow] = userAnswers.get(EstablishedUnderScotsLawPage) map {
    x => AnswerRow("establishedUnderScotsLaw.checkYourAnswersLabel", yesOrNo(x), routes.EstablishedUnderScotsLawController.onPageLoad(CheckMode).url)
  }

  def trustResidentInUK: Option[AnswerRow] = userAnswers.get(TrustResidentInUKPage) map {
    x => AnswerRow("trustResidentInUK.checkYourAnswersLabel", yesOrNo(x), routes.TrustResidentInUKController.onPageLoad(CheckMode).url)
  }

  def countryAdministeringTrust: Option[AnswerRow] = userAnswers.get(CountryAdministeringTrustPage) map {
    x => AnswerRow("countryAdministeringTrust.checkYourAnswersLabel", escape(x), routes.CountryAdministeringTrustController.onPageLoad(CheckMode).url)
  }

  def administrationInsideUK: Option[AnswerRow] = userAnswers.get(AdministrationInsideUKPage) map {
    x => AnswerRow("administrationInsideUK.checkYourAnswersLabel", yesOrNo(x), routes.AdministrationInsideUKController.onPageLoad(CheckMode).url)
  }

  def countryGoverningTrust: Option[AnswerRow] = userAnswers.get(CountryGoverningTrustPage) map {
    x => AnswerRow("countryGoverningTrust.checkYourAnswersLabel", escape(x), routes.CountryGoverningTrustController.onPageLoad(CheckMode).url)
  }

  def governedInsideTheUK: Option[AnswerRow] = userAnswers.get(GovernedInsideTheUKPage) map {
    x => AnswerRow("governedInsideTheUK.checkYourAnswersLabel", yesOrNo(x), routes.GovernedInsideTheUKController.onPageLoad(CheckMode).url)
  }

  def trustName: Option[AnswerRow] = userAnswers.get(TrustNamePage) map {
    x => AnswerRow("trustName.checkYourAnswersLabel", escape(x), routes.TrustNameController.onPageLoad(CheckMode).url)
  }
}

object CheckYourAnswersHelper {
  private val dateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy")

  private def yesOrNo(answer: Boolean)(implicit messages: Messages): Html =
    if (answer) {
      HtmlFormat.escape(messages("site.yes"))
    } else {
      HtmlFormat.escape(messages("site.no"))
    }


  private def answer[T](key : String, answer: T)(implicit messages: Messages) : Html =
    HtmlFormat.escape(messages(s"$key.$answer"))

  private def escape(x : String) = HtmlFormat.escape(x)
}
