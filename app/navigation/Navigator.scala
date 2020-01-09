/*
 * Copyright 2020 HM Revenue & Customs
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

import config.FrontendAppConfig
import controllers.register.routes
import javax.inject.{Inject, Singleton}
import models._
import models.core.UserAnswers
import models.core.pages.IndividualOrBusiness
import models.registration.pages.TrusteesBasedInTheUK._
import models.registration.pages.WhatKindOfAsset._
import models.registration.pages._
import pages._
import pages.register._
import pages.register.agents._
import pages.register.asset.money.AssetMoneyValuePage
import pages.register.asset._
import pages.register.settlors.deceased_settlor._
import pages.register.asset.shares._
import pages.register.beneficiaries.individual._
import pages.register.beneficiaries._
import pages.register.settlors.SettlorsBasedInTheUKPage
import pages.register.trustees._
import play.api.mvc.Call
import sections._
import sections.beneficiaries.{ClassOfBeneficiaries, IndividualBeneficiaries}
import uk.gov.hmrc.auth.core.AffinityGroup

@Singleton
class Navigator @Inject()(
                         config: FrontendAppConfig
                         ) {

  protected def normalRoutes(draftId: String): Page => AffinityGroup => UserAnswers => Call = {
    //  Matching
    case TrustRegisteredOnlinePage => _ => _ => routes.TrustHaveAUTRController.onPageLoad(NormalMode, draftId)
    case TrustHaveAUTRPage => af => userAnswers => trustHaveAUTRRoute(userAnswers, af, draftId)
    case WhatIsTheUTRPage => _ => _ => routes.TrustNameController.onPageLoad(NormalMode, draftId)
    case PostcodeForTheTrustPage => _ => _ => routes.FailedMatchController.onPageLoad(draftId)

    //  Trust Details
    case TrustNamePage => _ => trustNameRoute(draftId)
    case WhenTrustSetupPage => _ => _ => routes.GovernedInsideTheUKController.onPageLoad(NormalMode, draftId)
    case GovernedInsideTheUKPage => _ => isTrustGovernedInsideUKRoute(draftId)
    case CountryGoverningTrustPage => _ => _ => routes.AdministrationInsideUKController.onPageLoad(NormalMode, draftId)
    case AdministrationInsideUKPage => _ => isTrustGeneralAdministrationRoute(draftId)
    case CountryAdministeringTrustPage => _ => _ => controllers.register.trustees.routes.TrusteesBasedInTheUKController.onPageLoad(NormalMode, draftId)
    case TrusteesBasedInTheUKPage => _ => isTrusteesBasedInTheUKPage(draftId)

    case SettlorsBasedInTheUKPage => _ => isSettlorsBasedInTheUKPage(draftId)
    case EstablishedUnderScotsLawPage => _ => _ => routes.TrustResidentOffshoreController.onPageLoad(NormalMode, draftId)
    case TrustResidentOffshorePage => _ => wasTrustPreviouslyResidentOffshoreRoute(draftId)
    case TrustPreviouslyResidentPage => _ => _ => routes.TrustDetailsAnswerPageController.onPageLoad(draftId)
    case RegisteringTrustFor5APage => _ => registeringForPurposeOfSchedule5ARoute(draftId)
    case NonResidentTypePage => _ => _ => routes.TrustDetailsAnswerPageController.onPageLoad(draftId)
    case InheritanceTaxActPage => _ => inheritanceTaxRoute(draftId)
    case AgentOtherThanBarristerPage => _ => _ => routes.TrustDetailsAnswerPageController.onPageLoad(draftId)
    case TrustDetailsAnswerPage => _ => _ => routes.TaskListController.onPageLoad(draftId)

    //  Trustees
    case IsThisLeadTrusteePage(index) => _ =>_ => controllers.register.trustees.routes.TrusteeIndividualOrBusinessController.onPageLoad(NormalMode, index, draftId)
    case TrusteeIndividualOrBusinessPage(index)  => _ => ua => trusteeIndividualOrBusinessRoute(ua, index, draftId)

    case TrusteesNamePage(index) => _ => _ => controllers.register.trustees.routes.TrusteesDateOfBirthController.onPageLoad(NormalMode, index, draftId)
    case TrusteesDateOfBirthPage(index) => _ => ua => trusteeDateOfBirthRoute(ua, index, draftId)
    case TrusteeAUKCitizenPage(index) => _ => ua => trusteeAUKCitizenRoute(ua, index, draftId)
    case TrusteesNinoPage(index) => _ => _ => controllers.register.trustees.routes.TrusteeLiveInTheUKController.onPageLoad(NormalMode, index, draftId)
    case TrusteeLiveInTheUKPage(index)  => _ => ua => trusteeLiveInTheUKRoute(ua, index, draftId)
    case TrusteesUkAddressPage(index) => _ => _ => controllers.register.trustees.routes.TelephoneNumberController.onPageLoad(NormalMode, index, draftId)
    case TelephoneNumberPage(index) => _ => _ => controllers.register.trustees.routes.TrusteesAnswerPageController.onPageLoad(index, draftId)
    case TrusteesAnswerPage => _ => _ => controllers.register.trustees.routes.AddATrusteeController.onPageLoad(draftId)
    case AddATrusteePage => _ => addATrusteeRoute(draftId)
    case AddATrusteeYesNoPage => _ => addATrusteeYesNoRoute(draftId)

    //Agents
    case AgentInternalReferencePage => _ => _ => controllers.register.agents.routes.AgentNameController.onPageLoad(NormalMode, draftId)
    case AgentNamePage => _ => _ => controllers.register.agents.routes.AgentAddressYesNoController.onPageLoad(NormalMode, draftId)
    case AgentAddressYesNoPage => _ => ua => agentAddressYesNoRoute(ua, draftId)
    case AgentUKAddressPage => _ => _ => controllers.register.agents.routes.AgentTelephoneNumberController.onPageLoad(NormalMode, draftId)
    case AgentInternationalAddressPage => _ => _ => controllers.register.agents.routes.AgentTelephoneNumberController.onPageLoad(NormalMode, draftId)
    case AgentTelephoneNumberPage => _ => _ => controllers.register.agents.routes.AgentAnswerController.onPageLoad(draftId)
    case AgentAnswerPage => _ => _ => routes.TaskListController.onPageLoad(draftId)

    //Assets
    case AssetMoneyValuePage(index) => _ => ua => assetMoneyValueRoute(ua, index, draftId)
    case WhatKindOfAssetPage(index) => _ => ua => whatKindOfAssetRoute(ua, index, draftId)
    case SharesInAPortfolioPage(index) => _ => ua => sharesInAPortfolio(ua, index, draftId)
    case SharePortfolioNamePage(index) => _ => ua => controllers.register.asset.shares.routes.SharePortfolioOnStockExchangeController.onPageLoad(NormalMode, index, draftId)
    case SharePortfolioOnStockExchangePage(index) => _ => ua => controllers.register.asset.shares.routes.SharePortfolioQuantityInTrustController.onPageLoad(NormalMode, index, draftId)
    case SharePortfolioQuantityInTrustPage(index) => _ => _ => controllers.register.asset.shares.routes.SharePortfolioValueInTrustController.onPageLoad(NormalMode, index, draftId)
    case SharePortfolioValueInTrustPage(index) => _ => _ => controllers.register.asset.shares.routes.ShareAnswerController.onPageLoad(index, draftId)
    case SharesOnStockExchangePage(index) => _ => _ => controllers.register.asset.shares.routes.ShareClassController.onPageLoad(NormalMode, index, draftId)
    case ShareClassPage(index) => _ => _ => controllers.register.asset.shares.routes.ShareQuantityInTrustController.onPageLoad(NormalMode, index, draftId)
    case AddAssetsPage => _ => addAssetsRoute(draftId)
    case AddAnAssetYesNoPage => _ => addAnAssetYesNoRoute(draftId)
    case ShareQuantityInTrustPage(index) => _ => _ => controllers.register.asset.shares.routes.ShareValueInTrustController.onPageLoad(NormalMode, index, draftId)
    case ShareValueInTrustPage(index) => _ => _ => controllers.register.asset.shares.routes.ShareAnswerController.onPageLoad(index, draftId)
    case ShareAnswerPage => _ => _ => controllers.register.asset.routes.AddAssetsController.onPageLoad(draftId)
    case ShareCompanyNamePage(index) => _ => _ => controllers.register.asset.shares.routes.SharesOnStockExchangeController.onPageLoad(NormalMode, index, draftId)

    // Deceased Settlor
    case SetupAfterSettlorDiedYesNoPage => _ => setupAfterSettlorDiedRoute(draftId)
    case SettlorsNamePage => _ => _ => controllers.register.settlors.deceased_settlor.routes.SettlorDateOfDeathYesNoController.onPageLoad(NormalMode, draftId)
    case SettlorDateOfDeathYesNoPage => _ => deceasedSettlorDateOfDeathRoute(draftId)
    case SettlorDateOfBirthYesNoPage => _ => deceasedSettlorDateOfBirthRoute(draftId)
    case SettlorsDateOfBirthPage => _ => _ => controllers.register.settlors.deceased_settlor.routes.SettlorsNINoYesNoController.onPageLoad(NormalMode, draftId)
    case SettlorsNINoYesNoPage => _ => deceasedSettlorNinoRoute(draftId)
    case SettlorsLastKnownAddressYesNoPage => _ => deceasedSettlorLastKnownAddressRoute(draftId)
    case SettlorDateOfDeathPage => _ => _ => controllers.register.settlors.deceased_settlor.routes.SettlorDateOfBirthYesNoController.onPageLoad(NormalMode, draftId)
    case SettlorNationalInsuranceNumberPage => _ => _ => controllers.register.settlors.deceased_settlor.routes.DeceasedSettlorAnswerController.onPageLoad(draftId)
    case WasSettlorsAddressUKYesNoPage => _ => deceasedSettlorAddressRoute(draftId)
    case SettlorsInternationalAddressPage => _ => _ => controllers.register.settlors.deceased_settlor.routes.DeceasedSettlorAnswerController.onPageLoad(draftId)
    case SettlorsUKAddressPage => _ => _ => controllers.register.settlors.deceased_settlor.routes.DeceasedSettlorAnswerController.onPageLoad(draftId)
    case DeceasedSettlorAnswerPage => _ => _ => routes.TaskListController.onPageLoad(draftId)

     //Beneficiary
    case IndividualBeneficiaryNamePage(index) => _ => _ => controllers.register.beneficiaries.routes.IndividualBeneficiaryDateOfBirthYesNoController.onPageLoad(NormalMode, index, draftId)
    case IndividualBeneficiaryDateOfBirthYesNoPage(index) => _ => ua => individualBeneficiaryDateOfBirthRoute(ua, index, draftId)
    case IndividualBeneficiaryDateOfBirthPage(index) => _ => _ => controllers.register.beneficiaries.routes.IndividualBeneficiaryIncomeYesNoController.onPageLoad(NormalMode, index, draftId)
    case IndividualBeneficiaryIncomeYesNoPage(index) => _ => ua => individualBeneficiaryIncomeRoute(ua, index, draftId)
    case IndividualBeneficiaryIncomePage(index) => _ => _ => controllers.register.beneficiaries.routes.IndividualBeneficiaryNationalInsuranceYesNoController.onPageLoad(NormalMode, index, draftId)
    case IndividualBeneficiaryNationalInsuranceYesNoPage(index) => _ => ua => individualBeneficiaryNationalInsuranceYesNoRoute(ua, index, draftId)
    case IndividualBeneficiaryNationalInsuranceNumberPage(index) => _ => _ =>
      controllers.register.beneficiaries.routes.IndividualBeneficiaryVulnerableYesNoController.onPageLoad(NormalMode, index, draftId)
    case IndividualBeneficiaryAddressYesNoPage(index) => _ => ua => individualBeneficiaryAddressRoute(ua, index, draftId)
    case IndividualBeneficiaryAddressUKYesNoPage(index) => _ => ua => individualBeneficiaryAddressUKYesNoRoute(ua, index, draftId)
    case IndividualBeneficiaryAddressUKPage(index) => _ => _ => controllers.register.beneficiaries.routes.IndividualBeneficiaryVulnerableYesNoController.onPageLoad(NormalMode, index, draftId)
    case IndividualBeneficiaryVulnerableYesNoPage(index) => _ => _ => controllers.register.beneficiaries.routes.IndividualBeneficiaryAnswersController.onPageLoad(index, draftId)
    case IndividualBeneficiaryAnswersPage => _ => _ => controllers.register.beneficiaries.routes.AddABeneficiaryController.onPageLoad(draftId)

    case AddABeneficiaryPage => _ => addABeneficiaryRoute(draftId)
    case AddABeneficiaryYesNoPage => _ => addABeneficiaryYesNoRoute(draftId)
    case WhatTypeOfBeneficiaryPage => _ => whatTypeOfBeneficiaryRoute(draftId)
    case ClassBeneficiaryDescriptionPage(index) => _ => _ => controllers.register.beneficiaries.routes.AddABeneficiaryController.onPageLoad(draftId)


    //  Default
    case _ => _ => _ => routes.IndexController.onPageLoad()
  }

  private def sharesInAPortfolio(userAnswers: UserAnswers, index : Int, draftId: String) : Call = {
    userAnswers.get(SharesInAPortfolioPage(index)) match {
      case Some(true) =>
        controllers.register.asset.shares.routes.SharePortfolioNameController.onPageLoad(NormalMode, index, draftId)
      case Some(false) =>
        controllers.register.asset.shares.routes.ShareCompanyNameController.onPageLoad(NormalMode, index, draftId)
      case _=>
        routes.SessionExpiredController.onPageLoad()
    }
  }

  private def whatTypeOfBeneficiaryRoute(draftId: String)(userAnswers: UserAnswers) : Call = {
    val whatBeneficiaryToAdd = userAnswers.get(WhatTypeOfBeneficiaryPage)
    whatBeneficiaryToAdd match {
      case Some(WhatTypeOfBeneficiary.Individual) =>
        routeToIndividualBeneficiaryIndex(userAnswers, draftId)
      case Some(WhatTypeOfBeneficiary.ClassOfBeneficiary) =>
        routeToClassOfBeneficiaryIndex(userAnswers, draftId)
      case _ => routes.SessionExpiredController.onPageLoad()
    }
  }

  private def routeToIndividualBeneficiaryIndex(userAnswers: UserAnswers, draftId: String) = {
    val indBeneficiaries = userAnswers.get(IndividualBeneficiaries).getOrElse(List.empty)
    indBeneficiaries match {
      case Nil =>
        controllers.register.beneficiaries.routes.IndividualBeneficiaryNameController.onPageLoad(NormalMode, 0, draftId)
      case t if t.nonEmpty =>
        controllers.register.beneficiaries.routes.IndividualBeneficiaryNameController.onPageLoad(NormalMode, t.size, draftId)
    }
  }

  private def routeToClassOfBeneficiaryIndex(userAnswers: UserAnswers, draftId: String) = {
    val classOfBeneficiaries = userAnswers.get(ClassOfBeneficiaries).getOrElse(List.empty)
    classOfBeneficiaries match {
      case Nil =>
        controllers.register.beneficiaries.routes.ClassBeneficiaryDescriptionController.onPageLoad(NormalMode, 0, draftId)
      case t if t.nonEmpty =>
        controllers.register.beneficiaries.routes.ClassBeneficiaryDescriptionController.onPageLoad(NormalMode, t.size, draftId)
    }
  }

  private def agentAddressYesNoRoute(userAnswers: UserAnswers, draftId: String) : Call =
    userAnswers.get(AgentAddressYesNoPage) match {
      case Some(false) => controllers.register.agents.routes.AgentInternationalAddressController.onPageLoad(NormalMode, draftId)
      case Some(true) => controllers.register.agents.routes.AgentUKAddressController.onPageLoad(NormalMode, draftId)
      case _ => routes.SessionExpiredController.onPageLoad()
    }

  private def individualBeneficiaryAddressRoute(userAnswers: UserAnswers, index: Int, draftId: String) : Call =
    userAnswers.get(IndividualBeneficiaryAddressYesNoPage(index)) match {
      case Some(false) => controllers.register.beneficiaries.routes.IndividualBeneficiaryVulnerableYesNoController.onPageLoad(NormalMode, index, draftId)
      case Some(true) => controllers.register.beneficiaries.routes.IndividualBeneficiaryAddressUKYesNoController.onPageLoad(NormalMode, index, draftId)
      case _ => routes.SessionExpiredController.onPageLoad()
    }

  private def individualBeneficiaryAddressUKYesNoRoute(userAnswers: UserAnswers, index: Int, draftId: String) : Call =
    userAnswers.get(IndividualBeneficiaryAddressUKYesNoPage(index)) match {
      case Some(false) => controllers.register.beneficiaries.routes.IndividualBeneficiaryAddressUKYesNoController.onPageLoad(NormalMode, index, draftId)
      case Some(true) => controllers.register.beneficiaries.routes.IndividualBeneficiaryAddressUKController.onPageLoad(NormalMode, index, draftId)
      case _ => routes.SessionExpiredController.onPageLoad()
    }

  private def individualBeneficiaryNationalInsuranceYesNoRoute(userAnswers: UserAnswers, index: Int, draftId: String) : Call =
    userAnswers.get(IndividualBeneficiaryNationalInsuranceYesNoPage(index)) match {
      case Some(false) => controllers.register.beneficiaries.routes.IndividualBeneficiaryAddressYesNoController.onPageLoad(NormalMode, index, draftId)
      case Some(true) => controllers.register.beneficiaries.routes.IndividualBeneficiaryNationalInsuranceNumberController.onPageLoad(NormalMode, index, draftId)
      case _ => routes.SessionExpiredController.onPageLoad()
  }

  private def individualBeneficiaryIncomeRoute(userAnswers: UserAnswers, index: Int, draftId: String) : Call =
    userAnswers.get(IndividualBeneficiaryIncomeYesNoPage(index)) match {
      case Some(false) => controllers.register.beneficiaries.routes.IndividualBeneficiaryIncomeController.onPageLoad(NormalMode, index, draftId)
      case Some(true) => controllers.register.beneficiaries.routes.IndividualBeneficiaryNationalInsuranceYesNoController.onPageLoad(NormalMode, index, draftId)
      case _ => routes.SessionExpiredController.onPageLoad()
  }

  private def individualBeneficiaryDateOfBirthRoute(userAnswers: UserAnswers, index: Int, draftId: String) : Call =
    userAnswers.get(IndividualBeneficiaryDateOfBirthYesNoPage(index)) match {
    case Some(false) => controllers.register.beneficiaries.routes.IndividualBeneficiaryIncomeYesNoController.onPageLoad(NormalMode, index, draftId)
    case Some(true) => controllers.register.beneficiaries.routes.IndividualBeneficiaryDateOfBirthController.onPageLoad(NormalMode, index, draftId)
    case _ => routes.SessionExpiredController.onPageLoad()
  }

  private def setupAfterSettlorDiedRoute(draftId: String)(userAnswers: UserAnswers) : Call = userAnswers.get(SetupAfterSettlorDiedYesNoPage) match {
    case Some(false) => controllers.register.settlors.living_settlor.routes.KindOfTrustController.onPageLoad(NormalMode, draftId)
    case Some(true) => controllers.register.settlors.deceased_settlor.routes.SettlorsNameController.onPageLoad(NormalMode, draftId)
    case _ => routes.SessionExpiredController.onPageLoad()
  }

  private def deceasedSettlorAddressRoute(draftId: String)(userAnswers: UserAnswers) : Call = userAnswers.get(WasSettlorsAddressUKYesNoPage) match {
    case Some(false) => controllers.register.settlors.deceased_settlor.routes.SettlorsInternationalAddressController.onPageLoad(NormalMode, draftId)
    case Some(true) => controllers.register.settlors.deceased_settlor.routes.SettlorsUKAddressController.onPageLoad(NormalMode, draftId)
    case _ => routes.SessionExpiredController.onPageLoad()
  }

  private def deceasedSettlorLastKnownAddressRoute(draftId: String)(userAnswers: UserAnswers) : Call = userAnswers.get(SettlorsLastKnownAddressYesNoPage) match {
    case Some(false) => controllers.register.settlors.deceased_settlor.routes.DeceasedSettlorAnswerController.onPageLoad(draftId)
    case Some(true) => controllers.register.settlors.deceased_settlor.routes.WasSettlorsAddressUKYesNoController.onPageLoad(NormalMode, draftId)
    case _ => routes.SessionExpiredController.onPageLoad()
  }

  private def deceasedSettlorNinoRoute(draftId: String)(userAnswers: UserAnswers) : Call = userAnswers.get(SettlorsNINoYesNoPage) match {
    case Some(false) => controllers.register.settlors.deceased_settlor.routes.SettlorsLastKnownAddressYesNoController.onPageLoad(NormalMode, draftId)
    case Some(true) => controllers.register.settlors.deceased_settlor.routes.SettlorNationalInsuranceNumberController.onPageLoad(NormalMode, draftId)
    case _ => routes.SessionExpiredController.onPageLoad()
  }

  private def deceasedSettlorDateOfBirthRoute(draftId: String)(userAnswers: UserAnswers): Call = userAnswers.get(SettlorDateOfBirthYesNoPage) match {
    case Some(false) => controllers.register.settlors.deceased_settlor.routes.SettlorsNINoYesNoController.onPageLoad(NormalMode, draftId)
    case Some(true) => controllers.register.settlors.deceased_settlor.routes.SettlorsDateOfBirthController.onPageLoad(NormalMode, draftId)
    case _ => routes.SessionExpiredController.onPageLoad()
  }

  private def deceasedSettlorDateOfDeathRoute(draftId: String)(userAnswers: UserAnswers) : Call = userAnswers.get(SettlorDateOfDeathYesNoPage) match {
    case Some(false) => controllers.register.settlors.deceased_settlor.routes.SettlorDateOfBirthYesNoController.onPageLoad(NormalMode, draftId)
    case Some(true) => controllers.register.settlors.deceased_settlor.routes.SettlorDateOfDeathController.onPageLoad(NormalMode, draftId)
    case _ => routes.SessionExpiredController.onPageLoad()
  }

  private def addAnAssetYesNoRoute(draftId: String)(userAnswers: UserAnswers) : Call = userAnswers.get(AddAnAssetYesNoPage) match {
    case Some(false) => routes.TaskListController.onPageLoad(draftId)
    case Some(true) => controllers.register.asset.routes.WhatKindOfAssetController.onPageLoad(NormalMode, 0, draftId)
    case _ => routes.SessionExpiredController.onPageLoad()
  }

  private def addAssetsRoute(draftId: String)(answers: UserAnswers) = {
    val addAnother = answers.get(AddAssetsPage)

    def routeToAssetIndex = {
      val assets = answers.get(sections.Assets).getOrElse(List.empty)
      assets match {
        case Nil =>
          controllers.register.asset.routes.WhatKindOfAssetController.onPageLoad(NormalMode, 0, draftId)
        case t if t.nonEmpty =>
          controllers.register.asset.routes.WhatKindOfAssetController.onPageLoad(NormalMode, t.size, draftId)
      }
    }

    addAnother match {
      case Some(AddAssets.YesNow) =>
        routeToAssetIndex
      case Some(AddAssets.YesLater) =>
        routes.TaskListController.onPageLoad(draftId)
      case Some(AddAssets.NoComplete) =>
        routes.TaskListController.onPageLoad(draftId)
      case _ => routes.SessionExpiredController.onPageLoad()
    }
  }

  private def assetMoneyValueRoute(answers: UserAnswers, index: Int, draftId: String) = {
    val assets = answers.get(sections.Assets).getOrElse(List.empty)
    assets match  {
      case Nil => controllers.register.asset.routes.WhatKindOfAssetController.onPageLoad(NormalMode, 0, draftId)
      case _ => controllers.register.asset.routes.AddAssetsController.onPageLoad(draftId)
    }
  }

  private def whatKindOfAssetRoute(answers: UserAnswers, index: Int, draftId: String) = answers.get(WhatKindOfAssetPage(index)) match {
      case Some(Money) => controllers.register.asset.money.routes.AssetMoneyValueController.onPageLoad(NormalMode, index, draftId)
      case Some(Shares) => controllers.register.asset.shares.routes.SharesInAPortfolioController.onPageLoad(NormalMode, index, draftId)
      case Some(PropertyOrLand) => controllers.register.asset.property_or_land.routes.PropertyOrLandAddressYesNoController.onPageLoad(NormalMode, index, draftId)
      case Some(Business) => controllers.register.asset.routes.WhatKindOfAssetController.onPageLoad(NormalMode, index, draftId)
      case Some(Partnership) => controllers.register.asset.routes.WhatKindOfAssetController.onPageLoad(NormalMode, index, draftId)
      case Some(Other) => controllers.register.asset.routes.WhatKindOfAssetController.onPageLoad(NormalMode, index, draftId)
      case _ => routes.SessionExpiredController.onPageLoad()
    }

  private def addATrusteeYesNoRoute(draftId: String)(answers: UserAnswers) : Call = {
    answers.get(AddATrusteeYesNoPage) match {
      case Some(true) =>
        controllers.register.trustees.routes.IsThisLeadTrusteeController.onPageLoad(NormalMode, 0, draftId)
      case Some(false) =>
        routes.TaskListController.onPageLoad(draftId)
      case _ => routes.SessionExpiredController.onPageLoad()
    }
  }

  private def addATrusteeRoute(draftId: String)(answers: UserAnswers) = {
    val addAnother = answers.get(AddATrusteePage)

    def routeToTrusteeIndex = {
      val trustees = answers.get(Trustees).getOrElse(List.empty)
      trustees match {
        case Nil =>
          controllers.register.trustees.routes.IsThisLeadTrusteeController.onPageLoad(NormalMode, 0, draftId)
        case t if t.nonEmpty =>
          controllers.register.trustees.routes.IsThisLeadTrusteeController.onPageLoad(NormalMode, t.size, draftId)
      }
    }

    addAnother match {
      case Some(AddATrustee.YesNow) =>
        routeToTrusteeIndex
      case Some(AddATrustee.YesLater) =>
        routes.TaskListController.onPageLoad(draftId)
      case Some(AddATrustee.NoComplete) =>
        routes.TaskListController.onPageLoad(draftId)
      case _ => routes.SessionExpiredController.onPageLoad()
    }
  }

  private def addABeneficiaryYesNoRoute(draftId: String)(answers: UserAnswers) = {
    val add = answers.get(AddABeneficiaryYesNoPage)

    add match {
      case Some(true) =>
        controllers.register.beneficiaries.routes.WhatTypeOfBeneficiaryController.onPageLoad(draftId)
      case Some(false) =>
        routes.TaskListController.onPageLoad(draftId)
      case _ => routes.SessionExpiredController.onPageLoad()
    }
  }

  private def addABeneficiaryRoute(draftId: String)(answers: UserAnswers) = {
    val addAnother = answers.get(AddABeneficiaryPage)
    addAnother match {
      case Some(AddABeneficiary.YesNow) =>
        controllers.register.beneficiaries.routes.WhatTypeOfBeneficiaryController.onPageLoad(draftId)
      case Some(AddABeneficiary.YesLater) =>
        routes.TaskListController.onPageLoad(draftId)
      case Some(AddABeneficiary.NoComplete) =>
        routes.TaskListController.onPageLoad(draftId)
      case _ => routes.SessionExpiredController.onPageLoad()
    }
  }

  private def routeToMaintain = {
    if (config.claimEnabled) {
      controllers.playback.routes.WhatIsTheUTRVariationsController.onPageLoad()
    } else {
      controllers.register.routes.CannotMakeChangesController.onPageLoad()
    }
  }

  private def routeToRegistration(affinityGroup: AffinityGroup, draftId: String) = {
    if(affinityGroup == AffinityGroup.Organisation){
      routes.TaskListController.onPageLoad(draftId)
    } else {
      controllers.register.agents.routes.AgentInternalReferenceController.onPageLoad(NormalMode, draftId)
    }
  }

  private def trustHaveAUTRRoute(answers: UserAnswers, af: AffinityGroup, draftId: String) = {
    val condition = (answers.get(TrustRegisteredOnlinePage), answers.get(TrustHaveAUTRPage))

    condition match {
      case (Some(false), Some(true)) => routes.WhatIsTheUTRController.onPageLoad(NormalMode, draftId)
      case (Some(false), Some(false)) => routeToRegistration(af, draftId)
      case (Some(true), Some(false)) => routes.UTRSentByPostController.onPageLoad()
      case (Some(true), Some(true)) => routeToMaintain
      case _ => routes.SessionExpiredController.onPageLoad()
    }
  }

  private def trustNameRoute(draftId: String)(answers: UserAnswers) = {
    val hasUTR = answers.get(TrustHaveAUTRPage).contains(true)

    if (hasUTR) {
      routes.PostcodeForTheTrustController.onPageLoad(NormalMode, draftId)
    } else {
      routes.WhenTrustSetupController.onPageLoad(NormalMode, draftId)
    }
  }

  private def isTrustGovernedInsideUKRoute(draftId: String)(answers: UserAnswers) = answers.get(GovernedInsideTheUKPage) match {
    case Some(true)  => routes.AdministrationInsideUKController.onPageLoad(NormalMode, draftId)
    case Some(false) => routes.CountryGoverningTrustController.onPageLoad(NormalMode, draftId)
    case None        => routes.SessionExpiredController.onPageLoad()
  }

  private def isTrustGeneralAdministrationRoute(draftId: String)(answers: UserAnswers) = answers.get(AdministrationInsideUKPage) match {
    case Some(true)  => controllers.register.trustees.routes.TrusteesBasedInTheUKController.onPageLoad(NormalMode, draftId)
    case Some(false) => routes.CountryAdministeringTrustController.onPageLoad(NormalMode, draftId)
    case None        => routes.SessionExpiredController.onPageLoad()
  }

  private def isTrusteesBasedInTheUKPage(draftId: String)(answers: UserAnswers) = answers.get(TrusteesBasedInTheUKPage) match {
    case Some(UKBasedTrustees)   => routes.EstablishedUnderScotsLawController.onPageLoad(NormalMode, draftId)
    case Some(NonUkBasedTrustees)  => routes.RegisteringTrustFor5AController.onPageLoad(NormalMode, draftId)
    case Some(InternationalAndUKTrustees)  => controllers.register.settlors.routes.SettlorsBasedInTheUKController.onPageLoad(NormalMode, draftId)
    case None         => routes.SessionExpiredController.onPageLoad()
  }

  private def isSettlorsBasedInTheUKPage(draftId: String)(answers: UserAnswers) = answers.get(SettlorsBasedInTheUKPage) match {
    case Some(true)   => routes.EstablishedUnderScotsLawController.onPageLoad(NormalMode, draftId)
    case Some(false)  => routes.RegisteringTrustFor5AController.onPageLoad(NormalMode, draftId)
    case None         => routes.SessionExpiredController.onPageLoad()
  }

  private def wasTrustPreviouslyResidentOffshoreRoute(draftId: String)(answers: UserAnswers) = answers.get(TrustResidentOffshorePage) match {
    case Some(true)   => routes.TrustPreviouslyResidentController.onPageLoad(NormalMode, draftId)
    case Some(false)  => routes.TrustDetailsAnswerPageController.onPageLoad(draftId)
    case None         => routes.SessionExpiredController.onPageLoad()
  }

  private def registeringForPurposeOfSchedule5ARoute(draftId: String)(answers: UserAnswers) = answers.get(RegisteringTrustFor5APage) match {
    case Some(true)   => routes.NonResidentTypeController.onPageLoad(NormalMode, draftId)
    case Some(false)  => routes.InheritanceTaxActController.onPageLoad(NormalMode, draftId)
    case None         => routes.SessionExpiredController.onPageLoad()
  }

  private def inheritanceTaxRoute(draftId: String)(answers: UserAnswers) = answers.get(InheritanceTaxActPage) match {
    case Some(true)   => controllers.register.agents.routes.AgentOtherThanBarristerController.onPageLoad(NormalMode, draftId)
    case Some(false)  => routes.TrustDetailsAnswerPageController.onPageLoad(draftId)
    case None         => routes.SessionExpiredController.onPageLoad()
  }

  private def trusteeAUKCitizenRoute(answers: UserAnswers, index: Int, draftId: String) = answers.get(TrusteeAUKCitizenPage(index)) match {
    case Some(true)   => controllers.register.trustees.routes.TrusteesNinoController.onPageLoad(NormalMode,index, draftId)
    case Some(false)  => controllers.register.trustees.routes.TrusteeAUKCitizenController.onPageLoad(NormalMode,index, draftId)
    case None         => routes.SessionExpiredController.onPageLoad()
  }

  private def trusteeLiveInTheUKRoute(answers: UserAnswers, index: Int, draftId: String) = answers.get(TrusteeLiveInTheUKPage(index)) match {
    case Some(true)   => controllers.register.trustees.routes.TrusteesUkAddressController.onPageLoad(NormalMode,index, draftId)
    case Some(false)  => controllers.register.trustees.routes.TrusteeLiveInTheUKController.onPageLoad(NormalMode,index, draftId)
    case None         => routes.SessionExpiredController.onPageLoad()
  }

  private def trusteeDateOfBirthRoute(answers: UserAnswers, index : Int, draftId: String) = answers.get(IsThisLeadTrusteePage(index)) match {
    case Some(true) => controllers.register.trustees.routes.TrusteeAUKCitizenController.onPageLoad(NormalMode, index, draftId)
    case Some(false) => controllers.register.trustees.routes.TrusteesAnswerPageController.onPageLoad(index, draftId)
    case None => routes.SessionExpiredController.onPageLoad()
  }

  private def trusteeIndividualOrBusinessRoute(answers: UserAnswers, index : Int, draftId: String) = answers.get(TrusteeIndividualOrBusinessPage(index)) match {
    case Some(IndividualOrBusiness.Individual) => controllers.register.trustees.routes.TrusteesNameController.onPageLoad(NormalMode, index, draftId)
    case Some(IndividualOrBusiness.Business) => controllers.register.trustees.routes.TrusteeIndividualOrBusinessController.onPageLoad(NormalMode,index, draftId)
    case None => routes.SessionExpiredController.onPageLoad()
  }

  def nextPage(page: Page, mode: Mode, draftId: String,  af :AffinityGroup = AffinityGroup.Organisation): UserAnswers => Call = mode match {
    case NormalMode =>
      normalRoutes(draftId)(page)(af)
    case CheckMode =>
      normalRoutes(draftId)(page)(af)
  }

}
