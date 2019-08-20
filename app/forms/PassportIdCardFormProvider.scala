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
import play.api.data.Form
import play.api.data.Forms._
import models.PassportIdCardDetails


class PassportIdCardFormProvider @Inject() extends Mappings {


  def apply(): Form[PassportIdCardDetails] = Form(
    mapping(
      "countryOfIssue" -> text("site.passportOrIdCard.error.countryOfIssue.required")
        .verifying(
          firstError(
            maxLength(100, "site.passportOrIdCard.cardNumber.error.countryOfIssue.length"),
            isNotEmpty("countryOfIssue", "site.passportOrIdCard.cardNumber.error.countryOfIssue.required")
          )
        ),
      "number" -> text("site.passportOrIdCard.error.cardNumber.required")
        .verifying(
          firstError(
            maxLength(30, "site.passportOrIdCard.error.cardNumber.length"),
            regexp(Validation.passportOrIdCardNumberRegEx, " = ID card number can only include letters and numbers"),
            isNotEmpty("cardNumber", "site.passportOrIdCard.error.cardNumber.required")
          )
        ),
      "expiryDate" -> localDate(
        invalidKey = "site.passportOrIdCard.error.invalid",
        allRequiredKey = "site.passportOrIdCard.error.required.al",
        twoRequiredKey = "site.passportOrIdCard.error.required.two",
        requiredKey = "site.passportOrIdCard.error.required"
      )
    )(PassportIdCardDetails.apply)(PassportIdCardDetails.unapply)
  )
}