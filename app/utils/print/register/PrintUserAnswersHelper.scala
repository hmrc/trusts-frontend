/*
 * Copyright 2026 HM Revenue & Customs
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

import play.api.i18n.Messages
import services.DraftRegistrationService
import uk.gov.hmrc.http.HeaderCarrier
import viewmodels.AnswerSection

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class PrintUserAnswersHelper @Inject() (draftRegistrationService: DraftRegistrationService)(implicit
  ec: ExecutionContext
) {

  def summary(draftId: String)(implicit hc: HeaderCarrier, messages: Messages): Future[List[AnswerSection]] =

    draftRegistrationService.getAnswerSections(draftId).map { registrationAnswerSections =>
      val entitySectionsHead = List(
        registrationAnswerSections.trustDetails,
        registrationAnswerSections.trustees,
        registrationAnswerSections.beneficiaries,
        registrationAnswerSections.settlors,
        registrationAnswerSections.assets
      ).flatten.flatten

      val entitySectionsTail = List(
        registrationAnswerSections.protectors,
        registrationAnswerSections.otherIndividuals
      ).flatten.flatten

      List(
        entitySectionsHead,
        entitySectionsTail
      ).flatten

    }

}
