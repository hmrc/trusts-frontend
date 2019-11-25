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

package forms.playback

import forms.Validation
import forms.mappings.Mappings
import javax.inject.Inject
import models.core.pages.FullName
import models.playback.pages.Declaration
import play.api.data.Form
import play.api.data.Forms.{mapping, optional}

class DeclarationFormProvider @Inject() extends Mappings {

  def apply(): Form[Declaration] =
  Form(
    mapping(
      "" -> fullName,
      "email" -> optional(text().verifying(
        firstError(
          maxLength(35, s"declaration.changes.noChanges.error.email.length"),
          regexp(Validation.emailRegex, s"declaration.changes.noChanges.error.email.invalid"))
      ))
    )(Declaration.apply)(Declaration.unapply)
  )

  val fullName = mapping(

    "firstName" -> text("declaration.changes.noChanges.error.firstName.required")
      .verifying(
        firstError(
          maxLength(35, s"declaration.changes.noChanges.error.firstName.length"),
          isNotEmpty("firstName", s"declaration.changes.noChanges.error.firstName.required"),
          regexp(Validation.nameRegex, s"declaration.changes.noChanges.error.firstName.invalid")
        )),
    "middleName" -> optional(text()
      .verifying(
        firstError(
          maxLength(35, s"declaration.changes.noChanges.error.middleName.length"),
          regexp(Validation.nameRegex, s"declaration.changes.noChanges.error.middleName.invalid"))
      )),
    "lastName" -> text("declaration.changes.noChanges.error.lastName.required")
      .verifying(
        firstError(
          maxLength(35, s"declaration.changes.noChanges.error.lastName.length"),
          isNotEmpty("lastName", s"declaration.changes.noChanges.error.lastName.required"),
          regexp(Validation.nameRegex, s"declaration.changes.noChanges.error.lastName.invalid")
        ))
  )(FullName.apply)(FullName.unapply)

}
