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

package navigation.navigators.registration

import java.time.LocalDate

import base.RegistrationSpecBase
import controllers.register.settlors.living_settlor.routes
import controllers.register.settlors.living_settlor.business.{routes => businessRoutes}
import generators.Generators
import models.NormalMode
import models.core.UserAnswers
import models.core.pages.{IndividualOrBusiness, InternationalAddress, UKAddress}
import models.core.pages.IndividualOrBusiness.Individual
import models.registration.pages.DeedOfVariation.{ReplaceAbsolute, ReplacedWill}
import models.registration.pages.KindOfBusiness.Trading
import models.registration.pages.KindOfTrust.{Deed, Employees, FlatManagement}
import models.registration.pages.RoleInCompany.Employee
import models.registration.pages.{AddASettlor, KindOfTrust}
import navigation.Navigator
import navigation.registration.LivingSettlorNavigator
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.register.settlors.living_settlor._
import pages.register.settlors.living_settlor.business._
import pages.register.settlors.living_settlor.trust_type._
import pages.register.settlors.{AddASettlorPage, AddASettlorYesNoPage, AddAnotherSettlorYesNoPage, SetUpAfterSettlorDiedYesNoPage}

trait LivingSettlorRoutes {

  self: ScalaCheckPropertyChecks with Generators with RegistrationSpecBase =>

  private val index = 0

  private val navigator: Navigator = injector.instanceOf[LivingSettlorNavigator]

