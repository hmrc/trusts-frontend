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

import javax.inject.Inject
import models.playback.UserAnswers
import models.playback.pages.DeclarationWhatNext.DeclareTheTrustIsUpToDate
import pages.playback.DeclarationWhatNextPage

class VariationsNavigator @Inject()() {

  def declarationWhatsNextPage(answers: UserAnswers) = {
    answers.get(DeclarationWhatNextPage) match {
      case Some(DeclareTheTrustIsUpToDate) =>
        controllers.playback.routes.DeclarationController.onPageLoad()
      case _ =>
        controllers.playback.routes.DeclarationWhatNextController.onPageLoad()
    }
  }
}
