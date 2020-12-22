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

package views.register

import java.time.LocalDateTime

import pages.register.{RegistrationSubmissionDatePage, RegistrationTRNPage}
import uk.gov.hmrc.http.HeaderCarrier
import utils.AccessibilityHelper._
import utils.print.register.PrintUserAnswersHelper
import utils.{DateFormatter, TestUserAnswers}
import views.behaviours.ViewBehaviours
import views.html.register.ConfirmationAnswerPageView

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class ConfirmationAnswerPageViewSpec extends ViewBehaviours {
  val index = 0

  "ConfirmationAnswerPage view" must {

    val userAnswers = TestUserAnswers.emptyUserAnswers
      .set(RegistrationTRNPage, "XNTRN000000001").success.value
      .set(RegistrationSubmissionDatePage, LocalDateTime.of(2010, 10, 10, 13, 10, 10)).success.value

    val formatter = injector.instanceOf[DateFormatter]

    val trnDateTime: String = formatter.formatDateTime(LocalDateTime.of(2010, 10, 10, 13, 10, 10))

    implicit val hc: HeaderCarrier = HeaderCarrier()

    val app = applicationBuilder().build()

    val helper = app.injector.instanceOf[PrintUserAnswersHelper]

    val viewFuture = helper.summary(fakeDraftId).map {
      sections =>
        val view = viewFor[ConfirmationAnswerPageView](Some(userAnswers))

        view.apply(sections, formatReferenceNumber("XNTRN000000001"), trnDateTime)(fakeRequest, messages)
    }

    val view = Await.result(viewFuture, Duration.Inf)

    val doc = asDocument(view)

    behave like normalPage(view, None, "confirmationAnswerPage")

    "assert header content" in {
      assertContainsText(doc, messages("confirmationAnswerPage.paragraph1", formatReferenceNumber("XNTRN000000001")))
      assertContainsText(doc, messages("confirmationAnswerPage.paragraph2", trnDateTime))
    }

  }
}
