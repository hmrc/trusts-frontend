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

package controllers

import base.SpecBase
import connector.TrustConnector
import controllers.actions.{DataRequiredAction, DataRequiredActionImpl}
import models.{Closed, TrustStatusResponse, UserAnswers}
import pages.WhatIsTheUTRVariationPage
import play.api.Application
import play.api.mvc.{AnyContentAsEmpty, Result}
import play.api.test.FakeRequest
import views.html.{CannotAccessErrorView, DoesNotMatchErrorView, StillProcessingErrorView}
import play.api.test.Helpers._
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatest.mockito.MockitoSugar.mock
import play.api.inject.bind

import scala.concurrent.Future

class TrustStatusControllerSpec extends SpecBase {


  trait LocalSetup {

    def utr = "1234567das"

    def userAnswers: UserAnswers = emptyUserAnswers.set(WhatIsTheUTRVariationPage, utr ).success.value

    val fakeTrustConnector: TrustConnector = mock[TrustConnector]

    def application: Application = applicationBuilder(userAnswers = Some(userAnswers)).overrides(
      bind[TrustConnector].to(fakeTrustConnector)
    ).build()

    def request: FakeRequest[AnyContentAsEmpty.type]

    def result: Future[Result] = route(application, request).value
  }

  "TrustStatus Controller" when {

    "navigating to GET ../closed" in new LocalSetup {

      override val request = FakeRequest(GET, routes.TrustStatusController.closed(fakeDraftId).url)

      val view: CannotAccessErrorView = application.injector.instanceOf[CannotAccessErrorView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(utr)(fakeRequest, messages).toString

      application.stop()
    }

    "navigating to GET ../processing" in new LocalSetup {

      override val request = FakeRequest(GET, routes.TrustStatusController.processing(fakeDraftId).url)

      val view: StillProcessingErrorView = application.injector.instanceOf[StillProcessingErrorView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(utr)(fakeRequest, messages).toString

      application.stop()
    }

    "navigating to GET ../notFound" in new LocalSetup {

      override val request = FakeRequest(GET, routes.TrustStatusController.notFound(fakeDraftId).url)

      val view: DoesNotMatchErrorView = application.injector.instanceOf[DoesNotMatchErrorView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(utr)(fakeRequest, messages).toString

      application.stop()
    }

    "navigating to GET ../error" in new LocalSetup {

      override val application: Application = application

      override val request = FakeRequest(GET, routes.TrustStatusController.onError(fakeDraftId).url)

      when(fakeTrustConnector.getTrustStatus(any[String])(any(), any())).thenReturn(Future.successful(Closed))

      status(result) mustEqual SEE_OTHER

      redirectLocation(result) mustEqual true

      application.stop()
    }
  }
}
