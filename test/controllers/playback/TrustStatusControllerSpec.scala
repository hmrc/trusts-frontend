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

package controllers.playback

import base.SpecBase
import connector.{TrustClaim, TrustConnector, TrustsStoreConnector}
import models.core.UserAnswers
import models.playback.http._
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatest.BeforeAndAfterEach
import pages.playback.WhatIsTheUTRVariationPage
import play.api.Application
import play.api.inject.bind
import play.api.libs.json._
import play.api.mvc.{AnyContentAsEmpty, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core.AffinityGroup
import views.html.playback.status._

import scala.concurrent.Future
import scala.io.Source

class TrustStatusControllerSpec extends SpecBase with BeforeAndAfterEach {

  trait LocalSetup {

    def utr = "1234567890"

    def userAnswers: UserAnswers = emptyUserAnswers.set(WhatIsTheUTRVariationPage, utr).success.value

    val fakeTrustConnector: TrustConnector = mock[TrustConnector]
    val fakeTrustStoreConnector: TrustsStoreConnector = mock[TrustsStoreConnector]

    def application: Application = applicationBuilder(userAnswers = Some(userAnswers)).overrides(
      bind[TrustConnector].to(fakeTrustConnector),
      bind[TrustsStoreConnector].to(fakeTrustStoreConnector)
    ).build()

    def request: FakeRequest[AnyContentAsEmpty.type]

    def result: Future[Result] = route(application, request).value
  }

  "TrustStatus Controller" when {

    "must return OK and the correct view for GET ../status/closed" in new LocalSetup {

      override val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, routes.TrustStatusController.closed().url)

      val view: ClosedErrorView = application.injector.instanceOf[ClosedErrorView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(AffinityGroup.Individual, utr)(fakeRequest, messages).toString

      application.stop()
    }

    "must return OK and the correct view for GET ../status/processing" in new LocalSetup {

      override val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, routes.TrustStatusController.processing().url)

      val view: StillProcessingErrorView = application.injector.instanceOf[StillProcessingErrorView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(AffinityGroup.Individual, utr)(fakeRequest, messages).toString

      application.stop()
    }

    "must return OK and the correct view for GET ../status/not-found" in new LocalSetup {

      override val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, routes.TrustStatusController.notFound().url)

      val view: DoesNotMatchErrorView = application.injector.instanceOf[DoesNotMatchErrorView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(AffinityGroup.Individual)(fakeRequest, messages).toString

      application.stop()
    }

    "must return OK and the correct view for GET ../status/locked" in new LocalSetup {

      override val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, routes.TrustStatusController.locked().url)

      val view: TrustLockedView = application.injector.instanceOf[TrustLockedView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(utr)(fakeRequest, messages).toString

      application.stop()
    }

    "must return OK and the correct view for GET ../status/already-claimed" in new LocalSetup {

      override val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, routes.TrustStatusController.alreadyClaimed().url)

      val view: TrustAlreadyClaimedView = application.injector.instanceOf[TrustAlreadyClaimedView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(utr)(fakeRequest, messages).toString

      application.stop()
    }

    "must return OK and the correct view for GET ../status/sorry-there-has-been-a-problem" in new LocalSetup {

      override val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, routes.TrustStatusController.sorryThereHasBeenAProblem().url)

      val view: PlaybackProblemContactHMRCView = application.injector.instanceOf[PlaybackProblemContactHMRCView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(utr)(fakeRequest, messages).toString

      application.stop()
    }

    "must return SERVICE_UNAVAILABLE and the correct view for GET ../status/down" in new LocalSetup {

      override val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, routes.TrustStatusController.down().url)

      val view: IVDownView = application.injector.instanceOf[IVDownView]

      status(result) mustEqual SERVICE_UNAVAILABLE

      contentAsString(result) mustEqual
        view(AffinityGroup.Individual)(fakeRequest, messages).toString

      application.stop()
    }

    "must redirect to the correct route for GET ../status" when {

      "a Closed status is received from the trust connector" in new LocalSetup {

        override val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, routes.TrustStatusController.status().url)

        when(fakeTrustStoreConnector.get(any[String], any[String])(any(), any()))
          .thenReturn(Future.successful(Some(TrustClaim("1234567890", trustLocked = false, managedByAgent = false))))

        when(fakeTrustConnector.playback(any[String])(any(), any())).thenReturn(Future.successful(Closed))

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual "/maintain-a-trust/status/closed"

        application.stop()
      }

      "a Processing status is received from the trust connector" in new LocalSetup {

        override val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, routes.TrustStatusController.status().url)

        when(fakeTrustStoreConnector.get(any[String], any[String])(any(), any()))
          .thenReturn(Future.successful(Some(TrustClaim("1234567890", trustLocked = false, managedByAgent = false))))

        when(fakeTrustConnector.playback(any[String])(any(), any())).thenReturn(Future.successful(Processing))

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual "/maintain-a-trust/status/processing"

        application.stop()
      }

      "a NotFound status is received from the trust connector" in new LocalSetup {

        override val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, routes.TrustStatusController.status().url)

        when(fakeTrustStoreConnector.get(any[String], any[String])(any(), any()))
          .thenReturn(Future.successful(Some(TrustClaim("1234567890", trustLocked = false, managedByAgent = false))))

        when(fakeTrustConnector.playback(any[String])(any(), any())).thenReturn(Future.successful(UtrNotFound))

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual "/maintain-a-trust/status/not-found"

        application.stop()
      }

      "A locked trust claim is returned from the trusts store connector" in new LocalSetup {

        override val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, routes.TrustStatusController.status().url)

        when(fakeTrustStoreConnector.get(any[String], any[String])(any(), any()))
          .thenReturn(Future.successful(Some(TrustClaim("1234567890", trustLocked = true, managedByAgent = false))))

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual "/maintain-a-trust/status/locked"

        application.stop()
      }

      "a ServiceUnavailable status is received from the trust connector" in new LocalSetup {

        override val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, routes.TrustStatusController.status().url)

        when(fakeTrustStoreConnector.get(any[String], any[String])(any(), any()))
          .thenReturn(Future.successful(Some(TrustClaim("1234567890", trustLocked = false, managedByAgent = false))))

        when(fakeTrustConnector.playback(any[String])(any(), any())).thenReturn(Future.successful(TrustServiceUnavailable))

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual "/maintain-a-trust/status/down"

        application.stop()
      }

      "a Processed status is received from the trust connector" in new LocalSetup {

        override val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, routes.TrustStatusController.status().url)

        val payload : String =
          Source.fromFile(getClass.getResource("/display-trust.json").getPath).mkString

        val json : JsValue = Json.parse(payload)

        val getTrust : GetTrustDesResponse = json.as[GetTrustDesResponse]

        when(fakeTrustStoreConnector.get(any[String], any[String])(any(), any()))
          .thenReturn(Future.successful(Some(TrustClaim("1234567890", trustLocked = false, managedByAgent = false))))

        when(fakeTrustConnector.playback(any[String])(any(), any()))
          .thenReturn(Future.successful(Processed(getTrust.getTrust.value, "9873459837459837")))

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual frontendAppConfig.claimATrustUrl(utr)

        application.stop()
      }
    }
  }
}
