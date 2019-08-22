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
import models.{NormalMode, UserAnswers}
import navigation.{LivingSettlorNavigator, Navigator}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatest.prop.PropertyChecks
import pages.settlor._
import controllers.settlor.routes

trait LivingSettlorRoutes {

  self: PropertyChecks with Generators with SpecBase =>

  private val index = 0

  private val navigator : Navigator = new LivingSettlorNavigator

  def livingSettlorRoutes(): Unit = {

    "navigate from settlorIndividualName" in {

      val page = SettlorIndividualNamePage(index)

      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          navigator.nextPage(page, NormalMode, fakeDraftId)(userAnswers)
            .mustBe(routes.SettlorIndividualDateOfBirthYesNoController.onPageLoad(NormalMode, index, fakeDraftId))
      }
    }

  }

}
