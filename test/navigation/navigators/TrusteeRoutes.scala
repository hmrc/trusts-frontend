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
import models.IndividualOrBusiness.{Business, Individual}
import mapping.reads.Trustees
import models.{AddATrustee, NormalMode, UserAnswers}
import navigation.Navigator
import org.scalacheck.Arbitrary.arbitrary
import org.scalatest.prop.PropertyChecks
import pages._

trait TrusteeRoutes {

  self: PropertyChecks with Generators with SpecBase =>

  val index = 0

  def trusteeRoutes()(implicit navigator: Navigator) = {

    "there are no trustees" must {

      "go to the next trustee from AddATrusteePage when selected add them now" in {
        forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            val answers = userAnswers.set(AddATrusteePage, AddATrustee.YesNow).success.value
                .remove(Trustees).success.value

            navigator.nextPage(AddATrusteePage, NormalMode, fakeDraftId)(answers)
              .mustBe(routes.IsThisLeadTrusteeController.onPageLoad(NormalMode, 0, fakeDraftId))
        }
      }

      "go to the next trustee from AddATrusteeYesNoPage when selecting yes" in {
        forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            val answers = userAnswers.set(AddATrusteeYesNoPage, true).success.value
                .remove(Trustees).success.value

            navigator.nextPage(AddATrusteeYesNoPage, NormalMode, fakeDraftId)(answers)
              .mustBe(routes.IsThisLeadTrusteeController.onPageLoad(NormalMode, 0, fakeDraftId))
        }
      }

