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
import controllers.routes.TrustAddressUKController
import forms.AddressUKFormProvider
import models.{AddressUK, Mode, NormalMode}
import views.behaviours.QuestionViewBehaviours
import views.html.addressUK

class TrustAddressUKViewSpec extends QuestionViewBehaviours[AddressUK] {

  val messageKeyPrefix = "addressUK"
  def actionRoute(mode: Mode) = TrustAddressUKController.onSubmit(mode)

  override val form = new AddressUKFormProvider()()

  def createView = () => addressUK(frontendAppConfig, form, NormalMode, actionRoute(NormalMode), messageKeyPrefix)(fakeRequest, messages)

  def createViewUsingForm = (form: Form[_]) => addressUK(frontendAppConfig, form, NormalMode, actionRoute(NormalMode), messageKeyPrefix)(fakeRequest, messages)


  "TrustAddressUK view" must {

    behave like normalPage(createView, messageKeyPrefix)

    behave like pageWithBackLink(createView)

    behave like pageWithTextFields(
      createViewUsingForm,
      messageKeyPrefix,
      actionRoute(NormalMode).url,
      "line1", "line2", "line3", "town", "postcode"
    )
  }
}
