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
import play.api.data.Forms.{mapping, optional}

class AgentDeclarationFormProvider @Inject() extends Mappings {

  def apply(): Form[FullName] =
  Form(
    mapping(

      "firstName" -> text("agentDeclaration.error.firstName.required")
        .verifying(
          firstError(
            maxLength(35, s"agentDeclaration.error.firstName.length"),
            isNotEmpty("firstName", s"agentDeclaration.error.firstName.required"),
            regexp(Validation.nameRegex, s"agentDeclaration.error.firstName.invalid")
          )),
      "middleName" -> optional(text()
        .verifying(
          firstError(
            maxLength(35, s"agentDeclaration.error.middleName.length"),
            regexp(Validation.nameRegex, s"agentDeclaration.error.middleName.invalid"))
        )),
      "lastName" -> text("agentDeclaration.error.lastName.required")
        .verifying(
          firstError(
            maxLength(35, s"agentDeclaration.error.lastName.length"),
            isNotEmpty("lastName", s"agentDeclaration.error.lastName.required"),
            regexp(Validation.nameRegex, s"agentDeclaration.error.lastName.invalid")
          ))
    )(FullName.apply)(FullName.unapply)
  )
}
