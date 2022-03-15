/*
 * Copyright 2022 HM Revenue & Customs
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

package views.register.suitability

import views.behaviours.ViewBehaviours
import views.html.register.suitability.BeforeYouContinueExistingTaxableView

class BeforeYouContinueExistingTaxableViewSpec extends ViewBehaviours {

  "BeforeYouContinueExistingTaxable View" must {

    val application = applicationBuilder().build()

    val view = application.injector.instanceOf[BeforeYouContinueExistingTaxableView]

    val applyView = view.apply()(fakeRequest, messages)

    behave like normalPage(
      applyView,
      None,
      "suitability.beforeYouContinue.existing.taxable",
      "p1", "p2", "h2", "p3"
    )

    behave like pageWithASubmitButton(applyView)
  }
}
