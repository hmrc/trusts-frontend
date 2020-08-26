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

package models

import java.time.LocalDateTime

import models.core.UserAnswers
import models.registration.pages.Status
import models.registration.pages.Status.Completed
import play.api.libs.json.{JsValue, Json, OFormat}
import utils.TaxLiabilityHelper

case class SubmissionDraftData(data: JsValue, reference: Option[String], inProgress: Option[Boolean])

object SubmissionDraftData {
  implicit lazy val format: OFormat[SubmissionDraftData] = Json.format[SubmissionDraftData]
}
case class SubmissionDraftResponse(createdAt: LocalDateTime, data: JsValue, reference: Option[String])

object SubmissionDraftResponse {
  implicit lazy val format: OFormat[SubmissionDraftResponse] = Json.format[SubmissionDraftResponse]
}

case class SubmissionDraftId(draftId: String, createdAt: LocalDateTime, reference: Option[String])

object SubmissionDraftId {
  implicit lazy val format: OFormat[SubmissionDraftId] = Json.format[SubmissionDraftId]
}

object RegistrationSubmission {
  // Answer row and section, for display in print summary.
  case class AnswerRow(label: String, answer: String, labelArg: String)

  object AnswerRow {
    implicit lazy val format: OFormat[AnswerRow] = Json.format[AnswerRow]
  }

  case class AnswerSection(headingKey: Option[String],
                           rows: Seq[AnswerRow],
                           sectionKey: Option[String])

  object AnswerSection {
    implicit lazy val format: OFormat[AnswerSection] = Json.format[AnswerSection]
  }

  case class AllStatus(
                        beneficiaries: Option[Status] = None,
                        trustees: Option[Status] = None,
                        taxLiability: Option[Status] = None,
                        protectors: Option[Status] = None,
                        otherIndividuals: Option[Status] = None
                      ) {
    def allComplete(userAnswers: UserAnswers): Boolean = beneficiaries.contains(Completed) &&
                                trustees.contains(Completed) &&
                                protectors.contains(Completed) &&
                                otherIndividuals.contains(Completed) &&
      (taxLiability.contains(Completed) || !TaxLiabilityHelper.showTaxLiability(userAnswers))
  }

  object AllStatus {
    implicit lazy val format: OFormat[AllStatus] = Json.format[AllStatus]
    val withAllComplete = AllStatus(Some(Completed), Some(Completed), Some(Completed), Some(Completed), Some(Completed))
  }

  case class AllAnswerSections(
                                beneficiaries: Option[List[AnswerSection]],
                                trustees: Option[List[AnswerSection]],
                                protectors: Option[List[AnswerSection]],
                                otherIndividuals: Option[List[AnswerSection]]
                              )

  object AllAnswerSections {
    implicit lazy val format: OFormat[AllAnswerSections] = Json.format[AllAnswerSections]
  }

}
