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

import base.SpecBase
import models.{FullName, TrusteeOrIndividual, UserAnswers}
import pages.{TrusteeOrIndividualPage, TrusteesNamePage}
import viewmodels.TrusteeRow

class AddATrusteeViewHelperSpec extends SpecBase {

  val userAnswersWithTrustees = UserAnswers(userAnswersId)
    .set(TrusteesNamePage(0), FullName("First 0", None, "Last 0")).success.value
    .set(TrusteeOrIndividualPage(0), TrusteeOrIndividual.Individual).success.value
    .set(TrusteesNamePage(1), FullName("First 1", None, "Last 1")).success.value
    .set(TrusteeOrIndividualPage(1), TrusteeOrIndividual.Business).success.value

  val userAnswersWithNoTrustees = UserAnswers(userAnswersId)

  "AddATrusteeViewHelper" when {

    ".row" must {

      "generate Nil for a user answers with no trustees" in {
        val rows = new AddATrusteeViewHelper(userAnswersWithNoTrustees).rows
        rows mustBe Nil
      }

      "generate TrusteeRow from a list of Trustees in user answers" in {
        val rows = new AddATrusteeViewHelper(userAnswersWithTrustees).rows
        rows mustBe List(
          TrusteeRow("First 0 Last 0", `type` = TrusteeOrIndividual.Individual, "#", "#"),
          TrusteeRow("First 1 Last 1", `type` = TrusteeOrIndividual.Business, "#", "#")
        )
      }

    }

  }

}
