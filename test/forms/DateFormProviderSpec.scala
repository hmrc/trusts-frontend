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

import java.time.{LocalDate, ZoneOffset}

import forms.behaviours.{DateBehaviours, StringFieldBehaviours}
import play.api.data.{Form, FormError}

class DateFormProviderSpec extends DateBehaviours {


  "trustSettledDate page .value" should {

    val messageKeyPrefix = "trustSettledDate"
    val form :Form[LocalDate] = new DateFormProvider().apply(messageKeyPrefix)

    val validData = datesBetween(
      min = LocalDate.of(1900, 1, 1),
      max = LocalDate.now(ZoneOffset.UTC)
    )

    behave like dateField(form, "value", validData)

    behave like mandatoryDateField(form, "value", s"$messageKeyPrefix.error.required.all")

    behave like dateFieldWithMax(form, "value",
      max = LocalDate.now,
      FormError("value", s"$messageKeyPrefix.error.future", List("day", "month", "year"))
    )

    behave like dateFieldWithMin(form, "value",
      min = LocalDate.of(1900, 1, 1),
      FormError("value", s"$messageKeyPrefix.error.past", List("day", "month", "year"))
    )
  }

}
