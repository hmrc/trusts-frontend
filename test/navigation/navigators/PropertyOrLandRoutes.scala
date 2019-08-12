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
import models.{NormalMode, UserAnswers}
import navigation.{Navigator, PropertyOrLandNavigator}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatest.prop.PropertyChecks
import pages.property_or_land.PropertyOrLandAddressYesNoPage

trait PropertyOrLandRoutes {

  self: PropertyChecks with Generators with SpecBase =>

  private val index = 0

  private val navigator : Navigator = new PropertyOrLandNavigator

  def propertyOrLandRoutes(): Unit = {

    "navigate from PropertyOrLandAddressPage" when {
      "user answers yes to go to WhatIsThePropertyOrLandUKAddressPage" in {
        forAll(arbitrary[UserAnswers]) {
          userAnswers =>
            val answers = userAnswers.set(PropertyOrLandAddressYesNoPage(index), value = true).success.value
            navigator.nextPage(PropertyOrLandAddressYesNoPage(index), NormalMode, fakeDraftId)(answers)
              .mustBe(routes.PropertyOrLandUKAddressController.onPageLoad(NormalMode, index, fakeDraftId))
        }
      }

      "user answers no to go to PropertyOrLandInternationalAddressPage" in {
        forAll(arbitrary[UserAnswers]) {
          userAnswers =>
            val answers = userAnswers.set(PropertyOrLandAddressYesNoPage(index), value = false).success.value
            navigator.nextPage(PropertyOrLandAddressYesNoPage(index), NormalMode, fakeDraftId)(answers)
              .mustBe(routes.PropertyOrLandInternationalAddressController.onPageLoad(NormalMode, index, fakeDraftId))
        }
      }
    }

  }

}
