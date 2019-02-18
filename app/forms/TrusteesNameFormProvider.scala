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

import javax.inject.Inject
import forms.mappings.Mappings
import models.FullName
import play.api.data.Form
import play.api.data.Forms._


class TrusteesNameFormProvider @Inject() extends Mappings {

  def apply(): Form[FullName] = Form(
    mapping(
      "firstName" -> text("trusteesName.error.firstnamerequired")
        .verifying(
          firstError(
            maxLength(35, "trusteesName.error.lengthfirstname"),
            isNotEmpty("firstName", "trusteesName.error.firstnamerequired"),
            regexp(Validation.nameRegex, "trusteesName.error.invalidFirstNameCharacters")
          )
        ),
      "middleName" -> optional(text("fullName.error.middleName.required")
        .verifying(
          firstError(
            maxLength(35, "trusteesName.error.lengthmiddlename"),
            regexp(Validation.nameRegex, "trusteesName.error.invalidMiddleNameCharacters"))
        )
      ),
      "lastName" -> text("trusteesName.error.lastnamerequired")
        .verifying(
          firstError(
            maxLength(35, "trusteesName.error.lengthlastname"),
            isNotEmpty("lastName", "trusteesName.error.lastnamerequired"),
            regexp(Validation.nameRegex, "trusteesName.error.invalidLastNameCharacters")
          )
        )
    )(FullName.apply)(FullName.unapply)
  )
}

