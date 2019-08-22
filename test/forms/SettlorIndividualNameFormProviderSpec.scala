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
import forms.settlor.SettlorIndividualNameFormProvider
import play.api.data.FormError
import wolfendale.scalacheck.regexp.RegexpGen

class SettlorIndividualNameFormProviderSpec extends StringFieldBehaviours {

  val form = new SettlorIndividualNameFormProvider()()

    ".firstName" must {

      val fieldName = "firstName"
      val requiredKey = "settlorIndividualName.error.firstname.required"
      val lengthKey = "settlorIndividualName.error.firstname.length"
      val maxLength = 35

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
    }

    ".middleName" must {

      val fieldName = "middleName"
      val lengthKey = "settlorIndividualName.error.middlename.length"
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
    }

    ".lastName" must {

      val fieldName = "lastName"
      val requiredKey = "settlorIndividualName.error.lastname.required"
      val lengthKey = "settlorIndividualName.error.lastname.length"
      val maxLength = 35

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
    }

  }