      "go to the registration progress page from AddATrusteeYesNoPage when selecting no" in {
        forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            val answers = userAnswers.set(AddATrusteeYesNoPage, false).success.value
              .remove(Trustees).success.value

            navigator.nextPage(AddATrusteeYesNoPage, NormalMode, fakeDraftId)(answers)
              .mustBe(routes.TaskListController.onPageLoad(fakeDraftId))
        }
      }

    }

    "there is atleast one trustee" must {

      "go to the next trustee from AddATrusteePage when selected add them now" in {

            val answers = emptyUserAnswers
              .set(IsThisLeadTrusteePage(0), true).success.value
              .set(AddATrusteePage, AddATrustee.YesNow).success.value

            navigator.nextPage(AddATrusteePage, NormalMode, fakeDraftId)(answers)
              .mustBe(routes.IsThisLeadTrusteeController.onPageLoad(NormalMode, 1, fakeDraftId))
      }

    }

    "go to RegistrationProgress from AddATrusteePage when selecting add them later" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          val answers = userAnswers.set(IsThisLeadTrusteePage(0), true).success.value
            .set(AddATrusteePage, AddATrustee.YesLater).success.value

          navigator.nextPage(AddATrusteePage, NormalMode, fakeDraftId)(answers)
            .mustBe(routes.TaskListController.onPageLoad(fakeDraftId))
      }
    }

    "go to RegistrationProgress from AddATrusteePage when selecting added them all" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          val answers = userAnswers.set(IsThisLeadTrusteePage(0), true).success.value
            .set(AddATrusteePage, AddATrustee.NoComplete).success.value

          navigator.nextPage(AddATrusteePage, NormalMode, fakeDraftId)(answers)
            .mustBe(routes.TaskListController.onPageLoad(fakeDraftId))
      }
    }

    "go to TrusteeIndividualOrBusinessPage from IsThisLeadTrusteePage page" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          navigator.nextPage(IsThisLeadTrusteePage(index), NormalMode, fakeDraftId)(userAnswers)
            .mustBe(routes.TrusteeIndividualOrBusinessController.onPageLoad(NormalMode, index, fakeDraftId))
      }
    }

    "go to TrusteesNamePage from TrusteeOrIndividualPage page when answer is Individual" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          val answers = userAnswers.set(TrusteeIndividualOrBusinessPage(0), Individual).success.value

          navigator.nextPage(TrusteeIndividualOrBusinessPage(index), NormalMode, fakeDraftId)(answers)
            .mustBe(routes.TrusteesNameController.onPageLoad(NormalMode, index, fakeDraftId))
      }
    }

    "go to TrusteeIndividualOrBusinessPage from TrusteeOrIndividualPage page when answer is Business" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          val answers = userAnswers.set(TrusteeIndividualOrBusinessPage(0), Business).success.value

          navigator.nextPage(TrusteeIndividualOrBusinessPage(index), NormalMode, fakeDraftId)(answers)
            .mustBe(routes.TrusteeIndividualOrBusinessController.onPageLoad(NormalMode, index, fakeDraftId))
      }
    }

    "go to TrusteesDateOfBirthPage from TrusteesNamePage page" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          navigator.nextPage(TrusteesNamePage(index), NormalMode, fakeDraftId)(userAnswers)
            .mustBe(routes.TrusteesDateOfBirthController.onPageLoad(NormalMode, index, fakeDraftId))
      }
    }

    "non lead trustee" must {

      "go to TrusteeAnswersPage from TrusteesDateOfBirthPage" in {
        forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            val answers = userAnswers.set(IsThisLeadTrusteePage(index), false).success.value

            navigator.nextPage(TrusteesDateOfBirthPage(index), NormalMode, fakeDraftId)(answers)
              .mustBe(routes.TrusteesAnswerPageController.onPageLoad(index, fakeDraftId))
        }
      }

    }

    "lead trustee" must {

      "go to TrusteeAUKCitizen from TrusteesDateOfBirthPage page" in {
        forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            val answers = userAnswers.set(IsThisLeadTrusteePage(index), true).success.value

            navigator.nextPage(TrusteesDateOfBirthPage(index), NormalMode, fakeDraftId)(answers)
              .mustBe(routes.TrusteeAUKCitizenController.onPageLoad(NormalMode, index, fakeDraftId))
        }
      }

      "go to TrusteesNinoPage from TrusteeAUKCitizen when user answers Yes" in {
        forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            val answers = userAnswers.set(TrusteeAUKCitizenPage(index), value = true).success.value

            navigator.nextPage(TrusteeAUKCitizenPage(index), NormalMode, fakeDraftId)(answers)
              .mustBe(routes.TrusteesNinoController.onPageLoad(NormalMode, index, fakeDraftId))
        }
      }

      "go to TrusteeAUKCitizenPage from TrusteeAUKCitizen when user answers No" in {
        forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            val answers = userAnswers.set(TrusteeAUKCitizenPage(index), value = false).success.value

            navigator.nextPage(TrusteeAUKCitizenPage(index), NormalMode, fakeDraftId)(answers)
              .mustBe(routes.TrusteeAUKCitizenController.onPageLoad(NormalMode,index, fakeDraftId))
        }
      }

      "go to TrusteeLivesInUKPage from TrusteesNinoPage" in {
        forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            navigator.nextPage(TrusteesNinoPage(index), NormalMode, fakeDraftId)(userAnswers)
              .mustBe(routes.TrusteeLiveInTheUKController.onPageLoad(NormalMode, index, fakeDraftId))
        }
      }

      "go to TrusteesUkAddressPage from TrusteeLivesInUKPage when answer is yes" in {
        forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            val answers = userAnswers.set(TrusteeLiveInTheUKPage(index), value = true).success.value

            navigator.nextPage(TrusteeLiveInTheUKPage(index), NormalMode, fakeDraftId)(answers)
              .mustBe(routes.TrusteesUkAddressController.onPageLoad(NormalMode, index, fakeDraftId))
        }
      }

      "go to TrusteeLivesInUKPage from TrusteeLivesInUKPage when answer is no" in {
        forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            val answers = userAnswers.set(TrusteeLiveInTheUKPage(index), value = false).success.value

            navigator.nextPage(TrusteeLiveInTheUKPage(index), NormalMode, fakeDraftId)(answers)
              .mustBe(routes.TrusteeLiveInTheUKController.onPageLoad(NormalMode, index, fakeDraftId))
        }
      }

      "go to TrusteeTelephoneNumberPage from TrusteesUkAddressPage" in {
        forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            navigator.nextPage(TrusteesUkAddressPage(index), NormalMode, fakeDraftId)(userAnswers)
              .mustBe(routes.TelephoneNumberController.onPageLoad(NormalMode, index, fakeDraftId))
        }
      }

      "go to TrusteeAnswerPage from TrusteeTelephoneNumberPage" in {
        forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            navigator.nextPage(TelephoneNumberPage(index), NormalMode, fakeDraftId)(userAnswers)
              .mustBe(routes.TrusteesAnswerPageController.onPageLoad(index, fakeDraftId))
        }
      }

    }

    "go to AddATrusteePage from TrusteeAnswersPage page" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          navigator.nextPage(TrusteesAnswerPage, NormalMode, fakeDraftId)(userAnswers)
            .mustBe(routes.AddATrusteeController.onPageLoad(fakeDraftId))
      }
    }

  }
}
