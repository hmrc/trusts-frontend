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
  import generators.Generators
  import navigation.Navigator
  import org.scalatest.prop.PropertyChecks
  import org.scalacheck.Arbitrary.arbitrary
  import pages._
  import models.{NormalMode, UserAnswers}
  import controllers.routes

  trait TrusteeRoutes {

    self: PropertyChecks with Generators with SpecBase =>

    def trusteeRoutes()(implicit navigator: Navigator) = {

      "go to TrusteeOrIndividualPage from IsThisLeadTrusteePage page" in {
        forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            navigator.nextPage(IsThisLeadTrusteePage, NormalMode)(userAnswers)
              .mustBe(routes.TrusteeOrIndividualController.onPageLoad(NormalMode))
        }
      }

      "go to TrusteesNamePage from TrusteeOrIndividualPage page" in {
        forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            navigator.nextPage(TrusteeOrIndividualPage, NormalMode)(userAnswers)
              .mustBe(routes.TrusteesNameController.onPageLoad(NormalMode))
        }
      }

      "go to TrusteeAnswersPage from TrusteeNamePage page" in {
        forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            navigator.nextPage(TrusteesNamePage, NormalMode)(userAnswers)
              .mustBe(routes.TrusteesAnswerPageController.onPageLoad())
        }
      }

      "go to AddATrusteePage from TrusteeAnswersPage page" in {
        forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            navigator.nextPage(TrusteesAnswerPage, NormalMode)(userAnswers)
              .mustBe(routes.AddATrusteeController.onPageLoad())
        }
      }

    }
  }
