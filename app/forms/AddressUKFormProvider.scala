/*
 * Copyright 2018 HM Revenue & Customs
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
import play.api.data.Form
import play.api.data.Forms._
import models.AddressUK

class AddressUKFormProvider @Inject() extends Mappings {

   def apply(): Form[AddressUK] = Form(
     mapping(
      "line1" -> text("addressUK.error.line1.required")
        .verifying(maxLength(56, "addressUK.error.line1.length")),
      "line2" -> text("addressUK.error.line2.required")
        .verifying(maxLength(56, "addressUK.error.line2.length")),
        "town" -> text("addressUK.error.town.required")
          .verifying(maxLength(56, "addressUK.error.town.length")),
        "county" -> text("addressUK.error.county.required")
          .verifying(maxLength(56, "addressUK.error.county.length")),
        "postcode" -> text("addressUK.error.postcode.required")
          .verifying(maxLength(56, "addressUK.error.postcode.length"))
    )(AddressUK.apply)(AddressUK.unapply)
   )
 }
