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
import models.Status.Completed
import models.entities.{Assets, Trustees}
import models.{NormalMode, UserAnswers}
import pages._
import pages.entitystatus.TrustDetailsStatus
import play.api.mvc.Call
import viewmodels._

@Singleton
class TaskListNavigator @Inject()() {

  private def trustDetailsRoute(answers: UserAnswers) = {
    val completed = answers.get(TrustDetailsStatus).contains(Completed)
    completed match {
      case true =>
        routes.TrustDetailsAnswerPageController.onPageLoad()
      case _ =>
        routes.TrustNameController.onPageLoad(NormalMode)
    }
  }

  private def trusteeRoute(answers: UserAnswers) = {
    answers.get(viewmodels.Trustees).getOrElse(Nil) match {
      case Nil =>
        routes.TrusteesInfoController.onPageLoad()
      case _ :: _ =>
        routes.AddATrusteeController.onPageLoad()
    }
  }

  private def settlorRoute(answers: UserAnswers) = {
    val deceasedCompleted = answers.get(DeceasedSettlorComplete).contains(Completed)
    deceasedCompleted match {
      case true =>
        routes.DeceasedSettlorAnswerController.onPageLoad()
      case _ =>
        routes.SetupAfterSettlorDiedController.onPageLoad(NormalMode)
    }
  }

  private def beneficiaryRoute(answers: UserAnswers) = {
    isAnyBeneficiaryAdded(answers) match {
      case true =>
        routes.AddABeneficiaryController.onPageLoad()
      case false =>
        routes.IndividualBeneficiaryInfoController.onPageLoad()
    }
  }

  private def isAnyBeneficiaryAdded(answers: UserAnswers) = {

    val individuals = answers.get(IndividualBeneficiaries).getOrElse(Nil)
    val classes = answers.get(ClassOfBeneficiaries).getOrElse(Nil)

    individuals.nonEmpty || classes.nonEmpty
  }

  private def assetRoute(answers: UserAnswers) = {
    answers.get(viewmodels.Assets).getOrElse(Nil) match {
      case _ :: _ =>
        routes.AddAssetsController.onPageLoad()
      case Nil =>
        routes.AssetInterruptPageController.onPageLoad()
    }
  }

  private val taskListRoutes : Page => UserAnswers => Call = {
    case TrustDetails => trustDetailsRoute
    case Trustees => trusteeRoute
    case Settlors => settlorRoute
    case Beneficiaries => beneficiaryRoute
    case TaxLiability => _ => routes.TaskListController.onPageLoad()
    case Assets => assetRoute
    case _ => _ => routes.IndexController.onPageLoad()
  }

  def nextPage(page: Page, userAnswers: UserAnswers) : Call = {
    taskListRoutes(page)(userAnswers)
  }

}
