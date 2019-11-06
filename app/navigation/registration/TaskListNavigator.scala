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

package navigation.registration

import controllers.routes
import javax.inject.{Inject, Singleton}
import mapping.reads.{Assets, Trustees}
import models.Status.Completed
import models.{NormalMode, UserAnswers}
import pages._
import pages.entitystatus.{DeceasedSettlorStatus, TrustDetailsStatus}
import play.api.mvc.Call
import sections._

@Singleton
class TaskListNavigator @Inject()() {

  private def trustDetailsRoute(draftId: String)(answers: UserAnswers) = {
    val completed = answers.get(TrustDetailsStatus).contains(Completed)
    if (completed) {
      routes.TrustDetailsAnswerPageController.onPageLoad(draftId)
    } else {
      routes.TrustNameController.onPageLoad(NormalMode, draftId)
    }
  }

  private def trusteeRoute(draftId: String)(answers: UserAnswers) = {
    answers.get(sections.Trustees).getOrElse(Nil) match {
      case Nil =>
        controllers.trustees.routes.TrusteesInfoController.onPageLoad(draftId)
      case _ :: _ =>
        controllers.trustees.routes.AddATrusteeController.onPageLoad(draftId)
    }
  }

  private def settlorRoute(draftId: String)(answers: UserAnswers) = {
    answers.get(DeceasedSettlorStatus) match {
      case Some(value) =>
        if(value.equals(Completed)) {
          controllers.deceased_settlor.routes.DeceasedSettlorAnswerController.onPageLoad(draftId)
        } else {
          routes.SetupAfterSettlorDiedController.onPageLoad(NormalMode,draftId)
        }
      case None =>
        answers.get(SetupAfterSettlorDiedPage) match {
          case None => controllers.routes.SettlorInfoController.onPageLoad(draftId)
          case _ =>
            answers.get (LivingSettlors).getOrElse (Nil) match {
              case Nil => controllers.routes.SetupAfterSettlorDiedController.onPageLoad (NormalMode, draftId)
              case _ => controllers.routes.AddASettlorController.onPageLoad (draftId)
            }
        }
    }
  }

  private def beneficiaryRoute(draftId: String)(answers: UserAnswers) = {
    if(isAnyBeneficiaryAdded(answers)) {
      routes.AddABeneficiaryController.onPageLoad(draftId)
    } else {
      routes.IndividualBeneficiaryInfoController.onPageLoad(draftId)
    }
  }

  private def isAnyBeneficiaryAdded(answers: UserAnswers) = {

    val individuals = answers.get(IndividualBeneficiaries).getOrElse(Nil)
    val classes = answers.get(ClassOfBeneficiaries).getOrElse(Nil)

    individuals.nonEmpty || classes.nonEmpty
  }

  private def assetRoute(draftId: String)(answers: UserAnswers) = {
    answers.get(sections.Assets).getOrElse(Nil) match {
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
    case _ => _ => routes.IndexController.onPageLoad()
  }

  def nextPage(page: Page, userAnswers: UserAnswers, draftId: String) : Call = {
    taskListRoutes(draftId)(page)(userAnswers)
  }

}
