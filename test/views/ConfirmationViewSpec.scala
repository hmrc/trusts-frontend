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

import models.FullName
import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.ConfirmationView
import utils.AccessibilityHelper._

class ConfirmationViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "confirmationPage"
  val refNumber = "XC TRN 00 00 00 49 11"
  val accessibleRefNumber = formatTRN(refNumber)
  val postHMRC = "https://www.gov.uk/government/organisations/hm-revenue-customs/contact/trusts"

  val name = "John Smith"

  private def newTrust(view : HtmlFormat.Appendable) : Unit = {

    "assert content" in  {
      val doc = asDocument(view)

      assertContainsText(doc, "Registration received")
      assertContainsText(doc, "Your reference is:")
      assertRenderedById(doc, "trusts-registration-number")

      assertRenderedById(doc, "print-and-save")

      assertContainsText(doc, s"We will post $name a Unique Taxpayer Reference (UTR). If they are based in the UK, this can take 15 working days. For international trustees, this can take up to 21 working days.")

      assertContainsText(doc, "Make a note of your reference number in case you need to contact HMRC. If you do not get your UTR within 15 working days,")

      assertContainsText(doc, "You cannot make online changes to the trust.")

      assertContainsText(doc, "You must keep a record of any other changes until you can change them using the online service.")
    }

  }

  private def existingTrust(view: HtmlFormat.Appendable) : Unit = {

    "assert content" in {
      val doc = asDocument(view)

      assertContainsText(doc, "Registration received")
      assertContainsText(doc, "Your reference is:")
      assertRenderedById(doc, "trusts-registration-number")

      assertRenderedById(doc, "print-and-save")

      assertContainsText(doc, s"You can continue to use your Unique Taxpayer Reference as usual. If there is a problem with your registration, we will contact $name.")

      assertContainsText(doc, "Make a note of your reference number in case you need to contact HMRC.")

      assertContainsText(doc, "You cannot make online changes to the trust.")

      assertContainsText(doc, "You must keep a record of any other changes until you can change them using the online service.")
    }

  }

  private def confirmationForAgent(view: HtmlFormat.Appendable) : Unit = {

    "display return to agent overview link" in {

      val doc = asDocument(view)
      val agentOverviewLink = doc.getElementById("agent-overview")
      assertAttributeValueForElement(agentOverviewLink, "href", "#")
      assertContainsTextForId(doc, "agent-overview", "return to register and maintain a trust for a client.")
    }
  }

  "Confirmation view for a new trust" must {
    val view = viewFor[ConfirmationView](Some(emptyUserAnswers))

    val applyView = view.apply(
      draftId = fakeDraftId,
      isExistingTrust = false,
      isAgent = false,
      refNumber = refNumber,
      postHMRC = postHMRC,
      agentOverviewUrl = "#",
      leadTrusteeName= FullName("John", None, "Smith"))(fakeRequest, messages)

    behave like confirmationPage(applyView, messageKeyPrefix, refNumber, accessibleRefNumber)

    behave like newTrust(applyView)
  }

  "Confirmation view for an existing trust" must {
    val view = viewFor[ConfirmationView](Some(emptyUserAnswers))

    val applyView = view.apply(
      draftId = fakeDraftId,
      isExistingTrust = true,
      isAgent = false,
      refNumber = refNumber,
      postHMRC = postHMRC,
      agentOverviewUrl = "#",
      leadTrusteeName= FullName("John", None, "Smith"))(fakeRequest, messages)

    behave like confirmationPage(applyView, messageKeyPrefix, refNumber, accessibleRefNumber)

    behave like existingTrust(applyView)
  }

  "Confirmation view for an agent" must {
    val view = viewForAgent[ConfirmationView](Some(emptyUserAnswers))

    val applyView = view.apply(
      isExistingTrust = true,
      isAgent = true,
      draftId = fakeDraftId,
      refNumber = refNumber,
      postHMRC = postHMRC,
      agentOverviewUrl = "#",
      leadTrusteeName= FullName("John", None, "Smith"))(fakeRequest, messages)

    behave like confirmationPage(applyView, messageKeyPrefix, refNumber, accessibleRefNumber)

    behave like existingTrust(applyView)

    behave like confirmationForAgent(applyView)
  }
}
