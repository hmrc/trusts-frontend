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
import javax.inject.Inject
import forms.mappings.Mappings
import play.api.data.{Form, FormError}
import play.api.data.Forms._
import org.joda.time.LocalDate

class TrustSettledDateFormProvider @Inject() extends Mappings {

  def requiredKey(messagePreFix: String) = s"$messagePreFix.error.required"
  def invalidKey(messagePreFix: String) = s"$messagePreFix.error.invalid"

  def apply(messagePrefix: String): Form[LocalDate] = Form(
    single(
      "date" -> localDateMapping(
        "day" -> int(requiredKey(messagePrefix), invalidKey(messagePrefix)),
        "month" -> int(requiredKey(messagePrefix), invalidKey(messagePrefix)),
        "year" -> int(requiredKey(messagePrefix), invalidKey(messagePrefix))
      )
        .verifying(before(LocalDate.parse("1900-01-01"), s"$messagePrefix.error.past"))
        .replaceError(FormError("", "error.invalidDate"), FormError("", invalidKey(messagePrefix)))
        .replaceError(FormError("day", requiredKey(messagePrefix)), FormError("", requiredKey(messagePrefix)))
        .replaceError(FormError("month", requiredKey(messagePrefix)), FormError("", requiredKey(messagePrefix)))
        .replaceError(FormError("year", requiredKey(messagePrefix)), FormError("", requiredKey(messagePrefix)))
        .replaceError(FormError("day", invalidKey(messagePrefix)), FormError("", invalidKey(messagePrefix)))
        .replaceError(FormError("month", invalidKey(messagePrefix)), FormError("", invalidKey(messagePrefix)))
        .replaceError(FormError("year", invalidKey(messagePrefix)), FormError("", invalidKey(messagePrefix)))
    )
  )
}
