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

import java.time.LocalDate

import javax.inject.Inject
import forms.mappings.Mappings
import play.api.data.Form
import play.api.data.Forms._
import models.PassportIdCardDetails


class PassportIdCardFormProvider @Inject() extends Mappings {


  def apply(): Form[PassportIdCardDetails] = Form(
    mapping(
      "countryOfIssue" -> text("settlorIndividualPassport.error.country.required")
        .verifying(
          firstError(
            isNotEmpty("countryOfIssue", "settlorIndividualPassport.error.country.required")
          )
        ),
      "number" -> text("settlorIndividualPassport.number.error.required")
        .verifying(
          firstError(
            maxLength(30, "settlorIndividualPassport.number.error.length"),
            regexp(Validation.passportOrIdCardNumberRegEx, " = ID card number can only include letters and numbers"),
            isNotEmpty("cardNumber", "settlorIndividualPassport.number.error.required")
          )
        ),
      "expiryDate" -> localDate(
        invalidKey     = "settlorIndividualPassport.error.invalid",
        allRequiredKey = "settlorIndividualPassport.error.required.all",
        twoRequiredKey = "settlorIndividualPassport.error.required.two",
        requiredKey    = "settlorIndividualPassport.error.required"
      ).verifying(firstError(
        maxDate(LocalDate.now, s"settlorIndividualPassport.error.future", "day", "month", "year"),
        minDate(LocalDate.of(1500,1,1), s"settlorIndividualPassport.error.past", "day", "month", "year")
      ))

    )(PassportIdCardDetails.apply)(PassportIdCardDetails.unapply)
  )
}
