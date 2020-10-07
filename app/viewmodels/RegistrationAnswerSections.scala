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

package viewmodels

import models.RegistrationSubmission
import models.RegistrationSubmission.AllAnswerSections
import play.twirl.api.HtmlFormat

case class RegistrationAnswerSections(
                                       beneficiaries: Option[List[AnswerSection]] = None,
                                       trustees: Option[List[AnswerSection]] = None,
                                       protectors: Option[List[AnswerSection]] = None,
                                       otherIndividuals: Option[List[AnswerSection]] = None,
                                       trustDetails: Option[List[AnswerSection]] = None
                                     )

object RegistrationAnswerSections {
  def fromAllAnswerSections(sections: AllAnswerSections): RegistrationAnswerSections = {
    RegistrationAnswerSections(
      beneficiaries = convert(sections.beneficiaries),
      trustees = convert(sections.trustees),
      protectors = convert(sections.protectors),
      otherIndividuals = convert(sections.otherIndividuals),
      trustDetails = convert(sections.trustDetails)
    )
  }

  private def convert(row: RegistrationSubmission.AnswerRow): AnswerRow =
    AnswerRow(row.label, HtmlFormat.raw(row.answer), None, row.labelArg, canEdit = false)

  private def convert(section: RegistrationSubmission.AnswerSection): AnswerSection =
    AnswerSection(section.headingKey, section.rows.map(convert), section.sectionKey)

  private def convert(section: Option[List[RegistrationSubmission.AnswerSection]]): Option[List[AnswerSection]] =
    section map { _.map(convert) }
}

