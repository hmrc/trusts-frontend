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

package views.register.confirmation.newTrust

import play.api.Application
import utils.AccessibilityHelper.formatReferenceNumber
import views.behaviours.ViewBehaviours
import views.html.register.confirmation.newTrust.nonTaxable.AgentView

class AgentViewSpec extends ViewBehaviours {

  private val application: Application = applicationBuilder().build()

  private val view: AgentView = application.injector.instanceOf[AgentView]

  private val prefix: String = "confirmation.nonTaxable.agent"

  private val fakeRef: String = "XC TRN 00 00 00 49 11"
  private val formattedFakeRef: String = formatReferenceNumber(fakeRef)
  private val fakeName: String = "name"

  "non taxable New Trust Agent View" must {

    val applyView = view.apply(fakeDraftId, fakeRef, fakeName)(fakeRequest, messages)

    behave like confirmationPage(
      applyView,
      prefix,
      formattedFakeRef,
      fakeName,
      None,
      "subheading1", "p1", "p2", "p3", "link1",
      "subheading2", "p4", "p5", "b1", "b2", "p6", "p7",
      "subheading3", "p8", "p9", "p10", "link2", "p11",
      "subheading4", "p12", "p13",
      "subheading5", "p14", "p15", "link3", "p16", "p17"
    )
  }
}
