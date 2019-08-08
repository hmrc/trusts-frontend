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

package forms.property_or_land

import forms.Validation
import forms.behaviours.{IntFieldBehaviours, StringFieldBehaviours}
import org.scalacheck.Gen
import play.api.data.FormError
import wolfendale.scalacheck.regexp.RegexpGen

class PropertyLandValueTrustFormProviderSpec extends StringFieldBehaviours with IntFieldBehaviours {

  val form = new PropertyLandValueTrustFormProvider()()

  ".value" must {

    val fieldName = "value"
    val requiredKey = "propertyLandValueTrust.error.field1.required"
    val zeroNumberkey = "propertyLandValueTrust.error.field1.zero"
    val invalidOnlyNumbersKey = "propertyLandValueTrust.error.field1.invalid"
    val invalidWholeNumberKey = "propertyLandValueTrust.error.field1.whole"
    val lengthKey = "propertyLandValueTrust.error.field1.length"
    val maxLength = 12


    behave like nonDecimalField(
      form,
      fieldName,
      wholeNumberError = FormError(fieldName, invalidWholeNumberKey, Seq(Validation.decimalCheck))
    )

    behave like fieldWithRegexpWithGenerator(
      form,
      fieldName,
      Validation.onlyNumbersRegex,
      generator = stringsWithMaxLength(maxLength),
      error = FormError(fieldName, invalidOnlyNumbersKey, Seq(Validation.onlyNumbersRegex))
    )

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      RegexpGen.from(Validation.onlyNumbersRegex)
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = maxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(maxLength))
    )

    behave like intFieldWithMinimumWithGenerator(
      form,
      fieldName,
      1,
      Gen.const(0),
      FormError(fieldName, zeroNumberkey, Array("1"))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }

}