  def livingBusinessSettlorRoutes(): Unit = {

    "navigate from SettlorIndividualOrBusinessPage to SettlorBusinessName when user answers business" in {

      val page = SettlorIndividualOrBusinessPage(index)

      val ua = emptyUserAnswers
        .set(page, IndividualOrBusiness.Business).success.value

      navigator.nextPage(page, NormalMode, fakeDraftId)(ua)
        .mustBe(businessRoutes.SettlorBusinessNameController.onPageLoad(NormalMode, index, fakeDraftId))

    }

    "business settlor " must {
      val mode = NormalMode

      "Name page -> UTR yes/no page" in {
        val answers = emptyUserAnswers
          .set(SettlorBusinessNamePage(index), "Business Name").success.value

        navigator.nextPage(SettlorBusinessNamePage(index), mode, fakeDraftId)(answers)
          .mustBe(businessRoutes.SettlorBusinessUtrYesNoController.onPageLoad(mode, index, fakeDraftId))
      }

      "UTR yes/no page -> Yes -> UTR page" in {
        val answers = emptyUserAnswers
          .set(SettlorBusinessNamePage(index), "Business Name").success.value
          .set(SettlorBusinessUtrYesNoPage(index), true).success.value

        navigator.nextPage(SettlorBusinessUtrYesNoPage(index), mode, fakeDraftId)(answers)
          .mustBe(businessRoutes.SettlorBusinessUtrController.onPageLoad(mode, index, fakeDraftId))
      }

      "UTR yes/no page -> No -> Address yes/no page" in {
        val answers = emptyUserAnswers
          .set(SettlorBusinessNamePage(index), "Business Name").success.value
          .set(SettlorBusinessUtrYesNoPage(index), false).success.value

        navigator.nextPage(SettlorBusinessUtrYesNoPage(index), mode, fakeDraftId)(answers)
          .mustBe(businessRoutes.SettlorBusinessAddressYesNoController.onPageLoad(mode, index, fakeDraftId))
      }

      "UTR page (Not an Employment Related trust) -> Check details page" in {
        val answers = emptyUserAnswers
          .set(KindOfTrustPage, FlatManagement).success.value
          .set(SettlorBusinessNamePage(index), "Business Name").success.value
          .set(SettlorBusinessUtrPage(index), "1234567890").success.value

        navigator.nextPage(SettlorBusinessUtrPage(index), mode, fakeDraftId)(answers)
          .mustBe(businessRoutes.SettlorBusinessAnswerController.onPageLoad(index, fakeDraftId))
      }

      "UTR page (Employment Related trust) -> Business Type page" in {
        val answers = emptyUserAnswers
          .set(KindOfTrustPage, Employees).success.value
          .set(SettlorBusinessNamePage(index), "Business Name").success.value
          .set(SettlorBusinessUtrPage(index), "1234567890").success.value

        navigator.nextPage(SettlorBusinessUtrPage(index), mode, fakeDraftId)(answers)
          .mustBe(businessRoutes.SettlorBusinessTypeController.onPageLoad(mode, index, fakeDraftId))
      }

      "Address yes/no page (Not an Employment Related trust) -> No -> Check details page" in {
        val answers = emptyUserAnswers
          .set(KindOfTrustPage, FlatManagement).success.value
          .set(SettlorBusinessNamePage(index), "Business Name").success.value
          .set(SettlorBusinessAddressYesNoPage(index), false).success.value

        navigator.nextPage(SettlorBusinessAddressYesNoPage(index), mode, fakeDraftId)(answers)
          .mustBe(businessRoutes.SettlorBusinessAnswerController.onPageLoad(index, fakeDraftId))
      }

      "Address yes/no page (Employment Related trust) -> No -> Business Type page" in {
        val answers = emptyUserAnswers
          .set(KindOfTrustPage, Employees).success.value
          .set(SettlorBusinessNamePage(index), "Business Name").success.value
          .set(SettlorBusinessAddressYesNoPage(index), false).success.value

        navigator.nextPage(SettlorBusinessAddressYesNoPage(index), mode, fakeDraftId)(answers)
          .mustBe(businessRoutes.SettlorBusinessTypeController.onPageLoad(mode, index, fakeDraftId))
      }

      "Address yes/no page -> Yes -> Uk Address yes/no page" in {
        val answers = emptyUserAnswers
          .set(KindOfTrustPage, Employees).success.value
          .set(SettlorBusinessNamePage(index), "Business Name").success.value
          .set(SettlorBusinessAddressYesNoPage(index), true).success.value

        navigator.nextPage(SettlorBusinessAddressYesNoPage(index), mode, fakeDraftId)(answers)
          .mustBe(businessRoutes.SettlorBusinessAddressUKYesNoController.onPageLoad(mode, index, fakeDraftId))
      }

      "UK Address yes/no page -> Yes -> UK Address page" in {
        val answers = emptyUserAnswers
          .set(KindOfTrustPage, Employees).success.value
          .set(SettlorBusinessNamePage(index), "Business Name").success.value
          .set(SettlorBusinessAddressUKYesNoPage(index), true).success.value

        navigator.nextPage(SettlorBusinessAddressUKYesNoPage(index), mode, fakeDraftId)(answers)
          .mustBe(businessRoutes.SettlorBusinessAddressUKController.onPageLoad(mode, index, fakeDraftId))
      }

      "UK Address yes/no page -> No -> International Address page" in {
        val answers = emptyUserAnswers
          .set(KindOfTrustPage, Employees).success.value
          .set(SettlorBusinessNamePage(index), "Business Name").success.value
          .set(SettlorBusinessAddressUKYesNoPage(index), false).success.value

        navigator.nextPage(SettlorBusinessAddressUKYesNoPage(index), mode, fakeDraftId)(answers)
          .mustBe(businessRoutes.SettlorBusinessAddressInternationalController.onPageLoad(mode, index, fakeDraftId))
      }

      "UK Address page (Not an Employment Related trust) -> Check details page" in {
        val answers = emptyUserAnswers
          .set(KindOfTrustPage, FlatManagement).success.value
          .set(SettlorBusinessNamePage(index), "Business Name").success.value
          .set(SettlorBusinessAddressUKPage(index), UKAddress("line1", "line2",None, None, "AB11AB")).success.value

        navigator.nextPage(SettlorBusinessAddressUKPage(index), mode, fakeDraftId)(answers)
          .mustBe(businessRoutes.SettlorBusinessAnswerController.onPageLoad(index, fakeDraftId))
      }

      "UK Address page (Employment Related trust) -> Business Type page" in {
        val answers = emptyUserAnswers
          .set(KindOfTrustPage, Employees).success.value
          .set(SettlorBusinessNamePage(index), "Business Name").success.value
          .set(SettlorBusinessAddressUKPage(index), UKAddress("line1", "line2",None, None, "AB11AB")).success.value

        navigator.nextPage(SettlorBusinessAddressUKPage(index), mode, fakeDraftId)(answers)
          .mustBe(businessRoutes.SettlorBusinessTypeController.onPageLoad(mode, index, fakeDraftId))
      }

      "International Address page (Not an Employment Related trust) -> Check details page" in {
        val answers = emptyUserAnswers
          .set(KindOfTrustPage, FlatManagement).success.value
          .set(SettlorBusinessNamePage(index), "Business Name").success.value
          .set(SettlorBusinessAddressInternationalPage(index), InternationalAddress("line1", "line2",None, "DE")).success.value

        navigator.nextPage(SettlorBusinessAddressInternationalPage(index), mode, fakeDraftId)(answers)
          .mustBe(businessRoutes.SettlorBusinessAnswerController.onPageLoad(index, fakeDraftId))
      }

      "International Address page (Employment Related trust) -> Business Type page" in {
        val answers = emptyUserAnswers
          .set(KindOfTrustPage, Employees).success.value
          .set(SettlorBusinessNamePage(index), "Business Name").success.value
          .set(SettlorBusinessAddressInternationalPage(index), InternationalAddress("line1", "line2",None, "DE")).success.value

        navigator.nextPage(SettlorBusinessAddressInternationalPage(index), mode, fakeDraftId)(answers)
          .mustBe(businessRoutes.SettlorBusinessTypeController.onPageLoad(mode, index, fakeDraftId))
      }

      "Business type page (Employment Related trust) -> Business time page" in {
        val answers = emptyUserAnswers
          .set(KindOfTrustPage, Employees).success.value
          .set(SettlorBusinessNamePage(index), "Business Name").success.value
          .set(SettlorBusinessTypePage(index), Trading).success.value

        navigator.nextPage(SettlorBusinessTypePage(index), mode, fakeDraftId)(answers)
          .mustBe(businessRoutes.SettlorBusinessTimeYesNoController.onPageLoad(mode, index, fakeDraftId))
      }

      "Business time Yes/No page -> Yes -> Check details page" in {
        val answers = emptyUserAnswers
          .set(KindOfTrustPage, Employees).success.value
          .set(SettlorBusinessNamePage(index), "Business Name").success.value
          .set(SettlorBusinessTimeYesNoPage(index), true).success.value

        navigator.nextPage(SettlorBusinessTimeYesNoPage(index), mode, fakeDraftId)(answers)
          .mustBe(businessRoutes.SettlorBusinessAnswerController.onPageLoad(index, fakeDraftId))
      }

      "Business time Yes/No page -> No -> Check details page" in {
        val answers = emptyUserAnswers
          .set(KindOfTrustPage, Employees).success.value
          .set(SettlorBusinessNamePage(index), "Business Name").success.value
          .set(SettlorBusinessTimeYesNoPage(index), false).success.value

        navigator.nextPage(SettlorBusinessTimeYesNoPage(index), mode, fakeDraftId)(answers)
          .mustBe(businessRoutes.SettlorBusinessAnswerController.onPageLoad(index, fakeDraftId))
      }

      "Check details page -> Add a settlor page" in {

        val page = SettlorBusinessAnswerPage

        forAll(arbitrary[UserAnswers]) {
          userAnswers =>
            navigator.nextPage(page, NormalMode, fakeDraftId)(userAnswers)
              .mustBe(controllers.register.settlors.routes.AddASettlorController.onPageLoad(fakeDraftId))
        }
      }
    }
  }

