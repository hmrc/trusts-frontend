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
import services.DraftRegistrationService
import uk.gov.hmrc.http.HeaderCarrier
import utils.CheckYourAnswersHelper
import utils.countryOptions.CountryOptions
import viewmodels.AnswerSection

import scala.concurrent.{ExecutionContext, Future}

class PrintUserAnswersHelper @Inject()(
                                        countryOptions: CountryOptions,
                                        draftRegistrationService: DraftRegistrationService
                                      )(implicit ec: ExecutionContext){

  def summary(draftId: String, userAnswers : UserAnswers)
             (implicit messages: Messages, hc: HeaderCarrier) : Future[List[AnswerSection]] = {

    draftRegistrationService.getAnswerSections(draftId).map {
      registrationAnswerSections =>

      val helper = new CheckYourAnswersHelper(countryOptions)(userAnswers, draftId, canEdit = false)

      val entitySectionsHead = List(
        registrationAnswerSections.trustDetails,
        registrationAnswerSections.trustees,
        registrationAnswerSections.beneficiaries,
        registrationAnswerSections.settlors
      ).flatten.flatten

      val entitySectionsTail = List(
        registrationAnswerSections.protectors,
        registrationAnswerSections.otherIndividuals
      ).flatten.flatten

      val assetSections = List(
        Seq(AnswerSection(None, Nil, Some(messages("answerPage.section.assets.heading")))),
        helper.money,
        helper.propertyOrLand,
        helper.shares,
        helper.businessAsset,
        helper.partnership,
        helper.other
      ).flatten

        List(
        entitySectionsHead,
        assetSections,
        entitySectionsTail
      ).flatten

    }
  }
}
