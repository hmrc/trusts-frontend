/*
 * Copyright 2021 HM Revenue & Customs
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

package forms

import forms.behaviours.StringFieldBehaviours
import play.api.data.FormError

class AccessCodeFormProviderSpec extends StringFieldBehaviours {

  val requiredKey = "nonTaxableTrustRegistrationAccessCode.error.required"
  val invalidKey = "nonTaxableTrustRegistrationAccessCode.error.invalid"
  val regexp: String = Validation.accessCodeRegex

  val form = new AccessCodeFormProvider()()

  "AccessCodeFormProvider" must {

    val fieldName = "value"

    behave like mandatoryField(
      form = form,
      fieldName = fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )

    behave like fieldWithRegexpWithGenerator(
      form = form,
      fieldName = fieldName,
      regexp = regexp,
      generator = nonEmptyString,
      error = FormError(fieldName, invalidKey, Seq(regexp))
    )

    behave like nonEmptyField(
      form = form,
      fieldName = fieldName,
      requiredError = FormError(fieldName, requiredKey, Seq(fieldName))
    )
  }
}
