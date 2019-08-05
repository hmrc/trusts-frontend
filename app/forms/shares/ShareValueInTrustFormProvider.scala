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

package forms.shares

import forms.Validation
import forms.mappings.Mappings
import javax.inject.Inject
import play.api.data.Form

class ShareValueInTrustFormProvider @Inject() extends Mappings {

  def apply(): Form[String] =
    Form(
      "value" -> currency("shareValueInTrust.error.required")
        .verifying(
          firstError(
            maxLength(12, "shareValueInTrust.error.length"),
            isNotEmpty("value", "shareValueInTrust.error.required"),
            regexp(Validation.onlyNumbersRegex, "shareValueInTrust.error.invalid"),
            minimumValue("1", "shareValueInTrust.error.zero")
          )))
}
