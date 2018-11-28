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

package forms

import forms.behaviours.StringFieldBehaviours
import play.api.data.FormError
import utils.TrustsValidator.{alphaNumericWithSpecialsRegex}

class InternationalAddressFormProviderSpec extends StringFieldBehaviours {

  val form = new InternationalAddressFormProvider()()

  ".line1" must {

    val fieldName = "line1"
    val requiredKey = s"internationalAddress.error.$fieldName.required"
    val lengthKey = s"internationalAddress.error.$fieldName.length"
    val invalidKey = s"internationalAddress.error.$fieldName.invalidCharacters"
    val maxLength = 35

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

    behave like fieldWithRegexpWithGenerator(
      form,
      fieldName,
      regexp = alphaNumericWithSpecialsRegex,
      generator = stringsWithMaxLength(maxLength),
      error = FormError(fieldName, invalidKey, Seq(alphaNumericWithSpecialsRegex))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }

  ".line2" must {

    val fieldName = "line2"
    val requiredKey = s"internationalAddress.error.$fieldName.required"
    val lengthKey = s"internationalAddress.error.$fieldName.length"
    val invalidKey = s"internationalAddress.error.$fieldName.invalidCharacters"
    val maxLength = 35

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

    behave like fieldWithRegexpWithGenerator(
      form,
      fieldName,
      regexp = alphaNumericWithSpecialsRegex,
      generator = stringsWithMaxLength(maxLength),
      error = FormError(fieldName, invalidKey, Seq(alphaNumericWithSpecialsRegex))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }


  ".line3" must {

    val fieldName = "line3"
    val lengthKey = s"internationalAddress.error.$fieldName.length"
    val invalidKey = s"internationalAddress.error.$fieldName.invalidCharacters"
    val maxLength = 35

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

    behave like fieldWithRegexpWithGenerator(
      form,
      fieldName,
      regexp = alphaNumericWithSpecialsRegex,
      generator = stringsWithMaxLength(maxLength),
      error = FormError(fieldName, invalidKey, Seq(alphaNumericWithSpecialsRegex))
    )

    behave like optionalField(
      form,
      fieldName)
  }


  ".country" must {

    val fieldName = "country"
    val requiredKey = s"internationalAddress.error.$fieldName.required"
    val lengthKey = s"internationalAddress.error.$fieldName.length"
    val invalidKey = s"internationalAddress.error.$fieldName.invalidCharacters"
    val maxLength = 35

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

    behave like fieldWithRegexpWithGenerator(
      form,
      fieldName,
      regexp = alphaNumericWithSpecialsRegex,
      generator = stringsWithMaxLength(maxLength),
      error = FormError(fieldName, invalidKey, Seq(alphaNumericWithSpecialsRegex))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }


}
