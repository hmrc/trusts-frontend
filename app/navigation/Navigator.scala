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

import javax.inject.{Inject, Singleton}
import play.api.mvc.Call
import controllers.routes
import models.AddATrustee.{NoComplete, YesLater, YesNow}
import models.IndividualOrBusiness.Individual
import models.WhatKindOfAsset.{Business, Money, Other, Partnership, PropertyOrLand, Shares}
import pages._
import models._
import models.entities.Assets
import uk.gov.hmrc.auth.core.AffinityGroup
import viewmodels.{ClassOfBeneficiaries, IndividualBeneficiaries}

@Singleton
class Navigator @Inject()() {

  private val normalRoutes: Page => AffinityGroup => UserAnswers => Call = {
    //  Matching
    case TrustRegisteredOnlinePage => _ => _ => routes.TrustHaveAUTRController.onPageLoad(NormalMode)
    case TrustHaveAUTRPage => af => userAnswers => trustHaveAUTRRoute(userAnswers, af)
    case WhatIsTheUTRPage => _ => _ => routes.TrustNameController.onPageLoad(NormalMode)
    case PostcodeForTheTrustPage => _ => _ => routes.FailedMatchController.onPageLoad()

    //  Trust Details
    case TrustNamePage => _ => trustNameRoute
    case WhenTrustSetupPage => _ => _ => routes.GovernedInsideTheUKController.onPageLoad(NormalMode)
    case GovernedInsideTheUKPage => _ => isTrustGovernedInsideUKRoute
    case CountryGoverningTrustPage => _ => _ => routes.AdministrationInsideUKController.onPageLoad(NormalMode)
    case AdministrationInsideUKPage => _ => isTrustGeneralAdministrationRoute
    case CountryAdministeringTrustPage => _ => _ => routes.TrustResidentInUKController.onPageLoad(NormalMode)
    case TrustResidentInUKPage => _ => isTrustResidentInUKRoute
    case EstablishedUnderScotsLawPage => _ => _ => routes.TrustResidentOffshoreController.onPageLoad(NormalMode)
    case TrustResidentOffshorePage => _ => wasTrustPreviouslyResidentOffshoreRoute
    case TrustPreviouslyResidentPage => _ => _ => routes.TrustDetailsAnswerPageController.onPageLoad()
    case RegisteringTrustFor5APage => _ => registeringForPurposeOfSchedule5ARoute
    case NonResidentTypePage => _ => _ => routes.TrustDetailsAnswerPageController.onPageLoad()
    case InheritanceTaxActPage => _ => inheritanceTaxRoute
    case AgentOtherThanBarristerPage => _ => _ => routes.TrustDetailsAnswerPageController.onPageLoad()
    case TrustDetailsAnswerPage => _ => _ => routes.TaskListController.onPageLoad()

    //  Trustees
    case IsThisLeadTrusteePage(index) => _ =>_ => routes.TrusteeIndividualOrBusinessController.onPageLoad(NormalMode, index)
    case TrusteeIndividualOrBusinessPage(index)  => _ => ua => trusteeIndividualOrBusinessRoute(ua, index)

    case TrusteesNamePage(index) => _ => _ => routes.TrusteesDateOfBirthController.onPageLoad(NormalMode, index)
    case TrusteesDateOfBirthPage(index) => _ => ua => trusteeDateOfBirthRoute(ua, index)
    case TrusteeAUKCitizenPage(index) => _ => ua => trusteeAUKCitizenRoute(ua, index)
    case TrusteesNinoPage(index) => _ => _ => routes.TrusteeLiveInTheUKController.onPageLoad(NormalMode, index)
    case TrusteeLiveInTheUKPage(index)  => _ => ua => trusteeLiveInTheUKRoute(ua, index)
    case TrusteesUkAddressPage(index) => _ => _ => routes.TelephoneNumberController.onPageLoad(NormalMode, index)
    case TelephoneNumberPage(index) => _ => _ => routes.TrusteesAnswerPageController.onPageLoad(index)
    case TrusteesAnswerPage => _ => _ => routes.AddATrusteeController.onPageLoad()
    case AddATrusteePage => _ => addATrusteeRoute

    //Agents
    case AgentInternalReferencePage => _ => _ => routes.AgentNameController.onPageLoad(NormalMode)
    case AgentNamePage => _ => _ => routes.AgentAddressYesNoController.onPageLoad(NormalMode)
    case AgentAddressYesNoPage => _ => ua => agentAddressYesNoRoute(ua)
    case AgentUKAddressPage => _ => _ => routes.AgentTelephoneNumberController.onPageLoad(NormalMode)
    case AgentInternationalAddressPage => _ => _ => routes.AgentTelephoneNumberController.onPageLoad(NormalMode)
    case AgentTelephoneNumberPage => _ => _ => routes.AgentAnswerController.onPageLoad()
    case AgentAnswerPage => _ => _ => routes.TaskListController.onPageLoad()

    //Assets
    case AssetMoneyValuePage(index) => _ => ua => assetMoneyValueRoute(ua, index)
    case WhatKindOfAssetPage(index) => _ => ua => whatKindOfAssetRoute(ua, index)
    case AddAssetsPage => _ => addAssetsRoute

    //Settlors
    case SetupAfterSettlorDiedPage => _ => setupAfterSettlorDiedRoute
    case SettlorsNamePage => _ => _ => routes.SettlorDateOfDeathYesNoController.onPageLoad(NormalMode)
    case SettlorDateOfDeathYesNoPage => _ => deceasedSettlorDateOfDeathRoute
    case SettlorDateOfBirthYesNoPage => _ => deceasedSettlorDateOfBirthRoute
    case SettlorsDateOfBirthPage => _ => _ => routes.SettlorsNINoYesNoController.onPageLoad(NormalMode)
    case SettlorsNINoYesNoPage => _ => deceasedSettlorNinoRoute
    case SettlorsLastKnownAddressYesNoPage => _ => deceasedSettlorLastKnownAddressRoute
    case SettlorDateOfDeathPage => _ => _ => routes.SettlorDateOfBirthYesNoController.onPageLoad(NormalMode)
    case SettlorNationalInsuranceNumberPage => _ => _ => routes.DeceasedSettlorAnswerController.onPageLoad()
    case WasSettlorsAddressUKYesNoPage => _ => deceasedSettlorAddressRoute
    case SettlorsInternationalAddressPage => _ => _ => routes.DeceasedSettlorAnswerController.onPageLoad()
    case SettlorsUKAddressPage => _ => _ => routes.DeceasedSettlorAnswerController.onPageLoad()
    case DeceasedSettlorAnswerPage => _ => _ => routes.TaskListController.onPageLoad()

     //Beneficiary
    case IndividualBeneficiaryNamePage(index) => _ => _ => routes.IndividualBeneficiaryDateOfBirthYesNoController.onPageLoad(NormalMode, index)
    case IndividualBeneficiaryDateOfBirthYesNoPage(index) => _ => ua => individualBeneficiaryDateOfBirthRoute(ua, index)
    case IndividualBeneficiaryDateOfBirthPage(index) => _ => _ => routes.IndividualBeneficiaryIncomeYesNoController.onPageLoad(NormalMode, index)
    case IndividualBeneficiaryIncomeYesNoPage(index) => _ => ua => individualBeneficiaryIncomeRoute(ua, index)
    case IndividualBeneficiaryIncomePage(index) => _ => _ => routes.IndividualBeneficiaryNationalInsuranceYesNoController.onPageLoad(NormalMode, index)
    case IndividualBeneficiaryNationalInsuranceYesNoPage(index) => _ => ua => individualBeneficiaryNationalInsuranceYesNoRoute(ua, index)
    case IndividualBeneficiaryNationalInsuranceNumberPage(index) => _ => _ =>
      routes.IndividualBeneficiaryVulnerableYesNoController.onPageLoad(NormalMode, index)
    case IndividualBeneficiaryAddressYesNoPage(index) => _ => ua => individualBeneficiaryAddressRoute(ua, index)
    case IndividualBeneficiaryAddressUKYesNoPage(index) => _ => ua => individualBeneficiaryAddressUKYesNoRoute(ua, index)
    case IndividualBeneficiaryAddressUKPage(index)  => _ => _ => routes.IndividualBeneficiaryVulnerableYesNoController.onPageLoad(NormalMode, index)
    case IndividualBeneficiaryVulnerableYesNoPage(index) => _ => _ => routes.IndividualBeneficiaryAnswersController.onPageLoad(index)
    case IndividualBeneficiaryAnswersPage => _ => _ => routes.AddABeneficiaryController.onPageLoad()

    case AddABeneficiaryPage => _ => addABeneficiaryRoute
    case WhatTypeOfBeneficiaryPage => _ => whatTypeOfBeneficiaryRoute
    case ClassBeneficiaryDescriptionPage(index) => _ => _ => routes.AddABeneficiaryController.onPageLoad()


    //  Default
    case _ => _ => _ => routes.IndexController.onPageLoad()
  }


