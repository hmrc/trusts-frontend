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

package navigation.navigators

import base.SpecBase
import controllers.routes
import generators.Generators
import models.{NormalMode, UserAnswers}
import navigation.Navigator
import org.scalacheck.Arbitrary.arbitrary
import org.scalatest.prop.PropertyChecks
import pages._

trait TrustDetailsRoutes {

  self: PropertyChecks with Generators with SpecBase =>

  def trustDetailsRoutes()(implicit navigator : Navigator) = {

    "go to TrustSetup from TrustName when user does not have a UTR" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          val answers = userAnswers.set(TrustHaveAUTRPage, false).success.value

          navigator.nextPage(TrustNamePage, NormalMode)(answers)
            .mustBe(routes.WhenTrustSetupController.onPageLoad(NormalMode))
      }
    }

    "go to Is Trust Governed By Laws Inside The UK from Trust Setup Page" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          navigator.nextPage(WhenTrustSetupPage, NormalMode)(userAnswers)
            .mustBe(routes.GovernedInsideTheUKController.onPageLoad(NormalMode))
      }
    }

    "go to is Trust Administration Done Inside UK from Is Trust Governed By Laws Inside The UK when the user answers Yes" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          val answers = userAnswers.set(GovernedInsideTheUKPage, value = true).success.value

          navigator.nextPage(GovernedInsideTheUKPage, NormalMode)(answers)
            .mustBe(routes.AdministrationInsideUKController.onPageLoad(NormalMode))
      }
    }

    "go to What is the country governing the Trust from Is Trust Governed By Laws Inside The UK when the user answers No" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          val answers = userAnswers.set(GovernedInsideTheUKPage, value = false).success.value

          navigator.nextPage(GovernedInsideTheUKPage, NormalMode)(answers)
            .mustBe(routes.CountryGoverningTrustController.onPageLoad(NormalMode))
      }
    }

    "go to Is Trust Administration Done Inside UK from What is Country Governing The Trust" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          navigator.nextPage(CountryGoverningTrustPage, NormalMode)(userAnswers)
            .mustBe(routes.AdministrationInsideUKController.onPageLoad(NormalMode))
      }
    }

    "go to What Is Country Administering from Is Trust Administration Done Inside UK when user answers No" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          val answers = userAnswers.set(AdministrationInsideUKPage, value = false).success.value

          navigator.nextPage(AdministrationInsideUKPage, NormalMode)(answers)
            .mustBe(routes.CountryAdministeringTrustController.onPageLoad(NormalMode))
      }
    }

    "go to Is Trust Resident from Is Trust Administration Done Inside UK when user answers Yes" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          val answers = userAnswers.set(AdministrationInsideUKPage, value = true).success.value

          navigator.nextPage(AdministrationInsideUKPage, NormalMode)(answers)
            .mustBe(routes.TrustResidentInUKController.onPageLoad(NormalMode))
      }
    }

    "go to Is Trust Resident from What Is Country Administering" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          navigator.nextPage(CountryAdministeringTrustPage, NormalMode)(userAnswers)
            .mustBe(routes.TrustResidentInUKController.onPageLoad(NormalMode))
      }
    }

    "go to Registering for Purpose of 5A Schedule from Trust Resident in UK when user answers No" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          val answers = userAnswers.set(TrustResidentInUKPage, value = false).success.value

          navigator.nextPage(TrustResidentInUKPage, NormalMode)(answers)
            .mustBe(routes.RegisteringTrustFor5AController.onPageLoad(NormalMode))
      }
    }

    "go to Inheritance Tax from Registering for Purpose of Schedule 5A when user answers No" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          val answers = userAnswers.set(RegisteringTrustFor5APage, value = false).success.value

          navigator.nextPage(RegisteringTrustFor5APage, NormalMode)(answers)
            .mustBe(routes.InheritanceTaxActController.onPageLoad(NormalMode))
      }
    }

    "go to Check Your Answers from Inheritance Tax when user answers No" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          val answers = userAnswers.set(InheritanceTaxActPage, value = false).success.value

          navigator.nextPage(InheritanceTaxActPage, NormalMode)(answers)
            .mustBe(routes.CheckYourAnswersController.onPageLoad())
      }
    }

    "go to Agent Other Than Barrister from Inheritance Tax when user answers Yes" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          val answers = userAnswers.set(InheritanceTaxActPage, value = true).success.value

          navigator.nextPage(InheritanceTaxActPage, NormalMode)(answers)
            .mustBe(routes.AgentOtherThanBarristerController.onPageLoad(NormalMode))
      }
    }

    "go to Check Your Answers from Agent Other Than Barrister" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          navigator.nextPage(AgentOtherThanBarristerPage, NormalMode)(userAnswers)
            .mustBe(routes.CheckYourAnswersController.onPageLoad())
      }
    }

    "go to Check Your Answers from What is The Non Resident Type" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          navigator.nextPage(NonResidentTypePage, NormalMode)(userAnswers)
            .mustBe(routes.CheckYourAnswersController.onPageLoad())
      }
    }

    "go to What Is Non Resident Type from Registering for Purpose of Schedule 5A when user answers Yes" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          val answers = userAnswers.set(RegisteringTrustFor5APage, value = true).success.value

          navigator.nextPage(RegisteringTrustFor5APage, NormalMode)(answers)
            .mustBe(routes.NonResidentTypeController.onPageLoad(NormalMode))
      }
    }

    "go to Trust Established Under Scots Law from Trust Resident in UK when user answers Yes" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          val answers = userAnswers.set(TrustResidentInUKPage, value = true).success.value

          navigator.nextPage(TrustResidentInUKPage, NormalMode)(answers)
            .mustBe(routes.EstablishedUnderScotsLawController.onPageLoad(NormalMode))
      }
    }

    "go to Was Trust Resident Previously Offshore from Trust Established Under Scots Law" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          navigator.nextPage(EstablishedUnderScotsLawPage, NormalMode)(userAnswers)
            .mustBe(routes.TrustResidentOffshoreController.onPageLoad(NormalMode))
      }
    }

    "go to Where Was The Trust Previously Resident from Was Trust Resident Offshore when user answers Yes" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          val answers = userAnswers.set(TrustResidentOffshorePage, value = true).success.value

          navigator.nextPage(TrustResidentOffshorePage, NormalMode)(answers)
            .mustBe(routes.TrustPreviouslyResidentController.onPageLoad(NormalMode))
      }
    }

    "go to Check Your Answers from Was Trust Resident Offshore when user answers No" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          val answers = userAnswers.set(TrustResidentOffshorePage, value = false).success.value

          navigator.nextPage(TrustResidentOffshorePage, NormalMode)(answers)
            .mustBe(routes.CheckYourAnswersController.onPageLoad)
      }
    }

    "go to Check Your Answers from Where Was The Trust Previously Resident" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          navigator.nextPage(TrustPreviouslyResidentPage, NormalMode)(userAnswers)
            .mustBe(routes.CheckYourAnswersController.onPageLoad)
      }
    }
  }

}