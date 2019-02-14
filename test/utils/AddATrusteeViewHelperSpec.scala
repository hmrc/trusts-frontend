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
import models.{FullName, IndividualOrBusiness, UserAnswers}
import pages.{IndividualOrBusinessPage, TrusteesNamePage}
import viewmodels.TrusteeRow

class AddATrusteeViewHelperSpec extends SpecBase {

  val userAnswersWithTrusteesComplete = UserAnswers(userAnswersId)
    .set(TrusteesNamePage(0), FullName("First 0", None, "Last 0")).success.value
    .set(IndividualOrBusinessPage(0), IndividualOrBusiness.Individual).success.value
    .set(TrusteesNamePage(1), FullName("First 1", None, "Last 1")).success.value
    .set(IndividualOrBusinessPage(1), IndividualOrBusiness.Business).success.value

  val userAnswersWithTrusteesInProgress = UserAnswers(userAnswersId)
    .set(TrusteesNamePage(0), FullName("First 0", Some("Middle"), "Last 0")).success.value
    .set(TrusteesNamePage(1), FullName("First 1", Some("Middle"), "Last 1")).success.value

  val userAnswersWithCompleteAndInProgres = UserAnswers(userAnswersId)
    .set(TrusteesNamePage(0), FullName("First 0", Some("Middle"), "Last 0")).success.value
    .set(TrusteesNamePage(1), FullName("First 1", Some("Middle"), "Last 1")).success.value
    .set(IndividualOrBusinessPage(1), IndividualOrBusiness.Individual).success.value

  val userAnswersWithNoTrustees = UserAnswers(userAnswersId)

  "AddATrusteeViewHelper" when {

    ".row" must {

      "generate Nil for no user answers" in {
        val rows = new AddATrusteeViewHelper(userAnswersWithNoTrustees).rows
        rows.inProgress mustBe Nil
        rows.complete mustBe Nil
      }

      "generate rows from user answers for trustees in progress" in {
        val rows = new AddATrusteeViewHelper(userAnswersWithTrusteesInProgress).rows
        rows.inProgress mustBe List(
          TrusteeRow("First 0 Last 0", typeLabel = "Trustee", "#", "#"),
          TrusteeRow("First 1 Last 1", typeLabel = "Trustee", "#", "#")
        )
        rows.complete mustBe Nil
      }

      "generate rows from user answers for complete trustees" in {
        val rows = new AddATrusteeViewHelper(userAnswersWithTrusteesComplete).rows
        rows.complete mustBe List(
          TrusteeRow("First 0 Last 0", typeLabel = "Trustee Individual", "#", "#"),
          TrusteeRow("First 1 Last 1", typeLabel = "Trustee Business", "#", "#")
        )
        rows.inProgress mustBe Nil
      }

      "generate rows from user answers for complete and in progress trustees" in {
        val rows = new AddATrusteeViewHelper(userAnswersWithCompleteAndInProgres).rows
        rows.complete mustBe List(
          TrusteeRow("First 1 Last 1", typeLabel = "Trustee Individual", "#", "#")
        )
        rows.inProgress mustBe List(
          TrusteeRow("First 0 Last 0", typeLabel = "Trustee", "#", "#")
        )
      }

    }
  }

}
