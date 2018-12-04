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

package utils

import controllers.routes
import models.CheckMode
import pages._
import viewmodels.{AnswerRow}

class CheckYourAnswersHelper(userAnswers: UserAnswers) {

  def trustSettledDate: Option[AnswerRow] = userAnswers.get(TrustSettledDatePage) map {
    x => AnswerRow("trustSettledDate.checkYourAnswersLabel", s"$x", false, routes.TrustSettledDateController.onPageLoad(CheckMode).url)
  }

  def trustContactPhoneNumber: Option[AnswerRow] = userAnswers.get(TrustContactPhoneNumberPage) map {
    x => AnswerRow("trustContactPhoneNumber.checkYourAnswersLabel", s"$x", false, routes.TrustContactPhoneNumberController.onPageLoad(CheckMode).url)
  }

  def internationalAddress: Option[AnswerRow] = userAnswers.get(TrustsAddressInternationalPage) map {
    x => AnswerRow("internationalTrustsAddress.checkYourAnswersLabel",
      s"${x.line1} ${x.line2} ${x.line3.getOrElse("")} ${x.country}",
      false, routes.TrustsAddressInternationalController.onPageLoad(CheckMode).url)
  }

  def trustAddressUK: Option[AnswerRow] = userAnswers.get(TrustAddressUKPage) map {
    x => AnswerRow("trustAddressUK.checkYourAnswersLabel",
      s"${x.line1} ${x.line2.getOrElse("")} ${x.line3.getOrElse("")} ${x.town} ${x.postcode}",
      false, routes.TrustAddressUKController.onPageLoad(CheckMode).url)
  }

  def trustName: Option[AnswerRow] = userAnswers.get(TrustNamePage) map {
    x => AnswerRow("trustName.checkYourAnswersLabel", s"$x", false, routes.TrustNameController.onPageLoad(CheckMode).url)
  }

  def trustAddressUKYesNo: Option[AnswerRow] = userAnswers.get(TrustAddressUKYesNoPage) map {
    x => AnswerRow("trustAddressUKYesNo.checkYourAnswersLabel",
      if(x) "site.yes" else "site.no", true, routes.TrustAddressUKYesNoController.onPageLoad(CheckMode).url)
  }
}
