/*
 * Copyright 2026 HM Revenue & Customs
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

package views.abTestingUseOnly

import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.abTestingUseOnly.TestSignOutView

class TestSignOutViewSpec extends ViewBehaviours {

  private val messageKeyPrefix = "abTestingUseOnly.signOutPage"
  private val guidanceURL      = "https://www.gov.uk/guidance/manage-your-trusts-registration-service"

  "TestSignOutView" must {

    val view = viewFor[TestSignOutView](None)

    def applyView(): HtmlFormat.Appendable = view.apply()(fakeRequest, messages)

    behave like normalPage(applyView(), None, messageKeyPrefix)

    behave like pageWithLink(applyView(), "manage-trust-guidance", guidanceURL)
    behave like pageWithoutLanguageToggleLink(applyView())

    behave like pageWithText(applyView(), "You have signed out", "- heading")
    behave like pageWithText(applyView(), "You have signed out of the trust registration.", "- p1")
    behave like pageWithText(applyView(), "Get the URN for the trust", "- heading2")
    behave like pageWithText(
      applyView(),
      "You need to sign in to manage the trust using the Government Gateway user " +
        "ID and password that you set up to register this trust.",
      "- p2"
    )
    behave like pageWithText(applyView(), "Sign in to manage a trust", "- link text")
  }

}
