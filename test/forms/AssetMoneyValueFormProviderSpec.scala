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

import forms.behaviours.{IntFieldBehaviours, StringFieldBehaviours}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import play.api.data.FormError
import wolfendale.scalacheck.regexp.RegexpGen

import scala.collection.mutable

class AssetMoneyValueFormProviderSpec extends StringFieldBehaviours with IntFieldBehaviours {

  val requiredKey = "assetMoneyValue.error.required"
  val lengthKey = "assetMoneyValue.error.length"
  val invalidFormatKey = "assetMoneyValue.error.invalidFormat"
  val wholeNumberKey = "assetMoneyValue.error.wholeNumber"
  val zeroNumberkey = "assetMoneyValue.error.zero"
  val maxLength = 12
  val minValue = 1

  val maxLengthRegex = "^[0-9]{13}$"

  val form = new AssetMoneyValueFormProvider()()

  ".value" must {

    val fieldName = "value"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      RegexpGen.from(Validation.onlyNumbersRegex)
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
