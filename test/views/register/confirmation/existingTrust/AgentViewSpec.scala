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

package views.register.confirmation.existingTrust

import play.api.Application
import utils.AccessibilityHelper.formatReferenceNumber
import views.behaviours.ViewBehaviours
import views.html.register.confirmation.existingTrust.AgentView

class AgentViewSpec extends ViewBehaviours {

  private val application: Application = applicationBuilder().build()

  private val view: AgentView = application.injector.instanceOf[AgentView]

  private val prefix: String = "confirmation.existing.agent"

  private val fakeRef: String = "XC TRN 00 00 00 49 11"
  private val formattedFakeRef: String = formatReferenceNumber(fakeRef)
  private val fakeName: String = "name"

  "Existing Trust Agent View" must {

    val applyView = view.apply(fakeDraftId, fakeRef, fakeName)(fakeRequest, messages)

    behave like confirmationPage(
      applyView,
      prefix,
      formattedFakeRef,
      fakeName,
      "subheading1",
      "subheading2", "p2", "p3",
      "bullet1", "bullet1.p1", "bullet1.p2", "bullet1.p2.a", "bullet1.p3", "bullet1.p4",
      "bullet2", "bullet2.p1", "bullet2.p2", "bullet2.p3", "bullet2.p3.a", "bullet2.p4", "bullet2.p4.a"
    )
  }
}