  private def whatTypeOfBeneficiaryRoute(userAnswers: UserAnswers) : Call = {
    val whatBeneficiaryToAdd = userAnswers.get(WhatTypeOfBeneficiaryPage)
    whatBeneficiaryToAdd match {
      case Some(WhatTypeOfBeneficiary.Individual) =>
        routeToIndividualBeneficiaryIndex(userAnswers)
      case Some(WhatTypeOfBeneficiary.ClassOfBeneficiary) =>
        routeToClassOfBeneficiaryIndex(userAnswers)
      case _ => routes.SessionExpiredController.onPageLoad()
    }
  }

  private def routeToIndividualBeneficiaryIndex(userAnswers: UserAnswers) = {
    val indBeneficiaries = userAnswers.get(IndividualBeneficiaries).getOrElse(List.empty)
    indBeneficiaries match {
      case Nil =>
        routes.IndividualBeneficiaryNameController.onPageLoad(NormalMode, 0)
      case t if t.nonEmpty =>
        routes.IndividualBeneficiaryNameController.onPageLoad(NormalMode, t.size)
    }
  }

  private def routeToClassOfBeneficiaryIndex(userAnswers: UserAnswers) = {
    val classOfBeneficiaries = userAnswers.get(ClassOfBeneficiaries).getOrElse(List.empty)
    classOfBeneficiaries match {
      case Nil =>
        routes.ClassBeneficiaryDescriptionController.onPageLoad(NormalMode, 0)
      case t if t.nonEmpty =>
        routes.ClassBeneficiaryDescriptionController.onPageLoad(NormalMode, t.size)
    }
  }

