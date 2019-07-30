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

package pages

import javax.inject.Inject
import models.Status.{Completed, InProgress}
import mapping.reads.{Assets, Trustees}
import models.{AddABeneficiary, AddATrustee, AddAssets, Status, UserAnswers}
import navigation.TaskListNavigator
import pages.entitystatus.{DeceasedSettlorStatus, TrustDetailsStatus}
import sections.{Beneficiaries, ClassOfBeneficiaries, IndividualBeneficiaries, Settlors, TaxLiability, TrustDetails}
import viewmodels._
import viewmodels.addAnother.MoneyAssetViewModel

class RegistrationProgress @Inject()(navigator : TaskListNavigator){

  def items(userAnswers: UserAnswers, draftId: String) = List(
    Task(Link(TrustDetails, navigator.nextPage(TrustDetails, userAnswers, draftId).url), isTrustDetailsComplete(userAnswers)),
    Task(Link(Settlors, navigator.nextPage(Settlors, userAnswers, draftId).url), isDeceasedSettlorComplete(userAnswers)),
    Task(Link(Trustees, navigator.nextPage(Trustees, userAnswers, draftId).url), isTrusteesComplete(userAnswers)),
    Task(Link(Beneficiaries, navigator.nextPage(Beneficiaries, userAnswers, draftId).url), isBeneficiariesComplete(userAnswers)),
    Task(Link(Assets, navigator.nextPage(Assets, userAnswers, draftId).url), assetsStatus(userAnswers)),
    Task(Link(TaxLiability, navigator.nextPage(TaxLiability, userAnswers, draftId).url), None)
  )

  private def determineStatus(complete : Boolean) = {
    if (complete) {
      Some(Completed)
    } else{
      Some(InProgress)
    }
  }

  def isTrustDetailsComplete(userAnswers: UserAnswers) : Option[Status] = {
    userAnswers.get(WhenTrustSetupPage) match {
      case None => None
      case Some(_) =>
        val completed = userAnswers.get(TrustDetailsStatus).contains(Completed)
        determineStatus(completed)
    }
  }

  def isTrusteesComplete(userAnswers: UserAnswers) : Option[Status] = {
    val noMoreToAdd = userAnswers.get(AddATrusteePage).contains(AddATrustee.NoComplete)

    userAnswers.get(_root_.sections.Trustees) match {
      case Some(l) =>

        val hasLeadTrustee = l.exists(_.isLead)

        val isComplete = !l.exists(_.status == InProgress) && noMoreToAdd && hasLeadTrustee

        determineStatus(isComplete)
      case None =>
        None
    }
  }

  def isDeceasedSettlorComplete(userAnswers: UserAnswers) : Option[Status] = {
    val setUpAfterSettlorDied = userAnswers.get(SetupAfterSettlorDiedPage)

    setUpAfterSettlorDied match {
      case None => None
      case Some(x) =>
        val deceasedCompleted = userAnswers.get(DeceasedSettlorStatus)

        val isComplete = x && deceasedCompleted.contains(Completed)

        determineStatus(isComplete)
    }
  }

  def isBeneficiariesComplete(userAnswers: UserAnswers) : Option[Status] = {

    val noMoreToAdd = userAnswers.get(AddABeneficiaryPage).contains(AddABeneficiary.NoComplete)

    val individuals = userAnswers.get(IndividualBeneficiaries)
    val classes = userAnswers.get(ClassOfBeneficiaries)

    (individuals, classes) match {
      case (None, None) => None
      case (Some(ind), None) =>
        val indComplete = !ind.exists(_.status == InProgress)
        determineStatus(indComplete && noMoreToAdd)
      case (None, Some(c)) =>
        val classComplete = !c.exists(_.status == InProgress)
        determineStatus(classComplete && noMoreToAdd)
      case (Some(ind), Some(c)) =>
        val indComplete = !ind.exists(_.status == InProgress)
        val classComplete = !c.exists(_.status == InProgress)

        determineStatus(indComplete && classComplete && noMoreToAdd)
    }
  }

  def assetsStatus(userAnswers: UserAnswers) : Option[Status] = {
    val noMoreToAdd = userAnswers.get(AddAssetsPage).contains(AddAssets.NoComplete)
    val assets = userAnswers.get(sections.Assets).getOrElse(List.empty)

    assets match {
      case Nil => None
      case list =>

        println(s"Determining status for assets $list")

        val status = !list.exists(_.status == InProgress) && noMoreToAdd
        determineStatus(status)
    }
  }

  def isTaskListComplete(userAnswers: UserAnswers) : Boolean = {
    isTrustDetailsComplete(userAnswers).contains(Completed) &&
    isDeceasedSettlorComplete(userAnswers).contains(Completed) &&
    isTrusteesComplete(userAnswers).contains(Completed) &&
    isBeneficiariesComplete(userAnswers).contains(Completed) &&
    assetsStatus(userAnswers).contains(Completed)
  }



}
