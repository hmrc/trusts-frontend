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
import models.{AddASettlor, NormalMode, UserAnswers}
import navigation.{LivingSettlorNavigator, Navigator}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatest.prop.PropertyChecks
import pages.living_settlor._
import controllers.living_settlor.routes
import models.IndividualOrBusiness.Individual
import pages.{AddASettlorPage, AddASettlorYesNoPage, AddAssetsPage, SettlorHandoverReliefYesNoPage}

trait LivingSettlorRoutes {

  self: PropertyChecks with Generators with SpecBase =>

  private val index = 0

  private val navigator : Navigator = new LivingSettlorNavigator

  def livingSettlorRoutes(): Unit = {

    "navigate from SettlorHandoverReliefYesNoPage" in {

      val page = SettlorHandoverReliefYesNoPage

      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          navigator.nextPage(page, NormalMode, fakeDraftId)(userAnswers)
            .mustBe(routes.SettlorIndividualOrBusinessController.onPageLoad(NormalMode, index, fakeDraftId))
      }
    }

    "navigate from SettlorIndividualNamePage" in {

      val page = SettlorIndividualNamePage(index)

      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          navigator.nextPage(page, NormalMode, fakeDraftId)(userAnswers)
            .mustBe(routes.SettlorIndividualDateOfBirthYesNoController.onPageLoad(NormalMode, index, fakeDraftId))
      }
    }

    "navigate from SettlorIndividualDateOfBirthYesNoPage" when {

      val page = SettlorIndividualDateOfBirthYesNoPage(index)

      "answer is yes" in {
        forAll(arbitrary[UserAnswers]) {
          userAnswers =>
            val answers = userAnswers.set(page, value = true).success.value
            navigator.nextPage(page, NormalMode, fakeDraftId)(answers)
              .mustBe(routes.SettlorIndividualDateOfBirthController.onPageLoad(NormalMode, index, fakeDraftId))
        }
      }
      "answer is no" in {
        forAll(arbitrary[UserAnswers]) {
          userAnswers =>
            val answers = userAnswers.set(page, value = false).success.value
            navigator.nextPage(page, NormalMode, fakeDraftId)(answers)
              .mustBe(routes.SettlorIndividualNINOYesNoController.onPageLoad(NormalMode, index, fakeDraftId))
        }
      }

    }

    "navigate from SettlorIndividualDateOfBirthPage" in {

      val page = SettlorIndividualDateOfBirthPage(index)

      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          navigator.nextPage(page, NormalMode, fakeDraftId)(userAnswers)
            .mustBe(routes.SettlorIndividualNINOYesNoController.onPageLoad(NormalMode, index, fakeDraftId))
      }
    }

    "navigate from SettlorIndividualNINOYesNoPage" when {

      val page = SettlorIndividualNINOYesNoPage(index)

      "answer is yes" in {

        forAll(arbitrary[UserAnswers]) {
          userAnswers =>
            val answers = userAnswers.set(page, value = true).success.value
            navigator.nextPage(page, NormalMode, fakeDraftId)(answers)
              .mustBe(routes.SettlorIndividualNINOController.onPageLoad(NormalMode, index, fakeDraftId))
        }
      }
      "answer is no" in {

        forAll(arbitrary[UserAnswers]) {
          userAnswers =>
            val answers = userAnswers.set(page, value = false).success.value
            navigator.nextPage(page, NormalMode, fakeDraftId)(answers)
              .mustBe(routes.SettlorIndividualAddressYesNoController.onPageLoad(NormalMode, index, fakeDraftId))
        }
      }
    }

    "navigate from SettlorIndividualNINOPage" in {

      val page = SettlorIndividualNINOPage(index)

      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          navigator.nextPage(page, NormalMode, fakeDraftId)(userAnswers)
            .mustBe(routes.SettlorIndividualAnswerController.onPageLoad(index, fakeDraftId))
      }
    }

    "navigate from SettlorIndividualAddressYesNoPage" when {

      val page = SettlorIndividualAddressYesNoPage(index)

      "answer is yes" in {

        forAll(arbitrary[UserAnswers]) {
          userAnswers =>
            val answers = userAnswers.set(page, value = true).success.value
            navigator.nextPage(page, NormalMode, fakeDraftId)(answers)
              .mustBe(routes.SettlorIndividualAddressUKYesNoController.onPageLoad(NormalMode, index, fakeDraftId))
        }
      }
      "answer is no" in {

        forAll(arbitrary[UserAnswers]) {
          userAnswers =>
            val answers = userAnswers.set(page, value = false).success.value
            navigator.nextPage(page, NormalMode, fakeDraftId)(answers)
              .mustBe(routes.SettlorIndividualAnswerController.onPageLoad(index, fakeDraftId))
        }
      }
    }

    "navigate from SettlorIndividualAddressUKYesNoPage" when {

      val page = SettlorIndividualAddressUKYesNoPage(index)

      "answer is yes" in {

        forAll(arbitrary[UserAnswers]) {
          userAnswers =>
            val answers = userAnswers.set(page, value = true).success.value
            navigator.nextPage(page, NormalMode, fakeDraftId)(answers)
              .mustBe(routes.SettlorIndividualAddressUKController.onPageLoad(NormalMode, index, fakeDraftId))
        }
      }
      "answer is no" in {

        forAll(arbitrary[UserAnswers]) {
          userAnswers =>
            val answers = userAnswers.set(page, value = false).success.value
            navigator.nextPage(page, NormalMode, fakeDraftId)(answers)
              .mustBe(routes.SettlorIndividualAddressInternationalController.onPageLoad(NormalMode, index, fakeDraftId))
        }
      }
    }

    "navigate from SettlorIndividualAddressUKPage" in {

      val page = SettlorIndividualAddressUKPage(index)

      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          navigator.nextPage(page, NormalMode, fakeDraftId)(userAnswers)
            .mustBe(routes.SettlorIndividualPassportYesNoController.onPageLoad(NormalMode, index, fakeDraftId))
      }
    }

    "navigate from SettlorIndividualAddressInternationalPage" in {

      val page = SettlorIndividualAddressInternationalPage(index)

        forAll(arbitrary[UserAnswers]) {
          userAnswers =>
            navigator.nextPage(page, NormalMode, fakeDraftId)(userAnswers)
              .mustBe(routes.SettlorIndividualPassportYesNoController.onPageLoad(NormalMode, index, fakeDraftId))
        }
      }
    }

    "navigate from SettlorIndividualPassportYesNoPage" when {

      val page = SettlorIndividualPassportYesNoPage(index)

      "answer is yes" in {

        forAll(arbitrary[UserAnswers]) {
          userAnswers =>
            val answers = userAnswers.set(page, value = true).success.value
            navigator.nextPage(page, NormalMode, fakeDraftId)(answers)
              .mustBe(routes.SettlorIndividualPassportController.onPageLoad(NormalMode, index, fakeDraftId))
        }
      }

      "answer is no" in {

        forAll(arbitrary[UserAnswers]) {
          userAnswers =>
            val answers = userAnswers.set(page, value = false).success.value
            navigator.nextPage(page, NormalMode, fakeDraftId)(answers)
              .mustBe(routes.SettlorIndividualIDCardYesNoController.onPageLoad(NormalMode, index, fakeDraftId))
        }
      }
    }

    "navigate from SettlorIndividualPassportPage" in {

      val page = SettlorIndividualPassportPage(index)

      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          navigator.nextPage(page, NormalMode, fakeDraftId)(userAnswers)
            .mustBe(routes.SettlorIndividualAnswerController.onPageLoad(index, fakeDraftId))
      }
    }

    "navigate from SettlorIndividualIDCardYesNoPage" when {
      "answer is yes" in {

        val page = SettlorIndividualIDCardYesNoPage(index)

        forAll(arbitrary[UserAnswers]) {
          userAnswers =>
            val answers = userAnswers.set(page, value = true).success.value
            navigator.nextPage(page, NormalMode, fakeDraftId)(answers)
              .mustBe(routes.SettlorIndividualIDCardController.onPageLoad(NormalMode, index, fakeDraftId))
        }
      }
      "answer is no" in {

        val page = SettlorIndividualIDCardYesNoPage(index)

        forAll(arbitrary[UserAnswers]) {
          userAnswers =>
            val answers = userAnswers.set(page, value = false).success.value
            navigator.nextPage(page, NormalMode, fakeDraftId)(answers)
              .mustBe(routes.SettlorIndividualAnswerController.onPageLoad(index, fakeDraftId))
        }
      }
    }

    "navigate from SettlorIndividualIDCardPage" in {

      val page = SettlorIndividualIDCardPage(index)

      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          navigator.nextPage(page, NormalMode, fakeDraftId)(userAnswers)
            .mustBe(routes.SettlorIndividualAnswerController.onPageLoad(index, fakeDraftId))
      }

    }

  "add another settlor" must {

    "go to the IndividualOrBusiness from AddASettlorPage when selected add them now" in {

      val answers = emptyUserAnswers
        .set(SettlorIndividualOrBusinessPage(0), Individual).success.value
        .set(AddASettlorPage, AddASettlor.YesNow).success.value

      navigator.nextPage(AddASettlorPage, NormalMode, fakeDraftId)(answers)
        .mustBe(controllers.living_settlor.routes.SettlorIndividualOrBusinessController.onPageLoad(NormalMode, 1, fakeDraftId))
    }
  }

  "go to RegistrationProgress from AddASettlorPage when selecting add them later" in {
    forAll(arbitrary[UserAnswers]) {
      userAnswers =>

        val answers = emptyUserAnswers
          .set(SettlorIndividualOrBusinessPage(0), Individual).success.value
          .set(AddASettlorPage, AddASettlor.YesLater).success.value

        navigator.nextPage(AddASettlorPage, NormalMode, fakeDraftId)(answers)
          .mustBe(controllers.routes.TaskListController.onPageLoad(fakeDraftId))
    }
  }

  "go to RegistrationProgress from AddASettlorPage when selecting no complete" in {
    forAll(arbitrary[UserAnswers]) {
      userAnswers =>

        val answers = emptyUserAnswers
          .set(SettlorIndividualOrBusinessPage(0), Individual).success.value
          .set(AddASettlorPage, AddASettlor.NoComplete).success.value

        navigator.nextPage(AddASettlorPage, NormalMode, fakeDraftId)(answers)
          .mustBe(controllers.routes.TaskListController.onPageLoad(fakeDraftId))
    }
  }

  "go to SettlorIndividualOrBusinessPage from from AddAASettlorYesNoPage when selected Yes" in {
    val index = 0

    forAll(arbitrary[UserAnswers]) {
      userAnswers =>

        val answers = userAnswers.set(AddASettlorYesNoPage, true).success.value

        navigator.nextPage(AddASettlorYesNoPage, NormalMode, fakeDraftId)(answers)
          .mustBe(controllers.living_settlor.routes.SettlorIndividualOrBusinessController.onPageLoad(NormalMode,
            index, fakeDraftId))
    }
  }

  "go to RegistrationProgress from from AddASettlorYesNoPage when selected No" in {
    forAll(arbitrary[UserAnswers]) {
      userAnswers =>

        val answers = userAnswers.set(AddASettlorYesNoPage, false).success.value

        navigator.nextPage(AddASettlorYesNoPage, NormalMode, fakeDraftId)(answers)
          .mustBe(controllers.routes.TaskListController.onPageLoad(fakeDraftId))
    }
  }



  }
