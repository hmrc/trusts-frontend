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
import play.api.data.{Form, Forms}
import play.api.data.Forms._
import models.InternationalAddress

class InternationalAddressFormProvider @Inject() extends Mappings {

  def apply(): Form[InternationalAddress] = Form(
    mapping(
      "line1" ->
        text("internationalAddress.error.line1.required")
          .verifying(
            firstError(
              isNotEmpty("line1", "internationalAddress.error.line1.required"),
              maxLength(35, "internationalAddress.error.line1.length"),
              regexp(Validation.addressLineRegex, "internationalAddress.error.line1.invalidCharacters")
            )),
      "line2" ->
        text("internationalAddress.error.line2.required")
          .verifying(
            firstError(
              isNotEmpty("line2", "internationalAddress.error.line2.required"),
              maxLength(35, "internationalAddress.error.line2.length"),
              regexp(Validation.addressLineRegex, "internationalAddress.error.line2.invalidCharacters")
            )),
      "line3" ->
        optional(Forms.text
          .verifying(
            firstError(
              maxLength(35, "internationalAddress.error.line3.length"),
              regexp(Validation.addressLineRegex, "internationalAddress.error.line3.invalidCharacters")
            ))),
      "line4" ->
        optional(Forms.text
          .verifying(
            firstError(
              maxLength(35, "internationalAddress.error.line4.length"),
              regexp(Validation.addressLineRegex, "internationalAddress.error.line4.invalidCharacters")
            ))),
      "country" ->
        text("internationalAddress.error.country.required")
          .verifying(
            firstError(
              isNotEmpty("country", "internationalAddress.error.country.required")
            ))
    )(InternationalAddress.apply)(InternationalAddress.unapply)
  )
}