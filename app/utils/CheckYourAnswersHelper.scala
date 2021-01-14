/*
 * Copyright 2021 HM Revenue & Customs
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

import javax.inject.Inject
import models.NormalMode
import models.core.UserAnswers
import models.registration.Matched.Success
import pages.register._
import pages.register.agents._
import play.api.i18n.Messages
import play.twirl.api.HtmlFormat
import utils.CheckAnswersFormatters._
import utils.countryOptions.CountryOptions
import viewmodels.{AnswerRow, AnswerSection}

class CheckYourAnswersHelper @Inject()(countryOptions: CountryOptions)
                                      (userAnswers: UserAnswers, draftId: String, canEdit: Boolean)
                                      (implicit messages: Messages) {

  def trustDetails: Option[Seq[AnswerSection]] = {
    val isExistingTrust: Boolean = userAnswers.get(ExistingTrustMatched).contains(Success)

    val existingTrustRows = if (isExistingTrust) {
      Seq(
        trustRegisteredWithUkAddress,
        postcodeForTheTrust,
        whatIsTheUTR
      )
    } else {
      Nil
    }

    val questions = (trustName(canEdit) +: existingTrustRows).flatten

    if (questions.nonEmpty) Some(Seq(AnswerSection(None, questions, Some(messages("answerPage.section.trustsDetails.heading"))))) else None
  }

  def agentInternationalAddress: Option[AnswerRow] = userAnswers.get(AgentInternationalAddressPage) map {
    x =>
      AnswerRow(
        "site.address.international.checkYourAnswersLabel",
        internationalAddress(x, countryOptions),
        Some(controllers.register.agents.routes.AgentInternationalAddressController.onPageLoad(NormalMode, draftId).url),
        agencyName(userAnswers),
        canEdit = canEdit
      )
  }

  def agentUKAddress: Option[AnswerRow] = userAnswers.get(AgentUKAddressPage) map {
    x =>
      AnswerRow(
        "site.address.uk.checkYourAnswersLabel",
        ukAddress(x),
        Some(controllers.register.agents.routes.AgentUKAddressController.onPageLoad(NormalMode, draftId).url),
        agencyName(userAnswers),
        canEdit = canEdit
      )
  }

  def agentAddressYesNo: Option[AnswerRow] = userAnswers.get(AgentAddressYesNoPage) map {
    x =>
      AnswerRow(
        "agentAddressYesNo.checkYourAnswersLabel",
        yesOrNo(x),
        Some(controllers.register.agents.routes.AgentAddressYesNoController.onPageLoad(NormalMode, draftId).url),
        agencyName(userAnswers),
        canEdit = canEdit
      )
  }

  def agentName: Option[AnswerRow] = userAnswers.get(AgentNamePage) map {
    x =>
      AnswerRow(
        "agentName.checkYourAnswersLabel",
        HtmlFormat.escape(x),
        Some(controllers.register.agents.routes.AgentNameController.onPageLoad(NormalMode, draftId).url),
        canEdit = canEdit
      )
  }

  def agentInternalReference: Option[AnswerRow] = userAnswers.get(AgentInternalReferencePage) map {
    x =>
      AnswerRow(
        "agentInternalReference.checkYourAnswersLabel",
        HtmlFormat.escape(x),
        Some(controllers.register.agents.routes.AgentInternalReferenceController.onPageLoad(NormalMode, draftId).url),
        canEdit = canEdit
      )
  }

  def agenciesTelephoneNumber: Option[AnswerRow] = userAnswers.get(AgentTelephoneNumberPage) map {
    x =>
      AnswerRow(
        "agentTelephoneNumber.checkYourAnswersLabel",
        HtmlFormat.escape(x),
        Some(controllers.register.agents.routes.AgentTelephoneNumberController.onPageLoad(NormalMode, draftId).url),
        agencyName(userAnswers),
        canEdit = canEdit
      )
  }

  def postcodeForTheTrust: Option[AnswerRow] = userAnswers.get(PostcodeForTheTrustPage) map {
    x =>
      AnswerRow(
        "postcodeForTheTrust.checkYourAnswersLabel",
        HtmlFormat.escape(x),
        Some(controllers.register.routes.PostcodeForTheTrustController.onPageLoad(NormalMode, draftId).url),
        canEdit = canEdit
      )
  }

  def whatIsTheUTR: Option[AnswerRow] = userAnswers.get(WhatIsTheUTRPage) map {
    x =>
      AnswerRow(
        "whatIsTheUTR.checkYourAnswersLabel",
        HtmlFormat.escape(x),
        Some(controllers.register.routes.WhatIsTheUTRController.onPageLoad(NormalMode, draftId).url),
        canEdit = canEdit
      )
  }

  def trustHaveAUTR: Option[AnswerRow] = userAnswers.get(TrustHaveAUTRPage) map {
    x =>
      AnswerRow(
        "trustHaveAUTR.checkYourAnswersLabel",
        yesOrNo(x),
        Some(controllers.register.routes.TrustHaveAUTRController.onPageLoad(NormalMode, draftId).url),
        canEdit = canEdit
      )
  }

  def trustRegisteredOnline: Option[AnswerRow] = userAnswers.get(TrustRegisteredOnlinePage) map {
    x =>
      AnswerRow(
        "trustRegisteredOnline.checkYourAnswersLabel",
        yesOrNo(x),
        Some(controllers.register.routes.TrustRegisteredOnlineController.onPageLoad(NormalMode, draftId).url),
        canEdit = canEdit
      )
  }

  def trustName(canEdit: Boolean = canEdit): Option[AnswerRow] = userAnswers.get(MatchingNamePage) map {
    x => AnswerRow(
      "trustName.checkYourAnswersLabel",
      escape(x),
      Some(controllers.register.routes.MatchingNameController.onPageLoad(draftId).url),
      canEdit = canEdit
    )
  }

  def trustRegisteredWithUkAddress: Option[AnswerRow] = userAnswers.get(TrustRegisteredWithUkAddressYesNoPage) map {
    x =>
      AnswerRow(
        "trustRegisteredWithUkAddress.checkYourAnswersLabel",
        yesOrNo(x),
        canEdit = canEdit
      )
  }

}