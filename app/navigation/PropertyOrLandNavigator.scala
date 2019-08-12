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
import controllers.property_or_land.routes
import javax.inject.Singleton
import models.{NormalMode, UserAnswers}
import pages.Page
import pages.property_or_land._
import play.api.mvc.Call
import uk.gov.hmrc.auth.core.AffinityGroup

@Singleton
class PropertyOrLandNavigator extends Navigator {

  override protected def normalRoutes(draftId: String): Page => AffinityGroup => UserAnswers => Call = {
    case PropertyOrLandAddressUkYesNoPage(index) => _ => propertyOrLandAddressUkYesNoPage(draftId, index)
  }

  private def propertyOrLandAddressUkYesNoPage(draftId: String, index: Int)(answers: UserAnswers) = answers.get(PropertyOrLandAddressUkYesNoPage(index)) match {
    case Some(true)  => routes.PropertyOrLandUKAddressController.onPageLoad(NormalMode, index, draftId)
    case Some(false) => routes.PropertyOrLandInternationalAddressController.onPageLoad(NormalMode, index, draftId)
    case None        => controllers.routes.SessionExpiredController.onPageLoad()
  }

}
