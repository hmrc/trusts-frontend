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

package views.abTestingUseOnly.register.confirmation.nonTaxable

import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.abTestingUseOnly.register.confirmation.nonTaxable.TestIndividualView

class TestIndividualViewSpec extends ViewBehaviours {

  private val refNumber = "X1234567890YZ"
  private val messageKeyPrefix = "abTestingUseOnly.confirmationPage"
  private val printOrSaveLink = s"/trusts-registration/$fakeDraftId/your-draft-registration"

  "TestIndividualView" must {

    val view = viewFor[TestIndividualView](Some(emptyUserAnswers))

    def applyView(): HtmlFormat.Appendable = view.apply(fakeDraftId, refNumber)(fakeRequest, messages)

    behave like normalPage(applyView(), None, messageKeyPrefix)

    behave like pageWithLink(applyView(), "print-or-save", printOrSaveLink)
    behave like pageWithoutLanguageToggleLink(applyView())

    behave like pageWithText(applyView(), "Registration submitted", "- heading")
    behave like pageWithText(applyView(), "Print or save a copy of your answers", "- link text")
    behave like pageWithText(applyView(), "What you must do next", "- heading 2")
    behave like pageWithText(applyView(), "You need to get the Unique Reference Number (URN) for the trust.", "- p1")
    behave like pageWithText(applyView(), "The URN is how HMRC will identify this trust. You can give the URN to people who " +
      "are authorised to be involved with managing the trust such as agents, financial advisors and other trustees.", "- p2")
    behave like pageWithText(applyView(), "How to get the URN for the trust", "- heading 3")
    behave like pageWithText(applyView(), "You will need to sign out of this service and sign in to manage the trust using the " +
      "same Government Gateway user ID and password that you set up to register this trust.", "- p3")
    behave like pageWithText(applyView(), "If you need to contact HMRC", "- heading 4")
    behave like pageWithText(applyView(), "The reference number for this registration is:", "- p4")
    behave like pageWithText(applyView(), refNumber, "ref number")
    behave like pageWithText(applyView(), "Keep a note of this reference number in case you need to contact HMRC. It is not the " +
      "URN for the trust.", "- p5")
    behave like pageWithText(applyView(), "Sign out", "- button text")

    behave like pageWithASubmitButton(applyView())
  }
}