  private def agentAddressYesNoRoute(userAnswers: UserAnswers) : Call =
    userAnswers.get(AgentAddressYesNoPage) match {
      case Some(false) => routes.AgentInternationalAddressController.onPageLoad(NormalMode)
      case Some(true) => routes.AgentUKAddressController.onPageLoad(NormalMode)
      case _ => routes.SessionExpiredController.onPageLoad()
    }

  private def individualBeneficiaryAddressRoute(userAnswers: UserAnswers, index: Int) : Call =
    userAnswers.get(IndividualBeneficiaryAddressYesNoPage(index)) match {
      case Some(false) => routes.IndividualBeneficiaryVulnerableYesNoController.onPageLoad(NormalMode, index)
      case Some(true) => routes.IndividualBeneficiaryAddressUKYesNoController.onPageLoad(NormalMode, index)
      case _ => routes.SessionExpiredController.onPageLoad()
    }

  private def individualBeneficiaryAddressUKYesNoRoute(userAnswers: UserAnswers, index: Int) : Call =
    userAnswers.get(IndividualBeneficiaryAddressUKYesNoPage(index)) match {
      case Some(false) => routes.IndividualBeneficiaryAddressUKYesNoController.onPageLoad(NormalMode, index)
      case Some(true) => routes.IndividualBeneficiaryAddressUKController.onPageLoad(NormalMode, index)
      case _ => routes.SessionExpiredController.onPageLoad()
    }

  private def individualBeneficiaryNationalInsuranceYesNoRoute(userAnswers: UserAnswers, index: Int) : Call =
    userAnswers.get(IndividualBeneficiaryNationalInsuranceYesNoPage(index)) match {
      case Some(false) => routes.IndividualBeneficiaryAddressYesNoController.onPageLoad(NormalMode, index)
      case Some(true) => routes.IndividualBeneficiaryNationalInsuranceNumberController.onPageLoad(NormalMode, index)
      case _ => routes.SessionExpiredController.onPageLoad()
  }

