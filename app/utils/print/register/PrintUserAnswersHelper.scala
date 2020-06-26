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

package utils.print.register

import javax.inject.Inject
import models.core.UserAnswers
import play.api.i18n.Messages
import utils.CheckYourAnswersHelper
import utils.countryOptions.CountryOptions
import viewmodels.AnswerSection

class PrintUserAnswersHelper @Inject()(countryOptions: CountryOptions){

  def summary(draftId: String, userAnswers : UserAnswers)(implicit messages: Messages) : Seq[AnswerSection] = {

    val helper = new CheckYourAnswersHelper(countryOptions)(userAnswers, draftId, canEdit = false)

    val entitySections = List(
      helper.trustDetails,
      helper.deceasedSettlor,
      helper.livingSettlors,
      helper.trustees,
      helper.individualBeneficiaries,
      helper.classOfBeneficiaries(helper.individualBeneficiaries.exists(_.nonEmpty))
    ).flatten.flatten

    val assetSections = List(
      Seq(AnswerSection(None, Nil, Some(messages("answerPage.section.assets.heading")))),
      helper.money,
      helper.shares,
      helper.propertyOrLand,
      helper.partnership,
      helper.other
    ).flatten

    List(
      entitySections,
      assetSections
    ).flatten
  }
}
