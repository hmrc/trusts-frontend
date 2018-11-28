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


import java.time.LocalDate

import forms.mappings.Mappings
import javax.inject.Inject

import play.api.data.Form

class TrustSettledDateFormProvider @Inject() extends Mappings {


  def apply(messagePrefix: String): Form[LocalDate] =
    Form(
      "value" -> localDate(
        invalidKey     = s"$messagePrefix.error.invalid",
        allRequiredKey = s"$messagePrefix.error.required.all",
        twoRequiredKey = s"$messagePrefix.error.required.two",
        requiredKey    = s"$messagePrefix.error.required"
      ).verifying(firstError(
        maxDate(LocalDate.now, s"$messagePrefix.error.future", "day", "month", "year"),
        minDate(LocalDate.of(1900,1,1), s"$messagePrefix.error.past", "day", "month", "year")
      )
      )
    )
}
