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

package forms.property_or_land

import forms.behaviours.LongFieldBehaviours
import play.api.data.FormError

class PropertyLandValueTrustFormProviderSpec extends LongFieldBehaviours {

  private val fieldName = "value"
  private val requiredKey = "propertyLandValueTrust.error.required"
  private val zeroNumberkey = "propertyLandValueTrust.error.zero"
  private val invalidOnlyNumbersKey = "propertyLandValueTrust.error.invalid"
  private val invalidWholeNumberKey = "propertyLandValueTrust.error.whole"
  private val maxValueKey = "propertyLandValueTrust.error.moreThanMax"

  ".value" must {

    val maxValue: Long = 100L

    val form = new PropertyLandValueTrustFormProvider().withMaxValue(maxValue)

    behave like longField(
      form,
      fieldName,
      nonNumericError = FormError(fieldName, invalidOnlyNumbersKey),
      wholeNumberError = FormError(fieldName, invalidWholeNumberKey),
      maxNumberError = FormError(fieldName, maxValueKey),
      zeroError = FormError(fieldName, zeroNumberkey),
      Some(maxValue)
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }
}
