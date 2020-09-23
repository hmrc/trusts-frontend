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

import java.time.LocalDate

import base.RegistrationSpecBase
import controllers.register.routes._
import controllers.register.trust_details.routes._
import models.NormalMode
import models.core.UserAnswers
import models.registration.Matched.Success
import pages.register.trust_details.{TrustNamePage, WhenTrustSetupPage}
import pages.register.{ExistingTrustMatched, PostcodeForTheTrustPage, TrustRegisteredWithUkAddressYesNoPage, WhatIsTheUTRPage}
import play.twirl.api.HtmlFormat
import utils.countryOptions.CountryOptions
import viewmodels.{AnswerRow, AnswerSection}

class CheckYourAnswersHelperSpec extends RegistrationSpecBase {

  private val countryOptions: CountryOptions = injector.instanceOf[CountryOptions]

  "CheckYourAnswers Helper" must {

    "render trust details" when {

      val utr: String = "1234567890"
      val trustName: String = "Trust Name"
      val postcode: String = "NE1 1NE"
      val setupDate: LocalDate = LocalDate.parse("1996-02-03")

      val sectionKey = Some("Trust details")

      "registering an existing trust" when {

        val baseAnswers: UserAnswers = emptyUserAnswers
          .set(ExistingTrustMatched, Success).success.value
          .set(WhatIsTheUTRPage, utr).success.value
          .set(TrustNamePage, trustName).success.value
          .set(WhenTrustSetupPage, setupDate).success.value

        "registered with UK address" in {

          val userAnswers: UserAnswers = baseAnswers
            .set(TrustRegisteredWithUkAddressYesNoPage, true).success.value
            .set(PostcodeForTheTrustPage, postcode).success.value

          val helper = new CheckYourAnswersHelper(countryOptions)(userAnswers, fakeDraftId, false)

          helper.trustDetails.get mustBe Seq(
            AnswerSection(
              headingKey = None,
              rows = Seq(
                AnswerRow(
                  label = "trustName.checkYourAnswersLabel",
                  answer = HtmlFormat.escape(trustName),
                  changeUrl = Some(TrustNameController.onPageLoad(NormalMode, fakeDraftId).url),
                  canEdit = false
                ),
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
                ),
                AnswerRow(
                  label = "whenTrustSetup.checkYourAnswersLabel",
                  answer = HtmlFormat.escape("3 February 1996"),
                  changeUrl = Some(WhenTrustSetupController.onPageLoad(NormalMode, fakeDraftId).url),
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

          val helper = new CheckYourAnswersHelper(countryOptions)(userAnswers, fakeDraftId, false)

          helper.trustDetails.get mustBe Seq(
            AnswerSection(
              headingKey = None,
              rows = Seq(
                AnswerRow(
                  label = "trustName.checkYourAnswersLabel",
                  answer = HtmlFormat.escape(trustName),
                  changeUrl = Some(TrustNameController.onPageLoad(NormalMode, fakeDraftId).url),
                  canEdit = false
                ),
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
                ),
                AnswerRow(
                  label = "whenTrustSetup.checkYourAnswersLabel",
                  answer = HtmlFormat.escape("3 February 1996"),
                  changeUrl = Some(WhenTrustSetupController.onPageLoad(NormalMode, fakeDraftId).url),
                  canEdit = false
                )
              ),
              sectionKey = sectionKey
            )
          )
        }
      }

      "registering a new trust" in {

        val userAnswers: UserAnswers = emptyUserAnswers
          .set(TrustNamePage, trustName).success.value
          .set(WhenTrustSetupPage, setupDate).success.value

        val helper = new CheckYourAnswersHelper(countryOptions)(userAnswers, fakeDraftId, false)

        helper.trustDetails.get mustBe Seq(
          AnswerSection(
            headingKey = None,
            rows = Seq(
              AnswerRow(
                label = "trustName.checkYourAnswersLabel",
                answer = HtmlFormat.escape(trustName),
                changeUrl = Some(TrustNameController.onPageLoad(NormalMode, fakeDraftId).url),
                canEdit = false
              ),
              AnswerRow(
                label = "whenTrustSetup.checkYourAnswersLabel",
                answer = HtmlFormat.escape("3 February 1996"),
                changeUrl = Some(WhenTrustSetupController.onPageLoad(NormalMode, fakeDraftId).url),
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
