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

package navigation

import controllers.routes
import javax.inject.{Inject, Singleton}
import models.{NormalMode, UserAnswers}
import pages._
import play.api.mvc.Call

@Singleton
class TaskListNavigator @Inject()() {

  private def trustDetailsRoute(answers: UserAnswers) = {
    val (trustName, whenSetup) = (answers.get(TrustNamePage), answers.get(WhenTrustSetupPage))

    (trustName, whenSetup) match {
      case (Some(_), Some(_)) =>
        routes.TrustDetailsAnswerPageController.onPageLoad()
      case _ =>
        routes.TrustNameController.onPageLoad(NormalMode)
    }
  }

  private def trusteeRoute(answers: UserAnswers) = {
    answers.get(Trustees).getOrElse(Nil) match {
      case Nil =>
        routes.TrusteesInfoController.onPageLoad()
      case _ :: _ =>
        routes.AddATrusteeController.onPageLoad()
    }
  }

  private def settlorRoute(answers: UserAnswers) = {
    answers.get(SettlorsNamePage) match {
      case Some(_) =>
        routes.DeceasedSettlorAnswerController.onPageLoad()
      case None =>
        routes.SetupAfterSettlorDiedController.onPageLoad(NormalMode)
    }
  }


  private val taskListRoutes : Page => UserAnswers => Call = {
    case TrustDetails => trustDetailsRoute
    case Trustees => trusteeRoute
    case Settlors => settlorRoute
    case Beneficiaries => _ => routes.TaskListController.onPageLoad()
    case TaxLiability => _ => routes.TaskListController.onPageLoad()
    case Assets => _ => routes.AssetInterruptPageController.onPageLoad()
    case _ => _ => routes.IndexController.onPageLoad()
  }

  def nextPage(page: Page, userAnswers: UserAnswers) : Call = {
    taskListRoutes(page)(userAnswers)
  }

}
