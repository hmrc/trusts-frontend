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

package forms

import forms.behaviours.StringFieldBehaviours
import org.scalacheck.Arbitrary.arbitrary
import play.api.data.FormError
import wolfendale.scalacheck.regexp.RegexpGen

class TelephoneNumberFormProviderSpec extends StringFieldBehaviours {

  val requiredKey = "telephoneNumber.error.required"
  val lengthKey = "telephoneNumber.error.length"
  val invalidCharKey = "telephoneNumber.error.invalid.characters"
  val maxLength = 100
  val messageKeyPrefix = "telephoneNumber"

  val form = new TelephoneNumberFormProvider()(messageKeyPrefix)

  ".value" must {

    val fieldName = "value"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      RegexpGen.from(Validation.telephoneRegex)
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )

    behave like nonEmptyField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey, Seq(fieldName))
    )

    behave like fieldWithRegexpWithGenerator(
      form,
      fieldName,
      regexp = Validation.telephoneRegex,
      generator = arbitrary[String],
      error = FormError(fieldName, invalidCharKey, Seq(Validation.telephoneRegex))
    )
  }
}
