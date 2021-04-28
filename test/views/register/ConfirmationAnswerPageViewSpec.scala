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

package views.register

import uk.gov.hmrc.http.HeaderCarrier
import utils.AccessibilityHelper._
import utils.print.register.PrintUserAnswersHelper
import views.behaviours.ViewBehaviours
import views.html.register.ConfirmationAnswerPageView

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class ConfirmationAnswerPageViewSpec extends ViewBehaviours {

  private val prefix = "confirmationAnswerPage"
  private val formattedSubmissionTime: String = "22 April 2021"
  private val trn = "XNTRN000000001"

  "ConfirmationAnswerPageView" when {

    "taxable" must {

      implicit val hc: HeaderCarrier = HeaderCarrier()

      val app = applicationBuilder().build()

      val helper = app.injector.instanceOf[PrintUserAnswersHelper]

      val viewFuture = helper.summary(fakeDraftId).map { sections =>
        val view = viewFor[ConfirmationAnswerPageView](Some(emptyUserAnswers))
        view.apply(sections, formatReferenceNumber(trn), formattedSubmissionTime, isTaxable = true)(fakeRequest, messages)
      }

      val view = Await.result(viewFuture, Duration.Inf)

      val doc = asDocument(view)

      behave like normalPage(view, None, prefix)

      behave like pageWithWarning(view)

      behave like pageWithReturnToTopLink(view)

      "assert header content" in {
        assertContainsText(doc, messages(s"$prefix.paragraph1", formatReferenceNumber(trn)))
        assertContainsText(doc, messages(s"$prefix.paragraph2", formattedSubmissionTime))
      }
    }

    "non-taxable" must {

      implicit val hc: HeaderCarrier = HeaderCarrier()

      val app = applicationBuilder().build()

      val helper = app.injector.instanceOf[PrintUserAnswersHelper]

      val viewFuture = helper.summary(fakeDraftId).map { sections =>
        val view = viewFor[ConfirmationAnswerPageView](Some(emptyUserAnswers))
        view.apply(sections, formatReferenceNumber(trn), formattedSubmissionTime, isTaxable = false)(fakeRequest, messages)
      }

      val view = Await.result(viewFuture, Duration.Inf)

      val doc = asDocument(view)

      behave like normalPage(view, None, prefix)

      behave like pageWithReturnToTopLink(view)

      "assert header content" in {
        assertContainsText(doc, messages(s"$prefix.paragraph1", formatReferenceNumber(trn)))
        assertContainsText(doc, messages(s"$prefix.paragraph2", formattedSubmissionTime))
      }
    }
  }
}
