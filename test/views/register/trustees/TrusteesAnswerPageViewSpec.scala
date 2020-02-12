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

package views.register.trustees

import pages.register.trustees.IsThisLeadTrusteePage
import views.behaviours.ViewBehaviours
import views.html.register.trustees.{TrusteeIndividualOrBusinessView, TrusteesAnswerPageView}

class TrusteesAnswerPageViewSpec extends ViewBehaviours {

  val index = 0

  "TrusteesAnswerPage as a lead trustee view" must {

    val titlePrefix = "leadTrusteesAnswerPage"

    val userAnswers = emptyUserAnswers
      .set(IsThisLeadTrusteePage(index), true).success.value

    val view = viewFor[TrusteesAnswerPageView](Some(userAnswers))

    val applyView = view.apply(index, fakeDraftId, Nil, titlePrefix)(fakeRequest, messages)

    behave like normalPage(applyView, titlePrefix)

    behave like pageWithBackLink(applyView)
  }

  "TrusteesAnswerPage as a trustee view" must {

    val titlePrefix = "trusteesAnswerPage"

    val userAnswers = emptyUserAnswers
      .set(IsThisLeadTrusteePage(index), false).success.value

    val view = viewFor[TrusteesAnswerPageView](Some(userAnswers))

    val applyView = view.apply(index, fakeDraftId, Nil, titlePrefix)(fakeRequest, messages)

    behave like normalPage(applyView, titlePrefix)

    behave like pageWithBackLink(applyView)
  }
}
