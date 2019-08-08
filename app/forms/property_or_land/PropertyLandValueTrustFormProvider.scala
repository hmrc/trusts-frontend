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

package forms.property_or_land

import forms.Validation
import forms.mappings.Mappings
import javax.inject.Inject
import models.PropertyLandValueTrust
import play.api.data.Form
import play.api.data.Forms._

class PropertyLandValueTrustFormProvider @Inject() extends Mappings {

  def apply(): Form[PropertyLandValueTrust] = Form(
    mapping(
      "value" -> currency("propertyLandValueTrust.error.field1.required")
        .verifying(
          firstError(
            maxLength(12, "propertyLandValueTrust.error.field1.length"),
            isNotEmpty("value", "propertyLandValueTrust.error.field1.required"),
            regexp(Validation.decimalCheck, "propertyLandValueTrust.error.field1.whole"),
            regexp(Validation.numericRegex, "propertyLandValueTrust.error.field1.invalid"),
            minimumValue("1", "propertyLandValueTrust.error.field1.zero")
          )
        ))(PropertyLandValueTrust.apply)(PropertyLandValueTrust.unapply)
  )
}