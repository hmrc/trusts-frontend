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

import models.core.pages.FullName
import play.twirl.api.HtmlFormat
import utils.AccessibilityHelper._
import views.behaviours.ViewBehaviours
import views.html.register.{ConfirmationAgentView, ConfirmationExistingView, ConfirmationIndividualView}

class ConfirmationViewSpec extends ViewBehaviours {

  val refNumber = "XC TRN 00 00 00 49 11"
  val accessibleRefNumber = formatReferenceNumber(refNumber)

  val name = "John Smith"

  private def newTrust(view : HtmlFormat.Appendable) : Unit = {

    "assert content" in  {
      val doc = asDocument(view)

      assertContainsText(doc, "Registration received")
      assertContainsText(doc, "Your reference is:")
      assertRenderedById(doc, "trusts-registration-number")

      assertRenderedById(doc, "print-and-save")

      assertContainsText(doc, s"We will post $name the trust’s Unique Taxpayer Reference (UTR). If they are based in the UK, this can take 15 working days. For international trustees, this can take up to 21 working days.")

      assertContainsText(doc, "Keep a note of your reference in case you need to contact HMRC.")
      assertContainsText(doc, "If any of the settlor, trustee or beneficiary details change (before you make your next declaration) you will need to update them using the online service once the trust’s UTR has been received.")

      assertContainsText(doc, "Where to declare the trust is up to date")

      assertContainsText(doc, "If the trust has a tax liability, you have a legal responsibility to declare every year that the details we hold are up to date.")
      assertContainsText(doc, "You need to make a declaration through the Trust Registration Service and Self Assessment online or the Trust and Estate Tax Return form (SA900).")

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

      assertContainsText(doc, "Keep a note of your reference in case you need to contact HMRC.")

    }

  }

  private def confirmationForAgent(view: HtmlFormat.Appendable) : Unit = {

    "assert content for agents" in {

      val doc = asDocument(view)
      val agentOverviewLink = doc.getElementById("agent-overview")

      assertAttributeValueForElement(agentOverviewLink, "href", controllers.register.agents.routes.AgentOverviewController.onPageLoad().url)

      assertContainsText(doc, "One of the trustees will need to complete the next 2 steps once the trust’s UTR has been received.")
      assertContainsText(doc, "You will then be able to maintain the trust online on their behalf.")

      assertContainsText(doc, s"We will post $name the trust’s Unique Taxpayer Reference (UTR). If they are based in the UK, this can take 15 working days. For international trustees, this can take up to 21 working days.")

      assertContainsText(doc, "Trustee needs to claim the trust")
      assertContainsText(doc, "A trustee needs to log onto the Trust Registration Service using their Government Gateway account.")
      assertContainsText(doc, "The account they use must be an organisation account, not an individual account.")
      assertContainsText(doc, "The trustee needs to correctly answer questions about the trust so HMRC can prove the connection between the trustee and the trust.")
      assertContainsText(doc, "The trustee must complete this step before you can request authorisation from them to maintain the trust.")

      assertContainsText(doc, "Trustee needs to authorise your online access to the trust")
      assertContainsText(doc, "Through Agent Services you can create an authorisation link to send to the trustee.")

    }
  }

  "Confirmation view for a new trust" must {

    val messageKeyPrefix = "confirmationIndividualPage"

    val view = viewFor[ConfirmationIndividualView](Some(emptyUserAnswers))

    val applyView = view.apply(
      draftId = fakeDraftId,
      refNumber = refNumber,
      FullName("John", None, "Smith").toString
    )(fakeRequest, messages)

    behave like confirmationPage(applyView, messageKeyPrefix, refNumber, accessibleRefNumber)

    behave like newTrust(applyView)
  }

  "Confirmation view for an existing trust" must {

    val messageKeyPrefix = "confirmationExistingPage"

    val view = viewFor[ConfirmationExistingView](Some(emptyUserAnswers))

    val applyView = view.apply(
      draftId = fakeDraftId,
      isAgent = false,
      refNumber = refNumber,
      leadTrusteeName = FullName("John", None, "Smith").toString
    )(fakeRequest, messages)

    behave like confirmationPage(applyView, messageKeyPrefix, refNumber, accessibleRefNumber)

    behave like existingTrust(applyView)
  }

  "Confirmation view for an agent" must {

    val messageKeyPrefix = "confirmationAgentPage"

    val view = viewForAgent[ConfirmationAgentView](Some(emptyUserAnswers))

    val applyView = view.apply(
      draftId = fakeDraftId,
      refNumber = refNumber,
      leadTrusteeName = FullName("John", None, "Smith").toString
    )(fakeRequest, messages)

    behave like confirmationPage(applyView, messageKeyPrefix, refNumber, accessibleRefNumber)

    behave like confirmationForAgent(applyView)
  }
}
