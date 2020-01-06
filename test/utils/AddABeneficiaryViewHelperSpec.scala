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

import base.RegistrationSpecBase
import controllers.register.beneficiaries.routes
import models.core.pages.FullName
import models.registration.pages.Status.Completed
import models.registration.pages.WhatTypeOfBeneficiary
import pages.entitystatus.{ClassBeneficiaryStatus, IndividualBeneficiaryStatus}
import pages.register.beneficiaries.individual.{IndividualBeneficiaryIncomeYesNoPage, IndividualBeneficiaryNamePage}
import pages.register.beneficiaries.{ClassBeneficiaryDescriptionPage, WhatTypeOfBeneficiaryPage}
import viewmodels.AddRow

class AddABeneficiaryViewHelperSpec extends RegistrationSpecBase {

  def removeIndividualRoute(index : Int) =
    routes.RemoveIndividualBeneficiaryController.onPageLoad(index, fakeDraftId).url

  def removeClassRoute(index : Int) =
    routes.RemoveClassOfBeneficiaryController.onPageLoad(index, fakeDraftId).url

  "AddABeneficiaryViewHelper" when {

    ".row" must {

      "generate Nil for no user answers" in {
        val rows = new AddABeneficiaryViewHelper(emptyUserAnswers, fakeDraftId).rows
        rows.inProgress mustBe Nil
        rows.complete mustBe Nil
      }

      "generate rows from user answers for beneficiaries in progress" in {
        val userAnswers = emptyUserAnswers
          .set(WhatTypeOfBeneficiaryPage, WhatTypeOfBeneficiary.Individual).success.value
          .set(IndividualBeneficiaryIncomeYesNoPage(0), true).success.value
          .set(WhatTypeOfBeneficiaryPage, WhatTypeOfBeneficiary.ClassOfBeneficiary).success.value
          .set(ClassBeneficiaryDescriptionPage(0), "Future issues").success.value

        val rows = new AddABeneficiaryViewHelper(userAnswers, fakeDraftId).rows

        rows.inProgress mustBe List(
          AddRow("No name added", typeLabel = "Individual Beneficiary", "#", removeIndividualRoute(0)),
          AddRow("Future issues", typeLabel = "Class of beneficiaries", "#", removeClassRoute(0))
        )
        rows.complete mustBe Nil
      }

      "generate rows from user answers for complete beneficiaries" in {

        val userAnswers = emptyUserAnswers
          .set(WhatTypeOfBeneficiaryPage, WhatTypeOfBeneficiary.Individual).success.value
          .set(IndividualBeneficiaryNamePage(0), FullName("First", None, "Last")).success.value
          .set(IndividualBeneficiaryStatus(0), Completed).success.value
          .set(IndividualBeneficiaryNamePage(1), FullName("Second", None, "Last")).success.value
          .set(IndividualBeneficiaryStatus(1), Completed).success.value
          .set(WhatTypeOfBeneficiaryPage, WhatTypeOfBeneficiary.ClassOfBeneficiary).success.value
          .set(ClassBeneficiaryDescriptionPage(0), "Future issues").success.value
          .set(ClassBeneficiaryStatus(0), Completed).success.value

        val rows = new AddABeneficiaryViewHelper(userAnswers, fakeDraftId).rows
        rows.complete mustBe List(
          AddRow("First Last", typeLabel = "Individual Beneficiary", "#", removeIndividualRoute(0)),
          AddRow("Second Last", typeLabel = "Individual Beneficiary", "#", removeIndividualRoute(1)),
          AddRow("Future issues", typeLabel = "Class of beneficiaries", "#", removeClassRoute(0))
        )
        rows.inProgress mustBe Nil
      }

    }
  }
}
