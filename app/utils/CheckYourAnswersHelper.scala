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
import models.{CheckMode, UserAnswers}
import pages._
import viewmodels.{AnswerRow, RepeaterAnswerRow, RepeaterAnswerSection}

class CheckYourAnswersHelper(userAnswers: UserAnswers) {

  def administrationOutsideUK: Option[AnswerRow] = userAnswers.get(AdministrationOutsideUKPage) map {
    x => AnswerRow("administrationOutsideUK.checkYourAnswersLabel", if(x) "site.yes" else "site.no", true, routes.AdministrationOutsideUKController.onPageLoad(CheckMode).url)
  }

  def countryGoverningTrust: Option[AnswerRow] = userAnswers.get(CountryGoverningTrustPage) map {
    x => AnswerRow("countryGoverningTrust.checkYourAnswersLabel", s"$x", false, routes.CountryGoverningTrustController.onPageLoad(CheckMode).url)
  }

  def governedOutsideTheUK: Option[AnswerRow] = userAnswers.get(GovernedOutsideTheUKPage) map {
    x => AnswerRow("governedOutsideTheUK.checkYourAnswersLabel", if(x) "site.yes" else "site.no", true, routes.GovernedOutsideTheUKController.onPageLoad(CheckMode).url)
  }

  def trustName: Option[AnswerRow] = userAnswers.get(TrustNamePage) map {
    x => AnswerRow("trustName.checkYourAnswersLabel", s"$x", false, routes.TrustNameController.onPageLoad(CheckMode).url)
  }
}
