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
import models.{AddABeneficiary, AddATrustee, AddAssets, UserAnswers}
import navigation.TaskListNavigator
import utils.{AddABeneficiaryViewHelper, AddATrusteeViewHelper, AddAssetViewHelper}
import viewmodels.{Completed, InProgress, Link, Tag, Task}
import play.api.i18n._

class RegistrationProgress @Inject()(navigator : TaskListNavigator) {

  def sections(userAnswers: UserAnswers)(implicit messages: Messages) = List(
    Task(Link(TrustDetails, navigator.nextPage(TrustDetails, userAnswers).url), trustDetailsSectionStatus(userAnswers)),
    Task(Link(Settlors, navigator.nextPage(Settlors, userAnswers).url), settlorsSectionStatus(userAnswers)),
    Task(Link(Trustees, navigator.nextPage(Trustees, userAnswers).url), trusteesSectionStatus(userAnswers)),
    Task(Link(Beneficiaries, navigator.nextPage(Beneficiaries, userAnswers).url), beneficiariesSectionStatus(userAnswers)),
    Task(Link(Assets, navigator.nextPage(Assets, userAnswers).url), assetsSectionStatus(userAnswers)),
    Task(Link(TaxLiability, navigator.nextPage(TaxLiability, userAnswers).url), None)
  )

  def trustDetailsSectionStatus(userAnswers: UserAnswers)(implicit messages: Messages): Option[Tag] = {
    val trustName = userAnswers.get(TrustNamePage).nonEmpty
    val trustResidentOffshore = userAnswers.get(TrustResidentOffshorePage).getOrElse(None)
    val trustPreviouslyResident = userAnswers.get(TrustPreviouslyResidentPage).nonEmpty
    val nonResidentType = userAnswers.get(NonResidentTypePage).nonEmpty
    val inheritanceTaxAct = userAnswers.get(InheritanceTaxActPage).getOrElse(None)
    val agentOtherThanBarrister = userAnswers.get(AgentOtherThanBarristerPage).nonEmpty
    (trustName, trustResidentOffshore, trustPreviouslyResident, nonResidentType, inheritanceTaxAct, agentOtherThanBarrister) match {
      case (true, false, _, _, _, _) => Some(Completed) // completed with trust resident in UK
      case (true, true, true, _, _, _) => Some(Completed) // completed with nonUK country trust previously resident
      case (true, _, _, true, _, _) => Some(Completed) // completed with non resident type
      case (true, _, _, _, false, _) => Some(Completed) // completed with Not registering for purpose of Inheritance Tax Act 1984
      case (true, _, _, _, _, true) => Some(Completed) // completed with agent other than barrister
      case (true, _, _, _, _, _) => Some(InProgress) // In progress
      case (_, _, _, _, _, _) => None
    }
  }


  def beneficiariesSectionStatus(userAnswers: UserAnswers)(implicit messages: Messages): Option[Tag] = {
    val Beneficiaries = new AddABeneficiaryViewHelper(userAnswers).rows
    val addAnother = userAnswers.get(AddABeneficiaryPage)
    (Beneficiaries.inProgress.isEmpty, Beneficiaries.complete.isEmpty, addAnother) match {
      case (true, false, Some(AddABeneficiary.NoComplete)) => Some(Completed) // None in progress, at least one completed and selected No more to add
      case (_, false, Some(AddABeneficiary.YesNow)) => Some(InProgress)  // Has at least one completed and selected 'add now' or 'add more later'
      case (_, false, Some(AddABeneficiary.YesLater)) => Some(InProgress)  // Has at least one completed and selected 'add now' or 'add more later'
      case (false, _, _ ) => Some(InProgress)  // Has at least one in progress
      case (_, _, _) => None
    }
  }

  def trusteesSectionStatus(userAnswers: UserAnswers)(implicit messages: Messages): Option[Tag] = {
    val trustees = new AddATrusteeViewHelper(userAnswers).rows
    val addAnother = userAnswers.get(AddATrusteePage)
    val hasLeadTrustee = userAnswers.get(pages.Trustees).toList.flatten.filter(_.lead).nonEmpty
    (trustees.inProgress.isEmpty, trustees.complete.isEmpty, addAnother, hasLeadTrustee) match {
      case (true, false, Some(AddATrustee.NoComplete), true) => Some(Completed)
      case (_, false, _, _) => Some(InProgress)
      case (false, _, _ , _) => Some(InProgress)
      case (_, _, _, _) => None
    }
  }

  def settlorsSectionStatus(userAnswers: UserAnswers)(implicit messages: Messages): Option[Tag] = {
    val settlorTrustSetup= userAnswers.get(SetupAfterSettlorDiedPage).isDefined
    val settlorNino = userAnswers.get(SettlorNationalInsuranceNumberPage).nonEmpty
    val settlorKnowAddressYesNo = userAnswers.get(SettlorsLastKnownAddressYesNoPage).getOrElse(None)
    val settlorAddressUK = userAnswers.get(SettlorsUKAddressPage).nonEmpty
    val settlorAddressInternational = userAnswers.get(SettlorsInternationalAddressPage).nonEmpty
    (settlorTrustSetup, settlorNino, settlorKnowAddressYesNo, settlorAddressUK, settlorAddressInternational) match {
      case (true, _, false, _, _) => Some(Completed) // completed with Name
      case (true, true, _, _, _) => Some(Completed) // completed with Nino
      case (true, false, _, true, _) => Some(Completed) // completed with UK Address
      case (true, false, _, _, true) => Some(Completed) // completed with International Address
      case (true, _, _, _, _) => Some(InProgress) // In progress
      case (_, _, _, _, _) => None
    }
  }

  def assetsSectionStatus(userAnswers: UserAnswers)(implicit messages: Messages): Option[Tag] = {
    val assets = new AddAssetViewHelper(userAnswers).rows
    val addAnother = userAnswers.get(AddAssetsPage)
    (assets.inProgress.isEmpty, assets.complete.isEmpty, addAnother) match {
      case (true, false, Some(AddAssets.NoComplete)) => Some(Completed)
      case (_, false, _) => Some(InProgress)
      case (false, _, _ ) => Some(InProgress)
      case (_, _, _) => None
    }
  }

  def isTaskListComplete(userAnswers: UserAnswers)(implicit messages: Messages):Boolean = {
    trustDetailsSectionStatus(userAnswers) == Some(Completed) &&
    beneficiariesSectionStatus(userAnswers) == Some(Completed) &&
      trusteesSectionStatus(userAnswers) == Some(Completed) &&
      settlorsSectionStatus(userAnswers) == Some(Completed) &&
      assetsSectionStatus(userAnswers) == Some(Completed)
  }


}
