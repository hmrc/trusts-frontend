/*
 * Copyright 2024 HM Revenue & Customs
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

package controllers.abTestingUseOnly

import base.RegistrationSpecBase
import org.mockito.ArgumentMatchers.{eq => eqTo, _}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.api.inject.bind
import uk.gov.hmrc.play.audit.http.connector.AuditConnector
import views.html.abTestingUseOnly.TestSignOutView
import org.mockito.Mockito.{never, verify}

class TestSignOutControllerSpec extends RegistrationSpecBase {

  "TestSignOutController" must {

    "render the test sign out view and send audit for /GET" in {

      val mockAuditConnector = mock[AuditConnector]

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .overrides(bind[AuditConnector].toInstance(mockAuditConnector))
        .build()

      val request = FakeRequest(GET, routes.TestSignOutController.onPageLoad().url)

      val result = route(application, request).value

      val view = application.injector.instanceOf[TestSignOutView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual view()(request, messages).toString

      verify(mockAuditConnector, never())
        .sendExplicitAudit(eqTo("trusts"), any[Map[String, String]])(any(), any())

      application.stop()
    }
  }

}