  def livingSettlorRoutes(): Unit = {

    "go to KindOfTrust from setUpAfterSettlorDied when user answers no" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          val answers = userAnswers.set(SetUpAfterSettlorDiedYesNoPage, value = false).success.value

          navigator.nextPage(SetUpAfterSettlorDiedYesNoPage, NormalMode, fakeDraftId)(answers)
            .mustBe(controllers.register.settlors.living_settlor.routes.KindOfTrustController.onPageLoad(NormalMode, fakeDraftId))
      }
    }

    "navigate from KindOfTrustPage" when {

      "user answers Deed" in {
        val page = KindOfTrustPage
        val answer = KindOfTrust.Deed

        val answers = emptyUserAnswers.set(page, answer).success.value

        navigator.nextPage(page, NormalMode, fakeDraftId)(answers).mustBe(controllers.register.settlors.routes.AdditionToWillTrustYesNoController.onPageLoad(NormalMode, fakeDraftId))
      }

      "user answers Lifetime" in {
        val page = KindOfTrustPage
        val answer = KindOfTrust.Intervivos

        val answers = emptyUserAnswers.set(page, answer).success.value

        navigator.nextPage(page, NormalMode, fakeDraftId)(answers).mustBe(routes.HoldoverReliefYesNoController.onPageLoad(NormalMode, fakeDraftId))
      }

      "user answers Building" in {
        val page = KindOfTrustPage
        val answer = KindOfTrust.FlatManagement

        val answers = emptyUserAnswers.set(page, answer).success.value

        navigator.nextPage(page, NormalMode, fakeDraftId)(answers).mustBe(routes.SettlorIndividualOrBusinessController.onPageLoad(NormalMode, 0, fakeDraftId))
      }

      "user answers Repair Historic" in {
        val page = KindOfTrustPage
        val answer = KindOfTrust.HeritageMaintenanceFund

        val answers = emptyUserAnswers.set(page, answer).success.value

        navigator.nextPage(page, NormalMode, fakeDraftId)(answers).mustBe(routes.SettlorIndividualOrBusinessController.onPageLoad(NormalMode, 0, fakeDraftId))
      }

      "user answers Employees" in {
        val page = KindOfTrustPage
        val answer = KindOfTrust.Employees

        val answers = emptyUserAnswers.set(page, answer).success.value

        navigator.nextPage(page, NormalMode, fakeDraftId)(answers).mustBe(routes.EmployerFinancedRbsYesNoController.onPageLoad(NormalMode, fakeDraftId))
      }

    }

    "go to SetUpInAdditionToWillTrustYesNoPage from KindOfTrustPage when user answers yes" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          val answers = userAnswers.set(KindOfTrustPage, value = Deed).success.value

          navigator.nextPage(KindOfTrustPage, NormalMode, fakeDraftId)(answers)
            .mustBe(controllers.register.settlors.routes.AdditionToWillTrustYesNoController.onPageLoad(NormalMode, fakeDraftId))
      }
    }

    "go to EfrbsStartDatePage from EfrbsYesNoPage when user answers no" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          val answers = userAnswers.set(EfrbsYesNoPage, value = true).success.value

          navigator.nextPage(EfrbsYesNoPage, NormalMode, fakeDraftId)(answers)
            .mustBe(routes.EmployerFinancedRbsStartDateController.onPageLoad(NormalMode, fakeDraftId))
      }
    }

    "go to SettlorIndividualOrBusinessPage from EfrbsYesNoPage when user answers no" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          val answers = userAnswers.set(EfrbsYesNoPage, value = false).success.value

          navigator.nextPage(EfrbsYesNoPage, NormalMode, fakeDraftId)(answers)
            .mustBe(routes.SettlorIndividualOrBusinessController.onPageLoad(NormalMode, index, fakeDraftId))
      }
    }

    "go to SettlorIndividualOrBusinessPage from EfrbsStartDatePage" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          val answers = userAnswers.set(EfrbsStartDatePage, value = LocalDate.now).success.value

          navigator.nextPage(EfrbsStartDatePage, NormalMode, fakeDraftId)(answers)
            .mustBe(routes.SettlorIndividualOrBusinessController.onPageLoad(NormalMode, index, fakeDraftId))
      }
    }
    "go to HowDeedOfVariationCreatedPage from SetUpInAdditionToWillTrustYesNoPage when user answers no" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          val answers = userAnswers.set(SetUpInAdditionToWillTrustYesNoPage, value = false).success.value

          navigator.nextPage(SetUpInAdditionToWillTrustYesNoPage, NormalMode, fakeDraftId)(answers)
            .mustBe(controllers.register.settlors.routes.HowDeedOfVariationCreatedController.onPageLoad(NormalMode, fakeDraftId))
      }
    }

    "go to SettlorsNamePage from SetUpAfterSettlorDiedPage when user answers yes" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          val answers = userAnswers.set(SetUpAfterSettlorDiedYesNoPage, value = true).success.value

          navigator.nextPage(SetUpAfterSettlorDiedYesNoPage, NormalMode, fakeDraftId)(answers)
            .mustBe(controllers.register.settlors.deceased_settlor.routes.SettlorsNameController.onPageLoad(NormalMode, fakeDraftId))
      }
    }

    "go to SettlorsNamePage from SetUpInAdditionToWillTrustYesNoPage when user answers yes" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          val answers = userAnswers.set(SetUpInAdditionToWillTrustYesNoPage, value = true).success.value

          navigator.nextPage(SetUpInAdditionToWillTrustYesNoPage, NormalMode, fakeDraftId)(answers)
            .mustBe(controllers.register.settlors.deceased_settlor.routes.SettlorsNameController.onPageLoad(NormalMode, fakeDraftId))
      }
    }

    "go to SettlorIndividualOrBusinessPage from from HowDeedOfVariationCreatedPage" when {
      "selected ReplacedWill" in {
        val index = 0

        forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            val answers = userAnswers.set(HowDeedOfVariationCreatedPage, ReplacedWill).success.value

            navigator.nextPage(HowDeedOfVariationCreatedPage, NormalMode, fakeDraftId)(answers)
              .mustBe(controllers.register.settlors.living_settlor.routes.SettlorIndividualOrBusinessController.onPageLoad(NormalMode,
                index, fakeDraftId))
        }
      }

      "selected ReplaceAbsolute" in {
        val index = 0

        forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            val answers = userAnswers.set(HowDeedOfVariationCreatedPage, ReplaceAbsolute).success.value

            navigator.nextPage(HowDeedOfVariationCreatedPage, NormalMode, fakeDraftId)(answers)
              .mustBe(controllers.register.settlors.living_settlor.routes.SettlorIndividualOrBusinessController.onPageLoad(NormalMode,
                index, fakeDraftId))
        }
      }
    }

    "navigate from HoldoverReliefYesNoPage" in {

      val page = HoldoverReliefYesNoPage

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

      val page = SettlorAddressYesNoPage(index)

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

      val page = SettlorAddressUKYesNoPage(index)

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

      val page = SettlorAddressUKPage(index)

      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          navigator.nextPage(page, NormalMode, fakeDraftId)(userAnswers)
            .mustBe(routes.SettlorIndividualPassportYesNoController.onPageLoad(NormalMode, index, fakeDraftId))
      }
    }

    "navigate from SettlorIndividualAddressInternationalPage" in {

      val page = SettlorAddressInternationalPage(index)

      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          navigator.nextPage(page, NormalMode, fakeDraftId)(userAnswers)
            .mustBe(routes.SettlorIndividualPassportYesNoController.onPageLoad(NormalMode, index, fakeDraftId))
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
          .mustBe(routes.SettlorIndividualOrBusinessController.onPageLoad(NormalMode, 1, fakeDraftId))
      }
    }

    "go to RegistrationProgress from AddASettlorPage when selecting add them later" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          val answers = emptyUserAnswers
            .set(SettlorIndividualOrBusinessPage(0), Individual).success.value
            .set(AddASettlorPage, AddASettlor.YesLater).success.value

          navigator.nextPage(AddASettlorPage, NormalMode, fakeDraftId)(answers)
            .mustBe(controllers.register.routes.TaskListController.onPageLoad(fakeDraftId))
      }
    }

    "go to RegistrationProgress from AddASettlorPage when selecting no complete" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          val answers = emptyUserAnswers
            .set(SettlorIndividualOrBusinessPage(0), Individual).success.value
            .set(AddASettlorPage, AddASettlor.NoComplete).success.value

          navigator.nextPage(AddASettlorPage, NormalMode, fakeDraftId)(answers)
            .mustBe(controllers.register.routes.TaskListController.onPageLoad(fakeDraftId))
      }
    }

    "go to SettlorIndividualOrBusinessPage from from AddAASettlorYesNoPage when selected Yes" in {
      val index = 0

      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          val answers = userAnswers.set(AddASettlorYesNoPage, true).success.value

          navigator.nextPage(AddASettlorYesNoPage, NormalMode, fakeDraftId)(answers)
            .mustBe(routes.SettlorIndividualOrBusinessController.onPageLoad(NormalMode,
              index, fakeDraftId))
      }
    }

    "go to RegistrationProgress from from AddASettlorYesNoPage when selected No" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          val answers = userAnswers.set(AddASettlorYesNoPage, false).success.value

          navigator.nextPage(AddASettlorYesNoPage, NormalMode, fakeDraftId)(answers)
            .mustBe(controllers.register.routes.TaskListController.onPageLoad(fakeDraftId))
      }
    }


    "navigate from SettlorIndividualAnswerPage" in {

      val page = SettlorIndividualAnswerPage

    forAll(arbitrary[UserAnswers]) {
      userAnswers =>
        navigator.nextPage(page, NormalMode, fakeDraftId)(userAnswers)
          .mustBe(controllers.register.settlors.routes.AddASettlorController.onPageLoad(fakeDraftId))
    }

    }

  }

}
