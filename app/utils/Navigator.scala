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

import javax.inject.{Inject, Singleton}
import play.api.mvc.Call
import controllers.routes
import pages.{TrustNamePage, _}
import models.{CheckMode, Mode, NormalMode}

@Singleton
class Navigator @Inject()() {

  private val routeMap: Map[Page, UserAnswers => Call] = Map(
    TrustNamePage -> (_=> routes.TrustAddressUKYesNoController.onPageLoad(NormalMode)),
    TrustAddressUKYesNoPage -> trustAddressUKYesNoRoute()
  )

  private val checkRouteMap: Map[Page, UserAnswers => Call] = Map(

  )

  private def trustAddressUKYesNoRoute()(answers: UserAnswers ) = answers.get(TrustAddressUKYesNoPage) match {
    case Some(true) => routes.TrustAddressUKController.onPageLoad(NormalMode)
    case Some(false) => routes.IndexController.onPageLoad()
    case None =>routes.IndexController.onPageLoad()
  }

  def nextPage(page: Page, mode: Mode): UserAnswers => Call = mode match {
    case NormalMode =>
      routeMap.getOrElse(page, _ => routes.IndexController.onPageLoad())
    case CheckMode =>
      checkRouteMap.getOrElse(page, _ => routes.CheckYourAnswersController.onPageLoad())
  }
}