  private def individualBeneficiaryIncomeRoute(userAnswers: UserAnswers, index: Int) : Call =
    userAnswers.get(IndividualBeneficiaryIncomeYesNoPage(index)) match {
      case Some(false) => routes.IndividualBeneficiaryIncomeController.onPageLoad(NormalMode, index)
      case Some(true) => routes.IndividualBeneficiaryNationalInsuranceYesNoController.onPageLoad(NormalMode, index)
      case _ => routes.SessionExpiredController.onPageLoad()
  }

  private def individualBeneficiaryDateOfBirthRoute(userAnswers: UserAnswers, index: Int) : Call =
    userAnswers.get(IndividualBeneficiaryDateOfBirthYesNoPage(index)) match {
    case Some(false) => routes.IndividualBeneficiaryIncomeYesNoController.onPageLoad(NormalMode, index)
    case Some(true) => routes.IndividualBeneficiaryDateOfBirthController.onPageLoad(NormalMode, index)
    case _ => routes.SessionExpiredController.onPageLoad()
  }


  private def setupAfterSettlorDiedRoute(userAnswers: UserAnswers) : Call = userAnswers.get(SetupAfterSettlorDiedPage) match {
    case Some(false) => routes.SetupAfterSettlorDiedController.onPageLoad(NormalMode)
    case Some(true) => routes.SettlorsNameController.onPageLoad(NormalMode)
    case _ => routes.SessionExpiredController.onPageLoad()
  }

  private def deceasedSettlorAddressRoute(userAnswers: UserAnswers) : Call = userAnswers.get(WasSettlorsAddressUKYesNoPage) match {
    case Some(false) => routes.SettlorsInternationalAddressController.onPageLoad(NormalMode)
    case Some(true) => routes.SettlorsUKAddressController.onPageLoad(NormalMode)
    case _ => routes.SessionExpiredController.onPageLoad()
  }

  private def deceasedSettlorLastKnownAddressRoute(userAnswers: UserAnswers) : Call = userAnswers.get(SettlorsLastKnownAddressYesNoPage) match {
    case Some(false) => routes.DeceasedSettlorAnswerController.onPageLoad()
    case Some(true) => routes.WasSettlorsAddressUKYesNoController.onPageLoad(NormalMode)
    case _ => routes.SessionExpiredController.onPageLoad()
  }

  private def deceasedSettlorNinoRoute(userAnswers: UserAnswers) : Call = userAnswers.get(SettlorsNINoYesNoPage) match {
    case Some(false) => routes.SettlorsLastKnownAddressYesNoController.onPageLoad(NormalMode)
    case Some(true) => routes.SettlorNationalInsuranceNumberController.onPageLoad(NormalMode)
    case _ => routes.SessionExpiredController.onPageLoad()
  }

  private def deceasedSettlorDateOfBirthRoute(userAnswers: UserAnswers): Call = userAnswers.get(SettlorDateOfBirthYesNoPage) match {
    case Some(false) => routes.SettlorsNINoYesNoController.onPageLoad(NormalMode)
    case Some(true) => routes.SettlorsDateOfBirthController.onPageLoad(NormalMode)
    case _ => routes.SessionExpiredController.onPageLoad()
  }

  private def deceasedSettlorDateOfDeathRoute(userAnswers: UserAnswers) : Call = userAnswers.get(SettlorDateOfDeathYesNoPage) match {
    case Some(false) => routes.SettlorDateOfBirthYesNoController.onPageLoad(NormalMode)
    case Some(true) => routes.SettlorDateOfDeathController.onPageLoad(NormalMode)
    case _ => routes.SessionExpiredController.onPageLoad()
  }

  private def addAssetsRoute(answers: UserAnswers) = {
    val addAnother = answers.get(AddAssetsPage)

    def routeToAssetIndex = {
      val assets = answers.get(viewmodels.Assets).getOrElse(List.empty)
      assets match {
        case Nil =>
          routes.WhatKindOfAssetController.onPageLoad(NormalMode, 0)
        case t if t.nonEmpty =>
          routes.WhatKindOfAssetController.onPageLoad(NormalMode, t.size)
      }
    }

    addAnother match {
      case Some(models.AddAssets.YesNow) =>
        routeToAssetIndex
      case Some(models.AddAssets.YesLater) =>
        routes.TaskListController.onPageLoad()
      case Some(models.AddAssets.NoComplete) =>
        routes.TaskListController.onPageLoad()
      case _ => routes.SessionExpiredController.onPageLoad()
    }
  }

