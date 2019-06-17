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
import pages.entitystatus.{DeceasedSettlorStatus, TrustDetailsStatus}
import play.api.mvc.Call
import viewmodels._

@Singleton
class TaskListNavigator @Inject()() {

  private def trustDetailsRoute(draftId: String)(answers: UserAnswers) = {
    val completed = answers.get(TrustDetailsStatus).contains(Completed)
    completed match {
      case true =>
        routes.TrustDetailsAnswerPageController.onPageLoad(draftId)
      case _ =>
        routes.TrustNameController.onPageLoad(NormalMode, draftId)
    }
  }

  private def trusteeRoute(draftId: String)(answers: UserAnswers) = {
    answers.get(viewmodels.Trustees).getOrElse(Nil) match {
      case Nil =>
        routes.TrusteesInfoController.onPageLoad(draftId)
      case _ :: _ =>
        routes.AddATrusteeController.onPageLoad(draftId)
    }
  }

  private def settlorRoute(draftId: String)(answers: UserAnswers) = {
    val deceasedCompleted = answers.get(DeceasedSettlorStatus).contains(Completed)
    deceasedCompleted match {
      case true =>
        routes.DeceasedSettlorAnswerController.onPageLoad(draftId)
      case _ =>
        routes.SetupAfterSettlorDiedController.onPageLoad(NormalMode,draftId)
    }
  }

  private def beneficiaryRoute(draftId: String)(answers: UserAnswers) = {
    isAnyBeneficiaryAdded(answers) match {
      case true =>
        routes.AddABeneficiaryController.onPageLoad(draftId)
      case false =>
        routes.IndividualBeneficiaryInfoController.onPageLoad(draftId)
    }
  }

  private def isAnyBeneficiaryAdded(answers: UserAnswers) = {

    val individuals = answers.get(IndividualBeneficiaries).getOrElse(Nil)
    val classes = answers.get(ClassOfBeneficiaries).getOrElse(Nil)

    individuals.nonEmpty || classes.nonEmpty
  }

  private def assetRoute(draftId: String)(answers: UserAnswers) = {
    answers.get(viewmodels.Assets).getOrElse(Nil) match {
      case _ :: _ =>
        routes.AddAssetsController.onPageLoad(draftId)
      case Nil =>
        routes.AssetInterruptPageController.onPageLoad(draftId)
    }
  }

  private def taskListRoutes(draftId: String): Page => UserAnswers => Call = {
    case TrustDetails => trustDetailsRoute(draftId)
    case Trustees => trusteeRoute(draftId)
    case Settlors => settlorRoute(draftId)
    case Beneficiaries => beneficiaryRoute(draftId)
    case TaxLiability => _ => routes.TaskListController.onPageLoad(draftId)
    case Assets => assetRoute(draftId)
    case _ => _ => routes.IndexController.onPageLoad(draftId)
  }

  def nextPage(page: Page, userAnswers: UserAnswers, draftId: String) : Call = {
    taskListRoutes(draftId)(page)(userAnswers)
  }

}
