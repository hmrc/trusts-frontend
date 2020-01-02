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

package utils

import javax.inject.Inject
import models.core.UserAnswers
import play.api.i18n.Messages
import utils.countryOptions.CountryOptions
import viewmodels.AnswerSection

class PrintUserAnswersHelper @Inject()(countryOptions: CountryOptions){

  def summary(draftId: String, userAnswers : UserAnswers)(implicit messages: Messages) : Seq[AnswerSection] = {
    val checkYourAnswersHelper = new CheckYourAnswersHelper(countryOptions)(userAnswers, draftId, canEdit = false)

    val trustDetails = checkYourAnswersHelper.trustDetails.getOrElse(Nil)
    val trustees = checkYourAnswersHelper.trustees.getOrElse(Nil)
    val settlors = checkYourAnswersHelper.deceasedSettlor.getOrElse(Nil)
    val livingSettlors = checkYourAnswersHelper.livingSettlors.getOrElse(Nil)
    val individualBeneficiaries = checkYourAnswersHelper.individualBeneficiaries.getOrElse(Nil)
    val individualBeneficiariesExist: Boolean = individualBeneficiaries.nonEmpty
    val classOfBeneficiaries = checkYourAnswersHelper.classOfBeneficiaries(individualBeneficiariesExist).getOrElse(Nil)

    val money = checkYourAnswersHelper.money
    val shares = checkYourAnswersHelper.shares
    val propertyOrLand = checkYourAnswersHelper.propertyOrLand

    val assetSection = Seq(AnswerSection(None, Nil, Some(messages("answerPage.section.assets.heading"))))

    val sections =
      trustDetails ++
      settlors ++
      livingSettlors ++
      trustees ++
      individualBeneficiaries ++
      classOfBeneficiaries ++
      assetSection ++
      money ++
      shares ++
      propertyOrLand

    sections
  }
}
