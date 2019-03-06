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
import models.TrusteesUkAddress

class TrusteesUkAddressFormProvider @Inject() extends Mappings {

  def apply(): Form[TrusteesUkAddress] = Form(
    mapping(
      "line1" ->
        text("trusteesUkAddress.error.line1.required")
          .verifying(
            firstError(
              isNotEmpty("line1", "trusteesUkAddress.error.line1.required"),
              maxLength(35, "trusteesUkAddress.error.line1.length"),
              regexp(Validation.addressLineRegex, "trusteesUkAddress.error.line1.invalidCharacters")
            )),
      "line2" ->
        optional(Forms.text
          .verifying(
            firstError(
              maxLength(35, "trusteesUkAddress.error.line2.length"),
              regexp(Validation.addressLineRegex, "trusteesUkAddress.error.line2.invalidCharacters")
            ))),
      "line3" ->
        optional(Forms.text
          .verifying(
            firstError(
              maxLength(35, "trusteesUkAddress.error.line3.length"),
              regexp(Validation.addressLineRegex, "trusteesUkAddress.error.line3.invalidCharacters")
            ))),
      "townOrCity" ->
        text("trusteesUkAddress.error.townOrCity.required")
          .verifying(
            firstError(
              isNotEmpty("townOrCity", "trusteesUkAddress.error.townOrCity.required"),
              maxLength(35, "trusteesUkAddress.error.townOrCity.length"),
              regexp(Validation.addressLineRegex, "trusteesUkAddress.error.townOrCity.invalidCharacters")
            )),
      "postcode" ->
        postcode("trusteesUkAddress.error.postcode.required")
          .verifying(
            firstError(
              isNotEmpty("postcode", "trusteesUkAddress.error.postcode.required"),
              regexp(Validation.postcodeRegex, "trusteesUkAddress.error.postcode.invalidCharacters")
            ))
    )(TrusteesUkAddress.apply)(TrusteesUkAddress.unapply)
   )
 }
