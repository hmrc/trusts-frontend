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

package forms

import forms.behaviours.StringFieldBehaviours
import play.api.data.FormError

class TrustPreviouslyResidentFormProviderSpec extends StringFieldBehaviours {

  val requiredKey = "trustPreviouslyResident.error.required"
  val lengthKey = "trustPreviouslyResident.error.length"
  val maxLength = 100
  val regexp = "^[A-Za-z ,.()'-]*$"
  val invalidKey = "trustPreviouslyResident.error.invalidCharacters"

  val form = new TrustPreviouslyResidentFormProvider()()

  ".value" must {

    val fieldName = "value"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(maxLength)
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = maxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(maxLength))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )

    behave like fieldWithRegexpWithGenerator(
      form,
      fieldName,
      regexp = regexp,
      generator = stringsWithMaxLength(maxLength),
      error = FormError(fieldName, invalidKey, Seq(regexp))
    )

    behave like nonEmptyField(
      form, fieldName, FormError(fieldName, requiredKey, Seq(fieldName))
    )

  }
}
