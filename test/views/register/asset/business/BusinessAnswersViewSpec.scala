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

package views.register.asset.business

import pages.register.asset.business.BusinessNamePage
import views.behaviours.ViewBehaviours
import views.html.register.asset.buisness.BusinessAnswersView

class BusinessAnswersViewSpec extends ViewBehaviours {

  val index = 0

  "AssetAnswerPage" must {

    val titlePrefix = "assetAnswer"

    val userAnswers = emptyUserAnswers
      .set(BusinessNamePage(index), "Test").success.value

    val view = viewFor[BusinessAnswersView](Some(userAnswers))

    val applyView = view.apply(index, fakeDraftId, Nil)(fakeRequest, messages)

    behave like normalPage(applyView, titlePrefix)

    behave like pageWithBackLink(applyView)
  }
}
