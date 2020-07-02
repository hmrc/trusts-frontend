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

package navigation

import base.RegistrationSpecBase
import controllers.register.routes._
import generators.Generators
import models.NormalMode
import navigation.navigators.registration._
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages._

class NavigatorSpec extends RegistrationSpecBase
  with ScalaCheckPropertyChecks
  with Generators
  with TrustDetailsRoutes
  with MatchingRoutes
  with TrusteeRoutes
  with AgentRoutes
  with AssetRoutes
  with DeceasedSettlorRoutes
  with BeneficiaryRoutes
  with PropertyOrLandRoutes
  with LivingSettlorRoutes
{

  implicit val navigator : Navigator = injector.instanceOf[Navigator]

  "Navigator" when {

    "in Normal mode" must {

      "go to Index from a page that doesn't exist in the route map" in {
        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, NormalMode, fakeDraftId)(emptyUserAnswers) mustBe IndexController.onPageLoad()
      }

      behave like matchingRoutes

      behave like trustDetailsRoutes

      behave like trusteeRoutes

      behave like agentRoutes

      behave like assetRoutes

      behave like deceasedSettlorRoutes

      behave like beneficiaryRoutes

      behave like propertyOrLandRoutes

      behave like livingSettlorRoutes

      behave like livingBusinessSettlorRoutes

    }

  }
}
