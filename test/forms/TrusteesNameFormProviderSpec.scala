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
import play.api.data.FormError
import wolfendale.scalacheck.regexp.RegexpGen

class TrusteesNameFormProviderSpec extends StringFieldBehaviours {

  val form = new TrusteesNameFormProvider()()
  val invalidKey = "trusteesName.error.invalidCharacters"

  val maxLength = 35
  val minLength = 1


  ".firstName" must {

    val fieldName = "firstName"
    val requiredKey = "trusteesName.error.firstnamerequired"
    val lengthKey = "trusteesName.error.lengthfirstname"
    val regex = "^[A-Za-z0-9 ,.()/&'-]*$"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      RegexpGen.from(regex)
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
    val lengthKey = "trusteesName.error.lengthmiddlename"
    val maxLength = 35
    val regex = "^[A-Za-z0-9 ,.()/&'-]*$"


    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = maxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(maxLength))
    )

    behave like optionalField(
      form,
      fieldName,
      validDataGenerator =  RegexpGen.from(regex))
  }


  ".lastName" must {

    val fieldName = "lastName"
    val requiredKey = "trusteesName.error.lastnamerequired"
    val lengthKey = "trusteesName.error.lengthlastname"
    val regex = "^[A-Za-z0-9 ,.()/&'-]*$"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      RegexpGen.from(regex)
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
