/*
 * Copyright 2018 HM Revenue & Customs
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

import base.SpecBase
import controllers.routes
import generators.Generators
import models.{CheckMode, NormalMode, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatest.prop.PropertyChecks
import pages._

class NavigatorSpec extends SpecBase with PropertyChecks with Generators{

  val navigator = new Navigator

  "Navigator" when {

    "in Normal mode" must {

      "go to Index from a page that doesn't exist in the route map" in {

        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, NormalMode)(UserAnswers("id")) mustBe routes.IndexController.onPageLoad()
      }

      "go to When Trust Setup from Index page" in {

        forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            navigator.nextPage(TrustNamePage, NormalMode)(userAnswers)
              .mustBe(routes.WhenTrustSetupController.onPageLoad(NormalMode))
        }
      }

      "go to Is Trust Governed By Laws Outside The UK from Trust Setup Page" in {
        forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            navigator.nextPage(WhenTrustSetupPage, NormalMode)(userAnswers)
              .mustBe(routes.GovernedOutsideTheUKController.onPageLoad(NormalMode))
        }
      }

      "go to What is the country governing the Trust from Is Trust Governed By Laws Outside The UK when the user answers Yes" in {
        forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            val answers = userAnswers.set(GovernedOutsideTheUKPage, value = true).success.value

            navigator.nextPage(GovernedOutsideTheUKPage, NormalMode)(answers)
              .mustBe(routes.CountryGoverningTrustController.onPageLoad(NormalMode))
        }
      }

      "go to Is Trust Administration Done Outside UK from Is Trust Governed By Laws Outside The UK when the user answers No" in {
        forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            val answers = userAnswers.set(GovernedOutsideTheUKPage, value = false).success.value

            navigator.nextPage(GovernedOutsideTheUKPage, NormalMode)(answers)
              .mustBe(routes.AdministrationOutsideUKController.onPageLoad(NormalMode))
        }
      }

      "go to Is Trust Administration Done Outside UK from What is Country Governing The Trust" in {
        forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            navigator.nextPage(CountryGoverningTrustPage, NormalMode)(userAnswers)
              .mustBe(routes.AdministrationOutsideUKController.onPageLoad(NormalMode))
        }
      }

      "go to Is Trust Resident from Is Trust Administration Done Outside UK when user answers No" in {
        forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            val answers = userAnswers.set(AdministrationOutsideUKPage, value = false).success.value

            navigator.nextPage(AdministrationOutsideUKPage, NormalMode)(answers)
              .mustBe(routes.TrustResidentInUKController.onPageLoad(NormalMode))
        }
      }

      "go to What Is Country Administering from Is Trust Administration Done Outside UK when user answers Yes" in {
        forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            val answers = userAnswers.set(AdministrationOutsideUKPage, value = true).success.value

            navigator.nextPage(AdministrationOutsideUKPage, NormalMode)(answers)
              .mustBe(routes.CountryAdministeringTrustController.onPageLoad(NormalMode))
        }
      }

      "go to Is Trust Resident from What Is Country Administering" in {
        forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            navigator.nextPage(CountryAdministeringTrustPage, NormalMode)(userAnswers)
              .mustBe(routes.TrustResidentInUKController.onPageLoad(NormalMode))
        }
      }

    }

    "in Check mode" must {

      "go to CheckYourAnswers from a page that doesn't exist in the edit route map" in {

        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, CheckMode)(UserAnswers("id")) mustBe routes.CheckYourAnswersController.onPageLoad()
      }

    }
  }
}
