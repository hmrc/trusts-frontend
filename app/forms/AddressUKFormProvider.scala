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
import utils.TrustsValidator._

class AddressUKFormProvider @Inject() extends Mappings {

   def apply(): Form[AddressUK] = Form(
     mapping(
      "line1" -> text("addressUK.error.line1.required")
        .verifying(firstError(
          maxLength(35, "addressUK.error.line1.length"),
          regexp(alphaNumericWithSpecialsRegex, "addressUK.error.line1.invalidCharacters"),
          isNotEmpty("line1", "addressUK.error.line1.required")
        )),
      "line2" -> optional(text("addressUK.error.line2.required")
        .verifying(firstError(
          maxLength(35, "addressUK.error.line2.length"),
          regexp(alphaNumericWithSpecialsRegex, "addressUK.error.line2.invalidCharacters"),
          isNotEmpty("line2", "addressUK.error.line2.invalidCharacters"))
        )),
        "line3" -> optional(text("addressUK.error.line3.required")
          .verifying(firstError(
            maxLength(35, "addressUK.error.line3.length"),
            regexp(alphaNumericWithSpecialsRegex, "addressUK.error.line3.invalidCharacters"),
            isNotEmpty("line3", "addressUK.error.line3.invalidCharacters"))
          )),
        "town" -> text("addressUK.error.town.required")
          .verifying(firstError(
            maxLength(35, "addressUK.error.town.length"),
            regexp(alphaNumericWithSpecialsRegex, "addressUK.error.town.invalidCharacters"),
            isNotEmpty("town", "addressUK.error.town.required")
          )),
        "postcode" -> text("addressUK.error.postcode.required")
          .verifying(firstError(
            regexp(postcodeRegex, "addressUK.error.postcode.invalid")
          ))
    )(AddressUK.apply)(AddressUK.unapply)
   )
 }
