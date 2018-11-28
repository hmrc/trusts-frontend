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

package utils

import base.SpecBase
import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import controllers.routes
import pages._
import models._

class NavigatorSpec extends SpecBase with MockitoSugar {

  val navigator = new Navigator

  "Navigator" when {

    "in Normal mode" must {

      "go to Index from a page that doesn't exist in the route map" in {
        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, NormalMode)(mock[UserAnswers]) mustBe routes.IndexController.onPageLoad()
      }

      "go from the TrustNamePage page to TrustAddressUKYesNoPage" in {
          navigator.nextPage(TrustNamePage, NormalMode)(mock[UserAnswers]) mustEqual routes.TrustAddressUKYesNoController.onPageLoad(NormalMode)
      }

      "go from TrustAddressUKYesNoPage to TrustAddressUKPage" when {
        "the user has answered Yes to TrustAddressUKYesNoPage" in {
          val mockAnswers = mock[UserAnswers]
          when(mockAnswers.get(TrustAddressUKYesNoPage)) thenReturn Some(true)
          navigator.nextPage(TrustAddressUKYesNoPage, NormalMode)(mockAnswers) mustEqual routes.TrustAddressUKController.onPageLoad(NormalMode)
        }
      }

      "go from TrustAddressUKYesNoPage to InternationalTrustsAddressPage" when {
        "the user has answered No to TrustAddressUKYesNoPage" in {
          val mockAnswers = mock[UserAnswers]
          when(mockAnswers.get(TrustAddressUKYesNoPage)) thenReturn Some(false)
          navigator.nextPage(TrustAddressUKYesNoPage, NormalMode)(mockAnswers) mustEqual routes.TrustsAddressInternationalController.onPageLoad(NormalMode)
        }
      }

      "go from TrustAddressUKYesNoPage to IndexPage" when {
        "incorrect answer to TrustAddressUKYesNoPage" in {
          val mockAnswers = mock[UserAnswers]
          when(mockAnswers.get(TrustAddressUKYesNoPage)) thenReturn None
          navigator.nextPage(TrustAddressUKYesNoPage, NormalMode)(mockAnswers) mustEqual routes.IndexController.onPageLoad()
        }
      }

      "go from the TrustAddressUKPage page to IndexPae" in {
        navigator.nextPage(TrustAddressUKPage, NormalMode)(mock[UserAnswers]) mustEqual routes.IndexController.onPageLoad()
      }

      "go from the InternationalTrustsAddressPage page to IndexPae" in {
        navigator.nextPage(TrustsAddressInternationalPage, NormalMode)(mock[UserAnswers]) mustEqual routes.IndexController.onPageLoad()
      }

    }

    "in Check mode" must {

      "go to CheckYourAnswers from a page that doesn't exist in the edit route map" in {

        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, CheckMode)(mock[UserAnswers]) mustBe routes.CheckYourAnswersController.onPageLoad()
      }
    }
  }
}
