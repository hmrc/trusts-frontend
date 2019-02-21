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
import views.html.TrusteesAnswerPageView

class TrusteesAnswerPageViewSpec extends ViewBehaviours {

  val index = 0

  "TrusteesAnswerPage view" must {

    val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

    val view = application.injector.instanceOf[TrusteesAnswerPageView]

    val applyView = view.apply(index, Nil)(fakeRequest, messages)

    behave like normalPage(applyView, "trusteesAnswerPage")

    behave like pageWithBackLink(applyView)
  }
}
