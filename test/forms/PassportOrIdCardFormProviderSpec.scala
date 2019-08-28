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

import java.time.{LocalDate, ZoneOffset}

import forms.behaviours.{DateBehaviours, PassportOrIDCardBehaviours, StringFieldBehaviours}
import models.PassportOrIdCardDetails
import play.api.data.FormError
import wolfendale.scalacheck.regexp.RegexpGen

class PassportOrIdCardFormProviderSpec extends
  StringFieldBehaviours with PassportOrIDCardBehaviours with DateBehaviours {

  val form = new PassportOrIdCardFormProvider()()

  ".country" must {

    val fieldName = "country"
    val requiredKey = "settlorIndividualPassport.country.error.required"
    val lengthKey = "settlorIndividualPassport.country.error.length"
    val maxLength = 100

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

    behave like nonEmptyField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey, Seq(fieldName))
    )
  }

  ".number" must {

    val fieldName = "number"
    val requiredKey = "settlorIndividualPassport.number.error.required"
    val invalidLengthKey = "settlorIndividualPassport.number.error.length"
    val invalidKey = "settlorIndividualPassport.number.error.invalid"
    val maxLength = 30

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = maxLength,
      lengthError = FormError(fieldName, invalidLengthKey, Seq(maxLength))
    )

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      RegexpGen.from(Validation.passportOrIdCardNumberRegEx)
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )

    behave like nonEmptyField(
      form,
      fieldName,
      requiredError = FormError(fieldName, invalidKey, Seq(Validation.passportOrIdCardNumberRegEx))
    )

    behave like cardNumberField(
      form,
      fieldName,
      invalidError = FormError(fieldName, invalidKey, Seq(Validation.passportOrIdCardNumberRegEx))
    )
  }

  ".expiryDate" must {

    val key = "expiryDate"

    val min = LocalDate.of(1500, 1, 1)
    val max = LocalDate.now(ZoneOffset.UTC)

    val requiredBindings = Map("country" -> "country", "number" -> "1234567")


    val validData = datesBetween(
        min = min,
        max = max
      )

      behave like dateFieldForPassportOrIdForm(form, key, validData, requiredBindings)

      behave like mandatoryDateField(form, key, "settlorIndividualPassport.expiryDate.error.required.all")

      behave like dateFieldWithMax(form, key,
        max = max,
        FormError(key, s"settlorIndividualPassport.expiryDate.error.future", List("day", "month", "year"))
      )

      behave like dateFieldWithMin(form, key,
        min = min,
        FormError(key, s"settlorIndividualPassport.expiryDate.error.past", List("day", "month", "year"))
      )
  }
}