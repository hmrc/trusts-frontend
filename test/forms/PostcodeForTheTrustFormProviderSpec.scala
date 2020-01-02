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

import forms.behaviours.{OptionalFieldBehaviours, StringFieldBehaviours}
import org.scalacheck.Arbitrary.arbitrary
import play.api.data.FormError
import wolfendale.scalacheck.regexp.RegexpGen

class PostcodeForTheTrustFormProviderSpec extends StringFieldBehaviours with OptionalFieldBehaviours {

  val invalidKey : String = "error.postcodeInvalid"

  val form = new PostcodeForTheTrustFormProvider()()

  val postcodeRegexWithSpaceConstrained = """^[A-Z]{1,2}[0-9][0-9A-Z] [0-9][A-Z]{2}$"""

  ".value" must {

    val fieldName = "value"

    behave like fieldWithRegexpWithGenerator(
      form,
      fieldName,
      regexp = postcodeRegexWithSpaceConstrained,
      generator = arbitrary[String],
      error = FormError(fieldName, invalidKey)
    )

    behave like optionalField(
      form,
      fieldName,
      RegexpGen.from(postcodeRegexWithSpaceConstrained)
    )

  }
}
