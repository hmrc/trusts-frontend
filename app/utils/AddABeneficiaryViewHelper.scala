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

import models.{FullName, UserAnswers}
import play.api.i18n.Messages
import viewmodels.addAnother.{ClassOfBeneficiaryViewModel, IndividualBeneficiaryViewModel}
import viewmodels.{AddRow, AddToRows, ClassOfBeneficiaries, IndividualBeneficiaries}

class AddABeneficiaryViewHelper(userAnswers: UserAnswers)(implicit messages: Messages) {

  private def parseName(name : Option[FullName]) : String = {
    name match {
      case Some(x) => s"$x"
      case None => ""
    }
  }

  private def parseIndividualBeneficiary(individualBeneficiary : IndividualBeneficiaryViewModel) : AddRow = {
    AddRow(
      parseName(individualBeneficiary.name),
      "Individual Beneficiary",
      "#",
      "#"
    )
  }

  private def parseClassOfBeneficiary(classOfBeneficiary : ClassOfBeneficiaryViewModel) : AddRow = {
    AddRow(
      classOfBeneficiary.description.getOrElse(""),
      "Class of beneficiaries",
      "#",
      "#"
    )
  }

  def rows : AddToRows = {
    val individualBeneficiaries = userAnswers.get(IndividualBeneficiaries).toList.flatten

    val indBeneficiaryComplete = individualBeneficiaries.filter(_.isComplete).map(parseIndividualBeneficiary)

    val indBenInProgress = individualBeneficiaries.filterNot(_.isComplete).map(parseIndividualBeneficiary)

    val classOfBeneficiaries = userAnswers.get(ClassOfBeneficiaries).toList.flatten

    val classOfBeneficiariesComplete = classOfBeneficiaries.filter(_.isComplete).map(parseClassOfBeneficiary)

    val classOfBeneficiariesInProgress = classOfBeneficiaries.filterNot(_.isComplete).map(parseClassOfBeneficiary)

    AddToRows(indBenInProgress ::: classOfBeneficiariesInProgress,
      indBeneficiaryComplete ::: classOfBeneficiariesComplete)
  }

}
