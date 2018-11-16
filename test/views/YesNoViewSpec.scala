/*
 * Copyright 2018 HM Revenue & Customs
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

import play.api.data.Form
import controllers.routes.TrustAddressUKYesNoController
import forms.YesNoFormProvider
import views.behaviours.YesNoViewBehaviours
import models.{Mode, NormalMode}
import views.html.yesNo

class YesNoViewSpec extends YesNoViewBehaviours {

  val messageKeyPrefix = "trustAddressUKYesNo"

  val form = new YesNoFormProvider()(messageKeyPrefix)
  def actionRoute(mode: Mode) = TrustAddressUKYesNoController.onSubmit(mode)

  def createView = () => yesNo(frontendAppConfig, form, NormalMode, actionRoute(NormalMode), messageKeyPrefix)(fakeRequest, messages)

  def createViewUsingForm = (form: Form[_]) => yesNo(frontendAppConfig, form, NormalMode, actionRoute(NormalMode), messageKeyPrefix)(fakeRequest, messages)

  "YesNo view" must {

    behave like normalPage(createView, messageKeyPrefix)

    behave like pageWithBackLink(createView)

    behave like yesNoPage(createViewUsingForm, messageKeyPrefix, actionRoute(NormalMode).url)
  }
}
