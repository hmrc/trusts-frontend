/*
 * Copyright 2020 HM Revenue & Customs
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

package utils

import base.RegistrationSpecBase
import controllers.register.routes._
import models.NormalMode
import models.core.UserAnswers
import models.registration.Matched.Success
import pages.register.{ExistingTrustMatched, PostcodeForTheTrustPage, TrustRegisteredWithUkAddressYesNoPage, WhatIsTheUTRPage}
import play.twirl.api.HtmlFormat
import utils.countryOptions.CountryOptions
import viewmodels.{AnswerRow, AnswerSection}

class CheckYourAnswersHelperSpec extends RegistrationSpecBase {

  private val countryOptions: CountryOptions = injector.instanceOf[CountryOptions]
  private val dateFormatterImpl: DateFormatterImpl = injector.instanceOf[DateFormatterImpl]

  "CheckYourAnswers Helper" must {

    "render trust details" when {

      val utr: String = "1234567890"
      val postcode: String = "NE1 1NE"

      val sectionKey = Some("Trust details")

      "registering an existing trust" when {

        val baseAnswers: UserAnswers = emptyUserAnswers
          .set(ExistingTrustMatched, Success).success.value
          .set(WhatIsTheUTRPage, utr).success.value

        "registered with UK address" in {

          val userAnswers: UserAnswers = baseAnswers
            .set(TrustRegisteredWithUkAddressYesNoPage, true).success.value
            .set(PostcodeForTheTrustPage, postcode).success.value

          val helper = new CheckYourAnswersHelper(countryOptions, dateFormatterImpl)(userAnswers, fakeDraftId, false)

          helper.trustDetails.get mustBe Seq(
            AnswerSection(
              headingKey = None,
              rows = Seq(
                AnswerRow(
                  label = "trustRegisteredWithUkAddress.checkYourAnswersLabel",
                  answer = HtmlFormat.escape("Yes"),
                  canEdit = false
                ),
                AnswerRow(
                  label = "postcodeForTheTrust.checkYourAnswersLabel",
                  answer = HtmlFormat.escape(postcode),
                  changeUrl = Some(PostcodeForTheTrustController.onPageLoad(NormalMode, fakeDraftId).url),
                  canEdit = false
                ),
                AnswerRow(
                  label = "whatIsTheUTR.checkYourAnswersLabel",
                  answer = HtmlFormat.escape(utr),
                  changeUrl = Some(WhatIsTheUTRController.onPageLoad(NormalMode, fakeDraftId).url),
                  canEdit = false
                )
              ),
              sectionKey = sectionKey
            )
          )
        }

        "registered with non-UK address" in {

          val userAnswers: UserAnswers = baseAnswers
            .set(TrustRegisteredWithUkAddressYesNoPage, false).success.value

          val helper = new CheckYourAnswersHelper(countryOptions, dateFormatterImpl)(userAnswers, fakeDraftId, false)

          helper.trustDetails.get mustBe Seq(
            AnswerSection(
              headingKey = None,
              rows = Seq(
                AnswerRow(
                  label = "trustRegisteredWithUkAddress.checkYourAnswersLabel",
                  answer = HtmlFormat.escape("No"),
                  canEdit = false
                ),
                AnswerRow(
                  label = "whatIsTheUTR.checkYourAnswersLabel",
                  answer = HtmlFormat.escape(utr),
                  changeUrl = Some(WhatIsTheUTRController.onPageLoad(NormalMode, fakeDraftId).url),
                  canEdit = false
                )
              ),
              sectionKey = sectionKey
            )
          )
        }
      }
    }
  }
}
