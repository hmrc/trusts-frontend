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
import pages._
import models._

@Singleton
class Navigator @Inject()() {

  private val normalRoutes: Page => UserAnswers => Call = {
    //  Matching
    case TrustRegisteredOnlinePage => _ => routes.TrustHaveAUTRController.onPageLoad(NormalMode)
    case TrustHaveAUTRPage => trustHaveAUTRRoute
    case WhatIsTheUTRPage => _ => routes.TrustNameController.onPageLoad(NormalMode)
    case PostcodeForTheTrustPage => _ => routes.FailedMatchController.onPageLoad()
    //  Trust Details
    case TrustNamePage => trustNameRoute
    case WhenTrustSetupPage => _ => routes.GovernedInsideTheUKController.onPageLoad(NormalMode)
    case GovernedInsideTheUKPage => isTrustGovernedInsideUKRoute
    case CountryGoverningTrustPage => _ => routes.AdministrationInsideUKController.onPageLoad(NormalMode)
    case AdministrationInsideUKPage => isTrustGeneralAdministrationRoute
    case CountryAdministeringTrustPage => _ => routes.TrustResidentInUKController.onPageLoad(NormalMode)
    case TrustResidentInUKPage => isTrustResidentInUKRoute
    case EstablishedUnderScotsLawPage => _ => routes.TrustResidentOffshoreController.onPageLoad(NormalMode)
    case TrustResidentOffshorePage => wasTrustPreviouslyResidentOffshoreRoute
    case TrustPreviouslyResidentPage => _ => routes.CheckYourAnswersController.onPageLoad()
    case RegisteringTrustFor5APage => registeringForPurposeOfSchedule5ARoute
    case NonResidentTypePage => _ => routes.CheckYourAnswersController.onPageLoad()
    case InheritanceTaxActPage => inheritanceTaxRoute
    case AgentOtherThanBarristerPage => _ => routes.CheckYourAnswersController.onPageLoad()
    //  Trustees
    case IsThisLeadTrusteePage(index) => _ => routes.TrusteeIndividualOrBusinessController.onPageLoad(NormalMode, index)
    case TrusteeIndividualOrBusinessPage(index) => _ => routes.TrusteesNameController.onPageLoad(NormalMode, index)
    case TrusteesNamePage(index) => _ => routes.TrusteesAnswerPageController.onPageLoad(index)
    case TrusteesAnswerPage => _ => routes.AddATrusteeController.onPageLoad()
    case AddATrusteePage => addATrusteeRoute
    //  Default
    case _ => _ => routes.IndexController.onPageLoad()
  }

  private def addATrusteeRoute(answers: UserAnswers) = {
    val addAnother = answers.get(AddATrusteePage)

    def routeToTrusteeIndex = {
      val trustees = answers.get(Trustees).getOrElse(List.empty)
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
        routes.AddATrusteeController.onPageLoad()
      case Some(NoComplete) =>
        routes.AddATrusteeController.onPageLoad()
      case _ => routes.SessionExpiredController.onPageLoad()
    }
  }

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

  private val checkRouteMap: Page => UserAnswers => Call = {
    case _ => _ => routes.CheckYourAnswersController.onPageLoad()
  }

  def nextPage(page: Page, mode: Mode): UserAnswers => Call = mode match {
    case NormalMode =>
      normalRoutes(page)
    case CheckMode =>
      checkRouteMap(page)
  }
}
