/*
 * Copyright 2024 HM Revenue & Customs
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

package navigation.navigators.registration

import base.RegistrationSpecBase
import controllers.register.suitability.routes
import generators.Generators
import models.core.UserAnswers
import navigation.Navigator
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.register.suitability.{ExpressTrustYesNoPage, TaxLiabilityInCurrentTaxYearYesNoPage, TrustTaxableYesNoPage, UndeclaredTaxLiabilityYesNoPage}

trait SuitabilityRoutes {

  self: ScalaCheckPropertyChecks with Generators with RegistrationSpecBase =>

  def suitabilityRoutes()(implicit navigator: Navigator): Unit = {

      "ExpressTrust Page" when {
        "-> YES -> Any tax liability in current tax year?" in {
          forAll(arbitrary[UserAnswers]) {
            userAnswers =>
              val answers = userAnswers.set(ExpressTrustYesNoPage, true).success.value
                .set(TrustTaxableYesNoPage, true).success.value

              navigator.nextPage(ExpressTrustYesNoPage)(answers)
                .mustBe(routes.BeforeYouContinueController.onPageLoad())
          }
        }

        "-> No -> Any tax liability in current tax year?" in {
          forAll(arbitrary[UserAnswers]) {
            userAnswers =>
              val answers = userAnswers.set(ExpressTrustYesNoPage, false).success.value
                .set(TrustTaxableYesNoPage, true).success.value

              navigator.nextPage(ExpressTrustYesNoPage)(answers)
                .mustBe(routes.BeforeYouContinueController.onPageLoad())
          }
        }
      }

      "Any tax liability in current tax year?" when {
        "-> YES -> Before you continue" in {
          forAll(arbitrary[UserAnswers]) {
            userAnswers =>
              val answers = userAnswers.set(TaxLiabilityInCurrentTaxYearYesNoPage, true).success.value
                .set(TrustTaxableYesNoPage, true).success.value

              navigator.nextPage(TaxLiabilityInCurrentTaxYearYesNoPage)(answers)
                .mustBe(routes.BeforeYouContinueController.onPageLoad())
          }
        }
        "-> NO -> Any undeclared tax liability?" in {
          forAll(arbitrary[UserAnswers]) {
            userAnswers =>
              val answers = userAnswers.set(TaxLiabilityInCurrentTaxYearYesNoPage, false).success.value
                .set(TrustTaxableYesNoPage, true).success.value

              navigator.nextPage(TaxLiabilityInCurrentTaxYearYesNoPage)(answers)
                .mustBe(routes.UndeclaredTaxLiabilityYesNoController.onPageLoad())
          }
        }
      }

      "Any undeclared tax liability?" when {
        "not an Express Trust" must {
          "-> YES -> Before you continue" in {
            forAll(arbitrary[UserAnswers]) {
              userAnswers =>
                val answers = userAnswers
                  .set(ExpressTrustYesNoPage, false).success.value
                  .set(UndeclaredTaxLiabilityYesNoPage, true).success.value

                navigator.nextPage(UndeclaredTaxLiabilityYesNoPage)(answers)
                  .mustBe(routes.BeforeYouContinueController.onPageLoad())
            }
          }
          "-> NO -> You do not need to register" in {
            forAll(arbitrary[UserAnswers]) {
              userAnswers =>
                val answers = userAnswers
                  .set(ExpressTrustYesNoPage, false).success.value
                  .set(UndeclaredTaxLiabilityYesNoPage, false).success.value

                navigator.nextPage(UndeclaredTaxLiabilityYesNoPage)(answers)
                  .mustBe(routes.NoNeedToRegisterController.onPageLoad())
            }
          }
        }

        "an Express Trust" must {
          "-> YES -> Before you continue" in {
            forAll(arbitrary[UserAnswers]) {
              userAnswers =>
                val answers = userAnswers
                  .set(ExpressTrustYesNoPage, true).success.value
                  .set(UndeclaredTaxLiabilityYesNoPage, true).success.value

                navigator.nextPage(UndeclaredTaxLiabilityYesNoPage)(answers)
                  .mustBe(routes.BeforeYouContinueController.onPageLoad())
            }
          }
          "-> No -> Before you continue" in {
            forAll(arbitrary[UserAnswers]) {
              userAnswers =>
                val answers = userAnswers
                  .set(ExpressTrustYesNoPage, true).success.value
                  .set(UndeclaredTaxLiabilityYesNoPage, false).success.value

                navigator.nextPage(UndeclaredTaxLiabilityYesNoPage)(answers)
                  .mustBe(routes.BeforeYouContinueController.onPageLoad())
            }
          }
        }
      }
  }
}
