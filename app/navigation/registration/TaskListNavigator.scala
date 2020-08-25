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

package navigation.registration

import config.FrontendAppConfig
import javax.inject.{Inject, Singleton}
import models.NormalMode
import models.core.UserAnswers
import models.registration.Matched.Success
import models.registration.pages.Status.Completed
import pages.entitystatus.{DeceasedSettlorStatus, TrustDetailsStatus}
import pages.register.ExistingTrustMatched
import pages.register.settlors.SetUpAfterSettlorDiedYesNoPage
import play.api.mvc.Call
import sections._

@Singleton
class TaskListNavigator @Inject()(frontendAppConfig: FrontendAppConfig) {

  def trustDetailsJourney(userAnswers: UserAnswers, draftId: String): Call = {
    {
      val completed = userAnswers.get(TrustDetailsStatus).contains(Completed)
      val successfullyMatched = userAnswers.get(ExistingTrustMatched).contains(Success)

      if (completed) {
        controllers.register.trust_details.routes.TrustDetailsAnswerPageController.onPageLoad(draftId)
      } else {
        if (successfullyMatched) {
          controllers.register.trust_details.routes.WhenTrustSetupController.onPageLoad(NormalMode, draftId)
        } else {
          controllers.register.trust_details.routes.TrustNameController.onPageLoad(NormalMode, draftId)
        }
      }
    }
  }

  def trusteesJourneyUrl(draftId: String): String = {
    frontendAppConfig.trusteesFrontendUrl(draftId)
  }

  def settlorsJourney(userAnswers: UserAnswers, draftId: String): Call = {
    userAnswers.get(DeceasedSettlorStatus) match {
      case Some(value) =>
        if (value.equals(Completed)) {
          controllers.register.settlors.deceased_settlor.routes.DeceasedSettlorAnswerController.onPageLoad(draftId)
        } else {
          controllers.register.settlors.routes.SetUpAfterSettlorDiedController.onPageLoad(NormalMode, draftId)
        }
      case None =>
        userAnswers.get(SetUpAfterSettlorDiedYesNoPage) match {
          case None => controllers.register.settlors.routes.SettlorInfoController.onPageLoad(draftId)
          case _ =>
            userAnswers.get(LivingSettlors).getOrElse(Nil) match {
              case Nil => controllers.register.settlors.routes.SetUpAfterSettlorDiedController.onPageLoad(NormalMode, draftId)
              case _ => controllers.register.settlors.routes.AddASettlorController.onPageLoad(draftId)
            }
        }
    }
  }

  def beneficiariesJourneyUrl(draftId: String): String = {
    frontendAppConfig.beneficiariesFrontendUrl(draftId)
  }

  def assetsJourney(userAnswers: UserAnswers, draftId: String): Call = {
    userAnswers.get(sections.Assets).getOrElse(Nil) match {
      case _ :: _ =>
        controllers.register.asset.routes.AddAssetsController.onPageLoad(draftId)
      case Nil =>
        controllers.register.asset.routes.AssetInterruptPageController.onPageLoad(draftId)
    }
  }

  def taxLiabilityJourney(draftId: String): String = {
    frontendAppConfig.taxLiabilityFrontendUrl(draftId)
  }

}
