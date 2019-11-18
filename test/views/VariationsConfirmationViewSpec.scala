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
import utils.AccessibilityHelper._
import views.behaviours.ViewBehaviours
import views.html.ConfirmationView

class VariationsConfirmationViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "variationsConfirmationPage"
  val fakeTvn = "XC TVN 000 000 4912"
  val accessibleRefNumber = formatReferenceNumber(fakeTvn)

  val name = "John Smith"

  private def variationsTrust(view: HtmlFormat.Appendable) : Unit = {

    "assert content" in {
      val doc = asDocument(view)

      assertContainsText(doc, s"Declaration received, your reference is: $fakeTvn")
      assertContainsText(doc, "Print or save a declared copy of the trust’s registration (opens in a new window or tab)")

      assertContainsText(doc, "What happens next")

      assertContainsText(doc, "Keep a note of your reference in case you need to contact HMRC. If there is a problem with the declaration, we will contact the lead trustee.")

      assertContainsText(doc, "If any of the settlor, trustee or beneficiary details change (before you make your next declaration) you will need to update them using the online service.")

      assertContainsText(doc, "Declaring the trust is up to date")

      assertContainsText(doc, "You need to declare every year the details we have are up to date. This needs to be done through the Trust Registration Service and Self Assessment online or the Trust and Estate Tax Return form (SA900).")
    }

  }

}
