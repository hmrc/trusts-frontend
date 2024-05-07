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

package views.register.confirmation.existingTrust

import play.api.Application
import utils.AccessibilityHelper.formatReferenceNumber
import views.behaviours.ViewBehaviours
import views.html.register.confirmation.existingTrust.IndividualView

class IndividualViewSpec extends ViewBehaviours {

  private val application: Application = applicationBuilder().build()

  private val view: IndividualView = application.injector.instanceOf[IndividualView]

  private val prefix: String = "confirmation.existing.individual"

  private val fakeRef: String = "XC TRN 00 00 00 49 11"
  private val formattedFakeRef: String = formatReferenceNumber(fakeRef)
  private val fakeName: String = "name"

  "Existing Trust Individual View" must {

    val applyView = view.apply(fakeDraftId, fakeRef, fakeName)(fakeRequest, messages)

    behave like confirmationPage(
      applyView,
      prefix,
      formattedFakeRef,
      fakeName,
      None,
      "subheading1", "p1",
      "subheading2", "p2", "p3",
      "subheading3", "p4", "p5", "p6", "p6.a",
      "warning"
    )
  }
}
