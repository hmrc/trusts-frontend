/*
 * Copyright 2021 HM Revenue & Customs
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

package navigation.routes

import controllers.register.suitability.routes
import models.core.TrustsFrontendUserAnswers
import pages.Page
import pages.register.suitability._
import play.api.mvc.Call

object SuitabilityRoutes extends Routes {

  def route(): PartialFunction[Page, TrustsFrontendUserAnswers[_] => Call] = {
    case ExpressTrustYesNoPage => ua =>
      ua.get(TrustTaxableYesNoPage) match {
        case Some(true) => routes.BeforeYouContinueController.onPageLoad()
        case _ => routes.TaxLiabilityInCurrentTaxYearYesNoController.onPageLoad()
      }
    case TaxLiabilityInCurrentTaxYearYesNoPage =>
      yesNoNav(
        _,
        TaxLiabilityInCurrentTaxYearYesNoPage,
        routes.BeforeYouContinueController.onPageLoad(),
        routes.UndeclaredTaxLiabilityYesNoController.onPageLoad()
      )
    case UndeclaredTaxLiabilityYesNoPage => ua =>
      yesNoNav(
        ua,
        UndeclaredTaxLiabilityYesNoPage,
        routes.BeforeYouContinueController.onPageLoad(),
        nonTaxableRoute(ua)
      )
  }

  private def nonTaxableRoute(answers: TrustsFrontendUserAnswers[_]): Call = {
      yesNoNav(
        answers,
        ExpressTrustYesNoPage,
        routes.BeforeYouContinueController.onPageLoad(),
        routes.NoNeedToRegisterController.onPageLoad()
      )
  }

}
