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
import controllers.property_or_land.routes
import generators.Generators
import models.{InternationalAddress, NormalMode, UKAddress, UserAnswers}
import navigation.{Navigator, PropertyOrLandNavigator}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatest.prop.PropertyChecks
import pages.property_or_land._

trait PropertyOrLandRoutes {

  self: PropertyChecks with Generators with SpecBase =>

  private val index = 0

  private val navigator: Navigator = injector.instanceOf[PropertyOrLandNavigator]

  def propertyOrLandRoutes(): Unit = {

    "navigate from PropertyOrLandAddressYesNoPage" when {

      val page = PropertyOrLandAddressYesNoPage(index)

      "user answers yes to go to PropertyOrLandAddressUkYesNoPage" in {
        forAll(arbitrary[UserAnswers]) {
          userAnswers =>
            val answers = userAnswers.set(page, value = true).success.value
            navigator.nextPage(page, NormalMode, fakeDraftId)(answers)
              .mustBe(routes.PropertyOrLandAddressUkYesNoController.onPageLoad(NormalMode, index, fakeDraftId))
        }
      }

      "user answers no to go to PropertyOrLandDescriptionPage" in {
        forAll(arbitrary[UserAnswers]) {
          userAnswers =>
            val answers = userAnswers.set(page, value = false).success.value
            navigator.nextPage(page, NormalMode, fakeDraftId)(answers)
              .mustBe(routes.PropertyOrLandDescriptionController.onPageLoad(NormalMode, index, fakeDraftId))
        }
      }

    }

    "navigate from PropertyOrLandAddressUkYesNoPage" when {

      val page = PropertyOrLandAddressUkYesNoPage(index)

      "user answers yes to go to PropertyOrLandUKAddressPage" in {
        forAll(arbitrary[UserAnswers]) {
          userAnswers =>
            val answers = userAnswers.set(page, value = true).success.value
            navigator.nextPage(page, NormalMode, fakeDraftId)(answers)
              .mustBe(routes.PropertyOrLandUKAddressController.onPageLoad(NormalMode, index, fakeDraftId))
        }
      }

      "user answers no to go to PropertyOrLandInternationalAddressPage" in {
        forAll(arbitrary[UserAnswers]) {
          userAnswers =>
            val answers = userAnswers.set(page, value = false).success.value
            navigator.nextPage(page, NormalMode, fakeDraftId)(answers)
              .mustBe(routes.PropertyOrLandInternationalAddressController.onPageLoad(NormalMode, index, fakeDraftId))
        }
      }
    }

    "navigate to PropertyOrLandTotalValuePage" when {
      "navigating from PropertyOrLandDescriptionPage" in {

        val page = PropertyOrLandDescriptionPage(index)

        forAll(arbitrary[UserAnswers]) {
          userAnswers =>
            val answers = userAnswers.set(page, "Test").success.value
            navigator.nextPage(page, NormalMode, fakeDraftId)(answers)
              .mustBe(routes.PropertyOrLandTotalValueController.onPageLoad(NormalMode, index, fakeDraftId))
        }
      }
      "navigating from PropertyOrLandInternationalAddressPage" in {

        val page = PropertyOrLandInternationalAddressPage(index)

        forAll(arbitrary[UserAnswers]) {
          userAnswers =>
            val answers = userAnswers.set(page, InternationalAddress("line1", "line2", None, "France")).success.value
            navigator.nextPage(page, NormalMode, fakeDraftId)(answers)
              .mustBe(routes.PropertyOrLandTotalValueController.onPageLoad(NormalMode, index, fakeDraftId))
        }
      }
      "navigating from PropertyOrLandUKAddressPage" in {

        val page = PropertyOrLandUKAddressPage(index)

        forAll(arbitrary[UserAnswers]) {
          userAnswers =>
            val answers = userAnswers.set(page, UKAddress("line1", "line2",  None, None, "NE11NE")).success.value
            navigator.nextPage(page, NormalMode, fakeDraftId)(answers)
              .mustBe(routes.PropertyOrLandTotalValueController.onPageLoad(NormalMode, index, fakeDraftId))
        }
      }
    }

    "navigate from PropertyOrLandTotalValuePage to TrustOwnAllThePropertyOrLandPage" in {

      val page = PropertyOrLandTotalValuePage(index)

      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          val answers = userAnswers.set(page, "100").success.value
          navigator.nextPage(page, NormalMode, fakeDraftId)(answers)
            .mustBe(routes.TrustOwnAllThePropertyOrLandController.onPageLoad(NormalMode, index, fakeDraftId))
      }
    }

    "navigate from TrustOwnAllThePropertyOrLandPage" when {

      val page = TrustOwnAllThePropertyOrLandPage(index)

      "user answers yes to go to PropertyLandValueTrustPage" in {
        forAll(arbitrary[UserAnswers]) {
          userAnswers =>
            val answers = userAnswers.set(page, value = false).success.value
            navigator.nextPage(page, NormalMode, fakeDraftId)(answers)
              .mustBe(routes.PropertyLandValueTrustController.onPageLoad(NormalMode, index, fakeDraftId))
        }
      }

    }

    "navigate to PropertyOrLandAnswerPage" when {
      "navigating from PropertyLandValueTrustPage" in {

        val page = PropertyLandValueTrustPage(index)

        forAll(arbitrary[UserAnswers]) {
          userAnswers =>
            val answers = userAnswers.set(page, "100").success.value
            navigator.nextPage(page, NormalMode, fakeDraftId)(answers)
              .mustBe(routes.PropertyOrLandAnswerController.onPageLoad(index, fakeDraftId))
        }
      }

      "navigating from TrustOwnAllThePropertyOrLandPage when user answers no" in {

        val page = TrustOwnAllThePropertyOrLandPage(index)

        forAll(arbitrary[UserAnswers]) {
          userAnswers =>
            val answers = userAnswers.set(page, true).success.value
            navigator.nextPage(page, NormalMode, fakeDraftId)(answers)
              .mustBe(routes.PropertyOrLandAnswerController.onPageLoad(index, fakeDraftId))
        }
      }
    }

    "navigate from PropertyOrLandAnswerPage" in {

      val page = PropertyOrLandAnswerPage

      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          navigator.nextPage(page, NormalMode, fakeDraftId)(userAnswers)
            .mustBe(controllers.routes.AddAssetsController.onPageLoad(fakeDraftId))
      }
    }

  }

}
