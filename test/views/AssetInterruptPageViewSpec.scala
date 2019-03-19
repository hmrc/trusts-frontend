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

import views.behaviours.ViewBehaviours
import views.html.AssetInterruptPageView

class AssetInterruptPageViewSpec extends ViewBehaviours {

  "AssetInterruptPage view" must {

    val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

    val view = application.injector.instanceOf[AssetInterruptPageView]

    val applyView = view.apply()(fakeRequest, messages)

    application.stop()

    behave like normalPage(applyView, "assetInterruptPage", "title",
      "subheading1",
      "paragraph1",
      "subheading2",
      "paragraph2",
      "bullet1",
      "bullet2",
      "bullet3",
      "subheading3",
      "paragraph3",
      "bullet4",
      "bullet5",
      "bullet6",
      "paragraph4",
      "subheading4",
      "paragraph5",
      "bullet7",
      "bullet8",
      "bullet9",
      "paragraph6",
      "continue")


    behave like pageWithBackLink(applyView)

  }
}
