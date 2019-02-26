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
import models.{AddATrustee, NormalMode, UserAnswers}
import navigation.Navigator
import org.scalacheck.Arbitrary.arbitrary
import org.scalatest.prop.PropertyChecks
import pages._

trait TrusteeRoutes {

  self: PropertyChecks with Generators with SpecBase =>

  val index = 0

  def trusteeRoutes()(implicit navigator: Navigator) = {

    "there a no trustees" must {

      "go to the next trustee from AddATrusteePage when selected add them now" in {
        forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            val answers = userAnswers.set(AddATrusteePage, AddATrustee.YesNow).success.value
                .remove(Trustees).success.value

            navigator.nextPage(AddATrusteePage, NormalMode)(answers)
              .mustBe(routes.IsThisLeadTrusteeController.onPageLoad(NormalMode, 0))
        }
      }

    }

    "there is atleast one trustee" must {

      "go to the next trustee from AddATrusteePage when selected add them now" in {

            val answers = UserAnswers(userAnswersId)
              .set(IsThisLeadTrusteePage(0), true).success.value
              .set(AddATrusteePage, AddATrustee.YesNow).success.value

            navigator.nextPage(AddATrusteePage, NormalMode)(answers)
              .mustBe(routes.IsThisLeadTrusteeController.onPageLoad(NormalMode, 1))
      }

    }

    "stay on AddATrustee from AddATrusteePage when selecting add them later" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          val answers = userAnswers.set(IsThisLeadTrusteePage(0), true).success.value
            .set(AddATrusteePage, AddATrustee.YesLater).success.value

          navigator.nextPage(AddATrusteePage, NormalMode)(answers)
            .mustBe(routes.AddATrusteeController.onPageLoad())
      }
    }

    "stay on AddATrustee from AddATrusteePage when selecting added them all" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          val answers = userAnswers.set(IsThisLeadTrusteePage(0), true).success.value
            .set(AddATrusteePage, AddATrustee.NoComplete).success.value

          navigator.nextPage(AddATrusteePage, NormalMode)(answers)
            .mustBe(routes.AddATrusteeController.onPageLoad())
      }
    }

    "go to TrusteeIndividualOrBusinessPage from IsThisLeadTrusteePage page" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          navigator.nextPage(IsThisLeadTrusteePage(index), NormalMode)(userAnswers)
            .mustBe(routes.TrusteeIndividualOrBusinessController.onPageLoad(NormalMode, index))
      }
    }

    "go to TrusteesNamePage from TrusteeOrIndividualPage page" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          navigator.nextPage(TrusteeIndividualOrBusinessPage(index), NormalMode)(userAnswers)
            .mustBe(routes.TrusteesNameController.onPageLoad(NormalMode, index))
      }
    }

    "go to TrusteesDateOfBirthPage from TrusteesNamePage page" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          navigator.nextPage(TrusteesNamePage(index), NormalMode)(userAnswers)
            .mustBe(routes.TrusteesDateOfBirthController.onPageLoad(NormalMode, index))
      }
    }

    "go to TrusteeAUKCitizen from TrusteesDateOfBirthPage page" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          navigator.nextPage(TrusteesDateOfBirthPage(index), NormalMode)(userAnswers)
            .mustBe(routes.TrusteeAUKCitizenController.onPageLoad(NormalMode, index))
      }
    }

    "go to TrusteesNinoPage from TrusteeAUKCitizen when user answers Yes" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          val answers = userAnswers.set(TrusteeAUKCitizenPage(index), value = true).success.value

          navigator.nextPage(TrusteeAUKCitizenPage(index), NormalMode)(answers)
            .mustBe(routes.TrusteesAnswerPageController.onPageLoad(index))
      }
    }

    "go to TrusteePassportOrIDPage from TrusteeAUKCitizen when user answers No" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          val answers = userAnswers.set(TrusteeAUKCitizenPage(index), value = false).success.value

          navigator.nextPage(TrusteeAUKCitizenPage(index), NormalMode)(answers)
            .mustBe(routes.TrusteesAnswerPageController.onPageLoad(index))
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
