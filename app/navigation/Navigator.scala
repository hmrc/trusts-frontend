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
import pages._
import models._



trait TrusteeNavigator {
  protected val trusteeDetails: Map[Page, UserAnswers => Call] = Map(
    IsThisLeadTrusteePage -> (_ => routes.TrusteeOrIndividualController.onPageLoad(NormalMode)),
    TrusteeOrIndividualPage -> (_ => routes.TrusteesNameController.onPageLoad(NormalMode)),
    TrusteesNamePage -> (_ => routes.TrusteesAnswerPageController.onPageLoad()),
    TrusteesAnswerPage -> (_ => routes.AddATrusteeController.onPageLoad())
  )
}

trait MatchingNavigator {

  protected val matchingDetails: Map[Page, UserAnswers => Call] = Map(
    TrustRegisteredOnlinePage -> (_ => routes.TrustHaveAUTRController.onPageLoad(NormalMode)),
    TrustHaveAUTRPage -> trustHaveAUTRRoute,
    WhatIsTheUTRPage -> (_ => routes.TrustNameController.onPageLoad(NormalMode)),
    PostcodeForTheTrustPage -> (_ => routes.FailedMatchController.onPageLoad())
  )

  private def trustHaveAUTRRoute(answers: UserAnswers) = {
    val condition = (answers.get(TrustRegisteredOnlinePage), answers.get(TrustHaveAUTRPage))

    condition match {
      case (Some(false), Some(true)) => routes.WhatIsTheUTRController.onPageLoad(NormalMode)
      case (Some(false), Some(false)) => routes.TrustNameController.onPageLoad(NormalMode)
      case (Some(true), Some(false)) => routes.UTRSentByPostController.onPageLoad()
      case (Some(true), Some(true)) => routes.CannotMakeChangesController.onPageLoad()
      case _ => routes.SessionExpiredController.onPageLoad()
    }
  }

}

@Singleton
class Navigator @Inject()() extends MatchingNavigator with TrusteeNavigator {

  private val trustDetails: Map[Page, UserAnswers => Call] = Map(
    TrustNamePage -> trustNameRoute,
    WhenTrustSetupPage -> (_ => routes.GovernedInsideTheUKController.onPageLoad(NormalMode)),
    GovernedInsideTheUKPage -> isTrustGovernedInsideUKRoute,
    CountryGoverningTrustPage -> (_ => routes.AdministrationInsideUKController.onPageLoad(NormalMode)),
    AdministrationInsideUKPage -> isTrustGeneralAdministrationRoute,
    CountryAdministeringTrustPage -> (_ => routes.TrustResidentInUKController.onPageLoad(NormalMode)),
    TrustResidentInUKPage -> isTrustResidentInUKRoute,
    EstablishedUnderScotsLawPage -> (_ => routes.TrustResidentOffshoreController.onPageLoad(NormalMode)),
    TrustResidentOffshorePage -> wasTrustPreviouslyResidentOffshoreRoute,
    TrustPreviouslyResidentPage -> (_ => routes.CheckYourAnswersController.onPageLoad()),
    RegisteringTrustFor5APage -> registeringForPurposeOfSchedule5ARoute,
    NonResidentTypePage -> (_ => routes.CheckYourAnswersController.onPageLoad()),
    InheritanceTaxActPage -> inheritanceTaxRoute,
    AgentOtherThanBarristerPage -> (_ => routes.CheckYourAnswersController.onPageLoad())
  )

  private def trustNameRoute(answers: UserAnswers) = {
    val hasUTR = answers.get(TrustHaveAUTRPage).getOrElse(false)

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
    case Some(false)  => routes.CheckYourAnswersController.onPageLoad()
    case None         => routes.SessionExpiredController.onPageLoad()
  }

  private def registeringForPurposeOfSchedule5ARoute(answers: UserAnswers) = answers.get(RegisteringTrustFor5APage) match {
    case Some(true)   => routes.NonResidentTypeController.onPageLoad(NormalMode)
    case Some(false)  => routes.InheritanceTaxActController.onPageLoad(NormalMode)
    case None         => routes.SessionExpiredController.onPageLoad()
  }

  private def inheritanceTaxRoute(answers: UserAnswers) = answers.get(InheritanceTaxActPage) match {
    case Some(true)   => routes.AgentOtherThanBarristerController.onPageLoad(NormalMode)
    case Some(false)  => routes.CheckYourAnswersController.onPageLoad()
    case None         => routes.SessionExpiredController.onPageLoad()
  }

  private val checkRouteMap: Map[Page, UserAnswers => Call] = Map(

  )

  def nextPage(page: Page, mode: Mode): UserAnswers => Call = mode match {
    case NormalMode =>

      val routeMap = trustDetails ++ matchingDetails ++ trusteeDetails

      routeMap.getOrElse(page, _ => routes.IndexController.onPageLoad())
    case CheckMode =>
      checkRouteMap.getOrElse(page, _ => routes.CheckYourAnswersController.onPageLoad())
  }
}
