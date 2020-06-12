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

package navigation.routes

import controllers.register.routes
import models.NormalMode
import models.core.UserAnswers
import models.registration.pages.TrusteesBasedInTheUK.{InternationalAndUKTrustees, NonUkBasedTrustees, UKBasedTrustees}
import pages.Page
import pages.register.trust_details._
import pages.register.{TrustDetailsAnswerPage, TrustHaveAUTRPage}
import play.api.mvc.Call
import uk.gov.hmrc.auth.core.AffinityGroup

object TrustDetailRoutes {
  def route(draftId: String): PartialFunction[Page, AffinityGroup => UserAnswers => Call] = {
    case TrustNamePage => _ => trustNameRoute(draftId)
    case WhenTrustSetupPage => _ => _ => controllers.register.trust_details.routes.GovernedInsideTheUKController.onPageLoad(NormalMode, draftId)
    case GovernedInsideTheUKPage => _ => isTrustGovernedInsideUKRoute(draftId)
    case CountryGoverningTrustPage => _ => _ => controllers.register.trust_details.routes.AdministrationInsideUKController.onPageLoad(NormalMode, draftId)
    case AdministrationInsideUKPage => _ => isTrustGeneralAdministrationRoute(draftId)
    case CountryAdministeringTrustPage => _ => _ => controllers.register.trust_details.routes.TrusteesBasedInTheUKController.onPageLoad(NormalMode, draftId)
    case TrusteesBasedInTheUKPage => _ => isTrusteesBasedInTheUKPage(draftId)

    case SettlorsBasedInTheUKPage => _ => isSettlorsBasedInTheUKPage(draftId)
    case EstablishedUnderScotsLawPage => _ => _ => controllers.register.trust_details.routes.TrustResidentOffshoreController.onPageLoad(NormalMode, draftId)
    case TrustResidentOffshorePage => _ => wasTrustPreviouslyResidentOffshoreRoute(draftId)
    case TrustPreviouslyResidentPage => _ => _ => controllers.register.trust_details.routes.TrustDetailsAnswerPageController.onPageLoad(draftId)
    case RegisteringTrustFor5APage => _ => registeringForPurposeOfSchedule5ARoute(draftId)
    case NonResidentTypePage => _ => _ => controllers.register.trust_details.routes.TrustDetailsAnswerPageController.onPageLoad(draftId)
    case InheritanceTaxActPage => _ => inheritanceTaxRoute(draftId)
    case AgentOtherThanBarristerPage => _ => _ => controllers.register.trust_details.routes.TrustDetailsAnswerPageController.onPageLoad(draftId)
    case TrustDetailsAnswerPage => _ => _ => routes.TaskListController.onPageLoad(draftId)
  }
  private def trustNameRoute(draftId: String)(answers: UserAnswers) = {
    val hasUTR = answers.get(TrustHaveAUTRPage).contains(true)

    if (hasUTR) {
      routes.PostcodeForTheTrustController.onPageLoad(NormalMode, draftId)
    } else {
      controllers.register.trust_details.routes.WhenTrustSetupController.onPageLoad(NormalMode, draftId)
    }
  }

  private def isTrustGovernedInsideUKRoute(draftId: String)(answers: UserAnswers) = answers.get(GovernedInsideTheUKPage) match {
    case Some(true)  => controllers.register.trust_details.routes.AdministrationInsideUKController.onPageLoad(NormalMode, draftId)
    case Some(false) => controllers.register.trust_details.routes.CountryGoverningTrustController.onPageLoad(NormalMode, draftId)
    case None        => routes.SessionExpiredController.onPageLoad()
  }

  private def isTrustGeneralAdministrationRoute(draftId: String)(answers: UserAnswers) = answers.get(AdministrationInsideUKPage) match {
    case Some(true)  => controllers.register.trust_details.routes.TrusteesBasedInTheUKController.onPageLoad(NormalMode, draftId)
    case Some(false) => controllers.register.trust_details.routes.CountryAdministeringTrustController.onPageLoad(NormalMode, draftId)
    case None        => routes.SessionExpiredController.onPageLoad()
  }

  private def isTrusteesBasedInTheUKPage(draftId: String)(answers: UserAnswers) = answers.get(TrusteesBasedInTheUKPage) match {
    case Some(UKBasedTrustees)   => controllers.register.trust_details.routes.EstablishedUnderScotsLawController.onPageLoad(NormalMode, draftId)
    case Some(NonUkBasedTrustees)  => controllers.register.trust_details.routes.RegisteringTrustFor5AController.onPageLoad(NormalMode, draftId)
    case Some(InternationalAndUKTrustees)  => controllers.register.trust_details.routes.SettlorsBasedInTheUKController.onPageLoad(NormalMode, draftId)
    case None         => routes.SessionExpiredController.onPageLoad()
  }

  private def isSettlorsBasedInTheUKPage(draftId: String)(answers: UserAnswers) = answers.get(SettlorsBasedInTheUKPage) match {
    case Some(true)   => controllers.register.trust_details.routes.EstablishedUnderScotsLawController.onPageLoad(NormalMode, draftId)
    case Some(false)  => controllers.register.trust_details.routes.RegisteringTrustFor5AController.onPageLoad(NormalMode, draftId)
    case None         => routes.SessionExpiredController.onPageLoad()
  }

  private def wasTrustPreviouslyResidentOffshoreRoute(draftId: String)(answers: UserAnswers) = answers.get(TrustResidentOffshorePage) match {
    case Some(true)   => controllers.register.trust_details.routes.TrustPreviouslyResidentController.onPageLoad(NormalMode, draftId)
    case Some(false)  => controllers.register.trust_details.routes.TrustDetailsAnswerPageController.onPageLoad(draftId)
    case None         => routes.SessionExpiredController.onPageLoad()
  }

  private def registeringForPurposeOfSchedule5ARoute(draftId: String)(answers: UserAnswers) = answers.get(RegisteringTrustFor5APage) match {
    case Some(true)   => controllers.register.trust_details.routes.NonResidentTypeController.onPageLoad(NormalMode, draftId)
    case Some(false)  => controllers.register.trust_details.routes.InheritanceTaxActController.onPageLoad(NormalMode, draftId)
    case None         => routes.SessionExpiredController.onPageLoad()
  }

  private def inheritanceTaxRoute(draftId: String)(answers: UserAnswers) = answers.get(InheritanceTaxActPage) match {
    case Some(true)   => controllers.register.trust_details.routes.AgentOtherThanBarristerController.onPageLoad(NormalMode, draftId)
    case Some(false)  => controllers.register.trust_details.routes.TrustDetailsAnswerPageController.onPageLoad(draftId)
    case None         => routes.SessionExpiredController.onPageLoad()
  }
}
