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

import forms.mappings.Mappings
import javax.inject.Inject
import models.FullName
import play.api.data.Form
import play.api.data.Forms._

class SettlorsNameFormProvider @Inject() extends Mappings {

  def apply(): Form[FullName] = Form(
    mapping(

      "firstName" -> text("settlorsName.error.firstName.required")
        .verifying(
          firstError(
            maxLength(35, s"settlorsName.error.firstName.length"),
            isNotEmpty("firstName", s"settlorsName.error.firstName.required"),
            regexp(Validation.nameRegex, s"settlorsName.error.firstName.invalid")
          )),
      "middleName" -> optional(text()
        .verifying(
          firstError(
            maxLength(35, s"settlorsName.error.middleName.length"),
            regexp(Validation.nameRegex, s"settlorsName.error.middleName.invalid"))
        )),
      "lastName" -> text("settlorsName.error.lastName.required")
        .verifying(
          firstError(
            maxLength(35, s"settlorsName.error.lastName.length"),
            isNotEmpty("lastName", s"settlorsName.error.lastName.required"),
            regexp(Validation.nameRegex, s"settlorsName.error.lastName.invalid")
          ))
    )(FullName.apply)(FullName.unapply)
  )
}
