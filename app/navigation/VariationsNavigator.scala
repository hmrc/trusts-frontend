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

import config.FrontendAppConfig
import controllers.routes
import javax.inject.Inject
import models.DeclarationWhatNext.DeclareTheTrustIsUpToDate
import models.UserAnswers
import pages.{DeclarationWhatNextPage, Page}
import play.api.mvc.Call
import uk.gov.hmrc.auth.core.AffinityGroup

class VariationsNavigator @Inject()(config: FrontendAppConfig) extends Navigator(config) {

  override protected def normalRoutes(draftId: String): Page => AffinityGroup => UserAnswers => Call = {
    case DeclarationWhatNextPage => _ => declarationWhatsNextPage(draftId)
    case _ => _ => _ => routes.IndexController.onPageLoad()
  }

  private def declarationWhatsNextPage(draftId: String)(answers: UserAnswers) = {
    answers.get(DeclarationWhatNextPage) match {
      case Some(DeclareTheTrustIsUpToDate) =>
        controllers.routes.DeclarationController.onPageLoad(draftId)
      case _ =>
        controllers.routes.DeclarationWhatNextController.onPageLoad(draftId)
    }
  }
}
