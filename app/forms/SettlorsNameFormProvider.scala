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
        .verifying(maxLength(35, "settlorsName.error.firstName.length")),
       "middleName" -> optional(text()),
      "lastName" -> text("settlorsName.error.lastName.required")
        .verifying(maxLength(35, "settlorsName.error.lastName.length"))
    )(FullName.apply)(FullName.unapply)
   )
 }
