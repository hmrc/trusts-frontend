/*
 * Copyright 2024 HM Revenue & Customs
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

import forms.Validation.emailRegex
import forms.behaviours.StringFieldBehaviours
import play.api.data.FormError
import wolfendale.scalacheck.regexp.RegexpGen

class DeclarationFormProviderSpec extends StringFieldBehaviours {

  val form = new DeclarationFormProvider()()

  ".firstName" must {

    val fieldName   = "firstName"
    val requiredKey = "declaration.error.firstName.required"
    val lengthKey   = "declaration.error.firstName.length"
    val maxLength   = 35

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      RegexpGen.from(Validation.nameRegex)
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

    behave like nonEmptyField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey, Seq(fieldName))
    )

    "replace smart apostrophes" in {
      val result = form.bind(Map("firstName" -> "‘apos’trophes‘", "middleName" -> "middle", "lastName" -> "lastName"))
      result.value.value.name.firstName shouldBe "'apos'trophes'"
    }
  }

  ".middleName" must {

    val fieldName = "middleName"
    val lengthKey = "declaration.error.middleName.length"
    val maxLength = 35

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = maxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(maxLength))
    )

    behave like optionalField(
      form,
      fieldName,
      validDataGenerator = RegexpGen.from(Validation.nameRegex)
    )

    "bind whitespace trim values" in {
      val result = form.bind(Map("firstName" -> "firstName", "middleName" -> "  middle  ", "lastName" -> "lastName"))
      result.value.value.name.middleName shouldBe Some("middle")
    }

    "bind whitespace blank values" in {
      val result = form.bind(Map("firstName" -> "firstName", "middleName" -> "  ", "lastName" -> "lastName"))
      result.value.value.name.middleName shouldBe None
    }

    "bind whitespace no values" in {
      val result = form.bind(Map("firstName" -> "firstName", "middleName" -> "", "lastName" -> "lastName"))
      result.value.value.name.middleName shouldBe None
    }

    "replace smart apostrophes" in {
      val result =
        form.bind(Map("firstName" -> "firstName", "middleName" -> "‘apos’trophes‘", "lastName" -> "lastName"))
      result.value.value.name.middleName shouldBe Some("'apos'trophes'")
    }
  }

  ".lastName" must {

    val fieldName   = "lastName"
    val requiredKey = "declaration.error.lastName.required"
    val lengthKey   = "declaration.error.lastName.length"
    val maxLength   = 35

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      RegexpGen.from(Validation.nameRegex)
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

    behave like nonEmptyField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey, Seq(fieldName))
    )

    "replace smart apostrophes" in {
      val result =
        form.bind(Map("firstName" -> "firstName", "middleName" -> "middleName", "lastName" -> "‘apos’trophes‘"))
      result.value.value.name.lastName shouldBe "'apos'trophes'"
    }
  }

  ".email" must {

    val fieldName   = "email"
    val requiredKey = "declaration.error.email.invalid"

    behave like optionalField(
      form,
      fieldName,
      validDataGenerator = RegexpGen.from(Validation.emailRegex)
    )

    "bind whitespace trim values" in {
      val result = form.bind(
        Map(
          "firstName"  -> "firstName",
          "middleName" -> "middle",
          "lastName"   -> "lastName",
          "email"      -> "  test@test.com  "
        )
      )
      result.value.value.email shouldBe Some("test@test.com")
    }

    "bind whitespace no values" in {
      val result =
        form.bind(Map("firstName" -> "firstName", "middleName" -> "middle", "lastName" -> "lastName", "email" -> ""))
      result.value.value.email shouldBe None
    }

    "replace smart apostrophes before the @ sign in an email" in {
      val result = form.bind(
        Map(
          "firstName"  -> "firstName",
          "middleName" -> "middleName",
          "lastName"   -> "lastName",
          "email"      -> "‘a‘pos‘@trophes.com"
        )
      )
      result.value.value.email shouldBe Some("'a'pos'@trophes.com")
    }

    "throw an error if an email has a smart apostrophe after the @" in {
      val result = form.bind(
        Map(
          "firstName"  -> "firstName",
          "middleName" -> "middleName",
          "lastName"   -> "lastName",
          "email"      -> "apos@t‘rophes.com"
        )
      )
      result.errors should contain(FormError(fieldName, requiredKey, Seq(emailRegex)))
    }

    "throw an error if an email ends with a smart apostrophe after the @" in {
      val result = form.bind(
        Map(
          "firstName"  -> "firstName",
          "middleName" -> "middleName",
          "lastName"   -> "lastName",
          "email"      -> "apos@trophes.com‘"
        )
      )
      result.errors should contain(FormError(fieldName, requiredKey, Seq(emailRegex)))
    }
  }

}