  private def assetMoneyValueRoute(answers: UserAnswers, index: Int) = {
    val assets = answers.get(viewmodels.Assets).getOrElse(List.empty)
    assets match  {
      case Nil => routes.WhatKindOfAssetController.onPageLoad(NormalMode, 0)
      case _ => routes.AddAssetsController.onPageLoad()
    }
  }

  private def whatKindOfAssetRoute(answers: UserAnswers, index: Int) = answers.get(WhatKindOfAssetPage(index)) match {
      case Some(Money) => routes.AssetMoneyValueController.onPageLoad(NormalMode, index)
      case Some(PropertyOrLand) => routes.WhatKindOfAssetController.onPageLoad(NormalMode, index)
      case Some(Shares) => routes.WhatKindOfAssetController.onPageLoad(NormalMode, index)
      case Some(Business) => routes.WhatKindOfAssetController.onPageLoad(NormalMode, index)
      case Some(Partnership) => routes.WhatKindOfAssetController.onPageLoad(NormalMode, index)
      case Some(Other) => routes.WhatKindOfAssetController.onPageLoad(NormalMode, index)
      case _ => routes.SessionExpiredController.onPageLoad()
    }

  private def addATrusteeRoute(answers: UserAnswers) = {
    val addAnother = answers.get(AddATrusteePage)

    def routeToTrusteeIndex = {
      val trustees = answers.get(viewmodels.Trustees).getOrElse(List.empty)
      trustees match {
        case Nil =>
          routes.IsThisLeadTrusteeController.onPageLoad(NormalMode, 0)
        case t if t.nonEmpty =>
          routes.IsThisLeadTrusteeController.onPageLoad(NormalMode, t.size)
      }
    }

    addAnother match {
      case Some(YesNow) =>
        routeToTrusteeIndex
      case Some(YesLater) =>
        routes.TaskListController.onPageLoad()
      case Some(NoComplete) =>
        routes.TaskListController.onPageLoad()
      case _ => routes.SessionExpiredController.onPageLoad()
    }
  }

  private def addABeneficiaryRoute(answers: UserAnswers) = {
    val addAnother = answers.get(AddABeneficiaryPage)
    addAnother match {
      case Some(AddABeneficiary.YesNow) =>
        routes.WhatTypeOfBeneficiaryController.onPageLoad()
      case Some(AddABeneficiary.YesLater) =>
        routes.TaskListController.onPageLoad()
      case Some(AddABeneficiary.NoComplete) =>
        routes.TaskListController.onPageLoad()
      case _ => routes.SessionExpiredController.onPageLoad()
    }
  }


  private def trustHaveAUTRRoute(answers: UserAnswers, af: AffinityGroup) = {
    val condition = (answers.get(TrustRegisteredOnlinePage), answers.get(TrustHaveAUTRPage))

    condition match {
      case (Some(false), Some(true)) => routes.WhatIsTheUTRController.onPageLoad(NormalMode)
      case (Some(false), Some(false)) =>

        if(af == AffinityGroup.Organisation){
          routes.TaskListController.onPageLoad()
        } else {
          routes.AgentInternalReferenceController.onPageLoad(NormalMode)
        }

      case (Some(true), Some(false)) => routes.UTRSentByPostController.onPageLoad()
      case (Some(true), Some(true)) => routes.CannotMakeChangesController.onPageLoad()
      case _ => routes.SessionExpiredController.onPageLoad()
    }
  }

  private def trustNameRoute(answers: UserAnswers) = {
    val hasUTR = answers.get(TrustHaveAUTRPage).contains(true)

    if (hasUTR) {
      routes.PostcodeForTheTrustController.onPageLoad(NormalMode)
    } else {
      routes.WhenTrustSetupController.onPageLoad(NormalMode)
    }
  }

  private def isTrustGovernedInsideUKRoute(answers: UserAnswers) = answers.get(GovernedInsideTheUKPage) match {
    case Some(true)  => routes.AdministrationInsideUKController.onPageLoad(NormalMode)
    case Some(false) => routes.CountryGoverningTrustController.onPageLoad(NormalMode)
    case None        => routes.SessionExpiredController.onPageLoad()
  }

