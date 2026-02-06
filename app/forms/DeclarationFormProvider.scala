/*
 * Copyright 2026 HM Revenue & Customs
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

import forms.helpers.WhitespaceHelper._
import forms.mappings.Mappings

import javax.inject.Inject
import models.core.pages.{Declaration, FullName}
import play.api.data.Form
import play.api.data.Forms.{mapping, optional}

class DeclarationFormProvider @Inject() extends Mappings {

  def apply(): Form[Declaration] =
    Form(
      mapping(
        ""      -> fullName,
        "email" -> optional(
          text()
            .transform(trimWhitespace, identity[String])
            .verifying(
              firstError(regexp(Validation.emailRegex, "declaration.error.email.invalid"))
            )
        ).transform(emptyToNone, identity[Option[String]])
      )(Declaration.apply)(Declaration.unapply)
    )

  val fullName = mapping(
    "firstName"  -> text("declaration.error.firstName.required")
      .verifying(
        firstError(
          maxLength(35, "declaration.error.firstName.length"),
          isNotEmpty("firstName", "declaration.error.firstName.required"),
          regexp(Validation.nameRegex, "declaration.error.firstName.invalid")
        )
      ),
    "middleName" -> optional(
      text()
        .transform(trimWhitespace, identity[String])
        .verifying(
          firstError(
            maxLength(35, "declaration.error.middleName.length"),
            regexp(Validation.nameRegex, "declaration.error.middleName.invalid")
          )
        )
    ).transform(emptyToNone, identity[Option[String]]),
    "lastName"   -> text("declaration.error.lastName.required")
      .verifying(
        firstError(
          maxLength(35, "declaration.error.lastName.length"),
          isNotEmpty("lastName", "declaration.error.lastName.required"),
          regexp(Validation.nameRegex, "declaration.error.lastName.invalid")
        )
      )
  )(FullName.apply)(FullName.unapply)

}
