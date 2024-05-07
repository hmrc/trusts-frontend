/*
 * Copyright 2024 HM Revenue & Customs
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
                           (implicit messages: Messages, answerRowUtils: AnswerRowUtils): RegistrationAnswerSections = {
    RegistrationAnswerSections(
      beneficiaries = convert(sections.beneficiaries),
      trustees = convert(sections.trustees),
      protectors = convert(sections.protectors),
      otherIndividuals = convert(sections.otherIndividuals),
      trustDetails = convert(sections.trustDetails),
      settlors = convert(sections.settlors),
      assets = convert(sections.assets)
    )
  }

  private def convert(section: Option[List[RegistrationSubmission.AnswerSection]])
                     (implicit messages: Messages, answerRowUtils: AnswerRowUtils): Option[List[AnswerSection]] = {
    section map {
      _.map(convert(_))
    }
  }

  private def convert(section: RegistrationSubmission.AnswerSection)
                     (implicit messages: Messages, answerRowUtils: AnswerRowUtils): AnswerSection = {

    val checkSettlorAlive: Boolean =
      section.rows.exists(row =>
        row.label.contains("settlorAliveYesNo.checkYourAnswersLabel") &&
          row.answer == "No"
      )

    val rowsInCorrectTense =
      if (checkSettlorAlive) answerRowUtils.rowsWithCorrectTense(section) else section.rows

      AnswerSection(
        headingKey = section.headingKey.map(x => messages(x, section.headingArgs.map(answerRowUtils.reverseEngineerArg): _*)),
        rows = rowsInCorrectTense.map(convert(_)),
        sectionKey = section.sectionKey.map(messages(_))
      )
  }

  private def convert(row: RegistrationSubmission.AnswerRow)
                     (implicit messages: Messages, answerRowUtils: AnswerRowUtils): AnswerRow = AnswerRow(
    label = row.label,
    answer = HtmlFormat.raw(answerRowUtils.reverseEngineerAnswer(row.answer)),
    changeUrl = None,
    labelArgs = row.labelArgs.map(answerRowUtils.reverseEngineerArg),
    canEdit = false
  )

}
