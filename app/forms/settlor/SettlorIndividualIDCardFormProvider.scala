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

package forms.settlor

import forms.mappings.Mappings
import javax.inject.Inject
import models.SettlorIndividualIDCard
import play.api.data.Form
import play.api.data.Forms._

class SettlorIndividualIDCardFormProvider @Inject() extends Mappings {

  def apply(): Form[SettlorIndividualIDCard] = Form(
    mapping(
      "field1" -> text("settlorIndividualIDCard.error.field1.required")
  .verifying(
    firstError(
      maxLength(100, "settlorIndividualIDCard.error.field1.length"),
  isNotEmpty("field1","settlorIndividualIDCard.error.field1.required")
    )
  ),
  "field2" -> text("settlorIndividualIDCard.error.field2.required")
  .verifying(
    firstError(
      maxLength(100, "settlorIndividualIDCard.error.field2.length"),
  isNotEmpty("field2","settlorIndividualIDCard.error.field2.required")
    )
  )
  )(SettlorIndividualIDCard.apply)(SettlorIndividualIDCard.unapply)
  )
}