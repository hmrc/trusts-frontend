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

import controllers.routes
import forms.TrustHaveAUTRFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import viewmodels.Link
import views.behaviours.YesNoViewBehaviours
import views.html.TrustHaveAUTRView

class TrustHaveAUTRViewSpec extends YesNoViewBehaviours {

  val messageKeyPrefix = "trustHaveAUTR"

  val form = new TrustHaveAUTRFormProvider()()

  "TrustHaveAUTR view" must {

    val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

    val view = application.injector.instanceOf[TrustHaveAUTRView]

    val url = "https://www.gov.uk/find-lost-utr-number"
    val link = Link(messages("trustHaveAUTR.link"), url)

    def applyView(form: Form[_]): HtmlFormat.Appendable = view.apply(form, NormalMode, Some(link))(fakeRequest, messages)

    behave like normalPage(applyView(form), messageKeyPrefix)

    behave like pageWithBackLink(applyView(form))

    behave like pageWithLink(applyView(form), "link", url)

    behave like yesNoPage(form, applyView, messageKeyPrefix, routes.TrustHaveAUTRController.onSubmit(NormalMode).url, Some(messageKeyPrefix))

    behave like pageWithASubmitButton(applyView(form))
  }
}
