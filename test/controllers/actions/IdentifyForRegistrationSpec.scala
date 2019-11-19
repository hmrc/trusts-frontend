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

package controllers.actions

import base.SpecBase
import config.FrontendAppConfig
import play.api.mvc.{Action, AnyContent, Results}
import uk.gov.hmrc.auth.core._

class IdentifyForRegistrationSpec extends SpecBase {

  val tmp = new IdentifyForRegistration("123456789")
  val action = app.injector.instanceOf[IdentifyForRegistration]
  val x = action = {
    _ => Results.Ok
  }
  val mockAuthConnector: AuthConnector = mock[AuthConnector]
  val appConfig: FrontendAppConfig = injector.instanceOf[FrontendAppConfig]

  val fakeAction: Action[AnyContent] =  { _ => Results.Ok }

  lazy override val trustsAuth = new TrustsAuth(mockAuthConnector, appConfig)


  "invoking the IdentifyForRegistrations action builder" when {

    
  }
}

