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

package views

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import models.UserAnswers
import pages.{RegistrationSubmissionDatePage, RegistrationTRNPage}
import views.behaviours.ViewBehaviours
import views.html.ConfirmationAnswerPageView

class ConfirmationAnswerPageViewSpec extends ViewBehaviours {
  val index = 0

  "ConfirmationAnswerPage view" must {

    val userAnswers =
      UserAnswers(userAnswersId)
        .set(RegistrationTRNPage, "XNTRN000000001").success.value
        .set(RegistrationSubmissionDatePage, LocalDateTime.now).success.value

    val dateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy")
    val trnDateTime = LocalDateTime.now.format(dateFormatter)

    val view = viewFor[ConfirmationAnswerPageView](Some(userAnswers))

    val applyView = view.apply(Nil, "XNTRN000000001", trnDateTime)(fakeRequest, messages)

    behave like normalPage(applyView, "confirmationAnswerPage")


    "assert content" in {
      val doc = asDocument(applyView)
      assertContainsText(doc, messages("confirmationAnswerPage.paragraph1", "XNTRN000000001"))
      assertContainsText(doc, messages("confirmationAnswerPage.paragraph2", trnDateTime))
    }

  }
}