  private def isTrustGeneralAdministrationRoute(answers: UserAnswers) = answers.get(AdministrationInsideUKPage) match {
    case Some(true)  => routes.TrustResidentInUKController.onPageLoad(NormalMode)
    case Some(false) => routes.CountryAdministeringTrustController.onPageLoad(NormalMode)
    case None        => routes.SessionExpiredController.onPageLoad()
  }

  private def isTrustResidentInUKRoute(answers: UserAnswers) = answers.get(TrustResidentInUKPage) match {
    case Some(true)   => routes.EstablishedUnderScotsLawController.onPageLoad(NormalMode)
    case Some(false)  => routes.RegisteringTrustFor5AController.onPageLoad(NormalMode)
    case None         => routes.SessionExpiredController.onPageLoad()
  }

  private def wasTrustPreviouslyResidentOffshoreRoute(answers: UserAnswers) = answers.get(TrustResidentOffshorePage) match {
    case Some(true)   => routes.TrustPreviouslyResidentController.onPageLoad(NormalMode)
    case Some(false)  => routes.TrustDetailsAnswerPageController.onPageLoad()
    case None         => routes.SessionExpiredController.onPageLoad()
  }

  private def registeringForPurposeOfSchedule5ARoute(answers: UserAnswers) = answers.get(RegisteringTrustFor5APage) match {
    case Some(true)   => routes.NonResidentTypeController.onPageLoad(NormalMode)
    case Some(false)  => routes.InheritanceTaxActController.onPageLoad(NormalMode)
    case None         => routes.SessionExpiredController.onPageLoad()
  }

  private def inheritanceTaxRoute(answers: UserAnswers) = answers.get(InheritanceTaxActPage) match {
    case Some(true)   => routes.AgentOtherThanBarristerController.onPageLoad(NormalMode)
    case Some(false)  => routes.TrustDetailsAnswerPageController.onPageLoad()
    case None         => routes.SessionExpiredController.onPageLoad()
  }

  private def trusteeAUKCitizenRoute(answers: UserAnswers, index: Int) = answers.get(TrusteeAUKCitizenPage(index)) match {
    case Some(true)   => routes.TrusteesNinoController.onPageLoad(NormalMode,index)
    case Some(false)  => routes.TrusteeAUKCitizenController.onPageLoad(NormalMode,index)
    case None         => routes.SessionExpiredController.onPageLoad()
  }

  private def trusteeLiveInTheUKRoute(answers: UserAnswers, index: Int) = answers.get(TrusteeLiveInTheUKPage(index)) match {
    case Some(true)   => routes.TrusteesUkAddressController.onPageLoad(NormalMode,index)
    case Some(false)  => routes.TrusteeLiveInTheUKController.onPageLoad(NormalMode,index)
    case None         => routes.SessionExpiredController.onPageLoad()
  }

  private def trusteeDateOfBirthRoute(answers: UserAnswers, index : Int) = answers.get(IsThisLeadTrusteePage(index)) match {
    case Some(true) => routes.TrusteeAUKCitizenController.onPageLoad(NormalMode, index)
    case Some(false) => routes.TrusteesAnswerPageController.onPageLoad(index)
    case None => routes.SessionExpiredController.onPageLoad()
  }

  private def trusteeIndividualOrBusinessRoute(answers: UserAnswers, index : Int) = answers.get(TrusteeIndividualOrBusinessPage(index)) match {
    case Some(Individual) => routes.TrusteesNameController.onPageLoad(NormalMode, index)
    case Some(IndividualOrBusiness.Business) => routes.TrusteeIndividualOrBusinessController.onPageLoad(NormalMode,index)
    case None => routes.SessionExpiredController.onPageLoad()
  }


  private val checkRouteMap: Page => UserAnswers => Call = {
    // TrustDetails
    case TrustNamePage => _ => routes.TrustDetailsAnswerPageController.onPageLoad()
    case WhenTrustSetupPage => _ => routes.TrustDetailsAnswerPageController.onPageLoad()
    case _ => _ => routes.CheckYourAnswersController.onPageLoad()
  }

  def nextPage(page: Page, mode: Mode, af :AffinityGroup = AffinityGroup.Organisation): UserAnswers => Call = mode match {
    case NormalMode =>
      normalRoutes(page)(af)
    case CheckMode =>
      checkRouteMap(page)
  }

}
