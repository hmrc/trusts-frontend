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

package navigation

import base.SpecBase
import controllers.routes
import models.playback.pages.DeclarationWhatNext
import pages._

class VariationsNavigatorSpec extends SpecBase {

  implicit val navigator : VariationsNavigator = injector.instanceOf[VariationsNavigator]

  "VariationsNavigator" when {

    "DeclareTheTrustIsUpToDate has been selected" must {
      "go to the DeclarationNoChanges page" in {
        val answers = emptyUserAnswers
          .set(DeclarationWhatNextPage, DeclarationWhatNext.DeclareTheTrustIsUpToDate).success.value
        navigator.declarationWhatsNextPage(answers) mustBe controllers.playback.routes.DeclarationController.onPageLoad()
      }
    }

    "DeclareTheTrustIsUpToDate has not been selected" must {
      "go to the DeclarationNoChanges page" in {
        navigator.declarationWhatsNextPage(emptyUserAnswers) mustBe routes.DeclarationWhatNextController.onPageLoad()
      }
    }

  }
}
