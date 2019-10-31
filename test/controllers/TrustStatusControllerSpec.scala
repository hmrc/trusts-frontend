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
import models.UserAnswers
import models.TrustStatusResponse._
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatest.BeforeAndAfterEach
import pages.WhatIsTheUTRVariationPage
import play.api.Application
import play.api.inject.bind
import play.api.mvc.{AnyContentAsEmpty, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core.AffinityGroup
import views.html.{ClosedErrorView, DoesNotMatchErrorView, IVDownView, StillProcessingErrorView}

import scala.concurrent.Future

class TrustStatusControllerSpec extends SpecBase with BeforeAndAfterEach {

  trait LocalSetup {

    def utr = "1234567890"

    def userAnswers: UserAnswers = emptyUserAnswers.set(WhatIsTheUTRVariationPage, utr).success.value

    val fakeTrustConnector: TrustConnector = mock[TrustConnector]

    def application: Application = applicationBuilder(userAnswers = Some(userAnswers)).overrides(
      bind[TrustConnector].to(fakeTrustConnector)
    ).build()

    def request: FakeRequest[AnyContentAsEmpty.type]

    def result: Future[Result] = route(application, request).value
  }

  "TrustStatus Controller" when {

    "must return OK and the correct view for GET ../status/closed" in new LocalSetup {

      override val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, routes.TrustStatusController.closed(fakeDraftId).url)

      val view: ClosedErrorView = application.injector.instanceOf[ClosedErrorView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(fakeDraftId, AffinityGroup.Individual, utr)(fakeRequest, messages).toString

      application.stop()
    }

    "must return OK and the correct view for GET ../status/processing" in new LocalSetup {

      override val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, routes.TrustStatusController.processing(fakeDraftId).url)

      val view: StillProcessingErrorView = application.injector.instanceOf[StillProcessingErrorView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(fakeDraftId, AffinityGroup.Individual, utr)(fakeRequest, messages).toString

      application.stop()
    }

    "must return OK and the correct view for GET ../status/not-found" in new LocalSetup {

      override val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, routes.TrustStatusController.notFound(fakeDraftId).url)

      val view: DoesNotMatchErrorView = application.injector.instanceOf[DoesNotMatchErrorView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(fakeDraftId, AffinityGroup.Individual)(fakeRequest, messages).toString

      application.stop()
    }

    "must return SERVICE_UNAVAILABLE and the correct view for GET ../status/down" in new LocalSetup {

      override val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, routes.TrustStatusController.down(fakeDraftId).url)

      val view: IVDownView = application.injector.instanceOf[IVDownView]

      status(result) mustEqual SERVICE_UNAVAILABLE

      contentAsString(result) mustEqual
        view(fakeDraftId, AffinityGroup.Individual)(fakeRequest, messages).toString

      application.stop()
    }

    "must redirect to the correct route for GET ../status" when {
      "a Closed status is received from the trust connector" in new LocalSetup {

        override val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, routes.TrustStatusController.status(fakeDraftId).url)

        when(fakeTrustConnector.getTrustStatus(any[String])(any(), any())).thenReturn(Future.successful(Closed))

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual "/trusts-registration/id/status/closed"

        application.stop()
      }

      "a Processing status is received from the trust connector" in new LocalSetup {

        override val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, routes.TrustStatusController.status(fakeDraftId).url)

        when(fakeTrustConnector.getTrustStatus(any[String])(any(), any())).thenReturn(Future.successful(Processing))

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual "/trusts-registration/id/status/processing"

        application.stop()
      }

      "a NotFound status is received from the trust connector" in new LocalSetup {

        override val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, routes.TrustStatusController.status(fakeDraftId).url)

        when(fakeTrustConnector.getTrustStatus(any[String])(any(), any())).thenReturn(Future.successful(UtrNotFound))

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual "/trusts-registration/id/status/not-found"

        application.stop()
      }

      "a ServiceUnavailable status is received from the trust connector" in new LocalSetup {

        override val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, routes.TrustStatusController.status(fakeDraftId).url)

        when(fakeTrustConnector.getTrustStatus(any[String])(any(), any())).thenReturn(Future.successful(ServiceUnavailable))

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual "/trusts-registration/id/status/down"

        application.stop()
      }

      "a Processed status is received from the trust connector" in new LocalSetup {

        override val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, routes.TrustStatusController.status(fakeDraftId).url)

        when(fakeTrustConnector.getTrustStatus(any[String])(any(), any())).thenReturn(Future.successful(Processed))

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual frontendAppConfig.claimATrustUrl(utr)

        application.stop()
      }
    }
  }
}
