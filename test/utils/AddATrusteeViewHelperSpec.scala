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
import models.core.pages.{FullName, IndividualOrBusiness}
import models.registration.pages.Status.Completed
import pages.entitystatus.TrusteeStatus
import pages.register.trustees.individual.TrusteesNamePage
import pages.register.trustees.organisation.TrusteeOrgNamePage
import pages.register.trustees.{IsThisLeadTrusteePage, TrusteeIndividualOrBusinessPage}
import viewmodels.AddRow

class AddATrusteeViewHelperSpec extends RegistrationSpecBase {

  val userAnswersWithTrusteesComplete = emptyUserAnswers
    .set(IsThisLeadTrusteePage(0), true).success.value
    .set(TrusteeIndividualOrBusinessPage(0), IndividualOrBusiness.Individual).success.value
    .set(TrusteesNamePage(0), FullName("First 0", None, "Last 0")).success.value
    .set(TrusteeStatus(0), Completed).success.value
    .set(IsThisLeadTrusteePage(1), false).success.value
    .set(TrusteeIndividualOrBusinessPage(1), IndividualOrBusiness.Individual).success.value
    .set(TrusteesNamePage(1), FullName("First 1", None, "Last 1")).success.value
    .set(TrusteeStatus(1), Completed).success.value
    .set(IsThisLeadTrusteePage(2), false).success.value
    .set(TrusteeOrgNamePage(2), "BusinessName").success.value
    .set(TrusteeIndividualOrBusinessPage(2), IndividualOrBusiness.Business).success.value
    .set(TrusteeStatus(2), Completed).success.value

  val userAnswersWithTrusteesAndLeadTrusteeBusinessComplete = emptyUserAnswers
    .set(IsThisLeadTrusteePage(0), true).success.value
    .set(TrusteeOrgNamePage(0), "BusinessName").success.value
    .set(TrusteeIndividualOrBusinessPage(0), IndividualOrBusiness.Business).success.value
    .set(TrusteeStatus(0), Completed).success.value


  val userAnswersWithTrusteesInProgress = emptyUserAnswers
    .set(IsThisLeadTrusteePage(0), false).success.value
    .set(TrusteesNamePage(0), FullName("First 0", Some("Middle"), "Last 0")).success.value
    .set(IsThisLeadTrusteePage(1), false).success.value
    .set(TrusteesNamePage(1), FullName("First 1", Some("Middle"), "Last 1")).success.value
    .set(IsThisLeadTrusteePage(2),false).success.value
    .set(IsThisLeadTrusteePage(3), false).success.value
    .set(TrusteeOrgNamePage(3), "BusinessName").success.value

  val userAnswersWithCompleteAndInProgress = emptyUserAnswers
    .set(IsThisLeadTrusteePage(0), false).success.value
    .set(TrusteesNamePage(0), FullName("First 0", Some("Middle"), "Last 0")).success.value
    .set(IsThisLeadTrusteePage(1), true).success.value
    .set(TrusteeIndividualOrBusinessPage(1), IndividualOrBusiness.Individual).success.value
    .set(TrusteesNamePage(1), FullName("First 1", Some("Middle"), "Last 1")).success.value
    .set(TrusteeStatus(1), Completed).success.value

  val userAnswersWithNoTrustees = emptyUserAnswers

  "AddATrusteeViewHelper" when {

    ".row" must {

      "generate Nil for no user answers" in {
        val rows = new AddATrusteeViewHelper(userAnswersWithNoTrustees, fakeDraftId).rows
        rows.inProgress mustBe Nil
        rows.complete mustBe Nil
      }

      "generate rows from user answers for trustees in progress" in {
        val rows = new AddATrusteeViewHelper(userAnswersWithTrusteesInProgress, fakeDraftId).rows
        rows.inProgress mustBe List(
          AddRow("First 0 Last 0", typeLabel = "Trustee", "#", "/trusts-registration/id/trustees/individual/0/remove"),
          AddRow("First 1 Last 1", typeLabel = "Trustee", "#", "/trusts-registration/id/trustees/individual/1/remove"),
          AddRow("No name added", typeLabel = "Trustee", "#", "/trusts-registration/id/trustees/individual/2/remove"),
          AddRow("BusinessName", typeLabel = "Trustee", "#", "/trusts-registration/id/trustees/individual/3/remove")
        )
        rows.complete mustBe Nil
      }

      "generate rows from user answers for complete trustees (Lead Trustee Individual)" in {
        val rows = new AddATrusteeViewHelper(userAnswersWithTrusteesComplete, fakeDraftId).rows
        rows.complete mustBe List(
          AddRow("First 0 Last 0", typeLabel = "Lead Trustee Individual", "#", "/trusts-registration/id/trustees/individual/0/remove"),
          AddRow("First 1 Last 1", typeLabel = "Trustee Individual", "#", "/trusts-registration/id/trustees/individual/1/remove"),
          AddRow("BusinessName", typeLabel = "Trustee Company", "#", "/trusts-registration/id/trustees/business/2/remove")
        )
        rows.inProgress mustBe Nil
      }

      "generate rows from user answers for complete trustees (Lead Trustee Business)" in {
        val rows = new AddATrusteeViewHelper(userAnswersWithTrusteesAndLeadTrusteeBusinessComplete, fakeDraftId).rows
        rows.complete mustBe List(
          AddRow("BusinessName", typeLabel = "Lead Trustee Company", "#", "/trusts-registration/id/trustees/business/0/remove")
        )
        rows.inProgress mustBe Nil
      }

      "generate rows from user answers for complete and in progress trustees" in {
        val rows = new AddATrusteeViewHelper(userAnswersWithCompleteAndInProgress, fakeDraftId).rows
        rows.complete mustBe List(
          AddRow("First 1 Last 1", typeLabel = "Lead Trustee Individual", "#", "/trusts-registration/id/trustees/individual/1/remove")
        )
        rows.inProgress mustBe List(
          AddRow("First 0 Last 0", typeLabel = "Trustee", "#", "/trusts-registration/id/trustees/individual/0/remove")
        )
      }

    }
  }
}
