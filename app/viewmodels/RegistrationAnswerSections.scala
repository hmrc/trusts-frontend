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

package viewmodels

import models.RegistrationSubmission
import models.RegistrationSubmission.AllAnswerSections
import play.api.i18n.Messages
import play.twirl.api.HtmlFormat
import utils.AnswerRowUtils

case class RegistrationAnswerSections(beneficiaries: Option[List[AnswerSection]] = None,
                                      trustees: Option[List[AnswerSection]] = None,
                                      protectors: Option[List[AnswerSection]] = None,
                                      otherIndividuals: Option[List[AnswerSection]] = None,
                                      trustDetails: Option[List[AnswerSection]] = None,
                                      settlors: Option[List[AnswerSection]] = None,
                                      assets: Option[List[AnswerSection]] = None)

object RegistrationAnswerSections {

  def fromAllAnswerSections(sections: AllAnswerSections)
                           (answerRowUtils: AnswerRowUtils)
                           (implicit messages: Messages): RegistrationAnswerSections = {
    RegistrationAnswerSections(
      beneficiaries = convert(sections.beneficiaries)(answerRowUtils),
      trustees = convert(sections.trustees)(answerRowUtils),
      protectors = convert(sections.protectors)(answerRowUtils),
      otherIndividuals = convert(sections.otherIndividuals)(answerRowUtils),
      trustDetails = convert(sections.trustDetails)(answerRowUtils),
      settlors = convert(sections.settlors)(answerRowUtils),
      assets = convert(sections.assets)(answerRowUtils)
    )
  }

  private def convert(section: Option[List[RegistrationSubmission.AnswerSection]])
                     (answerRowUtils: AnswerRowUtils)
                     (implicit messages: Messages): Option[List[AnswerSection]] = {
    section map {
      _.map(convert(_)(answerRowUtils))
    }
  }

  private def convert(section: RegistrationSubmission.AnswerSection)
                     (answerRowUtils: AnswerRowUtils)
                     (implicit messages: Messages): AnswerSection = AnswerSection(
    headingKey = section.headingKey.map(x => messages(x, section.headingArg.getOrElse(""))),
    rows = section.rows.map(convert(_)(answerRowUtils)),
    sectionKey = section.sectionKey.map(messages(_))
  )

  private def convert(row: RegistrationSubmission.AnswerRow)
                     (answerRowUtils: AnswerRowUtils)
                     (implicit messages: Messages): AnswerRow = AnswerRow(
    label = row.label,
    answer = HtmlFormat.raw(answerRowUtils.reverseEngineerAnswer(row.answer)),
    changeUrl = None,
    labelArg = row.labelArg,
    canEdit = false
  )

}
